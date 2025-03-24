package cat.dam.mindspeak.ui.screens.user

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import cat.dam.mindspeak.R
import cat.dam.mindspeak.model.EmotionItem
import cat.dam.mindspeak.model.EmotionRecord
import cat.dam.mindspeak.model.EmotionViewModel
import cat.dam.mindspeak.ui.theme.LocalCustomColors
import cat.dam.mindspeak.utils.SupabaseStorageUtil
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.runtime.LaunchedEffect
import cat.dam.mindspeak.firebase.EmotionRepository

@Composable
fun EmotionRatingScreen(
    navController: NavHostController,
    backStackEntry: NavBackStackEntry,
    viewModel: EmotionViewModel = viewModel()
) {
    val scrollState = rememberScrollState()
    val emotionType = backStackEntry.arguments?.getString("emotionType") ?: "UNKNOWN"
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // État pour stocker les données de l'émotion
    var emotionItem by remember { mutableStateOf<EmotionItem?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Charger les détails de l'émotion sélectionnée
    LaunchedEffect(emotionType) {
        try {
            val repository = EmotionRepository()
            emotionItem = repository.getEmotionByType(emotionType)
            isLoading = false
        } catch (e: Exception) {
            Log.e("EmotionRatingScreen", "Error loading emotion details", e)
            isLoading = false
        }
    }

    var rating by remember { mutableStateOf(0) }
    var comentariEmocional by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }

    // Créer un fichier temporaire pour la photo
    val tempImageFile = remember {
        File.createTempFile(
            "JPEG_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}_",
            ".jpg",
            context.cacheDir
        ).apply {
            deleteOnExit()
        }
    }

    // URI pour l'image capturée par la caméra
    val tempImageUri = remember {
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            tempImageFile
        )
    }

    // Define takePhotoLauncher first
    val takePhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            imageUri = tempImageUri
        }
    }

    // Define pickImageLauncher first
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        imageUri = uri
    }

    // Now define the permission launchers that use the above launchers
    val requestCameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            takePhotoLauncher.launch(tempImageUri)
        } else {
            Toast.makeText(
                context,
                "Es necessita permís de càmera per fer fotos",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    val requestStoragePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            pickImageLauncher.launch("image/*")
        } else {
            Toast.makeText(
                context,
                "Es necessita permís d'emmagatzematge per accedir a les imatges",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // Obtenir l'ID d'utilisateur actuel
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Carte d'émotion avec image depuis Firebase
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = emotionItem?.color ?: getEmotionColor(emotionType)
            )
        ) {
            if (isLoading) {
                // Afficher un indicateur de chargement
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = emotionType.uppercase(),
                        fontSize = 24.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(16.dp))

                    // Utiliser AsyncImage pour charger l'image depuis l'URL
                    if (emotionItem?.imageUrl?.isNotEmpty() == true) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(emotionItem?.imageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = emotionType,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(80.dp)
                        )
                    } else {
                        // Fallback à une image locale si nécessaire
                        Image(
                            painter = painterResource(id = getEmotionImage(emotionType)),
                            contentDescription = null,
                            modifier = Modifier.size(80.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Zone de texte pour les commentaires émotionnels
        OutlinedTextField(
            value = comentariEmocional,
            onValueChange = { comentariEmocional = it },
            label = { Text("Com et sents?") },
            placeholder = { Text("Expressa el teu estat emocional...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Black,
                focusedContainerColor = Color.Black
            ),
            minLines = 3
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Section pour l'image
        Card(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(160.dp),
            colors = CardDefaults.cardColors(containerColor = Color.LightGray.copy(alpha = 0.3f))
        ) {
            if (imageUri != null) {
                // Montrer l'image sélectionnée
                Image(
                    painter = rememberAsyncImagePainter(imageUri),
                    contentDescription = "Imatge seleccionada",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Montrer les options pour ajouter une image
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Afegir una imatge",
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = {
                                when (PackageManager.PERMISSION_GRANTED) {
                                    ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.CAMERA
                                    ) -> {
                                        takePhotoLauncher.launch(tempImageUri)
                                    }

                                    else -> {
                                        requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = LocalCustomColors.current.secondary)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddAPhoto,
                                contentDescription = "Fer una foto"
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Càmera")
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Botón para la galería
                        Button(
                            onClick = {
                                val permission =
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        Manifest.permission.READ_MEDIA_IMAGES
                                    } else {
                                        Manifest.permission.READ_EXTERNAL_STORAGE
                                    }

                                when (PackageManager.PERMISSION_GRANTED) {
                                    ContextCompat.checkSelfPermission(context, permission) -> {
                                        pickImageLauncher.launch("image/*")
                                    }

                                    else -> {
                                        requestStoragePermissionLauncher.launch(permission)
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = LocalCustomColors.current.secondary)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = "Seleccionar una imatge"
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Galeria")
                        }
                    }
                }
            }
        }

        // Button pour supprimer l'image si une est sélectionnée
        if (imageUri != null) {
            Button(
                onClick = { imageUri = null },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.7f)),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text("Eliminar imatge", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Étoiles pour la notation
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            repeat(5) { index ->
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = if (index < rating) Color.Yellow else Color.Gray,
                    modifier = Modifier
                        .size(40.dp)
                        .clickable { rating = index + 1 }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Bouton Sauvegarder
        Button(
            onClick = {
                if (imageUri != null) {
                    isUploading = true
                    scope.launch {
                        try {
                            val imageUrl = if (imageUri != null) {
                                val url = SupabaseStorageUtil.uploadImage(context, imageUri!!)
                                Log.d("UploadDebug", "Imagen subida correctamente a: $url")
                                url
                            } else null
                            // En EmotionRatingScreen donde guardas el registro
                            val emotionRecord = EmotionRecord(
                                emotionType = emotionType,
                                rating = rating,
                                date = Date(),
                                userId = currentUserId,
                                comentari = comentariEmocional,
                                fotoUri = imageUrl ?: "" // Usar string vacío en lugar de null
                            )

                            Log.d(
                                "EmotionRatingScreen",
                                "Guardando registro con URL de imagen: $imageUrl"
                            )


                            // Sauvegarder dans Firestore
                            viewModel.addEmotionRecord(emotionRecord)
                            isUploading = false
                            navController.popBackStack()
                        } catch (e: Exception) {
                            Log.e("EmotionRatingScreen", "Error subiendo imagen: ${e.message}", e)
                            Toast.makeText(
                                context,
                                "Error al subir la imagen: ${e.message?.take(100)}",
                                Toast.LENGTH_LONG
                            ).show()
                            isUploading = false
                        } catch (e: Exception) {
                            Log.e("UploadError", "Error completo: ${e.stackTraceToString()}")
                        }
                    }
                } else {
                    // Sans image
                    val emotionRecord = EmotionRecord(
                        emotionType = emotionType,
                        rating = rating,
                        date = Date(),
                        userId = currentUserId,
                        comentari = comentariEmocional,
                        fotoUri = null
                    )
                    viewModel.addEmotionRecord(emotionRecord)
                    navController.popBackStack()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = LocalCustomColors.current.secondary),
            modifier = Modifier.fillMaxWidth(0.8f),
            enabled = !isUploading
        ) {
            if (isUploading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Pujant...", color = LocalCustomColors.current.text1)
            } else {
                Text("Guardar", color = LocalCustomColors.current.text1, fontSize = 16.sp)
            }
        }
    }
}

// Fonction auxiliaire pour obtenir une couleur d'émotion par défaut si nécessaire
fun getEmotionColor(emotionType: String): Color {
    return when (emotionType.uppercase()) {
        "FELIZ" -> Color(0xFF4CAF50)
        "TRISTE" -> Color(0xFF2196F3)
        "ENFADADO" -> Color(0xFFF44336)
        "MIEDO" -> Color(0xFF9C27B0)
        "ANSIOSO" -> Color(0xFFFF9800)
        else -> Color(0xFF607D8B)
    }
}

// Fonction auxiliaire pour obtenir une image locale par défaut si nécessaire
fun getEmotionImage(emotionType: String): Int {
    // Remplacez ces valeurs par vos ressources réelles
    return R.drawable.ic_launcher_foreground
}