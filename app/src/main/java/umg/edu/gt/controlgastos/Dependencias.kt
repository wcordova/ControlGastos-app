package umg.edu.gt.controlgastos

import edu.umg.programacion2.gastos.datos.GastoRepositoryEnMemoria
import edu.umg.programacion2.gastos.servicio.ControlGastos

/**
 * Las piezas que TODA la app comparte.
 *
 * object = una clase de la que existe UN SOLO objeto. Kotlin lo crea la primera vez que
 * alguien escribe "Dependencias.control" y despues siempre devuelve ese mismo objeto.
 *
 * IMPORTANTE: por eso la lista y el formulario ven los MISMOS gastos. Si cada Activity
 * hiciera ControlGastos(GastoRepositoryEnMemoria()), cada una tendria su propia lista.
 *
 * En la semana 4 solo cambia esta linea: GastoRepositoryEnMemoria() -> GastoRepositoryRoom(...)
 */
object Dependencias {
    val control = ControlGastos(GastoRepositoryEnMemoria())
}
