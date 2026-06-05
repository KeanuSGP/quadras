package com.example.quadras

import android.content.Intent
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

        // vê se o usuário logado é admin
        val ehAdmin = intent.getBooleanExtra("ehAdmin", false)

        // Ação do Botão Confirmar
        btnConfirmar.setOnClickListener {
            val inc = horarioAdapter.primeiroClique
            val fim = horarioAdapter.segundoClique

            if (inc == null || fim == null) {
                Toast.makeText(this, "Selecione um horário válido (Início e Fim)!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 1. Monta as datas no formato ISO correto antes de criar o objeto
            val partesData = dataSelecionada.split("/")
            val dataBaseIso = "${partesData[2]}-${partesData[1]}-${partesData[0]}"
            val horaInicioIso = "${dataBaseIso}T${String.format("%02d", inc)}:00:00+00:00"
            val horaFimIso = "${dataBaseIso}T${String.format("%02d", fim)}:00:00+00:00"

            // 2. Cria o objeto Reserva pré-preenchido
            val objetoReserva = Reserva(
                idUsuario = "", // Deixamos vazio, pois a tela de revisão vai injetar o UID real do Supabase
                idQuadra = idQuadra, // Convertendo Int para String conforme seu modelo
                horaInicio = horaInicioIso,
                horaFim = horaFimIso
            )

            if (ehAdmin) {
                val intent = Intent(this, ActivityTipoInterdicao::class.java)
                intent.putExtra("objeto_reserva",objetoReserva)
                intent.putExtra("nome_quadra",quadra.nome)
                intent.putExtra("ehAdmin", ehAdmin)
                startActivity(intent)
            } else {
                val intent = Intent(this, ActivityConfirmarReserva::class.java)
                intent.putExtra("objeto_reserva",objetoReserva)
                intent.putExtra("nome_quadra",quadra.nome)
                startActivity(intent)
            }


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