package com.example.quadras

import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class ActivityReservas : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_reservas)

        val repository = ReservationRepository()
        val timeZoneUTC = TimeZone.getTimeZone("UTC")
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        sdf.timeZone = timeZoneUTC
        val calendario = findViewById<ImageView>(R.id.filtrarReservas)
        val txtData = findViewById<TextView>(R.id.txtData)
        val voltar = findViewById<ImageView>(R.id.imageView)
        val userId = intent.getStringExtra("userId").toString()
        val rvReservas = findViewById<RecyclerView>(R.id.rvReservas)
        val txtNenhumaReservaEncontrada = findViewById<TextView>(R.id.txtNenhumaReserva)
        var reservas: List<Reserva> = emptyList()
        var quadras: List<Quadra> = emptyList()


        val adapter = ReservasAdapter(reservas, quadras, userId, lifecycleScope )
        rvReservas.layoutManager = LinearLayoutManager(this@ActivityReservas)
        rvReservas.adapter = adapter
        Log.d("RESERVAS: ", reservas.toString())

        // requisita reservas e quadras e monta o recycler view
        lifecycleScope.launch {
            reservas = repository.obterReservasDoUsuario(userId)
            quadras = repository.obterQuadras()

            if (reservas.isEmpty()) {
                txtNenhumaReservaEncontrada.visibility = View.VISIBLE
            } else {
                adapter.atualizarDado(reservas, quadras)
                txtNenhumaReservaEncontrada.visibility = View.GONE
                Log.d("RESERVAS PARA VER DATA", reservas.toString())
            }

        }


        // finaliza activity atual e volta para a anterior
        voltar.setOnClickListener {
            finish()
        }

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Selecione o dia da reserva")
            .build()

        calendario.setOnClickListener {
            datePicker.show(supportFragmentManager, "TAG_CALENDARIO")
        }

        // carrega o texto com a data do dia
        txtData.text = "Filtre pela data"

        // exibe a data selecionada
        datePicker.addOnPositiveButtonClickListener { d ->
            val data = sdf.format(Date(d))
            Log.d("DATA", data)
            txtData.text = data

            // selecionar a data filtra as reservas pela data selecionada e carrega no recycler view
            val reservasFiltradas = reservas.filter{r ->
                val rData = r.horaInicio.split("T")[0]
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                val rDataParsed = LocalDate.parse(rData)
                val rDataFormatada = rDataParsed.format(formatter)
                rDataFormatada == data
            }

            if (reservasFiltradas.isEmpty()) {
                txtNenhumaReservaEncontrada.visibility = View.VISIBLE
                adapter.atualizarDado(reservasFiltradas, quadras)
            } else {
                txtNenhumaReservaEncontrada.visibility = View.GONE
                adapter.atualizarDado(reservasFiltradas, quadras)
            }
        }



    }
}