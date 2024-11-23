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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.emsi.apollobankingapp.beans.Transaction
import com.emsi.apollobankingapp.state.UiState

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AccountTransactionListScreen(
    viewModel: MainViewModel,
    navController: NavController,
    compteId: Long
) {

    val transactionsState by viewModel.transactions.collectAsState()

    LaunchedEffect(compteId) {
        viewModel.fetchTransactionsByCompteId(compteId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFE5E5))
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {


            Text(
                text = "Transactions du compte $compteId",
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


            when (transactionsState) {
                is UiState.Loading -> Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

                is UiState.Success -> {
                    val transactions = (transactionsState as UiState.Success).data
                    if (transactions.isEmpty()) {
                        CommonUI.EmptyStateScreen(message = "Aucune transaction disponible.")
                    } else {
                        TransactionList(transactions = transactions)
                    }
                }

                is UiState.Error -> CommonUI.ErrorScreen(
                    message = (transactionsState as UiState.Error).message
                ) {
                    viewModel.fetchTransactionsByCompteId(compteId)
                }
            }
        }
    }
}


@Composable
fun TransactionList(
    transactions: List<Transaction>
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(transactions) { transaction ->
            TransactionListItem(transaction = transaction)
        }
    }
}

@Composable
fun TransactionListItem(transaction: Transaction) {


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

