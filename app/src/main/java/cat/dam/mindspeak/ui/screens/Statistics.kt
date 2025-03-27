package cat.dam.mindspeak.ui.screens

import EmotionStatsViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cat.dam.mindspeak.ui.theme.LocalCustomColors

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

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Estadísticas de Emociones") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors()
            )
        },
        containerColor = LocalCustomColors.current.background

    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(LocalCustomColors.current.background)
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
                // Añadimos el gráfico de barras aquí
                SimpleEmotionBarChart(emotionRecords)

                Spacer(modifier = Modifier.height(16.dp))

                RecentEmotionRecords(emotionRecords.take(5))
            } else {
                Text(
                    text = "Selecciona un usuario para ver sus estadísticas",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = LocalCustomColors.current.text1,
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