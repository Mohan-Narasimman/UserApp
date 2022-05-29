package com.interview.pagination.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.interview.pagination.data.entity.RemoteKeys
import com.interview.pagination.model.Results
import com.interview.pagination.data.dao.UsersDao
import com.interview.pagination.data.dao.RemoteKeysDao
import com.interview.pagination.utils.Converters


@Database(
    entities = [Results::class, RemoteKeys::class],
    version = 1, exportSchema = false
)

@TypeConverters(Converters::class)
abstract class AppDataBase : RoomDatabase() {

    abstract val usersDao: UsersDao
    abstract val remoteKeysDao: RemoteKeysDao

    companion object {
        @Volatile
        private var instance: AppDataBase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance
            ?: synchronized(LOCK) {
                instance ?: buildDatabase(
                    context
                ).also {
                    instance = it
                }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                AppDataBase::class.java,
                "app_db"
            ).fallbackToDestructiveMigration()
                .build()
    }
}