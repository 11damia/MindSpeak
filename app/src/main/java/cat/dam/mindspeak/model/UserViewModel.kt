package cat.dam.mindspeak.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

data class UserData(
    val nom: String? = null,
    val cognom: String? = null,
    val email: String? = null,
    val telefon: String? = null,
    val rol: String? = null
)

class UserViewModel : ViewModel() {
    internal var userData by mutableStateOf(UserData())

    private var isLoggedIn: Boolean by mutableStateOf(false)

    fun updateUserData(
        nom: String? = null,
        cognom: String? = null,
        email: String? = null,
        telefon: String? = null,
        rol: String? = null
    ) {
        userData = UserData(
            nom = nom ?: userData.nom,
            cognom = cognom ?: userData.cognom,
            email = email ?: userData.email,
            telefon = telefon ?: userData.telefon,
            rol = rol ?: userData.rol
        )
        isLoggedIn = true
    }

    fun clearUserData() {
        userData = UserData()
        isLoggedIn = false
    }
}