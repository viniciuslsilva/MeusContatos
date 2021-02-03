package br.edu.ifsp.scl.ads.s5.pdm.agenda.model

import android.content.ContentValues
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import br.edu.ifsp.scl.ads.s5.pdm.agenda.R
import br.edu.ifsp.scl.ads.s5.pdm.agenda.model.ContactSqlite.Constants.CONTATO_TABLE
import br.edu.ifsp.scl.ads.s5.pdm.agenda.model.ContactSqlite.Constants.CREATE_CONTATO_TABLE_STATEMENT
import br.edu.ifsp.scl.ads.s5.pdm.agenda.model.ContactSqlite.Constants.EMAIL_COLUMN
import br.edu.ifsp.scl.ads.s5.pdm.agenda.model.ContactSqlite.Constants.LISTA_CONTATOS_DATABASE
import br.edu.ifsp.scl.ads.s5.pdm.agenda.model.ContactSqlite.Constants.NOME_COLUMN
import br.edu.ifsp.scl.ads.s5.pdm.agenda.model.ContactSqlite.Constants.TELEFONE_COLUMN

class ContactSqlite(context: Context) : ContactDAO {
    object Constants {
        val LISTA_CONTATOS_DATABASE = "listaContatos"
        val CONTATO_TABLE = "contato"
        val NOME_COLUMN = "nome"
        val TELEFONE_COLUMN = "telefone"
        val EMAIL_COLUMN = "email"

        val CREATE_CONTATO_TABLE_STATEMENT = "CREATE TABLE IF NOT EXISTS ${CONTATO_TABLE} (" +
                "${NOME_COLUMN} TEXT NOT NULL PRIMARY KEY, " +
                "${TELEFONE_COLUMN} TEXT NOT NULL, " +
                "${EMAIL_COLUMN} TEXT NOT NULL );"
    }

    val contactListDb: SQLiteDatabase

    init {
        contactListDb = context.openOrCreateDatabase(LISTA_CONTATOS_DATABASE, MODE_PRIVATE, null)
        try {
            contactListDb.execSQL(CREATE_CONTATO_TABLE_STATEMENT)
        } catch (se: SQLException) {
            Log.e(context.getString(R.string.app_name), se.toString())
        }
    }

    override fun create(contact: Contact) {
        val values = ContentValues()
        values.put(NOME_COLUMN, contact.name)
        values.put(TELEFONE_COLUMN, contact.telephone)
        values.put(EMAIL_COLUMN, contact.email)

        contactListDb.insert(CONTATO_TABLE, null, values)
    }

    override fun findOne(nome: String): Contact {
        val contactCursor = contactListDb.query(
            true,   // distinct
            CONTATO_TABLE,  // tabela
            null,   // columns
            "${NOME_COLUMN} = ?",  // selection
            arrayOf("${nome}"), // selectionArgs
            null, // groupBy
            null,  // having
            null, // orderBy
            null // limit
        )

        return if (contactCursor.moveToFirst())
            Contact(
                contactCursor.getString(contactCursor.getColumnIndex(NOME_COLUMN)),
                contactCursor.getString(contactCursor.getColumnIndex(TELEFONE_COLUMN)),
                contactCursor.getString(contactCursor.getColumnIndex(EMAIL_COLUMN))
            )
        else
            Contact()
    }

    override fun findAll(): MutableList<Contact> {
        val contactsList: MutableList<Contact> = mutableListOf()

        val query = "SELECT * FROM ${CONTATO_TABLE};"
        val cursor = contactListDb.rawQuery(query, null)

        while (cursor.moveToNext()) {
            contactsList.add(
                Contact(
                    cursor.getString(cursor.getColumnIndex(NOME_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(TELEFONE_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(EMAIL_COLUMN))
                )
            )
        }
        return contactsList
    }

    override fun update(contact: Contact) {
        val values = ContentValues()
        values.put(TELEFONE_COLUMN, contact.telephone)
        values.put(EMAIL_COLUMN, contact.email)

        contactListDb.update(
            CONTATO_TABLE, // table
            values,        // values
            "${NOME_COLUMN} = ?", // whereClause
            arrayOf(contact.name) // whereArgs
        )
    }

    override fun delete(name: String) {
        contactListDb.delete(
            CONTATO_TABLE,        // table
            "${NOME_COLUMN} = ?", // whereClause
            arrayOf(name)         // whereArgs
        )
    }
}