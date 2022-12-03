package com.udacity.asteroidradar.main

import com.udacity.asteroidradar.Asteroid

/**
 * Click listener for Videos. By giving the block a name it helps a reader understand what it does.
 *
 */
class AsteroidClick(val block: (Asteroid) -> Unit) {
    /**
     * Called when an asteroid is clicked
     */
    fun onClick(asteroid: Asteroid) = block(asteroid)
}