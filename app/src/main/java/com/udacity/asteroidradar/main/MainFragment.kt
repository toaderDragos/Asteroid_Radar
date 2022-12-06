package com.udacity.asteroidradar.main

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import kotlinx.android.synthetic.main.fragment_main.view.*

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this, MainViewModel.Factory(requireActivity().application))
            .get(MainViewModel::class.java)
    }

    private var myAdapter: AsteroidAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentMainBinding.inflate(inflater)

        binding.setLifecycleOwner(viewLifecycleOwner)
        binding.viewModel = viewModel

        myAdapter = AsteroidAdapter(AsteroidClick {
            this.findNavController().navigate(MainFragmentDirections.actionShowDetail(it))
        })

        // First we connect to the list of fetched asteroids and then we bind it to the adapter
        viewModel.fetchedAsteroidList.observe(viewLifecycleOwner) { asteroids ->
            asteroids?.apply {
                myAdapter!!.asteroids = viewModel.fetchedAsteroidList.value!!
            }
        }
        binding.asteroidRecycler.adapter = myAdapter

        /** 1. Download if internet is present
         * 2.Observe when it downloads the image. It can return either the url or noImage*/
        if (isInternetAvailable(requireContext())) {
            viewModel.fromJSONtoPicture()
        }

        viewModel.fetchedImageUrlOrNoImage.observe(viewLifecycleOwner) {
            println("dra in observer it has the following value: " + it)
            if (it != "noImage") {
                Picasso.get().load(it)
                    .into(binding.activityMainImageOfTheDay.activity_main_image_of_the_day)
            } else {
                binding.activityMainImageOfTheDay.activity_main_image_of_the_day.setImageResource(R.drawable.placeholder_picture_of_day)
            }
        }

        /** Observing when it downloads the title */
        viewModel.contentDescriptionImg.observe(viewLifecycleOwner) {
            binding.activityMainImageOfTheDay.contentDescription = it
        }

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * Making list operations in place, in the viewModel, on already downloaded data. Then binding the new list to the adapter
     * Should work without internet because it doesn't make new API calls. ( the query information on Nasa site was lacklustre)
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.show_sorted_asteroids) {
            viewModel.sortAsteroidsByDate()
            myAdapter!!.asteroids = viewModel.sortedAsteroids.value!!

        } else if (item.itemId == R.id.show_today_asteroids) {
            viewModel.showTodayAsteroids()
            myAdapter!!.asteroids = viewModel.todaysAsteroids

        } else {
            myAdapter!!.asteroids = viewModel.fetchedAsteroidList.value!!
        }
        return true
    }

    /**
     * Internet Connectivity check - solution from StackOverflow Q 49819923 Answer by AliSh
     */
    private fun isInternetAvailable(context: Context): Boolean {
        val result: Boolean
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        result = when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
        return result
    }

}
