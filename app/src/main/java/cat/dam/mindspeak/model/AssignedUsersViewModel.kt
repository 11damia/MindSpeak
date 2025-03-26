package cat.dam.mindspeak.model
/*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.dam.mindspeak.firebase.FirebaseManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AssignedUsersViewModel : ViewModel() {
    private val firebaseManager = FirebaseManager()

    private val _assignedUsers = MutableStateFlow<List<AssignedUser>>(emptyList())
    val assignedUsers: StateFlow<List<AssignedUser>> = _assignedUsers.asStateFlow()

    private val _selectedUserEmotions = MutableStateFlow<List<EmotionRecord>>(emptyList())
    val selectedUserEmotions: StateFlow<List<EmotionRecord>> = _selectedUserEmotions.asStateFlow()

    init {
        fetchAssignedUsers()
    }

    fun fetchAssignedUsers() {
        viewModelScope.launch {
            _assignedUsers.value = firebaseManager.getAssignedUsers()
        }
    }

    fun fetchEmotionsForUser(userId: String) {
        viewModelScope.launch {
            _selectedUserEmotions.value = firebaseManager.getEmotionsForUser(userId)
        }
    }
}

 */