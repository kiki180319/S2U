package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserEntity::class,
        EventEntity::class,
        ForumEntity::class,
        CommentEntity::class,
        VideoEntity::class,
        EventCommentEntity::class,
        EventAttendeeEntity::class,
        CommunityUpdateEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun eventDao(): EventDao
    abstract fun forumDao(): ForumDao
    abstract fun commentDao(): CommentDao
    abstract fun videoDao(): VideoDao
    abstract fun eventCommentDao(): EventCommentDao
    abstract fun eventAttendeeDao(): EventAttendeeDao
    abstract fun communityUpdateDao(): CommunityUpdateDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "hearts2hearts_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
