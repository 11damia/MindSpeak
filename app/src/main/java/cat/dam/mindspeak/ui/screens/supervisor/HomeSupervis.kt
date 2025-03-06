package cat.dam.mindspeak.ui.screens.supervisor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import cat.dam.mindspeak.ui.theme.LocalCustomColors

@Composable
fun HomeSupervisorScreen(navController: NavHostController) {


    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
    ) {
        item {
            Text(
                text = "Bienvenid@",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = LocalCustomColors.current.text1,
//                modifier = Modifier.align(Alignment.Start)
            )

            Text(
                text = "Username",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = LocalCustomColors.current.text1,
//                modifier = Modifier.padding(bottom = 24.dp).align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(30.dp))
        }
        item {
            OptionButton(text = "Alertas", onClick = { navController.navigate("notis") })
        }
        item {
            OptionButton(text = "Gráfico", onClick = { navController.navigate("grafic") })
        }
        item {
            OptionButton(text = "Añadir Ejercicio", onClick = { navController.navigate("upload") })
        }
        item {
            OptionButton(text = "Configuración", onClick = { navController.navigate("settings") })
        }
        item {
            OptionButton(text = "Crear Usuario", onClick = { navController.navigate("signup") })
        }
    }
}


@Composable
fun OptionButton(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 15.dp)
            .background(LocalCustomColors.current.third, shape = RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .height(100.dp)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 28.sp,
            fontWeight = FontWeight.Medium,
            color = LocalCustomColors.current.text2
        )
    }
}