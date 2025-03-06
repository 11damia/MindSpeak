package cat.dam.mindspeak.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EmotionStatistics() {
    val daysOfWeek = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")
    val emotions = listOf("Miedo", "Felicidad", "Ansiedad", "Tristeza", "Ira")
    val colors = listOf(Color(0xFF9B59B6), Color(0xFFFFEB3B), Color(0xFFFF9800), Color(0xFF2196F3), Color(0xFFF44336))
    val values = listOf(
        listOf(2, 5, 1, 3, 2),
        listOf(1, 10, 3, 6, 4),
        listOf(2, 4, 3, 2, 5),
        listOf(7, 3, 2, 1, 2),
        listOf(4, 6, 2, 3, 1),
        listOf(3, 5, 12, 2, 4),
        listOf(2, 14, 4, 1, 3)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val barHeight = canvasHeight / (daysOfWeek.size * 1.5f)
            val maxValue = 15f
            val scaleX = (canvasWidth - 100f) / maxValue

            // Dibujar números en la parte inferior
            for (i in 1..15 step 2) {
                val x = 100f + (i * scaleX)
                drawIntoCanvas { canvas ->
                    val paint = android.graphics.Paint().apply {
                        color = android.graphics.Color.BLACK
                        textSize = 24f
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                    canvas.nativeCanvas.drawText(i.toString(), x, canvasHeight - 10f, paint)
                }
            }

            daysOfWeek.forEachIndexed { dayIndex, day ->
                val y = dayIndex * barHeight * 1.5f + barHeight / 2
                drawIntoCanvas { canvas ->
                    val paint = android.graphics.Paint().apply {
                        color = android.graphics.Color.BLACK
                        textSize = 30f
                        textAlign = android.graphics.Paint.Align.RIGHT
                    }
                    canvas.nativeCanvas.drawText(day, 40f, y + 10f, paint)
                }

                var xOffset = 100f
                values[dayIndex].forEachIndexed { emotionIndex, value ->
                    drawRect(
                        color = colors[emotionIndex],
                        topLeft = Offset(xOffset, y),
                        size = Size(value * scaleX, barHeight * 0.8f)
                    )
                    xOffset += value * scaleX + 5f
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Leyenda con nombres y colores
        Column(modifier = Modifier.fillMaxWidth()) {
            emotions.forEachIndexed { index, emotion ->
                Row(modifier = Modifier.padding(4.dp)) {
                    Canvas(modifier = Modifier.size(20.dp)) {
                        drawCircle(color = colors[index])
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    androidx.compose.material3.Text(text = emotion, fontSize = 16.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEmotionStatistics() {
    MaterialTheme {
        EmotionStatistics()
    }
}