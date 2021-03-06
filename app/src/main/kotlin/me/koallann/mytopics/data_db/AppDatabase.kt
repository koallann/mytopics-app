package me.koallann.mytopics.data_db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import me.koallann.mytopics.data_db.topic.TopicDao
import me.koallann.mytopics.data_db.topic.TopicEntity
import me.koallann.mytopics.data_db.topic.TopicTypeConverter
import me.koallann.mytopics.data_db.user.UserDao
import me.koallann.mytopics.data_db.user.UserEntity

@Database(version = 1, entities = [UserEntity::class, TopicEntity::class], exportSchema = false)
@TypeConverters(TopicTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "mytopics.db"
            )
                .build()
        }
    }

    abstract fun getUserDao(): UserDao

    abstract fun getTopicDao(): TopicDao

}
