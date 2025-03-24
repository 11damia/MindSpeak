package cat.dam.mindspeak.ui.screens.user

import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import cat.dam.mindspeak.firebase.FirebaseManager
import cat.dam.mindspeak.model.EmotionItem
import cat.dam.mindspeak.ui.theme.LocalCustomColors
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun Emotions(navController: NavHostController) {
    var emotions by remember { mutableStateOf<List<EmotionItem>>(listOf()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(key1 = Unit) {
        try {
            val firebaseManager = FirebaseManager()
            val loadedEmotions = firebaseManager.obtenirEmocions()
            emotions = loadedEmotions
            if (emotions.isEmpty()) {
                errorMessage = "No s'han trobat emocions"
            }
        } catch (e: Exception) {
            Log.e("Emotions", "Error loading emotions", e)
            errorMessage = e.message
        } finally {
            isLoading = false
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(LocalCustomColors.current.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Como te sientes?",
                fontSize = 24.sp,
                color = LocalCustomColors.current.text1
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (errorMessage != null) {
                Text(
                    text = "Error: $errorMessage",
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
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
}

@Composable
fun EmotionCard(emotionItem: EmotionItem, navController: NavHostController) {
    val context = LocalContext.current

    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .fillMaxWidth()
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

                // Use AsyncImage from Coil to load the image from URL
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(emotionItem.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = emotionItem.text,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(95.dp),

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