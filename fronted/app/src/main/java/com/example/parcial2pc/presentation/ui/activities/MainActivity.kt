package com.example.parcial2pc.presentation.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.parcial2pc.presentation.ui.screens.CreateGoalScreen
import com.example.parcial2pc.presentation.ui.screens.GoalDetailScreen
import com.example.parcial2pc.presentation.ui.screens.HomeScreen
import com.example.parcial2pc.presentation.ui.screens.PaymentScreen
import com.example.parcial2pc.presentation.ui.screens.PaymentsListScreen
import com.example.parcial2pc.presentation.ui.theme.Parcial2PcTheme
import com.example.parcial2pc.viewmodels.GoalViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Parcial2PcTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val viewModel: GoalViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                onGoalClick = { goalId ->
                    navController.navigate("goalDetail/$goalId")
                },
                onCreateGoal = {
                    navController.navigate("createGoal")
                },
                viewModel = viewModel
            )
        }

        composable("createGoal") {
            CreateGoalScreen(
                onBack = { navController.popBackStack() },
                onGoalCreated = { navController.popBackStack() },
                viewModel = viewModel
            )
        }

        composable(
            route = "goalDetail/{goalId}",
            arguments = listOf(navArgument("goalId") { type = NavType.StringType })
        ) { backStackEntry ->
            val goalId = backStackEntry.arguments?.getString("goalId") ?: ""
            GoalDetailScreen(
                goalId = goalId,
                onBack = { navController.popBackStack() },
                onMakePayment = { id -> navController.navigate("payment/$id") },
                onViewPayments = { id -> navController.navigate("paymentsList/$id") },
                viewModel = viewModel
            )
        }

        composable(
            route = "payment/{goalId}",
            arguments = listOf(navArgument("goalId") { type = NavType.StringType })
        ) { backStackEntry ->
            val goalId = backStackEntry.arguments?.getString("goalId") ?: ""
            PaymentScreen(
                goalId = goalId,
                onBack = { navController.popBackStack() },
                onPaymentConfirmed = { navController.popBackStack() },
                viewModel = viewModel
            )
        }

        composable(
            route = "paymentsList/{goalId}",
            arguments = listOf(navArgument("goalId") { type = NavType.StringType })
        ) { backStackEntry ->
            val goalId = backStackEntry.arguments?.getString("goalId") ?: ""
            PaymentsListScreen(
                goalId = goalId,
                onBack = { navController.popBackStack() },
                viewModel = viewModel
            )
        }
    }
}
