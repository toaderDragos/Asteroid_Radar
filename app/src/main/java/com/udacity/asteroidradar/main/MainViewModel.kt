package com.udacity.asteroidradar.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.CreationExtras
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.api.Constants
import com.udacity.asteroidradar.api.Network
import com.udacity.asteroidradar.api.getNextSevenDaysFormattedDates
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.launch
import org.json.JSONObject


class MainViewModel(application: Application) : AndroidViewModel(application) {

    val todaysAsteroids = mutableListOf<Asteroid>()

    private val database = getDatabase(application)
    private val asteroidsRepository = AsteroidRepository(database)

    private val _fetchedImageUrlOrNoImage = MutableLiveData<String>()
    val fetchedImageUrlOrNoImage: LiveData<String>
        get() = _fetchedImageUrlOrNoImage

    private val _contentDescriptionImg = MutableLiveData<String>()
    val contentDescriptionImg: LiveData<String>
        get() = _contentDescriptionImg

    private val _sortedAsteroids = MutableLiveData<List<Asteroid>>()
    val sortedAsteroids: LiveData<List<Asteroid>>
        get() = _sortedAsteroids

    /**
     * init{} is called immediately when this ViewModel is created.
     */
    init {
        viewModelScope.launch {
            asteroidsRepository.refreshAsteroids()
        }
//        fromJSONtoPicture()
    }

    val fetchedAsteroidList = asteroidsRepository.asteroids

    /**
     * Implementing the parsing of data and saving into a LiveData variable IN THE SAME COROUTINE,
     * Otherwise you can;t guarantee that the results from the API get saved
     */

    fun fromJSONtoPicture() {
        var fetchedMediaType = ""
        var fetchedTitle = "Image Title"
        var fetchedUrl = ""
        var fetchedImageDictionary = ""

        viewModelScope.launch {
            try {
                fetchedImageDictionary = Network.imageService.getImage(Constants.YOUR_API_KEY)
                val newJSONObject = JSONObject(fetchedImageDictionary)
                fetchedMediaType = newJSONObject["media_type"].toString()
                fetchedTitle = newJSONObject["title"].toString()
                fetchedUrl = newJSONObject["url"].toString()
                // println("dra - the fetched image json is " + fetchedMediaType + " " + fetchedTitle + " " + fetchedUrl)
            } catch (e: Exception) {
                Log.i("dra imageService", "Api or download error: $e")
                // if it doesn't have internet then it shouldn't be looking after the photo
                _fetchedImageUrlOrNoImage.value = "noImage"
            }
            // media type can only be video or image
            if (fetchedMediaType == "image") {
                _fetchedImageUrlOrNoImage.value = fetchedUrl
            } else {
                _fetchedImageUrlOrNoImage.value = "noImage"
            }

            // saving it in the coroutine so we are sure that it has been saved
            _contentDescriptionImg.value = fetchedTitle
        }
    }

    // filter
    fun sortAsteroidsByDate() {
        _sortedAsteroids.value = fetchedAsteroidList.value?.sortedBy { it.closeApproachDate }
        println("dra sorted and fetched Asteroids are " + _sortedAsteroids.value + " " + fetchedAsteroidList.value)
    }

    fun showTodayAsteroids() {
        val todaysDate = getNextSevenDaysFormattedDates()[0]
        for (asteroid in fetchedAsteroidList.value!!) {
            if (asteroid.closeApproachDate == todaysDate) {
//                println("dra avem datele:" + asteroid.closeApproachDate +"|si|" + todaysDate)
                todaysAsteroids.add(asteroid)
            }
        }
    }


    /**
     * Factory for constructing MAinViewModel with parameter
     */
    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}