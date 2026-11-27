
package com.example.playlist_maker.presentation.library.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlist_maker.R
import com.example.playlist_maker.databinding.FragmentFavoritesBinding
import com.example.playlist_maker.domain.models.Track
import com.example.playlist_maker.presentation.search.TrackAdapter
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavoritesViewModel by viewModel()

    private lateinit var trackAdapter: TrackAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        trackAdapter = TrackAdapter { track ->
            openAudioPlayer(track)
        }

        binding.rvFavorites.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = trackAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.observeState().observe(viewLifecycleOwner) { state ->
            when (state) {
                is FavoritesState.Empty -> showEmptyState()

                is FavoritesState.Content -> showTracks(state.tracks)
            }
        }
    }

    private fun showEmptyState() {
        binding.rvFavorites.visibility = View.GONE
        binding.ivEmpty.visibility = View.VISIBLE
        binding.tvEmptyMessage.visibility = View.VISIBLE
    }

    private fun showTracks(tracks: List<Track>) {
        binding.rvFavorites.visibility = View.VISIBLE
        binding.ivEmpty.visibility = View.GONE
        binding.tvEmptyMessage.visibility = View.GONE

        trackAdapter.setTracks(tracks)
    }

    private fun openAudioPlayer(track: Track) {
        val bundle = Bundle().apply {
            putString("track", Gson().toJson(track))
        }

        findNavController().navigate(
            R.id.action_libraryFragment_to_audioPlayerFragment,
            bundle
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = FavoritesFragment()
    }
}