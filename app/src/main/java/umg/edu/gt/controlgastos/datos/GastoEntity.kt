package umg.edu.gt.controlgastos.datos

import androidx.room.Entity
import androidx.room.PrimaryKey
import edu.umg.programacion2.gastos.modelo.Categoria
import edu.umg.programacion2.gastos.modelo.Gasto

/**
 * Una FILA de la tabla "gastos" en SQLite.
 *
 * ¿Por que no ponemos @Entity directamente sobre Gasto? Porque Gasto vive en la libreria
 * gastos-core, que es Kotlin puro y NO conoce Android ni Room (por eso tambien corre en consola).
 * Entonces el proyecto Android tiene su propia clase "espejo" y la convierte de ida y vuelta.
 *
 *   Gasto (libreria, reglas del negocio)  <-- aGasto() / desde() -->  GastoEntity (tabla SQLite)
 */
@Entity(tableName = "gastos")
data class GastoEntity(
    // 1. Llave primaria. autoGenerate = true es el AUTO_INCREMENT de MySQL:
    //    si llega id = 0, SQLite le asigna el siguiente numero.
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val descripcion: String,
    val monto: Double,
    // 2. SQLite no sabe que es un enum: guardamos su NOMBRE como texto ("ALIMENTACION").
    // Cuidado: no guardes toString() ("Alimentación", puede cambiar) ni ordinal (cambia si
    // alguien reordena el enum). name es el valor mas estable.
    val categoria: String,
    val fecha: String
) {
    /** Fila de la tabla -> objeto de la libreria. */
    fun aGasto(): Gasto = Gasto(
        id = id,
        descripcion = descripcion,
        monto = monto,
        categoria = Categoria.valueOf(categoria),  // "ALIMENTACION" -> Categoria.ALIMENTACION
        fecha = fecha
    )

    companion object {
        /** Objeto de la libreria -> fila de la tabla. */
        fun desde(gasto: Gasto): GastoEntity = GastoEntity(
            id = gasto.id,
            descripcion = gasto.descripcion,
            monto = gasto.monto,
            categoria = gasto.categoria.name,      // Categoria.ALIMENTACION -> "ALIMENTACION"
            fecha = gasto.fecha
        )
    }
}
