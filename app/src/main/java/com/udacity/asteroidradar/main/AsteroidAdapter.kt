package com.udacity.asteroidradar.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.ItemAsteroidRecyclerviewBinding

class AsteroidAdapter(val callback: AsteroidClick) : RecyclerView.Adapter<AsteroidViewHolder>() {

    /**
     * A list that is replaced by the downloaded items
     */
    var asteroids: List<Asteroid> = listOf(
        Asteroid(
            23424, "FoxThroat", "val closeApproachDate: String",
            4324343.49, 24.234,
            2344.434, 312321.33,
            false
        ), Asteroid(
            23424, "The eagle", "tommorow neeeewww",
            4324343.49, 24.234,
            2344.434, 312321.33,
            false
        )
    )
        set(value) {
            field = value
            // For an extra challenge, update this to use the paging library.
            // Notify any registered observers that the data set has changed. This will cause every
            // element in our RecyclerView to be invalidated.
            notifyDataSetChanged()
        }

    /**
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AsteroidViewHolder {
        val withDataBinding: ItemAsteroidRecyclerviewBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            AsteroidViewHolder.LAYOUT,
            parent,
            false
        )
        return AsteroidViewHolder(withDataBinding)
    }

    override fun getItemCount() = asteroids.size

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link ViewHolder#itemView} to reflect the item at the given
     * position.
     */
    override fun onBindViewHolder(holder: AsteroidViewHolder, position: Int) {
        holder.viewDataBinding.also {
            it.codename.text = asteroids[position].codename
            it.date.text = asteroids[position].closeApproachDate
            it.asteroidCallback = callback
            // bind it because you have it defined in XML as a variable
            it.asteroid = asteroids[position]
            if (asteroids[position].isPotentiallyHazardous == true) {
                it.imageSmileyFace.setImageResource(R.drawable.ic_status_potentially_hazardous)
            }
        }
    }
}

/**
 * ViewHolder for Asteroid Items . All work is done by data binding.
 */
class AsteroidViewHolder(val viewDataBinding: ItemAsteroidRecyclerviewBinding) :
    RecyclerView.ViewHolder(viewDataBinding.root) {
    companion object {
        @LayoutRes
        val LAYOUT = R.layout.item_asteroid_recyclerview
    }
}

