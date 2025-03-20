package cat.dam.mindspeak.ui.screens.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import cat.dam.mindspeak.R
import cat.dam.mindspeak.ui.theme.LocalCustomColors

data class EmotionItem(
    val text: String,
    val color: Color,
    val imageRes: Int
)

@Composable
fun Emotions(navController: NavHostController) {
    val emotions = listOf(
        EmotionItem("ENFADADO", Color(0xFFFF5353), R.drawable.enfadado),
        EmotionItem("TRISTE", Color(0xFF2E9DFF), R.drawable.triste),
        EmotionItem("MIEDO", Color(0xFF894AB8), R.drawable.miedoso),
        EmotionItem("FELIZ", Color(0xFF4CAF50), R.drawable.feliz),
        EmotionItem("ANSIOSO", Color(0xFFD97904), R.drawable.ansioso)
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.how_do_you_feel),
                fontSize = 24.sp,
                color = LocalCustomColors.current.text1
            )
            Spacer(modifier = Modifier.height(32.dp))
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(emotions) { emotion ->
                    EmotionCard(
                        emotionItem = emotion,
                        navController = navController
                    )
                }
            }
        }
    }
}

@Composable
fun EmotionCard(emotionItem: EmotionItem, navController: NavHostController) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .clickable {
                navController.navigate("emotionRating/${emotionItem.text}")
            }
    ) {
        Surface(
            color = emotionItem.color
        ) {
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = emotionItem.text,
                    fontSize = 18.sp,
                    color = LocalCustomColors.current.text4
                )
                Spacer(modifier = Modifier.width(16.dp))
                Image(
                    painter = painterResource(id = emotionItem.imageRes),
                    contentDescription = null,
                    modifier = Modifier.size(95.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEmotionSelectorScreen() {
    MaterialTheme {
        Emotions(navController = rememberNavController())
    }
}