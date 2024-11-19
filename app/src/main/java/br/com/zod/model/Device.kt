package br.com.zod.model

data class Device(
    val nome: String,
    val watts: Int,
    val descricao: String?,
    val horasPorDia: Int,
    val diasNoMes: Int,
    val precoKWh: Double
)
