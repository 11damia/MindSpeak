package cat.dam.mindspeak.ui.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import cat.dam.mindspeak.firebase.FirebaseManager
import cat.dam.mindspeak.model.EmotionViewModel
import cat.dam.mindspeak.model.UserRelationViewModel
import cat.dam.mindspeak.model.UserViewModel
import cat.dam.mindspeak.ui.screens.EmotionStatistics
import cat.dam.mindspeak.ui.screens.Login
import cat.dam.mindspeak.ui.screens.LogoPage
import cat.dam.mindspeak.ui.screens.SignUp
import cat.dam.mindspeak.ui.screens.UserProblems
import cat.dam.mindspeak.ui.screens.supervisor.HomeSupervisorScreen
import cat.dam.mindspeak.ui.screens.supervisor.NotificationScreen
import cat.dam.mindspeak.ui.screens.supervisor.SupervisorManagementScreen
import cat.dam.mindspeak.ui.screens.supervisor.SupervisorUserAssignmentScreen
import cat.dam.mindspeak.ui.screens.supervisor.UploadResourceApp
import cat.dam.mindspeak.ui.screens.supervisor.UserEmotionsScreen
import cat.dam.mindspeak.ui.screens.user.EmotionHistoryScreen
import cat.dam.mindspeak.ui.screens.user.EmotionRatingScreen
import cat.dam.mindspeak.ui.screens.user.Emotions
import cat.dam.mindspeak.ui.screens.user.Exercises
import cat.dam.mindspeak.ui.screens.user.Inicio
import cat.dam.mindspeak.ui.screens.user.SettingsUser
import cat.dam.mindspeak.ui.theme.LocalCustomColors

@Composable
fun NavigationHost(
    navController: NavHostController,
    emotionViewModel: EmotionViewModel, // Renommé
    userViewModel: UserViewModel // Añade el UserRoleViewModel como parámetro
) {

    NavHost(navController = navController, startDestination = "logo") {
        composable("logo") { LogoPage(navController) }
        composable("signup") { SignUp(navController) }
        composable("homesupervis") { HomeSupervisorScreen(navController,userViewModel) }
        composable("user_management") { SupervisorManagementScreen(navController, FirebaseManager()  ) }
        composable("user_Assignment") {
            val userRelationViewModel: UserRelationViewModel = viewModel() // Correct
            val currentSupervisorId = userViewModel.getCurrentUserId()
            // Debug
            println("Current Supervisor ID in Nav: $currentSupervisorId")
            SupervisorUserAssignmentScreen(
                navController = navController,
                userRelationViewModel = userRelationViewModel,
                currentSupervisorId = currentSupervisorId
            )
        }
        composable("upload"){ UploadResourceApp() }
        composable("notis") {
            NotificationScreen(
                navController = navController,
                firebaseManager = FirebaseManager()
            )
        }
        // New route for user emotions screen
        composable(
            route = "user_emotions/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            if (userId != null) {
                UserEmotionsScreen(
                    navController = navController,
                    userId = userId,
                    firebaseManager = FirebaseManager()
                )
            }
        }
        composable("problemas") { UserProblems(navController) }
        composable("login") {Login(navController = navController, userViewModel = userViewModel, context = LocalContext.current) }
        composable("homeuser") { Inicio(navController,userViewModel) }
        composable("emotions") { Emotions(navController) }
        composable("exercise") { Exercises(navController) }
        composable("settings") { SettingsUser(LocalCustomColors, navController, userViewModel) }
        composable("history") { EmotionHistoryScreen(viewModel =emotionViewModel) }
        composable("grafic") { EmotionStatistics() }
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
                viewModel = emotionViewModel
            )
        }
    }
}