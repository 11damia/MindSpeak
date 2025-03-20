package cat.dam.mindspeak.model

import com.google.firebase.firestore.DocumentId
import java.util.Date

data class EmotionRecord(
    @DocumentId
    val id: String = "",
    val emotionType: String = "",
    val rating: Int = 0,
    val date: Date = Date(),
    val userId: String = "", // Per associar registres amb usuaris específics
    val comentari: String = "", // Nou camp pel comentari emocional
    val fotoUri: String? = null // Nou camp per l'URL de la foto a Supabase
) {
    // Firestore requereix un constructor sense arguments
    constructor() : this("", "", 0, Date(), "", "", null)

    // Convertir a map per Firestore
    fun toMap(): Map<String, Any> {
        val map = mutableMapOf(
            "emotionType" to emotionType,
            "rating" to rating,
            "date" to date,
            "userId" to userId,
            "comentari" to comentari
        )

        // Afegir fotoUri només si no és null
        fotoUri?.let { map["fotoUri"] = it }

        return map
    }
}