package cat.dam.mindspeak.model

import androidx.compose.ui.graphics.Color

data class EmotionItem(
    val text: String,
    val color: Color,
    val imageRes: Int = 0,  // Ressource locale (optionnelle)
    val imageUrl: String = ""  // URL d'image (pour Firebase)
)
