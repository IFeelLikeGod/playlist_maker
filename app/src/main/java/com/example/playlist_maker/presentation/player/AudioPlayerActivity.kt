package com.example.playlist_maker.presentation.player

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlist_maker.R
import com.example.playlist_maker.domain.models.getCoverArtwork
import com.example.playlist_maker.domain.models.Track
import com.google.gson.Gson

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var viewModel: PlayerViewModel
    private lateinit var playButton: ImageButton
    private lateinit var trackTimeView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audioplayer)

        val json = intent.getStringExtra("track")
        val track = Gson().fromJson(json, Track::class.java)

        val ivBack = findViewById<ImageButton>(R.id.ivBack)
        val trackImageView = findViewById<ImageView>(R.id.trackImage)
        val trackName = findViewById<TextView>(R.id.trackName)
        val trackArtistTime = findViewById<TextView>(R.id.trackArtistTime)
        trackTimeView = findViewById(R.id.trackTime)
        val tvDurationValue = findViewById<TextView>(R.id.tvDurationValue)
        val tvAlbumValue = findViewById<TextView>(R.id.tvAlbumValue)
        val tvYearValue = findViewById<TextView>(R.id.tvYearValue)
        val tvGenreValue = findViewById<TextView>(R.id.tvGenreValue)
        val tvCountryValue = findViewById<TextView>(R.id.tvCountryValue)
        val tvAlbumLabel = findViewById<TextView>(R.id.tvAlbumLabel)
        val tvYearLabel = findViewById<TextView>(R.id.tvYearLabel)
        playButton = findViewById(R.id.playButton)

        trackName.text = track.trackName
        trackArtistTime.text = track.artistName
        tvDurationValue.text = track.trackTime
        tvCountryValue.text = track.country
        tvGenreValue.text = track.primaryGenreName

        if (track.collectionName == null) {
            tvAlbumLabel.visibility = View.GONE
            tvAlbumValue.visibility = View.GONE
        } else {
            tvAlbumValue.text = track.collectionName
        }

        if (track.releaseDate == null) {
            tvYearLabel.visibility = View.GONE
            tvYearValue.visibility = View.GONE
        } else {
            tvYearValue.text = track.releaseDate.substringBefore("-")
        }

        Glide.with(trackImageView.context)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.placeholder)
            .transform(RoundedCorners(8))
            .into(trackImageView)


        viewModel = ViewModelProvider(
            this,
            PlayerViewModel.getFactory(track.previewUrl)
        )[PlayerViewModel::class.java]

        viewModel.observeState().observe(this) { render(it) }

        ivBack.setOnClickListener { finish() }
        playButton.setOnClickListener { viewModel.onPlayButtonClicked() }
    }

    private fun render(state: PlayerState) {
        when (state) {
            is PlayerState.Default -> {
                playButton.isEnabled = false
                playButton.setImageResource(R.drawable.play_button)
            }
            is PlayerState.Prepared -> {
                playButton.isEnabled = true
                playButton.setImageResource(R.drawable.play_button)
                trackTimeView.text = state.progress
            }
            is PlayerState.Playing -> {
                playButton.isEnabled = true
                playButton.setImageResource(R.drawable.pause_button)
                trackTimeView.text = state.progress
            }
            is PlayerState.Paused -> {
                playButton.isEnabled = true
                playButton.setImageResource(R.drawable.play_button)
                trackTimeView.text = state.progress
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }
}