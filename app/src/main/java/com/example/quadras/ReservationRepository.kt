package com.example.quadras

import android.util.Log
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email

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
}