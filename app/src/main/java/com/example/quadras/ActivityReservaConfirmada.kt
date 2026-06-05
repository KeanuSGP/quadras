package com.example.quadras

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class ActivityReservaConfirmada : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_reserva_confirmada)

        // 1. Mapeia os componentes XML
        val quadraNomeTxt = findViewById<TextView>(R.id.txtConfirmacaoQuadra)
        val dataTxt = findViewById<TextView>(R.id.txtConfirmacaoDia)
        val horarioTxt = findViewById<TextView>(R.id.txtConfirmacaoHorario)
        val btnInicio = findViewById<Button>(R.id.btnVoltarInicio)

        // 2. Recupera as informações enviadas pela tela de revisão
        val nomeQuadra = intent.getStringExtra("nome_quadra") ?: "Quadra"

        @Suppress("DEPRECATION")
        val reserva = intent.getSerializableExtra("objeto_reserva") as Reserva

        if(reserva != null){
            val ano = reserva.horaInicio.substring(0, 4)
            val mes = reserva.horaInicio.substring(5, 7)
            val dia = reserva.horaInicio.substring(8, 10)

            val horaInicioExibicao = reserva.horaInicio.substring(11, 13)
            val horaFimExibicao = reserva.horaFim.substring(11, 13)

            quadraNomeTxt.text = nomeQuadra
            horarioTxt.text = "Horário: ${horaInicioExibicao}h - ${horaFimExibicao}h"
            dataTxt.text = "Dia: $dia/$mes/$ano"
        } else {
            quadraNomeTxt.text = nomeQuadra
            horarioTxt.text = "Reserva realizada com sucesso!"
        }


        btnInicio.setOnClickListener {
            val intent = Intent(this, ActivityHomeMorador::class.java)
            intent.putExtra("user_id",reserva.idUsuario)
            startActivity(intent)

            //fechando as telas intermediarias
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish() // Fecha a tela de sucesso definitivamente
        }
    }
}