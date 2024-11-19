package br.com.zod.activity.UserRegister

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import br.com.zod.R
import br.com.zod.auth.Api
import br.com.zod.service.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterEmailActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_email)


        val edtEmail = findViewById<EditText>(R.id.edtEmail)
        val edtSenha = findViewById<EditText>(R.id.edtSenha)
        val edtConfirmaSenha = findViewById<EditText>(R.id.edtConfirmaSenha)
        val btnProximo = findViewById<Button>(R.id.btnProximo)


        btnProximo.setOnClickListener {
            val email = edtEmail.text.toString().trim()
            val senha = edtSenha.text.toString().trim()
            val confirmaSenha = edtConfirmaSenha.text.toString().trim()

            if (email.isEmpty() || senha.isEmpty() || confirmaSenha.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha todos os campos!", Toast.LENGTH_SHORT).show()
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Por favor, insira um e-mail válido!", Toast.LENGTH_SHORT).show()
            } else if (senha.length < 6) {
                Toast.makeText(this, "A senha deve ter pelo menos 6 caracteres!", Toast.LENGTH_SHORT).show()
            } else if (senha != confirmaSenha) {
                Toast.makeText(this, "As senhas não coincidem!", Toast.LENGTH_SHORT).show()
            } else {
                val service = Api.instance.create(ApiService::class.java)
                val user = mapOf("email" to email, "senha" to senha)

                service.registerEmail(user).enqueue(object : Callback<Map<String, Any>> {
                    override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                        if (response.isSuccessful) {
                            val loginId = response.body()?.get("loginId").toString()
                            val intent = Intent(this@RegisterEmailActivity, RegisterPesronInformationActivity::class.java)
                            intent.putExtra("loginId", loginId)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this@RegisterEmailActivity, "Erro no cadastro!", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                        Toast.makeText(this@RegisterEmailActivity, "Falha na conexão!", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }
}
