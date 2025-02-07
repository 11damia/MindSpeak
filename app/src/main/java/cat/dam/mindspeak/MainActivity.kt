package cat.dam.mindspeak

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import cat.dam.mindspeak.ui.navigation.NavigationHost
import cat.dam.mindspeak.ui.screens.shared.BottomBar
import cat.dam.mindspeak.ui.screens.shared.TopBar
import cat.dam.mindspeak.ui.theme.LocalCustomColors
import cat.dam.mindspeak.ui.theme.MindSpeakTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
           MindSpeakTheme {
                MyApp()
            }
        }
    }
}




@Composable
fun MyApp() {
    val navController = rememberNavController()
    val selectedButton = rememberSaveable { mutableIntStateOf(0) }
    val showBottomBar by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .background(LocalCustomColors.current.background)
    ) {
        TopBar(modifier = Modifier.align(Alignment.TopCenter))
        NavigationHost(
            navController = navController,
        )
        if (showBottomBar) {
            BottomBar(
                navController,
                selectedButton,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}



@Composable
fun Info(){

}

@Preview(showBackground = true)
@Composable
fun MyAppPreview() {
    MindSpeakTheme {
        MyApp()
    }
}