package cat.dam.mindspeak.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.dam.mindspeak.firebase.EmotionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EmotionViewModel : ViewModel() {
    private val repository = EmotionRepository()

    private val _emotionRecords = MutableStateFlow<List<EmotionRecord>>(emptyList())
    val emotionRecords: StateFlow<List<EmotionRecord>> = _emotionRecords

    init {
        fetchEmotionRecords()
    }

    fun addEmotionRecord(record: EmotionRecord) {
        viewModelScope.launch {
            try {
                repository.addEmotionRecord(record)
                fetchEmotionRecords()
            } catch (e: Exception) {
                // Handle error (you might want to add error state to ViewModel)
                println("Error adding emotion record: ${e.message}")
            }
        }
    }

    private fun fetchEmotionRecords() {
        viewModelScope.launch {
            val records = repository.getEmotionRecords()
            _emotionRecords.value = records
        }
    }
}