package com.example.quadras

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class Usuario(
    @SerialName("id") val id: String,
    @SerialName("nome") val nome: String,
    @SerialName("num_apt") val numApt: String,
    @SerialName("bloco_apt") val blocoApt: String,
    @SerialName("admin") val admin: Boolean,
    @SerialName("fcm_token") val fcmToken: String? = null
)
