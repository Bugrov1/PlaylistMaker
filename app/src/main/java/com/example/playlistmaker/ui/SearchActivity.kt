package com.example.playlistmaker.ui


import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.SearchHistory
import com.example.playlistmaker.TRACK_SEARCH_HISTORY
import com.example.playlistmaker.data.dto.TrackResponse
import com.example.playlistmaker.data.network.ItunesAPI
import com.example.playlistmaker.domain.Track
import com.example.playlistmaker.ui.PlayerActivity
import com.example.playlistmaker.ui.Adapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : Activity() {
    private val baseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunesService = retrofit.create(ItunesAPI::class.java)

    companion object {
        const val SAVED_INPUT = "SAVED_INPUT"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private var isClickAllowed = true

    private val handler = Handler(Looper.getMainLooper())

    private lateinit var inputText: String
    private lateinit var input: EditText
    private lateinit var placeholderImage: ImageView
    private lateinit var placeholderMessage: TextView
    private lateinit var backButton: ImageButton
    private lateinit var inputEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var placeholderButton: Button
    private lateinit var historyView: View
    private lateinit var clearHistoryButton: Button
    private lateinit var historyRecycler: RecyclerView
    private lateinit var progressBar: ProgressBar

    private val trackList = arrayListOf<Track>()
    private val adapter = Adapter()



    //private val adapterHistory = AdapterHistory()
    private val adapterHistory = Adapter()


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        backButton = findViewById(R.id.back)
        inputEditText = findViewById(R.id.inputEditText)
        clearButton = findViewById(R.id.clearIcon)
        recyclerView = findViewById(R.id.recyclerView)
        placeholderImage = findViewById(R.id.placeholderImage)
        placeholderMessage = findViewById(R.id.placeholderMessage)
        placeholderButton = findViewById(R.id.placeholderButton)
        historyView = findViewById(R.id.historyViewList)
        clearHistoryButton = findViewById(R.id.clearHistory)
        historyRecycler = findViewById(R.id.historyRecycler)
        progressBar = findViewById(R.id.progressBar)

        inputText = ""
        input = inputEditText

        var sharedPreferences = getSharedPreferences(TRACK_SEARCH_HISTORY, MODE_PRIVATE)
        val history = SearchHistory(sharedPreferences).read()?.toCollection(ArrayList())

        adapter.tracks = trackList
        recyclerView.adapter = adapter
        adapter.onItemClick ={
            if (clickDebounce()) {
                sharedPreferences = getSharedPreferences(TRACK_SEARCH_HISTORY, MODE_PRIVATE)
                val history = SearchHistory(sharedPreferences)
                history.write(it)
                adapter.notifyDataSetChanged()
                val intent = Intent(this, PlayerActivity::class.java)
                putExtra(intent, it)

                startActivity(intent)
        }

        }

        adapterHistory.onItemClick = {
            if (clickDebounce()) {
                sharedPreferences = getSharedPreferences(TRACK_SEARCH_HISTORY, MODE_PRIVATE)
                val history = SearchHistory(sharedPreferences)
                history.write(it)
                adapterHistory.tracks =
                    SearchHistory(sharedPreferences).read()?.toCollection(ArrayList())!!
                adapterHistory.notifyDataSetChanged()
                val intent = Intent(this, PlayerActivity::class.java)//PlayerActivity

                putExtra(intent, it)
                putExtra(intent, it)
                startActivity(intent)
                //
            }
        }

        if (history != null && history.size != 0) {
            sharedPreferences = getSharedPreferences(TRACK_SEARCH_HISTORY, MODE_PRIVATE)
            adapterHistory.tracks =
                SearchHistory(sharedPreferences).read()?.toCollection(ArrayList())!!
            historyRecycler.adapter = adapterHistory
            historyView.visibility = View.VISIBLE

        }


        clearHistoryButton.setOnClickListener {
            Log.v(ContentValues.TAG, "history is is $history");
            SearchHistory(sharedPreferences).clear()
            historyView.visibility = View.GONE

        }
        backButton.setOnClickListener {
            finish()
        }
        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search(inputEditText)
                true
            }
            false
        }
        placeholderButton.setOnClickListener { search(input) }

        clearButton.setOnClickListener {
            inputEditText.setText("")
            trackList.clear()
            placeholderImage.visibility = View.GONE
            placeholderMessage.visibility = View.GONE
            placeholderButton.visibility = View.GONE
            val sharedPref = getSharedPreferences(TRACK_SEARCH_HISTORY, MODE_PRIVATE)
            val history = SearchHistory(sharedPref).read()?.toCollection(ArrayList())

            if (history == null || history.size == 0) {

                historyView.visibility = View.GONE
            } else {
                adapterHistory.tracks = history
                historyRecycler.adapter = adapterHistory
                adapterHistory.notifyDataSetChanged()
                historyView.visibility = View.VISIBLE
            }
            adapter.notifyDataSetChanged()


            val view: View? = this.currentFocus
            val inputMethodManager =
                getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

            // on below line hiding our keyboard.
            if (view != null) {
                inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            }

        }
        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
                inputText = inputEditText.text.toString()
                historyView.visibility =
                    if (inputEditText.hasFocus() && s?.isEmpty() == true && history?.size != 0) View.VISIBLE else View.GONE
                searchDebounce()

            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)


    }

    private val searchRunnable = Runnable { search(inputEditText) }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun showMessage(noDataFound: Boolean) {
        if (noDataFound) {
            placeholderImage.setImageResource(R.drawable.nothing_found_image)
            placeholderMessage.text = getString(R.string.nothing_found)
            placeholderImage.visibility = View.VISIBLE
            placeholderMessage.visibility = View.VISIBLE
            placeholderButton.visibility = View.GONE
            historyView.visibility = View.GONE
            trackList.clear()
            adapter.notifyDataSetChanged()

        } else {
            placeholderImage.setImageResource(R.drawable.goes_wrong_image)
            placeholderMessage.text = getString(R.string.something_went_wrong)
            placeholderImage.visibility = View.VISIBLE
            placeholderMessage.visibility = View.VISIBLE
            placeholderButton.visibility = View.VISIBLE
            historyView.visibility = View.GONE
            trackList.clear()
            adapter.notifyDataSetChanged()
        }
    }

    private fun putExtra(intent: Intent, track: Track) {
        intent.putExtra("trackName", track.trackName)
        intent.putExtra("artistName", track.artistName)
        intent.putExtra("trackTimeMillis", track.trackTimeMillis)
        intent.putExtra("artworkUrl100", track.artworkUrl100)
        intent.putExtra("collectionName", track.collectionName)
        intent.putExtra("releaseDate", track.releaseDate)
        intent.putExtra("primaryGenreName", track.primaryGenreName)
        intent.putExtra("country", track.country)
        intent.putExtra("previewUrl", track.previewUrl)
    }

    private fun search(textToSearch: EditText) {
        if (textToSearch.text.isNotEmpty()) {
            progressBar.visibility = View.VISIBLE
            placeholderMessage.visibility = View.GONE
            recyclerView.visibility = View.GONE

            itunesService.search(textToSearch.text.toString()).enqueue(object :
                Callback<TrackResponse> {
                override fun onResponse(
                    call: Call<TrackResponse>,
                    response: Response<TrackResponse>
                ) {
                    progressBar.visibility = View.GONE
                    if (response.code() == 200) {
                        trackList.clear()
                        if (response.body()?.results?.isNotEmpty() == true) {
                            recyclerView.visibility = View.VISIBLE
                            trackList.addAll(response.body()?.results!!)
                            adapter.notifyDataSetChanged()
                        }
                        if (trackList.isEmpty()) {
                            showMessage(true)
                        }
                    } else {
                        showMessage(false)
                    }
                }

                override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    showMessage(false)
                }

            })
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SAVED_INPUT, inputText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        inputText = savedInstanceState.getString(SAVED_INPUT).toString()
        input.setText(inputText)

    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }
}