package com.example.quadras

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class Quadra(
    @SerialName("id") val id: String,
    @SerialName("nome") val nome: String,
    @SerialName("tipo") val tipo: String,
    @SerialName("active") val active: Boolean
)
