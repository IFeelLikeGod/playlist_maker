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

    class TrackViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val trackImage: ImageView = view.findViewById(R.id.trackImage)
        private val trackTime: TextView = view.findViewById(R.id.trackTime)
        private val trackName: TextView = view.findViewById(R.id.trackName)
        private val trackArtistName: TextView = view.findViewById(R.id.trackArtistTime)

        fun bind(track: Track) {
            trackName.text = track.trackName
            trackArtistName.text = "${track.artistName} • "
            trackTime.text = track.trackTime

            val cornerRadiusPx = itemView.resources.getDimensionPixelSize(R.dimen.track_image_corner_radius)

            Glide.with(itemView.context)
                .load(track.artworkUrl100)
                .transform(RoundedCorners(8))
                .into(trackImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_track, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    override fun getItemCount(): Int = tracks.size
}