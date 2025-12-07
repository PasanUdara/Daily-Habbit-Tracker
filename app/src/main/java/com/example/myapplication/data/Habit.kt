package com.example.myapplication.data

import java.io.Serializable

data class Habit(
    val id: Long,
    var name: String,
    var description: String,
    var frequency: String,
    var hasReminder: Boolean,
    var isCompletedToday: Boolean,
    val createdAt: Long = System.currentTimeMillis()
) : Serializable

data class MoodEntry(
    val id: Long,
    val moodType: String,
    val emoji: String,
    val note: String,
    val timestamp: Long = System.currentTimeMillis()
) : Serializable

data class HydrationEntry(
    val id: Long,
    val amount: Int, // in glasses
    val timestamp: Long = System.currentTimeMillis()
) : Serializable

data class UserSettings(
    var dailyWaterGoal: Int = 8,
    var reminderInterval: Int = 2, // hours
    var remindersEnabled: Boolean = true,
    var theme: String = "light"
) : Serializable

