package br.com.zod.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import br.com.zod.R

import br.com.zod.auth.Api
import br.com.zod.model.User
import br.com.zod.service.ApiService
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class EditProfileActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: androidx.appcompat.app.ActionBarDrawerToggle
    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var btnSave: Button
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)


        drawerLayout = findViewById(R.id.drawerLayout)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        toggle = androidx.appcompat.app.ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById<NavigationView>(R.id.navigationView)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            handleMenuItemClick(menuItem)
        }


        etName = findViewById(R.id.EtName)
        etEmail = findViewById(R.id.etEmail)
        btnSave = findViewById(R.id.btnSave)
        apiService = Api.instance.create(ApiService::class.java)


        loadUserData()


        btnSave.setOnClickListener {
            updateUserProfile()
        }
    }

    private fun loadUserData() {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)

        if (token != null) {
            apiService.getUser("Bearer $token").enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        val user = response.body()
                        user?.let {
                            etName.setText(it.nome)
                            etEmail.setText(it.email)
                        }
                    } else {
                        Toast.makeText(
                            this@EditProfileActivity,
                            "Erro ao carregar usuário",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Toast.makeText(
                        this@EditProfileActivity,
                        "Erro de conexão",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
    }

    private fun updateUserProfile() {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)

        if (token != null) {

            val nameUsuario = mapOf("nome" to etName.text.toString())


            val emailUsuario = mapOf("email" to etEmail.text.toString())


            apiService.updateUser("Bearer $token", nameUsuario).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {

                        apiService.updateEmail("Bearer $token", emailUsuario).enqueue(object : Callback<Void> {
                            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                if (response.isSuccessful) {
                                    Toast.makeText(
                                        this@EditProfileActivity,
                                        "Perfil atualizado com sucesso",
                                        Toast.LENGTH_SHORT
                                    ).show()


                                    val intent = Intent(this@EditProfileActivity, ProfileActivity::class.java)
                                    startActivity(intent)
                                    finish()

                                } else {
                                    Toast.makeText(
                                        this@EditProfileActivity,
                                        "Erro ao atualizar o e-mail",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            override fun onFailure(call: Call<Void>, t: Throwable) {
                                Toast.makeText(
                                    this@EditProfileActivity,
                                    "Erro de conexão ao atualizar o e-mail",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
                    } else {
                        Toast.makeText(
                            this@EditProfileActivity,
                            "Erro ao atualizar o nome",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(
                        this@EditProfileActivity,
                        "Erro de conexão ao atualizar o nome",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        } else {
            Toast.makeText(
                this,
                "Token de autenticação não encontrado",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    private fun handleMenuItemClick(menuItem: MenuItem): Boolean {
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
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
