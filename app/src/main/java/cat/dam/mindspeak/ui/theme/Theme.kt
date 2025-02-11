package cat.dam.mindspeak.ui.theme

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

//White theme
val White = Color(0xFFFFFFFF)
val SecondaryColor = Color(0xFF0C99FF)
val ThirdColorLight = Color(0xFFD8EEFF)
val DarkGray = Color(0xFF434343)
val Black = Color(0xFF000000)
//Dark theme
val BackgroundDark = Color(0xFF272727)
val BottomBarDark = Color(0xFF1B1B1B)
val DarkThirdColor = Color(0xFF23394A)

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)
data class CustomColors(
    val background: Color,
    val backgroundBottomBar: Color,
    val secondary: Color,
    val third: Color,
    val text1: Color,
    val text2: Color,
    val text3: Color,
    val text4: Color,
)

val LightCustomColors = CustomColors(
    background = White,
    backgroundBottomBar = White,
    secondary = SecondaryColor,
    third = ThirdColorLight,
    text1 = Black,
    text2 = DarkGray,
    text3 = Black,
    text4 = White
)
val DarkCustomColors = CustomColors(
    background = BackgroundDark,
    backgroundBottomBar = BottomBarDark,
    secondary = SecondaryColor,
    third = DarkThirdColor,
    text1 = White,
    text2 = White,
    text3 = Black,
    text4 = White
)

val LocalCustomColors = staticCompositionLocalOf { LightCustomColors }


@Composable
fun MindSpeakTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val customColors = if (darkTheme) DarkCustomColors else LightCustomColors
    CompositionLocalProvider(LocalCustomColors provides customColors) {
        Box(modifier = Modifier.background(LocalCustomColors.current.background).fillMaxSize())
        MaterialTheme(
            colorScheme = if (darkTheme) darkColorScheme() else lightColorScheme(),
            content = content
        )
    }
}
