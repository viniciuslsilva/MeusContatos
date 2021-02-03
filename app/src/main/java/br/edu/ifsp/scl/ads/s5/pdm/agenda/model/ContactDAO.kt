package br.edu.ifsp.scl.ads.s5.pdm.agenda.model

interface ContactDAO {
    fun create(contact: Contact)
    fun findOne(nome: String): Contact
    fun findAll(): MutableList<Contact>
    fun update(contact: Contact)
    fun delete(nome: String)
}