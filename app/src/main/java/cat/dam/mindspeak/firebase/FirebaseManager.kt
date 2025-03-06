package cat.dam.mindspeak.firebase

import android.util.Log
import cat.dam.mindspeak.model.EmotionRecord
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.util.Date

class FirebaseManager {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    companion object {
        fun logoutUser() {
            FirebaseAuth.getInstance().signOut()
        }
    }
    // Registrar un usuari
    suspend fun registrarUsuari(
        email: String,
        contrasenya: String,
        nom: String,
        cognom: String,
        telefon: String?,
        dataNaixement: Long?,
        sexe: String?,
        grau: String?,
        rol: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        try {
            // Crear compte a Firebase Auth
            val authResult = auth.createUserWithEmailAndPassword(email, contrasenya).await()
            val userId = authResult.user?.uid ?: ""

            // Afegir informació a Firestore
            val personaData = mutableMapOf<String, Any?>(
                "email" to email,
                "nom" to nom,
                "cognom" to cognom,
                "rol" to rol,
                "telefon" to telefon,
                "contrasenya" to contrasenya // Nota: Utilitza un hash per la seguretat
            )

            // Guardar dades bàsiques a Persona
            db.collection("Persona").document(userId).set(personaData).await()

            // Crear rol específic a la subcol·lecció Roles
            val rolData = mutableMapOf<String, Any?>(
                "type" to rol
            )
            if (dataNaixement != null) rolData["data_naixement"] = dataNaixement
            if (sexe != null) rolData["sexe"] = sexe
            if (grau != null) rolData["grau"] = grau

            db.collection("Persona").document(userId).collection("Roles")
                .add(rolData).await()

            onSuccess()
        } catch (e: Exception) {
            onFailure(e.message ?: "Error desconegut")
        }
    }

    // Iniciar sessió
    suspend fun iniciarSessio(
        email: String,
        contrasenya: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        try {
            // Connectar l'usuari via Firebase Auth
            auth.signInWithEmailAndPassword(email, contrasenya).await()
            onSuccess()
        } catch (e: Exception) {
            onFailure(e.message ?: "Error desconegut")
        }
    }

    // Obtindre el rol de l'usuari connectat
    suspend fun obtenirRolUsuari(): String? {
        try {
            val userId = auth.currentUser?.uid ?: return null
            val documentSnapshot = db.collection("Persona").document(userId).get().await()

            if (documentSnapshot.exists()) {
                return documentSnapshot.getString("rol")
            }
        } catch (e: Exception) {
            println("Error al obtenir el rol: ${e.message}")
        }
        return null
    }

    // Comprovar si hi ha un usuari connectat
    fun estaUsuariConnectat(): Boolean {
        return auth.currentUser != null
    }
    // Add a new emotion record
    suspend fun afegirRegistreEmocio(
        emotionType: String,
        rating: Int,
        onSuccess: () -> Unit = {},
        onFailure: (String) -> Unit = {}
    ) {
        try {
            // Get current user ID
            val userId = auth.currentUser?.uid
                ?: throw Exception("No hi ha un usuari connectat")

            // Prepare emotion record data
            val emotionData = mapOf(
                "userId" to userId,
                "emotionType" to emotionType,
                "rating" to rating,
                "date" to com.google.firebase.Timestamp.now()
            )

            // Add to Firestore
            db.collection("Emotions")
                .add(emotionData)
                .await()

            onSuccess()
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Error adding emotion record", e)
            onFailure(e.message ?: "Error desconegut en afegir el registre d'emocions")
        }
    }

    // Fetch emotion records for the current user
    suspend fun obtenirRegistresEmocions(): List<EmotionRecord> {
        try {
            // Get current user ID
            val userId = auth.currentUser?.uid
                ?: return emptyList()

            // Fetch emotions for the current user, ordered by date
            val snapshot = db.collection("Emotions")
                .whereEqualTo("userId", userId)
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()

            // Convert Firestore documents to EmotionRecord objects
            return snapshot.documents.map { document ->
                EmotionRecord(
                    id = document.id,
                    emotionType = document.getString("emotionType") ?: "",
                    rating = document.getLong("rating")?.toInt() ?: 0,
                    date = document.getTimestamp("date")?.toDate() ?: Date(),
                    userId = userId
                )
            }
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Error fetching emotion records", e)
            return emptyList()
        }
    }

    // Eliminar un registre d'emocions específic
    suspend fun eliminarRegistreEmocio(
        emotionRecordId: String,
        onSuccess: () -> Unit = {},
        onFailure: (String) -> Unit = {}
    ) {
        try {
            // Verificar que l'usuari estigui connectat
            val userId = auth.currentUser?.uid
                ?: throw Exception("No hi ha un usuari connectat")

            // Eliminar el registre d'emocions
            db.collection("Emotions")
                .document(emotionRecordId)
                .delete()
                .await()

            onSuccess()
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Error deleting emotion record", e)
            onFailure(e.message ?: "Error desconegut en eliminar el registre d'emocions")
        }
    }

    // Obtenir estadístiques bàsiques de les emocions
    suspend fun obtenirEstatistiquesEmocions(): Map<String, Any> {
        try {
            // Get current user ID
            val userId = auth.currentUser?.uid
                ?: return emptyMap()

            // Fetch all emotion records for the user
            val snapshot = db.collection("Emotions")
                .whereEqualTo("userId", userId)
                .get()
                .await()

            // Calcular estadístiques
            val emotions = snapshot.documents.map { document ->
                document.getString("emotionType") to document.getLong("rating")?.toInt()
            }

            return mapOf(
                "totalRegistres" to emotions.size,
                "mitjanaValoracions" to emotions.mapNotNull { it.second }.average(),
                "distribuciooEmocions" to emotions.groupingBy { it.first }.eachCount()
            )
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Error fetching emotion statistics", e)
            return emptyMap()
        }
    }

}