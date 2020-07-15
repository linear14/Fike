package com.dongldh.fike.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="stars")
data class Star(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name="id") val id: Long = 0,
    val stationName: String
)