package cat.dam.mindspeak.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.dam.mindspeak.firebase.FirebaseManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class UserData(
    val nom: String? = null,
    val cognom: String? = null,
    val email: String? = null,
    val telefon: String? = null,
    val rol: String? = null,
    val professor: String? = null,
    val familiar: String? = null,
    val profileImage: String? = null // Agregamos la imagen de perfil
)

class UserViewModel : ViewModel() {
    private val firebaseManager = FirebaseManager()
    private val _userData = MutableStateFlow(UserData())
    val userData: StateFlow<UserData> = _userData.asStateFlow()

    // Actualizar los datos del usuario, incluyendo la imagen de perfil
    fun updateUserData(
        nom: String? = null,
        cognom: String? = null,
        email: String? = null,
        telefon: String? = null,
        rol: String? = null,
        profileImage: String? = null
    ) {
        viewModelScope.launch {
            _userData.value = UserData(
                nom = nom ?: _userData.value.nom,
                cognom = cognom ?: _userData.value.cognom,
                email = email ?: _userData.value.email,
                telefon = telefon ?: _userData.value.telefon,
                rol = rol ?: _userData.value.rol,
                profileImage = profileImage ?: _userData.value.profileImage // Persistir imagen
            )
        }
    }

    fun loadProfileImage() {
        viewModelScope.launch {
            val userData = firebaseManager.obtenirDadesUsuari()
            userData?.let {
                _userData.value = _userData.value.copy(
                    profileImage = it.profileImage
                )
            }
        }
    }

    fun updateProfileImage(profileImageUrl: String) {
        viewModelScope.launch {
            try {
                firebaseManager.updateProfileImage(profileImageUrl)
                _userData.value = _userData.value.copy(profileImage = profileImageUrl)
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error al actualizar la imagen de perfil", e)
            }
        }
    }

    // Obtener los datos completos del usuario desde Firebase
    fun fetchCurrentUserData() {
        viewModelScope.launch {
            val userData = firebaseManager.obtenirDadesUsuari()
            userData?.let {
                _userData.value = UserData(
                    nom = it.nom,
                    cognom = it.cognom,
                    email = it.email,
                    telefon = it.telefon,
                    rol = it.rol,
                    profileImage = it.profileImage // Asegúrate de incluir la imagen aquí
                )
            }
        }
    }

    // Método para asignar un profesor al usuario
    fun assignProfessor(professorId: String) {
        viewModelScope.launch {
            val currentEmail = _userData.value.email
            if (currentEmail != null) {
                firebaseManager.assignUserToProfessor(currentEmail, professorId)
                fetchCurrentUserData() // Actualizar los datos
            }
        }
    }

    // Método para asignar un familiar al usuario
    fun assignFamiliar(familiarId: String) {
        viewModelScope.launch {
            val currentEmail = _userData.value.email
            if (currentEmail != null) {
                firebaseManager.assignUserToFamiliar(currentEmail, familiarId)
                _userData.value = _userData.value.copy(familiar = familiarId)
            }
        }
    }

    // Método para eliminar la asignación de un profesor
    fun removeProfessorAssignment() {
        viewModelScope.launch {
            val currentEmail = _userData.value.email
            val currentProfessor = _userData.value.professor
            if (currentEmail != null && currentProfessor != null) {
                firebaseManager.removeUserAssignment(currentEmail, currentProfessor)
                _userData.value = _userData.value.copy(professor = null)
            }
        }
    }

    // Método para eliminar la asignación de un familiar
    fun removeFamiliarAssignment() {
        viewModelScope.launch {
            val currentEmail = _userData.value.email
            val currentFamiliar = _userData.value.familiar
            if (currentEmail != null && currentFamiliar != null) {
                firebaseManager.removeUserAssignment(currentEmail, currentFamiliar)
                _userData.value = _userData.value.copy(familiar = null)
            }
        }
    }

    // Limpiar los datos del usuario
    fun clearUserData() {
        viewModelScope.launch {
            _userData.value = UserData()
        }
    }

    // Obtener el ID del usuario actual
    fun getCurrentUserId(): String {
        return firebaseManager.auth.currentUser?.uid ?: ""
    }

    // Obtener el rol del usuario actual
    suspend fun getCurrentUserRole(): String? {
        return firebaseManager.obtenirRolUsuari()
    }
}
