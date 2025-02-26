package cat.dam.mindspeak

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
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
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val showBottomBar = when (currentRoute) {
        "login" -> false // Ocultar en la pantalla de login
        "logo" ->false
        "signup" ->false
        else -> !isLandscape // Mostrar en otras pantallas
    }
    val showTopBar = when (currentRoute){
        "login" -> false // Ocultar en la pantalla de login
        "logo" ->false
        "signup" -> false
        else -> !isLandscape // Mostrar en otras pantallas
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .background(LocalCustomColors.current.background)
    ) {
        if(showTopBar){
            TopBar(navController = navController)
        }

        Box(modifier = Modifier.weight(1f)) {
            NavigationHost(navController = navController, viewModel = viewModel)
        }

        if (showBottomBar) {
            BottomBar(
                navController = navController,
                currentRoute = currentRoute
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