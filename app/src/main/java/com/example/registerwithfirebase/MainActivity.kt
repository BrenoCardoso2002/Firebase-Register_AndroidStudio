package com.example.registerwithfirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    // Cria as variaveis dos campos:
    private lateinit var email: TextInputEditText
    private lateinit var senha: TextInputEditText
    private lateinit var rsenha: TextInputEditText
    private lateinit var cadastrar: Button

    // Cria varaivel de autenticação:
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializa as variaveis dos campos:
        email = findViewById(R.id.txt_Email)
        senha = findViewById(R.id.txt_Senha)
        rsenha = findViewById(R.id.txt_Rsenha)
        cadastrar = findViewById(R.id.btn_cadastrar)

        // incializa variavel de autenticação:
        auth = Firebase.auth

        // Clique do botão cadastrar:
        cadastrar.setOnClickListener{
            // Obtem o texto dos campos:
            val emailS = email.text.toString().trim()
            val senhaS = senha.text.toString()
            val rsenhaS = rsenha.text.toString()

            if (emailS.isEmpty() || senhaS.isEmpty() || rsenhaS.isEmpty()){
                // Erro, há algum campo em branco
                Toast.makeText(this, "Há algum campo em branco!", Toast.LENGTH_LONG).show()
            }else if(senhaS.length < 7){
                // Erro, senha é muito curta
                Toast.makeText(this, "A senha é muito curta!", Toast.LENGTH_LONG).show()
            }else if(senhaS != rsenhaS){
                // Erro, senhas são diferentes
                Toast.makeText(this, "As senhas são diferentes!", Toast.LENGTH_LONG).show()
            }else{
                auth.createUserWithEmailAndPassword(emailS, rsenhaS)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful){
                            // logado com sucesso
                            // vai para outra tela
                            startActivity(Intent(this, Home::class.java))
                            finish()
                        }else{
                            // Aqui tem o tratamento de erro caso não seja cadastrado com sucesso
                            Log.e("erroRegister", task.exception.toString())
                            try {
                                throw task.exception!!
                            } catch (e: FirebaseAuthInvalidCredentialsException) {
                                // erro, o endereço de email está mal formatado
                                Toast.makeText(this, "O endereço de e-mail está mal formatado!", Toast.LENGTH_LONG).show()
                            } catch (e: FirebaseAuthUserCollisionException){
                                // erro o email ja ta cadastrado
                                Toast.makeText(this, "O endereço de e-mail já está sendo usado por outra conta!", Toast.LENGTH_LONG).show()
                            } catch (e: FirebaseAuthException){
                                // erro abrangente
                                Toast.makeText(this, "Erro ao relizar o cadastro!", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // obtem o dado do usuario autal
        val currentUser = auth.currentUser
        // verifica se há algum usuario logado
        // se estiver usuario conectdado (not null) vai para a tela home
        // caso contrario continua na tela de login
        if(currentUser != null){
            // está logado, vai pra tela home
            startActivity(Intent(this, Home::class.java))
            finish()
        }
    }
}