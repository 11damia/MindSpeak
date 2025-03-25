package cat.dam.mindspeak.ui.screens.supervisor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import cat.dam.mindspeak.R
import cat.dam.mindspeak.model.UserViewModel
import cat.dam.mindspeak.ui.theme.LocalCustomColors

@Composable
fun HomeSupervisorScreen(navController: NavHostController, userViewModel: UserViewModel) {
    val userData by userViewModel.userData.collectAsState()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        item {
            Text(
                text = stringResource(R.string.welcome),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = LocalCustomColors.current.text1,
            )

            Text(
                text = userData.nom ?: "Usuari",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = LocalCustomColors.current.text1,
            )
            Spacer(modifier = Modifier.height(30.dp))
        }
        item {
            OptionButton(text = stringResource(R.string.alert), onClick = { navController.navigate("notis") })
        }
        item {
            OptionButton(text = stringResource(R.string.grafic), onClick = { navController.navigate("grafic") })
        }
        item {
            OptionButton(text = stringResource(R.string.add_activity), onClick = { navController.navigate("upload") })
        }
        item {
            OptionButton(text = stringResource(R.string.configuration), onClick = { navController.navigate("settings") })
        }
        item {
            OptionButton(text = stringResource(R.string.add_user), onClick = { navController.navigate("signup") })
        }
        item {
            OptionButton(text = stringResource(R.string.manage_users), onClick = { navController.navigate("user_management") })
        }
        item {
            OptionButton(text = stringResource(R.string.assignment_users), onClick = { navController.navigate("user_assignment") })
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