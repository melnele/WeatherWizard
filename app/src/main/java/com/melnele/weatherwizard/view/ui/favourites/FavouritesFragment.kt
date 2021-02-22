package com.melnele.weatherwizard.view.ui.favourites

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.melnele.weatherwizard.R
import com.melnele.weatherwizard.data.local.FavouriteDB
import com.melnele.weatherwizard.data.repository.FavouriteRepo
import com.melnele.weatherwizard.databinding.FragmentFavouritesBinding
import com.melnele.weatherwizard.view.settings.MapActivity

class FavouritesFragment : Fragment() {
    private val favouritesViewModel by viewModels<FavouritesViewModel> {
        val db = FavouriteDB.getDatabase(requireContext())
        FavouritesViewModelFactory(FavouriteRepo(db.favouriteDao()))
    }
    private var _binding: FragmentFavouritesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavouritesBinding.inflate(inflater, container, false)

        favouritesViewModel.favourites.observe(viewLifecycleOwner, {
            binding.recyclerView.adapter = FavouritesAdapter(it)
            binding.recyclerView.layoutManager = LinearLayoutManager(context)
            binding.recyclerView.scrollToPosition(0)
        })
        binding.fab.setOnClickListener {
            val intent = Intent(context, MapActivity::class.java)
            intent.putExtra(getString(R.string.KEY_FAVOURITE), true)
            startActivity(intent)
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}