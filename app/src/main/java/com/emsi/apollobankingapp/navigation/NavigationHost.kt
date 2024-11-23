package com.emsi.apollobankingapp.navigation

import MainViewModel
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.emsi.apollobankingapp.HomeScreen
import com.emsi.apollobankingapp.screens.AccountDetailsScreen
import com.emsi.apollobankingapp.screens.AccountListScreen
import com.emsi.apollobankingapp.screens.AccountTransactionListScreen
import com.emsi.apollobankingapp.screens.AllTransactionListScreen
import com.emsi.apollobankingapp.screens.CreateAccountScreen
import com.emsi.apollobankingapp.screens.CreateTransactionScreen
import com.emsi.apollobankingapp.screens.SoldeStatisticsScreen
import com.emsi.apollobankingapp.screens.StatsScreen
import com.emsi.apollobankingapp.screens.TransactionStatsScreen

@Composable
fun NavigationHost(viewModel: MainViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(navController = navController)
        }
        composable("accounts") {
            AccountListScreen(viewModel = viewModel, navController = navController)
        }
        composable("create_account") {
            CreateAccountScreen(viewModel = viewModel)
        }
        composable(
            "account_transactions_list/{id}",
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val accountId = backStackEntry.arguments?.getLong("id") ?: 0L
            AccountTransactionListScreen(
                viewModel = viewModel,
                navController = navController,
                compteId = accountId
            )
        }
        composable("create_transaction") {
            CreateTransactionScreen(viewModel = viewModel)
        }
        composable("solde_statistics") {
            SoldeStatisticsScreen(viewModel = viewModel)
        }
        composable(
            "account_details/{id}",
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val accountId = backStackEntry.arguments?.getLong("id") ?: 0L
            AccountDetailsScreen(
                viewModel = viewModel,
                compteId = accountId,
                navController = navController
            )
        }
        composable("transaction_stats/{id}") { backStackEntry ->
            TransactionStatsScreen(
                viewModel = viewModel,
                navController = navController,
                backStackEntry = backStackEntry
            )
        }
        composable("all_transactions") {
            AllTransactionListScreen(viewModel = viewModel, navController = navController)
        }
        composable("statistics") {
            StatsScreen(viewModel = viewModel)
        }
    }
}
