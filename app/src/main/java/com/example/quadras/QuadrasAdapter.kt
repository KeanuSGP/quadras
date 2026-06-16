package com.example.quadras

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class QuadrasAdapter(private val dataSet: List<Quadra>, private val context: Context, private val ehAdmin: Boolean) : RecyclerView.Adapter<QuadrasAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imagem : ImageView
        val nomeQuadra : TextView

        init {
            imagem = view.findViewById<ImageView>(R.id.imgQuadraFoto)
            nomeQuadra = view.findViewById<TextView>(R.id.txtNomeQuadraCard)
        }
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_quadra_card, viewGroup, false)

        return ViewHolder(view)
    }


    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val quadra: Quadra = dataSet[position]


        if (quadra.tipo == "quadra_tenis") {
            viewHolder.imagem.setImageResource(R.drawable.teste_quadra_ascija)
            viewHolder.nomeQuadra.text = dataSet[position].nome
        } else if (quadra.tipo == "quadra_poliesportiva") {
            viewHolder.imagem.setImageResource(R.drawable.quadra_poliesportiva_teste)
            viewHolder.nomeQuadra.text = dataSet[position].nome
        } else {
            viewHolder.imagem.setImageResource(R.drawable.quadra_futebol_teste)
            viewHolder.nomeQuadra.text = dataSet[position].nome
        }

        viewHolder.itemView.setOnClickListener {
            val intent = Intent(context, ActivityAgendarHorario::class.java)
            intent.putExtra("quadra", quadra)
            intent.putExtra("ehAdmin", ehAdmin)
            context.startActivity(intent)
        }


    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size
}