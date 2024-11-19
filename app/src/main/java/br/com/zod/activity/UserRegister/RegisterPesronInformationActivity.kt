package br.com.zod.activity.UserRegister

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import br.com.zod.R
import br.com.zod.activity.LoginActivity
import br.com.zod.auth.Api
import br.com.zod.service.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterPesronInformationActivity: Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registerpersoninformation)


        val edtNome = findViewById<EditText>(R.id.edtNome)
        val edtCpf = findViewById<EditText>(R.id.edtCpf)
        val edtRg = findViewById<EditText>(R.id.edtRg)
        val edtDataNascimento = findViewById<EditText>(R.id.edtDataNascimento)
        val btnContinuar = findViewById<Button>(R.id.btnContinuar)
        val txtJaTemConta = findViewById<TextView>(R.id.txtJaTemConta)


        btnContinuar.setOnClickListener {
            val nome = edtNome.text.toString().trim()
            val cpf = edtCpf.text.toString().trim()
            val rg = edtRg.text.toString().trim()
            val dataNascimento = edtDataNascimento.text.toString().trim()
            val loginId = intent.getStringExtra("loginId")!!


            if (nome.isEmpty() || cpf.isEmpty() || rg.isEmpty() || dataNascimento.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha todos os campos!", Toast.LENGTH_SHORT).show()
            } else {
                val service = Api.instance.create(ApiService::class.java)
                val userInfo =
                    mapOf(
                        "nome" to nome,
                        "cpf" to cpf,
                        "rg" to rg,
                        "dataNascimento" to dataNascimento
                        , "loginId" to loginId
                    )

                service.registerUserInfo(userInfo).enqueue(object : Callback<Map<String, Any>> {
                    override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                        if (response.isSuccessful) {
                            val userId = response.body()?.get("userId").toString()
                            val intent = Intent(this@RegisterPesronInformationActivity, RegisterPhoneActivity::class.java)
                            intent.putExtra("userId", userId)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this@RegisterPesronInformationActivity, "Erro no cadastro!", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                        Toast.makeText(this@RegisterPesronInformationActivity, "Falha na conexão!", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }


        txtJaTemConta.setOnClickListener {

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }


}