import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.dam.mindspeak.firebase.FirebaseManager
import cat.dam.mindspeak.model.AssignedUser
import cat.dam.mindspeak.model.EmotionRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*

class EmotionStatsViewModel(
    private val firebaseManager: FirebaseManager = FirebaseManager()
) : ViewModel() {

    // Estados para la UI
    private val _assignedUsers = MutableStateFlow<List<AssignedUser>>(emptyList())
    val assignedUsers: StateFlow<List<AssignedUser>> = _assignedUsers.asStateFlow()

    private val _selectedUser = MutableStateFlow<AssignedUser?>(null)
    val selectedUser: StateFlow<AssignedUser?> = _selectedUser.asStateFlow()

    private val _emotionRecords = MutableStateFlow<List<EmotionRecord>>(emptyList())
    val emotionRecords: StateFlow<List<EmotionRecord>> = _emotionRecords.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _emotionStats = MutableStateFlow<EmotionStatistics>(EmotionStatistics())
    val emotionStats: StateFlow<EmotionStatistics> = _emotionStats.asStateFlow()

    private val _currentTimeFilter = MutableStateFlow(TimeFilter.WEEK)
    val currentTimeFilter: StateFlow<TimeFilter> = _currentTimeFilter.asStateFlow()

    private val _selectedDate = MutableStateFlow<Calendar?>(null)
    val selectedDate: StateFlow<Calendar?> = _selectedDate.asStateFlow()

    private val _selectedWeek = MutableStateFlow<Int?>(null)
    val selectedWeek: StateFlow<Int?> = _selectedWeek.asStateFlow()

    private val _selectedMonth = MutableStateFlow<Int?>(null)
    val selectedMonth: StateFlow<Int?> = _selectedMonth.asStateFlow()

    private val _selectedYear = MutableStateFlow<Int>(Calendar.getInstance().get(Calendar.YEAR))
    val selectedYear: StateFlow<Int> = _selectedYear.asStateFlow()

    // Funciones para actualizar la selección
    fun selectDate(date: Calendar) {
        _selectedDate.value = date
        _currentTimeFilter.value = TimeFilter.DAY
        updateStatistics()
    }

    fun selectWeek(week: Int) {
        _selectedWeek.value = week
        _currentTimeFilter.value = TimeFilter.WEEK
        updateStatistics()
    }

    fun selectMonth(month: Int) {
        _selectedMonth.value = month
        _currentTimeFilter.value = TimeFilter.MONTH
        updateStatistics()
    }

    fun selectYear(year: Int) {
        _selectedYear.value = year
        updateStatistics()
    }

    init {
        loadAssignedUsers()
    }

    fun loadAssignedUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _assignedUsers.value = firebaseManager.getAssignedUsers()
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar usuarios: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectUser(user: AssignedUser) {
        _selectedUser.value = user
        loadUserEmotions(user.userId)
    }

    fun loadUserEmotions(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _emotionRecords.value = firebaseManager.getEmotionsForUser(userId)
                updateStatistics()
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar emociones: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setTimeFilter(filter: TimeFilter) {
        _currentTimeFilter.value = filter
        updateStatistics()
    }

    private fun updateStatistics() {
        val records = getFilteredRecords()
        _emotionStats.value = if (records.isEmpty()) {
            EmotionStatistics()
        } else {
            EmotionStatistics(
                totalRecords = records.size,
                averageRating = calculateAverageRating(records),
                emotionDistribution = calculateEmotionDistribution(records),
                lastWeekCount = calculateLastWeekRecords(records),
                mostCommonEmotion = calculateMostCommonEmotion(records),
                ratingTrend = calculateRatingTrend(records)
            )
        }
    }

    private fun getFilteredRecords(): List<EmotionRecord> {
        return when (_currentTimeFilter.value) {
            TimeFilter.DAY -> getRecordsForSelectedDay()
            TimeFilter.WEEK -> getRecordsForSelectedWeek()
            TimeFilter.MONTH -> getRecordsForSelectedMonth()
            TimeFilter.YEAR -> getRecordsForSelectedYear()
        }
    }
    private fun getRecordsForSelectedDay(): List<EmotionRecord> {
        val selected = _selectedDate.value ?: return emptyList()
        return _emotionRecords.value.filter { record ->
            val cal = Calendar.getInstance().apply { time = record.date }
            cal.get(Calendar.DAY_OF_YEAR) == selected.get(Calendar.DAY_OF_YEAR) &&
                    cal.get(Calendar.YEAR) == selected.get(Calendar.YEAR)
        }
    }

    private fun getRecordsForSelectedWeek(): List<EmotionRecord> {
        val week = _selectedWeek.value ?: return emptyList()
        val year = _selectedYear.value
        return _emotionRecords.value.filter { record ->
            val cal = Calendar.getInstance().apply { time = record.date }
            cal.get(Calendar.WEEK_OF_YEAR) == week &&
                    cal.get(Calendar.YEAR) == year
        }
    }
    private fun getRecordsForSelectedMonth(): List<EmotionRecord> {
        val month = _selectedMonth.value ?: return emptyList()
        val year = _selectedYear.value
        return _emotionRecords.value.filter { record ->
            val cal = Calendar.getInstance().apply { time = record.date }
            cal.get(Calendar.MONTH) + 1 == month && // +1 porque Calendar.MONTH es 0-based
                    cal.get(Calendar.YEAR) == year
        }
    }

    private fun getRecordsForSelectedYear(): List<EmotionRecord> {
        val year = _selectedYear.value
        return _emotionRecords.value.filter { record ->
            val cal = Calendar.getInstance().apply { time = record.date }
            cal.get(Calendar.YEAR) == year
        }
    }

    private fun calculateAverageRating(records: List<EmotionRecord>): Double {
        return if (records.isNotEmpty()) records.map { it.rating }.average() else 0.0
    }

    private fun calculateEmotionDistribution(records: List<EmotionRecord>): Map<String, Float> {
        val total = records.size.toFloat()
        return if (total > 0) {
            records.groupBy { it.emotionType }
                .mapValues { (_, group) -> group.size.toFloat() / total }
        } else {
            emptyMap()
        }
    }

    private fun calculateLastWeekRecords(records: List<EmotionRecord>): Int {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.WEEK_OF_YEAR, -1)
        return records.count { it.date.after(calendar.time) }
    }

    private fun calculateMostCommonEmotion(records: List<EmotionRecord>): String {
        return records.groupBy { it.emotionType }
            .maxByOrNull { it.value.size }
            ?.key ?: ""
    }

    private fun calculateRatingTrend(records: List<EmotionRecord>): List<Float> {
        return records.sortedBy { it.date }
            .takeLast(7)
            .map { it.rating.toFloat() }
    }
}

enum class TimeFilter {
    DAY, WEEK, MONTH, YEAR
}

data class EmotionStatistics(
    val totalRecords: Int = 0,
    val averageRating: Double = 0.0,
    val emotionDistribution: Map<String, Float> = emptyMap(),
    val lastWeekCount: Int = 0,
    val mostCommonEmotion: String = "",
    val ratingTrend: List<Float> = emptyList()
)
data class EmotionEntry(
    val x: Float,
    val y: Float,
    val emotion: String
)
