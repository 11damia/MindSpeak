package cat.dam.mindspeak.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cat.dam.mindspeak.R
import cat.dam.mindspeak.ui.theme.CustomColors
import cat.dam.mindspeak.ui.theme.LocalCustomColors


@Composable
fun Statistics(localCustomColors: ProvidableCompositionLocal<CustomColors>) {

    val exercises = listOf(
        "Inhala\n3 segundos" to R.drawable.inspirar,
        "Aguanta\n7 segundos" to R.drawable.aguantar,
        "Exhala\n8 segundos" to R.drawable.expirar
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 20.dp, end = 20.dp)
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                Text(
                    text = "Relájemos juntos",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp,
                        color = LocalCustomColors.current.text1,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            items(exercises) { (text, imageRes) ->
                ExerciseStep(text, imageRes)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    MaterialTheme {
        Statistics(LocalCustomColors)
    }
}