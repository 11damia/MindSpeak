package cat.dam.mindspeak.ui.screens.supervisor


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import cat.dam.mindspeak.R
import cat.dam.mindspeak.firebase.FirebaseManager
import cat.dam.mindspeak.model.AssignedUser
import cat.dam.mindspeak.ui.theme.LocalCustomColors
import kotlinx.coroutines.launch

@Composable
fun NotificationScreen(
    navController: NavHostController,
    firebaseManager: FirebaseManager = FirebaseManager()
) {
    var assignedUsers by remember { mutableStateOf<List<AssignedUser>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    // Fetch assigned users when the screen is first loaded
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            assignedUsers = firebaseManager.getAssignedUsers()
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(assignedUsers) { user ->
            StyledNotification(
                userName = "${user.nom} ${user.cognom}",
                description = user.email,
                onClick = {
                    navController.navigate("user_emotions/${user.userId}")
                }
            )
        }
    }
}

@Composable
fun StyledNotification(userName: String, description: String, onClick: () -> Unit) {
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
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.user_icon),
                    contentDescription = "User Icon",
                    modifier = Modifier
                        .size(40.dp)
                        .background(LocalCustomColors.current.third, shape = RoundedCornerShape(50))
                        .padding(5.dp)
                )
                Text(
                    text = userName,
                    fontWeight = FontWeight.Bold,
                    color = LocalCustomColors.current.text1,
                    fontSize = 18.sp,
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .weight(1f)
                )
                Image(
                    painter = painterResource(id = R.drawable.trash),
                    contentDescription = "Delete Icon",
                    colorFilter = ColorFilter.tint(LocalCustomColors.current.text2),
                    modifier = Modifier
                        .size(30.dp)
                        .clickable { /* Handle delete click */ }
                )
            }
            Text(
                text = description,
                color = LocalCustomColors.current.text1,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 10.dp)
            )
        }
    }
}

