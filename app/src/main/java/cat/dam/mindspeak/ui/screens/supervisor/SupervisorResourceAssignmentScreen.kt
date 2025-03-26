package cat.dam.mindspeak.ui.screens.supervisor

import android.content.Context
import android.net.Uri
import android.util.Log
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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import cat.dam.mindspeak.R
import cat.dam.mindspeak.firebase.FirebaseManager
import cat.dam.mindspeak.model.UserRelation
import cat.dam.mindspeak.ui.theme.LocalCustomColors
import cat.dam.mindspeak.utils.SupabaseConfig
import cat.dam.mindspeak.utils.SupabaseStorageUtil
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

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