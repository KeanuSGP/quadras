package com.example.quadras

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class agendarHorario : AppCompatActivity() {

    private val repository = ReservationRepository()
    private lateinit var horarioAdapter: HorariosAdapter
    private var idQuadra: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_agendar_horario)

        val voltar = findViewById<ImageView>(R.id.imageViewBackPage)
        voltar.setOnClickListener { finish() }

        // Recupera a quadra selecionada vinda do Adapter anterior
        val quadra = intent.getSerializableExtra("quadra") as? Quadra
        if (quadra == null) {
            Toast.makeText(this, "Erro ao carregar dados da quadra", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        idQuadra = quadra.id

        val formatarData = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val rvHorario = findViewById<RecyclerView>(R.id.rvHorarios)
        val resumo = findViewById<TextView>(R.id.txtResumoSelecao)
        val btnConfirmar = findViewById<Button>(R.id.btnConfirmar)
        val calendario = findViewById<ImageView>(R.id.calendario)
        val txtData = findViewById<TextView>(R.id.txtDataSelecionada)

        // Configuração inicial da data
        var dataSelecionada = formatarData.format(Date())
        txtData.text = dataSelecionada

        // Inicializa o Adapter
        val quantidadeHorarios = 18
        horarioAdapter = HorariosAdapter(quantidadeHorarios, this, resumo)
        rvHorario.layoutManager = GridLayoutManager(this, 3)
        rvHorario.adapter = horarioAdapter

        // Busca inicial de reservas para o dia de hoje
        buscarReservasDoDia(dataSelecionada)

        // Configuração do Calendário Material
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Selecione o dia da reserva")
            .build()

        calendario.setOnClickListener {
            datePicker.show(supportFragmentManager, "TAG_CALENDARIO")
        }

        datePicker.addOnPositiveButtonClickListener { d ->
            formatarData.timeZone = TimeZone.getTimeZone("UTC")
            dataSelecionada = formatarData.format(Date(d))
            txtData.text = dataSelecionada


            // Sempre que o morador trocar a data, rodamos a busca novamente!
            buscarReservasDoDia(dataSelecionada)
        }

        // Ação do Botão Confirmar
        btnConfirmar.setOnClickListener {
            val inc = horarioAdapter.primeiroClique
            val fim = horarioAdapter.segundoClique

            if (inc == null || fim == null) {
                Toast.makeText(this, "Selecione um horário válido (Início e Fim)!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // TODO: Aqui criaremos o POST final enviando a reserva estruturada ao Supabase!
            Toast.makeText(this, "Preparado para salvar intervalo das $inc:00 até $fim:00!", Toast.LENGTH_LONG).show()
        }
    }

    private fun buscarReservasDoDia(data: String) {
        lifecycleScope.launch {
            Toast.makeText(this@agendarHorario, "Buscando horários disponíveis...", Toast.LENGTH_SHORT).show()
            val reservasOcupadas = repository.obterReservasQuardaNoDia(idQuadra, data)

            // Alimenta o adapter com as reservas reais vindas do banco
            horarioAdapter.atualizarHorariosOcupados(reservasOcupadas)
        }
    }
}