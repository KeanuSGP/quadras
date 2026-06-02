package com.example.quadras

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quadras.QuadrasAdapter.ViewHolder

class HorariosAdapter(private val quantidadeHorario: Int, private val context: Context, private val resumo: TextView) :
    RecyclerView.Adapter<HorariosAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val horario: TextView
        val status: TextView

        init {
            horario = view.findViewById<TextView>(R.id.txtHora)
            status = view.findViewById<TextView>(R.id.txtStatus)
        }
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): HorariosAdapter.ViewHolder {

        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_horario, viewGroup, false)

        return ViewHolder(view)
    }

    var horaInicio = 0;
    var horaFim = 0;

    override fun onBindViewHolder(viewHolder: HorariosAdapter.ViewHolder, position: Int) {
            var hora = 6 + position
            viewHolder.horario.text = "${hora.toString().padStart(2, '0')}:00"
            viewHolder.status.text = "disponivel"

        viewHolder.itemView.setOnClickListener {
            var horaClique = viewHolder.horario.text.split(":")[0].padStart(2, ' ').toInt()

            if (horaInicio == 0) {
                horaInicio = horaClique
                resumo.text = "Horário selecionado: ${horaInicio}:00 - 00:00"
            }





//            if (horaFim == 0 && horaInicio != 0) {
//                horaFim = horaClique
//                resumo.text = "Horário selecionado: ${horaInicio} - ${horaFim}"
//            }
        }

    }
    override fun getItemCount() = quantidadeHorario

}