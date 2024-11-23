package com.emsi.apollobankingapp.screens

import MainViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emsi.apollobankingapp.state.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(viewModel: MainViewModel) {
    val soldeStatsState = viewModel.soldeStats.collectAsState()
    val transactionStatsState = viewModel.transactionStats.collectAsState()


    var selectedSoldeStat by remember { mutableStateOf("Nombre de comptes") }
    var selectedTransactionStat by remember { mutableStateOf("Nombre de transactions") }


    var isSoldeDropdownExpanded by remember { mutableStateOf(false) }
    var isTransactionDropdownExpanded by remember { mutableStateOf(false) }


    val soldeOptions = listOf("Nombre de comptes", "Total solde", "Moyenne solde")
    val transactionOptions =
        listOf("Nombre de transactions", "Total des dépôts", "Total des retraits")


    LaunchedEffect(Unit) {
        viewModel.fetchTotalSolde()
        viewModel.fetchTransactionStats()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFE5E5))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Statistiques",
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 24.sp)
            )


            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Statistiques de Solde",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    ExposedDropdownMenuBox(
                        expanded = isSoldeDropdownExpanded,
                        onExpandedChange = { isSoldeDropdownExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedSoldeStat,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Sélectionner une statistique") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isSoldeDropdownExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )

                        ExposedDropdownMenu(
                            expanded = isSoldeDropdownExpanded,
                            onDismissRequest = { isSoldeDropdownExpanded = false }
                        ) {
                            soldeOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        selectedSoldeStat = option
                                        isSoldeDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    when (val soldeStats = soldeStatsState.value) {
                        is UiState.Loading -> {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                        }

                        is UiState.Success -> {
                            Text(
                                text = when (selectedSoldeStat) {
                                    "Nombre de comptes" -> "Nombre de comptes: ${soldeStats.data.count}"
                                    "Total solde" -> "Total solde: ${
                                        String.format(
                                            "%.2f",
                                            soldeStats.data.sum
                                        )
                                    } DH"

                                    "Moyenne solde" -> "Moyenne solde: ${
                                        String.format(
                                            "%.2f",
                                            soldeStats.data.average
                                        )
                                    } DH"

                                    else -> ""
                                },
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        is UiState.Error -> {
                            Text(
                                "Erreur: ${soldeStats.message}",
                                color = Color.Red
                            )
                        }
                    }
                }
            }


            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Statistiques des Transactions",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    ExposedDropdownMenuBox(
                        expanded = isTransactionDropdownExpanded,
                        onExpandedChange = { isTransactionDropdownExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedTransactionStat,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Sélectionner une statistique") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isTransactionDropdownExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )

                        ExposedDropdownMenu(
                            expanded = isTransactionDropdownExpanded,
                            onDismissRequest = { isTransactionDropdownExpanded = false }
                        ) {
                            transactionOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        selectedTransactionStat = option
                                        isTransactionDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    when (val transactionStats = transactionStatsState.value) {
                        is UiState.Loading -> {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                        }

                        is UiState.Success -> {
                            Text(
                                text = when (selectedTransactionStat) {
                                    "Nombre de transactions" -> "Nombre de transactions: ${transactionStats.data.count}"
                                    "Total des dépôts" -> "Total des dépôts: ${
                                        String.format(
                                            "%.2f",
                                            transactionStats.data.sumDepots
                                        )
                                    } DH"

                                    "Total des retraits" -> "Total des retraits: ${
                                        String.format(
                                            "%.2f",
                                            transactionStats.data.sumRetraits
                                        )
                                    } DH"

                                    else -> ""
                                },
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        is UiState.Error -> {
                            Text(
                                "Erreur: ${transactionStats.message}",
                                color = Color.Red
                            )
                        }
                    }
                }
            }
        }
    }
}