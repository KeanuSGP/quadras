package com.example.quadras

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ActivityLogin : AppCompatActivity() {
    // Apenas instanciamos a nossa classe de testes isolada
    private val tester = SupabaseTest()
    private val repository = ReservationRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // força o aplicativo a só usar tema claro
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        AppCompatDelegate.setApplicationLocales(
            LocaleListCompat.forLanguageTags("pt-BR")
        )

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
            val intent = Intent(this, ActivityHomeMorador::class.java)

            // Dispara o gatilho dos testes de forma assíncrona
            lifecycleScope.launch{
                val uid = repository.fazerLogin(email,senha)


                if(uid != null){
                    val ehAdmin = repository.ehAdmin(uid)
                    intent.putExtra("user_id",uid)
                    intent.putExtra("ehAdmin", ehAdmin)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@ActivityLogin, "Falha no login. Verifique as credenciais.", Toast.LENGTH_SHORT).show()
                    Log.e("SUPABASE_LOGIN", "[UI] Falha no login. Verifique as credenciais.")
                }

                }
            }
        }
    }