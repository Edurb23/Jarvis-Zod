package br.com.zod.activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.zod.R
import br.com.zod.auth.Api
import br.com.zod.model.Device
import br.com.zod.service.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterDeviceActivity : AppCompatActivity() {

    private lateinit var nameInput: EditText
    private lateinit var wattsInput: EditText
    private lateinit var descriptionInput: EditText
    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_device)

        Log.d("RegisterDeviceActivity", "onCreate iniciado")


        nameInput = findViewById(R.id.deviceName)
        wattsInput = findViewById(R.id.deviceVoltage)
        descriptionInput = findViewById(R.id.deviceDescription)
        saveButton = findViewById(R.id.continueButton)

        Log.d("RegisterDeviceActivity", "Componentes inicializados")


        saveButton.setOnClickListener {
            Log.d("RegisterDeviceActivity", "Botão de salvar clicado")
            registerDevice()
        }
    }

    private fun retrieveToken(): String? {
        Log.d("RegisterDeviceActivity", "Tentando recuperar token do SharedPreferences")
        val sharedPreferences = getSharedPreferences("AuthPrefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("authToken", null)

        if (token.isNullOrEmpty()) {
            Log.e("RegisterDeviceActivity", "Token não encontrado ou está nulo")
            Toast.makeText(this, "Erro na autenticação. Faça login novamente.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return null
        }

        Log.d("RegisterDeviceActivity", "Token recuperado: $token")
        return token
    }

    private fun registerDevice() {
        val token = retrieveToken()
        if (token == null) {
            Log.e("RegisterDeviceActivity", "Token não recuperado, registro cancelado")
            return
        }

        Log.d("RegisterDeviceActivity", "Preparando para registrar dispositivo")


        val nome = nameInput.text.toString().trim()
        val watts = wattsInput.text.toString().trim()
        val descricao = descriptionInput.text.toString().trim()

        Log.d("RegisterDeviceActivity", "Validando campos: nome='$nome', watts='$watts', descricao='$descricao'")

        if (nome.isEmpty() || watts.isEmpty()) {
            Toast.makeText(this, "Os campos Nome e Voltagem são obrigatórios.", Toast.LENGTH_SHORT).show()
            Log.e("RegisterDeviceActivity", "Campos obrigatórios vazios")
            return
        }


        val device = Device(
            nome = nome,
            watts = watts.toInt(),
            descricao = if (descricao.isEmpty()) null else descricao,
            horasPorDia = 12,
            diasNoMes = 30,
            precoKWh = 0.6
        )

        Log.d("RegisterDeviceActivity", "Objeto do dispositivo criado: $device")


        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Cadastrando dispositivo...")
        progressDialog.setCancelable(false)
        progressDialog.show()


        val apiService = Api.instance.create(ApiService::class.java)


        apiService.registerDevice("Bearer $token", device).enqueue(object : Callback<Map<String, Any>> {
            override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                progressDialog.dismiss() // Esconde o loading

                if (response.isSuccessful) {
                    Log.d("RegisterDeviceActivity", "Resposta da API: sucesso, dispositivo registrado")
                    showSuccessDialog("Dispositivo cadastrado com sucesso!")
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("RegisterDeviceActivity", "Erro ao registrar dispositivo: $errorBody")
                    Toast.makeText(this@RegisterDeviceActivity, "Erro ao cadastrar dispositivo.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                progressDialog.dismiss() // Esconde o loading
                Log.e("RegisterDeviceActivity", "Erro de conexão ao registrar dispositivo: ${t.message}")
                Toast.makeText(this@RegisterDeviceActivity, "Erro de conexão: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showSuccessDialog(message: String) {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Sucesso")
            .setMessage(message)
            .setPositiveButton("OK") { _, _ ->

                setResult(RESULT_OK)
                finish()
            }
            .show()
    }

}
