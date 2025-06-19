package com.example.thedaily

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.thedaily.data.AppDatabase
import com.example.thedaily.data.SettingsManager
import com.example.thedaily.data.repository.TheDailyRepository
import com.example.thedaily.ui.theme.TheDailyTheme
import kotlinx.coroutines.launch

class ClearDataStoreActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TheDailyTheme {
                ClearDataScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClearDataScreen() {
    val context = LocalContext.current
    var isClearing by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Clear App Data") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "This tool helps clear app data if you're experiencing database issues.",
                style = MaterialTheme.typography.bodyLarge
            )
            
            if (message.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        text = message,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            
            Button(
                onClick = {
                    isClearing = true
                    message = "Clearing database..."
                    (context as ComponentActivity).lifecycleScope.launch {
                        try {
                            clearDatabase(context)
                            message = "Database cleared successfully!"
                        } catch (e: Exception) {
                            message = "Error clearing database: ${e.message}"
                            Log.e("ClearDataStore", "Error clearing database", e)
                        } finally {
                            isClearing = false
                        }
                    }
                },
                enabled = !isClearing,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isClearing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("Clear Database Only")
            }
            
            Button(
                onClick = {
                    isClearing = true
                    message = "Clearing all data..."
                    (context as ComponentActivity).lifecycleScope.launch {
                        try {
                            clearAllData(context)
                            message = "All data cleared successfully!"
                        } catch (e: Exception) {
                            message = "Error clearing data: ${e.message}"
                            Log.e("ClearDataStore", "Error clearing all data", e)
                        } finally {
                            isClearing = false
                        }
                    }
                },
                enabled = !isClearing,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                if (isClearing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colorScheme.onError
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("Clear All Data (Database + Settings)")
            }
            
            Button(
                onClick = {
                    isClearing = true
                    message = "Migrating data..."
                    (context as ComponentActivity).lifecycleScope.launch {
                        try {
                            migrateData(context)
                            message = "Data migration completed!"
                        } catch (e: Exception) {
                            message = "Error migrating data: ${e.message}"
                            Log.e("ClearDataStore", "Error migrating data", e)
                        } finally {
                            isClearing = false
                        }
                    }
                },
                enabled = !isClearing,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                if (isClearing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("Migrate Settings to Database")
            }
        }
    }
}

private suspend fun clearDatabase(context: Context) {
    AppDatabase.closeDatabase()
    val database = AppDatabase.getDatabase(context)
    database.clearAllTables()
    Log.d("ClearDataStore", "Database cleared")
}

private suspend fun clearAllData(context: Context) {
    // Clear database
    clearDatabase(context)
    
    // Clear settings
    val settingsManager = SettingsManager(context)
    settingsManager.clearEverything()
    
    Log.d("ClearDataStore", "All data cleared")
}

private suspend fun migrateData(context: Context) {
    val repository = TheDailyRepository.getInstance(context)
    repository.migrateDataFromSettings()
    Log.d("ClearDataStore", "Data migration completed")
}