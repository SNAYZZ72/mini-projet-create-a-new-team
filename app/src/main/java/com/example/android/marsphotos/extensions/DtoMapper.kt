package com.example.android.marsphotos.extensions

import com.example.android.marsphotos.data.MarsPhoto
import com.example.android.marsphotos.network.model.MarsPhotoDto

fun MarsPhotoDto.toMarsPhoto() = MarsPhoto(
    id = id,
    url = imgSrcUrl
)
