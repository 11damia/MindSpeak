package cat.dam.mindspeak.ui.screens.supervisor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import cat.dam.mindspeak.firebase.FirebaseManager
import cat.dam.mindspeak.model.UserRelation
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupervisorManagementScreen(
    navController: NavHostController,
    firebaseManager: FirebaseManager
) {
    var users by remember { mutableStateOf<List<UserRelation>>(emptyList()) }
    var professors by remember { mutableStateOf<List<UserRelation>>(emptyList()) }
    var familiars by remember { mutableStateOf<List<UserRelation>>(emptyList()) }
    var selectedUser by remember { mutableStateOf<UserRelation?>(null) }
    var isAddUserDialogOpen by remember { mutableStateOf(false) }
    var isEditUserDialogOpen by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Récupérer l'ID du superviseur connecté
    var currentSupervisorId by remember { mutableStateOf<String?>(null) }

    // Use LaunchedEffect to safely call suspend functions
    LaunchedEffect(Unit) {
        // Récupérer l'ID du superviseur
        currentSupervisorId = firebaseManager.auth.currentUser?.uid

        // Charger les utilisateurs assignés au superviseur connecté
        currentSupervisorId?.let { supervisorId ->
            users = firebaseManager.getUsersBySupervisor(supervisorId)
        }

        professors = firebaseManager.getUsersByRole("Professor")
        familiars = firebaseManager.getUsersByRole("Familiar")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestió d'Usuaris Assignats") }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(users) { user ->
                UserListItem(
                    user = user,
                    onEdit = {
                        selectedUser = user
                        isEditUserDialogOpen = true
                    },
                    onDelete = {
                        coroutineScope.launch {
                            currentSupervisorId?.let { supervisorId ->
                                firebaseManager.removeUserAssignment(user.userId, supervisorId)
                                // Recharger la liste des utilisateurs après suppression
                                users = firebaseManager.getUsersBySupervisor(supervisorId)
                            }
                        }
                    }
                )
            }
        }

        if (isAddUserDialogOpen) {
            AddEditUserDialog(
                isEditing = false,
                professors = professors,
                familiars = familiars,
                onDismiss = { isAddUserDialogOpen = false },
                onSave = { newUser, selectedProfessor, selectedFamiliar ->
                    coroutineScope.launch {
                        firebaseManager.registrarUsuari(
                            email = newUser.email,
                            contrasenya = "defaultPassword",
                            nom = newUser.nom,
                            cognom = newUser.cognom,
                            telefon = newUser.telefon,
                            dataNaixement = null,
                            sexe = null,
                            grau = null,
                            rol = "Usuari",
                            onSuccess = {
                                coroutineScope.launch {
                                    users = firebaseManager.getUsersByRole("Usuari")
                                }

                                // Assignació del professor si n'hi ha
                                selectedProfessor?.let {
                                    coroutineScope.launch {
                                        firebaseManager.assignUserToProfessor(newUser.email, it.userId)
                                    }
                                }

                                // Assignació del familiar si n'hi ha
                                selectedFamiliar?.let {
                                    coroutineScope.launch {
                                        firebaseManager.assignUserToFamiliar(newUser.email, it.userId)
                                    }
                                }

                                isAddUserDialogOpen = false
                            },
                            onFailure = { /* Handle error */ }
                        )
                    }
                }
            )
        }

        if (isEditUserDialogOpen && selectedUser != null) {
            AddEditUserDialog(
                isEditing = true,
                initialUser = selectedUser,
                professors = professors,
                familiars = familiars,
                onDismiss = { isEditUserDialogOpen = false },
                onSave = { updatedUser, selectedProfessor, selectedFamiliar ->
                    coroutineScope.launch {
                        // Update user information
                        firebaseManager.updateUserInformation(updatedUser)

                        // Assign professor if selected
                        selectedProfessor?.let {
                            firebaseManager.assignUserToProfessor(updatedUser.email, it.userId)
                        }

                        // Assign familiar if selected
                        selectedFamiliar?.let {
                            firebaseManager.assignUserToFamiliar(updatedUser.email, it.userId)
                        }

                        // Refresh users list
                        currentSupervisorId?.let { supervisorId ->
                            users = firebaseManager.getUsersBySupervisor(supervisorId)
                        }

                        isEditUserDialogOpen = false
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditUserDialog(
    isEditing: Boolean,
    initialUser: UserRelation? = null,
    professors: List<UserRelation> = emptyList(),
    familiars: List<UserRelation> = emptyList(),
    onDismiss: () -> Unit,
    onSave: (UserRelation, UserRelation?, UserRelation?) -> Unit
) {
    var nom by remember { mutableStateOf(initialUser?.nom ?: "") }
    var cognom by remember { mutableStateOf(initialUser?.cognom ?: "") }
    var email by remember { mutableStateOf(initialUser?.email ?: "") }
    var telefon by remember { mutableStateOf(initialUser?.telefon ?: "") }
    var expandedProfessor by remember { mutableStateOf(false) }
    var selectedProfessor by remember {
        mutableStateOf(
            professors.find { it.userId == initialUser?.professor }
        )
    }
    var expandedFamiliar by remember { mutableStateOf(false) }
    var selectedFamiliar by remember {
        mutableStateOf(
            familiars.find { it.userId == initialUser?.familiar }
        )
    }

    // Basic validation
    val isValidEmail = email.matches(Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"))
    val isValidPhone = telefon.isEmpty() || telefon.matches(Regex("\\+?\\d{9,}"))
    val isFormValid = nom.isNotBlank() &&
            cognom.isNotBlank() &&
            isValidEmail &&
            isValidPhone

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEditing) "Editar Usuari" else "Afegir Usuari") },
        text = {
            Column {
                TextField(
                    value = nom,
                    onValueChange = { nom = it },
                    label = { Text("Nom") },
                    isError = nom.isBlank()
                )
                if (nom.isBlank()) {
                    Text("Nom és obligatori", color = Color.Red)
                }

                TextField(
                    value = cognom,
                    onValueChange = { cognom = it },
                    label = { Text("Cognom") },
                    isError = cognom.isBlank()
                )
                if (cognom.isBlank()) {
                    Text("Cognom és obligatori", color = Color.Red)
                }

                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    isError = !isValidEmail
                )
                if (!isValidEmail) {
                    Text("Format d'email no vàlid", color = Color.Red)
                }

                TextField(
                    value = telefon,
                    onValueChange = { telefon = it },
                    label = { Text("Telèfon") },
                    isError = !isValidPhone
                )
                if (telefon.isNotEmpty() && !isValidPhone) {
                    Text("Format de telèfon no vàlid", color = Color.Red)
                }

                // Professor dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedProfessor,
                    onExpandedChange = { expandedProfessor = !expandedProfessor }
                ) {
                    TextField(
                        value = selectedProfessor?.let { "${it.nom} ${it.cognom}" } ?: "Seleccionar Professor",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedProfessor)
                        },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedProfessor,
                        onDismissRequest = { expandedProfessor = false }
                    ) {
                        professors.forEach { professor ->
                            DropdownMenuItem(
                                text = { Text("${professor.nom} ${professor.cognom}") },
                                onClick = {
                                    selectedProfessor = professor
                                    expandedProfessor = false
                                }
                            )
                        }
                    }
                }

                // Familiar dropdown (similar to professor dropdown)
                ExposedDropdownMenuBox(
                    expanded = expandedFamiliar,
                    onExpandedChange = { expandedFamiliar = !expandedFamiliar }
                ) {
                    TextField(
                        value = selectedFamiliar?.let { "${it.nom} ${it.cognom}" } ?: "Seleccionar Familiar",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedFamiliar)
                        },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedFamiliar,
                        onDismissRequest = { expandedFamiliar = false }
                    ) {
                        familiars.forEach { familiar ->
                            DropdownMenuItem(
                                text = { Text("${familiar.nom} ${familiar.cognom}") },
                                onClick = {
                                    selectedFamiliar = familiar
                                    expandedFamiliar = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val user = UserRelation(
                        userId = initialUser?.userId ?: "",
                        nom = nom,
                        cognom = cognom,
                        email = email,
                        telefon = telefon,
                        supervisor = initialUser?.supervisor,
                        professor = selectedProfessor?.userId,
                        familiar = selectedFamiliar?.userId
                    )
                    onSave(user, selectedProfessor, selectedFamiliar)
                },
                enabled = isFormValid
            ) {
                Text(if (isEditing) "Actualitzar" else "Afegir")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel·lar")
            }
        }
    )
}

@Composable
fun UserListItem(
    user: UserRelation,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("${user.nom} ${user.cognom}")
                Text(user.email)
                Text(user.telefon ?: "")
            }
            Row {
                IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, "Editar") }
                IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, "Eliminar") }
            }
        }
    }
}