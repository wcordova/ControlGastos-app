package umg.edu.gt.controlgastos

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import edu.umg.programacion2.gastos.modelo.Categoria
import edu.umg.programacion2.gastos.modelo.Gasto
import kotlinx.coroutines.launch
import umg.edu.gt.controlgastos.databinding.ActivityFormularioGastoBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Pantalla de formulario. Tiene DOS modos:
 *  - CREAR:  se abre sin extra (boton "+" de la lista).
 *  - EDITAR: se abre con el extra EXTRA_ID_GASTO (al tocar un gasto de la lista).
 */
class FormularioGastoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFormularioGastoBinding

    // IMPORTANTE: el MISMO control que usa la lista (ver Dependencias.kt).
    private val control = Dependencias.control

    // null = modo CREAR. Si tiene un Gasto = modo EDITAR (es el gasto original).
    private var gastoEditando: Gasto? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 1. Inflar el layout con View Binding (igual que en la Clase 12).
        binding = ActivityFormularioGastoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        respetarBarrasDelSistema()

        // 2. El enum alimenta el Spinner.
        binding.spCategoria.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item, Categoria.entries
        )

        // 3. ¿Nos enviaron un id? 0 = no vino nada -> modo CREAR.
        val id = intent.getIntExtra(EXTRA_ID_GASTO, 0)
        if (id != 0) {
            lifecycleScope.launch { cargarGasto(id) }
        }

        binding.btnGuardar.setOnClickListener { guardar() }
    }

    /** Modo EDITAR: busca el gasto y llena los campos con sus datos. */
    private suspend fun cargarGasto(id: Int) {
        val gasto = control.buscarPorId(id)
        if (gasto == null) {
            Toast.makeText(this, "No existe el gasto #$id", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        gastoEditando = gasto

        binding.tvTitulo.text = "Editar gasto"
        binding.btnGuardar.text = "Guardar cambios"
        // Cuidado: un EditText se llena con setText(...), no con "text = ...".
        binding.etDescripcion.setText(gasto.descripcion)
        binding.etMonto.setText(gasto.monto.toString())
        // ordinal = posicion en el enum = posicion en el Spinner (lo llenamos con Categoria.entries).
        binding.spCategoria.setSelection(gasto.categoria.ordinal)
    }

    private fun guardar() {
        // 1. Leer y revisar el monto (igual que en el Incremento 2).
        val monto = binding.etMonto.text.toString().toDoubleOrNull()
        if (monto == null) {
            binding.etMonto.error = "Ingresa un monto válido"
            return
        }
        val descripcion = binding.etDescripcion.text.toString().trim()
        val categoria = binding.spCategoria.selectedItem as Categoria

        // 2. Copiamos la propiedad a una variable local: dentro del if Kotlin ya sabe que no es null.
        val original = gastoEditando

        lifecycleScope.launch {
            // 3. if devuelve un valor: el mensaje de error (o null) de registrar o de actualizar.
            val error = if (original == null) {
                control.registrar(
                    Gasto(descripcion = descripcion, monto = monto, categoria = categoria, fecha = fechaDeHoy())
                )
            } else {
                // copy() conserva el id y la fecha originales; solo cambia lo editado.
                control.actualizar(
                    original.copy(descripcion = descripcion, monto = monto, categoria = categoria)
                )
            }

            if (error != null) {
                Toast.makeText(this@FormularioGastoActivity, error, Toast.LENGTH_SHORT).show()
                return@launch
            }

            // 4. Todo bien: cerramos esta pantalla y Android muestra otra vez la lista.
            // IMPORTANTE: NO se abre MainActivity con startActivity (apilaria una segunda lista).
            Toast.makeText(this@FormularioGastoActivity, "Gasto guardado", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    // Cuidado: java.time.LocalDate necesita API 26; con minSdk 24 usamos SimpleDateFormat.
    private fun fechaDeHoy(): String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    /**
     * Receta: con targetSdk 35+ la app se dibuja DEBAJO de la barra de estado.
     * Esto agrega arriba y abajo el espacio que ocupan las barras del sistema.
     */
    private fun respetarBarrasDelSistema() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { vista, insets ->
            val barras = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            vista.updatePadding(top = barras.top, bottom = barras.bottom)
            insets
        }
    }

    // companion object = "static" de Java. La lista usa esta constante para enviar el id.
    companion object {
        const val EXTRA_ID_GASTO = "id_gasto"
    }
}
