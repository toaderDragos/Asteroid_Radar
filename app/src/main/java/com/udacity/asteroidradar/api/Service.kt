package com.udacity.asteroidradar.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.udacity.asteroidradar.api.Constants.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/** * A retrofit service to fetch a list of asteroids. OBVIOUSLY a JSON is a string */
interface AsteroidService {
    @GET("/neo/rest/v1/feed")
    fun getAsteroids(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("api_key") API_Key: String
    ): String
}


/** * Main entry point for network access. */
object Network {
    // Configure retrofit to parse JSON and use coroutines
    private val retrofit = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(ScalarsConverterFactory.create())  // info from stackOverflow
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    val asteroidsService = retrofit.create(AsteroidService::class.java)

    private val getRetrofitImage = Retrofit.Builder()
        .addConverterFactory(ScalarsConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()


}

/**
 * RESOLVE THIS ACCES
 */
val appendUrl = appendUrl()
fun appendUrl(): String {
    val result = "/neo/rest/v1/feed?start_date="
    return result
}