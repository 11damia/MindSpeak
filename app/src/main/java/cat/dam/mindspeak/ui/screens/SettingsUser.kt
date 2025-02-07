package cat.dam.mindspeak.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cat.dam.mindspeak.R


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
                    .offset(x = screenWidth * 0.85f) // Desplaza la imagen al 30% del ancho
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
    }
}