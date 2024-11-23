package com.emsi.apollobankingapp.screens

import MainViewModel
import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.emsi.apollobankingapp.beans.TransactionInput
import com.emsi.apollobankingapp.beans.TypeTransaction
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTransactionScreen(viewModel: MainViewModel) {
    var compteId by remember { mutableStateOf("") }
    var montant by remember { mutableStateOf("") }
    var transactionType by remember { mutableStateOf(TypeTransaction.DEPOT) }
    var isCreating by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFEBEE))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            OutlinedTextField(
                value = compteId,
                onValueChange = { compteId = it },
                label = { Text("Compte ID") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = montant,
                onValueChange = { montant = it },
                label = { Text("Montant") },
                modifier = Modifier.fillMaxWidth(),

                )

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = transactionType == TypeTransaction.DEPOT,
                    onClick = { transactionType = TypeTransaction.DEPOT }
                )
                Text("Dépot")
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(
                    selected = transactionType == TypeTransaction.RETRAIT,
                    onClick = { transactionType = TypeTransaction.RETRAIT }
                )
                Text("Retrait")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Date : $formattedDate",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                shape = RoundedCornerShape(0.dp),
                onClick = {
                    val compteIdLong = compteId.toLongOrNull()
                    val montantDouble = montant.toDoubleOrNull()

                    if (compteIdLong == null) {
                        scope.launch {
                            snackbarHostState.showSnackbar("ID de compte invalide.")
                        }
                        return@Button
                    }
                    if (montantDouble == null) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Montant invalide.")
                        }
                        return@Button
                    }

                    val transactionInput = TransactionInput(
                        compteId = compteIdLong,
                        montant = montantDouble,
                        date = formattedDate,
                        type = transactionType
                    )

                    isCreating = true

                    viewModel.createTransaction(transactionInput)

                    scope.launch {
                        snackbarHostState.showSnackbar("Transaction créée avec succès!")
                        isCreating = false
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                enabled = !isCreating
            ) {
                Text(
                    text = if (isCreating) "Création en cours..." else "Soumettre",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            SnackbarHost(hostState = snackbarHostState)
        }
    }
}