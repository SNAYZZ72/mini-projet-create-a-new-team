package com.example.android.marsphotos.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "mars_photos")
data class MarsPhoto(
    @PrimaryKey
    val id: String,
    val url: String,
    val tags: List<String>?,
    var liked: Boolean?,
    val shared: Boolean?,
    val deleted: Boolean?
)