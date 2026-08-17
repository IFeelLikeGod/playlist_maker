package com.example.playlist_maker

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import retrofit2.Call
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.LinearLayout
import retrofit2.Callback
import retrofit2.Response
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.addTextChangedListener

class SearchActivity : AppCompatActivity() {

    private lateinit var etSearch: AppCompatEditText

    private lateinit var placeholderEmpty: LinearLayout
    private lateinit var placeholderError: LinearLayout
    private lateinit var btnRetry: Button

    private var lastSearchText: String = ""

    private val currentTracks = mutableListOf<Track>()
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var ivClear: ImageView
    private var searchText: String = ""

    companion object {
        private const val SEARCH_TEXT_KEY = "SEARCH_TEXT_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        etSearch = findViewById(R.id.etSearch)
        ivClear = findViewById(R.id.ivClear)
        recyclerView = findViewById(R.id.recyclerView)
        placeholderEmpty = findViewById(R.id.placeholderEmpty)
        placeholderError = findViewById(R.id.placeholderError)
        btnRetry = findViewById(R.id.btnRetry)

        val ivBack = findViewById<ImageView>(R.id.ivBack)

        recyclerView.layoutManager = LinearLayoutManager(this)
        trackAdapter = TrackAdapter(currentTracks)
        recyclerView.layoutManager = LinearLayoutManager(this)
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
            ivClear.visibility = if (searchText.isEmpty()) View.GONE else View.VISIBLE
        }
        etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                performSearch(searchText)
                true
            } else {
                false
            }
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



    private fun showKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
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
    private fun performSearch(query: String) {
        if (query.isBlank()) return
        lastSearchText = query

        RetrofitClient.iTunesApi.search(query).enqueue(object : Callback<TrackSearchResponse> {
            override fun onResponse(
                call: Call<TrackSearchResponse>,
                response: Response<TrackSearchResponse>
            ) {
                if (response.isSuccessful) {
                    val tracks = response.body()?.results?.map { it.toTrack() } ?: emptyList()

                    currentTracks.clear()
                    currentTracks.addAll(tracks)
                    trackAdapter.notifyDataSetChanged()

                    if (tracks.isEmpty()) {
                        showEmptyPlaceholder()
                    } else {
                        showTrackList()
                    }
                } else {
                    showErrorPlaceholder()
                }
            }

            override fun onFailure(call: Call<TrackSearchResponse>, t: Throwable) {
                showErrorPlaceholder()
            }
        })
    }
}