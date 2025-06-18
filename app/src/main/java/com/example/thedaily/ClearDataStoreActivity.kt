package com.example.thedaily

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

// Top-level property delegate for DataStore
val ComponentActivity.dataStore by preferencesDataStore(name = "settings")

class ClearDataStoreActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            dataStore.edit { it.clear() }
            Toast.makeText(this@ClearDataStoreActivity, "DataStore cleared. Please uninstall this activity.", Toast.LENGTH_LONG).show()
            finish()
        }
    }
}