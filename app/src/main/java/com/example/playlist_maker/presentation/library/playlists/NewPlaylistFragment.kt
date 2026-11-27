package com.example.playlist_maker.presentation.library.playlists

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlist_maker.R
import com.example.playlist_maker.databinding.FragmentNewPlaylistBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class NewPlaylistFragment : Fragment() {

    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NewPlaylistViewModel by viewModel()

    private var selectedCoverPath: String? = null

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let { saveImageToPrivateStorage(it) }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.etName.addTextChangedListener { text ->
            binding.btnCreate.isEnabled = !text.isNullOrBlank()
        }

        binding.ivCover.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.ivBack.setOnClickListener { confirmExitIfNeeded() }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    confirmExitIfNeeded()
                }
            }
        )

        binding.btnCreate.setOnClickListener {
            val name = binding.etName.text?.toString()?.trim() ?: ""
            val description = binding.etDescription.text?.toString()?.trim()
            viewModel.createPlaylist(name, description, selectedCoverPath)
            Toast.makeText(
                requireContext(),
                getString(R.string.playlist_created, name),
                Toast.LENGTH_SHORT
            ).show()
            findNavController().navigateUp()
        }
    }

    private fun saveImageToPrivateStorage(uri: Uri) {
        val filePath = File(
            requireContext().getDir("playlist_covers", Context.MODE_PRIVATE),
            "cover_${System.currentTimeMillis()}.jpg"
        )
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(filePath)
        inputStream?.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        selectedCoverPath = filePath.absolutePath

        Glide.with(this)
            .load(filePath)
            .transform(RoundedCorners(8))
            .into(binding.ivCover)
    }

    private fun confirmExitIfNeeded() {
        val isNameNotEmpty = !binding.etName.text.isNullOrBlank()
        val isDescriptionNotEmpty = !binding.etDescription.text.isNullOrBlank()
        val isCoverSelected = selectedCoverPath != null

        if (isNameNotEmpty || isDescriptionNotEmpty || isCoverSelected) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.finish_playlist_creation_title)
                .setMessage(R.string.finish_playlist_creation_message)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.finish) { _, _ ->
                    findNavController().navigateUp()
                }
                .show()
        } else {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
