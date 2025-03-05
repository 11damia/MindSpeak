package cat.dam.mindspeak.ui.screens.shared

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import cat.dam.mindspeak.R
import cat.dam.mindspeak.ui.theme.LocalCustomColors

@Composable
fun BottomBarAdmin(
    navController: NavHostController,
    currentRoute: String?, // Ruta actual
    modifier: Modifier = Modifier
) {
    val items = listOf("homesupervis", "grafic", "notis","upload","settingsAdmin")
    val icons = listOf(
        painterResource(id = R.drawable.home),
        painterResource(id = R.drawable.grafic),
        painterResource(id = R.drawable.alert),
        painterResource(id = R.drawable.heartwind),
        painterResource(id = R.drawable.settings),
        )

    BottomAppBar(
        containerColor = LocalCustomColors.current.backgroundBottomBar,
        modifier = modifier
    ) {
        items.forEachIndexed { index, item ->
            val route = when (index) {
                0 -> "homesupervis" // Ruta para "Home"
                1 -> "grafic" // Ruta para "Emotions"
                2 -> "notis" // Ruta para "Exercise"
                3 -> "upload" // Ruta para "Settings"
                4 -> "settingsAdmin" // Ruta para "Settings"
                else -> null
            }
            NavigationBarItem(
                icon = {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 24.dp, vertical = 8.dp)
                    ) {
                        val icon = icons[index]
                        Icon(
                            painter = icon,
                            contentDescription = item,
                            modifier = Modifier.size(30.dp))
                    }
                },
                selected = currentRoute == route, // Determina si está seleccionado
                onClick = {
                    route?.let {
                        navController.navigate(it) {
                            launchSingleTop = true
                            restoreState = true
                        }
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
