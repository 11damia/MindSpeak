package cat.dam.mindspeak.ui.screens.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import cat.dam.mindspeak.ui.theme.LocalCustomColors
import cat.dam.mindspeak.R

@Composable
fun Exercises(navController: NavHostController) {
    val exercises = listOf(
        stringResource(R.string.inhale) to R.drawable.inspirar,
        stringResource(R.string.hold) to R.drawable.aguantar,
        stringResource(R.string.exhale) to R.drawable.expirar
    )


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 20.dp, end = 20.dp)
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                Text(
                    text = stringResource(R.string.relax_together),
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
@Composable
fun ExerciseStep(
    text: String,
    imageRes: Int
) {
    val localCustomColors = LocalCustomColors.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Spacer(Modifier.weight(0.1f))
        Text(
            text = text,
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp,
                color = localCustomColors.text1
            ),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Start
        )
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = text,
            modifier = Modifier
                .width(200.dp)
                .height(200.dp)
        )
        Spacer(Modifier.weight(0.1f))
    }
    Spacer(modifier = Modifier.height(16.dp))
}

@Preview(showBackground = true)
@Composable
fun BottomBarPreview() {
    Exercises(navController = rememberNavController())
}
