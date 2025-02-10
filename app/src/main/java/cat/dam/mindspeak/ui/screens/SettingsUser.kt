package cat.dam.mindspeak

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cat.dam.mindspeak.ui.theme.CustomColors

@Composable
fun SettingsUser(localCustomColors: ProvidableCompositionLocal<CustomColors>) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 80.dp, bottom = 25.dp) // Padding superior e inferior
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
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
                            .align(Alignment.TopEnd), // Alinea el lápiz a la derecha
                        contentScale = ContentScale.Fit
                    )
                }
            }

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

            // Columnas para los elementos de configuración
            Column(
                modifier = Modifier.padding(horizontal = 20.dp) // Mismo padding horizontal
            ) {
                Spacer(modifier = Modifier.height(40.dp))
                settingItem("Nombre", "Nombre del usuario",localCustomColors)
                Spacer(modifier = Modifier.height(5.dp))
                settingItem("Apellido", "Apellido del usuario",localCustomColors)
                Spacer(modifier = Modifier.height(5.dp))
                settingItem("Correo Electrónico", "Correo del usuario",localCustomColors)
                Spacer(modifier = Modifier.height(5.dp))
                settingItem("Fecha Nacimiento", "22/01/2002",localCustomColors)
            }
        }

        // Imagen superpuesta (persona_settings_user)
        Image(
            painter = painterResource(id = R.drawable.persona_settings_user),
            contentDescription = null,
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.BottomEnd) // Alinea la imagen en la esquina inferior derecha
                .offset(x = (-20).dp, y = (-20).dp), // Ajusta la posición para que no quede pegada al borde
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun settingItem(
    txt1: String,
    txt2: String,
    localCustomColors: ProvidableCompositionLocal<CustomColors>
) {
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
                    style = TextStyle(fontSize = 25.sp, fontWeight = FontWeight.Bold, color = localCustomColors.current.text1)
                )
                Text(
                    txt2,
                    style = TextStyle(color = localCustomColors.current.text1, fontSize = 25.sp),
                    modifier = Modifier.padding(top = 10.dp)
                )
            }
            // Imagen del lápiz alineada a la derecha
            Image(
                painter = painterResource(id = R.drawable.lapiz_icon),
                contentDescription = null,
                colorFilter = ColorFilter.tint(localCustomColors.current.text1),
                modifier = Modifier
                    .size(30.dp)
                    .align(Alignment.TopEnd), // Alinea la imagen a la derecha
                contentScale = ContentScale.Fit
            )
        }
    }
}