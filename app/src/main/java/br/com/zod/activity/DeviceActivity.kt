package br.com.zod.activity

import DeviceAdapter
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import br.com.zod.R
import br.com.zod.auth.Api
import br.com.zod.model.ListDevices
import br.com.zod.service.ApiService
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DeviceActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    companion object {
        const val REQUEST_CODE_REGISTER = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device)

        setupDrawerAndToolbar()

        listView = findViewById(R.id.deviceListView)

        // Botão para adicionar dispositivo
        findViewById<Button>(R.id.newButton).setOnClickListener {
            val intent = Intent(this, RegisterDeviceActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_REGISTER)
        }

        getDevices()
    }

    private fun setupDrawerAndToolbar() {
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
    }

    private fun getDevices() {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)

        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "Erro na autenticação. Faça login novamente.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        val apiService = Api.instance.create(ApiService::class.java)

        apiService.getDevices("Bearer $token").enqueue(object : Callback<ListDevices> {
            override fun onResponse(call: Call<ListDevices>, response: Response<ListDevices>) {
                if (response.isSuccessful) {
                    val listDevices = response.body()?.dispositivos ?: mutableListOf()

                    if (listDevices.isEmpty()) {
                        Toast.makeText(this@DeviceActivity, "Nenhum dispositivo encontrado.", Toast.LENGTH_SHORT).show()
                    } else {
                        val adapter = DeviceAdapter(
                            this@DeviceActivity,
                            listDevices.toMutableList()
                        ) {
                            getDevices()
                        }
                        listView.adapter = adapter
                    }
                } else {
                    Toast.makeText(this@DeviceActivity, "Erro ao buscar dispositivos.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ListDevices>, t: Throwable) {
                Toast.makeText(this@DeviceActivity, "Erro de conexão: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_REGISTER && resultCode == RESULT_OK) {
            Log.d("DeviceActivity", "Novo dispositivo registrado, atualizando lista.")
            getDevices()
        }
    }

    private fun handleMenuItemClick(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.nav_home -> startActivity(Intent(this, HomeActivity::class.java))
            R.id.nav_profile -> startActivity(Intent(this, ProfileActivity::class.java))
            R.id.nav_devices -> Toast.makeText(this, "Você já está em Dispositivos.", Toast.LENGTH_SHORT).show()
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
