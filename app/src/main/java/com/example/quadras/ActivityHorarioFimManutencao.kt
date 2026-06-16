package com.example.quadras

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class ActivityHorarioFimManutencao : AppCompatActivity() {

    private lateinit var horarioAdapter: HorariosAdapter
    private val repository = ReservationRepository()
    private var idQuadra: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_horario_fim_manutencao)


        val voltar = findViewById<ImageView>(R.id.imageViewBackPage)
        val logoff = findViewById<ImageView>(R.id.imageViewHomeIcon)
        val calendario = findViewById<ImageView>(R.id.calendario)
        val txtData = findViewById<TextView>(R.id.txtDataSelecionada)
        val rv = findViewById<RecyclerView>(R.id.rvHorarios)
        val resumo = findViewById<TextView>(R.id.txtResumoSelecao)
        val btnConfirmar = findViewById<Button>(R.id.btnConfirmar)

        voltar.setOnClickListener { finish() }
        logoff.setOnClickListener { logoff() }

        // INFORMAÇÕES DO INTENT
        @Suppress("DEPRECATION")
        val reservaPrePreenchida = intent.getSerializableExtra("objeto_reserva") as Reserva
        val ehAdmin = intent.getBooleanExtra("ehAdmin", false)
        val nomeQuadra = intent.getStringExtra("nome_quadra")
        idQuadra = reservaPrePreenchida.idQuadra

        // sai da tela caso o usuário não seja admin
        if (!ehAdmin) {
            Toast.makeText(this, "TCHAU", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, ActivityLogin::class.java))
            finish()
        }

        // pega data e hora de inicio da reserva
        val hInc = reservaPrePreenchida.horaInicio.split("T")[1].split("+")[0].split(":")[0]
        val dInc = reservaPrePreenchida.horaInicio.split("T")[0]
        val dIncformatada = "(${dInc.split("-")[2]}/${dInc.split("-")[1]})"

        // Inicializa o Adapter
        val quantidadeHorarios = 18
        horarioAdapter = HorariosAdapter(quantidadeHorarios, this, resumo, ehAdmin, "fim")
        rv.layoutManager = object : GridLayoutManager(this, 3) {
            override fun canScrollVertically(): Boolean = false
        }
        rv.adapter = horarioAdapter

        // passa a data de início para o adapter (será usado na seleção de data de fim)
        horarioAdapter.horaInicioManutencao = hInc.toInt()
        horarioAdapter.dataInicioManutencao = dIncformatada

        resumo.text = "Horário Selecionado: ${hInc}:00 - 00:00"

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Selecione o dia final da manutenção")
            .build()

        calendario.setOnClickListener {
            datePicker.show(supportFragmentManager, "TAG_CALENDARIO")
        }

        // configuração para exibição de data
        val formatarData = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        var dataSelecionada = formatarData.format(Date())
        txtData.text = dataSelecionada
        val formatDtSelecionada = "(${dataSelecionada.split("/")[0]}/${dataSelecionada.split("/")[1]})"
        horarioAdapter.dataFimManutencao = formatDtSelecionada

        buscarReservasDoDia(dataSelecionada)

        datePicker.addOnPositiveButtonClickListener { d ->
            formatarData.timeZone = TimeZone.getTimeZone("UTC")
            dataSelecionada = formatarData.format(Date(d))
            horarioAdapter.dataFimManutencao = "(${dataSelecionada.split("/")[0]}/${
                dataSelecionada.split(
                    "/"
                )[1]
            })"
            horarioAdapter.atualizarResumo()
            txtData.text = dataSelecionada

            Log.d("TESTE", "primeiroClique antes = ${horarioAdapter.primeiroClique}")

            horarioAdapter.primeiroClique = null
            Log.d("TESTE", "primeiroClique depois = ${horarioAdapter.primeiroClique}")

            horarioAdapter.atualizarResumo()

            horarioAdapter.notifyDataSetChanged()

            buscarReservasDoDia(dataSelecionada)

        }

        Log.d("RESERVA PRE PREENCHIDO", reservaPrePreenchida.toString())

        btnConfirmar.setOnClickListener {
            val fim = horarioAdapter.primeiroClique

            if (fim == null) {
                Toast.makeText(this, "Selecione um horário válido (Fim)!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val partesData = dataSelecionada.split("/")
            val dataBaseIso = "${partesData[2]}-${partesData[1]}-${partesData[0]}"
            val dataFinalManutFormatada = "${dataBaseIso}T${String.format("%02d", fim)}:00:00+00:00"

            // 2. Cria o objeto Reserva pré-preenchido
            val objetoReserva = reservaPrePreenchida.copy(horaFim = dataFinalManutFormatada)

            Log.d("RESERVA PARA ENVIAR", objetoReserva.toString())


            // VERIFICAR MESMO DIA MAS HORARIO DE FIM MENOR QUE INICIO
            // comparação entre a data inicio e fim da reserva
            val dataInicio = OffsetDateTime.parse(objetoReserva.horaInicio).toLocalDate()
            val dataFim = OffsetDateTime.parse(objetoReserva.horaFim).toLocalDate()

            if (dataInicio.isAfter(dataFim)) {
                Toast.makeText(this, "A data final da manutenção deve ser superior a data de início", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, ActivityTipoInterdicao::class.java)
                intent.putExtra("objeto_reserva",objetoReserva)
                intent.putExtra("nome_quadra",nomeQuadra)
                intent.putExtra("ehAdmin", ehAdmin)
                startActivity(intent)
            }

        }

    }

    fun buscarReservasDoDia(data: String) {
        lifecycleScope.launch {
            val reservasOcupadas =
                repository.obterReservasQuardaNoDia(idQuadra, data)

            horarioAdapter.atualizarHorariosOcupados(
                reservasOcupadas
            )
        }
    }

    private fun logoff() {
        val intent = Intent(this, ActivityLogin::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        val builder = AlertDialog.Builder(this)

        builder.setTitle("Atenção")
        builder.setMessage("Tem certeza que quer sair do sistema?")

        builder.setPositiveButton("Sim") { dialog, which ->
            startActivity(intent)
        }

        builder.setNegativeButton("Cancelar") { dialog, which ->
        }

        val dialog = builder.create()
        dialog.show()
    }
}