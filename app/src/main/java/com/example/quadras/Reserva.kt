package com.example.quadras

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class Reserva(
    @SerialName("id") val id: String? = null,
    @SerialName("id_usuario") val idUsuario: String,
    @SerialName("id_quadra") val idQuadra: String,
    @SerialName("status") val status: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("hora_inicio") val horaInicio: String,
    @SerialName("hora_fim") val horaFim: String,
    @SerialName("cancelada_por") val canceladaPor: String? = null
)