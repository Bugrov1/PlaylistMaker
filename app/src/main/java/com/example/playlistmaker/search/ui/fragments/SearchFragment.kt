package com.example.playlistmaker.search.ui.fragments


import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.player.ui.fragment.PlayerFragment
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.Adapter
import com.example.playlistmaker.search.ui.models.SearchState
import com.example.playlistmaker.search.ui.viewmodel.SearchViewModel
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L

    }

    private val viewModel: SearchViewModel by viewModel()
    private val adapter = Adapter()
    private val adapterHistory = Adapter()


    private val trackList = arrayListOf<Track>()

    private lateinit var binding: FragmentSearchBinding

    private var isClickAllowed = true

    private lateinit var inputText: String
    private lateinit var input: EditText
    private lateinit var inputEditText: EditText
    private lateinit var history: Array<Track>
    private var simpleTextWatcher: TextWatcher? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root


    }

    @SuppressLint("MissingInflatedId")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        initListeners()

        history = viewModel.read() ?: emptyArray()
        inputText = ""
        input = inputEditText
        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
        isClickAllowed = true
    }


    private fun initViews() {
        inputEditText = binding.inputEditText
        binding.recyclerView.adapter = adapter
        binding.historyRecycler.adapter = adapterHistory


    }

    private fun initListeners() {
        simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                binding.clearIcon.visibility = clearButtonVisibility(s)
                inputText = inputEditText.text.toString()
                viewModel.searchDebounce2(changedText = s?.toString() ?: "")
                binding.historyViewList.visibility =
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
                startPlayer(it)
            }
        }

        adapterHistory.onItemClick = {
            if (clickDebounce()) {
                viewModel.write(it)
                viewModel.update()
                startPlayer(it)
            }
        }

        binding.clearHistory.setOnClickListener {
            viewModel.clear()
            binding.historyViewList.visibility = View.GONE
        }

        binding.placeholderButton.setOnClickListener {
//            viewModel.searchDebounce2(inputText)
            viewModel.onPlaceHolderButtonClicked(inputText)
        }

        binding.clearIcon.setOnClickListener {
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

    private fun startPlayer(track: Track) {
        val trackGson = Gson().toJson(track)
        findNavController().navigate(
            R.id.action_searchFragment_to_playerFragment,
            PlayerFragment.createArgs(trackGson)
        )

    }


    override fun onDestroy() {
        super.onDestroy()
        simpleTextWatcher?.let { inputEditText.removeTextChangedListener(it) }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        isClickAllowed = true
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            viewLifecycleOwner.lifecycleScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
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
        binding.progressBar.visibility = View.VISIBLE
        binding.placeholderMessage.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
        binding.placeholderImage.visibility = View.GONE
        binding.placeholderButton.visibility = View.GONE
    }

    fun showEmpty(message: String) {
        binding.progressBar.visibility = View.GONE
        binding.placeholderImage.setImageResource(R.drawable.nothing_found_image)
        binding.placeholderMessage.text = message
        binding.placeholderImage.visibility = View.VISIBLE
        binding.placeholderMessage.visibility = View.VISIBLE
        binding.placeholderButton.visibility = View.GONE
        binding.historyViewList.visibility = View.GONE
        trackList.clear()
        updateTracksList(trackList)
    }

    fun showError(message: String) {
        binding.progressBar.visibility = View.GONE
        binding.placeholderImage.setImageResource(R.drawable.goes_wrong_image)
        binding.placeholderMessage.text = message
        binding.placeholderImage.visibility = View.VISIBLE
        binding.placeholderMessage.visibility = View.VISIBLE
        binding.placeholderButton.visibility = View.VISIBLE
        binding.historyViewList.visibility = View.GONE
        trackList.clear()
        updateTracksList(trackList)
    }

    fun showContent(foundTracks: List<Track>) {
        binding.progressBar.visibility = View.GONE
        trackList.clear()
        trackList.addAll(foundTracks)
        updateTracksList(trackList)
        binding.recyclerView.visibility = View.VISIBLE
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
        binding.placeholderImage.visibility = View.GONE
        binding.placeholderMessage.visibility = View.GONE
        binding.placeholderButton.visibility = View.GONE

        if (history == null || history.isEmpty()) {
            binding.historyViewList.visibility = View.GONE
            this.history = emptyArray()

        } else {
            this.history = history
            adapterHistory.tracks = history.toCollection(ArrayList())
            binding.historyRecycler.adapter = adapterHistory
            adapterHistory.notifyDataSetChanged()
            binding.recyclerView.visibility = View.VISIBLE
            binding.historyViewList.visibility = View.VISIBLE
        }
    }
}