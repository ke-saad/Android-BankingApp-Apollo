package com.emsi.apollobankingapp.screens

import Compte
import MainViewModel
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emsi.apollobankingapp.beans.TypeCompte
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAccountScreen(viewModel: MainViewModel) {
    var solde by remember { mutableStateOf("") }
    var accountType by remember { mutableStateOf(TypeCompte.COURANT) }
    var isCreating by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var expanded by remember { mutableStateOf(false) }

    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val formattedDate = sdf.format(Date())

    fun formatSolde(value: String): String {
        return try {
            if (value.contains(".")) {
                val parts = value.split(".")
                if (parts.size == 2) {
                    parts[0] + "." + parts[1].take(2)
                } else {
                    value + ".00"
                }
            } else {
                value + ".00"
            }
        } catch (e: Exception) {
            value
        }
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
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Ajouter un compte",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontSize = 24.sp
                ),
                modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = solde,
                onValueChange = { solde = formatSolde(it) },
                label = { Text("Solde") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                ),
                visualTransformation = VisualTransformation.None
            )

            Spacer(modifier = Modifier.height(16.dp))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = accountType.name,
                    onValueChange = {},
                    label = { Text("Type de Compte") },
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    modifier = Modifier.menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    TypeCompte.values().forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption.name) },
                            onClick = {
                                accountType = selectionOption
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Créé le : $formattedDate",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFFC62828))
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (solde.isEmpty()) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Le solde ne peut pas être vide.")
                        }
                        return@Button
                    }

                    val balanceAmount = solde.toDoubleOrNull()?.let {
                        if (it >= 0) it else null
                    } ?: run {
                        scope.launch {
                            snackbarHostState.showSnackbar("Valeur de solde invalide.")
                        }
                        return@Button
                    }

                    val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

                    val compteToSave = Compte(
                        id = 0,
                        solde = balanceAmount,
                        dateCreation = formattedDate,
                        type = accountType
                    )

                    Log.d("CreateAccount", "Balance amount before creation: $balanceAmount")

                    isCreating = true

                    viewModel.createCompte(
                        compteToSave.solde,
                        compteToSave.dateCreation,
                        compteToSave.type
                    )

                    scope.launch {
                        snackbarHostState.showSnackbar("Compte créé avec succès!")
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
