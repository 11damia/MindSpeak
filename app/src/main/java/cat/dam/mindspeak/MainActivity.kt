package cat.dam.mindspeak

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import cat.dam.mindspeak.ui.theme.BackgroundDark
import cat.dam.mindspeak.ui.theme.White
import cat.dam.mindspeak.ui.theme.Black
import cat.dam.mindspeak.ui.theme.BottomBarDark
import cat.dam.mindspeak.ui.theme.DarkGray
import cat.dam.mindspeak.ui.theme.DarkThirdColor
import cat.dam.mindspeak.ui.theme.MindSpeakTheme
import cat.dam.mindspeak.ui.theme.SecondaryColor
import cat.dam.mindspeak.ui.theme.ThirdColorLight

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MindSpeakTheme {
                MyApp("Hola")
            }
        }
    }
}

data class CustomColors(
    val background: Color,
    val backgroundBottomBar: Color,
    val secondary: Color,
    val third: Color,
    val textWhite: Color,
    val textDark: Color,
    val textExtra: Color,
)
val LightCustomColors = CustomColors(
    background = White,
    backgroundBottomBar = White,
    secondary =  SecondaryColor,
    third = ThirdColorLight,
    textWhite = White,
    textDark = Black,
    textExtra = DarkGray
)
val DarkCustomColors = CustomColors(
    background = BackgroundDark,
    backgroundBottomBar = BottomBarDark,
    secondary =  SecondaryColor,
    third = DarkThirdColor,
    textWhite = White,
    textDark = Black,
    textExtra = DarkGray
)

val LocalCustomColors = staticCompositionLocalOf { LightCustomColors }


@Composable
fun MindSpeakTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val customColors = if (darkTheme) DarkCustomColors else LightCustomColors

    CompositionLocalProvider(LocalCustomColors provides customColors) {
        MaterialTheme(
            colorScheme = if (darkTheme) darkColorScheme() else lightColorScheme(),
            content = content
        )
    }
}


@Composable
fun MyApp(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        color = LocalCustomColors.current.textWhite,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MindSpeakTheme {
        MyApp("Android")
    }
}