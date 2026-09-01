package com.example.playlist_maker

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import android.widget.ImageButton
import java.util.Locale
import java.text.SimpleDateFormat

class AudioPlayerActivity : AppCompatActivity() {

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }

    private var playerState = STATE_DEFAULT
    private lateinit var playButton: ImageButton
    private lateinit var trackTimeView: TextView
    private var handler = Handler(Looper.getMainLooper())
    private var mediaPlayer = MediaPlayer()

    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            if (playerState == STATE_PLAYING) {
                trackTimeView.text = SimpleDateFormat("mm:ss", Locale.getDefault())
                    .format(mediaPlayer.currentPosition)
                handler.postDelayed(this, 500)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val json = intent.getStringExtra("track")
        val track = Gson().fromJson(json, Track::class.java)
        setContentView(R.layout.activity_audioplayer)

        val ivBack = findViewById<ImageButton>(R.id.ivBack)
        ivBack.setOnClickListener {
            finish()
        }

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
        playButton.setOnClickListener {
            playbackControl()
        }

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

        track.previewUrl?.let { url ->
            preparePlayer(url)
        }
    }

    private fun preparePlayer(url: String) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playButton.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playButton.setImageResource(R.drawable.play_button)
            playerState = STATE_PREPARED
            trackTimeView.text = "00:00"
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> pausePlayer()
            STATE_PREPARED, STATE_PAUSED -> startPlayer()
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playButton.setImageResource(R.drawable.pause_button)
        playerState = STATE_PLAYING
        handler.post(updateTimeRunnable)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playButton.setImageResource(R.drawable.play_button)
        playerState = STATE_PAUSED
    }
}