package cat.dam.mindspeak

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
            cat.dam.mindspeak.MindSpeakTheme {
                MyApp()

            }
        }
    }
}


data class CustomColors(
    val background: Color,
    val backgroundBottomBar: Color,
    val secondary: Color,
    val third: Color,
    val text1: Color,
    val text2: Color,
    val text3: Color,
)

val LightCustomColors = CustomColors(
    background = White,
    backgroundBottomBar = White,
    secondary = SecondaryColor,
    third = ThirdColorLight,
    text1 = Black,
    text2 = DarkGray,
    text3 = Black
)
val DarkCustomColors = CustomColors(
    background = BackgroundDark,
    backgroundBottomBar = BottomBarDark,
    secondary = SecondaryColor,
    third = DarkThirdColor,
    text1 = White,
    text2 = White,
    text3 = Black
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


@Composable
fun MyApp() {
    val navController = rememberNavController()
    val selectedButton = rememberSaveable { mutableIntStateOf(0) }
    var showBottomBar by remember { mutableStateOf(true) }

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
fun TopBar(modifier: Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "MIND",
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = LocalCustomColors.current.text2,
                ),
                modifier = Modifier.padding(start = 20.dp)
            )
            Text(
                text = "SPEAK",
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = LocalCustomColors.current.secondary,
                )
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "UserName",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = LocalCustomColors.current.text1,
                ),
                modifier = Modifier.padding(end = 10.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.user_icon),
                contentDescription = "User Icon",
                modifier = Modifier.size(55.dp).padding(end = 20.dp)
            )
        }
    }
}


@Composable
fun BottomBar(
    navController: NavHostController,
    selectedButton: MutableState<Int>,
    modifier: Modifier = Modifier
) {
    val items = listOf("home", "How I feel","Exercice","Settings")

    val icons = listOf(
        painterResource(id = R.drawable.home),
        painterResource(id = R.drawable.heartplus),
        painterResource(id = R.drawable.heartwind),
        painterResource(id = R.drawable.settings),
    )
    BottomAppBar(
        containerColor = LocalCustomColors.current.backgroundBottomBar,
        modifier = modifier
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 24.dp, vertical = 8.dp)
                    ) {
                        val icon = icons[index]
                        Icon(painter = icon, contentDescription = item, modifier = Modifier.size(30.dp))
                    }
                },
                selected = selectedButton.value == index,
                onClick = {
                    selectedButton.value = index
                    val route = when (index) {
                        0 -> "inicio"  // Ítem de "Inicio"
                        1 -> "Info"
                        else -> return@NavigationBarItem
                    }
                    navController.navigate(route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = LocalCustomColors.current.secondary,
                    unselectedIconColor = LocalCustomColors.current.text1,
                    selectedTextColor = LocalCustomColors.current.secondary,
                    unselectedTextColor = LocalCustomColors.current.text1,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}
@Composable
fun NavigationHost(
    navController: NavHostController,
) {
    NavHost(navController = navController, startDestination = "inicio") {
        composable("inicio") { Inicio(LocalCustomColors) }
        composable("Info") { Info() }

    }
}

@Composable
fun Info(){

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MindSpeakTheme {
    }
}