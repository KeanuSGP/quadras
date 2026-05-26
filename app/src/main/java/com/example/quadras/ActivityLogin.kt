package com.example.quadras

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class ActivityLogin : AppCompatActivity() {

    // Apenas instanciamos a nossa classe de testes isolada
    private val tester = SupabaseTest()
    private val repository = ReservationRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_morador)

        //components
        val edtEmail = findViewById<EditText>(R.id.editTextTextEmailMorador)
        val edtSenha = findViewById<EditText>(R.id.editTextTextPasswordSenhaMorador)
        val btnConfirmar = findViewById<Button>(R.id.buttonLoginMorador)

        //click action
        btnConfirmar.setOnClickListener {
            val email = edtEmail.text.toString().trim()
            val senha = edtSenha.text.toString().trim()

            if (email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Log.d("SUPABASE_TESTE", "[UI] Botão clicado. Iniciando processo para: $email")
            Toast.makeText(this, "Autenticando...", Toast.LENGTH_SHORT).show()

            // Dispara o gatilho dos testes de forma assíncrona
            lifecycleScope.launch {
                val uid = repository.fazerLogin(email,senha)

                if(uid != null){
                    Log.d("SUPABASE_TESTE", "[UI] Autenticado! Iniciando testes de banco...")
                    Toast.makeText(this@ActivityLogin, "Login OK! Rodando testes no Logcat.", Toast.LENGTH_LONG).show()

                    // Roda a sequência de testes passando o UID dinâmico
                    tester.rodarTodosOsTestes(uid)
                } else {
                    Log.e("SUPABASE_TESTE", "[UI] Falha no login. Verifique as credenciais.")
                    Toast.makeText(this@ActivityLogin, "Erro no login! Olhe o Logcat.", Toast.LENGTH_SHORT).show()
                }

                }
            }
        }
    }