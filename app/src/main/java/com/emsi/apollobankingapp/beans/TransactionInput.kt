package com.emsi.apollobankingapp.beans

data class TransactionInput(
    val compteId: Long,
    val montant: Double,
    val date: String,
    val type: TypeTransaction
)