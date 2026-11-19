
package com.example.playlist_maker.presentation.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlist_maker.R
import com.example.playlist_maker.databinding.FragmentAudioPlayerBinding
import com.example.playlist_maker.domain.models.Track
import com.example.playlist_maker.domain.models.getCoverArtwork
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class AudioPlayerFragment : Fragment() {

    private var _binding: FragmentAudioPlayerBinding? = null
    private val binding get() = _binding!!

    private lateinit var track: Track

    private val viewModel: PlayerViewModel by viewModel {
        parametersOf(track.previewUrl, track)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val json = requireArguments().getString("track")
        track = Gson().fromJson(json, Track::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAudioPlayerBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Информация о треке
        binding.trackName.text = track.trackName
        binding.trackArtistTime.text = track.artistName
        binding.tvDurationValue.text = track.trackTime
        binding.tvCountryValue.text = track.country
        binding.tvGenreValue.text = track.primaryGenreName

        if (track.collectionName == null) {
            binding.tvAlbumLabel.visibility = View.GONE
            binding.tvAlbumValue.visibility = View.GONE
        } else {
            binding.tvAlbumValue.text = track.collectionName
        }

        val releaseDate = track.releaseDate

        if (releaseDate == null) {
            binding.tvYearLabel.visibility = View.GONE
            binding.tvYearValue.visibility = View.GONE
        } else {
            binding.tvYearValue.text = releaseDate.substringBefore("-")
        }

        // Обложка трека
        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder)
            .transform(RoundedCorners(8))
            .into(binding.trackImage)

        // Состояние аудиоплеера
        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        // Состояние кнопки избранного
        viewModel.observeFavorite().observe(viewLifecycleOwner) { isFavorite ->

            binding.likeTheSong.setImageResource(
                if (isFavorite) {
                    R.drawable.like_active
                } else {
                    R.drawable.like
                }
            )
        }

        // Нажатие на кнопку лайка
        binding.likeTheSong.setOnClickListener {
            viewModel.onFavoriteClicked()
        }

        // Воспроизведение
        binding.playButton.setOnClickListener {
            viewModel.onPlayButtonClicked()
        }

        // Возврат назад
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun render(state: PlayerState) {
        when (state) {

            is PlayerState.Default -> {
                binding.playButton.isEnabled = false
                binding.playButton.setImageResource(R.drawable.play_button)
            }

            is PlayerState.Prepared -> {
                binding.playButton.isEnabled = true
                binding.playButton.setImageResource(R.drawable.play_button)
                binding.trackTime.text = state.progress
            }

            is PlayerState.Playing -> {
                binding.playButton.isEnabled = true
                binding.playButton.setImageResource(R.drawable.pause_button)
                binding.trackTime.text = state.progress
            }

            is PlayerState.Paused -> {
                binding.playButton.isEnabled = true
                binding.playButton.setImageResource(R.drawable.play_button)
                binding.trackTime.text = state.progress
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
