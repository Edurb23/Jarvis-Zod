package br.com.zod.activity.UserRegister



import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import br.com.zod.R
import br.com.zod.activity.LoginActivity
import br.com.zod.auth.Api
import br.com.zod.service.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterPhoneActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_phone)


        val edtTelefone = findViewById<EditText>(R.id.edtTelefone)
        val btnProximo = findViewById<Button>(R.id.btnProximo)
        val userId = intent.getStringExtra("userId")!!


        btnProximo.setOnClickListener {
            val telefone = edtTelefone.text.toString().trim()


            if (telefone.isEmpty() || telefone.length < 10) {
                Toast.makeText(
                    this,
                    "Por favor, insira um telefone válido com DDD!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val service = Api.instance.create(ApiService::class.java)
                val telefoneModel = mapOf(
                    "telefone" to telefone,
                    "ddd" to telefone.substring(0, 2),
                    "tipo" to "1",
                    "userId" to userId
                )

                service.registerPhone(telefoneModel).enqueue(object : Callback<Map<String, Any>> {
                    override fun onResponse(
                        call: Call<Map<String, Any>>,
                        response: Response<Map<String, Any>>
                    ) {
                        if (response.isSuccessful) {
                            Toast.makeText(
                                this@RegisterPhoneActivity,
                                "Cadastro finalizado com sucesso!",
                                Toast.LENGTH_SHORT
                            ).show()
                            startActivity(
                                Intent(
                                    this@RegisterPhoneActivity,
                                    LoginActivity::class.java
                                )
                            )
                        } else {
                            Toast.makeText(
                                this@RegisterPhoneActivity,
                                "Erro no cadastro!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                        Toast.makeText(
                            this@RegisterPhoneActivity,
                            "Falha na conexão!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            }
        }
    }
}
