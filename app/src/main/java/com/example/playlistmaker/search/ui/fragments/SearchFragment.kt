package com.example.playlistmaker.search.ui.fragments


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.player.ui.activity.PlayerActivity
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.Adapter
import com.example.playlistmaker.search.ui.models.SearchState
import com.example.playlistmaker.search.ui.viewmodel.SearchViewModel
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    companion object {
        const val SAVED_INPUT = "SAVED_INPUT"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private val adapter = Adapter()
    private val adapterHistory = Adapter()
    private val trackList = arrayListOf<Track>()
    private val handler = Handler(Looper.getMainLooper())

    private lateinit var binding: FragmentSearchBinding

    private var isClickAllowed = true

    private lateinit var inputText: String
    private lateinit var input: EditText
    private lateinit var placeholderImage: ImageView
    private lateinit var placeholderMessage: TextView

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


    private val viewModel: SearchViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root

    }

    @SuppressLint("MissingInflatedId")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       Log.v("TESSSSSSSSSSST","onViewCreated")
        initViews()
        initListeners()
        inputText = ""
        input = inputEditText
        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
            Log.v("TESSSSSSSSSSST","$it")
        }
    }

    private fun initViews() {
        inputEditText = binding.inputEditText
        clearButton = binding.clearIcon
        recyclerView = binding.recyclerView
        placeholderImage = binding.placeholderImage
        placeholderMessage = binding.placeholderMessage
        placeholderButton = binding.placeholderButton
        historyView = binding.historyViewList
        clearHistoryButton = binding.clearHistory
        historyRecycler = binding.historyRecycler
        progressBar = binding.progressBar

        recyclerView.adapter = adapter
        historyRecycler.adapter = adapterHistory

    }

    private fun initListeners() {
        simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                clearButton.visibility = clearButtonVisibility(s)
                inputText = inputEditText.text.toString()
                viewModel.searchDebounce2(changedText = s?.toString() ?: "")
                historyView.visibility =
                    if (inputEditText.hasFocus() && s?.isEmpty() == true && history?.size != 0) View.VISIBLE else View.GONE
                viewModel.searchDebounce2(changedText = s?.toString() ?: "")


            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }
        simpleTextWatcher?.let { inputEditText.addTextChangedListener(it) }

        adapter.onItemClick = {
            if (clickDebounce()) {
                viewModel.write(it)
                val intent = Intent(requireContext(), PlayerActivity::class.java)
                intent.putExtra("track", Gson().toJson(it))

                startActivity(intent)

            }
        }

        adapterHistory.onItemClick = {
            if (clickDebounce()) {
                viewModel.write(it)
                viewModel.update()
                val intent = Intent(requireContext(), PlayerActivity::class.java)
                intent.putExtra("track", Gson().toJson(it))

                startActivity(intent)
            }
        }

        clearHistoryButton.setOnClickListener {

            viewModel.clear()
            historyView.visibility = View.GONE

        }

        placeholderButton.setOnClickListener {
            viewModel.searchDebounce2(inputText)
        }

        clearButton.setOnClickListener {
            inputEditText.setText("")
            trackList.clear()
            updateTracksList(trackList)
            viewModel.historyload()
            adapter.notifyDataSetChanged()
            val view: View? = requireActivity().currentFocus
            val inputMethodManager =
                requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            if (view != null) {
                inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v("TESSSSSSSSSSST","onDestroy")
        simpleTextWatcher?.let { inputEditText.removeTextChangedListener(it) }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
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

    fun updateHistory(history: Array<Track>?) {
        if (history != null) {
            this.history = history
            adapterHistory.tracks = history.toCollection(ArrayList())
            adapterHistory.notifyDataSetChanged()
        }
    }

    fun render(state: SearchState) {
        when (state) {
            is SearchState.Loading -> showLoading()
            is SearchState.Content -> showContent(state.tracks)
            is SearchState.Error -> showError(state.errorMessage)
            is SearchState.Empty -> showEmpty(state.message)
            is SearchState.History -> historyLoad(state.history)
            is SearchState.Update -> updateHistory(state.history)
            else -> {}
        }
    }

    fun historyLoad(history: Array<Track>?) {
        placeholderImage.visibility = View.GONE
        placeholderMessage.visibility = View.GONE
        placeholderButton.visibility = View.GONE
        if (history == null || history.isEmpty()) {
            historyView.visibility = View.GONE

        } else {
            this.history = history
            adapterHistory.tracks = history.toCollection(ArrayList())
            historyRecycler.adapter = adapterHistory
            adapterHistory.notifyDataSetChanged()
            recyclerView.visibility = View.VISIBLE
            historyView.visibility = View.VISIBLE
        }
    }
}