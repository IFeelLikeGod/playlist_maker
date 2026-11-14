package com.example.playlist_maker.presentation.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.playlist_maker.databinding.FragmentSettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeTheme().observe(viewLifecycleOwner) { settings ->
            binding.switchDarkTheme.isChecked = settings.darkTheme
            AppCompatDelegate.setDefaultNightMode(
                if (settings.darkTheme) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        binding.switchDarkTheme.setOnCheckedChangeListener { _, checked ->
            viewModel.onThemeChanged(checked)
        }

        binding.layoutShare.setOnClickListener { viewModel.onShareClicked() }
        binding.layoutSupport.setOnClickListener { viewModel.onSupportClicked() }
        binding.layoutAgreement.setOnClickListener { viewModel.onTermsClicked() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}