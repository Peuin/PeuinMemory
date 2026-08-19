package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [TripEntity::class, PlaceEntity::class, MemoryEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class PeuinDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao
    abstract fun placeDao(): PlaceDao
    abstract fun memoryDao(): MemoryDao

    companion object {
        @Volatile
        private var INSTANCE: PeuinDatabase? = null

        fun getDatabase(context: Context): PeuinDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PeuinDatabase::class.java,
                    "peuin_travel_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
