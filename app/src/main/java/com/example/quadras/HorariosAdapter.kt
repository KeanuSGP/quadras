package com.example.quadras

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

class HorariosAdapter(
    private val quantidadeHorarios: Int,
    private val context: Context,
    private val txtResumo: TextView,
    private val adminUser: Boolean = false,
    private val momento: String? = null
) : RecyclerView.Adapter<HorariosAdapter.ViewHolder>() {

    // Lista de horários que estão ocupados no banco de dados (guardamos as horas cheias, ex: 14)
    private val horasOcupadas = mutableSetOf<Int>()

    // Controle de cliques do usuário para montar o intervalo
    var primeiroClique: Int? = null
    var segundoClique: Int? = null

    var dataInicioManutencao: String? = null
    var dataFimManutencao: String? = null
    var horaInicioManutencao: Int? = null

    // O condomínio começa a abrir às 06:00 da manhã
    private val horaInicialFuncionamento = 6

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val card: MaterialCardView = view.findViewById(R.id.cardHorario)
        val txtHora: TextView = view.findViewById(R.id.txtHora)
        val txtStatus: TextView = view.findViewById(R.id.txtStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_horario, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val horaDoCard = horaInicialFuncionamento + position
        holder.txtHora.text = String.format("%02d:00", horaDoCard)

        // Verifica o estado atual desse card específico
        val estaOcupadoNoBanco = horasOcupadas.contains(horaDoCard)
        val estaNoIntervaloSelecionado = estaNoIntervalo(horaDoCard)

        when {
            estaOcupadoNoBanco -> {
                // 🔴 Caso 1: Ocupado por outro morador
                holder.card.setCardBackgroundColor(Color.parseColor("#B22222")) // Vermelho escuro
                holder.txtStatus.text = "Ocupado"
                holder.card.setOnClickListener(null) // Desativa o clique
            }
            estaNoIntervaloSelecionado -> {
                // 🟡 Caso 2: Selecionado pelo usuário atual
                holder.card.setCardBackgroundColor(Color.parseColor("#DAA520")) // Dourado/Amarelo Seleção
                holder.txtStatus.text = "Selecionado"
                if (adminUser) {
                    configurarCliqueAdmin(holder, horaDoCard)
                } else {
                    configurarClique(holder, horaDoCard)
                }
            }
            else -> {
                // 🟢 Caso 3: Disponível
                holder.card.setCardBackgroundColor(Color.parseColor("#144229")) // Verde ASCIJA padrão
                holder.txtStatus.text = "Disponível"
                if (adminUser) {
                    configurarCliqueAdmin(holder, horaDoCard)
                } else {
                    configurarClique(holder, horaDoCard)

                }
            }
        }
    }

    fun atualizarResumo() {
        txtResumo.text =
            "Horário selecionado: $dataInicioManutencao ${String.format("%02d:00", horaInicioManutencao)} - " +
                    "${dataFimManutencao ?: "__/__"} ${String.format("%02d:00", primeiroClique ?: 0)}"
    }

    private fun configurarCliqueAdmin(holder: ViewHolder, hora: Int) {
        holder.card.setOnClickListener {
            primeiroClique = hora
            segundoClique = null

            if (momento == "inicio") {
                txtResumo.text =
                    "Horário de início: ${String.format("%02d:00", hora)}"
            }
            if (momento == "fim") {
                txtResumo.text =
                    "Horário Selecionado: ${dataInicioManutencao} ${
                        String.format(
                            "%02d:00",
                            horaInicioManutencao
                        )
                    } - ${if(dataFimManutencao != null) dataFimManutencao else "(__/__)"} ${String.format("%02d:00", hora)}"
            }


            notifyDataSetChanged()
        }
    }

    private fun configurarClique(holder: ViewHolder, hora: Int) {
        holder.card.setOnClickListener {
            if (primeiroClique == null) {
                // Primeiro clique: Define o início
                primeiroClique = hora
                txtResumo.text = "Horário Selecionado: ${String.format("%02d:00", hora)} -> Escolha o fim"
            } else if (segundoClique == null) {
                // Segundo clique: Valida o intervalo antes de aplicar
                if (hora > primeiroClique!!) {
                    // Verifica se o usuário não tentou pular por cima de um horário já ocupado
                    if (contemOcupadoNoMeio(primeiroClique!!, hora)) {
                        txtResumo.text = "Não é possível selecionar um intervalo com horários ocupados!"
                        primeiroClique = null
                    } else {
                        segundoClique = hora
                        txtResumo.text = "Horário Selecionado: ${String.format("%02d:00", primeiroClique)} - ${String.format("%02d:00", segundoClique)}"
                    }
                } else {
                    // Se clicou em uma hora menor, reinicia e transforma ela no novo início
                    primeiroClique = hora
                    txtResumo.text = "Horário Selecionado: ${String.format("%02d:00", hora)} -> Escolha o fim"
                }
            } else {
                // Terceiro clique: Reinicia a seleção
                primeiroClique = hora
                segundoClique = null
                txtResumo.text = "Horário Selecionado: ${String.format("%02d:00", hora)} -> Escolha o fim"
            }
            notifyDataSetChanged() // Força a grade a se repintar inteira com as novas cores
        }
    }

    // Atualiza a lista de bloqueados com base nas respostas reais do Supabase
    fun atualizarHorariosOcupados(reservas: List<Reserva>) {
        horasOcupadas.clear()
        primeiroClique = null
        segundoClique = null

        // 1. Criamos o formatador calibrado exatamente para o formato: 2026-06-10T14:00:00+00:00
        val leitorIso = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", java.util.Locale.US).apply {
            // FORÇA o leitor a interpretar a data sem converter para o fuso do celular
            timeZone = java.util.TimeZone.getTimeZone("UTC")
        }

        // 2. Criamos o calendário também em UTC para extrair a hora pura vinda do banco
        val cal = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC"))

        reservas.forEach { reserva ->
            try {
                // Transforma o texto do banco em objeto Date do Java
                val dataInicio = leitorIso.parse(reserva.horaInicio)
                val dataFim = leitorIso.parse(reserva.horaFim)

                if (dataInicio != null && dataFim != null) {
                    // Extrai a HORA INICIAL pura do banco
                    cal.time = dataInicio
                    val horaInicioBanco = cal.get(java.util.Calendar.HOUR_OF_DAY)

                    // Extrai a HORA FINAL pura do banco
                    cal.time = dataFim
                    val horaFimBanco = cal.get(java.util.Calendar.HOUR_OF_DAY)

                    Log.d("ADAPTER_SEM_FUSO", "Bloqueando das $horaInicioBanco:00 até $horaFimBanco:00 (Hora direta do banco)")

                    // Preenche o Set com as horas do intervalo
                    for (h in horaInicioBanco until horaFimBanco) {
                        horasOcupadas.add(h)
                    }
                }
            } catch (e: Exception) {
                Log.e("ADAPTER_ERRO", "Erro de parse na string: ${reserva.horaInicio}", e)
            }
        }

        // Força o RecyclerView a se redesenhar aplicando as cores corretas
        notifyDataSetChanged()
    }

    private fun estaNoIntervalo(hora: Int): Boolean {
        val p = primeiroClique ?: return false
        val s = segundoClique
        if (s == null) return hora == p
        return hora in p..s
    }

    private fun contemOcupadoNoMeio(inicio: Int, fim: Int): Boolean {
        for (h in inicio..fim) {
            if (horasOcupadas.contains(h)) return true
        }
        return false
    }

    override fun getItemCount() = quantidadeHorarios
}