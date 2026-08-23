package com.example.playlist_maker
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import android.widget.ImageButton


class AudioPlayerActivity : AppCompatActivity() {

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
        val tvDurationValue = findViewById<TextView>(R.id.tvDurationValue)
        val tvAlbumValue = findViewById<TextView>(R.id.tvAlbumValue)
        val tvYearValue = findViewById<TextView>(R.id.tvYearValue)
        val tvGenreValue = findViewById<TextView>(R.id.tvGenreValue)
        val tvCountryValue = findViewById<TextView>(R.id.tvCountryValue)
        val tvAlbumLabel = findViewById<TextView>(R.id.tvAlbumLabel)
        val tvYearLabel = findViewById<TextView>(R.id.tvYearLabel)



        trackName.text = track.trackName
        trackArtistTime.text = track.artistName
        tvDurationValue.text = track.trackTime
        tvCountryValue.text = track.country
        tvYearValue.text = track.releaseDate
        tvGenreValue.text = track.primaryGenreName

        if (track.collectionName == null) {
            tvAlbumLabel.visibility = View.GONE
            tvAlbumValue.visibility = View.GONE
        } else {
            tvAlbumValue.text = track.collectionName
        }

        if (track.releaseDate == null){
            tvYearLabel.visibility = View.GONE
            tvYearValue.visibility = View.GONE
        }
        else{
            tvYearValue.text = track.releaseDate.substringBefore("-")
        }



        Glide.with(trackImageView.context)
            .load(track.getCoverArtwork())
            .placeholder((R.drawable.placeholder))
            .transform(RoundedCorners(8))
            .into(trackImageView)
    }
}