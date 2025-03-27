package cat.dam.mindspeak.ui.screens.user


import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavHostController
import cat.dam.mindspeak.firebase.FirebaseManager
import cat.dam.mindspeak.ui.theme.LocalCustomColors
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResourceViewScreen(
    navController: NavHostController,
    resourceId: String
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val firebaseManager = FirebaseManager()

    var resource by remember { mutableStateOf<SupervisorResource?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Fetch resource details
    LaunchedEffect(resourceId) {
        coroutineScope.launch {
            try {
                val doc = firebaseManager.db.collection("Recurs").document(resourceId).get().await()
                resource = SupervisorResource(
                    id = doc.id,
                    title = doc.getString("title") ?: "Recurs sense títol",
                    type = doc.getString("type") ?: "Sense categoria",
                    uri = doc.getString("uri") ?: "",
                    timestamp = doc.getTimestamp("timestamp")
                )
                isLoading = false
            } catch (e: Exception) {
                errorMessage = "Error de connexió:: ${e.localizedMessage}"
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = resource?.title ?: "Détails de la ressource",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                errorMessage != null -> {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                resource != null -> {
                    ResourceContent(resource!!)
                }
                else -> {
                    Text(
                        text = "No hi ha recursos disponibles en aquest moment",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
private fun ResourceContent(resource: SupervisorResource) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Resource details
        Text(
            text = "Tipus de recurs: ${resource.type.capitalize()}",
            color = LocalCustomColors.current.text2,
            fontWeight = FontWeight.Medium,
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Render different content based on resource type
        when (resource.type) {
            "image" -> {
                ResourceImage(imageUrl = resource.uri)
            }
            "video" -> {
                ResourceVideo(videoUrl = resource.uri)
            }
            "audio" -> {
                ResourceAudio(audioUrl = resource.uri)
            }
            else -> {
                Text(
                    text = "Tipus de recurs non supporté",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun ResourceImage(imageUrl: String) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        AndroidView(
            factory = { ctx ->
                androidx.appcompat.widget.AppCompatImageView(ctx).apply {
                    Glide.with(ctx)
                        .load(imageUrl)
                        .apply(RequestOptions().fitCenter())
                        .into(this)
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun ResourceVideo(videoUrl: String) {
    val context = LocalContext.current

    // Create ExoPlayer
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(Uri.parse(videoUrl)))
            prepare()
        }
    }

    // Dispose of the player when the composable is disposed
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun ResourceAudio(audioUrl: String) {
    val context = LocalContext.current

    // Create ExoPlayer for audio
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(Uri.parse(audioUrl)))
            prepare()
        }
    }

    // Dispose of the player when the composable is disposed
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Lecture audio",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    if (exoPlayer.isPlaying) exoPlayer.pause()
                    else exoPlayer.play()
                }
            ) {
                Text(text = if (exoPlayer.isPlaying) "Pause" else "Play")
            }
        }

        // Optional: Add a progress bar or other audio controls
    }
}