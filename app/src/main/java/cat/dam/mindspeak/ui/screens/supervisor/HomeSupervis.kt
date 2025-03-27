package cat.dam.mindspeak.ui.screens.supervisor

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
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
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = stringResource(R.string.welcome),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = LocalCustomColors.current.text1
                )

                Text(
                    text = userData.nom ?: "Usuario",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium,
                    color = LocalCustomColors.current.text1,
                    modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
                )
            }
        }

        val options = listOf(
            Triple(R.string.alert, "notis", Icons.Default.Notifications),
            Triple(R.string.grafic, "grafic", Icons.Default.PieChart),
            Triple(R.string.add_activity, "upload", Icons.Default.AddCircle),
            Triple(R.string.configuration, "settings", Icons.Default.Settings),
            Triple(R.string.add_user, "signup", Icons.Default.PersonAdd),
            Triple(R.string.manage_users, "user_management", Icons.Default.Group),
            Triple(R.string.assignment_users, "user_assignment", Icons.Default.Assignment)
        )

        items(options.size) { index ->
            val (textRes, route, icon) = options[index]
            StyledOptionButton(text = stringResource(textRes), icon = icon, navController, route)
        }
    }
}

@Composable
fun StyledOptionButton(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector, navController: NavHostController, route: String) {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "buttonScale"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .scale(scale)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        LocalCustomColors.current.third,
                        LocalCustomColors.current.secondary
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable {
                isPressed = true
                navController.navigate(route)
            }
            .padding(vertical = 16.dp, horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = LocalCustomColors.current.text2,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = LocalCustomColors.current.text2
        )
    }
}
