package com.example.myapplication.utils

import android.content.Context
import com.example.myapplication.data.MoodEntry
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MoodManager(private val context: Context) {
    
    // Initialize SharedPreferences for persistent mood data storage
    // SharedPreferences provides simple key-value storage for app data
    // Context.MODE_PRIVATE ensures only this app can access the data
    private val prefs = context.getSharedPreferences("wellness_tracker", Context.MODE_PRIVATE)
    
    // Initialize Gson for JSON serialization/deserialization of mood data
    // This allows us to store complex objects as JSON strings in SharedPreferences
    private val gson = Gson()
    
    // Key used to store/retrieve mood entries from SharedPreferences
    private val moodsKey = "mood_entries"
    
    fun addMoodEntry(moodEntry: MoodEntry) {
        val moods = getAllMoodEntries().toMutableList()
        moods.add(moodEntry)
        saveMoodEntries(moods)
    }
    
    fun getAllMoodEntries(): List<MoodEntry> {
        // Retrieve mood entries JSON string from SharedPreferences
        // Returns null if the key doesn't exist (first app launch)
        val moodsJson = prefs.getString(moodsKey, null)
        
        return if (moodsJson != null) {
            // Convert JSON string back to List<MoodEntry> using Gson
            // TypeToken is needed for generic type information at runtime
            val type = object : TypeToken<List<MoodEntry>>() {}.type
            gson.fromJson(moodsJson, type) ?: emptyList()
        } else {
            // First time user - return empty list (no mood history yet)
            emptyList()
        }
    }
    
    fun getMoodEntriesForDate(date: Long): List<MoodEntry> {
        val allMoods = getAllMoodEntries()
        val startOfDay = date - (date % (24 * 60 * 60 * 1000))
        val endOfDay = startOfDay + (24 * 60 * 60 * 1000)
        
        return allMoods.filter { it.timestamp in startOfDay until endOfDay }
    }
    
    private fun saveMoodEntries(moods: List<MoodEntry>) {
        // Convert List<MoodEntry> to JSON string for storage in SharedPreferences
        val moodsJson = gson.toJson(moods)
        
        // Save JSON string to SharedPreferences using the editor
        // apply() saves asynchronously (faster than commit())
        prefs.edit().putString(moodsKey, moodsJson).apply()
    }
}

