package com.example.quadras

import android.util.Log
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.query.Order
import java.time.Instant

class ReservationRepository {

    // Motor do Postgrest através do nosso cliente central
    private val postgrest = SupabaseClient.instance.postgrest

    /**
    O withContext(Dispatchers.IO) garante que toda essa comunicação pesada com a internet rode em uma linha de execução de segundo plano, sem travar o celular do morador enquanto ele espera o app carregar.
     */

    suspend fun fazerLogin(email: String, contrasinal: String): String? = withContext(Dispatchers.IO) {
        try {
            // requisição de login no Supabase
            SupabaseClient.instance.auth.signInWith(Email) {
                this.email = email
                this.password = contrasinal
            }

            // Se deu certo, recupera o UID gerado na autenticação
            val usuarioAtual = SupabaseClient.instance.auth.currentUserOrNull()
            return@withContext usuarioAtual?.id
        } catch (e: Exception) {
            Log.e("SUPABASE_TESTE", "[AUTH ERROR] Erro nas credenciais: ${e.message}")
            null
        }
    }
    suspend fun obterQuadras(): List<Quadra> = withContext(Dispatchers.IO) {

        val resultado = postgrest.from("quadras").select()

        // A SDK do Supabase decodifica o JSON automaticamente para uma lista de Quadras
        return@withContext resultado.decodeList<Quadra>()
    }

    suspend fun cadastrarReserva(reserva: Reserva) = withContext(Dispatchers.IO) {
        postgrest.from("reservas").insert(reserva)
    }

    suspend fun obterReservasDoUsuario(idUsuario: String): List<Reserva> = withContext(Dispatchers.IO) {
        val resultado = postgrest.from("reservas")
            .select {
                filter {
                    eq("id_usuario", idUsuario)
                }
            }
        return@withContext resultado.decodeList<Reserva>()
    }

    suspend fun obterProximaReserva(idUsuario: String) : Reserva? = withContext(Dispatchers.IO) {
        try {
            // Captura a data atual compatível com qualquer versão do Android (API 24+)
            val formatador = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.US).apply {
                timeZone = java.util.TimeZone.getTimeZone("UTC")
            }
            val dataAtual = formatador.format(java.util.Date())

            val proximaReserva = SupabaseClient.instance.postgrest["reservas"]
                .select {
                    filter {
                        eq("id_usuario", idUsuario)
                        gte("hora_inicio", dataAtual)
                    }
                    order(column = "hora_inicio", order = Order.ASCENDING)
                    limit(count = 1)
                }.decodeList<Reserva>()
            return@withContext proximaReserva.firstOrNull()
        } catch (e: Exception) {
            Log.e("SUPABASE_HOME", "ERRO AO BUSCAR PROXIMA RESERVA: ${e.message}",e)
            null
        }
    }

    suspend fun obterQuadra(idQuadra: String) : Quadra? = withContext(Dispatchers.IO) {
        try {
            val quadra = SupabaseClient.instance.postgrest["quadras"]
                .select {
                    filter {
                        eq("id", idQuadra)
                    }
                }.decodeSingleOrNull<Quadra>()
            return@withContext quadra
        } catch (e: Exception) {
            Log.e("SUPABASE_REPOSITORY", "Erro ao buscar quadra por id: ${e.message}")
            null
        }
    }

    suspend fun obterReservasQuardaNoDia(idQuadra: String, dataFormatada: String): List<Reserva> = withContext(
        Dispatchers.IO){
        try {
            // Conversão simples: "26/05/2026" vira o início e fim do dia no formato ISO salvo no Banco
            val partes = dataFormatada.split("/")
            val dataIsoBase = "${partes[2]}-${partes[1]}-${partes[0]}" // 2026-05-26

            val inicioDia = "${dataIsoBase}T00:00:00Z"
            val fimDia = "${dataIsoBase}T23:59:59Z"

            return@withContext SupabaseClient.instance.postgrest["reservas"]
                .select {
                    filter {
                        eq("id_quadra", idQuadra)
                        gte("hora_inicio", inicioDia)
                        lte("hora_inicio", fimDia)
                    }
                }.decodeList<Reserva>()
        } catch (e: Exception) {
            Log.e("SUPABASE_AGENDA", "Erro ao buscar reservas do dia: ${e.message}")
            emptyList()
        }
    }
}