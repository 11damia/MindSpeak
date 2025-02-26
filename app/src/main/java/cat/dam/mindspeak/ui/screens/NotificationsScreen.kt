package cat.dam.mindspeak.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import cat.dam.mindspeak.R
import cat.dam.mindspeak.ui.theme.LocalCustomColors

@Preview(showBackground = true)
@Composable
fun NotificationScreen() {


    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
    ) {
        item {
            Notification(userName = "User 1", description = "Esta trist li ha passat algo a l’escola...", onClick = { /* Handle click */ })
        }
        item {
            Notification(userName = "User 2", description = "Esta trist li ha passat algo a l’escola...", onClick = { /* Handle click */ })
        }
        item {
            Notification(userName = "User 3", description = "Esta trist li ha passat algo a l’escola...", onClick = { /* Handle click */ })
        }
        item {
            Notification(userName = "User 4", description = "Esta trist li ha passat algo a l’escola...", onClick = { /* Handle click */ })
        }
        item {
            Notification(userName = "User 5", description = "Esta trist li ha passat algo a l’escola...", onClick = { /* Handle click */ })
        }
    }
}


@Composable
fun Notification(userName: String, description: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 15.dp)
            .background(LocalCustomColors.current.third, shape = RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .height(100.dp)
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column (
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ){
            Row (
                modifier = Modifier.fillMaxWidth()
            ){
                Image(
                    painter = painterResource(id = R.drawable.user_icon),
                    contentDescription = "user_icon",
                    modifier = Modifier
                        .size(30.dp)
                )
                Text(
                    text = userName,
                    fontWeight = FontWeight.Bold,
                    color = LocalCustomColors.current.text2,
                    fontSize = 18.sp,
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .weight(1f)
                )
                Image(
                    painter = painterResource(id = R.drawable.trash),
                    contentDescription = "user_icon",
                    modifier = Modifier
                        .size(25.dp)
                        .align(Alignment.CenterVertically)
                )
            }
            Text(
                text = description,
                color = LocalCustomColors.current.text3,
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(top = 10.dp)
                    .weight(1f)
            )
        }
    }
}