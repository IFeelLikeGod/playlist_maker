package com.example.playlist_maker.presentation.search

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.EditorInfo
import com.example.playlist_maker.domain.usecase.HistoryInteractor
import com.example.playlist_maker.domain.usecase.TracksInteractor
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.addTextChangedListener
import com.example.playlist_maker.creator.Creator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlist_maker.presentation.player.AudioPlayerActivity
import com.example.playlist_maker.R
import com.example.playlist_maker.domain.models.Track
import com.google.gson.Gson



class SearchActivity : AppCompatActivity() {

    private lateinit var historyInteractor: HistoryInteractor
    private lateinit var tracksInteractor: TracksInteractor
    private lateinit var etSearch: AppCompatEditText
    private lateinit var placeholderEmpty: LinearLayout
    private lateinit var placeholderError: LinearLayout
    private lateinit var btnRetry: Button
    private lateinit var btnClear: Button
    private lateinit var recentSearchesContainer: LinearLayout
    private lateinit var recentSearchesRecyclerView: RecyclerView

    private var lastSearchText: String = ""

    private val currentTracks = mutableListOf<Track>()
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var ivClear: ImageView
    private var handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable ?= null
    private lateinit var progressBar: View
    private var isClickAllowed = true

    private var searchText: String = ""

    companion object {
        private const val SEARCH_TEXT_KEY = "SEARCH_TEXT_KEY"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        historyInteractor = Creator.provideHistoryInteractor(this)
        tracksInteractor = Creator.provideTracksInteractor()

        etSearch = findViewById(R.id.etSearch)
        ivClear = findViewById(R.id.ivClear)
        recyclerView = findViewById(R.id.recyclerView)
        placeholderEmpty = findViewById(R.id.placeholderEmpty)
        placeholderError = findViewById(R.id.placeholderError)
        btnRetry = findViewById(R.id.btnRetry)
        btnClear = findViewById(R.id.btnClear)
        recentSearchesContainer=findViewById(R.id.recentSearchesContainer)
        recentSearchesRecyclerView=findViewById(R.id.recentSearchesRecyclerView)
        progressBar = findViewById(R.id.progressBar)

        val ivBack = findViewById<ImageView>(R.id.ivBack)

        recyclerView.layoutManager = LinearLayoutManager(this)
        trackAdapter = TrackAdapter(currentTracks) { track ->
            historyInteractor.addTrack(track)
            if (clickDebounce()) {
                openAudioPlayer(track)
            }
        }
        recyclerView.adapter = trackAdapter
        searchText = savedInstanceState?.getString(SEARCH_TEXT_KEY, "") ?: ""
        etSearch.setText(searchText)
        ivClear.visibility = if (searchText.isEmpty()) View.GONE else View.VISIBLE

        etSearch.post {
            etSearch.requestFocus()
            showKeyboard(etSearch)
        }

        etSearch.addTextChangedListener { text ->
            searchText = text?.toString() ?: ""

            ivClear.visibility =
                if (searchText.isEmpty()) View.GONE else View.VISIBLE

            updateHistoryVisibility()

            if (searchText.isNotBlank()) {
                searchDebounce()
            } else {
                searchRunnable?.let {
                    handler.removeCallbacks(it)
                }

                progressBar.visibility = View.GONE
                recyclerView.visibility = View.GONE
                placeholderEmpty.visibility = View.GONE
                placeholderError.visibility = View.GONE
            }
        }
        etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchRunnable?.let { handler.removeCallbacks(it) }
                performSearch(searchText)
                true
            } else {
                false
            }
        }
        etSearch.setOnFocusChangeListener { _, hasFocus ->
            updateHistoryVisibility()
        }
        ivBack.setOnClickListener {
            finish()
        }

        btnRetry.setOnClickListener {
            performSearch(lastSearchText)
        }
        ivClear.setOnClickListener {
            etSearch.text?.clear()
            searchText = ""
            etSearch.clearFocus()
            hideKeyboard()
            ivClear.visibility = View.GONE

            currentTracks.clear()
            trackAdapter.notifyDataSetChanged()
            recyclerView.visibility = View.GONE
            placeholderEmpty.visibility = View.GONE
            placeholderError.visibility = View.GONE
        }
        btnClear.setOnClickListener {
            historyInteractor.clearHistory()
            updateHistoryVisibility()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT_KEY, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getString(SEARCH_TEXT_KEY, "") ?: ""
        etSearch.setText(searchText)
        ivClear.visibility = if (searchText.isEmpty()) View.GONE else View.VISIBLE
    }


    private fun clickDebounce(): Boolean{
        val current = isClickAllowed
        if (isClickAllowed){
            isClickAllowed = false
            handler.postDelayed({isClickAllowed = true}, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun searchDebounce() {
        searchRunnable?.let {
            handler.removeCallbacks(it)
        }

        searchRunnable = Runnable {
            performSearch(searchText)
        }

        handler.postDelayed(
            searchRunnable!!,
            SEARCH_DEBOUNCE_DELAY
        )
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
    private fun showTrackList() {
        recyclerView.visibility = View.VISIBLE
        placeholderEmpty.visibility = View.GONE
        placeholderError.visibility = View.GONE
    }

    private fun showEmptyPlaceholder() {
        recyclerView.visibility = View.GONE
        placeholderEmpty.visibility = View.VISIBLE
        placeholderError.visibility = View.GONE
    }

    private fun showErrorPlaceholder() {
        recyclerView.visibility = View.GONE
        placeholderEmpty.visibility = View.GONE
        placeholderError.visibility = View.VISIBLE
    }

    private fun openAudioPlayer(track: Track){
        val json = Gson().toJson(track)

        val intent = Intent(this, AudioPlayerActivity::class.java)
        intent.putExtra("track", json)
        startActivity(intent)
    }
    private fun updateHistoryVisibility() {
        val history = historyInteractor.getHistory()
        val shouldShowHistory = etSearch.hasFocus() && searchText.isEmpty() && history.isNotEmpty()
        if (shouldShowHistory) {
            recentSearchesContainer.visibility = View.VISIBLE
            recentSearchesRecyclerView.layoutManager = LinearLayoutManager(this)
            val historyAdapter = TrackAdapter(history) { track ->
                historyInteractor.addTrack(track)
                updateHistoryVisibility()
                if (clickDebounce()) {
                    openAudioPlayer(track)
                }
            }
            recentSearchesRecyclerView.adapter = historyAdapter
        } else {
            recentSearchesContainer.visibility = View.GONE
        }
    }
    private fun performSearch(query: String) {
        if (query.isBlank()) return

        lastSearchText = query

        progressBar.visibility = View.VISIBLE

        recyclerView.visibility = View.GONE
        placeholderEmpty.visibility = View.GONE
        placeholderError.visibility = View.GONE
        recentSearchesContainer.visibility = View.GONE

        tracksInteractor.search(query, object : TracksInteractor.TracksConsumer {
            override fun consume(foundTracks: List<Track>, errorMessage: String?) {
                runOnUiThread {
                    progressBar.visibility = View.GONE

                    if (errorMessage != null) {
                        showErrorPlaceholder()
                    } else {
                        currentTracks.clear()
                        currentTracks.addAll(foundTracks)
                        trackAdapter.notifyDataSetChanged()

                        if (foundTracks.isEmpty()) {
                            showEmptyPlaceholder()
                        } else {
                            showTrackList()
                        }
                    }
                }
            }
        })
    }
}