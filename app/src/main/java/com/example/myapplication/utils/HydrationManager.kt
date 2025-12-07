package com.example.myapplication.utils

import android.content.Context
import com.example.myapplication.data.HydrationEntry
import com.example.myapplication.data.UserSettings
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class HydrationManager(private val context: Context) {
    
    // Initialize SharedPreferences for persistent hydration data storage
    // SharedPreferences provides simple key-value storage for app data
    // Context.MODE_PRIVATE ensures only this app can access the data
    private val prefs = context.getSharedPreferences("wellness_tracker", Context.MODE_PRIVATE)
    
    // Initialize Gson for JSON serialization/deserialization of hydration data
    // This allows us to store complex objects as JSON strings in SharedPreferences
    private val gson = Gson()
    
    // Keys used to store/retrieve data from SharedPreferences
    private val hydrationKey = "hydration_entries"  // For water intake history
    private val settingsKey = "user_settings"       // For user preferences
    
    fun addHydrationEntry(entry: HydrationEntry) {
        val entries = getAllHydrationEntries().toMutableList()
        entries.add(entry)
        saveHydrationEntries(entries)
    }
    
    fun getAllHydrationEntries(): List<HydrationEntry> {
        // Retrieve hydration entries JSON string from SharedPreferences
        // Returns null if the key doesn't exist (first app launch)
        val entriesJson = prefs.getString(hydrationKey, null)
        
        return if (entriesJson != null) {
            // Convert JSON string back to List<HydrationEntry> using Gson
            // TypeToken is needed for generic type information at runtime
            val type = object : TypeToken<List<HydrationEntry>>() {}.type
            gson.fromJson(entriesJson, type) ?: emptyList()
        } else {
            // First time user - return empty list (no hydration history yet)
            emptyList()
        }
    }
    
    fun getTodayHydrationEntries(): List<HydrationEntry> {
        val allEntries = getAllHydrationEntries()
        val today = System.currentTimeMillis()
        val startOfDay = today - (today % (24 * 60 * 60 * 1000))
        val endOfDay = startOfDay + (24 * 60 * 60 * 1000)
        
        return allEntries.filter { it.timestamp in startOfDay until endOfDay }
    }
    
    fun getTodayTotalGlasses(): Int {
        return getTodayHydrationEntries().sumOf { it.amount }
    }
    
    fun getUserSettings(): UserSettings {
        // Retrieve user settings JSON string from SharedPreferences
        val settingsJson = prefs.getString(settingsKey, null)
        
        return if (settingsJson != null) {
            // Convert JSON string back to UserSettings object using Gson
            gson.fromJson(settingsJson, UserSettings::class.java)
        } else {
            // First time user - return default settings
            UserSettings()
        }
    }
    
    fun saveUserSettings(settings: UserSettings) {
        // Convert UserSettings object to JSON string for storage
        val settingsJson = gson.toJson(settings)
        
        // Save JSON string to SharedPreferences using the editor
        // apply() saves asynchronously (faster than commit())
        prefs.edit().putString(settingsKey, settingsJson).apply()
    }
    
    private fun saveHydrationEntries(entries: List<HydrationEntry>) {
        // Convert List<HydrationEntry> to JSON string for storage in SharedPreferences
        val entriesJson = gson.toJson(entries)
        
        // Save JSON string to SharedPreferences using the editor
        // apply() saves asynchronously (faster than commit())
        prefs.edit().putString(hydrationKey, entriesJson).apply()
    }
    
    fun resetAllHydrationData() {
        // Remove all hydration entries from SharedPreferences
        // This clears the entire hydration history
        prefs.edit()
            .remove(hydrationKey)
            .apply()
    }
    
    fun resetTodayHydrationData() {
        val allEntries = getAllHydrationEntries()
        val today = System.currentTimeMillis()
        val startOfDay = today - (today % (24 * 60 * 60 * 1000))
        val endOfDay = startOfDay + (24 * 60 * 60 * 1000)
        
        // Keep only entries from before today
        val filteredEntries = allEntries.filter { it.timestamp !in startOfDay until endOfDay }
        saveHydrationEntries(filteredEntries)
        
        // Clear any cached today's data from SharedPreferences
        // This removes temporary cached data to ensure fresh calculations
        val prefs = context.getSharedPreferences("wellness_tracker", Context.MODE_PRIVATE)
        prefs.edit().remove("today_glasses_cache").apply()
    }
}

