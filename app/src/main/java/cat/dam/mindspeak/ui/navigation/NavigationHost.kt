package cat.dam.mindspeak.ui.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import cat.dam.mindspeak.ui.screens.user.SettingsUser
import cat.dam.mindspeak.model.EmotionViewModel
import cat.dam.mindspeak.model.UserViewModel
import cat.dam.mindspeak.ui.screens.user.EmotionRatingScreen
import cat.dam.mindspeak.ui.screens.user.Emotions
import cat.dam.mindspeak.ui.screens.user.Exercises
import cat.dam.mindspeak.ui.screens.user.Inicio
import cat.dam.mindspeak.ui.screens.user.EmotionHistoryScreen
import cat.dam.mindspeak.ui.screens.supervisor.HomeSupervisorScreen
import cat.dam.mindspeak.ui.screens.Login
import cat.dam.mindspeak.ui.screens.LogoPage
import cat.dam.mindspeak.ui.screens.supervisor.NotificationScreen
import cat.dam.mindspeak.ui.screens.SignUp
import cat.dam.mindspeak.ui.screens.UserProblems
import cat.dam.mindspeak.ui.screens.supervisor.UploadResourceApp
import cat.dam.mindspeak.ui.theme.LocalCustomColors

@Composable
fun NavigationHost(
    navController: NavHostController,
    viewModel: EmotionViewModel,
    userRoleViewModel: UserViewModel // Añade el UserRoleViewModel como parámetro
) {
    NavHost(navController = navController, startDestination = "logo") {
        composable("logo") { LogoPage(navController) }
        composable("signup") { SignUp(navController) }
        composable("homesupervis") { HomeSupervisorScreen(navController) }
        composable("upload"){ UploadResourceApp() }
        composable("notis") { NotificationScreen(navController) }
        composable("problemas") { UserProblems(navController) }
        composable("login") {Login(navController = navController, userViewModel = userRoleViewModel)}
        composable("homeuser") { Inicio(navController) }
        composable("emotions") { Emotions(navController) }
        composable("exercise") { Exercises(navController) }
        composable("settings") { SettingsUser(LocalCustomColors, navController) }
        composable("history") { EmotionHistoryScreen(viewModel = viewModel) }
        composable("emotionRating/{emotionType}") { backStackEntry ->
            val emotionType = backStackEntry.arguments?.getString("emotionType") ?: "UNKNOWN"
            if (emotionType == "UNKNOWN") {
                Log.e("EmotionRatingScreen", "Argument 'emotionType' is missing or invalid")
                navController.popBackStack()
                return@composable
            }
            EmotionRatingScreen(
                navController = navController,
                backStackEntry = backStackEntry,
                viewModel = viewModel
            )
        }
    }
}