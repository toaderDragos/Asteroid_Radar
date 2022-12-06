package com.udacity.asteroidradar.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.api.Constants.BASE_URL
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit


/** * A retrofit service to fetch a list of asteroids. OBVIOUSLY a JSON is a string. Suspend inserted because
 * error unable to call adapter by retrofit */
interface AsteroidService {
    @GET("neo/rest/v1/feed?")
    suspend fun getAsteroids(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("api_key") your_key: String
    ): String
}

interface ImageService {
    @GET("planetary/apod?")
    suspend fun getImage(@Query("api_key") your_key: String): String
}

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

/** * Main entry point for network access. */
object Network {

    // SocketTimeoutTException - Increasing Time for request implemented from
    // https://stackoverflow.com/questions/39219094/sockettimeoutexception-in-retrofit

    var client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(100, TimeUnit.SECONDS)
        .readTimeout(100, TimeUnit.SECONDS).build()

    // Configure retrofit to parse JSON and use coroutines
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL).client(client)
        .addConverterFactory(ScalarsConverterFactory.create())  // info from stackOverflow
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    private val retrofitForImage = Retrofit.Builder()
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(BASE_URL)
        .build()

    val asteroidsService = retrofit.create(AsteroidService::class.java)

    val imageService: ImageService by lazy { retrofitForImage.create(ImageService::class.java) }


}

