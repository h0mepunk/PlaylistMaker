package com.example.playlistmaker.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.domain.api.ThemeInteractor
import com.example.playlistmaker.presentation.settings.SettingsState
import com.example.playlistmaker.presentation.settings.SettingsViewModel
import org.koin.android.ext.android.inject
import kotlin.getValue

class SettingsFragment : Fragment() {

    private val viewModel by viewModels<SettingsViewModel>()
    private val themeInteractor: ThemeInteractor by inject()

    private lateinit var binding: FragmentSettingsBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.settingItemContactSupport.setOnClickListener { viewModel.clickContactSupport() }
        binding.settingItemShare.setOnClickListener { viewModel.clickShareApp() }
        binding.settingItemUserAgreement.setOnClickListener { viewModel.clickUserAgreement() }
        binding.settingItemDarkTheme.setOnCheckedChangeListener { _, isChecked ->
            viewModel.switchTheme(isChecked)
        }

        binding.settingItemDarkTheme.isChecked = themeInteractor.getTheme()

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(layoutInflater)
        return binding.root
    }

    fun clickContactSupport() {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = "mailto:".toUri()
        intent.putExtra(
            Intent.EXTRA_EMAIL,
            getString(R.string.support_email_value)
        )
        intent.putExtra(
            Intent.EXTRA_SUBJECT,
            getString(R.string.support_message_default_subject_value)
        )
        intent.putExtra(
            Intent.EXTRA_TEXT,
            getString(R.string.support_message_default_text_value)
        )
        startActivity(intent)
    }

    fun clickShareButton() {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(
            Intent.EXTRA_TEXT,
            getString(R.string.share_url_value)
        )
        startActivity(intent)
    }

    fun clickTheme(isDarkTheme: Boolean) {
        themeInteractor.setTheme(isDarkTheme)
    }

    fun clickUserAgreement() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = getString(R.string.user_agreement_url_value).toUri()
        startActivity(intent)
    }

    fun render(state: SettingsState) {
        when(state) {
            is SettingsState.Share -> {
                clickShareButton()
            }
            is SettingsState.Support ->  {
                clickContactSupport()
            }
            is SettingsState.Theme -> {
                clickTheme(state.isDarkThemeEnabled)
            }
            is SettingsState.UserAgreement ->  {
                clickUserAgreement()
            }
        }
    }
}