package cat.dam.mindspeak.ui.screens.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import cat.dam.mindspeak.R
import cat.dam.mindspeak.ui.theme.CustomColors

@Composable
fun SettingsUser(localCustomColors: ProvidableCompositionLocal<CustomColors>) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            item {
                // Fila para el lápiz de editar foto de perfil
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp) // Mismo padding horizontal que los settingItem
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Imagen de lápiz de editar foto de perfil
                        Image(
                            painter = painterResource(id = R.drawable.lapiz_icon),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(localCustomColors.current.text1),
                            modifier = Modifier
                                .size(30.dp)
                                .align(Alignment.TopEnd) // Alinea el lápiz a la derecha
                                .clickable { /* Aquí puedes agregar lógica para editar la foto de perfil */ },
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }
            item {
                // Fila para la imagen de perfil
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Imagen de foto de perfil
                    Image(
                        painter = painterResource(id = R.drawable.user_icon),
                        contentDescription = null,
                        modifier = Modifier
                            .size(150.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }
            item {

                // Columnas para los elementos de configuración
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp).zIndex(1f)
                ) // Mismo padding horizontal
                {
                    Spacer(modifier = Modifier.height(40.dp))
                    SettingItem("Nombre", "Nombre del usuario", localCustomColors)
                    Spacer(modifier = Modifier.height(5.dp))
                    SettingItem("Apellido", "Apellido del usuario", localCustomColors)
                    Spacer(modifier = Modifier.height(5.dp))
                    SettingItem("Correo Electrónico", "Correo del usuario", localCustomColors)
                    Spacer(modifier = Modifier.height(5.dp))
                    SettingItem("Fecha Nacimiento", "22/01/2002", localCustomColors)
                }
                Image(
                    painter = painterResource(id = R.drawable.persona_settings_user),
                    contentDescription = null,
                    modifier = Modifier
                        .zIndex(0f)
                        .size(150.dp)
                        .align(Alignment.BottomEnd), // Alinea la imagen en la esquina inferior derecha
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
    // Estado para controlar si el texto es editable
    val isEditing = remember { mutableStateOf(false) }
    // Estado para almacenar el texto editable
    val editableText = remember { mutableStateOf(txt2) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 15.dp), // Solo padding vertical
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    txt1,
                    style = TextStyle(
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        color = localCustomColors.current.text1
                    )
                )

                if (isEditing.value) {
                    // Campo de texto editable
                    OutlinedTextField(
                        value = editableText.value,
                        onValueChange = { editableText.value = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                    )
                } else {
                    // Texto no editable
                    Text(
                        editableText.value,
                        style = TextStyle(
                            color = localCustomColors.current.text1,
                            fontSize = 25.sp
                        ),
                        modifier = Modifier.padding(top = 10.dp)
                    )
                }
            }

            // Imagen del lápiz alineada a la derecha
            Image(
                painter = painterResource(id = R.drawable.lapiz_icon),
                contentDescription = null,
                colorFilter = ColorFilter.tint(localCustomColors.current.text1),
                modifier = Modifier
                    .size(30.dp)
                    .align(Alignment.TopEnd) // Alinea la imagen a la derecha
                    .clickable {
                        isEditing.value = !isEditing.value
                    }, // Cambia el estado de edición
                contentScale = ContentScale.Fit
            )
        }
    }
}