package cat.dam.mindspeak.ui.screens.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import cat.dam.mindspeak.R
import cat.dam.mindspeak.firebase.FirebaseManager
import cat.dam.mindspeak.ui.theme.CustomColors

@Composable
fun SettingsUser(localCustomColors: ProvidableCompositionLocal<CustomColors>, navController: NavController) {
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
                                .clickable { /* Aquí puedes agregar lógica para editar la foto de perfil */ },
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
                    SettingItem("Nombre", "Nombre del usuario", localCustomColors)
                    Spacer(modifier = Modifier.height(5.dp))
                    SettingItem("Apellido", "Apellido del usuario", localCustomColors)
                    Spacer(modifier = Modifier.height(5.dp))
                    SettingItem("Correo Electrónico", "Correo del usuario", localCustomColors)
                    Spacer(modifier = Modifier.height(5.dp))
                    SettingItem("Fecha Nacimiento", "22/01/2002", localCustomColors)

                    Spacer(modifier = Modifier.height(20.dp))

                    // Botón de Logout
                    Button(
                        onClick = {
                            FirebaseManager.logoutUser()  // Cerrar sesión en Firebase
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
    txt1: String,
    txt2: String,
    localCustomColors: ProvidableCompositionLocal<CustomColors>
) {
    val isEditing = remember { mutableStateOf(false) }
    val editableText = remember { mutableStateOf(txt2) }

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 15.dp)) {
        Text(
            txt1,
            style = TextStyle(
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = localCustomColors.current.text1
            )
        )

        if (isEditing.value) {
            OutlinedTextField(
                value = editableText.value,
                onValueChange = { editableText.value = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            )
        } else {
            Text(
                editableText.value,
                style = TextStyle(
                    color = localCustomColors.current.text1,
                    fontSize = 25.sp
                ),
                modifier = Modifier.padding(top = 10.dp)
            )
        }

        Image(
            painter = painterResource(id = R.drawable.lapiz_icon),
            contentDescription = null,
            colorFilter = ColorFilter.tint(localCustomColors.current.text1),
            modifier = Modifier
                .size(30.dp)
                .align(Alignment.End)
                .clickable {
                    isEditing.value = !isEditing.value
                },
            contentScale = ContentScale.Fit
        )
    }
}