package cat.dam.mindspeak.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date

data class EmotionRecord(
    val emotionType: String,
    val rating: Int,
    val date: Date
)

class EmotionViewModel : ViewModel() {
    private val _emotionRecords = MutableStateFlow<List<EmotionRecord>>(emptyList())
    val emotionRecords: StateFlow<List<EmotionRecord>> get() = _emotionRecords

    fun addEmotionRecord(record: EmotionRecord) {
        viewModelScope.launch {
            _emotionRecords.value = _emotionRecords.value + record
        }
    }
}
