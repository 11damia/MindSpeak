package cat.dam.mindspeak.ui.screens.supervisor

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cat.dam.mindspeak.R
import cat.dam.mindspeak.ui.theme.LocalCustomColors

@Preview
@Composable
fun UploadResourceApp() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        item {
        Text(text = "Subir un recurso", color = LocalCustomColors.current.text1,fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(32.dp))
        }
        item {
            Text(text = "Título del ejercicio:", fontWeight = FontWeight.Medium,color = LocalCustomColors.current.text1)
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = "Ejercicio n°1",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
        item {
            UploadSection(
                title = "Subir una imagen",
                imageResId = R.drawable.cam, // ID de la imagen
                onClick = { /* Lógica para subir una imagen */ }
            )
        }
        item {
            UploadSection(
                title = "Subir un video",
                imageResId = R.drawable.video, // ID de la imagen
                onClick = { /* Lógica para subir un video */ }
            )
        }
        item {
            UploadSection(
                title = "Grabar audio / Subir audio",
                imageResId = R.drawable.audio, // ID de la imagen
                onClick = { /* Lógica para grabar o subir audio */ }
            )
        }
    }
}

@Composable
fun UploadSection(title: String, imageResId: Int, imageSize: Dp = 48.dp, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Medium,
                color = LocalCustomColors.current.text1,
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier
                    .clickable {}
            ) {
                Image(
                    painter = painterResource(id = R.drawable.upload),
                    contentDescription = "Upload",
                    modifier = Modifier.size(20.dp))
            }

        }
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 10.dp),
            thickness = 2.dp,
            color = LocalCustomColors.current.text1
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clickable { onClick() }
                .background(LocalCustomColors.current.third, shape = RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = title,
                modifier = Modifier.size(imageSize))
        }
    }
    Spacer(modifier = Modifier.height(32.dp))
}
