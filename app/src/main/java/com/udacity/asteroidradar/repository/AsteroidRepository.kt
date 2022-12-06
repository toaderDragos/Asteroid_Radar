package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.api.*
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

    val first_date = getNextSevenDaysFormattedDates()[0]
    val last_date = getNextSevenDaysFormattedDates()[7]

    fun deleteYesterdaysAsteroids() {
        database.asteroidDao.deleteYesterday(first_date)
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
        try {
            withContext(Dispatchers.IO) {
                val playlist = parseAsteroidsJsonResult(
                    JSONObject(
                        Network.asteroidsService
                            .getAsteroids(first_date, last_date, Constants.YOUR_API_KEY)
                    )
                )
//            println("dra" + " playlist is" + playlist)
                database.asteroidDao.insertAll(*NetworkAsteroidContainer(playlist).asDatabaseModel())
            }
        } catch (e: Exception) {
            println("dra exception in fetching asteroids ")
        }
    }


}
