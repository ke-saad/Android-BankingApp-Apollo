package com.emsi.apollobankingapp.beans

import Compte

data class Transaction(
    val id: Long,
    val montant: Double,
    val date: String,
    val type: TypeTransaction,
    val compte: Compte
)