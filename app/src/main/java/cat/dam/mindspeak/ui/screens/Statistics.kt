package cat.dam.mindspeak.ui.screens

import EmotionStatsViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cat.dam.mindspeak.ui.components.EmotionStatsOverview
import cat.dam.mindspeak.ui.components.EmotionTimelineChart
import cat.dam.mindspeak.ui.components.RecentEmotionRecords
import cat.dam.mindspeak.ui.components.TimeFilterSelector
import cat.dam.mindspeak.ui.components.UserSelector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmotionStatsScreen(
    viewModel: EmotionStatsViewModel = viewModel()
) {
    val assignedUsers by viewModel.assignedUsers.collectAsState()
    val selectedUser by viewModel.selectedUser.collectAsState()
    val emotionRecords by viewModel.emotionRecords.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val stats by viewModel.emotionStats.collectAsState()
    val timeFilter by viewModel.currentTimeFilter.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Estadísticas de Emociones") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors()
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            UserSelector(
                users = assignedUsers,
                selectedUser = selectedUser,
                onUserSelected = { viewModel.selectUser(it) },
                isLoading = isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedUser != null) {
                EmotionStatsOverview(stats)

                Spacer(modifier = Modifier.height(16.dp))

                TimeFilterSelector(
                    currentFilter = timeFilter,
                    onFilterSelected = { viewModel.setTimeFilter(it) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                EmotionTimelineChart(
                    records = emotionRecords,
                    timeFilter = timeFilter,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                RecentEmotionRecords(emotionRecords.take(5))
            } else {
                Text(
                    text = "Selecciona un usuario para ver sus estadísticas",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            errorMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}