package com.example.myapplication.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.utils.HabitManager

class WellnessWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        // Widget is enabled - perform any setup needed
        android.util.Log.d("WellnessWidget", "Widget enabled")
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        // Widget is disabled - perform cleanup if needed
        android.util.Log.d("WellnessWidget", "Widget disabled")
    }

    companion object {
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            try {
                // Get habit data
                val habitManager = HabitManager(context)
                val habits = habitManager.getAllHabits()
                val completedHabits = habits.count { it.isCompletedToday }
                val totalHabits = habits.size
                
                // Calculate progress percentage
                val progressPercentage = if (totalHabits > 0) {
                    (completedHabits * 100) / totalHabits
                } else {
                    0
                }

                // Create RemoteViews
                val views = RemoteViews(context.packageName, R.layout.widget_wellness_simple)
                
                // Update text
                views.setTextViewText(R.id.widget_title, "Wellness Tracker")
                views.setTextViewText(R.id.widget_progress_text, "$completedHabits/$totalHabits")
                views.setTextViewText(R.id.widget_percentage, "$progressPercentage%")
                
                // Update progress bar
                views.setProgressBar(R.id.widget_progress_bar, 100, progressPercentage, false)
                
                // Set click intent to open app
                val intent = Intent(context, MainActivity::class.java)
                val pendingIntent = PendingIntent.getActivity(
                    context, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                views.setOnClickPendingIntent(R.id.widget_container, pendingIntent)
                
                // Note: Refresh button removed from simple layout for better compatibility
                
                // Update the widget
                appWidgetManager.updateAppWidget(appWidgetId, views)
                
            } catch (e: Exception) {
                // Handle any errors gracefully
                android.util.Log.e("WellnessWidget", "Error updating widget: ${e.message}")
            }
        }
        
        fun updateAllWidgets(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(
                ComponentName(context, WellnessWidgetProvider::class.java)
            )
            
            for (appWidgetId in appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId)
            }
        }
    }
}
