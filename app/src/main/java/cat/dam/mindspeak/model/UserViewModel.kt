package cat.dam.mindspeak.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
}