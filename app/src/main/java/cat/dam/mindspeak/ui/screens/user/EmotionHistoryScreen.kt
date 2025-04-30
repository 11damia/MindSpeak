package cat.dam.mindspeak.ui.screens.user


import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cat.dam.mindspeak.R
import cat.dam.mindspeak.model.EmotionRecord
import cat.dam.mindspeak.model.EmotionViewModel
import cat.dam.mindspeak.ui.theme.LocalCustomColors
import coil.compose.AsyncImage
import coil.request.ImageRequest
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun EmotionHistoryScreen(viewModel: EmotionViewModel = viewModel()) {
    val emotionRecords by viewModel.emotionRecords.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = stringResource(R.string.emotions_history),
            style = MaterialTheme.typography.headlineMedium,
            color = LocalCustomColors.current.text1,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (emotionRecords.isEmpty()) {
            Text(
                text = stringResource(R.string.emotion_recorded),
                color = LocalCustomColors.current.text1,
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            LazyColumn {
                items(emotionRecords) { emotion ->
                    EmotionHistoryItem(emotion)
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                }
            }
        }
    }
}

@Composable
fun EmotionHistoryItem(emotion: EmotionRecord) {
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) } // Estado para controlar el diálogo

    emotion.fotoUri?.let {
        Log.d("EmotionHistoryItem", "URL de imagen a cargar: $it")
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        colors = CardDefaults.cardColors(containerColor = getEmotionColor(emotion.emotionType))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = getTranslatedEmotion(emotion.emotionType),
                    style = MaterialTheme.typography.titleMedium,
                    color = LocalCustomColors.current.text1
                )
                Spacer(modifier = Modifier.height(4.dp))
                if (emotion.comentari.isNotBlank()) {
                    Text(
                        text = emotion.comentari,
                        style = MaterialTheme.typography.bodyMedium,
                        color = LocalCustomColors.current.text1
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
                Text(
                    text = stringResource(R.string.evaluation_history, emotion.rating),
                    style = MaterialTheme.typography.bodyMedium,
                    color = LocalCustomColors.current.text1
                )
                Text(
                    text = stringResource(R.string.date_history, dateFormat.format(emotion.date)),
                    style = MaterialTheme.typography.bodySmall,
                    color = LocalCustomColors.current.text1.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .padding(4.dp)
                    .clickable { showDialog = true }, // Al hacer clic, abrir el diálogo
                contentAlignment = Alignment.Center
            ) {
                if (!emotion.fotoUri.isNullOrEmpty()) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(emotion.fotoUri)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Imagen de ${emotion.emotionType}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Image(
                        painter = painterResource(id = getEmotionImage(emotion.emotionType)),
                        contentDescription = "Icono de ${emotion.emotionType}",
                        modifier = Modifier.size(64.dp)
                    )
                }
            }
        }
    }

    // **Diálogo para ver la imagen en grande**
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {},
            text = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(emotion.fotoUri)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Imagen en grande de ${emotion.emotionType}",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        )
    }
}
