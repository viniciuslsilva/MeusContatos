package br.edu.ifsp.scl.ads.s5.pdm.agenda.adapter

import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.edu.ifsp.scl.ads.s5.pdm.agenda.R
import br.edu.ifsp.scl.ads.s5.pdm.agenda.model.Contact


class ContatosAdapter(
    private val contatosList: MutableList<Contact>,
    private val onContatoClickListener: OnContatoClickListener,
): RecyclerView.Adapter<ContatosAdapter.ContatoViewHolder>() {

    inner class ContatoViewHolder(layoutContatoView: View): RecyclerView.ViewHolder(layoutContatoView),
        View.OnCreateContextMenuListener {
        val nomeTv: TextView = layoutContatoView.findViewById(R.id.nomeTv)
        val telefoneTv: TextView = layoutContatoView.findViewById(R.id.telefoneTv)
        init {
            layoutContatoView.setOnCreateContextMenuListener(this)
        }

        private val POSICAO_INVALIDA = -1
        var posicao: Int = POSICAO_INVALIDA

        override fun onCreateContextMenu(menu: ContextMenu?, view: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
            menu?.add("Editar")?.setOnMenuItemClickListener {
                if (posicao != POSICAO_INVALIDA) {
                    onContatoClickListener.onEditarMenuItemClick(posicao)
                    true
                }
                false
            }
            menu?.add("Remover")?.setOnMenuItemClickListener {
                if (posicao != POSICAO_INVALIDA) {
                    onContatoClickListener.onRemoverMenuItemClick(posicao)
                    true
                }
                false
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContatoViewHolder {
        val layoutContatoView: View = LayoutInflater.from(parent.context).inflate(R.layout.layout_contato, parent, false)
        return ContatoViewHolder(layoutContatoView)
    }

    override fun onBindViewHolder(holder: ContatoViewHolder, position: Int) {
        val contato = contatosList[position]

        holder.nomeTv.text = contato.name
        holder.telefoneTv.text = contato.telephone
        holder.itemView.setOnClickListener{
            onContatoClickListener.onContatoClick(position)
        }
        holder.posicao = position
    }

    override fun getItemCount(): Int = contatosList.size
}
