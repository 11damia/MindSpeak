package cat.dam.mindspeak.ui.screens.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import cat.dam.mindspeak.R
import cat.dam.mindspeak.firebase.FirebaseManager
import cat.dam.mindspeak.model.UserViewModel
import cat.dam.mindspeak.ui.theme.LocalCustomColors
import cat.dam.mindspeak.ui.theme.White
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Locale

data class SupervisorResource(
    val id: String = "",
    val title: String = "",
    val type: String = "",
    val uri: String = "",
    val timestamp: com.google.firebase.Timestamp? = null
)

@Composable
fun ExerciceList(navController: NavHostController, userViewModel: UserViewModel) {
    val userData by userViewModel.userData.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val firebaseManager = FirebaseManager()

    var supervisorResources by remember { mutableStateOf<List<SupervisorResource>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(userData) {
        coroutineScope.launch {
            try {
                val currentUserId = firebaseManager.auth.currentUser?.uid ?: run {
                    println("DEBUG: Aucun utilisateur connecté")
                    return@launch
                }

                println("DEBUG: Recherche de ressources pour l'userId: $currentUserId")

                val resources = firebaseManager.db.collection("Recurs")
                    .whereEqualTo("userId", currentUserId)
                    .get()
                    .await()
                    .documents
                    .map { doc ->
                        SupervisorResource(
                            id = doc.id,
                            title = doc.getString("title") ?: "Ressource sense títol",
                            type = doc.getString("type") ?: "",
                            uri = doc.getString("uri") ?: "",
                            timestamp = doc.getTimestamp("timestamp")
                        )
                    }
                    .sortedByDescending { it.timestamp }

                supervisorResources = resources
                isLoading = false
            } catch (e: Exception) {
                println("DEBUG: Erreur lors de la récupération des ressources")
                e.printStackTrace()
                errorMessage = "Error en carregar recursos: ${e.localizedMessage}"
                isLoading = false
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 20.dp, end = 20.dp)
    ) {
        item {
            Text(
                text = stringResource(R.string.welcome2, userData.nom ?: "Usuari"),
                fontWeight = FontWeight.Bold,
                color = LocalCustomColors.current.text1,
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Afficher un message de chargement
        if (isLoading) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }

        // Afficher un message d'erreur si nécessaire
        if (errorMessage != null) {
            item {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }
        }

        // Afficher les ressources du superviseur
        if (!isLoading && supervisorResources.isNotEmpty()) {
            items(supervisorResources) { resource ->
                SupervisorResourceItem(
                    resource = resource,
                    onResourceClick = {
                        // Navigation vers l'écran de visualisation de la ressource
                        navController.navigate("resource_view/${resource.id}")
                    }
                )
            }
        }

        // Message si aucune ressource n'est disponible
        if (!isLoading && supervisorResources.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_resource_dispo),
                        color = LocalCustomColors.current.text2,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SupervisorResourceItem(
    resource: SupervisorResource,
    onResourceClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
            .background(LocalCustomColors.current.third),
    ) {
        Column(
            modifier = Modifier.padding(top = 16.dp, end = 16.dp, start = 16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = resource.title,
                fontWeight = FontWeight.Bold,
                color = LocalCustomColors.current.text2,
                fontSize = 30.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            val resourceLabel = when (resource.type.lowercase()) {
                "image" -> stringResource(R.string.resource_image)
                "audio" -> stringResource(R.string.resource_audio)
                "video" -> stringResource(R.string.resource_video)
                else -> resource.type
            }

            Text(
                text = resourceLabel,
                color = LocalCustomColors.current.text2,
                fontWeight = FontWeight.Normal,
                fontSize = 17.sp
            )

            Text(
                text = stringResource(R.string.resource_type, resourceLabel.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.ROOT
                    ) else it.toString()
                }),
                color = LocalCustomColors.current.text2,
                fontWeight = FontWeight.Normal,
                fontSize = 17.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = onResourceClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LocalCustomColors.current.secondary
                )
            ) {
                Text(
                    text = stringResource(R.string.watch_res),
                    fontSize = 15.sp,
                    color = White
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}