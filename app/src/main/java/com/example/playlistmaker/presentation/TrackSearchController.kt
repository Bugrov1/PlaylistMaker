package com.example.playlistmaker.presentation

import android.app.Activity
import android.content.ContentValues
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.Track
import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.ui.Adapter
import com.example.playlistmaker.util.Creator

class TrackSearchController(
    private val activity: Activity,
    private val adapter: Adapter,
    private val adapterHistory: Adapter
) {
    companion object {
        const val SAVED_INPUT = "SAVED_INPUT"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private var isClickAllowed = true

    private val handler = Handler(Looper.getMainLooper())

    private val tracksInteractor = Creator.provideTrackInteractor(activity)
    private val trackList = arrayListOf<Track>()


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

    private val searchRunnable = Runnable { searchRequest(inputEditText) }
    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    fun onCreate() {
        //
        backButton = activity.findViewById(R.id.back)
        inputEditText = activity.findViewById(R.id.inputEditText)
        clearButton = activity.findViewById(R.id.clearIcon)
        recyclerView = activity.findViewById(R.id.recyclerView)
        placeholderImage = activity.findViewById(R.id.placeholderImage)
        placeholderMessage = activity.findViewById(R.id.placeholderMessage)
        placeholderButton = activity.findViewById(R.id.placeholderButton)
        historyView = activity.findViewById(R.id.historyViewList)
        clearHistoryButton = activity.findViewById(R.id.clearHistory)
        historyRecycler = activity.findViewById(R.id.historyRecycler)
        progressBar = activity.findViewById(R.id.progressBar)

        inputText = ""
        input = inputEditText

        val adapterHistory = adapterHistory

        adapter.tracks = trackList
        recyclerView.adapter = adapter

        val searchHistoryProvider = Creator.provideSearchHistory(activity.applicationContext)
        val history = searchHistoryProvider.read()

        if (history != null && history.size != 0) {

            adapterHistory.tracks = searchHistoryProvider.read()?.toCollection(ArrayList())!!
            historyRecycler.adapter = adapterHistory
            historyView.visibility = View.VISIBLE

        }

        //Buttons actions////////////////////////////////////////////////////////////
        clearHistoryButton.setOnClickListener {
            Log.v(ContentValues.TAG, "history is  $history");
            searchHistoryProvider.clear()
            historyView.visibility = View.GONE

        }

        backButton.setOnClickListener {
            activity.finish()
        }

        placeholderButton.setOnClickListener { searchRequest(input) }

        clearButton.setOnClickListener {
            inputEditText.setText("")
            trackList.clear()
            placeholderImage.visibility = View.GONE
            placeholderMessage.visibility = View.GONE
            placeholderButton.visibility = View.GONE
            //val sharedPref = getSharedPreferences(TRACK_SEARCH_HISTORY, MODE_PRIVATE)
            // val history = SearchHistory(sharedPref).read()?.toCollection(ArrayList())
            val history = searchHistoryProvider.read()?.toCollection(ArrayList())!!

            if (history == null || history.size == 0) {

                historyView.visibility = View.GONE
            } else {
                adapterHistory.tracks = history
                historyRecycler.adapter = adapterHistory
                adapterHistory.notifyDataSetChanged()
                historyView.visibility = View.VISIBLE
            }
            adapter.notifyDataSetChanged()


            val view: View? = activity.currentFocus
            val inputMethodManager =
                activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            // on below line hiding our keyboard.
            if (view != null) {
                inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            }

        }

        /////////////////////////////////////////////////////////////////////////////////

        //EditText actions
        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchRequest(inputEditText)//search(inputEditText)
                true
            }
            false
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


        //////////////////////////////////////////////////////////////////////////////////////


    }

    ////Functions

    private fun searchRequest(textToSearch: EditText) {
        if (textToSearch.text.isNotEmpty()) {

            progressBar.visibility = View.VISIBLE
            placeholderMessage.visibility = View.GONE
            recyclerView.visibility = View.GONE

            tracksInteractor.searchTracks(
                textToSearch.text.toString(),
                object : TrackInteractor.TracksConsumer {
                    override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                        handler.post {
                            if (foundTracks != null) {
                                progressBar.visibility = View.GONE
                                trackList.clear()
                                trackList.addAll(foundTracks)
                                recyclerView.visibility = View.VISIBLE
                                adapter.notifyDataSetChanged()
                            }
                            if (errorMessage != null) {
                                showMessage(false)
                            } else if (trackList.isEmpty()) {
                                //Log.v(ContentValues.TAG, "Something here")
                                showMessage(true)
                            }
                        }
                    }
                })
        }
    }

    private fun showMessage(noDataFound: Boolean) {
        if (noDataFound) {
            progressBar.visibility = View.GONE
            placeholderImage.setImageResource(R.drawable.nothing_found_image)
            placeholderMessage.text = activity.getString(R.string.nothing_found)
            placeholderImage.visibility = View.VISIBLE
            placeholderMessage.visibility = View.VISIBLE
            placeholderButton.visibility = View.GONE
            historyView.visibility = View.GONE
            trackList.clear()
            adapter.notifyDataSetChanged()

        } else {
            progressBar.visibility = View.GONE
            placeholderImage.setImageResource(R.drawable.goes_wrong_image)
            placeholderMessage.text = activity.getString(R.string.something_went_wrong)
            placeholderImage.visibility = View.VISIBLE
            placeholderMessage.visibility = View.VISIBLE
            placeholderButton.visibility = View.VISIBLE
            historyView.visibility = View.GONE
            trackList.clear()
            adapter.notifyDataSetChanged()
        }
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    fun onSaveInstanceState(outState: Bundle) {
        outState.putString(SAVED_INPUT, inputText)
    }

    fun onRestoreInstanceState(savedInstanceState: Bundle) {
        inputText = savedInstanceState.getString(SAVED_INPUT).toString()
        input.setText(inputText)

    }

    fun onDestroy() {
        handler.removeCallbacks(searchRunnable)
    }


}