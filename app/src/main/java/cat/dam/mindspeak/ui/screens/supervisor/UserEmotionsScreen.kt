package cat.dam.mindspeak.ui.screens.supervisor

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import cat.dam.mindspeak.R
import cat.dam.mindspeak.firebase.FirebaseManager
import cat.dam.mindspeak.model.EmotionRecord
import cat.dam.mindspeak.ui.screens.user.getEmotionColor
import cat.dam.mindspeak.ui.screens.user.getEmotionImage
import cat.dam.mindspeak.ui.theme.LocalCustomColors
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun UserEmotionsScreen(
    navController: NavHostController,
    userId: String,
    firebaseManager: FirebaseManager = FirebaseManager()
) {
    // State to hold the list of emotion records
    var emotionRecords by remember { mutableStateOf<List<EmotionRecord>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Coroutine scope for async operations
    val coroutineScope = rememberCoroutineScope()

    // Fetch emotion records for the specific user when the screen is first loaded
    LaunchedEffect(userId) {
        coroutineScope.launch {
            try {
                emotionRecords = firebaseManager.getEmotionsForUser(userId)
            } catch (e: Exception) {
                Log.e("UserEmotionsScreen", "Error fetching emotions", e)
            } finally {
                isLoading = false
            }
        }
    }

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Titre stylisé
            Text(
                text = stringResource(R.string.Emotions),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                textAlign = TextAlign.Center
            )

            // Animation de chargement
            AnimatedVisibility(
                visible = isLoading,
                enter = fadeIn(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(300))
            ) {
                LoadingAnimation()
            }

            // Liste des émotions ou message d'absence
            AnimatedVisibility(
                visible = !isLoading,
                enter = fadeIn(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(300))
            ) {
                if (emotionRecords.isEmpty()) {
                    EmptyStateView()
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(emotionRecords) { record ->
                            EmotionRecordItem(record)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "Loading Transition")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Cloud Scale"
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Cloud,
                contentDescription = "Chargement",
                modifier = Modifier
                    .size(100.dp)
                    .scale(scale)
                    .alpha(0.7f),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = stringResource(R.string.loading_emotions),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun EmptyStateView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = getEmotionImage("neutral")),
                contentDescription = "Aucune émotion",
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.no_emotions_found),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    }
}


@Composable
fun EmotionRecordItem(record: EmotionRecord) {
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    record.fotoUri?.let {
        Log.d("EmotionRecordItem", "URL de imagen a cargar: $it")
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        colors = CardDefaults.cardColors(containerColor = getEmotionColor(record.emotionType))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = record.emotionType.uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    color = LocalCustomColors.current.text1
                )
                Spacer(modifier = Modifier.height(4.dp))
                if (!record.comentari.isNullOrBlank()) {
                    Text(
                        text = record.comentari,
                        style = MaterialTheme.typography.bodyMedium,
                        color = LocalCustomColors.current.text1
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
                Text(
                    text = stringResource(R.string.evaluation_2, record.rating),
                    style = MaterialTheme.typography.bodyMedium,
                    color = LocalCustomColors.current.text1
                )
                Text(
                    text = stringResource(R.string.date_2, dateFormat.format(record.date)),
                    style = MaterialTheme.typography.bodySmall,
                    color = LocalCustomColors.current.text1.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .padding(4.dp)
                    .clickable { showDialog = true },
                contentAlignment = Alignment.Center
            ) {
                if (!record.fotoUri.isNullOrEmpty()) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(record.fotoUri)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Imagen de ${record.emotionType}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Image(
                        painter = painterResource(id = getEmotionImage(record.emotionType)),
                        contentDescription = "Icono de ${record.emotionType}",
                        modifier = Modifier.size(64.dp)
                    )
                }
            }
        }
    }

    // Diálogo para ver la imagen en grande
    if (showDialog && !record.fotoUri.isNullOrEmpty()) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = MaterialTheme.shapes.large,
            title = {
                Text(
                    text = " ${record.emotionType}",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(record.fotoUri)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Imagen en grande de ${record.emotionType}",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            },
            confirmButton = {
                Text(
                    text = stringResource(R.string.close),
                    modifier = Modifier
                        .clickable { showDialog = false }
                        .padding(16.dp),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        )
    }
}
