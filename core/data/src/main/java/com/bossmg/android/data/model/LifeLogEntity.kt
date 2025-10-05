package com.bossmg.android.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lifelogs")
data class LifeLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: String,
    val title: String,
    val description: String,
    val mood: String,
    val img: String? = null
)