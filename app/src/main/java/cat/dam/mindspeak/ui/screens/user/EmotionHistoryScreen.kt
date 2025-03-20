package cat.dam.mindspeak.ui.screens.user


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cat.dam.mindspeak.model.EmotionRecord
import cat.dam.mindspeak.model.EmotionViewModel
import cat.dam.mindspeak.ui.theme.LocalCustomColors
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun EmotionHistoryScreen(viewModel: EmotionViewModel = viewModel()) {
    val emotionRecords by viewModel.emotionRecords.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Historial de emociones",
            style = MaterialTheme.typography.headlineMedium,
            color = LocalCustomColors.current.text1,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (emotionRecords.isEmpty()) {
            Text(
                text = "No hay emociones registradas.",
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
                    text = emotion.emotionType.uppercase(),
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
                    text = "Evaluación : ${emotion.rating}/5",
                    style = MaterialTheme.typography.bodyMedium,
                    color = LocalCustomColors.current.text1
                )
                Text(
                    text = "Fecha : ${dateFormat.format(emotion.date)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = LocalCustomColors.current.text1.copy(alpha = 0.7f)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))

            // Ici, nous affichons soit l'image téléchargée par l'utilisateur (si elle existe),
            // soit l'icône d'émotion par défaut
            if (emotion.fotoUri != null && emotion.fotoUri.isNotEmpty()) {
                // Utiliser Coil pour charger l'image depuis l'URL Supabase
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(context)
                            .data(emotion.fotoUri)
                            .crossfade(true)
                            .build()
                    ),
                    contentDescription = "Photo pour ${emotion.emotionType}",
                    modifier = Modifier.size(64.dp)
                )
            } else {
                // Utiliser l'icône par défaut pour cette émotion
                Image(
                    painter = painterResource(id = getEmotionImage(emotion.emotionType)),
                    contentDescription = null,
                    modifier = Modifier.size(64.dp)
                )
            }
        }
    }
}
/*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cat.dam.mindspeak.R
import cat.dam.mindspeak.model.EmotionRecord
import cat.dam.mindspeak.model.EmotionViewModel
import cat.dam.mindspeak.ui.theme.LocalCustomColors
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun EmotionHistoryScreen(viewModel: EmotionViewModel = viewModel()) {
    val emotionRecords by viewModel.emotionRecords.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = stringResource(R.string.history_emotions),
            style = MaterialTheme.typography.headlineMedium,
            color = LocalCustomColors.current.text1,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (emotionRecords.isEmpty()) {
            Text(
                text = stringResource(R.string.not_emotions_registry),
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
                    text = emotion.emotionType.uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    color = LocalCustomColors.current.text1
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.evaluation, emotion.rating),
                    style = MaterialTheme.typography.bodyMedium,
                    color = LocalCustomColors.current.text1
                )
                Text(
                    text = stringResource(R.string.date, dateFormat.format(emotion.date)),
                    style = MaterialTheme.typography.bodySmall,
                    color = LocalCustomColors.current.text1.copy(alpha = 0.7f)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Image(
                painter = painterResource(id = getEmotionImage(emotion.emotionType)),
                contentDescription = null,
                modifier = Modifier.size(64.dp)
            )
        }
    }
}

 */