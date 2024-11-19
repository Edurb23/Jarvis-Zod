package br.com.zod.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("NM_USUARIO")
    val nome: String?,
    @SerializedName("NR_CPF")
    val cpf: String?,
    @SerializedName("NR_RG")
    val rg: String?,
    @SerializedName("DT_DATANASCIMENTO")
    val dataNascimento: String?,
    @SerializedName("DS_EMAIL")
    val email: String?,
    val telefone: String?
)


