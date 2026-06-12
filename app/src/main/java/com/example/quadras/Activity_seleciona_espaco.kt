package com.example.quadras

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Activity_seleciona_espaco : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_seleciona_espaco)

        @Suppress("DEPRECATION")
        val quadras = intent.getSerializableExtra("quadras") as List<Quadra>
        Log.d("Quadras em seleção do espaço: ", quadras.toString())
        val voltar = findViewById<ImageView>(R.id.imageViewBackPage)
        val ehAdmin = intent.getBooleanExtra("ehAdmin", false)
        val logoff = findViewById<ImageView>(R.id.imageViewHomeIcon)

        logoff.setOnClickListener { logoff() }

        voltar.setOnClickListener {
            finish()
        }


        val quadrasAdapter = QuadrasAdapter(quadras, this, ehAdmin)

        val rvQuadras = findViewById<RecyclerView>(R.id.rvQuadras)
        rvQuadras.layoutManager = LinearLayoutManager(this)
        rvQuadras.adapter = quadrasAdapter




    }
    private fun logoff() {
        val intent = Intent(this, ActivityLogin::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        val builder = AlertDialog.Builder(this)

        builder.setTitle("Atenção")
        builder.setMessage("Tem certeza que quer deslogar do sistema?")

        builder.setPositiveButton("Sim") { dialog, which ->
            startActivity(intent)
        }

        builder.setNegativeButton("Cancelar") { dialog, which ->
        }

        val dialog = builder.create()
        dialog.show()
    }
}