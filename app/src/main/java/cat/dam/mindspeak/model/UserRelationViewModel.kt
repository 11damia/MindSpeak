package cat.dam.mindspeak.model

import androidx.annotation.OptIn
import androidx.lifecycle.ViewModel
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import cat.dam.mindspeak.firebase.FirebaseManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Model de dades extès per gestionar relacions entre usuaris
data class UserRelation(
    val userId: String,
    val nom: String,
    val cognom: String,
    val email: String,
    val telefon: String?,
    val supervisor: String? = null,
    val professor: String? = null,
    val familiar: String? = null
)

// Extensió de ViewModel per gestionar les relacions d'usuaris
class UserRelationViewModel : ViewModel() {
    private val firebaseManager = FirebaseManager()

    private val _availableUsers = MutableStateFlow<List<UserRelation>>(emptyList())
    val availableUsers: StateFlow<List<UserRelation>> = _availableUsers.asStateFlow()

    private val _assignedUsers = MutableStateFlow<List<UserRelation>>(emptyList())
    val assignedUsers: StateFlow<List<UserRelation>> = _assignedUsers.asStateFlow()

    @OptIn(UnstableApi::class)
    suspend fun loadAvailableUsers() {
        try {
            val users = firebaseManager.getUsersWithoutSupervisor()
            Log.d("UserRelationViewModel", "Nombre d'usuaris disponibles: ${users.size}")

            // Imprimir detalls de cada usuari amb més context
            users.forEach { user ->
                Log.d("UserRelationViewModel", "Detalls de l'usuari - " +
                        "ID: ${user.userId}, " +
                        "Nom complet: ${user.nom} ${user.cognom}, " +
                        "Correu electrònic: ${user.email}, " +
                        "Supervisor actual: ${user.supervisor ?: "Cap"}")
            }

            // Actualitzar el StateFlow amb els usuaris
            _availableUsers.value = users
        } catch (e: Exception) {
            Log.e("UserRelationViewModel", "Error en carregar usuaris disponibles", e)
            // Establir una llista buida en cas d'error
            _availableUsers.value = emptyList()
        }
    }

    // Carregar els usuaris assignats a aquest supervisor
    @OptIn(UnstableApi::class)
    suspend fun loadAssignedUsers(supervisorId: String) {
        try {
            val users = firebaseManager.getUsersBySupervisor(supervisorId)
            Log.d("UserRelationViewModel", "Nombre d'usuaris assignats: ${users.size}")

            // Imprimir detalls dels usuaris assignats
            users.forEach { user ->
                Log.d("UserRelationViewModel", "Detalls de l'usuari assignat - " +
                        "ID: ${user.userId}, " +
                        "Nom complet: ${user.nom} ${user.cognom}, " +
                        "Correu electrònic: ${user.email}")
            }

            _assignedUsers.value = users
        } catch (e: Exception) {
            Log.e("UserRelationViewModel", "Error en carregar els usuaris assignats", e)
            // Establir una llista buida en cas d'error
            _assignedUsers.value = emptyList()
        }
    }

    // Assignar un usuari a un supervisor
    @OptIn(UnstableApi::class)
    suspend fun assignUserToSupervisor(userId: String, supervisorId: String) {
        try {
            firebaseManager.assignUserToSupervisor(userId, supervisorId)
            // Actualitzar les llistes
            loadAvailableUsers()
            loadAssignedUsers(supervisorId)
        } catch (e: Exception) {
            Log.e("UserRelationViewModel", "Error en assignar l'usuari", e)
        }
    }

    // Eliminar l'assignació d'un usuari
    @OptIn(UnstableApi::class)
    suspend fun removeUserAssignment(userId: String, supervisorId: String) {
        try {
            firebaseManager.removeUserAssignment(userId, supervisorId)
            // Actualitzar les llistes
            loadAvailableUsers()
            loadAssignedUsers(supervisorId)
        } catch (e: Exception) {
            Log.e("UserRelationViewModel", "Error en eliminar l'assignació", e)
        }
    }
}

