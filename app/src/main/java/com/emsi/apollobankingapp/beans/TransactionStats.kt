package com.emsi.apollobankingapp.beans

data class TransactionStats(
    val count: Int,
    val sumDepots: Double,
    val sumRetraits: Double
)