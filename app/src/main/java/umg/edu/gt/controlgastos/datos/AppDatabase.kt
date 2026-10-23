package umg.edu.gt.controlgastos.datos

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * La base de datos completa: que tablas tiene y que DAOs ofrece.
 *
 * - entities: las clases @Entity (una tabla por cada una).
 * - version:  numero del "esquema". Si cambias GastoEntity (ej. agregas un campo),
 *             hay que subirlo; si no, la app se cierra al abrir (ver la guia).
 * - exportSchema = false: no guardamos el historial del esquema en archivos JSON
 *             (se usa en proyectos grandes para probar migraciones).
 *
 * Es abstract: Room genera AppDatabase_Impl al compilar.
 */
@Database(entities = [GastoEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gastoDao(): GastoDao
}
