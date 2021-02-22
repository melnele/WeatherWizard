package com.melnele.weatherwizard.view.ui.favourites

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.melnele.weatherwizard.R
import com.melnele.weatherwizard.data.local.FavouriteDB
import com.melnele.weatherwizard.data.model.Favourite
import com.melnele.weatherwizard.databinding.FavouriteListItemBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FavouritesAdapter(private val favourites: List<Favourite>) :
    RecyclerView.Adapter<FavouritesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = FavouriteListItemBinding.inflate(inflater, parent, false)

        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvTitle.text = favourites[position].title
        holder.tvCity.text =
            if (favourites[position].city == null)
                holder.itemView.context.getString(R.string.unknown_place)
            else favourites[position].city
        holder.btnDelete.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                val db = FavouriteDB.getDatabase(holder.itemView.context)
                db.favouriteDao().delete(favourites[position])
//                notifyDataSetChanged()
            }
        }

        holder.itemView.setOnClickListener {
            val fav = favourites[position]
            val latLng = LatLng(fav.latitude, fav.longitude)
            val nav = it.findNavController()
            val args =
                Bundle().apply { putParcelable(it.context.getString(R.string.KEY_LAT_LNG), latLng) }
            nav.navigate(R.id.action_navigation_favourites_to_navigation_favourite_item, args)
        }
    }

    override fun getItemCount(): Int = favourites.size

    class ViewHolder(binding: FavouriteListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvTitle = binding.tvTitle
        val tvCity = binding.tvCity
        val btnDelete = binding.btnDelete
    }
}
