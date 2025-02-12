package cat.dam.mindspeak.ui.screens.shared

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import cat.dam.mindspeak.R
import cat.dam.mindspeak.ui.theme.LocalCustomColors


@Composable
fun BottomBar(
    navController: NavHostController,
    selectedButton: MutableState<Int>,
    modifier: Modifier = Modifier
) {
    val items = listOf("home", "Emotions", "Exercise", "Settings")
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
                        Icon(
                            painter = icon,
                            contentDescription = item,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                },
                selected = selectedButton.value == index,
                onClick = {
                    selectedButton.value = index
                    val route = when (index) {
                        0 -> "inicio" // Route pour "Home"
                        1 -> "emotions"  // Route pour "How I feel"
                        2 -> "exercise" // Route pour "Exercise"
                        3 -> "settings" // Route pour "Settings"
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

@Preview(showBackground = true)
@Composable
fun BottomBarPreview() {
        // Simuler un NavController et un état sélectionné
        val navController = rememberNavController()
        val selectedButton = remember { mutableIntStateOf(0) }
        BottomBar(
            navController = navController,
            selectedButton = selectedButton
        )
}