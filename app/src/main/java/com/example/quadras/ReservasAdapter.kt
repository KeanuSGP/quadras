package com.example.quadras

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Date
import java.util.Locale

class ReservasAdapter(private var reservas: List<Reserva>, private var quadras: List<Quadra>, private val userId: String, private val lifecycleScope: LifecycleCoroutineScope, private val diaAtual: String, private val horaFormatada: String)
    : RecyclerView.Adapter<ReservasAdapter.ViewHolder>() {

        private val repository = ReservationRepository()

        class ViewHolder(itemReserva: View): RecyclerView.ViewHolder(itemReserva) {
            val nome: TextView
            val data: TextView
            val cancelar: Button

            init {
                nome = itemReserva.findViewById<TextView>(R.id.txtQuadra)
                data = itemReserva.findViewById<TextView>(R.id.txtData)
                cancelar = itemReserva.findViewById<Button>(R.id.btnCancelar)
            }
        }

    override fun onCreateViewHolder(rvReservas: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(rvReservas.context).inflate(R.layout.item_reserva, rvReservas, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val reserva: Reserva = reservas[position]
        // pega a data
        val data = LocalDate.parse(reserva.horaInicio.split("T")[0])
        // pega o dia
        val dia = data.toString().split('-')[2]
        // pega o mês e converte para o nome dele em enum e em inglês
        val mes = Month.of(data.toString().split('-')[1].toInt())
        val quadra = quadras.find{ it.id == reserva.idQuadra }

        // getDisplayName ta formantando para string. FULL indica que vai exibir o nome completo do mês e
        // locale está setando o idioma para o brasileiro
        @Suppress("DEPRECATION")
        val dataTxt = "${dia} de ${mes.getDisplayName(TextStyle.FULL, Locale("pt", "BR"))} \n" +
                "${reserva.horaInicio.split("T")[1].split(":")[0]}h" +
                " - ${reserva.horaFim.split("T")[1].split(":")[0]}h"


        viewHolder.nome.text = quadra?.nome
        viewHolder.data.text = dataTxt

        viewHolder.cancelar.setOnClickListener {
            deletarReserva(reserva)
        }

        }


    fun deletarReserva(reserva: Reserva) {
        lifecycleScope.launch {
            try {
                repository.deletarReserva(reserva.id.toString())
                val reservasAposDelecao = repository.obterReservasDoUsuario(userId, "${diaAtual}T${horaFormatada}")
                atualizarDado(reservasAposDelecao, quadras)
            } catch(e: Exception) {
                    Log.d("ERRO AO DELETAR", e.message.toString())
            }

        }
    }

    fun atualizarDado(reservas: List<Reserva>, quadras: List<Quadra>) {
        Log.d("ADAPTER", "Atualizando para ${reservas.size}")
        this.reservas = reservas
        this.quadras = quadras

        // propriedade usada para avisar ao adapter que as propridades foram setadas, forçando a renderização novamente
        notifyDataSetChanged()
    }

        override fun getItemCount():Int {
            return reservas.size
        }

}