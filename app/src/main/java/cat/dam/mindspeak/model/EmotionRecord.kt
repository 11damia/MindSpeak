package cat.dam.mindspeak.model


import com.google.firebase.firestore.DocumentId
import java.util.Date

data class EmotionRecord(
    @DocumentId
    val id: String = "",
    val emotionType: String = "",
    val rating: Int = 0,
    val date: Date = Date(),
    val userId: String = "" // To associate records with specific users
) {
    // Firestore requires a no-argument constructor
    constructor() : this("", "", 0, Date(), "")

    // Convert to map for Firestore
    fun toMap(): Map<String, Any> = mapOf(
        "emotionType" to emotionType,
        "rating" to rating,
        "date" to date,
        "userId" to userId
    )
}