package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.api.Constants
import com.udacity.asteroidradar.api.Network
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidsDatabase
import com.udacity.asteroidradar.database.asDomainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AsteroidRepository(private val database: AsteroidsDatabase) {

    val asteroids: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAsteroids()) {
            it.asDomainModel()
        }

    /**
     * Refresh the videos stored in the offline cache.
     *
     * This function uses the IO dispatcher to ensure the database insert database operation
     * happens on the IO dispatcher. By switching to the IO dispatcher using `withContext` this
     * function is now safe to call from any thread including the Main thread.
     *
     * To actually load the asteroids for use, observe [asteroids]
     */
    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            val playlist = parseAsteroidsJsonResult(
                JSONObject(
                    Network.asteroidsService
                        .getAsteroids("2022-01-01", "2023-08-08", Constants.YOUR_API_KEY)
                )
            )

//            val playlist = com.udacity.asteroidradar.api.Network.asteroids
//                .getAsteroids("2022-01-01","2023-08-08", Constants.YOUR_API_KEY)
//                .await()
            println("dra" + " playlist is" + playlist)
            database.asteroidDao.insertAll(*playlist.asDatabaseModel())
        }
    }

}
