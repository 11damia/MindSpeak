package cat.dam.mindspeak.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import cat.dam.mindspeak.R
import cat.dam.mindspeak.ui.theme.DarkGray
import cat.dam.mindspeak.ui.theme.LocalCustomColors

@Composable
fun LogoPage(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        /*
        Image(
            painter = painterResource(id = R.drawable.circulo2),
            contentDescription = "image description",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopStart)
                .zIndex(-1f)
                .graphicsLayer {
                    rotationZ = 180f // Rota la imagen 180 grados
                }
        )*/

        // Contenido centrado
        LazyColumn(
            modifier = Modifier
                .align(Alignment.Center),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "image description",
                    contentScale = ContentScale.FillBounds
                )
                Spacer(Modifier.height(8.dp))
            }
            item {
                Row {
                    Text(
                        text = "MIND",
                        color = LocalCustomColors.current.text2,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 32.sp,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )
                    Text(
                        text = "SPEAK",
                        color = LocalCustomColors.current.secondary,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 32.sp,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )
                }
                Spacer(Modifier.height(8.dp))

            }
            item {
                Button(
                    onClick = { navController.navigate("login") },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LocalCustomColors.current.secondary
                    ),
                    modifier = Modifier
                        .width(300.dp)
                        .height(50.dp)
                ) {
                    Text("Iniciar Sesión")
                }
                Spacer(Modifier.height(8.dp))
            }
            item {
                Button(
                    onClick = { navController.navigate("signup") },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkGray
                    ),
                    modifier = Modifier
                        .width(300.dp)
                        .height(50.dp)
                ) {
                    Text("Registrarse")
                }
                Spacer(Modifier.height(20.dp))
            }
        }
        /*
        Image(
            painter = painterResource(id = R.drawable.circulo2),
            contentDescription = "image description",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomEnd)
                .zIndex(-1f)
                /*
                .graphicsLayer {
                    rotationZ = 180f // Rota la imagen 180 grados
                }*/
        )
        */

    }
}