package cat.dam.mindspeak.ui.screens.user

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import cat.dam.mindspeak.R
import cat.dam.mindspeak.ui.theme.LocalCustomColors
import cat.dam.mindspeak.ui.theme.White

@Composable
fun Inicio(navController: NavHostController) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 20.dp, end = 20.dp)
    ) {
        item {
            Text(
                text = "Bienvenid@",
                fontWeight = FontWeight.Bold,
                color = LocalCustomColors.current.text1,
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "UserName",
                color = LocalCustomColors.current.text1,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
                    .background(LocalCustomColors.current.third),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.persona_settings_user),
                    contentDescription = "Feeling",
                    modifier = Modifier
                        .zIndex(1f)
                        .align(Alignment.BottomEnd)
                )
                Column(
                    modifier = Modifier
                        .padding(top = 16.dp, end = 16.dp, start = 16.dp)
                        .zIndex(2f),
                    horizontalAlignment = Alignment.Start
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Como me siento",
                        color = LocalCustomColors.current.text2, // Accès direct
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Espacio para que niños y adultos elijan imágenes que representen cómo se sienten.",
                        color = LocalCustomColors.current.text2, // Accès direct
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(90.dp))
                    Button(
                        onClick = { navController.navigate("emotions") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LocalCustomColors.current.secondary
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
                    .background(LocalCustomColors.current.third),
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
                        color = LocalCustomColors.current.text2,
                        fontSize = 30.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Recursos y actividades para apoyar su desarrollo, comunicación y autonomía de forma inclusiva.",
                        color = LocalCustomColors.current.text2,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(90.dp))
                    Button(
                        onClick = {
                            navController.navigate("exercise")
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LocalCustomColors.current.secondary // Accès direct
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
        }
        item {
            Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(LocalCustomColors.current.third)
                        .padding(bottom = 24.dp),
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.history_icon), // Ajoutez un icône appropriée
                        contentDescription = "Historial",
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
                            text = "Historial de emociones",
                            fontWeight = FontWeight.Bold,
                            color = LocalCustomColors.current.text2,
                            fontSize = 30.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Consulta tu historial de emociones registradas.",
                            color = LocalCustomColors.current.text2,
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(90.dp))
                        Button(
                            onClick = { navController.navigate("history") }, // Navigation vers l'historial
                            colors = ButtonDefaults.buttonColors(
                                containerColor = LocalCustomColors.current.secondary
                            )
                        ) {
                            Text(
                                text = "Ver historial",
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
    Inicio(navController = rememberNavController())
}