package com.example.quadras

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.quadras.R

class ActivityConfirmaReserva : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_confirma_reserva)

        @Suppress("DEPRECATION")
        val quadra = intent.getSerializableExtra("quadra") as Quadra
        val inicio = intent.getIntExtra("inicio", 0)
        val fim = intent.getIntExtra("fim", 0)
        val quadraNome = findViewById<TextView>(R.id.txtConfirmacaoQuadra)
        val horario = findViewById<TextView>(R.id.txtConfirmacaoHorario)
        val btnInicio = findViewById<Button>(R.id.btnVoltarInicio)
        quadraNome.text = quadra.nome
        horario.text = "Horário: ${inicio}h - ${fim}h"

        btnInicio.setOnClickListener {
            val intent = Intent(this, ActivityHomeMorador::class.java)
            startActivity(intent)
        }
    }
}