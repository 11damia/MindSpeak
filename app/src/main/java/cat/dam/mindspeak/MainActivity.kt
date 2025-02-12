package cat.dam.mindspeak

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import cat.dam.mindspeak.model.EmotionViewModel
import cat.dam.mindspeak.ui.navigation.NavigationHost
import cat.dam.mindspeak.ui.screens.shared.BottomBar
import cat.dam.mindspeak.ui.screens.shared.TopBar
import cat.dam.mindspeak.ui.theme.LocalCustomColors
import cat.dam.mindspeak.ui.theme.MindSpeakTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MindSpeakTheme {
                val viewModel: EmotionViewModel = viewModel()
                MyApp(viewModel)
            }
        }
    }
}




@Composable
fun MyApp(viewModel: EmotionViewModel = viewModel()) {
    val navController = rememberNavController()
    val selectedButton = rememberSaveable { mutableIntStateOf(0) }
    val showBottomBar by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .background(LocalCustomColors.current.background)
    ) {

        TopBar(navController = navController)


        Box(modifier = Modifier.weight(1f)) {
            NavigationHost(navController = navController, viewModel = viewModel)
        }


        if (showBottomBar) {
            BottomBar(
                navController = navController,
                selectedButton = selectedButton
            )
        }
    }
}



@Preview(showBackground = true)
@Composable
fun MyAppPreview() {
    MindSpeakTheme {
        MyApp()
    }
}