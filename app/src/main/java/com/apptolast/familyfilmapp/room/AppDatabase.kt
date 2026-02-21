package com.apptolast.familyfilmapp.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.apptolast.familyfilmapp.model.room.GroupMovieStatusTable
import com.apptolast.familyfilmapp.model.room.GroupTable
import com.apptolast.familyfilmapp.model.room.UserTable
import com.apptolast.familyfilmapp.room.converters.DateConverter
import com.apptolast.familyfilmapp.room.converters.MovieStatusConverter
import com.apptolast.familyfilmapp.room.converters.StringListConverter
import com.apptolast.familyfilmapp.room.group.GroupDao
import com.apptolast.familyfilmapp.room.groupmoviestatus.GroupMovieStatusDao
import com.apptolast.familyfilmapp.room.user.UserDao

/**
 * Database class with a singleton Instance object.
 */
@Database(
    entities = [
        UserTable::class,
        GroupTable::class,
        GroupMovieStatusTable::class,
    ],
    version = 7,
    exportSchema = true,
)
@TypeConverters(
    value = [
        StringListConverter::class,
        DateConverter::class,
        MovieStatusConverter::class,
    ],
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun groupDao(): GroupDao
    abstract fun groupMovieStatusDao(): GroupMovieStatusDao

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
        const val GROUP_MOVIE_STATUS_TABLE_NAME = "group_movie_status_table"

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS $GROUP_MOVIE_STATUS_TABLE_NAME (
                        groupId TEXT NOT NULL,
                        userId TEXT NOT NULL,
                        movieId INTEGER NOT NULL,
                        status TEXT NOT NULL,
                        PRIMARY KEY(groupId, userId, movieId),
                        FOREIGN KEY(groupId) REFERENCES $GROUPS_TABLE_NAME(groupId) ON DELETE CASCADE
                    )
                    """.trimIndent(),
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_group_movie_status_table_groupId ON $GROUP_MOVIE_STATUS_TABLE_NAME (groupId)",
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_group_movie_status_table_userId ON $GROUP_MOVIE_STATUS_TABLE_NAME (userId)",
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_group_movie_status_table_groupId_userId ON $GROUP_MOVIE_STATUS_TABLE_NAME (groupId, userId)",
                )
            }
        }

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Rebuild users_table without statusMovies column
                // SQLite on API 26 doesn't support ALTER TABLE DROP COLUMN
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS ${USERS_TABLE_NAME}_new (
                        userId TEXT NOT NULL PRIMARY KEY,
                        email TEXT NOT NULL,
                        language TEXT NOT NULL,
                        photoUrl TEXT NOT NULL
                    )
                    """.trimIndent(),
                )
                db.execSQL(
                    """
                    INSERT INTO ${USERS_TABLE_NAME}_new (userId, email, language, photoUrl)
                    SELECT userId, email, language, photoUrl FROM $USERS_TABLE_NAME
                    """.trimIndent(),
                )
                db.execSQL("DROP TABLE $USERS_TABLE_NAME")
                db.execSQL(
                    "ALTER TABLE ${USERS_TABLE_NAME}_new RENAME TO $USERS_TABLE_NAME",
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_users_table_email ON $USERS_TABLE_NAME (email)",
                )
            }
        }

        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE $USERS_TABLE_NAME ADD COLUMN username TEXT NOT NULL DEFAULT ''",
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_users_table_username ON $USERS_TABLE_NAME (username)",
                )
            }
        }

        // For Singleton instantiation
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase = instance ?: synchronized(this) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, APP_DATABASE_NAME)
                .addMigrations(MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7)
                .fallbackToDestructiveMigration(false)
                .build()
    }
}
