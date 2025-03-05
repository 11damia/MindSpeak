package cat.dam.mindspeak.firebase

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
    private val emotionsCollection = firestore.collection("emotions")

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