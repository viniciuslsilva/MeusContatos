package br.edu.ifsp.scl.ads.s5.pdm.agenda.model

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.google.gson.Gson

class ContatoSharedPreferences(context: Context): ContactDAO {
    private val CONTATOS_LIST_SHARED_PREFERENCES = "contatosListSharedPreferences"
    private val CONTATOS_LIST_KEY = "contatosList"
    private val contatosListSp: SharedPreferences = context.getSharedPreferences(
        CONTATOS_LIST_SHARED_PREFERENCES,
        MODE_PRIVATE
    )

    // lista de contatos que "simula" um banco de dados
    private val contatosList: MutableList<Contact> = mutableListOf()

    // Objeto GSON para conversão de Contato em Json e vice-versa
    private val gson: Gson = Gson()

    init {
        // Carregamento inicial da lista de contatos
        findAll()
    }

    override fun create(contact: Contact) {
        // Busca contato repetido antes de criar
        if (contatosList.filter { it.name.equals(contact.name) }.size == 0) {
            contatosList.add(contact)
            salvaAtualizaContatos()
        }
    }

    override fun findOne(nome: String) = contatosList[contatosList.indexOfFirst { it.name.equals(nome) }]

    override fun findAll(): MutableList<Contact> {
        val contatosListString = contatosListSp.getString(CONTATOS_LIST_KEY, "")

        // Converter de String para um Array de Contato
        val contatoArray = gson.fromJson(contatosListString, Array<Contact>::class.java)
        if (contatoArray != null) {
            contatosList.clear()
            contatosList.addAll(contatoArray.toList())
        }

        return contatosList
    }

    override fun update(contact: Contact) {
        val posicao = contatosList.indexOfFirst { it.name.equals(contact.name) }
        if (posicao != -1) {
            contatosList[posicao] = contact
            salvaAtualizaContatos()
        }
    }

    override fun delete(nome: String) {
        val posicao = contatosList.indexOfFirst { it.name.equals(nome) }
        if (posicao != -1) {
            contatosList.removeAt(posicao)
            salvaAtualizaContatos()
        }
    }

    private fun salvaAtualizaContatos() {
        val editor = contatosListSp.edit()
        editor.putString(CONTATOS_LIST_KEY, gson.toJson(contatosList))
        editor.commit()
    }
}
