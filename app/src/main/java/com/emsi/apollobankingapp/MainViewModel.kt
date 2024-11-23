import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.ApolloClient
import com.emsi.apollobankingapp.AddTransactionMutation
import com.emsi.apollobankingapp.GetAllTransactionsQuery
import com.emsi.apollobankingapp.GetCompteByIdQuery
import com.emsi.apollobankingapp.GetComptesQuery
import com.emsi.apollobankingapp.GetTotalSoldeQuery
import com.emsi.apollobankingapp.GetTransactionStatsQuery
import com.emsi.apollobankingapp.GetTransactionsByCompteIdQuery
import com.emsi.apollobankingapp.GetTransactionsByTypeAndAccountIdQuery
import com.emsi.apollobankingapp.GetTransactionsByTypeQuery
import com.emsi.apollobankingapp.SaveCompteMutation
import com.emsi.apollobankingapp.beans.SoldeStats
import com.emsi.apollobankingapp.beans.Transaction
import com.emsi.apollobankingapp.beans.TransactionInput
import com.emsi.apollobankingapp.beans.TransactionStats
import com.emsi.apollobankingapp.beans.TypeCompte
import com.emsi.apollobankingapp.beans.TypeTransaction
import com.emsi.apollobankingapp.state.UiState
import com.emsi.apollobankingapp.type.CompteRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(private val apolloClient: ApolloClient) : ViewModel() {

    private val _comptes = MutableStateFlow<UiState<List<Compte>>>(UiState.Loading)
    val comptes: StateFlow<UiState<List<Compte>>> = _comptes

    private val _compte = MutableStateFlow<UiState<Compte>>(UiState.Loading)
    val compte: StateFlow<UiState<Compte>> = _compte

    private val _transactions = MutableStateFlow<UiState<List<Transaction>>>(UiState.Loading)
    val transactions: StateFlow<UiState<List<Transaction>>> = _transactions

    private val _transactionStats = MutableStateFlow<UiState<TransactionStats>>(UiState.Loading)
    val transactionStats: StateFlow<UiState<TransactionStats>> = _transactionStats

    private val _soldeStats = MutableStateFlow<UiState<SoldeStats>>(UiState.Loading)
    val soldeStats: StateFlow<UiState<SoldeStats>> = _soldeStats


    private val _totalSolde = MutableStateFlow<UiState<SoldeStats>>(UiState.Loading)
    val totalSolde: StateFlow<UiState<SoldeStats>> = _totalSolde

    fun fetchComptes() {
        viewModelScope.launch {
            _comptes.value = UiState.Loading
            try {
                val response = apolloClient.query(GetComptesQuery()).execute()
                val comptesList = response.data?.allComptes?.mapNotNull { it ->
                    it?.let {
                        Compte(
                            id = it.id?.toLong() ?: 0L,
                            solde = it.solde ?: 0.0,
                            dateCreation = it.dateCreation.orEmpty(),
                            type = mapApolloToAppTypeCompte(it.type)
                        )
                    }
                } ?: emptyList()

                _comptes.value = if (comptesList.isEmpty()) {
                    UiState.Error("No accounts found.")
                } else {
                    UiState.Success(comptesList)
                }
            } catch (e: Exception) {
                _comptes.value = UiState.Error("Error: ${e.message}")
                Log.e("MainViewModel", "Error fetching accounts", e)
            }
        }
    }


    fun fetchCompteById(compteId: Long) {
        viewModelScope.launch {
            _compte.value = UiState.Loading
            try {
                val response =
                    apolloClient.query(GetCompteByIdQuery(id = compteId.toString())).execute()
                val apiCompte = response.data?.compteById

                if (apiCompte != null) {
                    val mappedCompte = Compte(
                        id = apiCompte.id.toLong(),
                        solde = apiCompte.solde,
                        dateCreation = apiCompte.dateCreation,
                        type = mapApolloToAppTypeCompte(apiCompte.type)
                    )
                    _compte.value = UiState.Success(mappedCompte)
                } else {
                    _compte.value = UiState.Error("Account not found.")
                }
            } catch (e: Exception) {
                _compte.value = UiState.Error("Error: ${e.message}")
                Log.e("MainViewModel", "Error fetching account by ID", e)
            }
        }
    }

    fun createCompte(solde: Double, dateCreation: String, type: TypeCompte) {
        viewModelScope.launch {
            _comptes.value = UiState.Loading
            try {
                Log.d("CreateAccount", "Creating account with date: $dateCreation, type: $type")
                val compteInput = CompteRequest(
                    solde = solde,
                    dateCreation = dateCreation,
                    type = mapAppToApolloTypeCompte(type)
                )

                val response =
                    apolloClient.mutation(SaveCompteMutation(compte = compteInput)).execute()


            } catch (e: Exception) {
                _comptes.value = UiState.Error("Error: ${e.message}")
                Log.e("MainViewModel", "Error creating account", e)
            }
        }
    }


    fun fetchTransactionStats() {
        viewModelScope.launch {
            _transactionStats.value = UiState.Loading
            try {
                val response = apolloClient.query(GetTransactionStatsQuery()).execute()
                val stats = response.data?.transactionStats

                if (stats != null) {
                    _transactionStats.value = UiState.Success(
                        TransactionStats(
                            count = stats.count,
                            sumDepots = stats.sumDepots,
                            sumRetraits = stats.sumRetraits
                        )
                    )
                } else {
                    _transactionStats.value = UiState.Error("Failed to fetch transaction stats.")
                }
            } catch (e: Exception) {
                _transactionStats.value = UiState.Error("Error: ${e.message}")
                Log.e("MainViewModel", "Error fetching transaction stats", e)
            }
        }
    }


    fun fetchTotalSolde() {
        viewModelScope.launch {
            _soldeStats.value = UiState.Loading
            try {
                val response = apolloClient.query(GetTotalSoldeQuery()).execute()
                val stats = response.data?.totalSolde

                if (stats != null) {
                    _soldeStats.value = UiState.Success(
                        SoldeStats(
                            count = stats.count, sum = stats.sum, average = stats.average
                        )
                    )
                } else {
                    _soldeStats.value = UiState.Error("Failed to fetch solde stats.")
                }
            } catch (e: Exception) {
                _soldeStats.value = UiState.Error("Error: ${e.message}")
                Log.e("MainViewModel", "Error fetching solde stats", e)
            }
        }
    }

    fun mapApolloToAppTypeCompte(apolloType: com.emsi.apollobankingapp.type.TypeCompte): com.emsi.apollobankingapp.beans.TypeCompte {
        return when (apolloType) {
            com.emsi.apollobankingapp.type.TypeCompte.COURANT -> com.emsi.apollobankingapp.beans.TypeCompte.COURANT
            com.emsi.apollobankingapp.type.TypeCompte.EPARGNE -> com.emsi.apollobankingapp.beans.TypeCompte.EPARGNE
            else -> throw IllegalArgumentException("Unknown account type: $apolloType")
        }
    }


    fun mapApolloToAppTypeTransaction(apolloType: com.emsi.apollobankingapp.type.TypeTransaction): com.emsi.apollobankingapp.beans.TypeTransaction {
        return when (apolloType) {
            com.emsi.apollobankingapp.type.TypeTransaction.DEPOT -> com.emsi.apollobankingapp.beans.TypeTransaction.DEPOT
            com.emsi.apollobankingapp.type.TypeTransaction.RETRAIT -> com.emsi.apollobankingapp.beans.TypeTransaction.RETRAIT
            else -> throw IllegalArgumentException("Unknown account type: $apolloType")
        }
    }

    fun mapAppToApolloTypeTransaction(appType: com.emsi.apollobankingapp.beans.TypeTransaction): com.emsi.apollobankingapp.type.TypeTransaction {
        return when (appType) {
            com.emsi.apollobankingapp.beans.TypeTransaction.DEPOT -> com.emsi.apollobankingapp.type.TypeTransaction.DEPOT
            com.emsi.apollobankingapp.beans.TypeTransaction.RETRAIT -> com.emsi.apollobankingapp.type.TypeTransaction.RETRAIT
            else -> throw IllegalArgumentException("Unknown account type: $appType")
        }
    }

    fun mapAppToApolloTypeCompte(appType: com.emsi.apollobankingapp.beans.TypeCompte): com.emsi.apollobankingapp.type.TypeCompte {
        return when (appType) {
            com.emsi.apollobankingapp.beans.TypeCompte.COURANT -> com.emsi.apollobankingapp.type.TypeCompte.COURANT
            com.emsi.apollobankingapp.beans.TypeCompte.EPARGNE -> com.emsi.apollobankingapp.type.TypeCompte.EPARGNE
            else -> throw IllegalArgumentException("Unknown account type: $appType")
        }
    }

    fun createTransaction(newTransaction: TransactionInput) {
        viewModelScope.launch {
            _transactions.value = UiState.Loading
            try {
                val response = apolloClient.mutation(
                    AddTransactionMutation(
                        compteId = newTransaction.compteId.toString(),
                        montant = newTransaction.montant,
                        date = newTransaction.date,
                        type = mapAppToApolloTypeTransaction(newTransaction.type)
                    )
                ).execute()

                val createdTransaction = response.data?.addTransaction
                if (createdTransaction != null) {
                    val compteResponse =
                        apolloClient.query(GetCompteByIdQuery(id = newTransaction.compteId.toString()))
                            .execute()
                    val apiCompte = compteResponse.data?.compteById

                    val compte = if (apiCompte != null) {
                        Compte(
                            id = apiCompte.id.toLong(),
                            solde = apiCompte.solde,
                            dateCreation = apiCompte.dateCreation,
                            type = mapApolloToAppTypeCompte(apiCompte.type)
                        )
                    } else {

                        null
                    }


                    if (compte != null) {
                        _transactions.value = UiState.Success(
                            listOf(
                                Transaction(
                                    id = createdTransaction.id.toLong(),
                                    montant = createdTransaction.montant,
                                    date = createdTransaction.date,
                                    type = mapApolloToAppTypeTransaction(createdTransaction.type),
                                    compte = compte
                                )
                            )
                        )
                    } else {
                        _transactions.value = UiState.Error("Failed to fetch compte details.")
                    }
                } else {
                    _transactions.value = UiState.Error("Failed to create transaction.")
                }
            } catch (e: Exception) {
                _transactions.value = UiState.Error("Error: ${e.message}")
                Log.e("MainViewModel", "Error creating transaction", e)
            }
        }
    }

    fun fetchAllTransactions() {
        viewModelScope.launch {
            _transactions.value = UiState.Loading
            try {
                val response = apolloClient.query(GetAllTransactionsQuery()).execute()

                val transactionsList = response.data?.allTransactions?.mapNotNull { transaction ->
                    transaction?.let {
                        Transaction(id = it.id.toLong(),
                            montant = it.montant,
                            date = it.date,
                            type = it.type?.let { type -> mapApolloToAppTypeTransaction(type) }
                                ?: throw IllegalArgumentException("Transaction type is null"),
                            compte = it.compte?.let { compte ->
                                Compte(id = compte.id.toLong(),
                                    solde = compte.solde,
                                    dateCreation = compte.dateCreation,
                                    type = compte.type?.let { type -> mapApolloToAppTypeCompte(type) }
                                        ?: throw IllegalArgumentException("Compte type is null"))
                            } ?: throw IllegalArgumentException("Compte is null"))
                    }
                } ?: emptyList()

                _transactions.value = if (transactionsList.isEmpty()) {
                    UiState.Error("No transactions found.")
                } else {
                    UiState.Success(transactionsList)
                }
            } catch (e: Exception) {
                _transactions.value = UiState.Error("Error: ${e.message}")
                Log.e("MainViewModel", "Error fetching all transactions", e)
            }
        }
    }


    fun fetchTransactionsByCompteId(compteId: Long) {
        viewModelScope.launch {
            _transactions.value = UiState.Loading
            try {
                val response =
                    apolloClient.query(GetTransactionsByCompteIdQuery(compteId = compteId.toString()))
                        .execute()

                val transactionsList =
                    response.data?.compteTransactions?.mapNotNull { transaction ->
                        transaction?.let {
                            Transaction(id = it.id.toLong(),
                                montant = it.montant,
                                date = it.date,
                                type = it.type?.let { type -> mapApolloToAppTypeTransaction(type) }
                                    ?: throw IllegalArgumentException("Type is null"),
                                compte = it.compte?.let { compte ->
                                    Compte(id = compte.id.toLong(),
                                        solde = compte.solde,
                                        dateCreation = compte.dateCreation,
                                        type = compte.type?.let { type ->
                                            mapApolloToAppTypeCompte(type)
                                        } ?: throw IllegalArgumentException("Type is null"))
                                } ?: throw IllegalArgumentException("Compte is null"))
                        }
                    } ?: emptyList()

                _transactions.value = if (transactionsList.isEmpty()) {
                    UiState.Error("No transactions found.")
                } else {
                    UiState.Success(transactionsList)
                }
            } catch (e: Exception) {
                _transactions.value = UiState.Error("Error: ${e.message}")
                Log.e("MainViewModel", "Error fetching transactions", e)
            }
        }
    }

    fun fetchTransactionsByType(type: com.emsi.apollobankingapp.beans.TypeTransaction) {
        viewModelScope.launch {
            _transactions.value = UiState.Loading
            try {

                val response = apolloClient.query(
                    GetTransactionsByTypeQuery(type = mapAppToApolloTypeTransaction(type))
                ).execute()


                val transactionsList =
                    response.data?.transactionsByType?.mapNotNull { transaction ->
                        transaction?.let {
                            Transaction(id = it.id.toLong(),
                                montant = it.montant,
                                date = it.date,
                                type = it.type?.let { type -> mapApolloToAppTypeTransaction(type) }
                                    ?: throw IllegalArgumentException("Transaction type is null"),
                                compte = it.compte?.let { compte ->
                                    Compte(id = compte.id.toLong(),
                                        solde = compte.solde,
                                        dateCreation = compte.dateCreation,
                                        type = compte.type?.let { type ->
                                            mapApolloToAppTypeCompte(
                                                type
                                            )
                                        } ?: throw IllegalArgumentException("Compte type is null"))
                                } ?: throw IllegalArgumentException("Compte is null"))
                        }
                    } ?: emptyList()


                _transactions.value = if (transactionsList.isEmpty()) {
                    UiState.Error("No transactions of type $type found.")
                } else {
                    UiState.Success(transactionsList)
                }
            } catch (e: Exception) {

                _transactions.value = UiState.Error("Error: ${e.message}")
                Log.e("MainViewModel", "Error fetching transactions by type", e)
            }
        }
    }

    fun fetchTransactionsByTypeAndAccountId(type: TypeTransaction, accountId: Long) {
        viewModelScope.launch {
            try {
                _transactions.value = UiState.Loading

                val response = apolloClient.query(
                    GetTransactionsByTypeAndAccountIdQuery(
                        type = mapAppToApolloTypeTransaction(type), accountId = accountId.toString()
                    )
                ).execute()


                val transactionsList =
                    response.data?.allTransactionsByTypeAndAccountId?.mapNotNull { transaction ->
                        transaction?.let {

                            Transaction(id = it.id.toLong(),
                                montant = it.montant,
                                date = it.date,
                                type = mapApolloToAppTypeTransaction(it.type),
                                compte = it.compte?.let { compte ->
                                    Compte(
                                        id = compte.id.toLong(),
                                        solde = compte.solde,
                                        dateCreation = compte.dateCreation,
                                        type = mapApolloToAppTypeCompte(compte.type)
                                    )
                                } ?: throw IllegalArgumentException("Compte is null"))
                        }
                    } ?: emptyList()


                _transactions.value = if (transactionsList.isEmpty()) {
                    UiState.Error("No transactions found for type $type and account ID $accountId.")
                } else {
                    UiState.Success(transactionsList)
                }
            } catch (e: Exception) {

                _transactions.value = UiState.Error("Error: ${e.message}")
                Log.e("MainViewModel", "Error fetching transactions by type and account ID", e)
            }
        }
    }


}


