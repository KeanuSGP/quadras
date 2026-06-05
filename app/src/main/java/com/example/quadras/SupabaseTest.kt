package com.example.quadras
import android.util.Log

class SupabaseTest {

    private val repository = ReservationRepository()

    // O metodo principal que vai coordenar a ordem dos testes
    suspend fun rodarTodosOsTestes(uidLogado: String) {
        Log.d("SUPABASE_TESTE", "=== INICIANDO SEQUÊNCIA DE TESTES DO SUPABASE ===")

        testarObterQuadras()
        testarCadastrarReserva(uidLogado)
        testarObterReservasDoUsuario(uidLogado)
    }

    private suspend fun testarObterQuadras() {
        try {
            Log.d("SUPABASE_TESTE", "[GET] Tentando buscar quadras...")
            val listaQuadras = repository.obterQuadras()

            Log.d("SUPABASE_TESTE", "[GET] Sucesso! Quadras encontradas: ${listaQuadras.size}")
            listaQuadras.forEach { quadra ->
                Log.d("SUPABASE_TESTE", "   -> ID: ${quadra.id} | Nome: ${quadra.nome} | Tipo: ${quadra.tipo}")
            }
        } catch (e: Exception) {
            Log.e("SUPABASE_TESTE", "[ERROR GET QUADRAS] Falha: ${e.message}", e)
        }
    }

    private suspend fun testarCadastrarReserva(uidLogado: String) {
        try {
            Log.d("SUPABASE_TESTE", "[POST] Tentando inserir uma nova reserva...")

            val novaReserva = Reserva(
                idUsuario = uidLogado,
                idQuadra = "91d2b906-2300-4bcb-bb92-73e19538895c", // ID da quadra poliesportiva 3
                horaInicio = "2026-06-12 18:00:00+00",
                horaFim = "2026-06-12 19:00:00+00"
            )

            repository.cadastrarReserva(novaReserva)
            Log.d("SUPABASE_TESTE", "[POST] Sucesso! Nova linha gravada no Supabase.")
        } catch (e: Exception) {
            Log.e("SUPABASE_TESTE", "[ERROR POST RESERVA] Falha: ${e.message}", e)
        }
    }

    private suspend fun testarObterReservasDoUsuario(uidLogado: String) {
        try {
            Log.d("SUPABASE_TESTE", "[GET WHERE] Buscando reservas do usuário: $uidLogado")

            val listaReservas = repository.obterReservasDoUsuario(uidLogado, "")
            Log.d("SUPABASE_TESTE", "[GET WHERE] Sucesso! Reservas encontradas: ${listaReservas.size}")
            listaReservas.forEach { reserva ->
                Log.d("SUPABASE_TESTE", "   -> Reserva ID: ${reserva.id} | Quadra: ${reserva.idQuadra} | Início: ${reserva.horaInicio}")
            }
        } catch (e: Exception) {
            Log.e("SUPABASE_TESTE", "[ERROR GET WHERE] Falha: ${e.message}", e)
        }
    }
}