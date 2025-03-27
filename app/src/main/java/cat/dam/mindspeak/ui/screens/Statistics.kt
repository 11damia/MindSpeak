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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
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
                title = {
                    Text(
                        text = "Estadísticas de Emociones",
                        color = LocalCustomColors.current.secondary,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = LocalCustomColors.current.background,
                    titleContentColor = LocalCustomColors.current.text1
                ),
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .background(LocalCustomColors.current.background)
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(LocalCustomColors.current.background)
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp) // Añadir padding horizontal a toda la columna
            ) {
                UserSelector(
                    users = assignedUsers,
                    selectedUser = selectedUser,
                    onUserSelected = { viewModel.selectUser(it) },
                    isLoading = isLoading,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (selectedUser != null) {
                    // Gráfico de barras
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 200.dp) // Garantiza que el gráfico no se distorsione
                    ) {
                        SimpleEmotionBarChart(emotionRecords)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Registros recientes
                    RecentEmotionRecords(emotionRecords.take(5))

                } else {
                    // Mensaje de selección de usuario
                    Text(
                        text = "Selecciona un usuario para ver sus estadísticas",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = LocalCustomColors.current.text1,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // Mostrar mensaje de error si existe
                errorMessage?.let { message ->
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEmotionStatsScreen() {
    EmotionStatsScreen(viewModel = EmotionStatsViewModel())
}
