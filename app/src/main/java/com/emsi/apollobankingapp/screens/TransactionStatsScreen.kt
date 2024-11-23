package com.emsi.apollobankingapp.screens

import MainViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.emsi.apollobankingapp.state.UiState

@Composable
fun TransactionStatsScreen(
    viewModel: MainViewModel,
    navController: NavController,
    backStackEntry: NavBackStackEntry
) {
    val compteId = backStackEntry.arguments?.getString("id")?.toLongOrNull() ?: 0L
    val transactionsState by viewModel.transactions.collectAsState()


    var totalAmount by remember { mutableStateOf(0.0) }
    var numTransactions by remember { mutableStateOf(0) }
    var avgAmount by remember { mutableStateOf(0.0) }


    LaunchedEffect(compteId) {
        if (compteId != 0L) {
            viewModel.fetchTransactionsByCompteId(compteId)
        }
    }


    if (transactionsState is UiState.Success) {
        val transactions = (transactionsState as UiState.Success).data
        numTransactions = transactions.size
        totalAmount = transactions.sumOf { it.montant }
        avgAmount = if (numTransactions > 0) totalAmount / numTransactions else 0.0
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFFFE5E5))) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Statistiques des transactions",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (transactionsState) {
                is UiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
                is UiState.Success -> {
                    Column {
                        Text("Nombre total de transactions: $numTransactions")
                        Text("Montant total des transactions: %.2f".format(totalAmount))
                        Text("Montant moyen des transactions: %.2f".format(avgAmount))
                    }
                }
                is UiState.Error -> {
                    Text(
                        text = "Erreur lors du chargement des statistiques.",
                        color = Color.Red
                    )
                }
            }
        }
    }
}
