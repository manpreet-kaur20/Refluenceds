package com.example.refluenceds.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.refluenceds.data.local.entity.CampaignEntity

@Database(entities = [CampaignEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract val campaignDao: CampaignDao

    companion object {
        const val DATABASE_NAME = "refluenceds_db"
    }
}
