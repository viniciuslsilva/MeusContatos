package br.edu.ifsp.scl.ads.s5.pdm.agenda.view

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import br.edu.ifsp.scl.ads.s5.pdm.agenda.R
import br.edu.ifsp.scl.ads.s5.pdm.agenda.adapter.AutenticadorFirebase
import br.edu.ifsp.scl.ads.s5.pdm.agenda.adapter.ContatosAdapter
import br.edu.ifsp.scl.ads.s5.pdm.agenda.adapter.OnContatoClickListener
import br.edu.ifsp.scl.ads.s5.pdm.agenda.controller.ContatoController
import br.edu.ifsp.scl.ads.s5.pdm.agenda.databinding.ActivityMainBinding
import br.edu.ifsp.scl.ads.s5.pdm.agenda.model.Contact
import br.edu.ifsp.scl.ads.s5.pdm.agenda.view.MainActivity.Extras.EXTRA_CONTATO
import br.edu.ifsp.scl.ads.s5.pdm.agenda.view.MainActivity.Extras.VISUALIZAR_CONTATO_ACTION

class MainActivity : AppCompatActivity(), OnContatoClickListener {
    // Data source do Adapter
    private lateinit var contatosList: MutableList<Contact>

    // Adapter do RecyclerView
    private lateinit var contatosAdapter: ContatosAdapter

    // LayoutManager do RecyclerView
    private lateinit var contatosLayoutManager: LinearLayoutManager

    // Classe ViewBinding para evitar chamadas a findViewById
    private lateinit var activityMainBinding: ActivityMainBinding

    // Constantes para a ContatoActivity
    private val NOVO_CONTATO_REQUEST_CODE = 0
    private val EDITAR_CONTATO_REQUEST_CODE = 1

    object Extras {
        val EXTRA_CONTATO = "EXTRA_CONTATO"
        val VISUALIZAR_CONTATO_ACTION = "VISUALIZAR_CONTATO_ACTION"
    }

    // Controller
    private lateinit var contatoController: ContatoController

    override fun onStart() {
        super.onStart()

        val email = AutenticadorFirebase.firebaseAuth.currentUser?.email
        if (email == null) {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Instancia classe de ViewBinding e seta layout como sendo o root
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        // Instanciando Controller
        contatoController = ContatoController(this)

        // Inicializando lista de contatos para o Adapter
        contatosList = mutableListOf()
        val populaContatosListAt = object : AsyncTask<Void, Void, List<Contact>>() {
            override fun doInBackground(vararg p0: Void?): List<Contact> {
                // Thread filha
                Thread.sleep(5000)
                return contatoController.buscaContatos()
            }

            override fun onPreExecute() {
                super.onPreExecute()
                // Thread de GUI
                activityMainBinding.contatosListPb.visibility = View.VISIBLE
                activityMainBinding.listaContatosRv.visibility = View.GONE
            }

            override fun onPostExecute(result: List<Contact>?) {
                super.onPostExecute(result)
                // Thread de GUI
                activityMainBinding.contatosListPb.visibility = View.GONE
                activityMainBinding.listaContatosRv.visibility = View.VISIBLE
                if (result != null) {
                    contatosList.clear()
                    contatosList.addAll(result)
                    contatosAdapter.notifyDataSetChanged()
                }
            }
        }
        populaContatosListAt.execute()

        // Instanciando o LayoutManager
        contatosLayoutManager = LinearLayoutManager(this)

        // Instanciando o Adapter
        contatosAdapter = ContatosAdapter(contatosList, this)

        // Associar o Adapter e o LayoutManager com o RecyclerView
        activityMainBinding.listaContatosRv.adapter = contatosAdapter
        activityMainBinding.listaContatosRv.layoutManager = contatosLayoutManager
    }

    override fun onContatoClick(position: Int) {
        val contact: Contact = contatosList[position]
        val visualizarContatoIntent = Intent(this, ContatoActivity::class.java)

        visualizarContatoIntent.putExtra(EXTRA_CONTATO, contact)
        visualizarContatoIntent.action = VISUALIZAR_CONTATO_ACTION

        startActivity(visualizarContatoIntent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.novoContatoMi -> {
            val novoContantoIntent = Intent(this, ContatoActivity::class.java)
            startActivityForResult(novoContantoIntent, NOVO_CONTATO_REQUEST_CODE)
            true
        }
        R.id.sairMi -> {
            AutenticadorFirebase.firebaseAuth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            true
        }
        else -> false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == NOVO_CONTATO_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            data.getParcelableExtra<Contact>(EXTRA_CONTATO)?.let { contact ->
                contatoController.insereContato(contact)
                contatosList.add(contact)
                contatosAdapter.notifyDataSetChanged()
            }
        } else {
            if (requestCode == EDITAR_CONTATO_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
                data.getParcelableExtra<Contact>(EXTRA_CONTATO)?.let { contact ->
                    contatoController.atualizaContato(contact)
                    contatosList[contatosList.indexOfFirst { it.name == contact.name }] = contact
                    contatosAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    override fun onEditarMenuItemClick(position: Int) {
        val contactSelecionado: Contact = contatosList[position]
        val editarContatoIntent = Intent(this, ContatoActivity::class.java)

        editarContatoIntent.putExtra(EXTRA_CONTATO, contactSelecionado)
        startActivityForResult(editarContatoIntent, EDITAR_CONTATO_REQUEST_CODE)
    }

    override fun onRemoverMenuItemClick(position: Int) {
        val contatoExcluido = contatosList[position]

        if (position != -1) {
            contatoController.removeContato(contatoExcluido.name)
            contatosList.removeAt(position)
            contatosAdapter.notifyDataSetChanged()
        }
    }
}