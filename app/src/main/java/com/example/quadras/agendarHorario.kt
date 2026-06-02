package com.example.quadras

import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class agendarHorario : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_agendar_horario)

        val voltar = findViewById<ImageView>(R.id.imageViewBackPage)

        voltar.setOnClickListener {
            finish()
        }

        val formatarData = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        val inflater = LayoutInflater.from(this)
        val rvHorario = findViewById<RecyclerView>(R.id.rvHorarios)
        val resumo = findViewById<TextView>(R.id.txtResumoSelecao)
        val btnConfirmar = findViewById<Button>(R.id.btnConfirmar)
        val calendario = findViewById<ImageView>(R.id.calendario)
        val quadra = intent.getSerializableExtra("quadra")
        val txtData = findViewById<TextView>(R.id.txtDataSelecionada)
        var diaAtual = formatarData.format(Date())
        txtData.text = diaAtual
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Selecione o dia da reserva")
            .build()

        var horaInicio: Int = 0
        var horaFim: Int = 0

        val quantidadeHorarios = 18

        val horarioAdapter = HorariosAdapter(quantidadeHorarios, this)
        rvHorario.layoutManager = GridLayoutManager(this, 3)
        rvHorario.adapter = horarioAdapter


        calendario.setOnClickListener {
            datePicker.show(supportFragmentManager, "TAG_CALENDARIO")
        }

        datePicker.addOnPositiveButtonClickListener { d ->
            formatarData.timeZone = TimeZone.getTimeZone("UTC")
            val data = formatarData.format(Date(d))
            txtData.text = data
        }

    }
}