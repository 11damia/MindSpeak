package cat.dam.mindspeak.ui.screens.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import cat.dam.mindspeak.R
import cat.dam.mindspeak.firebase.FirebaseManager
import cat.dam.mindspeak.model.UserViewModel
import cat.dam.mindspeak.ui.theme.CustomColors

@Composable
fun SettingsUser(
    localCustomColors: ProvidableCompositionLocal<CustomColors>,
    navController: NavController,
    userViewModel: UserViewModel
) {
    val userData by userViewModel.userData.collectAsState()
    val firebaseManager = remember { FirebaseManager() }
    var showUpdateSuccess by remember { mutableStateOf(false) }

    if (showUpdateSuccess) {
        AlertDialog(
            onDismissRequest = { showUpdateSuccess = false },
            title = { Text("Actualización exitosa") },
            text = { Text("Los datos se han actualizado correctamente") },
            confirmButton = {
                Button(onClick = { showUpdateSuccess = false }) {
                    Text("OK")
                }
            }
        )
    }
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                // Fila para el lápiz de editar foto de perfil
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Image(
                            painter = painterResource(id = R.drawable.lapiz_icon),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(localCustomColors.current.text1),
                            modifier = Modifier
                                .size(30.dp)
                                .align(Alignment.TopEnd)
                                .clickable { /* Lógica para editar foto de perfil */ },
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }
            item {
                // Fila para la imagen de perfil
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.user_icon),
                        contentDescription = null,
                        modifier = Modifier.size(150.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }
            item {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .zIndex(1f)
                ) {
                    Spacer(modifier = Modifier.height(40.dp))

                    SettingItem(
                        label = "Nombre",
                        initialValue = userData.nom ?: "",
                        localCustomColors = localCustomColors,
                        onUpdate = { newValue ->
                            userViewModel.updateUserData(nom = newValue)
                            firebaseManager.updateUserFieldFromComposable("nom", newValue)
                            showUpdateSuccess = true
                        }
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    SettingItem(
                        label = "Apellido",
                        initialValue = userData.cognom ?: "",
                        localCustomColors = localCustomColors,
                        onUpdate = { newValue ->
                            userViewModel.updateUserData(cognom = newValue)
                            firebaseManager.updateUserFieldFromComposable("cognom", newValue)
                            showUpdateSuccess = true
                        }
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 15.dp)) {
                        Text(
                            "Correo Electrónico",
                            style = TextStyle(
                                fontSize = 25.sp,
                                fontWeight = FontWeight.Bold,
                                color = localCustomColors.current.text1
                            )
                        )

                        Text(
                            userData.email ?: "No disponible",
                            style = TextStyle(
                                color = localCustomColors.current.text1,
                                fontSize = 25.sp
                            ),
                            modifier = Modifier.padding(top = 10.dp)
                        )

                        Text(
                            "* Para cambiar el correo, contacta con soporte",
                            style = TextStyle(
                                color = localCustomColors.current.text2,
                                fontSize = 14.sp,
                                fontStyle = FontStyle.Italic
                            ),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(5.dp))

                    SettingItem(
                        label = "Teléfono",
                        initialValue = userData.telefon ?: "",
                        localCustomColors = localCustomColors,
                        onUpdate = { newValue ->
                            userViewModel.updateUserData(telefon = newValue)
                            firebaseManager.updateUserFieldFromComposable("telefon", newValue)
                            showUpdateSuccess = true
                        }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Botón de Logout
                    Button(
                        onClick = {
                            FirebaseManager.logoutUser()
                            userViewModel.clearUserData() // Limpiar los datos del usuario
                            navController.navigate("login") {
                                popUpTo("user") { inclusive = true }
                                launchSingleTop = true
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp)
                    ) {
                        Text(stringResource(R.string.logout))
                    }
                }
                Image(
                    painter = painterResource(id = R.drawable.persona_settings_user),
                    contentDescription = null,
                    modifier = Modifier
                        .zIndex(0f)
                        .size(150.dp)
                        .align(Alignment.BottomEnd),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
fun SettingItem(
    label: String,
    initialValue: String,
    localCustomColors: ProvidableCompositionLocal<CustomColors>,
    onUpdate: (String) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var currentValue by remember { mutableStateOf(initialValue) }

    LaunchedEffect(initialValue) {
        currentValue = initialValue
    }

    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 15.dp)) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                label,
                style = TextStyle(
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = localCustomColors.current.text1
                )
            )

            if (isEditing) {
                OutlinedTextField(
                    value = currentValue,
                    onValueChange = { currentValue = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                )
            } else {
                Text(
                    currentValue.ifEmpty { "No disponible" },
                    style = TextStyle(
                        color = localCustomColors.current.text1,
                        fontSize = 25.sp
                    ),
                    modifier = Modifier.padding(top = 10.dp)
                )
            }
        }

        // Mover los botones de edición al mismo nivel que el texto
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(y = 10.dp), // Ajuste fino para alinear verticalmente
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (isEditing) {
                // Botón de confirmar (usando el mismo ícono de lápiz pero rotado)
                IconButton(
                    onClick = {
                        if (currentValue != initialValue) {
                            onUpdate(currentValue)
                        }
                        isEditing = false
                    }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.lapiz_icon),
                        contentDescription = "Confirmar",
                        colorFilter = ColorFilter.tint(Color.Green),
                        modifier = Modifier
                            .size(24.dp)
                            .rotate(45f)
                    )
                }
                // Botón de cancelar (usando el mismo ícono pero con X)
                IconButton(
                    onClick = {
                        currentValue = initialValue
                        isEditing = false
                    }
                ) {
                    Box(modifier = Modifier.size(24.dp)) {
                        Image(
                            painter = painterResource(id = R.drawable.lapiz_icon),
                            contentDescription = "Cancelar",
                            colorFilter = ColorFilter.tint(Color.Red),
                            modifier = Modifier.size(12.dp)
                        )
                        Image(
                            painter = painterResource(id = R.drawable.lapiz_icon),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(Color.Red),
                            modifier = Modifier
                                .size(12.dp)
                                .rotate(90f)
                        )
                    }
                }
            } else {
                IconButton(
                    onClick = { isEditing = true }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.lapiz_icon),
                        contentDescription = "Editar",
                        colorFilter = ColorFilter.tint(localCustomColors.current.text1),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}