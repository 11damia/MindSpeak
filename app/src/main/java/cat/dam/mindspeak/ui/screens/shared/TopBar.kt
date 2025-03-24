package cat.dam.mindspeak.ui.screens.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import cat.dam.mindspeak.R
import cat.dam.mindspeak.model.UserViewModel
import cat.dam.mindspeak.ui.theme.LocalCustomColors
import cat.dam.mindspeak.ui.theme.MindSpeakTheme

@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    userViewModel: UserViewModel  // Añade el ViewModel como parámetro
) {
    val userData by userViewModel.userData.collectAsState()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "MIND",
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = LocalCustomColors.current.text2,
                ),
                modifier = Modifier.padding(start = 20.dp)
            )
            Text(
                text = "SPEAK",
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = LocalCustomColors.current.secondary,
                )
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Muestra el nombre del usuario si está disponible
            userData.nom?.let { nombre ->
                Text(
                    text = nombre,  // Usa el nombre del ViewModel
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = LocalCustomColors.current.text1,
                    ),
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
            IconButton(
                onClick = {
                    navController.navigate("settings")
                }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.user_icon),
                    contentDescription = stringResource(R.string.user_icon),
                    modifier = Modifier
                        .size(55.dp)
                        .padding(end = 20.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TopBarPreview() {
    MindSpeakTheme {
        //TopBar()
    }
}