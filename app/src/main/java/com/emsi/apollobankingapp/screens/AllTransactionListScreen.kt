package com.emsi.apollobankingapp.screens

import MainViewModel
import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.emsi.apollobankingapp.beans.Transaction
import com.emsi.apollobankingapp.beans.TypeTransaction
import com.emsi.apollobankingapp.state.UiState

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AllTransactionListScreen(
    viewModel: MainViewModel,
    navController: NavController
) {
    val transactionsState by viewModel.transactions.collectAsState()
    var selectedFilter by remember { mutableStateOf("Aucun") }
    var selectedType by remember { mutableStateOf<TypeTransaction?>(null) }
    var accountId by remember { mutableStateOf("") }
    var filteredTransactions by remember { mutableStateOf<List<Transaction>>(emptyList()) }


    LaunchedEffect(Unit) {
        viewModel.fetchAllTransactions()
    }


    LaunchedEffect(transactionsState) {
        if (transactionsState is UiState.Success<*>) {
            filteredTransactions = (transactionsState as UiState.Success).data as List<Transaction>
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFFFE5E5))) {
        Column(modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()) {

            Text(
                text = "Toutes les transactions",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontSize = 24.sp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Filtrer par:")
            var expandedDropdown by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = expandedDropdown,
                onExpandedChange = { expandedDropdown = !expandedDropdown },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedFilter,
                    onValueChange = { selectedFilter = it },
                    label = { Text("Choisir un filtre") },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expandedDropdown,
                    onDismissRequest = { expandedDropdown = false }
                ) {
                    listOf("Aucun", "Type", "Compte", "Les deux").forEach { filterOption ->
                        DropdownMenuItem(
                            text = { Text(filterOption) },
                            onClick = {
                                selectedFilter = filterOption
                                expandedDropdown = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))


            if (selectedFilter == "Type" || selectedFilter == "Les deux") {
                TypeTransactionDropdown(selectedType) { selectedType = it }
                Spacer(modifier = Modifier.height(16.dp))
            }


            if (selectedFilter == "Compte" || selectedFilter == "Les deux") {
                AccountIdTextField(accountId) { accountId = it }
                Spacer(modifier = Modifier.height(16.dp))
            }


            Button(
                onClick = {
                    when (selectedFilter) {
                        "Type" -> selectedType?.let { viewModel.fetchTransactionsByType(it) }
                        "Compte" -> if (accountId.isNotEmpty()) {
                            viewModel.fetchTransactionsByCompteId(accountId.toLong())
                        }

                        "Les deux" -> {
                            selectedType?.let { type ->
                                if (accountId.isNotEmpty()) {
                                    viewModel.fetchTransactionsByTypeAndAccountId(
                                        type,
                                        accountId.toLong()
                                    )
                                }
                            }
                        }

                        else -> viewModel.fetchAllTransactions()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Appliquer")
            }

            Spacer(modifier = Modifier.height(16.dp))


            when (transactionsState) {
                is UiState.Loading -> Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

                is UiState.Success<*> -> {
                    val transactions = filteredTransactions
                    if (transactions.isEmpty()) {
                        CommonUI.EmptyStateScreen(message = "Aucune transaction disponible.")
                    } else {
                        AllTransactionList(transactions = transactions)
                    }
                }

                is UiState.Error -> CommonUI.ErrorScreen(
                    message = (transactionsState as UiState.Error).message
                ) {
                    viewModel.fetchAllTransactions()
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TypeTransactionDropdown(
    selectedType: TypeTransaction?,
    onTypeSelected: (TypeTransaction) -> Unit
) {
    val types = TypeTransaction.values()
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedType?.name ?: "Sélectionner un type",
            onValueChange = {},
            label = { Text("Sélectionner un type") },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            types.forEach { type ->
                DropdownMenuItem(
                    text = { Text(type.name) },
                    onClick = {
                        onTypeSelected(type)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun AccountIdTextField(accountId: String, onAccountIdChange: (String) -> Unit) {
    OutlinedTextField(
        value = accountId,
        onValueChange = onAccountIdChange,
        label = { Text("ID du compte") },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun AllTransactionList(
    transactions: List<Transaction>
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(transactions) { transaction ->
            AllTransactionListItem(transaction = transaction)
        }
    }
}

@Composable
fun AllTransactionListItem(transaction: Transaction) {
    val depotColor = Color(0xFF347928)
    val transactionTypeColor = when (transaction.type.toString()) {
        "DEPOT" -> depotColor
        "RETRAIT" -> Color.Red
        else -> Color.Black
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .shadow(8.dp, MaterialTheme.shapes.large),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Identifiant: ${transaction.id}",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Montant: ${String.format("%.2f", transaction.montant)}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Date: ${transaction.date}",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Type: ${transaction.type}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = transactionTypeColor
                    )
                )
            }
        }
    }
}
