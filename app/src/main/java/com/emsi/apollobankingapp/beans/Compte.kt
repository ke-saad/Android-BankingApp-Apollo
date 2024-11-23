import com.emsi.apollobankingapp.beans.TypeCompte

data class Compte(
    val id: Long,

    val solde: Double,

    val dateCreation: String,

    val type: TypeCompte
)