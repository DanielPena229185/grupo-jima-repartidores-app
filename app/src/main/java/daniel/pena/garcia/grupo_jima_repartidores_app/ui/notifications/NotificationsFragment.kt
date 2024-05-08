package daniel.pena.garcia.grupo_jima_repartidores_app.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import daniel.pena.garcia.grupo_jima_repartidores_app.databinding.FragmentNotificationsBinding
import daniel.pena.garcia.grupo_jima_repartidores_app.ui.dashboard.DashboardFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        cargarGrafica()


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun cargarGrafica() {
        val url = "http://192.168.100.12:3000/"

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service: DashboardFragment.PedidoService =
            retrofit.create(DashboardFragment.PedidoService::class.java)

        GlobalScope.launch(Dispatchers.IO) {
            val call: Call<List<DashboardFragment.Pedido>> = service.getPedidosFinalizados()
            val response = call.execute()

            val call2: Call<List<DashboardFragment.Pedido>> = service.getPedidos()
            val response2 = call2.execute()

            if (response.isSuccessful) {
                val pedidosRes = response.body()
                // Check if pedidosRes is not null and not empty

                var cantidadPedidosFinalizados = pedidosRes?.size
                // Set the adapter inside the coroutine scope

                println(cantidadPedidosFinalizados)

                val pedidosRes2 = response2.body()

                var cantidadPedidosPendientes = pedidosRes2?.size
                // Set the adapter inside the coroutine scope

                println(cantidadPedidosPendientes)

                if (cantidadPedidosPendientes != null && cantidadPedidosFinalizados != null) {
                    val totalOrdenes = cantidadPedidosFinalizados + cantidadPedidosPendientes
                    val ordenesPendientesWeight = cantidadPedidosPendientes.toFloat() / totalOrdenes



                    withContext(Dispatchers.Main) {

                        val pendingOrdersBar = binding.viewPedidosTotales

                        val layoutParams =
                            pendingOrdersBar.layoutParams as LinearLayout.LayoutParams

                        layoutParams.weight = ordenesPendientesWeight

                        pendingOrdersBar.layoutParams = layoutParams

                         binding.tvPedidosPendientes.text = "Pedidos Pendientes: " + cantidadPedidosPendientes.toString()
                        binding.tvPedidosCompletados.text = "Pedidos Completados: " + totalOrdenes

                    }
                }


            } else {
                println("Failed to fetch pedidos: ${response.code()}")
            }
        }
    }
}