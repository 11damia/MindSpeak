package cat.dam.mindspeak.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class UserViewModel : ViewModel() {
    var userRole: String? by mutableStateOf(null)
        private set

    fun updateUserRole(role: String) { // Cambia el nombre de la función
        userRole = role
    }
}