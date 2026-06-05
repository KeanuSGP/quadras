package com.example.quadras

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle

class ActivityConfirmaManutencao : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_confirma_manutencao)
        @Suppress("DEPRECATION")
        val reserva = intent.getSerializableExtra("reserva") as? Reserva
        val nomeQuadra = intent.getStringExtra("quadra") ?: "Quadra"
        val userId = intent.getStringExtra("userId")
        val ehAdmin = intent.getBooleanExtra("ehAdmin", false)

        val txtNomeQuadra = findViewById<TextView>(R.id.txtConfirmacaoQuadra)
        val txtInicio = findViewById<TextView>(R.id.txtInicio)
        val txtTermino = findViewById<TextView>(R.id.txtTermino)
        val btnVoltarInicio = findViewById<Button>(R.id.btnVoltarInicio)

        txtNomeQuadra.text = nomeQuadra
        val data = reserva?.horaInicio.toString().split("T")
        val dataInicio = data[0]
        val horaInicio = data[1].split(":")[0]
        val dataFim = reserva?.horaFim.toString().split("T")
        val horaFim = dataFim[1].split(":")[0]
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val dataParse = LocalDate.parse(dataInicio, formatter)
        @Suppress("DEPRECATION")
        val mesNome = dataParse.month.getDisplayName(TextStyle.FULL, java.util.Locale("pt", "BR"))

        txtInicio.text = "Dia início: ${dataInicio.toString().split("-")[2]} de ${mesNome}\nHorário início: ${horaInicio}h"
        txtTermino.text = "Dia término: ${dataFim[0].split("-")[2]} de ${mesNome}\nHorário término: ${horaFim}h"

        btnVoltarInicio.setOnClickListener {
            val intent = Intent(this, ActivityHomeMorador::class.java)
            intent.putExtra("user_id", userId)
            intent.putExtra("ehAdmin", ehAdmin)
            // limpa toda a task e inicia a activity alvo em uma task
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        Log.d("RESERVA ", reserva.toString())
        Log.d("NOME QUADRA ", nomeQuadra)
    }
}