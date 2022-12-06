package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
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
    ): View? {
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

        // Observing when it downloads the image. It can return either the placeholder, the url or noImage
        viewModel.fetchedImageUrlOrNoImage.observe(viewLifecycleOwner, Observer {
            println("dra in observer it are urmatoarea valoare: " + it)
            if (it != "noImage") {
                Picasso.get().load(it)
                    .into(binding.activityMainImageOfTheDay.activity_main_image_of_the_day)
            } else {
                binding.activityMainImageOfTheDay.activity_main_image_of_the_day.setImageResource(R.drawable.placeholder_picture_of_day)
            }
        })

        // Observing when it downloads the title
        viewModel.contentDescriptionImg.observe(viewLifecycleOwner, Observer {
            binding.activityMainImageOfTheDay.setContentDescription(it)
        })

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {


        return true
    }
}
