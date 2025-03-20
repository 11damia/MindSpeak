package cat.dam.mindspeak.firebase


import android.util.Log
import androidx.compose.ui.graphics.Color
import cat.dam.mindspeak.model.EmotionItem
import cat.dam.mindspeak.model.EmotionRecord
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.util.Date

class EmotionRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Collection references
    private val emotionsCollection = firestore.collection("RegistroEmotions")
    private val emotionTypesCollection = firestore.collection("Emocio")

    // Add emotion record to Firestore
    suspend fun addEmotionRecord(record: EmotionRecord) {
        try {
            // Get current user ID or use anonymous ID
            val userId = auth.currentUser?.uid ?: ""

            // Create a new record with user ID
            val newRecord = record.copy(userId = userId)

            // Convert to map and add to Firestore
            emotionsCollection.add(newRecord.toMap()).await()
        } catch (e: Exception) {
            Log.e("EmotionRepository", "Error adding emotion record", e)
            throw e
        }
    }

    // Fetch emotion records for current user
    suspend fun getEmotionRecords(): List<EmotionRecord> {
        return try {
            val userId = auth.currentUser?.uid ?: ""

            val snapshot = emotionsCollection
                .whereEqualTo("userId", userId)
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()

            // Convert documents to EmotionRecord objects
            snapshot.documents.map { doc ->
                doc.getString("comentari")?.let {
                    EmotionRecord(
                        id = doc.id,
                        emotionType = doc.getString("emotionType") ?: "",
                        rating = doc.getLong("rating")?.toInt() ?: 0,
                        date = doc.getDate("date") ?: Date(),
                        userId = userId,
                        comentari = it,
                        fotoUri = doc.getString("fotoUri")
                    )
                }!!
            }
        } catch (e: Exception) {
            Log.e("EmotionRepository", "Error fetching emotion records", e)
            emptyList()
        }
    }

    // Fetch available emotion types with images
    suspend fun getEmotionTypes(): List<EmotionItem> {
        return try {
            Log.d("EmotionRepository", "Fetching emotion types")
            val snapshot = emotionTypesCollection.get().await()

            snapshot.documents.map { document ->
                // Convert color format to Compose Color
                val colorHex = document.getString("color") ?: "FFFFFFFF"
                val color = Color(android.graphics.Color.parseColor("#$colorHex"))

                EmotionItem(
                    text = document.getString("emocio_nom") ?: "",
                    color = color,
                    imageUrl = document.getString("imagen") ?: ""
                )
            }
        } catch (e: Exception) {
            Log.e("EmotionRepository", "Error fetching emotion types", e)
            throw e
        }
    }

    // Get a specific emotion by type
    suspend fun getEmotionByType(emotionType: String): EmotionItem? {
        return try {
            val snapshot = emotionTypesCollection
                .whereEqualTo("emocio_nom", emotionType)
                .limit(1)
                .get()
                .await()

            if (snapshot.documents.isEmpty()) {
                Log.d("EmotionRepository", "No emotion found with type: $emotionType")
                null
            } else {
                val document = snapshot.documents[0]
                val colorHex = document.getString("color") ?: "FFFFFFFF"
                val color = Color(android.graphics.Color.parseColor("#$colorHex"))

                EmotionItem(
                    text = document.getString("emocio_nom") ?: "",
                    color = color,
                    imageUrl = document.getString("imagen") ?: ""
                )
            }
        } catch (e: Exception) {
            Log.e("EmotionRepository", "Error fetching emotion by type", e)
            null
        }
    }

    // Delete an emotion record
    suspend fun deleteEmotionRecord(recordId: String) {
        try {
            emotionsCollection.document(recordId).delete().await()
        } catch (e: Exception) {
            Log.e("EmotionRepository", "Error deleting emotion record", e)
            throw e
        }
    }
}
/*
import android.util.Log
import cat.dam.mindspeak.model.EmotionRecord
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class EmotionRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Collection reference
    private val emotionsCollection = firestore.collection("RegistroEmotions")

    // Add emotion record to Firestore
    suspend fun addEmotionRecord(record: EmotionRecord) {
        try {
            // Get current user ID or use anonymous ID
            val userId = auth.currentUser?.uid ?: ""

            // Create a new record with user ID
            val newRecord = record.copy(userId = userId)

            // Convert to map and add to Firestore
            emotionsCollection.add(newRecord.toMap()).await()
        } catch (e: Exception) {
            Log.e("EmotionRepository", "Error adding emotion record", e)
            throw e
        }
    }

    // Fetch emotion records for current user
    suspend fun getEmotionRecords(): List<EmotionRecord> {
        return try {
            val userId = auth.currentUser?.uid ?: ""

            val snapshot = emotionsCollection
                .whereEqualTo("userId", userId)
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()

            // Convert documents to EmotionRecord objects
            snapshot.documents.map { doc ->
                EmotionRecord(
                    id = doc.id,
                    emotionType = doc.getString("emotionType") ?: "",
                    rating = doc.getLong("rating")?.toInt() ?: 0,
                    date = doc.getDate("date") ?: java.util.Date(),
                    userId = userId
                )
            }
        } catch (e: Exception) {
            Log.e("EmotionRepository", "Error fetching emotion records", e)
            emptyList()
        }
    }
}

 */