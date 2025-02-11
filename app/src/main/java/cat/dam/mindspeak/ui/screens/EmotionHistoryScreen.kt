package cat.dam.mindspeak.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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
            text = "Historial de emociones",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (emotionRecords.isEmpty()) {
            Text(
                text = "No hay emociones registradas.",
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
            Image(
                painter = painterResource(id = getEmotionImage(emotion.emotionType)),
                contentDescription = null,
                modifier = Modifier.size(64.dp)
            )
        }
    }
}