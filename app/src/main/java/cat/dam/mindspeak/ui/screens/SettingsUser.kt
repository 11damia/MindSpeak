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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Preview(showBackground = true)
@Composable
fun SettingsUser(){
    //Variable de ancho de la pantalla
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    Column (
        modifier = Modifier
            .fillMaxSize()
    ){
        Row (
            modifier = Modifier
                .fillMaxWidth()
        ){
            //Imagen de lapiz de editar foto perfil
            Image(
                painter = painterResource(id = R.drawable.lapiz_icon),
                contentDescription = null,
                modifier = Modifier
                    .offset(x = screenWidth * 0.85f)
                    .size(30.dp),
                contentScale = ContentScale.Fit
            )
        }
        Row (
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ){
            //Imagen de foto de perfil
            Image(
                painter = painterResource(id = R.drawable.user_icon),
                contentDescription = null,
                modifier = Modifier
                    .size(150.dp),
                contentScale = ContentScale.Fit
            )
        }

        Column {
            settingItem("Nombre", "Nombre del usuario")
            Spacer(modifier = Modifier.height(5.dp))
            settingItem("Apellido", "Apellido del usuario")
            Spacer(modifier = Modifier.height(5.dp))
            settingItem("Correo Electrónico", "Correo del usuario")
            Spacer(modifier = Modifier.height(5.dp))
            settingItem("Fecha Nacimiento", "22/01/2002")
        }
    }
}

//Clase para cada item
@Composable
fun settingItem(
    txt1 : String,
    txt2 : String
){
    //Variable de ancho de la pantalla
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp),
    ){
        Column(modifier = Modifier.weight(1f)) {
            Row {
                Text(txt1, style = TextStyle(fontSize = 25.sp, fontWeight = FontWeight.Bold, color = Color.Black))
                Image(
                    painter = painterResource(id = R.drawable.lapiz_icon),
                    contentDescription = null,
                    modifier = Modifier
                        .offset(x = screenWidth * 0.59f)
                        .size(30.dp),
                    contentScale = ContentScale.Fit
                )
            }
            Text(txt2, style = TextStyle(color = Color.Black, fontSize = 25.sp), modifier = Modifier.padding(top = 10.dp))
        }
    }
}