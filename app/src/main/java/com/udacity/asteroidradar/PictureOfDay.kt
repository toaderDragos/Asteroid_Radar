package com.udacity.asteroidradar

import com.squareup.moshi.Json

data class PictureOfDay(
    @Json(name = "media_type") val mediaType: String, var title: String,
    val url: String
)