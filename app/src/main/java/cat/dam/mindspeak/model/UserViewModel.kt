package cat.dam.mindspeak.model


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
    val familiar: String? = null
)

class UserViewModel : ViewModel() {
    private val firebaseManager = FirebaseManager()
    private val _userData = MutableStateFlow(UserData())
    val userData: StateFlow<UserData> = _userData.asStateFlow()

    fun updateUserData(
        nom: String? = null,
        cognom: String? = null,
        email: String? = null,
        telefon: String? = null,
        rol: String? = null,
        professor: String? = null,
        familiar: String? = null
    ) {
        viewModelScope.launch {
            _userData.value = UserData(
                nom = nom ?: _userData.value.nom,
                cognom = cognom ?: _userData.value.cognom,
                email = email ?: _userData.value.email,
                telefon = telefon ?: _userData.value.telefon,
                rol = rol ?: _userData.value.rol,
                professor = professor ?: _userData.value.professor,
                familiar = familiar ?: _userData.value.familiar
            )
        }
    }

    // New function to assign professor
    fun assignProfessor(professorId: String) {
        viewModelScope.launch {
            val currentEmail = _userData.value.email
            if (currentEmail != null) {
                firebaseManager.assignUserToProfessor(currentEmail, professorId)
                // Actualiser les données
                fetchCurrentUserData()
            }
        }
    }

    // New function to assign familiar
    fun assignFamiliar(familiarId: String) {
        viewModelScope.launch {
            val currentEmail = _userData.value.email
            if (currentEmail != null) {
                firebaseManager.assignUserToFamiliar(currentEmail, familiarId)
                _userData.value = _userData.value.copy(familiar = familiarId)
            }
        }
    }

    // New function to remove professor assignment
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

    // New function to remove familiar assignment
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

    fun clearUserData() {
        viewModelScope.launch {
            _userData.value = UserData()
        }
    }

    fun getCurrentUserId(): String {
        return firebaseManager.auth.currentUser?.uid ?: ""
    }

    suspend fun getCurrentUserRole(): String? {
        return firebaseManager.obtenirRolUsuari()
    }

    // New method to fetch and populate full user data
    fun fetchCurrentUserData() {
        viewModelScope.launch {
            val userData = firebaseManager.obtenirDadesUsuari()
            userData?.let {
                _userData.value = UserData(
                    nom = it.nom,
                    cognom = it.cognom,
                    email = it.email,
                    telefon = it.telefon,
                    rol = it.rol
                )
            }
        }
    }
}
/*
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
    val rol: String? = null
)

class UserViewModel : ViewModel() {
    private val firebaseManager = FirebaseManager()
    private val _userData = MutableStateFlow(UserData())
    val userData: StateFlow<UserData> = _userData.asStateFlow()

    fun updateUserData(
        nom: String? = null,
        cognom: String? = null,
        email: String? = null,
        telefon: String? = null,
        rol: String? = null
    ) {
        viewModelScope.launch {
            _userData.value = UserData(
                nom = nom ?: _userData.value.nom,
                cognom = cognom ?: _userData.value.cognom,
                email = email ?: _userData.value.email,
                telefon = telefon ?: _userData.value.telefon,
                rol = rol ?: _userData.value.rol
            )
        }
    }

    fun clearUserData() {
        viewModelScope.launch {
            _userData.value = UserData()
        }
    }

    fun getCurrentUserId(): String {
        return firebaseManager.auth.currentUser?.uid ?: ""
    }

    suspend fun getCurrentUserRole(): String? {
        return firebaseManager.obtenirRolUsuari()
    }
}

 */