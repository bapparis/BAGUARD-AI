package com.bapparis.baguardai.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "investigation_history")
data class InvestigationEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val originalContent: String,

    val riskLevel: String,

    val summary: String,

    val reportJson: String,

    val createdAt: Long = System.currentTimeMillis()
)