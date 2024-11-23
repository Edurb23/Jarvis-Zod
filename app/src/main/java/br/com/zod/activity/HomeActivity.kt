package br.com.zod.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import br.com.zod.R
import br.com.zod.auth.Api
import br.com.zod.model.ConsumoDispositivo
import br.com.zod.model.ConsumoTotal
import br.com.zod.service.ApiService
import br.com.zod.views.PieChartView
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var pieChartView: PieChartView
    private lateinit var valueConsumoTotal: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


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


        pieChartView = findViewById(R.id.pieChartView)
        valueConsumoTotal = findViewById(R.id.valueConsumoTotal)


        loadPieChartData()
        loadConsumoTotal()
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
                Toast.makeText(this, "Você já está na Home.", Toast.LENGTH_SHORT).show()
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


    private fun loadPieChartData() {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null) ?: run {
            Toast.makeText(this, "Erro na autenticação. Faça login novamente.", Toast.LENGTH_SHORT).show()
            return
        }

        val apiService = Api.instance.create(ApiService::class.java)
        apiService.getConsumoDetalhado("Bearer $token").enqueue(object : Callback<List<ConsumoDispositivo>> {
            override fun onResponse(
                call: Call<List<ConsumoDispositivo>>,
                response: Response<List<ConsumoDispositivo>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { data ->
                        pieChartView.setConsumoDispositivos(data)
                    }
                } else {
                    Toast.makeText(this@HomeActivity, "Erro ao carregar gráfico.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<ConsumoDispositivo>>, t: Throwable) {
                Toast.makeText(this@HomeActivity, "Falha ao carregar gráfico: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Função para carregar o consumo total
    private fun loadConsumoTotal() {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null) ?: run {
            Toast.makeText(this, "Erro na autenticação. Faça login novamente.", Toast.LENGTH_SHORT).show()
            return
        }

        val apiService = Api.instance.create(ApiService::class.java)
        apiService.getConsumoTotal("Bearer $token").enqueue(object : Callback<ConsumoTotal> {
            override fun onResponse(call: Call<ConsumoTotal>, response: Response<ConsumoTotal>) {
                if (response.isSuccessful) {
                    response.body()?.let { data ->
                        valueConsumoTotal.text = data.consumoTotal
                    }
                } else {
                    Toast.makeText(this@HomeActivity, "Erro ao carregar consumo total.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ConsumoTotal>, t: Throwable) {
                Toast.makeText(this@HomeActivity, "Falha ao carregar consumo total: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
