package com.example.quadras

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import io.github.jan.supabase.gotrue.auth
import kotlinx.coroutines.launch

class ActivityConfirmarReserva : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_confirmar_reserva)
        // Dentro do onCreate da ActivityRevisaoReserva.kt

// 1. Recupera o objeto Reserva e o nome da quadra vindo da Intent
        val reservaProvisoria = intent.getSerializableExtra("objeto_reserva") as? Reserva
        val nomeQuadra = intent.getStringExtra("nome_quadra") ?: "Quadra"

        //recuperar elementos
        val txtConfirmarQuadra = findViewById<TextView>(R.id.txtConfirmarQuadra)
        val txtData = findViewById<TextView>(R.id.txtConfirmarDia)
        val txtHorario= findViewById<TextView>(R.id.txtConfirmarHorario)
        val btnConfirmar = findViewById<Button>(R.id.btnConfirmar)

        //funccoes do banco
        val repository = ReservationRepository()

        if (reservaProvisoria != null) {

            // 2. Extrai as horas de dentro da String ISO para mostrar nos TextViews da tela (ex: puxa o "10" de "2026-06-15T10:00...")
            val horaIncExibicao = reservaProvisoria.horaInicio.substring(11, 13)
            val horaFimExibicao = reservaProvisoria.horaFim.substring(11, 13)
            val dataExibicao = reservaProvisoria.horaInicio.substring(8, 10) + "/" +
                    reservaProvisoria.horaInicio.substring(5, 7) + "/" +
                    reservaProvisoria.horaInicio.substring(0, 4)

            // Alimenta a sua UI igual ao seu lindo mockup
            txtConfirmarQuadra.text = nomeQuadra
            txtData.text = "Dia: $dataExibicao"
            txtHorario.text = "Horário: ${horaIncExibicao}h - ${horaFimExibicao}h"

            btnConfirmar.setOnClickListener {
                lifecycleScope.launch {
                    val userId = SupabaseClient.instance.auth.currentUserOrNull()?.id

                    if (userId == null) {
                        Toast.makeText(this@ActivityConfirmarReserva, "Sessão expirada!", Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                    // Cria o objeto final injetando o idUsuario real!
                    val reservaProntaParaOBanco = reservaProvisoria.copy(idUsuario = userId)

                    Toast.makeText(this@ActivityConfirmarReserva, "Salvando reserva...", Toast.LENGTH_SHORT).show()
                    val sucesso = repository.cadastrarReserva(reservaProntaParaOBanco)

                    if (sucesso) {
                        // Abre a tela de confirmação final passando o objeto pronto adiante
                        val intentSucesso = Intent(this@ActivityConfirmarReserva, ActivityReservaConfirmada::class.java)
                        intentSucesso.putExtra("objeto_reserva", reservaProntaParaOBanco)
                        intentSucesso.putExtra("nome_quadra", nomeQuadra)
                        startActivity(intentSucesso)
                        finish()
                    } else {
                        Toast.makeText(this@ActivityConfirmarReserva, "Falha ao agendar.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        }
    }
