package com.emsi.apollobankingapp.screens

import MainViewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.emsi.apollobankingapp.state.UiState

@Composable
fun SoldeStatisticsScreen(viewModel: MainViewModel) {
    val soldeStatsState by viewModel.totalSolde.collectAsState()


    LaunchedEffect(Unit) {
        viewModel.fetchTotalSolde()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        when (soldeStatsState) {
            is UiState.Loading -> {

                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            is UiState.Success -> {

                val stats = (soldeStatsState as UiState.Success).data
                Column(
                    modifier = Modifier.align(Alignment.TopCenter),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Solde Statistics", style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Total Accounts: ${stats.count}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text("Total Sum: ${stats.sum}", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "Average Solde: ${stats.average}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            is UiState.Error -> {

                ErrorScreen(message = (soldeStatsState as UiState.Error).message) {

                    viewModel.fetchTotalSolde()
                }
            }
        }
    }
}

@Composable
fun ErrorScreen(message: String, retryAction: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = retryAction) {
            Text("Retry")
        }
    }
}
