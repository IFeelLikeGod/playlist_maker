package com.example.playlist_maker.presentation.search

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlist_maker.R
import com.example.playlist_maker.databinding.FragmentSearchBinding
import com.example.playlist_maker.domain.models.Track
import com.google.gson.Gson
import androidx.core.os.bundleOf
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModel()

    private lateinit var trackAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    private val handler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        trackAdapter = TrackAdapter(::onTrackClick)
        historyAdapter = TrackAdapter(::onTrackClick)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = trackAdapter
        binding.recentSearchesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recentSearchesRecyclerView.adapter = historyAdapter

        viewModel.observeState().observe(viewLifecycleOwner) { render(it) }

        binding.etSearch.addTextChangedListener { text ->
            val query = text?.toString() ?: ""
            binding.ivClear.isVisible = query.isNotEmpty()
            viewModel.onQueryChanged(query)
        }

        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.onSearchSubmitted()
                true
            } else {
                false
            }
        }

        binding.etSearch.setOnFocusChangeListener { _, hasFocus ->
            viewModel.onFocusChanged(hasFocus)
        }

        binding.btnRetry.setOnClickListener { viewModel.onRetryClicked() }
        binding.btnClear.setOnClickListener { viewModel.onClearHistoryClicked() }

        binding.ivClear.setOnClickListener {
            binding.etSearch.text?.clear()
            binding.etSearch.clearFocus()
            hideKeyboard()
        }
    }

    private fun render(state: SearchState) {
        binding.progressBar.isVisible = state is SearchState.Loading
        binding.recyclerView.isVisible = state is SearchState.Content
        binding.placeholderEmpty.isVisible = state is SearchState.Empty
        binding.placeholderError.isVisible = state is SearchState.Error
        binding.recentSearchesContainer.isVisible = state is SearchState.History

        when (state) {
            is SearchState.Content -> trackAdapter.setTracks(state.tracks)
            is SearchState.History -> historyAdapter.setTracks(state.tracks)
            else -> Unit
        }
    }

    private fun onTrackClick(track: Track) {
        if (clickDebounce()) {
            viewModel.onTrackClicked(track)
            openAudioPlayer(track)
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun openAudioPlayer(track: Track) {
        findNavController().navigate(
            R.id.action_searchFragment_to_audioPlayerFragment,
            bundleOf("track" to Gson().toJson(track))
        )
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etSearch.windowToken, 0)
    }

    override fun onDestroyView() {
        handler.removeCallbacksAndMessages(null)
        isClickAllowed = true
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}