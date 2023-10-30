package com.example.android.marsphotos.data

data class MarsPhoto(
    val id: String,
    val url: String,
    val tags: List<String>?,
    var liked: Boolean?,
    val shared: Boolean?,
    val deleted: Boolean?
)