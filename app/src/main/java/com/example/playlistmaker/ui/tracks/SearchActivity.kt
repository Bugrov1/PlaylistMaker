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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.Track

import com.example.playlistmaker.presentation.trackSearch.SearchViewModel
import com.example.playlistmaker.ui.player.PlayerActivity
import com.example.playlistmaker.ui.tracks.models.SearchState
import com.google.gson.Gson

class SearchActivity : AppCompatActivity() {//ComponentActivity()


    companion object {
        const val SAVED_INPUT = "SAVED_INPUT"
        private const val CLICK_DEBOUNCE_DELAY = 1000L

    }

    private val adapter = Adapter()
    private val adapterHistory = Adapter()

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
    private lateinit var history: Array<Track>
    private var simpleTextWatcher: TextWatcher? = null

    private lateinit var viewModel: SearchViewModel

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        viewModel = ViewModelProvider(
            this,
            SearchViewModel.getViewModelFactory()
        )[SearchViewModel::class.java]

        initViews()
        initListeners()
        inputText = ""
        input = inputEditText
        viewModel.observeState().observe(this) {
            render(it)
        }
        viewModel.historyload()
        viewModel.historyData.observe(this) {
            if (it != null) {
                history = it
            }
        }

    }

    fun initViews(){
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
        historyRecycler.adapter = adapterHistory
    }
    fun initListeners(){
        simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                clearButton.visibility = clearButtonVisibility(s)
                inputText = inputEditText.text.toString()
                viewModel.searchDebounce(changedText = s?.toString() ?: "")
                historyView.visibility =
                    if (inputEditText.hasFocus() && s?.isEmpty() == true && history?.size != 0) View.VISIBLE else View.GONE
                viewModel.searchDebounce(changedText = s?.toString() ?: "")


            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }
        simpleTextWatcher?.let { inputEditText.addTextChangedListener(it) }




        adapter.onItemClick = {
            if (clickDebounce()) {
                viewModel.write(it)
                //adapter.notifyDataSetChanged()
                val intent = Intent(this, PlayerActivity::class.java)
                intent.putExtra("track", Gson().toJson(it))
                //putExtra(intent, it)
                startActivity(intent)
            }
        }

        adapterHistory.onItemClick = {
            if (clickDebounce()) {
                viewModel.write(it)
                // для обновления списка онлайн
                adapterHistory.notifyDataSetChanged()
                val intent = Intent(this, PlayerActivity::class.java)//PlayerActivity
                intent.putExtra("track", Gson().toJson(it))
                //putExtra(intent, it)
                startActivity(intent)
                adapterHistory.tracks =
                    history?.toCollection(ArrayList())!!

            }
        }

        backButton.setOnClickListener {
            finish()
        }

        clearHistoryButton.setOnClickListener {
            // Log.v("View", "history is  $history");
            viewModel.clear()
            historyView.visibility = View.GONE

        }

        placeholderButton.setOnClickListener {
            viewModel.searchDebounce(inputText)
        }


        clearButton.setOnClickListener {
            inputEditText.setText("")
            trackList.clear()
            updateTracksList(trackList)

            //render(SearchState.History(history = history))
            viewModel.historyload()
            adapter.notifyDataSetChanged()
            val view: View? = currentFocus
            val inputMethodManager =
                getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            // on below line hiding our keyboard.
            if (view != null) {
                inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            }

        }
    }


    override fun onDestroy() {
        super.onDestroy()
        simpleTextWatcher?.let { inputEditText.removeTextChangedListener(it) }
        //searchPresenter.onDestroy()
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

    fun showLoading() {
        progressBar.visibility = View.VISIBLE
        placeholderMessage.visibility = View.GONE
        recyclerView.visibility = View.GONE
        placeholderImage.visibility = View.GONE
        placeholderButton.visibility = View.GONE
    }

    fun showEmpty(message: String) {
        progressBar.visibility = View.GONE
        placeholderImage.setImageResource(R.drawable.nothing_found_image)
        placeholderMessage.text = message
        placeholderImage.visibility = View.VISIBLE
        placeholderMessage.visibility = View.VISIBLE
        placeholderButton.visibility = View.GONE
        historyView.visibility = View.GONE
        trackList.clear()
        updateTracksList(trackList)
    }

    fun showError(message: String) {
        progressBar.visibility = View.GONE
        placeholderImage.setImageResource(R.drawable.goes_wrong_image)
        placeholderMessage.text = message
        placeholderImage.visibility = View.VISIBLE
        placeholderMessage.visibility = View.VISIBLE
        placeholderButton.visibility = View.VISIBLE
        historyView.visibility = View.GONE
        trackList.clear()
        updateTracksList(trackList)
    }

    fun showContent(foundTracks: List<Track>) {
        progressBar.visibility = View.GONE
        trackList.clear()
        trackList.addAll(foundTracks)
        updateTracksList(trackList)
        recyclerView.visibility = View.VISIBLE
    }


    fun updateTracksList(newTrackList: List<Track>) {
        adapter.tracks.clear()
        adapter.tracks.addAll(newTrackList)
        adapter.notifyDataSetChanged()
    }

    fun render(state: SearchState) {
        when (state) {
            is SearchState.Loading -> showLoading()
            is SearchState.Content -> showContent(state.tracks)
            is SearchState.Error -> showError(state.errorMessage)
            is SearchState.Empty -> showEmpty(state.message)
            is SearchState.History -> historyLoad(state.history)
            else -> {}
        }
    }

    fun historyLoad(history: Array<Track>?) {
        //val history = searchPresenter.read()
        placeholderImage.visibility = View.GONE
        placeholderMessage.visibility = View.GONE
        placeholderButton.visibility = View.GONE
        if (history == null || history.isEmpty()) {
            historyView.visibility = View.GONE
        } else {
            adapterHistory.tracks = history.toCollection(ArrayList())!!
            historyRecycler.adapter = adapterHistory
            adapterHistory.notifyDataSetChanged()
            historyView.visibility = View.VISIBLE
        }
    }






}