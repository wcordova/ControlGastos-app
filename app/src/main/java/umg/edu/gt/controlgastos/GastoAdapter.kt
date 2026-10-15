package umg.edu.gt.controlgastos

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.umg.programacion2.gastos.modelo.Gasto
import edu.umg.programacion2.gastos.servicio.formatearQuetzales
import umg.edu.gt.controlgastos.databinding.ItemGastoBinding

/**
 * El "puente" entre la lista de datos (List<Gasto>) y las filas que se ven en pantalla.
 *
 * alTocarGasto es una FUNCION que recibe la Activity. El Adapter no sabe que pasa al tocar
 * un gasto (abrir el formulario, borrar, ...): solo avisa. La Activity decide.
 */
class GastoAdapter(
    private val alTocarGasto: (Gasto) -> Unit
) : RecyclerView.Adapter<GastoAdapter.GastoViewHolder>() {

    // Los datos que se muestran. Empieza vacia; la Activity la llena con mostrar(...).
    private var gastos: List<Gasto> = emptyList()

    /** Guarda las vistas de UNA fila para poder reutilizarla (reciclarla). */
    class GastoViewHolder(val binding: ItemGastoBinding) : RecyclerView.ViewHolder(binding.root)

    // 1. ¿Cuantas filas hay?
    override fun getItemCount(): Int = gastos.size

    // 2. Crear una fila NUEVA. RecyclerView lo llama solo unas pocas veces.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GastoViewHolder {
        // Cuidado: el ultimo parametro va en false; RecyclerView agrega la fila por su cuenta.
        val binding = ItemGastoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GastoViewHolder(binding)
    }

    // 3. Poner los datos del gasto "position" en una fila (nueva o reciclada).
    override fun onBindViewHolder(holder: GastoViewHolder, position: Int) {
        val gasto = gastos[position]
        holder.binding.tvDescripcion.text = gasto.descripcion
        holder.binding.tvDetalle.text = "${gasto.categoria} · ${gasto.fecha}"
        holder.binding.tvMonto.text = formatearQuetzales(gasto.monto)
        holder.binding.root.setOnClickListener { alTocarGasto(gasto) }
    }

    /** Reemplaza los datos y le pide a RecyclerView que redibuje la lista. */
    @SuppressLint("NotifyDataSetChanged") // forma simple; la mejorada es ListAdapter + DiffUtil
    fun mostrar(nuevos: List<Gasto>) {
        gastos = nuevos
        notifyDataSetChanged()
    }
}
