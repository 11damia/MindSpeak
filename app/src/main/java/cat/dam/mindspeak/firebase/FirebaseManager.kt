package cat.dam.mindspeak.firebase

import android.util.Log
import androidx.compose.ui.graphics.Color
import cat.dam.mindspeak.model.AssignedUser
import cat.dam.mindspeak.model.EmotionItem
import cat.dam.mindspeak.model.EmotionRecord
import cat.dam.mindspeak.model.UserData
import cat.dam.mindspeak.model.UserRelation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Date

class FirebaseManager {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    companion object {
        fun logoutUser() {
            FirebaseAuth.getInstance().signOut()
        }
    }
    suspend fun updateProfileImage(profileImageUrl: String) {
        try {
            val userId = auth.currentUser?.uid ?: throw Exception("Usuario no autenticado")

            db.collection("Persona")
                .document(userId)
                .update("profileImage", profileImageUrl)
                .await()
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Error al actualizar imagen de perfil", e)
            throw e
        }
    }
    suspend fun getProfileImage(): String {
        val userId = auth.currentUser?.uid ?: return ""
        val docRef = db.collection("Persona").document(userId)
        val snapshot = docRef.get().await()
        return snapshot.getString("profileImage") ?: ""
    }

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
            val userId =
                authResult.user?.uid ?: throw Exception("No s'ha pogut obtenir l'ID de l'usuari")

