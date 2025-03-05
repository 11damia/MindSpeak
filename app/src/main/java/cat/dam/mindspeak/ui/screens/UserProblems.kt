package cat.dam.mindspeak.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import cat.dam.mindspeak.R
import cat.dam.mindspeak.ui.theme.LocalCustomColors

@Composable
fun UserProblems(navController: NavHostController?) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 20.dp, horizontal = 25.dp)
    ) {
        // Flecha de retroceso fuera del Box
        Image(
            painter = painterResource(id = R.drawable.arrow_back),
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .clickable {
                    // Navegar hacia atrás o realizar alguna acción
                    navController?.popBackStack()
                }
                .padding(bottom = 10.dp) // Espacio entre la flecha y el Box
        )

        // Box que contiene el resto del contenido
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(LocalCustomColors.current.third, shape = RoundedCornerShape(14.dp))
                .padding(16.dp),
            contentAlignment = Alignment.TopStart
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.user_icon),
                        contentDescription = "user_icon",
                        modifier = Modifier.size(60.dp)
                    )
                    Text(
                        text = "UserName",
                        fontWeight = FontWeight.Bold,
                        color = LocalCustomColors.current.text2,
                        fontSize = 30.sp,
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(30.dp))
                Problem("01/01/0001", "desc")
                Divider(
                    modifier = Modifier.padding(vertical = 10.dp),
                    thickness = 2.dp,
                    color = LocalCustomColors.current.text1
                )
                Problem("02/02/0002", "desc")
                Divider(
                    modifier = Modifier.padding(vertical = 10.dp),
                    thickness = 2.dp,
                    color = LocalCustomColors.current.text1
                )
                Problem("03/03/0003", "desc")
                Divider(
                    modifier = Modifier.padding(vertical = 10.dp),
                    thickness = 2.dp,
                    color = LocalCustomColors.current.text1
                )
                Problem("04/04/0004", "desc")
                Divider(
                    modifier = Modifier.padding(vertical = 10.dp),
                    thickness = 2.dp,
                    color = LocalCustomColors.current.text1
                )
                Problem("05/05/0005", "desc")
            }
        }
    }
}


@Composable
fun Problem(date: String, description: String) {
    Column (Modifier.fillMaxWidth()){
        Row (modifier = Modifier.fillMaxWidth()){
            Text(
                text = date,
                fontWeight = FontWeight.ExtraBold,
                color = LocalCustomColors.current.text1,
                fontSize = 20.sp,
            )

        }
        Row (modifier = Modifier.fillMaxWidth()){
            Text(
                text = description,
                color = LocalCustomColors.current.text2,
                fontSize = 20.sp,
            )

        }
    }
}