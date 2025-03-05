package cat.dam.mindspeak.firebase

import android.util.Log
import cat.dam.mindspeak.model.EmotionRecord
import kotlinx.coroutines.tasks.await

class EmotionRepository {
    private val firestore = FirebaseManager.firestore
    private val auth = FirebaseManager.auth

    // Collection reference
    private val emotionsCollection = firestore.collection("emotions")

    // Add emotion record to Firestore
    suspend fun addEmotionRecord(record: EmotionRecord) {
        try {
            // Get current user ID or use anonymous ID
            val userId = getCurrentUserId()

            // Create a new record with user ID
            val newRecord = record.copy(userId = userId ?: "")

            // Add to Firestore
            emotionsCollection.add(newRecord.toMap()).await()
        } catch (e: Exception) {
            Log.e("EmotionRepository", "Error adding emotion record", e)
            throw e
        }
    }

    // Fetch emotion records for current user
    suspend fun getEmotionRecords(): List<EmotionRecord> {
        return try {
            val userId = getCurrentUserId()

            val snapshot = emotionsCollection
                .whereEqualTo("userId", userId)
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()

            snapshot.toObjects(EmotionRecord::class.java)
        } catch (e: Exception) {
            Log.e("EmotionRepository", "Error fetching emotion records", e)
            emptyList()
        }
    }

    // Get current user ID (or create anonymous user if not logged in)
    private fun getCurrentUserId(): String? {
        // If user is logged in, return their UID
        return FirebaseManager.getCurrentUserId() ?: run {
            // If no user, sign in anonymously
            try {
                // This is a synchronous call in a real app, you'd want to handle this more carefully
                val result = auth.signInAnonymously().result
                result.user?.uid
            } catch (e: Exception) {
                Log.e("EmotionRepository", "Error creating anonymous user", e)
                null
            }
        }
    }
}