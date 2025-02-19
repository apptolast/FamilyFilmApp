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
import com.apptolast.familyfilmapp.room.converters.IntListConverter
import com.apptolast.familyfilmapp.room.converters.SelectedMovieListConverter
import com.apptolast.familyfilmapp.room.converters.StringListConverter
import com.apptolast.familyfilmapp.room.converters.UserListConverter
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
        UserListConverter::class,
        IntListConverter::class,
        StringListConverter::class,
        DateConverter::class,
        SelectedMovieListConverter::class,
    ],
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun groupDao(): GroupDao

    companion object {

        private const val APP_DATABASE_NAME = "ffa_database"
        const val USERS_TABLE_NAME = "users_table"
        const val GROUPS_TABLE_NAME = "groups_table"

        // For Singleton instantiation
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase = instance ?: synchronized(this) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        // Create and pre-populate the database. See this article for more details:
        // https://medium.com/google-developers/7-pro-tips-for-room-fbadea4bfbd1#4785
        private fun buildDatabase(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, APP_DATABASE_NAME)
                .addMigrations(
                    *arrayOf(
                        MIGRATION_1_2,
                        MIGRATION_2_3,
                        MIGRATION_3_4,
                    ),
                )
                .build()

        // /////////////////////////////////////////////////////////////////////////
        // Migrations
        // /////////////////////////////////////////////////////////////////////////
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE $GROUPS_TABLE_NAME ADD COLUMN lastUpdated INTEGER NULL")
            }
        }
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // 1. Create the new table without 'groupIds'
                database.execSQL(
                    """
            CREATE TABLE users_table_new (
                userId TEXT PRIMARY KEY NOT NULL,
                email TEXT NOT NULL,
                language TEXT NOT NULL,
                watched TEXT NOT NULL DEFAULT '[]',
                toWatch TEXT NOT NULL DEFAULT '[]'
            )
                    """.trimIndent(),
                )

                // 2. Copy old data table in the new one excluding 'groupIds'
                database.execSQL(
                    """
            INSERT INTO users_table_new (userId, email, language, watched, toWatch)
            SELECT userId, email, language, '[]', '[]' FROM users_table
                    """.trimIndent(),
                )

                // 3. Delete the old table
                database.execSQL("DROP TABLE users_table")

                // 4. Rename the new table with the original one
                database.execSQL("ALTER TABLE users_table_new RENAME TO users_table")
            }
        }
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // 1. Create a temporary table with the new schema (without watchedList and toWatchList)
                database.execSQL(
                    """
            CREATE TABLE groups_table_new (
                groupId TEXT NOT NULL PRIMARY KEY,
                name TEXT NOT NULL,
                ownerId TEXT NOT NULL,
                users TEXT NOT NULL,
                lastUpdated INTEGER
            )
            """.trimIndent(),
                )

                // 2. Copy data from the old table to the new table, excluding watchedList and toWatchList
                database.execSQL(
                    """
            INSERT INTO groups_table_new (groupId, name, ownerId, users, lastUpdated)
            SELECT groupId, name, ownerId, users, lastUpdated FROM $GROUPS_TABLE_NAME
            """.trimIndent(),
                )

                // 3. Drop the old table
                database.execSQL("DROP TABLE $GROUPS_TABLE_NAME")

                // 4. Rename the new table to the original table name
                database.execSQL("ALTER TABLE groups_table_new RENAME TO $GROUPS_TABLE_NAME")
            }
        }
    }
}
