package com.example.quadras

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import io.github.jan.supabase.gotrue.auth
import kotlinx.coroutines.launch
import androidx.core.graphics.toColorInt

class ActivityTipoInterdicao : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tipo_interdicao)

        val repository = ReservationRepository()
        val reservaProvisoria = intent.getSerializableExtra("objeto_reserva") as? Reserva
        val nomeQuadra = intent.getStringExtra("nome_quadra") ?: "Quadra"
        val ehAdmin = intent.getBooleanExtra("ehAdmin", false)

        val voltar = findViewById<ImageView>(R.id.imageView)
        voltar.setOnClickListener {
            finish()
        }

        val btnManutencao = findViewById<AppCompatButton>(R.id.buttonManutencao)
        val btnEvento = findViewById<AppCompatButton>(R.id.buttonEvento)
        val btnOutro = findViewById<AppCompatButton>(R.id.buttonOutro)
        val btnConfirmar = findViewById<Button>(R.id.ButtonConfirmar)

        // array de estados para o botao: selected e nada(padrao)
        val estados = arrayOf(
            intArrayOf(android.R.attr.state_selected),
            intArrayOf()
        )

        // array de cores para os estados do botao
        val cores = intArrayOf(
            resources.getColor(R.color.selecionado, null),
            resources.getColor(R.color.verde_ascija, null)
        )

        // Array de cores para o texto do botao
        val coresTexto = intArrayOf(
            resources.getColor(android.R.color.white, null),
            resources.getColor(android.R.color.white, null)
        )

        // definindo os estados e cores dos estados
        val listaCorFundo = ColorStateList(estados, cores)

        // definindo os estados e as cores do texto para esses estados
        val listaCorTexto = ColorStateList(estados, coresTexto)

        // adicionando as listas para cada botão
        btnManutencao.backgroundTintList = listaCorFundo
        btnManutencao.setTextColor(listaCorTexto)

        btnEvento.backgroundTintList = listaCorFundo
        btnEvento.setTextColor(listaCorTexto)

        btnOutro.backgroundTintList = listaCorFundo
        btnOutro.setTextColor(listaCorTexto)


        // botão com toggle para seleção de somente uma causa
        btnManutencao.setOnClickListener {
            btnManutencao.isSelected = true
            if (btnEvento.isSelected || btnOutro.isSelected) {
                btnEvento.isSelected = false
                btnOutro.isSelected = false
            }
        }

        btnEvento.setOnClickListener {
            btnEvento.isSelected = true
            if (btnManutencao.isSelected || btnOutro.isSelected) {
                btnManutencao.isSelected = false
                btnOutro.isSelected = false
            }
        }

        btnOutro.setOnClickListener {
            btnOutro.isSelected = true
            if (btnManutencao.isSelected || btnEvento.isSelected) {
                btnManutencao.isSelected = false
                btnEvento.isSelected = false
            }
        }

        btnConfirmar.setOnClickListener {
            if (!btnManutencao.isSelected || !btnEvento.isSelected || !btnOutro.isSelected) {
                Toast.makeText(this, "Selecione um motivo!", Toast.LENGTH_SHORT).show()
            } else {
                lifecycleScope.launch {
                    val userId = SupabaseClient.instance.auth.currentUserOrNull()?.id
                    if (userId == null) {
                        Toast.makeText(
                            this@ActivityTipoInterdicao,
                            "Sessão expirada!",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@launch
                    }
                    val reserva = reservaProvisoria?.copy(idUsuario = userId, status = "indisponivel")
                    if (reserva != null) {
                        Toast.makeText(
                            this@ActivityTipoInterdicao,
                            "Agendando manutenção...",
                            Toast.LENGTH_SHORT
                        ).show()
                        val sucesso = repository.cadastrarReserva(reserva)
                        if (sucesso) {
                            val intent = Intent(
                                this@ActivityTipoInterdicao,
                                ActivityConfirmaManutencao::class.java
                            )
                            intent.putExtra("reserva", reserva)
                            intent.putExtra("quadra", nomeQuadra)
                            intent.putExtra("userId", userId)
                            intent.putExtra("ehAdmin", ehAdmin)
                            Log.d("NOME DA QUADRA ENVIADO PARA CONFIMACAO DE MANUTENCAO", nomeQuadra)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        Toast.makeText(
                            this@ActivityTipoInterdicao,
                            "Falha ao agendar manutenção.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}

