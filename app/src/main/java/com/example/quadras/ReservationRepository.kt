package com.example.quadras

import android.util.Log
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
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

    suspend fun cadastrarReserva(reserva: Reserva): Boolean = withContext(Dispatchers.IO) {
        try {
            postgrest.from("reservas").insert(reserva)
            true

        } catch (e: Exception) {
            Log.e("SUPABASE_RESERVA", "Erro ao salvar reserva no banco: ${e.message}", e)
            false
        }
    }

    suspend fun obterReservasDoUsuario(idUsuario: String, horaAtual: String): List<Reserva> = withContext(Dispatchers.IO) {
        val resultado = postgrest.from("reservas")
            .select {
                filter {
                    eq("id_usuario", idUsuario)
                    gte("hora_inicio", horaAtual)
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

            val inicioDia = "${dataIsoBase}T00:00:00"
            val fimDia = "${dataIsoBase}T23:59:59"

            Log.d("Data: ", inicioDia)
            Log.d("Data Fim: ", fimDia)
            Log.d("ID QUADRA: ", idQuadra)

            return@withContext SupabaseClient.instance.postgrest["reservas"]
                .select {
                    filter {
                        eq("id_quadra", idQuadra)
                        gte("hora_inicio", inicioDia)
                        lte("hora_fim", fimDia)
                    }
                }.decodeList<Reserva>()
        } catch (e: Exception) {
            Log.e("SUPABASE_AGENDA", "Erro ao buscar reservas do dia: ${e.message}")
            emptyList()
        }
    }

    suspend fun deletarReserva(idReserva: String) {
        try {
            SupabaseClient.instance.postgrest["reservas"].delete {
                filter {
                    eq("id", idReserva)
                }
            }
        } catch (e: Exception) {
            Log.d("DELETAR RESERVA", "Erro ao tentar deletar reserva: ${e.message}")
        }
    }

    suspend fun ehAdmin(idUsuario: String): Boolean {
        try {
            val usuario = SupabaseClient.instance.postgrest["usuarios"].select {
                filter {
                    eq("id", idUsuario)
                }
            }.decodeSingle<Usuario>()
            return usuario.admin
        } catch (e: Exception) {
            Log.d("Erro verificação de admin", e.message.toString())
            return false
        }
    }
}