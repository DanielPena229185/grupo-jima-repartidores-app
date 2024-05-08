package daniel.pena.garcia.grupo_jima_repartidores_app.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import daniel.pena.garcia.grupo_jima_repartidores_app.R
import daniel.pena.garcia.grupo_jima_repartidores_app.databinding.FragmentDashboardBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import toledo.luis.recycler.AdaptadorPedidosPendientes
import toledo.luis.recycler.PedidoPendiente

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        dashboardViewModel.text.observe(viewLifecycleOwner) {


        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    data class Pedido(
        val id: Int,
        val codigoRastreo: String,
        val detalles: String,
        val estado: String,
        val total: Number,
        var numeroRecorrido: String,
        var tortilleria: Tortilleria
    )

    data class Tortilleria(
        val telefono: String,
        val nombre: String,
        val direccion: String
    )

    // Retrofit service interface
    interface PedidoService {
        @GET("pedido/repartidor/2/listo")
        fun getPedidos(): Call<List<Pedido>>

        @GET("pedido/repartidor/2/entregado")
        fun getPedidosFinalizados(): Call<List<Pedido>>

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val url = "http://192.168.100.12:3000/"

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service: PedidoService = retrofit.create(PedidoService::class.java)

        GlobalScope.launch(Dispatchers.IO) {
            val call: Call<List<Pedido>> = service.getPedidos()
            val response = call.execute()

            if (response.isSuccessful) {
                val pedidosRes = response.body()
                // Check if pedidosRes is not null and not empty
                if (!pedidosRes.isNullOrEmpty()) {
                    val pedidosPendientes: ArrayList<PedidoPendiente> = ArrayList()
                    for (pedido in pedidosRes) {
                        pedidosPendientes.add(
                            PedidoPendiente(
                                id = pedido.id,
                                codigoRastreo = pedido.codigoRastreo,
                                estado = pedido.estado,
                                detalles = pedido.detalles,
                                imagen = R.drawable.tortillas,
                                gramaje = pedido.numeroRecorrido,
                                nombreTienda = pedido.tortilleria.nombre,
                                cantidadPaquetes = 5,
                            )
                        )
                    }
                    // Set the adapter inside the coroutine scope
                    withContext(Dispatchers.Main) {
                        val recyclerView: RecyclerView = view.findViewById(R.id.lista_view)
                        recyclerView.layoutManager = LinearLayoutManager(requireContext())
                        recyclerView.adapter = AdaptadorPedidosPendientes(pedidosPendientes)
                    }
                    println("Pedidos realizados")
                    println(pedidosPendientes)
                } else {
                    println("Empty list of pedidos")
                }
            } else {
                println("Failed to fetch pedidos: ${response.code()}")
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}