package com.batodev.tetris.presentation.settings

import android.os.Bundle
import android.widget.Toast
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.batodev.tetris.R
import com.batodev.tetris.domain.user.ValidateName

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        val editTextName: EditTextPreference = preferenceScreen.findPreference<Preference>(getString(R.string.key_name)) as EditTextPreference
        editTextName.setOnPreferenceChangeListener { _, newValue ->
            val name = newValue as String
            val result = ValidateName(name).execute()
            if (!result.success)
                Toast.makeText(requireContext(), result.errorMessage!!.asString(requireContext()), Toast.LENGTH_SHORT).show()
            result.success
        }
    }
}