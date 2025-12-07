package com.example.myapplication.utils

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import com.example.myapplication.R
import com.example.myapplication.data.Habit
import com.example.myapplication.widgets.WellnessWidgetProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class HabitManager(private val context: Context) {
    
    // Initialize SharedPreferences for persistent habit data storage
    // SharedPreferences provides simple key-value storage for app data
    // Context.MODE_PRIVATE ensures only this app can access the data
    private val prefs = context.getSharedPreferences("wellness_tracker", Context.MODE_PRIVATE)
    
    // Initialize Gson for JSON serialization/deserialization of Habit objects
    // This allows us to store complex objects as JSON strings in SharedPreferences
    private val gson = Gson()
    
    // Key used to store/retrieve habits list from SharedPreferences
    private val habitsKey = "habits"
    
    fun addHabit(habit: Habit) {
        val habits = getAllHabits().toMutableList()
        habits.add(habit)
        saveHabits(habits)
    }
    
    fun updateHabit(habit: Habit) {
        val habits = getAllHabits().toMutableList()
        val index = habits.indexOfFirst { it.id == habit.id }
        if (index != -1) {
            habits[index] = habit
            saveHabits(habits)
        }
    }
    
    fun deleteHabit(habitId: Long) {
        val habits = getAllHabits().toMutableList()
        habits.removeAll { it.id == habitId }
        saveHabits(habits)
    }
    
    fun getAllHabits(): List<Habit> {
        // Retrieve habits JSON string from SharedPreferences
        // Returns null if the key doesn't exist (first app launch)
        val habitsJson = prefs.getString(habitsKey, null)
        
        return if (habitsJson != null) {
            // Convert JSON string back to List<Habit> using Gson
            // TypeToken is needed for generic type information at runtime
            val type = object : TypeToken<List<Habit>>() {}.type
            gson.fromJson(habitsJson, type) ?: emptyList()
        } else {
            // First time user - return default habits and save them to SharedPreferences
            getDefaultHabits()
        }
    }
    
    private fun saveHabits(habits: List<Habit>) {
        // Convert List<Habit> to JSON string for storage in SharedPreferences
        val habitsJson = gson.toJson(habits)
        
        // Save JSON string to SharedPreferences using the editor
        // apply() saves asynchronously (faster than commit())
        prefs.edit().putString(habitsKey, habitsJson).apply()
        
        // Update home screen widget to reflect habit changes
        updateWidget()
    }
    
    private fun updateWidget() {
        try {
            WellnessWidgetProvider.updateAllWidgets(context)
        } catch (e: Exception) {
            android.util.Log.e("HabitManager", "Error updating widget: ${e.message}")
        }
    }
    
    private fun getDefaultHabits(): List<Habit> {
        val defaultHabits = listOf(
            Habit(
                id = 1,
                name = "Drink Water",
                description = "8 glasses daily",
                frequency = "Daily",
                hasReminder = true,
                isCompletedToday = false
            ),
            Habit(
                id = 2,
                name = "Exercise",
                description = "30 minutes daily",
                frequency = "Daily",
                hasReminder = false,
                isCompletedToday = false
            ),
            Habit(
                id = 3,
                name = "Meditation",
                description = "10 minutes daily",
                frequency = "Daily",
                hasReminder = true,
                isCompletedToday = false
            )
        )
        saveHabits(defaultHabits)
        return defaultHabits
    }
}

