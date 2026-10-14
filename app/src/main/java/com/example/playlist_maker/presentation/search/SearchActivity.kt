package com.example.playlist_maker.presentation.search

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlist_maker.R
import com.example.playlist_maker.domain.models.Track
import com.example.playlist_maker.presentation.player.AudioPlayerActivity
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {

    private val viewModel: SearchViewModel by viewModel()
    private lateinit var etSearch: AppCompatEditText
    private lateinit var ivClear: ImageView
    private lateinit var placeholderEmpty: LinearLayout
    private lateinit var placeholderError: LinearLayout
    private lateinit var btnRetry: Button
    private lateinit var btnClear: Button
    private lateinit var recentSearchesContainer: LinearLayout
    private lateinit var recentSearchesRecyclerView: RecyclerView
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: View

    private lateinit var trackAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    private val handler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)


        etSearch = findViewById(R.id.etSearch)
        ivClear = findViewById(R.id.ivClear)
        recyclerView = findViewById(R.id.recyclerView)
        placeholderEmpty = findViewById(R.id.placeholderEmpty)
        placeholderError = findViewById(R.id.placeholderError)
        btnRetry = findViewById(R.id.btnRetry)
        btnClear = findViewById(R.id.btnClear)
        recentSearchesContainer = findViewById(R.id.recentSearchesContainer)
        recentSearchesRecyclerView = findViewById(R.id.recentSearchesRecyclerView)
        progressBar = findViewById(R.id.progressBar)
        val ivBack = findViewById<ImageView>(R.id.ivBack)

        trackAdapter = TrackAdapter(::onTrackClick)
        historyAdapter = TrackAdapter(::onTrackClick)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = trackAdapter
        recentSearchesRecyclerView.layoutManager = LinearLayoutManager(this)
        recentSearchesRecyclerView.adapter = historyAdapter

        viewModel.observeState().observe(this) { render(it) }

        etSearch.post {
            etSearch.requestFocus()
            showKeyboard(etSearch)
        }

        etSearch.addTextChangedListener { text ->
            val query = text?.toString() ?: ""
            ivClear.isVisible = query.isNotEmpty()
            viewModel.onQueryChanged(query)
        }

        etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.onSearchSubmitted()
                true
            } else {
                false
            }
        }

        etSearch.setOnFocusChangeListener { _, hasFocus ->
            viewModel.onFocusChanged(hasFocus)
        }

        ivBack.setOnClickListener { finish() }
        btnRetry.setOnClickListener { viewModel.onRetryClicked() }
        btnClear.setOnClickListener { viewModel.onClearHistoryClicked() }

        ivClear.setOnClickListener {
            etSearch.text?.clear()
            etSearch.clearFocus()
            hideKeyboard()
        }
    }

    private fun render(state: SearchState) {
        progressBar.isVisible = state is SearchState.Loading
        recyclerView.isVisible = state is SearchState.Content
        placeholderEmpty.isVisible = state is SearchState.Empty
        placeholderError.isVisible = state is SearchState.Error
        recentSearchesContainer.isVisible = state is SearchState.History

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
        val intent = Intent(this, AudioPlayerActivity::class.java)
        intent.putExtra("track", Gson().toJson(track))
        startActivity(intent)
    }

    private fun showKeyboard(view: View) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        val view = currentFocus ?: View(this)
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}