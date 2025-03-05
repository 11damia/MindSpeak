package cat.dam.mindspeak.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

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


}