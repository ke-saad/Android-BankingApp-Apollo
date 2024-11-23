package com.emsi.apollobankingapp.screens

import Compte
import MainViewModel
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.emsi.apollobankingapp.state.UiState

@Composable
fun AccountDetailsScreen(
    viewModel: MainViewModel,
    compteId: Long,
    navController: NavController
) {
    var compte by remember { mutableStateOf<Compte?>(null) }
    var showSnackbar by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(compteId) {
        viewModel.fetchCompteById(compteId)
    }

    val compteState by viewModel.compte.collectAsState()

    LaunchedEffect(compteState) {
        val currentState = compteState
        if (currentState is UiState.Success<*>) {
            compte = currentState.data as Compte
        } else if (currentState is UiState.Error) {
            showSnackbar = true
            snackbarHostState.showSnackbar(currentState.message)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8F8))
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Détails du compte",
                color = Color(0xFFB71C1C),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 24.dp),
                fontSize = 28.sp
            )

            if (compte != null) {

                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Identifiant:",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        "${compte?.id}",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))


                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Solde: ",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        "${compte?.solde}",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))


                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Type: ",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        "${compte?.type}",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))


                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Crée en: ",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        "${compte?.dateCreation}",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))


                Button(
                    onClick = { navController.navigate("account_transactions_list/${compte?.id}") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0A3981)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "Voir les transactions du compte",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}
