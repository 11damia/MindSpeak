package cat.dam.mindspeak.ui.components

import EmotionStatistics
import TimeFilter
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cat.dam.mindspeak.model.EmotionRecord
import cat.dam.mindspeak.model.AssignedUser
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.startAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.component.lineComponent
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.entry.entryModelOf
import java.util.*

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
fun EmotionStatsOverview(stats: EmotionStatistics) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Resumen Estadístico",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            StatItem("Registros totales", stats.totalRecords.toString())
            StatItem("Valoración media", "%.1f".format(stats.averageRating))
            StatItem("Registros última semana", stats.lastWeekCount.toString())
            StatItem("Emoción más común", stats.mostCommonEmotion.ifEmpty { "N/A" })
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun TimeFilterSelector(
    currentFilter: TimeFilter,
    onFilterSelected: (TimeFilter) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        TimeFilter.values().forEach { filter ->
            FilterChip(
                selected = currentFilter == filter,
                onClick = { onFilterSelected(filter) },
                label = { Text(getFilterLabel(filter)) },
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}

@Composable
fun EmotionTimelineChart(
    records: List<EmotionRecord>,
    timeFilter: TimeFilter,
    modifier: Modifier = Modifier
) {
    if (records.isEmpty()) return

    val (groupedData, xLabels) = remember(records, timeFilter) {
        processDataForTimeline(records, timeFilter)
    }

    val model = remember(groupedData) {
        entryModelOf(
            *groupedData.flatMap { map ->
                map.entries.map { (_, count) -> count.toFloat() }
            }.toTypedArray()
        )
    }

    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = when (timeFilter) {
                    TimeFilter.DAY -> "Emociones por horas"
                    TimeFilter.WEEK -> "Emociones por días"
                    TimeFilter.MONTH -> "Emociones por semanas"
                    TimeFilter.YEAR -> "Emociones por meses"
                },
                style = MaterialTheme.typography.titleMedium
            )

            ProvideChartStyle {
                Chart(
                    chart = columnChart(
                        columns = groupedData.flatMap { map ->
                            map.keys.map { emotion ->
                                lineComponent(
                                    color = getEmotionColor(emotion),
                                    thickness = 8.dp
                                )
                            }
                        }
                    ),
                    model = model,
                    startAxis = startAxis(
                        title = "Cantidad",
                        valueFormatter = { value, _ -> value.toInt().toString() }
                    ),
                    bottomAxis = bottomAxis(
                        title = "Tiempo",
                        valueFormatter = { value, _ ->
                            xLabels.getOrNull(value.toInt()) ?: ""
                        }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                )
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

// Funciones de ayuda
private fun getFilterLabel(filter: TimeFilter): String {
    return when (filter) {
        TimeFilter.DAY -> "Día"
        TimeFilter.WEEK -> "Semana"
        TimeFilter.MONTH -> "Mes"
        TimeFilter.YEAR -> "Año"
    }
}

private fun getEmotionColor(emotion: String): Color {
    return when (emotion.lowercase()) {
        "feliz" -> Color(0xFF4CAF50)
        "triste" -> Color(0xFF2196F3)
        "enojado" -> Color(0xFFF44336)
        "ansioso" -> Color(0xFFFFC107)
        "neutral" -> Color(0xFF9E9E9E)
        else -> Color(0xFF673AB7)
    }
}

private fun processDataForTimeline(
    records: List<EmotionRecord>,
    timeFilter: TimeFilter
): Pair<List<Map<String, Int>>, List<String>> {
    return when (timeFilter) {
        TimeFilter.DAY -> processByHours(records)
        TimeFilter.WEEK -> processByDays(records)
        TimeFilter.MONTH -> processByWeeks(records)
        TimeFilter.YEAR -> processByMonths(records)
    }
}

private fun processByHours(records: List<EmotionRecord>): Pair<List<Map<String, Int>>, List<String>> {
    val calendar = Calendar.getInstance()
    val hours = (0..23).map { hour ->
        records.filter { record ->
            calendar.time = record.date
            calendar.get(Calendar.HOUR_OF_DAY) == hour
        }.groupBy { it.emotionType }
            .mapValues { it.value.size }
    }
    return Pair(hours, (0..23).map { "$it:00" })
}

private fun processByDays(records: List<EmotionRecord>): Pair<List<Map<String, Int>>, List<String>> {
    val calendar = Calendar.getInstance()
    val days = (1..7).map { dayOfWeek ->
        records.filter { record ->
            calendar.time = record.date
            calendar.get(Calendar.DAY_OF_WEEK) == dayOfWeek
        }.groupBy { it.emotionType }
            .mapValues { it.value.size }
    }
    return Pair(days, listOf("Dom", "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb"))
}

private fun processByWeeks(records: List<EmotionRecord>): Pair<List<Map<String, Int>>, List<String>> {
    val calendar = Calendar.getInstance()
    val weeks = records.groupBy { record ->
        calendar.time = record.date
        calendar.get(Calendar.WEEK_OF_YEAR)
    }.values.map { weekRecords ->
        weekRecords.groupBy { it.emotionType }
            .mapValues { it.value.size }
    }
    val weekNumbers = records.map { record ->
        calendar.time = record.date
        calendar.get(Calendar.WEEK_OF_YEAR)
    }.distinct().sorted()

    return Pair(weeks, weekNumbers.map { "Sem $it" })
}

private fun processByMonths(records: List<EmotionRecord>): Pair<List<Map<String, Int>>, List<String>> {
    val calendar = Calendar.getInstance()
    val months = (0..11).map { month ->
        records.filter { record ->
            calendar.time = record.date
            calendar.get(Calendar.MONTH) == month
        }.groupBy { it.emotionType }
            .mapValues { it.value.size }
    }
    return Pair(months, listOf("Ene", "Feb", "Mar", "Abr", "May", "Jun",
        "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"))
}