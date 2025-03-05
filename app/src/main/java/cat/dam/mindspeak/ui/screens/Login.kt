package cat.dam.mindspeak.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import cat.dam.mindspeak.firebase.FirebaseManager
import cat.dam.mindspeak.model.UserViewModel
import cat.dam.mindspeak.ui.theme.LocalCustomColors
import kotlinx.coroutines.launch


@Composable
fun Login(navController: NavHostController,userViewModel:UserViewModel) {
    val firebaseManager = FirebaseManager()
    var email by remember { mutableStateOf("") }
    var contrasenya by remember { mutableStateOf("") }
    var recordarMe by remember { mutableStateOf(false) }

    // Créer un scope de coroutine lié au cycle de vie du composant
    val coroutineScope = rememberCoroutineScope()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = "Iniciar sessió",
                color = LocalCustomColors.current.text1,
                fontSize = 24.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }

        item {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correu electrònic", color = LocalCustomColors.current.text1) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            OutlinedTextField(
                value = contrasenya,
                onValueChange = { contrasenya = it },
                label = { Text("Contrasenya", color = LocalCustomColors.current.text1) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = recordarMe,
                    onCheckedChange = { recordarMe = it }
                )
                Text(
                    text = "Recorda'm",
                    color = LocalCustomColors.current.secondary,
                    modifier = Modifier.padding(start = 8.dp)
                )
                TextButton(
                    onClick = { /* Gestionar l'olvid de contrasenya */ }
                ) {
                    Text(
                        text = "Has oblidat la contrasenya?",
                        color = LocalCustomColors.current.secondary
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            Button(
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LocalCustomColors.current.secondary
                ),
                onClick = {
                    if (email.isEmpty() || contrasenya.isEmpty()) {
                        println("Ompli tots els camps.")
                        return@Button
                    }

                    // Lancer une coroutine pour gérer toutes les opérations suspendues
                    coroutineScope.launch {
                        try {
                            // Appel de la fonction suspendue iniciarSessio
                            firebaseManager.iniciarSessio(
                                email = email,
                                contrasenya = contrasenya,
                                onSuccess = {
                                    // Obtenir le rôle de l'utilisateur après la connexion réussie
                                    coroutineScope.launch {
                                        val rol = firebaseManager.obtenirRolUsuari()
                                        userViewModel.updateUserRole(rol?:"Usuari")
                                        when (rol) {
                                            "Supervisor" -> navController.navigate("homesupervis")
                                            "Familiar" -> navController.navigate("homefamiliar")
                                            "Professor" -> navController.navigate("homeprofessor")
                                            "Usuari" -> navController.navigate("homeuser")
                                            else -> println("Rol no vàlid")
                                        }
                                    }
                                },
                                onFailure = { error ->
                                    println("Error durant l'inici de sessió: $error")
                                }
                            )
                        } catch (e: Exception) {
                            println("Error general: ${e.message}")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Iniciar sessió")
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}