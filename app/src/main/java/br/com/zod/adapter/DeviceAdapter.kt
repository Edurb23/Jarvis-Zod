import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import br.com.zod.R
import br.com.zod.auth.Api
import br.com.zod.service.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DeviceAdapter(
    private val context: Context,
    private val devices: MutableList<String>,
    private val onDeviceDeleted: () -> Unit
) : ArrayAdapter<String>(context, R.layout.item_device, devices) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_device, parent, false)
        val deviceName = view.findViewById<TextView>(R.id.deviceName)
        val btnDelete = view.findViewById<ImageButton>(R.id.btnDelete)

        deviceName.text = devices[position]


        btnDelete.setOnClickListener {
            showDeleteConfirmationDialog(devices[position], position)
        }

        return view
    }

    private fun showDeleteConfirmationDialog(deviceName: String, position: Int) {
        AlertDialog.Builder(context)
            .setTitle("Confirmar Exclusão")
            .setMessage("Tem certeza que deseja excluir o dispositivo \"$deviceName\"?")
            .setPositiveButton("Excluir") { _, _ ->
                deleteDevice(deviceName, position)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun deleteDevice(deviceName: String, position: Int) {
        val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)

        if (token.isNullOrEmpty()) {
            Toast.makeText(context, "Erro na autenticação. Faça login novamente.", Toast.LENGTH_SHORT).show()
            return
        }


        val Loading = ProgressDialog(context)
        Loading.setMessage("Excluindo dispositivo...")
        Loading.setCancelable(false)
        Loading.show()

        val apiService = Api.instance.create(ApiService::class.java)
        apiService.deleteDevice("Bearer $token", deviceName).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Loading.dismiss() //

                if (response.isSuccessful) {
                    devices.removeAt(position)
                    notifyDataSetChanged()
                    onDeviceDeleted()

                    showSuccessDialog("Dispositivo excluído com sucesso!")
                } else {
                    Toast.makeText(context, "Erro ao excluir dispositivo.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Loading.dismiss()
                Toast.makeText(context, "Erro de conexão: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showSuccessDialog(message: String) {
        AlertDialog.Builder(context)
            .setTitle("Sucesso")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
}
