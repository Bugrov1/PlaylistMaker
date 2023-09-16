package com.example.playlistmaker.ui.tracks


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
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.util.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.Track

import com.example.playlistmaker.presentation.trackSearch.SearchView
import com.example.playlistmaker.ui.player.PlayerActivity

class SearchActivity : AppCompatActivity(),
    SearchView {

    companion object {
        const val SAVED_INPUT = "SAVED_INPUT"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private val adapter = Adapter()
    private val adapterHistory = Adapter()

    private val searchPresenter =
        Creator.provideSearchPresenter(this, this)

    private val trackList = arrayListOf<Track>()

    private val handler = Handler(Looper.getMainLooper())

    private var isClickAllowed = true

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

    private var simpleTextWatcher: TextWatcher? = null

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

        recyclerView.adapter = adapter

        inputText = ""
        input = inputEditText


        val history = searchPresenter.read()


        if (history != null && history.size != 0) {
            //changed searchHistoryProvider via searchPresenter
            adapterHistory.tracks = searchPresenter.read()?.toCollection(ArrayList())!!
            historyRecycler.adapter = adapterHistory
            historyView.visibility = View.VISIBLE
        }


        val view: View? = currentFocus
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        // on below line hiding our keyboard.
        if (view != null) {
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }

        simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                clearButton.visibility = clearButtonVisibility(s)
                inputText = inputEditText.text.toString()
                searchPresenter.searchDebounce(changedText = s?.toString() ?: "")
                historyView.visibility =
                    if (inputEditText.hasFocus() && s?.isEmpty() == true && history?.size != 0) View.VISIBLE else View.GONE
                searchPresenter.searchDebounce(changedText = s?.toString() ?: "")


            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }
        simpleTextWatcher?.let { inputEditText.addTextChangedListener(it) }
        //inputEditText.addTextChangedListener(simpleTextWatcher)


        adapter.onItemClick = {
            if (clickDebounce()) {
                searchPresenter.write(it)
                //adapter.notifyDataSetChanged()
                val intent = Intent(this, PlayerActivity::class.java)
                putExtra(intent, it)
                startActivity(intent)
            }

        }

        adapterHistory.onItemClick = {
            if (clickDebounce()) {
                searchPresenter.write(it)
                // для обновления списка онлайн
                adapterHistory.notifyDataSetChanged()
                val intent = Intent(this, PlayerActivity::class.java)//PlayerActivity
                putExtra(intent, it)
                startActivity(intent)
                adapterHistory.tracks =
                    searchPresenter.read()?.toCollection(ArrayList())!!

            }
        }

        //button actions
        backButton.setOnClickListener {
            finish()
        }

        clearHistoryButton.setOnClickListener {
            Log.v(ContentValues.TAG, "history is  $history");
            searchPresenter.clear()
            historyView.visibility = View.GONE

        }

        placeholderButton.setOnClickListener {
             searchPresenter.searchDebounce(inputText) }

        clearButton.setOnClickListener {
            inputEditText.setText("")
            trackList.clear()
            updateTracksList(trackList)
            showPlaceholderImage(false)
            showPlaceholderMessage(false)
            showPlaceholderButton(false)

            val history = searchPresenter.read()?.toCollection(ArrayList())!!

            if (history == null || history.size == 0) {
                historyView.visibility = View.GONE
            } else {
                adapterHistory.tracks = history
                historyRecycler.adapter = adapterHistory
                adapterHistory.notifyDataSetChanged()
                historyView.visibility = View.VISIBLE
            }
            adapter.notifyDataSetChanged()

        }
    }


    override fun onDestroy() {
        super.onDestroy()
        simpleTextWatcher?.let { inputEditText.removeTextChangedListener(it) }
        searchPresenter.onDestroy()
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
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

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
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

    override fun showPlaceholderMessage(isVisible: Boolean) {
        placeholderMessage.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun showPlaceholderButton(isVisible: Boolean) {
        placeholderButton.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun showPlaceholderImage(isVisible: Boolean) {
        placeholderImage.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun showProgressBar(isVisible: Boolean) {
        progressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun showHistoryView(isVisible: Boolean) {
        historyView.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun showRecyclerView(isVisible: Boolean) {
        recyclerView.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun changePlaceholderText(newMessage: Int) {
        val text = this.getString(newMessage)
        placeholderMessage.text = text
    }

    override fun setPlaceholderImage(image: Int) {
        placeholderImage.setImageResource(image)
    }

    override fun updateTracksList(newTrackList: List<Track>) {
        adapter.tracks.clear()
        adapter.tracks.addAll(newTrackList)
        adapter.notifyDataSetChanged()
    }


}