package com.example.quadras

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.io.Serializable as JavaSerializable

@Serializable
data class Quadra(
    @SerialName("id") val id: String,
    @SerialName("nome") val nome: String,
    @SerialName("tipo") val tipo: String,
    @SerialName("active") val active: Boolean
) : JavaSerializable