            // Afegir informació a Firestore
            val personaData = mutableMapOf<String, Any?>(
                "email" to email,
                "nom" to nom,
                "cognom" to cognom,
                "rol" to rol,
                "telefon" to telefon
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

    suspend fun obtenirEmocions(): List<EmotionItem> {
        Log.d("FirebaseManager", "Début de la récupération des émotions")
        try {
            val snapshot = db.collection("Emocio").get().await()

            return snapshot.documents.map { document ->
                // Convertir la couleur en format Color
                val colorHex = document.getString("color") ?: "FFFFFFFF" // Sans le préfixe "0x"
                val color = Color(android.graphics.Color.parseColor("#$colorHex"))

                EmotionItem(
                    text = document.getString("emocio_nom") ?: "",
                    color = color,
                    imageUrl = document.getString("imagen") ?: ""

                )
            }
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Error fetching emotions", e)
            throw e
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

    suspend fun obtenirDadesUsuari(): UserData? {
        return try {
            val currentUser = Firebase.auth.currentUser
            val document = Firebase.firestore.collection("Persona")
                .document(currentUser?.uid ?: "")
                .get()
                .await()

            if (document.exists()) {
                UserData(
                    nom = document.getString("nom"),
                    cognom = document.getString("cognom"),
                    email = currentUser?.email,
                    telefon = document.getString("telefon"),
                    rol = document.getString("rol") ?: "Usuari"
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }


    private suspend fun updateUserField(field: String, value: String) {
        try {
            val userId = auth.currentUser?.uid ?: throw Exception("Usuario no autenticado")

            db.collection("Persona")
                .document(userId)
                .update(field, value)
                .await()
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Error al actualizar campo $field", e)
            throw e
        }
    }

    // Añade esta función para llamadas desde composables
    fun updateUserFieldFromComposable(
        field: String,
        value: String,
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                updateUserField(field, value)
                withContext(Dispatchers.Main) {
                    onSuccess()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError(e)
                }
            }
        }
    }

    // SupervisorManagement
    suspend fun getUsersByRole(role: String): List<UserRelation> {
        try {
            val snapshot = db.collection("Persona")
                .whereEqualTo("rol", role)
                .get()
                .await()

            return snapshot.documents.map { document ->
                UserRelation(
                    userId = document.id,
                    nom = document.getString("nom") ?: "",
                    cognom = document.getString("cognom") ?: "",
                    email = document.getString("email") ?: "",
                    telefon = document.getString("telefon"),
                    supervisor = document.getString("supervisor"),
                    professor = document.getString("professor"),
                    familiar = document.getString("familiar")
                )
            }
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Error fetching users by role", e)
            return emptyList()
        }
    }


    suspend fun updateUserInformation(user: UserRelation) {
        try {
            // Find the user document by email
            val querySnapshot = db.collection("Persona")
                .whereEqualTo("email", user.email)
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                val documentId = querySnapshot.documents[0].id

                // Prepare update map
                val updateData = mapOf(
                    "nom" to user.nom,
                    "cognom" to user.cognom,
                    "telefon" to user.telefon
                )

                // Update the document
                db.collection("Persona")
                    .document(documentId)
                    .update(updateData)
                    .await()
            } else {
                throw Exception("User not found")
            }
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Error updating user", e)
            throw e
        }
    }

    suspend fun deleteUser(email: String) {
        try {
            // Find the user document by email
            val querySnapshot = db.collection("Persona")
                .whereEqualTo("email", email)
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                val documentId = querySnapshot.documents[0].id

                // Delete the document
                db.collection("Persona")
                    .document(documentId)
                    .delete()
                    .await()
            } else {
                throw Exception("User not found")
            }
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Error deleting user", e)
            throw e
        }
    }

    // Assignar users a supervisor
    suspend fun getUsersWithoutSupervisor(): List<UserRelation> {
        try {
            val snapshot = db.collection("Persona")
                .whereEqualTo("rol", "Usuari")
                .get()
                .await()

            Log.d("FirebaseManager", "Total documents retrieved: ${snapshot.documents.size}")

            val users = snapshot.documents.map { document ->
                val userData = UserRelation(
                    userId = document.id,
                    nom = document.getString("nom") ?: "",
                    cognom = document.getString("cognom") ?: "",
                    email = document.getString("email") ?: "",
                    telefon = document.getString("telefon"),
                    supervisor = document.getString("supervisor"),
                    professor = document.getString("professor"),
                    familiar = document.getString("familiar")
                )

                // Log details of each user
                Log.d(
                    "FirebaseManager", "User details: " +
                            "ID: ${userData.userId}, " +
                            "Name: ${userData.nom} ${userData.cognom}, " +
                            "Email: ${userData.email}, " +
                            "Supervisor: ${userData.supervisor}"
                )

                userData
            }.filter { user ->
                // Explicitly log why a user is considered available
                val isAvailable = user.supervisor.isNullOrBlank()
                Log.d("FirebaseManager", "User ${user.email} is available: $isAvailable")
                isAvailable
            }

            Log.d("FirebaseManager", "Number of available users: ${users.size}")
            return users
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Error retrieving users without supervisor", e)
            return emptyList()
        }
    }

    suspend fun getUsersBySupervisor(supervisorId: String): List<UserRelation> {
        try {
            val snapshot = db.collection("Persona")
                .whereEqualTo("rol", "Usuari")
                .whereEqualTo("supervisor", supervisorId)
                .get()
                .await()

            return snapshot.documents.map { document ->
                UserRelation(
                    userId = document.id,
                    nom = document.getString("nom") ?: "",
                    cognom = document.getString("cognom") ?: "",
                    email = document.getString("email") ?: "",
                    telefon = document.getString("telefon"),
                    supervisor = document.getString("supervisor"),
                    professor = document.getString("professor"),
                    familiar = document.getString("familiar")
                )
            }
        } catch (e: Exception) {
            Log.e(
                "FirebaseManager",
                "Erreur lors de la récupération des utilisateurs du superviseur",
                e
            )
            return emptyList()
        }
    }

    suspend fun assignUserToSupervisor(userId: String, supervisorId: String) {
        try {
            Log.d(
                "FirebaseManager",
                "Attempting to assign user $userId to supervisor $supervisorId"
            )

            val currentUserRole = obtenirRolUsuari()
            if (currentUserRole != "Supervisor") {
                Log.e("FirebaseManager", "Assignment failed: Current user is not a supervisor")
                throw Exception("Seuls les superviseurs peuvent assigner des utilisateurs")
            }

            // Vérifier que l'utilisateur n'a pas déjà un superviseur
            val userDoc = db.collection("Persona").document(userId).get().await()
            val existingSupervisor = userDoc.getString("supervisor")

            if (existingSupervisor != null) {
                Log.e("FirebaseManager", "Assignment failed: User already has a supervisor")
                throw Exception("L'usuario te un supervisor")
            }

            // Mettre à jour le document de l'utilisateur
            db.collection("Persona")
                .document(userId)
                .update("supervisor", supervisorId)
                .await()

            Log.d(
                "FirebaseManager",
                "User $userId successfully assigned to supervisor $supervisorId"
            )
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Error in assignUserToSupervisor", e)
            throw e
        }
    }

    suspend fun removeUserAssignment(userId: String, supervisorId: String) {
        try {
            // Vérifier que le superviseur correspond
            val userDoc = db.collection("Persona").document(userId).get().await()
            val currentSupervisor = userDoc.getString("supervisor")

            if (currentSupervisor != supervisorId) {
                throw Exception("Le superviseur ne correspond pas")
            }

            // Supprimer l'assignation du superviseur
            db.collection("Persona")
                .document(userId)
                .update("supervisor", null)
                .await()
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Erreur lors de la suppression de l'assignation", e)
            throw e
        }
    }

    // Méthodes similaires à implémenter pour professor et familiar
    suspend fun assignUserToProfessor(userEmail: String, professorId: String) {
        try {
            // Trouver l'ID de l'utilisateur par email
            val userQuery = db.collection("Persona")
                .whereEqualTo("email", userEmail)
                .get()
                .await()

            if (userQuery.documents.isEmpty()) throw Exception("User not found")
            val userId = userQuery.documents[0].id

            // Assigner le professeur
            db.collection("Persona").document(userId)
                .update("professor", professorId)
                .await()
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Erreur d'assignation", e)
            throw e
        }
    }

    suspend fun assignUserToFamiliar(userEmail: String, familiarId: String) {
        try {
            // Trouver l'ID de l'utilisateur par email
            val userQuery = db.collection("Persona")
                .whereEqualTo("email", userEmail)
                .get()
                .await()

            if (userQuery.documents.isEmpty()) throw Exception("User not found")
            val userId = userQuery.documents[0].id

            // Assigner le professeur
            db.collection("Persona").document(userId)
                .update("familiar", familiarId)
                .await()
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Erreur d'assignation", e)
            throw e
        }
    }


    // New method to get assigned users
    suspend fun getAssignedUsers(): List<AssignedUser> {
        try {
            val currentUserId = auth.currentUser?.uid ?: return emptyList()

            // Check current user's role
            val userDoc = db.collection("Persona").document(currentUserId).get().await()
            val currentUserRole = userDoc.getString("rol")

            val query = when (currentUserRole) {
                "Supervisor" -> db.collection("Persona")
                    .whereEqualTo("supervisor", currentUserId)
                "Professor" -> db.collection("Persona")
                    .whereEqualTo("professor", currentUserId)
                "Familiar" -> db.collection("Persona")
                    .whereEqualTo("familiar", currentUserId)
                else -> return emptyList()
            }

            val snapshot = query.get().await()

            return snapshot.documents.map { document ->
                AssignedUser(
                    userId = document.id,
                    nom = document.getString("nom") ?: "",
                    cognom = document.getString("cognom") ?: "",
                    email = document.getString("email") ?: ""
                )
            }
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Error getting assigned users", e)
            return emptyList()
        }
    }
    // New method to get emotions for a specific user
    suspend fun getEmotionsForUser(userId: String): List<EmotionRecord> {
        try {
            val snapshot = db.collection("RegistroEmotions")
                .whereEqualTo("userId", userId)
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()

            return snapshot.documents.map { document ->
                EmotionRecord(
                    id = document.id,
                    emotionType = document.getString("emotionType") ?: "",
                    rating = document.getLong("rating")?.toInt() ?: 0,
                    date = document.getTimestamp("date")?.toDate() ?: Date(),
                    userId = userId,
                    comentari = document.getString("comentari") ?: "",
                    fotoUri = document.getString("fotoUri")
                )
            }
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Error getting user emotions", e)
            return emptyList()
        }
    }

}
    /*
suspend fun assignUserToProfessor(userId: String, professorId: String) {
    try {
        val userDoc = db.collection("Persona").document(userId).get().await()
        val existingProfessor = userDoc.getString("professor")

        if (existingProfessor != null) {
            throw Exception("L'usuario te un professor")
        }

        db.collection("Persona")
            .document(userId)
            .update("professor", professorId)
            .await()
    } catch (e: Exception) {
        Log.e("FirebaseManager", "Erreur lors de l'assignation du professeur", e)
        throw e
    }
}

suspend fun assignUserToFamiliar(userId: String, familiarId: String) {
    try {
        val userDoc = db.collection("Persona").document(userId).get().await()
        val existingFamiliar = userDoc.getString("familiar")

        if (existingFamiliar != null) {
            throw Exception("L'usuario te un familier")
        }

        db.collection("Persona")
            .document(userId)
            .update("familiar", familiarId)
            .await()
    } catch (e: Exception) {
        Log.e("FirebaseManager", "Erreur lors de l'assignation du familier", e)
        throw e
    }
}

 */

