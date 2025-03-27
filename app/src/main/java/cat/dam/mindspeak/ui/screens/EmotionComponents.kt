package cat.dam.mindspeak.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cat.dam.mindspeak.model.EmotionRecord
import cat.dam.mindspeak.model.AssignedUser
import cat.dam.mindspeak.ui.theme.LocalCustomColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserSelector(
    users: List<AssignedUser>,
    selectedUser: AssignedUser?,
    onUserSelected: (AssignedUser) -> Unit,
    isLoading: Boolean
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Usuario a analizar",
                    color = LocalCustomColors.current.text1,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedUser?.let { "${it.nom} ${it.cognom}" } ?: "",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
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
        }
    }
}

@Composable
fun SimpleEmotionBarChart(records: List<EmotionRecord>) {
    if (records.isEmpty()) return

    val emotionColors = mapOf(
        "ENFADADO" to Color(0xFFFF5353),
        "FELIZ" to Color(0xFF4CAF50),
        "MIEDO" to Color(0xFF894AB8),
        "TRISTE" to Color(0xFF2E9DFF),
        "ANSIOSO" to Color(0xFFD97904)
    )

    val emotionCounts = records.groupBy { it.emotionType }
        .mapValues { it.value.size }
        .toList()
        .sortedByDescending { it.second }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Distribución de Emociones",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Gráfico de barras (sin etiquetas de emoción)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                emotionCounts.forEach { (emotion, count) ->
                    val color = emotionColors[emotion] ?: Color.Gray
                    val height = (count.toFloat() / emotionCounts.maxOf { it.second } * 180).dp

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.width(40.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(height)
                                .background(color)
                        )
                        Text(
                            text = count.toString(), // Mantenemos solo el número de registros
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        // Eliminamos el Text que mostraba las 3 letras de la emoción
                    }
                }
            }

            // Leyenda de colores
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Leyenda:",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Column {
                emotionColors.forEach { (emotion, color) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .background(color)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = emotion.lowercase().replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecentEmotionRecords(records: List<EmotionRecord>) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Registros Recientes",
                style = MaterialTheme.typography.titleMedium
            )

            if (records.isEmpty()) {
                Text(
                    text = "No hay registros recientes",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                records.forEach { record ->
                    EmotionRecordItem(record)
                }
            }
        }
    }
}

@Composable
fun EmotionRecordItem(record: EmotionRecord) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = record.emotionType, style = MaterialTheme.typography.bodyLarge)
                Text(text = record.date.toString(), style = MaterialTheme.typography.bodySmall)
                if (record.comentari.isNotEmpty()) {
                    Text(
                        text = record.comentari,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            Text(
                text = "Valoración: ${record.rating}",
                style = MaterialTheme.typography.bodyLarge,
                color = when (record.rating) {
                    in 0..2 -> Color.Red
                    in 3..4 -> Color.Yellow
                    else -> Color.Green
                }
            )
        }
    }
}