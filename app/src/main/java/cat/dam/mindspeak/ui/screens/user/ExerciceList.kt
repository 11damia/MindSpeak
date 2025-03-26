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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import cat.dam.mindspeak.R
import cat.dam.mindspeak.model.UserViewModel
import cat.dam.mindspeak.ui.theme.LocalCustomColors
import cat.dam.mindspeak.ui.theme.White

@Composable
fun ExerciceList(navController: NavHostController, userViewModel: UserViewModel) {
    val userData by userViewModel.userData.collectAsState()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 20.dp, end = 20.dp)
    ) {
        item {
            Text(
                text = stringResource(R.string.welcome),
                fontWeight = FontWeight.Bold,
                color = LocalCustomColors.current.text1,
                fontSize = 24.sp
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
//                Image(
//                    painter = painterResource(id = R.drawable.tablet),
//                    contentDescription = stringResource(R.string.emotions),
//                    modifier = Modifier
//                        .width(181.dp)
//                        .height(161.dp)
//                        .zIndex(1f)
//                        .align(Alignment.BottomEnd)
//                )
                Column(
                    modifier = Modifier.padding(top = 16.dp, end = 16.dp, start = 16.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
//                        text = stringResource(R.string.activities),
                        text = "Titol exercici",
                        fontWeight = FontWeight.Bold,
                        color = LocalCustomColors.current.text2,
                        fontSize = 30.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Descripció ecercici",
                        color = LocalCustomColors.current.text2,
                        fontWeight = FontWeight.Normal,
                        fontSize = 17.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            navController.navigate("exercise")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LocalCustomColors.current.secondary // Accès direct
                        )
                    ) {
                        Text(
                            text = "FER EXERCICI",
                            fontSize = 15.sp,
                            color = White
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

    }
}