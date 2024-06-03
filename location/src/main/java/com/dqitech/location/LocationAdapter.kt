package com.dqitech.location

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LocationAdapter(private val locations: MutableList<LocationData> = mutableListOf()) : RecyclerView.Adapter<LocationAdapter.LocationViewHolder>() {

    private var itemClickListener: ((LocationData) -> Unit)? = null

    inner class LocationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.title)
        private val snippetTextView: TextView = itemView.findViewById(R.id.snippet)

        init {
            itemView.setOnClickListener {
                itemClickListener?.invoke(locations[adapterPosition])
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(locationData: LocationData) {
            titleTextView.text = "Location ${adapterPosition + 1}"
            snippetTextView.text = "Lat: ${locationData.latitude}, Lng: ${locationData.longitude}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_location, parent, false)
        return LocationViewHolder(view)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val locationData = locations[position]
        holder.bind(locationData)
    }

    override fun getItemCount(): Int = locations.size

    fun addLocation(locationData: LocationData) {
        locations.add(locationData)
        notifyItemInserted(locations.size - 1)
    }

    fun setOnItemClickListener(listener: (LocationData) -> Unit) {
        itemClickListener = listener
    }
}
