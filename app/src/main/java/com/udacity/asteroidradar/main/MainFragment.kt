package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this, MainViewModel.Factory(requireActivity().application)).get(
            MainViewModel::class.java
        )
    }

    private var myAdapter: AsteroidAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMainBinding.inflate(inflater)

        binding.setLifecycleOwner(viewLifecycleOwner)
        binding.viewModel = viewModel

        // not sure bc if we bind the recyclerview first without the list it may crash
        binding.asteroidRecycler.adapter = AsteroidAdapter(AsteroidClick {
            viewModel.displayAsteroidDetails(it)
        })

        /*** Navigate to details fragment */
        viewModel.navigateToSelectedAsteroid.observe(viewLifecycleOwner, Observer {
            if (null != it) {
                this.findNavController().navigate(MainFragmentDirections.actionShowDetail(it))
                viewModel.displayAsteroidDetailsComplete()
            }
        })

        setHasOptionsMenu(true)
        return binding.root
    }

    /** Observing the fetched list and sending it to the adapter.  */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        viewModel.fetchedAsteroidList.observe(viewLifecycleOwner, Observer<List<Asteroid>> { asteroids ->
//            asteroids?.apply {
//                myAdapter!!.asteroids = asteroids
//            }
//        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return true
    }
}
