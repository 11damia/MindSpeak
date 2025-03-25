package cat.dam.mindspeak.ui.screens.supervisor




import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import cat.dam.mindspeak.model.UserRelation
import cat.dam.mindspeak.model.UserRelationViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupervisorUserAssignmentScreen(
    navController: NavHostController,
    userRelationViewModel: UserRelationViewModel,
    currentSupervisorId: String
) {
    val coroutineScope = rememberCoroutineScope()
    val availableUsers by userRelationViewModel.availableUsers.collectAsState(initial = emptyList())
    val assignedUsers by userRelationViewModel.assignedUsers.collectAsState(initial = emptyList())


    // Debugging: Log the number of users when they change
    LaunchedEffect(availableUsers) {
        println("Available users in UI: ${availableUsers.size}")
    }
    LaunchedEffect(assignedUsers) {
        println("Assigned users in UI: ${assignedUsers.size}")
    }

    // Debugging print
    LaunchedEffect(currentSupervisorId) {
        println("Current Supervisor ID: $currentSupervisorId")

        if (currentSupervisorId.isNotBlank()) {
            try {
                userRelationViewModel.loadAvailableUsers()
                userRelationViewModel.loadAssignedUsers(currentSupervisorId)
            } catch (e: Exception) {
                println("Error loading users: ${e.message}")
                e.printStackTrace()
            }
        } else {
            println("Supervisor ID is blank")
        }
    }
/*
    // Carregar usuaris en inicialitzar-se la pantalla
    LaunchedEffect(currentSupervisorId) {
        if (currentSupervisorId.isNotBlank()) {
            userRelationViewModel.loadAvailableUsers()
            userRelationViewModel.loadAssignedUsers(currentSupervisorId)
        }
    }

 */

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Assignació d'Usuaris") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Secció d'usuaris disponibles
            Text(
                text = "Usuaris Disponibles (${availableUsers.size})",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp)
            )

            // LazyColumn per usuaris disponibles
            if (availableUsers.isEmpty()) {
                Text(
                    text = "No hi ha usuaris disponibles",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                ) {
                    items(availableUsers) { user ->
                        UserAssignmentItem(
                            user = user,
                            isAssigned = false,
                            onAssign = {
                                coroutineScope.launch {
                                    userRelationViewModel.assignUserToSupervisor(user.userId, currentSupervisorId)
                                }
                            }
                        )
                    }
                }
            }

            // Secció d'usuaris assignats
            Text(
                text = "Usuaris Assignats (${assignedUsers.size})",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp)
            )

            // LazyColumn per usuaris assignats
            if (assignedUsers.isEmpty()) {
                Text(
                    text = "No hi ha usuaris assignats",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                ) {
                    items(assignedUsers) { user ->
                        UserAssignmentItem(
                            user = user,
                            isAssigned = true,
                            onRemove = {
                                coroutineScope.launch {
                                    userRelationViewModel.removeUserAssignment(user.userId, currentSupervisorId)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UserAssignmentItem(
    user: UserRelation,
    isAssigned: Boolean,
    onAssign: (() -> Unit)? = null,
    onRemove: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "${user.nom} ${user.cognom}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (!isAssigned && onAssign != null) {
                IconButton(onClick = onAssign) {
                    Icon(Icons.Default.Add, contentDescription = "Assignar usuari")
                }
            }

            if (isAssigned && onRemove != null) {
                IconButton(onClick = onRemove) {
                    Icon(Icons.Default.Remove, contentDescription = "Retirar assignació")
                }
            }
        }
    }
}

