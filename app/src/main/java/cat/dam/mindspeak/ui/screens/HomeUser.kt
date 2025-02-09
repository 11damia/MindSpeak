package cat.dam.mindspeak.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cat.dam.mindspeak.ui.theme.CustomColors
import cat.dam.mindspeak.ui.theme.White

@Composable
fun Inicio(localCustomColors: ProvidableCompositionLocal<CustomColors>) {
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
                modifier = Modifier.fillMaxWidth().background(localCustomColors.current.third),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    //Image(painter = painterResource(id = R.drawable.ic_user_feeling), contentDescription = "Feeling")
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Como me siento",
                        color = localCustomColors.current.text2,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Espacio para que niños y adultos elijan imágenes que representen cómo se sienten.",
                        color = localCustomColors.current.text2,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
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
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            Box(
                modifier = Modifier.fillMaxWidth().background(localCustomColors.current.third),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    //Image(painter = painterResource(id = R.drawable.ic_exercise), contentDescription = "Exercise")
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Ejercicios",
                        fontWeight = FontWeight.Bold,
                        color = localCustomColors.current.text2,
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Recursos y actividades para apoyar su desarrollo, comunicación y autonomía de forma inclusiva.",
                        color = localCustomColors.current.text2,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { /* Acción al hacer clic */ },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = localCustomColors.current.secondary
                        )
                    ) {
                        Text(
                            text = "Hacer Tareas",
                            color = White
                        )
                    }
                }
            }
        }
    }
}