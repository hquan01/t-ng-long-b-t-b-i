package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.dao.AppDao
import com.example.data.entity.BotConfigEntity
import com.example.data.entity.BotLogEntity
import com.example.data.entity.DailyStatsEntity
import com.example.data.entity.FarmPlotEntity
import com.example.data.entity.FarmingConfigEntity
import com.example.data.entity.GuildQuestConfigEntity
import com.example.data.entity.MiningConfigEntity
import com.example.data.entity.PunishEvilConfigEntity

@Database(
    entities = [
        BotConfigEntity::class,
        GuildQuestConfigEntity::class,
        FarmingConfigEntity::class,
        FarmPlotEntity::class,
        MiningConfigEntity::class,
        PunishEvilConfigEntity::class,
        BotLogEntity::class,
        DailyStatsEntity::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tanglong_auto_database.db"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
