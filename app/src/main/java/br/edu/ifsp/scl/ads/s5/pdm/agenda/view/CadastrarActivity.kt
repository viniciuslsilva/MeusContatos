package br.edu.ifsp.scl.ads.s5.pdm.agenda.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import br.edu.ifsp.scl.ads.s5.pdm.agenda.adapter.AutenticadorFirebase
import br.edu.ifsp.scl.ads.s5.pdm.agenda.databinding.ActivityCadastrarBinding

class CadastrarActivity : AppCompatActivity() {

    private lateinit var activityCadastrarBinding: ActivityCadastrarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityCadastrarBinding = ActivityCadastrarBinding.inflate(layoutInflater)

        setContentView(activityCadastrarBinding.root)
    }

    fun onClick(view: View) {
        if (view == activityCadastrarBinding.cadastrarBt) {
            val email = activityCadastrarBinding.emailEt.text.toString()
            val password = activityCadastrarBinding.senhaEt.text.toString()
            val passwordConfirm = activityCadastrarBinding.repetirSenhaEt.text.toString()

            if (isValidValues(email, password, passwordConfirm)) {
                AutenticadorFirebase.firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Usuário $email criado com sucesso.", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this, "Erro na criação do usuário.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Campos preenchidos incorretamente.", Toast.LENGTH_SHORT).show()
                activityCadastrarBinding.emailEt.requestFocus()
            }
        }
    }

    private fun isValidValues(email: String, senha: String, repetirSenha: String): Boolean {
        return if (email.isBlank() || email.isEmpty())
            false
        else if (senha.isBlank() || senha.isEmpty())
            false
        else if (repetirSenha.isBlank() || repetirSenha.isEmpty())
            false
        else senha == repetirSenha
    }

}
