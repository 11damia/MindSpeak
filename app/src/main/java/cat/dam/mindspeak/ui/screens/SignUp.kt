package cat.dam.mindspeak.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import cat.dam.mindspeak.firebase.FirebaseManager
import cat.dam.mindspeak.ui.theme.DarkGray
import cat.dam.mindspeak.ui.theme.LocalCustomColors
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUp(navController: NavHostController) {
    val firebaseManager = FirebaseManager()
    var email by remember { mutableStateOf("") }
    var contrasenya by remember { mutableStateOf("") }
    var confirmarContrasenya by remember { mutableStateOf("") }
    var nom by remember { mutableStateOf("") }
    var cognom by remember { mutableStateOf("") }
    var telefon by remember { mutableStateOf("") }
    var dataNaixement by remember { mutableStateOf("") }
    var sexe by remember { mutableStateOf("") }
    var grau by remember { mutableStateOf("") }
    var isExpanded by remember { mutableStateOf(false) }
    var rolSeleccionat by remember { mutableStateOf("") }
    val opcions = listOf("Supervisor", "Familiar", "Professor", "Usuari")

    // Créer un scope de coroutine
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
                text = "Crear compte",
                color = LocalCustomColors.current.text1,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 24.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }

        item {
            OutlinedTextField(
                value = nom,
                onValueChange = { nom = it },
                label = { Text("Nom", color = LocalCustomColors.current.text1) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            OutlinedTextField(
                value = cognom,
                onValueChange = { cognom = it },
                label = { Text("Cognoms", color = LocalCustomColors.current.text1) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            OutlinedTextField(
                value = telefon,
                onValueChange = { telefon = it },
                label = { Text("Telèfon", color = LocalCustomColors.current.text1) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correu electrònic", color = LocalCustomColors.current.text1) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
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
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            OutlinedTextField(
                value = confirmarContrasenya,
                onValueChange = { confirmarContrasenya = it },
                label = { Text("Confirmar contrasenya", color = LocalCustomColors.current.text1) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            ExposedDropdownMenuBox(
                expanded = isExpanded,
                onExpandedChange = { isExpanded = it }
            ) {
                OutlinedTextField(
                    value = rolSeleccionat,
                    onValueChange = { },
                    label = { Text("Tipus d'usuari") },
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = isExpanded,
                    onDismissRequest = { isExpanded = false }
                ) {
                    opcions.forEach { opcio ->
                        DropdownMenuItem(
                            text = { Text(opcio) },
                            onClick = {
                                rolSeleccionat = opcio
                                isExpanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Button(
                onClick = {
                    if (contrasenya != confirmarContrasenya) {
                        println("Les contrasenyes no coincideixen.")
                        return@Button
                    }

                    if (nom.isEmpty() || cognom.isEmpty() || email.isEmpty() || contrasenya.isEmpty() || rolSeleccionat.isEmpty()) {
                        println("Tots els camps són obligatoris.")
                        return@Button
                    }

                    // Lancer une coroutine pour appeler les fonctions suspendues
                    coroutineScope.launch {
                        try {
                            firebaseManager.registrarUsuari(
                                email = email,
                                contrasenya = contrasenya,
                                nom = nom,
                                cognom = cognom,
                                telefon = telefon,
                                dataNaixement = dataNaixement.toLongOrNull(),
                                sexe = sexe,
                                grau = grau,
                                rol = rolSeleccionat,
                                onSuccess = { navController.navigate("homesupervis") },
                                onFailure = { error -> println(error) }
                            )
                        } catch (e: Exception) {
                            println("Error durant el registre: ${e.message}")
                        }
                    }
                },
                modifier = Modifier
                    .width(300.dp)
                    .height(50.dp)
            ) {
                Text("Crear compte")
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}