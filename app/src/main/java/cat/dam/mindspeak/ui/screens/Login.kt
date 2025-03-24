package cat.dam.mindspeak.ui.screens

import android.content.Context
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import cat.dam.mindspeak.R
import cat.dam.mindspeak.firebase.FirebaseManager
import cat.dam.mindspeak.firebase.Prefs
import cat.dam.mindspeak.model.UserViewModel
import cat.dam.mindspeak.ui.theme.LocalCustomColors
import cat.dam.mindspeak.ui.theme.White
import kotlinx.coroutines.launch


@Composable
fun Login(navController: NavHostController, userViewModel: UserViewModel, context: Context) {
    val firebaseManager = FirebaseManager()
    var email by remember { mutableStateOf("") }
    var contrasenya by remember { mutableStateOf("") }
    var recordarMe by remember { mutableStateOf(false) }

    // Inicializar SharedPreferences
    val prefs = remember { Prefs(context) }

    // Cargar el correo electrónico y la opción "Recordar usuario" al iniciar la pantalla
    LaunchedEffect(Unit) {
        email = prefs.getEmail() ?: ""
        contrasenya = prefs.getPassword() ?: ""
        recordarMe = prefs.getRememberMe()
    }
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
                text = stringResource(R.string.login),
                color = LocalCustomColors.current.text1,
                fontSize = 24.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }

        item {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = {
                    Text(
                        stringResource(R.string.mail_user),
                        color = LocalCustomColors.current.text1
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            OutlinedTextField(
                value = contrasenya,
                onValueChange = { contrasenya = it },
                label = {
                    Text(
                        stringResource(R.string.password),
                        color = LocalCustomColors.current.text1
                    )
                },
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
                    text = stringResource(R.string.remember),

                    color = LocalCustomColors.current.secondary,
                    modifier = Modifier.padding(start = 8.dp)
                )
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

                    coroutineScope.launch {
                        try {
                            firebaseManager.iniciarSessio(
                                email = email,
                                contrasenya = contrasenya,
                                onSuccess = {
                                    if (recordarMe) {
                                        prefs.saveEmail(email)
                                        prefs.savePassword(contrasenya)
                                        prefs.saveRememberMe(true)
                                    } else {
                                        prefs.clear()
                                    }

                                    // Obtener todos los datos del usuario
                                    coroutineScope.launch {
                                        val userDetails =
                                            firebaseManager.obtenirDadesUsuari() // Asume que tienes esta función

                                        // Actualizar ViewModel con todos los datos
                                        userViewModel.updateUserData(
                                            email = email,
                                            nom = userDetails?.nom,
                                            cognom = userDetails?.cognom,
                                            telefon = userDetails?.telefon,
                                            rol = userDetails?.rol ?: "Usuari"
                                        )

                                        // Navegar según el rol
                                        when (userDetails?.rol) {
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
                }
            ) {
                Text(text = stringResource(R.string.login), color = White)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}