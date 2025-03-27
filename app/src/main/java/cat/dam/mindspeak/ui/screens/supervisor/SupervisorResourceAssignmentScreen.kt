package cat.dam.mindspeak.ui.screens.supervisor

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import cat.dam.mindspeak.R
import cat.dam.mindspeak.firebase.FirebaseManager
import cat.dam.mindspeak.model.UserRelation
import cat.dam.mindspeak.utils.SupabaseConfig
import cat.dam.mindspeak.utils.SupabaseStorageUtil
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SupervisorResourceAssignmentScreen() {
    val firebaseManager = FirebaseManager()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    val audioRecordPermissionState = rememberPermissionState(android.Manifest.permission.RECORD_AUDIO)
    val snackbarHostState = remember { SnackbarHostState() }

    // États
    var users by remember { mutableStateOf<List<UserRelation>>(emptyList()) }
    var selectedUser by remember { mutableStateOf<UserRelation?>(null) }
    var resourceType by remember { mutableStateOf("") }
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showMediaDialog by remember { mutableStateOf(false) }
    var currentMediaType by remember { mutableStateOf("") }

    // URI des fichiers temporaires
    val imageUri = remember {
        createImageUri(context)
    }

    val videoUri = remember {
        createVideoUri(context)
    }

    val audioUri = remember {
        FileProvider.getUriForFile(
            context,
//            "${context.packageName}.provider",
            "cat.dam.mindspeak.fileprovider",
            createAudioFile(context)
        )
    }

    // Lanceurs d'activité
    val imagePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedFileUri = uri
        showMediaDialog = false
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            selectedFileUri = imageUri
            showMediaDialog = false
        }
    }

    val videoPickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedFileUri = uri
        showMediaDialog = false
    }

    val videoRecorderLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CaptureVideo()
    ) { success ->
        if (success) {
            selectedFileUri = videoUri
            showMediaDialog = false
        }
    }

    val audioPickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedFileUri = uri
        showMediaDialog = false
    }

    val audioRecorderLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedFileUri = uri
                showMediaDialog = false
            }
        }
    }

    // Lanceurs de permissions
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(imageUri)
        } else {
            errorMessage = "Camera permission denied"
        }
    }

    val audioRecordPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val intent = Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION)
            audioRecorderLauncher.launch(intent)
        } else {
            errorMessage = "Audio recording permission denied"
        }
    }

    // Chargement initial des utilisateurs
    LaunchedEffect(Unit) {
        try {
            users = firebaseManager.getUsersBySupervisor(firebaseManager.auth.currentUser?.uid ?: "")
        } catch (e: Exception) {
            errorMessage = "Error loading users: ${e.localizedMessage}"
        }
    }

    // Gestion des erreurs
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            errorMessage = null
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            // Sélection d'utilisateur
            UserSelectionDropdown(
                users = users,
                selectedUser = selectedUser,
                onUserSelected = { selectedUser = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Sélection du type de ressource
            ResourceTypeSelector(
                onImageSelected = {
                    resourceType = "image"
                    currentMediaType = "image"
                    showMediaDialog = true
                },
                onVideoSelected = {
                    resourceType = "video"
                    currentMediaType = "video"
                    showMediaDialog = true
                },
                onAudioSelected = {
                    resourceType = "audio"
                    currentMediaType = "audio"
                    showMediaDialog = true
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Affichage du fichier sélectionné
            selectedFileUri?.let { uri ->
                Text(
                    text = "Selected file: ${uri.lastPathSegment}",
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Bouton d'upload
            UploadButton(
                isLoading = isLoading,
                enabled = selectedUser != null && resourceType.isNotEmpty() && selectedFileUri != null,
                onUpload = {
                    coroutineScope.launch {
                        try {
                            isLoading = true
                            val uploadedUrl = uploadResourceToSupabase(
                                context = context,
                                resourceUri = selectedFileUri!!,
                                resourceType = resourceType
                            )

                            saveResourceToFirebase(
                                userId = selectedUser!!.userId,
                                supervisorId = firebaseManager.auth.currentUser?.uid ?: "",
                                resourceType = resourceType,
                                resourceUri = uploadedUrl,
                                originalFileName = selectedFileUri!!.lastPathSegment
                            )

                            errorMessage = "Upload successful!"
                        } catch (e: Exception) {
                            errorMessage = "Upload failed: ${e.localizedMessage}"
                        } finally {
                            isLoading = false
                            selectedFileUri = null
                            resourceType = ""
                        }
                    }
                }
            )
        }

        // Media Selection Dialog
        if (showMediaDialog) {
            MediaSelectionDialog(
                title = "Select $currentMediaType Source",
                options = buildList {
                    add("Gallery" to {
                        when (currentMediaType) {
                            "image" -> imagePickerLauncher.launch("image/*")
                            "video" -> videoPickerLauncher.launch("video/*")
                            "audio" -> audioPickerLauncher.launch("audio/*")
                        }
                    })

                    when (currentMediaType) {
                        "image" -> add("Camera" to {
                            if (cameraPermissionState.status.isGranted) {
                                cameraLauncher.launch(imageUri)
                            } else {
                                cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                            }
                        })
                        "video" -> add("Record" to {
                            videoRecorderLauncher.launch(videoUri)
                        })
                        "audio" -> add("Record" to {
                            if (audioRecordPermissionState.status.isGranted) {
                                val intent = Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION)
                                audioRecorderLauncher.launch(intent)
                            } else {
                                audioRecordPermissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
                            }
                        })
                    }
                },
                onDismiss = { showMediaDialog = false }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserSelectionDropdown(
    users: List<UserRelation>,
    selectedUser: UserRelation?,
    onUserSelected: (UserRelation) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            value = selectedUser?.let { "${it.nom} ${it.cognom}" } ?: "Select a user",
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            users.forEach { user ->
                DropdownMenuItem(
                    text = { Text("${user.nom} ${user.cognom}") },
                    onClick = {
                        onUserSelected(user)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun ResourceTypeSelector(
    onImageSelected: () -> Unit,
    onVideoSelected: () -> Unit,
    onAudioSelected: () -> Unit
) {
    Column {
        Text("Select Resource Type", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            ResourceTypeButton(
                icon = R.drawable.cam,
                label = "Image",
                onClick = onImageSelected
            )
            ResourceTypeButton(
                icon = R.drawable.video,
                label = "Video",
                onClick = onVideoSelected
            )
            ResourceTypeButton(
                icon = R.drawable.audio,
                label = "Audio",
                onClick = onAudioSelected
            )
        }
    }
}

@Composable
private fun ResourceTypeButton(
    icon: Int,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Image(
            painter = painterResource(icon),
            contentDescription = label,
            modifier = Modifier.size(48.dp)
        )
        Text(label, fontSize = 12.sp)
    }
}

@Composable
private fun MediaSelectionDialog(
    title: String,
    options: List<Pair<String, () -> Unit>>,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                options.forEach { (optionText, action) ->
                    Button(
                        onClick = {
                            action()
                            onDismiss()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text(optionText)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun UploadButton(
    isLoading: Boolean,
    enabled: Boolean,
    onUpload: () -> Unit
) {
    Button(
        onClick = onUpload,
        enabled = enabled && !isLoading,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            Text("Upload Resource")
        }
    }
}

private fun createAudioFile(context: Context): File {
    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    return File.createTempFile(
        "AUDIO_${timestamp}_",
        ".mp3",
        context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
    )
}

private fun createImageUri(context: Context): Uri {
    val file = File.createTempFile(
        "JPEG_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}_",
        ".jpg",
        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    )
    return FileProvider.getUriForFile(
        context,
        "cat.dam.mindspeak.fileprovider",
        file
    )
}

private fun createVideoUri(context: Context): Uri {
    val file = File.createTempFile(
        "MP4_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}_",
        ".mp4",
        context.getExternalFilesDir(Environment.DIRECTORY_MOVIES)
    )
    return FileProvider.getUriForFile(
        context,
        "cat.dam.mindspeak.fileprovider",
        file
    )
}

private suspend fun uploadResourceToSupabase(
    context: Context,
    resourceUri: Uri,
    resourceType: String
): String = withContext(Dispatchers.IO) {
    val supabase = SupabaseStorageUtil.supabaseClient
    val fileExtension = when (resourceType) {
        "image" -> "jpg"
        "video" -> "mp4"
        "audio" -> "mp3"
        else -> "dat"
    }

    val fileName = "${UUID.randomUUID()}.$fileExtension"
    val inputStream = context.contentResolver.openInputStream(resourceUri)
    val bytes = inputStream?.readBytes() ?: throw IllegalStateException("Empty file")

    supabase.storage.from(SupabaseConfig.BUCKET_NAME)
        .upload(fileName, bytes, upsert = true)

    supabase.storage.from(SupabaseConfig.BUCKET_NAME)
        .publicUrl(fileName)
}

private suspend fun saveResourceToFirebase(
    userId: String,
    supervisorId: String,
    resourceType: String,
    resourceUri: String,
    originalFileName: String?
) {
    FirebaseManager().db.collection("Recurs").add(
        mapOf(
            "userId" to userId,
            "supervisorId" to supervisorId,
            "type" to resourceType,
            "uri" to resourceUri,
            "fileName" to originalFileName,
            "timestamp" to com.google.firebase.Timestamp.now()
        )
    ).await()
}

/*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupervisorResourceAssignmentScreen() {
    val firebaseManager = FirebaseManager()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // State variables
    var users by remember { mutableStateOf<List<UserRelation>>(emptyList()) }
    var selectedUser by remember { mutableStateOf<UserRelation?>(null) }
    var resourceType by remember { mutableStateOf<String?>(null) }
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // File pickers and media capture launchers
    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri -> selectedFileUri = uri }
    val videoPickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri -> selectedFileUri = uri }
    val audioPickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri -> selectedFileUri = uri }

    // Load users at startup
    LaunchedEffect(Unit) {
        try {
            users = firebaseManager.getUsersBySupervisor(firebaseManager.auth.currentUser?.uid ?: "")
        } catch (e: Exception) {
            errorMessage = "Error carregant usuaris: ${e.localizedMessage}"
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            // User selection dropdown
            Text("Seleccionar Usuari", style = MaterialTheme.typography.titleMedium)
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                TextField(
                    value = selectedUser?.let { "${it.nom} ${it.cognom}" } ?: "Selecciona un usuari",
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    users.forEach { user ->
                        DropdownMenuItem(text = { Text("${user.nom} ${user.cognom}") }, onClick = { selectedUser = user; expanded = false })
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Resource selection
            Text("Tipo de Recurs", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                ResourceTypeButton("Imatge", R.drawable.cam) {
                    resourceType = "image"
                    imagePickerLauncher.launch("image/*")
                }
                ResourceTypeButton("Vídeo", R.drawable.video) {
                    resourceType = "video"
                    videoPickerLauncher.launch("video/*")
                }
                ResourceTypeButton("Audio", R.drawable.audio) {
                    resourceType = "audio"
                    audioPickerLauncher.launch("audio/*")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Upload button
            if (selectedUser != null && resourceType != null && selectedFileUri != null) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("Arxiu seleccionat: ${selectedFileUri?.lastPathSegment}", style = MaterialTheme.typography.bodyMedium)
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                try {
                                    isLoading = true
                                    val resourceUri = uploadResourceToSupabase(context, selectedFileUri!!, resourceType!!)
                                    saveResourceToFirebase(selectedUser!!.userId, firebaseManager.auth.currentUser?.uid ?: "", resourceType!!, resourceUri, selectedFileUri?.lastPathSegment)
                                    errorMessage = "Recurs pujat correctament"
                                    selectedFileUri = null
                                    resourceType = null
                                } catch (e: Exception) {
                                    errorMessage = "Error pujant el recurs: ${e.localizedMessage}"
                                } finally {
                                    isLoading = false
                                }
                            }
                        },
                        enabled = !isLoading
                    ) {
                        if (isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp)) else Text("Pujar Recurs")
                    }
                }
            }
        }
    }
}

@Composable
fun ResourceTypeButton(text: String, iconResId: Int, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(8.dp).clickable(onClick = onClick)) {
        Image(painter = painterResource(id = iconResId), contentDescription = text, modifier = Modifier.size(48.dp))
        Text(text = text, fontSize = 12.sp)
    }
}

// Fonction de téléchargement des ressources sur Supabase
suspend fun uploadResourceToSupabase(
    context: Context,
    resourceUri: Uri,
    resourceType: String
): String {
    return withContext(Dispatchers.IO) {
        try {
            // Générer un nom de fichier unique
            val fileExtension = when(resourceType) {
                "video" -> "mp4"
                "audio" -> "mp3"
                "image" -> "jpg"
                else -> "file"
            }
            val filename = "${UUID.randomUUID()}.$fileExtension"

            // Ouvrir et copier le fichier
            val inputStream = context.contentResolver.openInputStream(resourceUri)
            val file = File.createTempFile("upload", ".$fileExtension", context.cacheDir)
            val outputStream = FileOutputStream(file)

            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }

            // Obtenir le client Supabase
            val supabaseClient = SupabaseStorageUtil.supabaseClient
            val byteArray = file.readBytes()

            // Uploader le fichier
            supabaseClient.storage.from(SupabaseConfig.BUCKET_NAME).upload(
                path = filename,
                data = byteArray,
                upsert = true
            )

            // Obtenir l'URL publique
            var publicUrl = supabaseClient.storage.from(SupabaseConfig.BUCKET_NAME).publicUrl(filename)
            if (publicUrl.isNotEmpty() && !publicUrl.startsWith("http")) {
                publicUrl = "https://$publicUrl"
            }

            // Supprimer le fichier temporaire
            file.delete()
            publicUrl
        } catch (e: Exception) {
            Log.e("SupabaseStorageUtil", "Erreur lors de l'upload de la ressource", e)
            throw e
        }
    }
}

// Fonction pour enregistrer les métadonnées sur Firebase
suspend fun saveResourceToFirebase(
    userId: String,
    supervisorId: String,
    resourceType: String,
    resourceUri: String,
    originalFileName: String? = null
) {
    val firebaseManager = FirebaseManager()

    // Préparer les données de la ressource avec plus de détails
    val resourceData = mapOf(
        "userId" to userId,
        "supervisorId" to supervisorId,
        "type" to resourceType,
        "uri" to resourceUri,
        "date" to com.google.firebase.Timestamp.now(),
        "createdAt" to com.google.firebase.Timestamp.now(),
        "metadata" to mapOf(
            "fileExtension" to resourceType,
            "originalFileName" to originalFileName,
            "uploadSource" to "SupervisorResourceAssignmentScreen"
        )
    )

    // Enregistrer dans la collection 'Recurs'
    firebaseManager.db.collection("Recurs")
        .add(resourceData)
        .await()
}
 */

/*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupervisorResourceAssignmentScreen() {
    val firebaseManager = FirebaseManager()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // State variables
    var users by remember { mutableStateOf<List<UserRelation>>(emptyList()) }
    var selectedUser by remember { mutableStateOf<UserRelation?>(null) }
    var resourceType by remember { mutableStateOf<String?>(null) }
    var resourceUri by remember { mutableStateOf<String?>(null) }
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // File picker launchers for different resource types
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedFileUri = uri
    }

    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedFileUri = uri
    }

    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedFileUri = uri
    }

    // Load users when the screen is first displayed
    LaunchedEffect(Unit) {
        try {
            users = firebaseManager.getUsersBySupervisor(firebaseManager.auth.currentUser?.uid ?: "")
        } catch (e: Exception) {
            errorMessage = "Error carregant usuaris: ${e.localizedMessage}"
        }
    }

    // Show snackbar when error message changes
    if (errorMessage != null) {
        LaunchedEffect(errorMessage) {
            snackbarHostState.showSnackbar(
                message = errorMessage!!,
                actionLabel = "Tanca"
            )
            errorMessage = null
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Seleccionar Usuari",
                style = MaterialTheme.typography.titleMedium,
                color = LocalCustomColors.current.text1
            )

            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = selectedUser?.let { "${it.nom} ${it.cognom}" } ?: "Selecciona un usuari",
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    users.forEach { user ->
                        DropdownMenuItem(
                            text = { Text("${user.nom} ${user.cognom}") },
                            onClick = {
                                selectedUser = user
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Tipo de Recurs",
                style = MaterialTheme.typography.titleMedium,
                color = LocalCustomColors.current.text1
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                ResourceTypeButton("Imatge", R.drawable.cam) {
                    resourceType = "image"
                    imagePickerLauncher.launch("image/*")
                }
                ResourceTypeButton("Vídeo", R.drawable.video) {
                    resourceType = "video"
                    videoPickerLauncher.launch("video/*")
                }
                ResourceTypeButton("Audio", R.drawable.audio) {
                    resourceType = "audio"
                    audioPickerLauncher.launch("audio/*")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedUser != null && resourceType != null && selectedFileUri != null) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Arxiu seleccionat: ${selectedFileUri?.lastPathSegment}",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                try {
                                    isLoading = true
                                    // Upload resource to Supabase
                                    resourceUri = when(resourceType) {
                                        "image" -> SupabaseStorageUtil.uploadImage(context, selectedFileUri!!)
                                        "video" -> uploadResourceToSupabase(context, selectedFileUri!!, "video")
                                        "audio" -> uploadResourceToSupabase(context, selectedFileUri!!, "audio")
                                        else -> throw Exception("Tipus de recurs no vàlid")
                                    }

                                    // Save resource metadata to Firebase
                                    saveResourceToFirebase(
                                        userId = selectedUser?.userId ?: "",
                                        supervisorId = firebaseManager.auth.currentUser?.uid ?: "",
                                        resourceType = resourceType!!,
                                        resourceUri = resourceUri!!,
                                        originalFileName = selectedFileUri?.lastPathSegment
                                    )

                                    // Reset states
                                    errorMessage = "Recurs pujat correctament"
                                    selectedFileUri = null
                                    resourceType = null
                                } catch (e: Exception) {
                                    errorMessage = "Error pujant el recurs: ${e.localizedMessage}"
                                } finally {
                                    isLoading = false
                                }
                            }
                        },
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Pujar Recurs")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ResourceTypeButton(
    text: String,
    iconResId: Int,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(8.dp)
            .clickable(onClick = onClick)
    ) {
        Image(
            painter = painterResource(id = iconResId),
            contentDescription = text,
            modifier = Modifier.size(48.dp)
        )
        Text(
            text = text,
            fontSize = 12.sp,
            color = LocalCustomColors.current.text1
        )
    }
}

suspend fun uploadResourceToSupabase(
    context: Context,
    resourceUri: Uri,
    resourceType: String
): String {
    return withContext(Dispatchers.IO) {
        try {
            // Générer un nom de fichier unique
            val fileExtension = when(resourceType) {
                "video" -> "mp4"
                "audio" -> "mp3"
                "image" -> "jpg"
                else -> "file"
            }
            val filename = "${UUID.randomUUID()}.$fileExtension"

            // Ouvrir et copier le fichier
            val inputStream = context.contentResolver.openInputStream(resourceUri)
            val file = File.createTempFile("upload", ".$fileExtension", context.cacheDir)
            val outputStream = FileOutputStream(file)

            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }

            // Obtenir le client Supabase
            val storageClient = SupabaseStorageUtil.supabaseClient.storage
            val byteArray = file.readBytes()

            // Uploader le fichier
            storageClient.from(SupabaseConfig.BUCKET_NAME).upload(
                path = filename,
                data = byteArray,
                upsert = true
            )

            // Obtenir l'URL publique
            var publicUrl = storageClient.from(SupabaseConfig.BUCKET_NAME).publicUrl(filename)
            if (!publicUrl.startsWith("http")) {
                publicUrl = "https://$publicUrl"
            }

            // Supprimer le fichier temporaire
            file.delete()
            publicUrl
        } catch (e: Exception) {
            Log.e("SupabaseStorageUtil", "Erreur lors de l'upload de la ressource", e)
            throw e
        }
    }
}

suspend fun saveResourceToFirebase(
    userId: String,
    supervisorId: String,
    resourceType: String,
    resourceUri: String,
    originalFileName: String? = null
) {
    val firebaseManager = FirebaseManager()

    // Préparer les données de la ressource avec plus de détails
    val resourceData = mapOf(
        "userId" to userId,
        "supervisorId" to supervisorId,
        "type" to resourceType,
        "uri" to resourceUri,
        "date" to com.google.firebase.Timestamp.now(),
        "createdAt" to com.google.firebase.Timestamp.now(),
        "metadata" to mapOf(
            "fileExtension" to resourceType,
            "originalFileName" to originalFileName,
            "uploadSource" to "SupervisorResourceAssignmentScreen"
        )
    )

    // Enregistrer dans la collection 'Recurs'
    firebaseManager.db.collection("Recurs")
        .add(resourceData)
        .await()
}

 */