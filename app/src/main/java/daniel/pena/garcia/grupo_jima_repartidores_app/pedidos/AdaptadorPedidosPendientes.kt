package toledo.luis.recycler

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import daniel.pena.garcia.grupo_jima_repartidores_app.R
import daniel.pena.garcia.grupo_jima_repartidores_app.databinding.FragmentDashboardBinding


class AdaptadorPedidosPendientes(var pedidos_pendientes_list: List<PedidoPendiente>) :
    RecyclerView.Adapter<AdaptadorPedidosPendientes.PedidoPendienteViewHolder>() {

    private var mItemClickListener: ItemClickListener? = null

    class PedidoPendienteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imagen: ImageView = itemView.findViewById(R.id.imagen)
        var nombreTienda: TextView = itemView.findViewById(R.id.nombre_tienda)
        var codigoRastreo: TextView = itemView.findViewById(R.id.codigo_rastreo)
        var gramaje: TextView = itemView.findViewById(R.id.gramaje)
        var cantidadPaquetes: TextView = itemView.findViewById(R.id.cantidad_paquetes)
        var detalles: TextView = itemView.findViewById(R.id.detalles)
        var estado: TextView = itemView.findViewById(R.id.estado)
        var btnEntregar: Button = itemView.findViewById(R.id.btn_entregar)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidoPendienteViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.lista_item, parent, false)
        return PedidoPendienteViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return pedidos_pendientes_list.size
    }

    override fun onBindViewHolder(holder: PedidoPendienteViewHolder, position: Int) {
        val pedidoPendiente: PedidoPendiente = pedidos_pendientes_list[position]

        holder.imagen.setImageResource(pedidoPendiente.imagen)
        holder.nombreTienda.text = pedidoPendiente.nombreTienda
        holder.codigoRastreo.text = pedidoPendiente.codigoRastreo
        holder.gramaje.text = "Gramaje: ${pedidoPendiente.gramaje}"
        holder.cantidadPaquetes.text = "Cantidad Paquetes: " + pedidoPendiente.cantidadPaquetes
        holder.detalles.text = "Detalles extra: " + pedidoPendiente.detalles
        holder.estado.text = "Estado: " + pedidoPendiente.estado

        holder.btnEntregar.setOnClickListener(View.OnClickListener {
            if (mItemClickListener != null) {
                mItemClickListener!!.onItemClick(position)
            }

            val context: Context = holder.itemView.context


            if (pedidoPendiente.estado == "Entregado") {
                pedidoPendiente.estado = "Pendiente"
            } else if (pedidoPendiente.estado == "Pendiente") {
                pedidoPendiente.estado = "Entregado"
            }

            Toast.makeText(
                context,
                "Pedido  ${pedidoPendiente.codigoRastreo} está ${pedidoPendiente.estado}",
                Toast.LENGTH_SHORT
            )
                .show()

            val mutableList = pedidos_pendientes_list.toMutableList()

            mutableList.removeAt(position)

            pedidos_pendientes_list = mutableList.toList()
            notifyDataSetChanged()
        })


    }

    fun addItemClickListener(listener: ItemClickListener) {
        mItemClickListener = listener
    }

    interface ItemClickListener {
        fun onItemClick(position: Int)
    }

}