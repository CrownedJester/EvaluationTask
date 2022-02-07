package com.crownedjester.soft.evaluationtask.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "image_table")
data class Image(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val uriString: String
)
