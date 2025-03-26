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
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = LocalCustomColors.current.text1,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = userData.nom ?: "Usuari",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = LocalCustomColors.current.text1,
                modifier = Modifier.padding(bottom = 20.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
        // Applying a modern and clean design to OptionButton
        item {
            StyledOptionButton(text = stringResource(R.string.alert), navController, "notis")
        }
        item {
            StyledOptionButton(text = stringResource(R.string.grafic), navController, "grafic")
        }
        item {
            StyledOptionButton(text = stringResource(R.string.add_activity), navController, "upload")
        }
        item {
            StyledOptionButton(text = stringResource(R.string.configuration), navController, "settings")
        }
        item {
            StyledOptionButton(text = stringResource(R.string.add_user), navController, "signup")
        }
        item {
            StyledOptionButton(text = stringResource(R.string.manage_users), navController, "user_management")
        }
        item {
            StyledOptionButton(text = stringResource(R.string.assignment_users), navController, "user_assignment")
        }
    }
}

@Composable
fun StyledOptionButton(text: String, navController: NavHostController, route: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 15.dp)
            .background(
                brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                    colors = listOf(
                        LocalCustomColors.current.secondary,
                        LocalCustomColors.current.third
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { navController.navigate(route) }
            .padding(vertical = 12.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = LocalCustomColors.current.text2
        )
    }
}
