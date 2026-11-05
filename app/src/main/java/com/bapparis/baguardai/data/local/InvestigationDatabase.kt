package com.bapparis.baguardai.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [InvestigationEntity::class],
    version = 1,
    exportSchema = false
)
abstract class InvestigationDatabase : RoomDatabase() {

    abstract fun investigationDao(): InvestigationDao

    companion object {

        @Volatile
        private var INSTANCE: InvestigationDatabase? = null

        fun getDatabase(context: Context): InvestigationDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    InvestigationDatabase::class.java,
                    "baguard_investigation_database"
                ).build().also {
                    INSTANCE = it
                }
            }
        }
    }
}