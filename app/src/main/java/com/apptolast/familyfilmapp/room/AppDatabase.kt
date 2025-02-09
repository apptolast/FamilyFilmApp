package com.apptolast.familyfilmapp.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.apptolast.familyfilmapp.model.room.GroupTable
import com.apptolast.familyfilmapp.model.room.UserTable
import com.apptolast.familyfilmapp.room.converters.GroupListConverter
import com.apptolast.familyfilmapp.room.converters.IntListConverter
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
    version = 1, exportSchema = false,
)
@TypeConverters(
    value = [
        UserListConverter::class,
        IntListConverter::class,
        StringListConverter::class,
    ],
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun groupDao(): GroupDao

    companion object {

        private const val APP_DATABASE_NAME = "ffa_database"
        const val USERS_TABLE_NAME = "users_table"
        const val GROUPS_TABLE_NAME = "groups_table"
//        const val GROUP_USER_TABLE_NAME = "group_user_table"


        // For Singleton instantiation
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        // Create and pre-populate the database. See this article for more details:
        // https://medium.com/google-developers/7-pro-tips-for-room-fbadea4bfbd1#4785
        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, APP_DATABASE_NAME).build()
        }
    }
}
