package cat.dam.mindspeak.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import cat.dam.mindspeak.R
import cat.dam.mindspeak.ui.theme.CustomColors
import cat.dam.mindspeak.ui.theme.White

@Composable
fun Inicio(navController: NavHostController,localCustomColors: ProvidableCompositionLocal<CustomColors>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 80.dp, start = 20.dp, end = 20.dp)
    ) {
        item {
            Text(
                text = "Bienvenid@",
                fontWeight = FontWeight.Bold,
                color = localCustomColors.current.text1,
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "UserName",
                color = localCustomColors.current.text1,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            Box(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp).background(localCustomColors.current.third),
            ) {
                Image(
                painter = painterResource(id = R.drawable.persona_settings_user),
                contentDescription = "Feeling",
                modifier = Modifier
                    .zIndex(1f)
                    .align(Alignment.BottomEnd)
                )
                Column(
                    modifier = Modifier.padding(top = 16.dp, end = 16.dp, start = 16.dp).zIndex(2f),
                    horizontalAlignment = Alignment.Start
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Como me siento",
                        color = localCustomColors.current.text2,
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Espacio para que niños y adultos elijan imágenes que representen cómo se sienten.",
                        color = localCustomColors.current.text2,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(90.dp))
                    Button(
                        onClick = { /* Acción al hacer clic */ },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = localCustomColors.current.secondary
                        )
                    ) {
                        Text(
                            text = "Quiero Expresarme",
                            color = White
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
        item {
            Box(
                modifier = Modifier.fillMaxWidth().background(localCustomColors.current.third),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.tablet),
                    contentDescription = "Feeling",
                    modifier = Modifier
                        .width(181.dp)
                        .height(161.dp)
                        .zIndex(1f)
                        .align(Alignment.BottomEnd)
                )
                Column(
                    modifier = Modifier.padding(top = 16.dp, end = 16.dp, start = 16.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Ejercicios",
                        fontWeight = FontWeight.Bold,
                        color = localCustomColors.current.text2,
                        fontSize = 30.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Recursos y actividades para apoyar su desarrollo, comunicación y autonomía de forma inclusiva.",
                        color = localCustomColors.current.text2,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(90.dp))
                    Button(
                        onClick = {
                            navController.navigate("exercise")
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = localCustomColors.current.secondary
                        )
                    ) {
                        Text(
                            text = "Hacer Tareas",
                            color = White
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
@Preview(showBackground = true)
@Composable
fun InicioPreview() {
    //Inicio(n,LocalCustomColors)
}