package com.apptolast.familyfilmapp.room

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import com.apptolast.familyfilmapp.model.room.ChatMessageTable
import com.apptolast.familyfilmapp.model.room.GroupMovieStatusTable
import com.apptolast.familyfilmapp.model.room.GroupTable
import com.apptolast.familyfilmapp.model.room.SkippedMediaTable
import com.apptolast.familyfilmapp.model.room.UserTable
import com.apptolast.familyfilmapp.room.chat.ChatMessageDao
import com.apptolast.familyfilmapp.room.converters.DateConverter
import com.apptolast.familyfilmapp.room.converters.MediaStatusConverter
import com.apptolast.familyfilmapp.room.converters.MediaTypeConverter
import com.apptolast.familyfilmapp.room.converters.StringListConverter
import com.apptolast.familyfilmapp.room.group.GroupDao
import com.apptolast.familyfilmapp.room.groupmoviestatus.GroupMovieStatusDao
import com.apptolast.familyfilmapp.room.skippedmedia.SkippedMediaDao
import com.apptolast.familyfilmapp.room.user.UserDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@Database(
    entities = [
        UserTable::class,
        GroupTable::class,
        GroupMovieStatusTable::class,
        ChatMessageTable::class,
        SkippedMediaTable::class,
    ],
    version = 11,
    exportSchema = true,
)
@TypeConverters(
    value = [
        StringListConverter::class,
        DateConverter::class,
        MediaStatusConverter::class,
        MediaTypeConverter::class,
    ],
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun groupDao(): GroupDao
    abstract fun groupMovieStatusDao(): GroupMovieStatusDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun skippedMediaDao(): SkippedMediaDao

    companion object {

        const val APP_DATABASE_NAME = "ffa_database"

        const val USERS_TABLE_NAME = "users_table"
        const val GROUPS_TABLE_NAME = "groups_table"
        const val GROUP_MOVIE_STATUS_TABLE_NAME = "group_movie_status_table"
        const val CHAT_MESSAGES_TABLE_NAME = "chat_messages_table"
        const val SKIPPED_MEDIA_TABLE_NAME = "skipped_media_table"

        // v1 → v2 schemas are identical — intentional no-op.
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(connection: SQLiteConnection) {
                // intentionally empty
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(connection: SQLiteConnection) {
                connection.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_users_table_email ON $USERS_TABLE_NAME (email)",
                )
                connection.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_groups_table_ownerId ON $GROUPS_TABLE_NAME (ownerId)",
                )
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(connection: SQLiteConnection) {
                connection.execSQL(
                    "ALTER TABLE $USERS_TABLE_NAME ADD COLUMN photoUrl TEXT NOT NULL DEFAULT ''",
                )
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(connection: SQLiteConnection) {
                connection.execSQL(
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
                connection.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_group_movie_status_table_groupId ON $GROUP_MOVIE_STATUS_TABLE_NAME (groupId)",
                )
                connection.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_group_movie_status_table_userId ON $GROUP_MOVIE_STATUS_TABLE_NAME (userId)",
                )
                connection.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_group_movie_status_table_groupId_userId ON $GROUP_MOVIE_STATUS_TABLE_NAME (groupId, userId)",
                )
            }
        }

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(connection: SQLiteConnection) {
                connection.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS ${USERS_TABLE_NAME}_new (
                        userId TEXT NOT NULL PRIMARY KEY,
                        email TEXT NOT NULL,
                        language TEXT NOT NULL,
                        photoUrl TEXT NOT NULL
                    )
                    """.trimIndent(),
                )
                connection.execSQL(
                    """
                    INSERT INTO ${USERS_TABLE_NAME}_new (userId, email, language, photoUrl)
                    SELECT userId, email, language, photoUrl FROM $USERS_TABLE_NAME
                    """.trimIndent(),
                )
                connection.execSQL("DROP TABLE $USERS_TABLE_NAME")
                connection.execSQL("ALTER TABLE ${USERS_TABLE_NAME}_new RENAME TO $USERS_TABLE_NAME")
                connection.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_users_table_email ON $USERS_TABLE_NAME (email)",
                )
            }
        }

        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(connection: SQLiteConnection) {
                connection.execSQL(
                    "ALTER TABLE $USERS_TABLE_NAME ADD COLUMN username TEXT NOT NULL DEFAULT ''",
                )
                connection.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_users_table_username ON $USERS_TABLE_NAME (username)",
                )
            }
        }

        val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(connection: SQLiteConnection) {
                connection.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS ${GROUP_MOVIE_STATUS_TABLE_NAME}_new (
                        groupId TEXT NOT NULL,
                        userId TEXT NOT NULL,
                        movieId INTEGER NOT NULL,
                        status TEXT NOT NULL,
                        mediaType TEXT NOT NULL DEFAULT 'MOVIE',
                        PRIMARY KEY(groupId, userId, movieId, mediaType),
                        FOREIGN KEY(groupId) REFERENCES $GROUPS_TABLE_NAME(groupId) ON DELETE CASCADE
                    )
                    """.trimIndent(),
                )
                connection.execSQL(
                    """
                    INSERT INTO ${GROUP_MOVIE_STATUS_TABLE_NAME}_new (groupId, userId, movieId, status, mediaType)
                    SELECT groupId, userId, movieId, status, 'MOVIE' FROM $GROUP_MOVIE_STATUS_TABLE_NAME
                    """.trimIndent(),
                )
                connection.execSQL("DROP TABLE $GROUP_MOVIE_STATUS_TABLE_NAME")
                connection.execSQL(
                    "ALTER TABLE ${GROUP_MOVIE_STATUS_TABLE_NAME}_new RENAME TO $GROUP_MOVIE_STATUS_TABLE_NAME",
                )
                connection.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_group_movie_status_table_groupId ON $GROUP_MOVIE_STATUS_TABLE_NAME (groupId)",
                )
                connection.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_group_movie_status_table_userId ON $GROUP_MOVIE_STATUS_TABLE_NAME (userId)",
                )
                connection.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_group_movie_status_table_groupId_userId ON $GROUP_MOVIE_STATUS_TABLE_NAME (groupId, userId)",
                )
            }
        }

        val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(connection: SQLiteConnection) {
                connection.execSQL(
                    "ALTER TABLE $USERS_TABLE_NAME ADD COLUMN hasRemovedAds INTEGER NOT NULL DEFAULT 0",
                )
            }
        }

        val MIGRATION_9_10 = object : Migration(9, 10) {
            override fun migrate(connection: SQLiteConnection) {
                connection.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS $CHAT_MESSAGES_TABLE_NAME (
                        id TEXT NOT NULL PRIMARY KEY,
                        userId TEXT NOT NULL,
                        role TEXT NOT NULL,
                        content TEXT NOT NULL,
                        timestamp INTEGER NOT NULL
                    )
                    """.trimIndent(),
                )
                connection.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_${CHAT_MESSAGES_TABLE_NAME}_userId ON $CHAT_MESSAGES_TABLE_NAME (userId)",
                )
                connection.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_${CHAT_MESSAGES_TABLE_NAME}_userId_timestamp ON $CHAT_MESSAGES_TABLE_NAME (userId, timestamp)",
                )
            }
        }

        val MIGRATION_10_11 = object : Migration(10, 11) {
            override fun migrate(connection: SQLiteConnection) {
                connection.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS $SKIPPED_MEDIA_TABLE_NAME (
                        userId TEXT NOT NULL,
                        mediaId INTEGER NOT NULL,
                        mediaType TEXT NOT NULL,
                        title TEXT NOT NULL,
                        posterPath TEXT NOT NULL,
                        overview TEXT NOT NULL,
                        releaseDate TEXT NOT NULL,
                        voteAverage REAL NOT NULL,
                        popularity REAL NOT NULL,
                        skippedAt INTEGER NOT NULL,
                        PRIMARY KEY(userId, mediaId, mediaType)
                    )
                    """.trimIndent(),
                )
                connection.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_${SKIPPED_MEDIA_TABLE_NAME}_userId ON $SKIPPED_MEDIA_TABLE_NAME (userId)",
                )
                connection.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_${SKIPPED_MEDIA_TABLE_NAME}_userId_skippedAt ON $SKIPPED_MEDIA_TABLE_NAME (userId, skippedAt)",
                )
            }
        }
    }
}

// Room KSP generates the platform `actual object`s on each target.
@Suppress("KotlinNoActualForExpect", "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

fun buildAppDatabase(builder: RoomDatabase.Builder<AppDatabase>): AppDatabase = builder
    .addMigrations(
        AppDatabase.MIGRATION_1_2,
        AppDatabase.MIGRATION_2_3,
        AppDatabase.MIGRATION_3_4,
        AppDatabase.MIGRATION_4_5,
        AppDatabase.MIGRATION_5_6,
        AppDatabase.MIGRATION_6_7,
        AppDatabase.MIGRATION_7_8,
        AppDatabase.MIGRATION_8_9,
        AppDatabase.MIGRATION_9_10,
        AppDatabase.MIGRATION_10_11,
    )
    .setDriver(BundledSQLiteDriver())
    .setQueryCoroutineContext(Dispatchers.IO)
    .build()
