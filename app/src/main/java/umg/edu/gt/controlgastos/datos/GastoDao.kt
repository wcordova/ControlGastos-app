package umg.edu.gt.controlgastos.datos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

/**
 * DAO = Data Access Object: el MISMO patron que usamos con JDBC y MySQL (Clase 5).
 * La diferencia: aqui solo escribimos la INTERFACE. Room genera la clase que la implementa
 * (GastoDao_Impl) al compilar, con todo el codigo de Cursor, ContentValues, etc.
 *
 * IMPORTANTE: todas las funciones son suspend. Room las ejecuta en OTRO hilo; si no lo
 * fueran, la app se cerraria con "Cannot access database on the main thread".
 */
@Dao
interface GastoDao {

    // Devuelve el id que SQLite genero para la fila nueva (Long).
    @Insert
    suspend fun insertar(gasto: GastoEntity): Long

    // Cuidado: ordenamos por id (orden de llegada), igual que la lista en memoria.
    // El orden "mas reciente primero" lo decide ControlGastos.listar(), no el DAO.
    @Query("SELECT * FROM gastos ORDER BY id")
    suspend fun listar(): List<GastoEntity>

    // :id se reemplaza por el parametro id. Es el "?" del PreparedStatement de JDBC.
    @Query("SELECT * FROM gastos WHERE id = :id")
    suspend fun buscarPorId(id: Int): GastoEntity?

    // Busca la fila por la llave primaria y la reemplaza. Devuelve cuantas filas cambio.
    @Update
    suspend fun actualizar(gasto: GastoEntity): Int

    // Devuelve cuantas filas borro (0 si el id no existia).
    @Query("DELETE FROM gastos WHERE id = :id")
    suspend fun eliminar(id: Int): Int
}
