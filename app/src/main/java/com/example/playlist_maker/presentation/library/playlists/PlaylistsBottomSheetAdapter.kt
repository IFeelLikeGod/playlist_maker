package com.example.playlist_maker.presentation.library.playlists

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlist_maker.R
import com.example.playlist_maker.domain.models.Playlist
import java.io.File

class PlaylistsBottomSheetAdapter(
    private val onPlaylistClick: (Playlist) -> Unit
) : RecyclerView.Adapter<PlaylistsBottomSheetAdapter.PlaylistViewHolder>() {

    private var playlists: List<Playlist> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun setPlaylists(newPlaylists: List<Playlist>) {
        playlists = newPlaylists
        notifyDataSetChanged()
    }

    class PlaylistViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val cover: ImageView = view.findViewById(R.id.ivPlaylistCover)
        private val name: TextView = view.findViewById(R.id.tvPlaylistName)
        private val count: TextView = view.findViewById(R.id.tvTracksCount)

        fun bind(playlist: Playlist, onPlaylistClick: (Playlist) -> Unit) {
            name.text = playlist.name
            count.text = getTracksCountString(playlist.tracksCount)

            val coverPath = playlist.coverPath
            if (!coverPath.isNullOrEmpty()) {
                Glide.with(itemView.context)
                    .load(File(coverPath))
                    .placeholder(R.drawable.placeholder)
                    .transform(RoundedCorners(2))
                    .into(cover)
            } else {
                cover.setImageResource(R.drawable.placeholder)
            }

            itemView.setOnClickListener { onPlaylistClick(playlist) }
        }

        private fun getTracksCountString(count: Int): String {
            val rem100 = count % 100
            val rem10 = count % 10
            val suffix = when {
                rem100 in 11..19 -> "треков"
                rem10 == 1 -> "трек"
                rem10 in 2..4 -> "трека"
                else -> "треков"
            }
            return "$count $suffix"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_playlist_bottom_sheet, parent, false)
        return PlaylistViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlists[position], onPlaylistClick)
    }

    override fun getItemCount(): Int = playlists.size
}
