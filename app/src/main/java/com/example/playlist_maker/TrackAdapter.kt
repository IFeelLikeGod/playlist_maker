package com.example.playlist_maker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackAdapter(private val tracks: List<Track>) :
    RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {
    class TrackViewHolder(view:View):RecyclerView.ViewHolder(view){
        val trackImage: ImageView = view.findViewById(R.id.trackImage)
        val trackName: TextView = view.findViewById(R.id.trackName)
        val trackArtistName: TextView = view. findViewById(R.id.trackArtistTime)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_track, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = tracks[position]
        holder.trackName.text = track.trackName
        holder.trackArtistName.text = "${track.artistName} * ${track.trackTime}"

        Glide.with(holder.itemView.context)
            .load(track.artworkUrl100)
            .transform(RoundedCorners(8))
            .into(holder.trackImage)
    }

    override fun getItemCount(): Int = tracks.size

}