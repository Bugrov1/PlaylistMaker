package com.example.playlistmaker.mediateka.ui.fragments


import android.content.Context.MODE_PRIVATE
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.example.playlistmaker.mediateka.domain.model.Playlist
import com.example.playlistmaker.mediateka.ui.models.ButtonState
import com.example.playlistmaker.mediateka.ui.models.DialogStatus
import com.example.playlistmaker.mediateka.ui.viewmodel.CreatePlaylistViemodel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class CreatePlaylistFragment : Fragment() {

    private val viewModel: CreatePlaylistViemodel by viewModel()
    private lateinit var backButton: ImageButton
    private lateinit var binding: FragmentNewPlaylistBinding
    private var simpleTextWatcher: TextWatcher? = null
    private lateinit var nameEditText: EditText
    private lateinit var createButton: TextView
    private lateinit var addPhoto: ImageButton
    private lateinit var playlistName: String
    lateinit var confirmDialog: MaterialAlertDialogBuilder
    private var photoPath: Uri? = null
    private lateinit var description: String
    private lateinit var descriptionEditText: EditText
    private var simpleTextWatcher2: TextWatcher? = null
    private var uriPhoto: Uri? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.createButton.isEnabled = false
        nameEditText = binding.EditTextName
        createButton = binding.createButton
        addPhoto = binding.addPhoto
        backButton = binding.backButton
        descriptionEditText = binding.editTextDescription


        viewModel.state.observe(viewLifecycleOwner) {
            renderButton(it)
        }

        playlistName = ""

        nameEditText.doOnTextChanged { text, start, before, count ->
            viewModel.changeButtonstate(text.toString())
            if (text != null) {
                playlistName = text.toString()
            }

        }




        description = ""
        simpleTextWatcher2 = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null) {
                    description = s.toString()
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        }
        simpleTextWatcher2?.let { descriptionEditText.addTextChangedListener(it) }


        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->

                if (uri != null) {
                    addPhoto.setImageURI(uri)
                    uriPhoto = uri

                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }

        addPhoto.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        var launchStatus: DialogStatus = DialogStatus.Hidden
        confirmDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Завершить создание плейлиста?")
            .setMessage("Все несохраненные данные будут потеряны.")
            .setNeutralButton("Отмена") { dialog, which ->
                launchStatus = DialogStatus.Hidden
            }.setPositiveButton("Завершить") { dialog, which ->
                requireActivity().onBackPressedDispatcher.onBackPressed()

            }

        backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {

            override fun handleOnBackPressed() {

                when (playlistName.isNotEmpty()) {
                    true -> {
                        when (launchStatus) {
                            DialogStatus.Launched -> {
                                isEnabled = false
                                requireActivity().onBackPressedDispatcher.onBackPressed()
                            }

                            DialogStatus.Neutral -> {
                                isEnabled = false
                                requireActivity().onBackPressedDispatcher.onBackPressed()
                            }

                            else -> {
                                confirmDialog.show()
                                launchStatus = DialogStatus.Launched
                            }
                        }


                    }

                    false -> {
                        isEnabled = false
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    }
                }

            }
        })



        createButton.setOnClickListener {
            saveImageToPrivateStorage(uriPhoto)
            val playlist = Playlist(
                id = null,
                playlistName = playlistName,
                description = description,
                filepath = photoPath, tracks = null,
                length = null
            )
            viewModel.createPlaylist(playlist)

            Toast.makeText(requireContext(), "Плейлист $playlistName создан", Toast.LENGTH_SHORT)
                .show()
            launchStatus = DialogStatus.Neutral
            requireActivity().onBackPressedDispatcher.onBackPressed()

        }


    }

    override fun onDestroyView() {
        super.onDestroyView()

    }

    private fun renderButton(state: ButtonState) {
        when (state) {
            is ButtonState.Enabled -> createButton.isEnabled = true
            is ButtonState.Disabled -> createButton.isEnabled = false

            else -> {}
        }

    }

    private fun saveImageToPrivateStorage(uri: Uri?) {
        if (uri != null) {
            val filePath = File(requireContext().getDir(playlistName, MODE_PRIVATE), "albums")
            if (!filePath.exists()) {
                filePath.mkdirs()
            }
            val file = File(filePath, playlistName + ".jpg")
            photoPath = file.toUri()
            val inputStream = requireActivity().contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)
            BitmapFactory
                .decodeStream(inputStream)
                .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
        }

    }


}