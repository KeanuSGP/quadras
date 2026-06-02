package com.example.quadras

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.quadras.R
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class ActivityHomeMorador : AppCompatActivity() {

    private val repository = ReservationRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_morador)

        val btnReserva = findViewById<Button>(R.id.buttonReservarQuadra)
        val btnMinhasReservas = findViewById<Button>(R.id.buttonMinhasReservas)

        val userId = intent.getStringExtra("user_id")

        if (userId.isNullOrEmpty()) {
            Toast.makeText(this,"Erro ao recuperar dados do usuario.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        btnReserva.setOnClickListener {
            Log.d("User: ", userId)
            val intent = Intent(this, ActivitySelecionarQuadra::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        val userId = intent.getStringExtra("user_id")

        if(!userId.isNullOrEmpty()) {

            val layoutVazio = findViewById<LinearLayout>(R.id.layoutEstadoVazio)
            val layoutComReserva = findViewById<LinearLayout>(R.id.layoutEstadoComReserva)
            val txtNomeQuadra = findViewById<TextView>(R.id.txtNomeQuadraReserva)
            val txtDataHora = findViewById<TextView>(R.id.txtDataHoraReserva)

            carregarPainelProximaReserva(
                userId,
                layoutVazio,
                layoutComReserva,
                txtDataHora,
                txtNomeQuadra
            )
        }

    }

    //funcao assincrona para carregar painel dinamicamente
    private fun carregarPainelProximaReserva(
        userId: String,
        layoutVazio: View,
        layoutComReserva: View,
        txtDataHora: TextView,
        txtNomeQuadra: TextView
    ) {
        lifecycleScope.launch {
            val proximaReserva = repository.obterProximaReserva(userId)

            if(proximaReserva != null){

                //busca dados da quadra
                val quadra = repository.obterQuadra(proximaReserva.idQuadra)

                //atualizar visibilidade
                layoutVazio.visibility = View.GONE
                layoutComReserva.visibility = View.VISIBLE

                //caso encontrou quadra exibir com nome real
                if(quadra != null){
                    txtNomeQuadra.text = quadra.nome
                } else{
                    txtNomeQuadra.text = "Quadra selecionada (ID: ${proximaReserva.idQuadra})"
                }

                //formatar data
                val dataReserva = extrairData(proximaReserva.horaInicio)
                val horaInicioFormatada = extrairHora(proximaReserva.horaInicio)
                val horaFimFormatada = extrairHora(proximaReserva.horaFim)
                txtDataHora.text = """
                    - dia: $dataReserva
                    - inicio: $horaInicioFormatada
                    - fim: $horaFimFormatada
                """.trimIndent()

            } else{
                //nenhuma reserva encontrada
                layoutVazio.visibility = View.VISIBLE
                layoutComReserva.visibility = View.GONE
            }
        }
    }

    //TODO: passar funcoes helpers para uma classe / arquivo especifico
    private fun extrairData(dataIso: String): String {
        return try {
            val leitorIso = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.US).apply {
                timeZone = java.util.TimeZone.getTimeZone("UTC")
            }
            val dataObjeto = leitorIso.parse(dataIso)
            val formatadorData = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale("pt", "BR")).apply {
                timeZone = java.util.TimeZone.getDefault()
            }
            if (dataObjeto != null) formatadorData.format(dataObjeto) else dataIso
        } catch (e: Exception) {
            dataIso
        }
    }
    private fun extrairHora(dataIso: String): String {
        return try {
            val leitorIso = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.US).apply {
                timeZone = java.util.TimeZone.getTimeZone("UTC")
            }
            val dataObjeto = leitorIso.parse(dataIso)
            val formatadorHora = java.text.SimpleDateFormat("HH:mm", java.util.Locale("pt", "BR")).apply {
                timeZone = java.util.TimeZone.getDefault()
            }
            if (dataObjeto != null) formatadorHora.format(dataObjeto) else "--:--"
        } catch (e: Exception) {
            "--:--"
        }
    }
}