package com.example.quadras

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.quadras.R
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class ActivitySelecionarQuadra : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_selecionar_quadra)

        val repository = ReservationRepository()
        val user = intent.getStringExtra("userId")
        Log.d("Usuario em selecioanr Quadra: ", user.toString())
        var btnQuadraTenis = findViewById<MaterialButton>(R.id.buttonQuadraTenis)
        var btnQuadraPoli = findViewById<MaterialButton>(R.id.buttonQuadraPoli)
        var btnQuadraFutebol = findViewById<MaterialButton>(R.id.buttonCampoFutebol)
        val voltar = findViewById<ImageView>(R.id.imageView)

        voltar.setOnClickListener {
            finish()
        }


        lifecycleScope.launch {
            val quadras = repository.obterQuadras()
            val quadrasTenis = quadras.filter{q -> q.tipo == "quadra_tenis" }
            val quadrasPoli = quadras.filter{q -> q.tipo == "quadra_poliesportiva"}
            val quadrasFutebol = quadras.filter{q -> q.tipo == "quadra_futebol"}


            Log.d("Quadras em selecioanr quadra: ", quadras.toString())

            btnQuadraTenis.text = "Quadra de tênis\n${quadrasTenis.size} Disponíveis"
            btnQuadraPoli.text = "Quadra poliesportiva\n${quadrasPoli.size} Disponíveis"
            btnQuadraFutebol.text = "Quadra de futebol\n${quadrasFutebol.size} Disponíveis"

            val intent = Intent(applicationContext, Activity_seleciona_espaco::class.java)

            btnQuadraTenis.setOnClickListener {
                intent.putExtra("quadras", ArrayList(quadrasTenis))
                startActivity(intent)
            }
            btnQuadraPoli.setOnClickListener {
                intent.putExtra("quadras", ArrayList(quadrasPoli))
                startActivity(intent)
            }
            btnQuadraFutebol.setOnClickListener {
                intent.putExtra("quadras", ArrayList(quadrasFutebol))
                startActivity(intent)
            }

        }

    }
}