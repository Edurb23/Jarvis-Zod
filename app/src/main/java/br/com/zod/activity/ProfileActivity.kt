package br.com.zod.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import br.com.zod.R
import br.com.zod.auth.Api
import br.com.zod.model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import br.com.zod.service.ApiService
import com.google.android.material.navigation.NavigationView
import retrofit2.Retrofit


class ProfileActivity : AppCompatActivity() {

            private lateinit var  drawerLayout: DrawerLayout
            private lateinit var toggle: ActionBarDrawerToggle
            private lateinit var  apiService: ApiService


    override fun onCreate(savedInstanceState: Bundle?){
                super.onCreate(savedInstanceState)
                setContentView(R.layout.activity_profile)


        drawerLayout = findViewById(R.id.drawerLayout)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)


        toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()


        val navigationView = findViewById<NavigationView>(R.id.navigationView)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            handleMenuItemClick(menuItem)
        }


        val btnEditar = findViewById<Button>(R.id.btnEditar)


        btnEditar.setOnClickListener{
            startActivity(Intent(this,EditProfileActivity::class.java))
        }



        apiService = Api.instance.create(ApiService::class.java)


        getUserProfile()

    }



    private fun getUserProfile() {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)


        Log.d("ProfileActivity", "Token recuperado: $token")

        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "Erro na autenticação. Faça login novamente.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }


        apiService.getUser("Bearer $token").enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {

                Log.d("ProfileActivity", "Resposta da API: ${response.code()} - ${response.message()}")


                if (response.isSuccessful) {

                    val user = response.body()


                    user?.let {
                        Log.d("ProfileActivity", "Usuário recuperado: Nome=${it.nome}, Email=${it.email}, CPF=${it.cpf}, RG=${it.rg}, DataNascimento=${it.dataNascimento}")
                    }

                    val nomeTextView: TextView = findViewById(R.id.tvNome)
                    val emailTextView: TextView = findViewById(R.id.tvEmail)
                    val cpfTextView: TextView = findViewById(R.id.tvCpf)
                    val rgTextView: TextView = findViewById(R.id.tvRg)
                    val dataNascimentoTextView: TextView = findViewById(R.id.tvDataNascimento)


                    user?.let {
                        nomeTextView.text = it.nome
                        emailTextView.text = it.email
                        cpfTextView.text = it.cpf
                        rgTextView.text = it.rg
                        dataNascimentoTextView.text = it.dataNascimento
                    }
                } else {
                    Log.e("ProfileActivity", "Erro ao buscar usuário: ${response.message()} - Código: ${response.code()}")
                    Toast.makeText(
                        this@ProfileActivity,
                        "Erro ao buscar usuário: ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.e("ProfileActivity", "Erro de conexão: ${t.message}")
                Toast.makeText(this@ProfileActivity, "Erro de conexão: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }



    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle.syncState()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    private fun handleMenuItemClick(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.nav_home -> {
                startActivity(Intent(this, HomeActivity::class.java))
            }
            R.id.nav_profile -> {
                Toast.makeText(this, "Você já está no perfil.", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_devices -> {
                startActivity(Intent(this, DeviceActivity::class.java))
            }
            R.id.nav_logout -> {

                val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.clear()
                editor.apply()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
        drawerLayout.closeDrawers()
        return true
    }



}
