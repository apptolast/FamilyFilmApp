package com.apptolast.familyfilmapp.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.apptolast.familyfilmapp.model.room.GroupTable
import com.apptolast.familyfilmapp.model.room.UserTable
import com.apptolast.familyfilmapp.room.converters.DateConverter
import com.apptolast.familyfilmapp.room.converters.MapStatusConverter
import com.apptolast.familyfilmapp.room.converters.StringListConverter
import com.apptolast.familyfilmapp.room.group.GroupDao
import com.apptolast.familyfilmapp.room.user.UserDao

/**
 * Database class with a singleton Instance object.
 */
@Database(
    entities = [
        UserTable::class,
        GroupTable::class,
    ],
    version = 4,
    exportSchema = true,
)
@TypeConverters(
    value = [
        StringListConverter::class,
        DateConverter::class,
        MapStatusConverter::class,
    ],
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun groupDao(): GroupDao

    companion object {

        private const val APP_DATABASE_NAME = "ffa_database"

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE $USERS_TABLE_NAME ADD COLUMN photoUrl TEXT NOT NULL DEFAULT ''",
                )
            }
        }
        const val USERS_TABLE_NAME = "users_table"
        const val GROUPS_TABLE_NAME = "groups_table"

        // For Singleton instantiation
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase = instance ?: synchronized(this) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, APP_DATABASE_NAME)
                .addMigrations(MIGRATION_3_4)
                .fallbackToDestructiveMigration()
                .build()
    }
}
