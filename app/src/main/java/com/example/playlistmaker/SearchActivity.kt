package com.example.playlistmaker


import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    private val baseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunesService = retrofit.create(ItunesAPI::class.java)

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

    private val trackList = arrayListOf<Track>()
    private val adapter = Adapter()

    companion object {
        const val SAVED_INPUT = "SAVED_INPUT"
    }

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

        input = inputEditText

        adapter.tracks = trackList
        recyclerView.adapter = adapter

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
                if (s != null) {
                    historyView.visibility =
                        if (inputEditText.hasFocus() && s.isEmpty()) View.VISIBLE else View.GONE
                    //добавить проверку на историю поиска


                }
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)


    }

    private fun showMessage(noDataFound: Boolean) {
        if (noDataFound) {
            placeholderImage.setImageResource(R.drawable.nothing_found_image)
            placeholderMessage.text = getString(R.string.nothing_found)
            placeholderImage.visibility = View.VISIBLE
            placeholderMessage.visibility = View.VISIBLE
            placeholderButton.visibility = View.GONE
            trackList.clear()
            adapter.notifyDataSetChanged()

        } else {
            placeholderImage.setImageResource(R.drawable.goes_wrong_image)
            placeholderMessage.text = getString(R.string.something_went_wrong)
            placeholderImage.visibility = View.VISIBLE
            placeholderMessage.visibility = View.VISIBLE
            placeholderButton.visibility = View.VISIBLE
            trackList.clear()
            adapter.notifyDataSetChanged()
        }
    }

    private fun search(textToSearch: EditText) {
        itunesService.search(textToSearch.text.toString()).enqueue(object :
            Callback<TrackResponse> {
            override fun onResponse(
                call: Call<TrackResponse>,
                response: Response<TrackResponse>
            ) {
                if (response.code() == 200) {
                    trackList.clear()
                    if (response.body()?.results?.isNotEmpty() == true) {
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
                showMessage(false)
            }

        })
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