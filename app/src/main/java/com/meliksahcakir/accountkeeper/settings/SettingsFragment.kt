package com.meliksahcakir.accountkeeper.settings

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.meliksahcakir.accountkeeper.AccountKeeperApplication
import com.meliksahcakir.accountkeeper.MainActivity
import com.meliksahcakir.accountkeeper.R
import com.meliksahcakir.accountkeeper.login.LoginActivity
import com.meliksahcakir.accountkeeper.login.LoginRepository
import com.meliksahcakir.accountkeeper.utils.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.settings_fragment.*

class SettingsFragment : Fragment() {

    private val viewModel by viewModels<SettingsViewModel> {
        SettingsViewModelFactory(
            LoginRepository.getInstance(),
            (requireActivity().application as AccountKeeperApplication).accountRepository
        )
    }

    private val sharedPreferenceListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == PREF_NIGHT_MODE) {
                darkThemeSwitch.isChecked =
                    Preferences.nightMode == AppCompatDelegate.MODE_NIGHT_YES
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.settings_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpNavigation()
        Preferences.preferences.registerOnSharedPreferenceChangeListener(sharedPreferenceListener)
        darkThemeSwitch.isChecked = Preferences.nightMode == AppCompatDelegate.MODE_NIGHT_YES
        darkThemeSwitch.setOnCheckedChangeListener { _, isChecked ->
            Preferences.nightMode =
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        }
        viewModel.navigateToLoginScreen.observe(viewLifecycleOwner, EventObserver {
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        })
        viewModel.syncBusy.observe(viewLifecycleOwner) {
            syncCardView.busy = it
        }
        viewModel.snackBarParams.observe(viewLifecycleOwner, EventObserver {
            showSnackBar(it)
        })
        viewModel.editing.observe(viewLifecycleOwner) {
            userNameCardView.isTextVisible = !it
            userNameEditText.isVisible = it
            userNameEditText.setText(Preferences.userName)
            saveButton.isVisible = it
            if (it) {
                editButton.setImageResource(R.drawable.ic_close)
                userNameEditText.requestFocus()
                userNameEditText.moveCursorToEnd()
            } else {
                editButton.setImageResource(R.drawable.ic_edit)
                userNameCardView.text = Preferences.userName
            }
        }
        userNameCardView.text = Preferences.userName
        userEmailCardView.text = Preferences.userEmail
        editButton.setOnClickListener {
            viewModel.onEditProfileButtonClicked()
        }
        saveButton.setOnClickListener {
            viewModel.onSaveButtonClicked(userNameEditText.text.toString().trim())
        }
        signOutCardView.setOnClickListener {
            viewModel.onSignOutButtonClicked()
        }
        syncCardView.setOnClickListener {
            viewModel.onSyncButtonClicked()
        }
        shareProfileCardView.setOnClickListener {
            viewModel.onShareProfileButtonClicked(requireActivity())
        }
        changePasswordCardView.setOnClickListener {
            viewModel.onChangePasswordButtonClicked()
        }
        privacyPolicyCardView.setOnClickListener {
            val url = "https://meliksahcakirr.github.io/accountkeeper/privacy-policy.html"
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(browserIntent)
        }
        termsCardView.setOnClickListener {
            val url = "https://meliksahcakirr.github.io/accountkeeper/terms-and-conditions.html"
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(browserIntent)
        }
    }

    private fun setUpNavigation() {
        val mainFab = (requireActivity() as MainActivity).mainFab
        mainFab.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun showSnackBar(parameters: SnackBarParameters) {
        val snackbar =
            Snackbar.make(requireView(), parameters.messageStringId, Snackbar.LENGTH_SHORT)
        snackbar.anchorView = (requireActivity() as MainActivity).mainFab
        snackbar.show()
    }

    override fun onDetach() {
        super.onDetach()
        Preferences.preferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceListener)
    }
}
