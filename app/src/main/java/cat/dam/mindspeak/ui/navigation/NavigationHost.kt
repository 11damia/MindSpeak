package cat.dam.mindspeak.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import cat.dam.mindspeak.SettingsUser
import cat.dam.mindspeak.ui.screens.Exercises

import cat.dam.mindspeak.ui.screens.Inicio
import cat.dam.mindspeak.ui.theme.LocalCustomColors

@Composable
fun NavigationHost(
    navController: NavHostController,
) {
    NavHost(navController = navController, startDestination = "inicio") {
        composable("inicio") { Inicio(LocalCustomColors) }
        composable("settings") { SettingsUser() }
        composable("exercise") { Exercises(LocalCustomColors) }

    }
}