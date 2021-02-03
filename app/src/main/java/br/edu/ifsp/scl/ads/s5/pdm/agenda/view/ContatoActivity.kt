package br.edu.ifsp.scl.ads.s5.pdm.agenda.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import br.edu.ifsp.scl.ads.s5.pdm.agenda.adapter.AutenticadorFirebase
import br.edu.ifsp.scl.ads.s5.pdm.agenda.databinding.ActivityContatoBinding
import br.edu.ifsp.scl.ads.s5.pdm.agenda.model.Contact

class ContatoActivity : AppCompatActivity() {
    private lateinit var activityContatoBinding: ActivityContatoBinding

    private val userId = AutenticadorFirebase.firebaseAuth.currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityContatoBinding = ActivityContatoBinding.inflate(layoutInflater)
        setContentView(activityContatoBinding.root)

        intent.getParcelableExtra<Contact?>(MainActivity.Extras.EXTRA_CONTATO)?.let { contact ->
            activityContatoBinding.nomeContatoEt.setText(contact.name)
            activityContatoBinding.nomeContatoEt.isEnabled = false
            activityContatoBinding.telefoneContatoEt.setText(contact.telephone)
            activityContatoBinding.emailContatoEt.setText(contact.email)

            if (intent.action == MainActivity.Extras.VISUALIZAR_CONTATO_ACTION) {
                activityContatoBinding.telefoneContatoEt.isEnabled = false
                activityContatoBinding.emailContatoEt.isEnabled = false
                activityContatoBinding.salvarBt.visibility = View.GONE
            }
        }

        activityContatoBinding.salvarBt.setOnClickListener {
            userId?.let {
                val contact = Contact(
                    activityContatoBinding.nomeContatoEt.text.toString(),
                    userId,
                    activityContatoBinding.telefoneContatoEt.text.toString(),
                    activityContatoBinding.emailContatoEt.text.toString()
                )

                val intent = Intent()
                intent.putExtra(MainActivity.Extras.EXTRA_CONTATO, contact)
                setResult(RESULT_OK, intent)
                finish()
            }
        }
    }
}