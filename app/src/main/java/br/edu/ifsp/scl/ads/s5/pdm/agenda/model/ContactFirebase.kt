package br.edu.ifsp.scl.ads.s5.pdm.agenda.model

import br.edu.ifsp.scl.ads.s5.pdm.agenda.adapter.AutenticadorFirebase
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class ContactFirebase : ContactDAO {

    private val userId = AutenticadorFirebase.firebaseAuth.currentUser?.uid
    private val contactListRtDb = Firebase.database.getReference("users/$userId")

    private val contactList: MutableList<Contact> = mutableListOf()

    init {
        contactListRtDb.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val newContact: Contact = snapshot.getValue<Contact>() ?: Contact()

                if (contactList.indexOfFirst { it.name == newContact.name } == -1) {
                    contactList.add(newContact)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val contact: Contact = snapshot.getValue<Contact>() ?: Contact()
                val contactIndex = contactList.indexOfFirst { it.name == contact.name }
                contactList[contactIndex] = contact
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val contact: Contact = snapshot.getValue<Contact>() ?: Contact()
                contactList.remove(contact)
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun create(contact: Contact) {
        contactListRtDb.child(contact.name).setValue(contact)
    }

    override fun findOne(nome: String): Contact =
        contactList[contactList.indexOfFirst { it.name == nome }]

    override fun findAll(): MutableList<Contact> = contactList

    override fun update(contact: Contact) {
        contactListRtDb.child(contact.name).setValue(contact)
    }

    override fun delete(nome: String) {
        contactListRtDb.child(nome).removeValue()
    }

}