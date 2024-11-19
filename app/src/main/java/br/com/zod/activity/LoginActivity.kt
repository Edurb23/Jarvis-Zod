package br.com.zod.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import br.com.zod.R
import br.com.zod.activity.UserRegister.RegisterEmailActivity
import br.com.zod.auth.Api
import br.com.zod.service.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val edtEmail = findViewById<EditText>(R.id.edtEmail)
        val edtSenha = findViewById<EditText>(R.id.edtSenha)
        val btnEntrar = findViewById<Button>(R.id.btnEntrar)
        val txtCriarConta = findViewById<TextView>(R.id.txtCriarConta)

        // Verificar se há um token salvo
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val savedToken = sharedPreferences.getString("token", null)

        Log.d("LoginActivity", "Token salvo: $savedToken")
        if (!savedToken.isNullOrEmpty()) {
            Log.d("LoginActivity", "Token existente encontrado. Redirecionando para HomeActivity.")
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        btnEntrar.setOnClickListener {
            val email = edtEmail.text.toString().trim()
            val senha = edtSenha.text.toString().trim()

            if (email.isNotEmpty() && senha.isNotEmpty()) {
                loginUser(email, senha)
            } else {
                Toast.makeText(this, "Por favor, preencha todos os campos!", Toast.LENGTH_SHORT).show()
            }
        }

        txtCriarConta.setOnClickListener {
            startActivity(Intent(this, RegisterEmailActivity::class.java))
        }
    }

    private fun loginUser(email: String, senha: String) {
        Log.d("LoginActivity", "Tentativa de login para o email: $email")

        val service = Api.instance.create(ApiService::class.java)
        val credentials = mapOf("email" to email, "senha" to senha)

        service.login(credentials).enqueue(object : Callback<Map<String, String>> {
            override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                if (response.isSuccessful) {
                    val token = response.body()?.get("token") ?: ""
                    if (token.isNotEmpty()) {
                        saveToken(token)
                        Log.d("LoginActivity", "Login bem-sucedido. Token salvo: $token")
                        Toast.makeText(this@LoginActivity, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                        finish()
                    } else {
                        Log.e("LoginActivity", "Token não encontrado na resposta do servidor.")
                        Toast.makeText(this@LoginActivity, "Erro ao recuperar o token.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("LoginActivity", "Erro na resposta do login: ${response.message()}")
                    Toast.makeText(this@LoginActivity, "Credenciais inválidas!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                Log.e("LoginActivity", "Erro ao conectar ao servidor: ${t.message}")
                Toast.makeText(this@LoginActivity, "Erro ao conectar ao servidor!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveToken(token: String) {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("token", token)
            apply()
        }
        Log.d("LoginActivity", "Token salvo com sucesso no SharedPreferences.")
    }
}
