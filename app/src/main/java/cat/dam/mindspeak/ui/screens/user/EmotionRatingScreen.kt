package cat.dam.mindspeak.ui.screens.user

import android.util.Log
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import cat.dam.mindspeak.R
import cat.dam.mindspeak.model.EmotionRecord
import cat.dam.mindspeak.model.EmotionViewModel
import cat.dam.mindspeak.ui.theme.LocalCustomColors
import java.util.Date

@Composable
fun EmotionRatingScreen(
    navController: NavHostController,
    backStackEntry: NavBackStackEntry,
    viewModel: EmotionViewModel = viewModel()
) {
    val emotionType = backStackEntry.arguments?.getString("emotionType") ?: "UNKNOWN"

    if (emotionType == "UNKNOWN") {
        Log.e("EmotionRatingScreen", "Argument 'emotionType' is missing or invalid")
        navController.popBackStack()
        return
    }

    var rating by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = getEmotionColor(emotionType))
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = emotionType.uppercase(),
                    fontSize = 24.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(16.dp))
                Image(
                    painter = painterResource(id = getEmotionImage(emotionType)),
                    contentDescription = null,
                    modifier = Modifier.size(80.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            repeat(5) { index ->
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = if (index < rating) Color.Yellow else Color.Gray,
                    modifier = Modifier
                        .size(40.dp)
                        .clickable { rating = index + 1 }
                )
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = {
                // Use the EmotionRecord constructor with all parameters
                val emotionRecord = EmotionRecord(
                    emotionType = emotionType,
                    rating = rating,
                    date = Date()
                )
                viewModel.addEmotionRecord(emotionRecord)
                navController.popBackStack()
            },
            colors = ButtonDefaults.buttonColors(containerColor = LocalCustomColors.current.secondary)
        ) {
            Text(stringResource(R.string.save), color = LocalCustomColors.current.text1)
        }
    }
}

fun getEmotionColor(emotionType: String): Color {
    return when (emotionType) {
        "ENFADADO" -> Color(0xFFFF5353)
        "TRISTE" -> Color(0xFF2E9DFF)
        "MIEDO" -> Color(0xFF894AB8)
        "FELIZ" -> Color(0xFF4CAF50)
        "ANSIOSO" -> Color(0xFFD97904)
        else -> Color.Gray
    }
}

fun getEmotionImage(emotionType: String): Int {
    return when (emotionType) {
        "ENFADADO" -> R.drawable.enfadado
        "TRISTE" -> R.drawable.triste
        "MIEDO" -> R.drawable.miedoso
        "FELIZ" -> R.drawable.feliz
        "ANSIOSO" -> R.drawable.ansioso
        else -> R.drawable.placeholder
    }
}
