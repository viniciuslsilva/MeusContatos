package br.edu.ifsp.scl.ads.s5.pdm.agenda.controller

import br.edu.ifsp.scl.ads.s5.pdm.agenda.model.Contact
import br.edu.ifsp.scl.ads.s5.pdm.agenda.model.ContactDAO
import br.edu.ifsp.scl.ads.s5.pdm.agenda.model.ContactFirebase
import br.edu.ifsp.scl.ads.s5.pdm.agenda.view.MainActivity

class ContatoController(mainActivity: MainActivity) {
    val contactDAO: ContactDAO
    init {
        contactDAO = ContactFirebase()
    }

    fun insereContato(contact: Contact) = contactDAO.create(contact)
    fun buscaContatos() = contactDAO.findAll()
    fun atualizaContato(contact: Contact) = contactDAO.update(contact)
    fun removeContato(nome: String) = contactDAO.delete(nome)
}