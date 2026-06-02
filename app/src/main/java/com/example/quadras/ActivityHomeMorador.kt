package com.example.quadras

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.quadras.R

class ActivityHomeMorador : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_morador)

        val btnReserva = findViewById<Button>(R.id.buttonReservarQuadra)
        val btnMinhasReservas = findViewById<Button>(R.id.buttonMinhasReservas)
        val proxReserva = findViewById<LinearLayout>(R.id.caixaStatusReserva)
        val user = intent.getStringExtra("user");

        btnReserva.setOnClickListener {
            Log.d("User: ", user.toString())
            val intent = Intent(this, ActivitySelecionarQuadra::class.java)
            intent.putExtra("user", user)
            startActivity(intent)
        }
    }
}