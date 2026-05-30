package com.example.playlist_maker

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.addTextChangedListener

class SearchActivity : AppCompatActivity() {

    private lateinit var etSearch: AppCompatEditText
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

        val ivBack = findViewById<ImageView>(R.id.ivBack)

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

        ivBack.setOnClickListener {
            finish()
        }

        ivClear.setOnClickListener {
            etSearch.text?.clear()
            searchText = ""
            etSearch.clearFocus()
            hideKeyboard()
            ivClear.visibility = View.GONE
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
}