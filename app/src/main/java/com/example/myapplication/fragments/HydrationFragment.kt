package com.example.myapplication.fragments

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.adapters.IntakeAdapter
import com.example.myapplication.data.HydrationEntry
import com.example.myapplication.data.UserSettings
import com.example.myapplication.receivers.HydrationReminderReceiver
import com.example.myapplication.utils.HydrationManager
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.slider.Slider
import com.google.android.material.switchmaterial.SwitchMaterial
import java.text.SimpleDateFormat
import java.util.*

class HydrationFragment : Fragment() {
    
    private lateinit var progressCircle: CircularProgressIndicator
    private lateinit var tvProgressPercentage: TextView
    private lateinit var tvGlassesDrunk: TextView
    private lateinit var btnAddGlass: MaterialButton
    private lateinit var btnAddBottle: MaterialButton
    private lateinit var sliderGoal: Slider
    private lateinit var tvGoalValue: TextView
    private lateinit var switchReminders: SwitchMaterial
    private lateinit var reminderSettingsContainer: LinearLayout
    private lateinit var chipGroupInterval: ChipGroup
    private lateinit var customTimeContainer: LinearLayout
    private lateinit var btnSetCustomTime: MaterialButton
    private lateinit var btnResetHydration: MaterialButton
    private lateinit var recyclerIntakeHistory: RecyclerView
    private lateinit var emptyIntakeState: LinearLayout
    private lateinit var btnViewAllIntakeEntries: TextView
    private lateinit var intakeAdapter: IntakeAdapter
    
    private lateinit var hydrationManager: HydrationManager
    private var userSettings: UserSettings = UserSettings()
    private var todayGlasses = 0
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_hydration, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        initializeViews(view)
        setupRecyclerView()
        setupClickListeners()
        loadUserSettings()
        updateUI()
    }
    
    private fun initializeViews(view: View) {
        progressCircle = view.findViewById(R.id.progress_circle)
        tvProgressPercentage = view.findViewById(R.id.tv_progress_percentage)
        tvGlassesDrunk = view.findViewById(R.id.tv_glasses_drunk)
        btnAddGlass = view.findViewById(R.id.btn_add_glass)
        btnAddBottle = view.findViewById(R.id.btn_add_bottle)
        sliderGoal = view.findViewById(R.id.slider_goal)
        tvGoalValue = view.findViewById(R.id.tv_goal_value)
        switchReminders = view.findViewById(R.id.switch_reminders)
        reminderSettingsContainer = view.findViewById(R.id.reminder_settings_container)
        chipGroupInterval = view.findViewById(R.id.chip_group_interval)
        customTimeContainer = view.findViewById(R.id.custom_time_container)
        btnSetCustomTime = view.findViewById(R.id.btn_set_custom_time)
        btnResetHydration = view.findViewById(R.id.btn_reset_hydration)
        recyclerIntakeHistory = view.findViewById(R.id.recycler_intake_history)
        emptyIntakeState = view.findViewById(R.id.empty_intake_state)
        btnViewAllIntakeEntries = view.findViewById(R.id.btn_view_all_intake_entries)
        
        hydrationManager = HydrationManager(requireContext())
    }
    
    private fun setupRecyclerView() {
        intakeAdapter = IntakeAdapter(emptyList()) { entry ->
            showDeleteIntakeDialog(entry)
        }
        
        recyclerIntakeHistory.layoutManager = LinearLayoutManager(requireContext())
        recyclerIntakeHistory.adapter = intakeAdapter
    }
    
    private fun setupClickListeners() {
        btnAddGlass.setOnClickListener {
            addHydrationEntry(1)
        }
        
        btnAddBottle.setOnClickListener {
            addHydrationEntry(2) // Assuming bottle = 2 glasses
        }
        
        sliderGoal.addOnChangeListener { _, value, _ ->
            userSettings.dailyWaterGoal = value.toInt()
            updateGoalDisplay()
        }
        
        switchReminders.setOnCheckedChangeListener { _, isChecked ->
            userSettings.remindersEnabled = isChecked
            reminderSettingsContainer.visibility = if (isChecked) View.VISIBLE else View.GONE
            if (isChecked) {
                setupReminders()
            } else {
                cancelReminders()
            }
        }
        
        chipGroupInterval.setOnCheckedStateChangeListener { _, checkedIds ->
            val selectedChip = view?.findViewById<Chip>(checkedIds.firstOrNull() ?: -1)
            when (selectedChip?.id) {
                R.id.chip_1_hour -> {
                    userSettings.reminderInterval = 1
                    customTimeContainer.visibility = View.GONE
                }
                R.id.chip_2_hours -> {
                    userSettings.reminderInterval = 2
                    customTimeContainer.visibility = View.GONE
                }
                R.id.chip_3_hours -> {
                    userSettings.reminderInterval = 3
                    customTimeContainer.visibility = View.GONE
                }
            }
            if (userSettings.remindersEnabled) {
                setupReminders()
            }
        }
        
        btnSetCustomTime.setOnClickListener {
            showTimePickerDialog()
        }
        
        btnResetHydration.setOnClickListener {
            confirmResetTodayData()
        }
        
        btnViewAllIntakeEntries.setOnClickListener {
            showAllIntakeDialog()
        }
        
        // Add long press on the progress circle to show reset options
        progressCircle.setOnLongClickListener {
            showResetOptionsDialog()
            true
        }
    }
    
    private fun loadUserSettings() {
        userSettings = hydrationManager.getUserSettings()
        todayGlasses = hydrationManager.getTodayTotalGlasses()
        
        // Update UI with loaded settings
        sliderGoal.value = userSettings.dailyWaterGoal.toFloat()
        switchReminders.isChecked = userSettings.remindersEnabled
        reminderSettingsContainer.visibility = if (userSettings.remindersEnabled) View.VISIBLE else View.GONE
        
        // Set selected interval chip
        when (userSettings.reminderInterval) {
            1 -> chipGroupInterval.check(R.id.chip_1_hour)
            2 -> chipGroupInterval.check(R.id.chip_2_hours)
            3 -> chipGroupInterval.check(R.id.chip_3_hours)
        }
    }
    
    private fun updateUI() {
        updateProgress()
        updateGoalDisplay()
        updateIntakeHistory()
    }
    
    private fun updateProgress() {
        // Ensure todayGlasses is properly loaded from storage
        todayGlasses = hydrationManager.getTodayTotalGlasses()
        val goal = userSettings.dailyWaterGoal
        val percentage = if (goal > 0) (todayGlasses * 100) / goal else 0
        
        // Cap percentage at 100% to prevent display issues
        val displayPercentage = minOf(percentage, 100)
        
        progressCircle.progress = displayPercentage
        tvProgressPercentage.text = "$displayPercentage%"
        tvGlassesDrunk.text = "$todayGlasses / $goal glasses"
    }
    
    private fun updateGoalDisplay() {
        tvGoalValue.text = "${userSettings.dailyWaterGoal} glasses per day"
    }
    
    private fun updateIntakeHistory() {
        val todayEntries = hydrationManager.getTodayHydrationEntries().sortedByDescending { it.timestamp }
        
        if (todayEntries.isEmpty()) {
            recyclerIntakeHistory.visibility = View.GONE
            emptyIntakeState.visibility = View.VISIBLE
            btnViewAllIntakeEntries.visibility = View.GONE
        } else {
            recyclerIntakeHistory.visibility = View.VISIBLE
            emptyIntakeState.visibility = View.GONE
            btnViewAllIntakeEntries.visibility = View.VISIBLE
            intakeAdapter.updateEntries(todayEntries)
        }
    }
    
    private fun addHydrationEntry(amount: Int) {
        val entry = HydrationEntry(
            id = System.currentTimeMillis(),
            amount = amount
        )
        
        hydrationManager.addHydrationEntry(entry)
        todayGlasses += amount
        updateProgress()
        updateIntakeHistory()
        
        // Update widget when hydration changes
        updateWidget()
        
        Toast.makeText(requireContext(), "Added $amount glass(es) of water!", Toast.LENGTH_SHORT).show()
    }
    
    private fun updateWidget() {
        try {
            val appWidgetManager = android.appwidget.AppWidgetManager.getInstance(requireContext())
            val appWidgetIds = appWidgetManager.getAppWidgetIds(
                android.content.ComponentName(requireContext(), com.example.myapplication.widgets.WellnessWidgetProvider::class.java)
            )
            for (appWidgetId in appWidgetIds) {
                com.example.myapplication.widgets.WellnessWidgetProvider.updateAppWidget(
                    requireContext(), appWidgetManager, appWidgetId
                )
            }
        } catch (e: Exception) {
            android.util.Log.e("HydrationFragment", "Error updating widget: ${e.message}")
        }
    }
    
    private fun setupReminders() {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), HydrationReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val intervalMillis = userSettings.reminderInterval * 60 * 60 * 1000L // Convert hours to milliseconds
        
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + intervalMillis,
            intervalMillis,
            pendingIntent
        )
    }
    
    private fun cancelReminders() {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), HydrationReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        alarmManager.cancel(pendingIntent)
    }
    
    private fun showTimePickerDialog() {
        // TODO: Implement time picker dialog for custom reminder times
        Toast.makeText(requireContext(), "Custom time picker coming soon!", Toast.LENGTH_SHORT).show()
    }
    
    private fun showResetOptionsDialog() {
        val options = arrayOf("Reset Today's Data", "Reset All Data", "Cancel")
        
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Reset Hydration Data")
            .setMessage("Choose what you want to reset:")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> confirmResetTodayData()
                    1 -> confirmResetAllData()
                    2 -> { /* Cancel - do nothing */ }
                }
            }
            .show()
    }
    
    private fun confirmResetTodayData() {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Reset Today's Data")
            .setMessage("Are you sure you want to reset today's hydration data? This will clear all glasses consumed today.")
            .setPositiveButton("Reset") { _, _ ->
                hydrationManager.resetTodayHydrationData()
                todayGlasses = 0  // Reset the counter immediately
                loadUserSettings()
                updateProgress()
                updateIntakeHistory()
                updateWidget()  // Update widget after reset
                Toast.makeText(requireContext(), "Today's data reset successfully!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun confirmResetAllData() {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Reset All Data")
            .setMessage("Are you sure you want to reset ALL hydration data? This will permanently delete all your hydration history.")
            .setPositiveButton("Reset All") { _, _ ->
                hydrationManager.resetAllHydrationData()
                todayGlasses = 0  // Reset the counter immediately
                loadUserSettings()
                updateProgress()
                updateIntakeHistory()
                updateWidget()  // Update widget after reset
                Toast.makeText(requireContext(), "All data reset successfully!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun showDeleteIntakeDialog(entry: HydrationEntry) {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Delete Intake Entry")
            .setMessage("Are you sure you want to delete this water intake entry?")
            .setPositiveButton("Delete") { _, _ ->
                deleteIntakeEntry(entry)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun deleteIntakeEntry(entry: HydrationEntry) {
        // Remove the entry from storage
        val allEntries = hydrationManager.getAllHydrationEntries().toMutableList()
        allEntries.removeAll { it.id == entry.id }
        
        // Save updated entries to SharedPreferences
        // This removes the deleted entry from persistent storage
        val prefs = requireContext().getSharedPreferences("wellness_tracker", Context.MODE_PRIVATE)
        val gson = Gson()
        val entriesJson = gson.toJson(allEntries)
        
        // Save the modified hydration entries list back to SharedPreferences
        // apply() saves asynchronously for better performance
        prefs.edit().putString("hydration_entries", entriesJson).apply()
        
        // Update UI
        loadUserSettings()
        updateProgress()
        updateIntakeHistory()
        updateWidget()
        
        Toast.makeText(requireContext(), "Intake entry deleted", Toast.LENGTH_SHORT).show()
    }
    
    private fun showAllIntakeDialog() {
        val todayEntries = hydrationManager.getTodayHydrationEntries()
        val entriesText = if (todayEntries.isEmpty()) {
            "No hydration entries for today."
        } else {
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            todayEntries.mapIndexed { index, entry ->
                "${index + 1}. ${entry.amount} glass(es) at ${timeFormat.format(Date(entry.timestamp))}"
            }.joinToString("\n")
        }
        
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Today's Hydration History")
            .setMessage(entriesText)
            .setPositiveButton("OK", null)
            .show()
    }
    
    override fun onPause() {
        super.onPause()
        // Save settings when fragment is paused
        hydrationManager.saveUserSettings(userSettings)
    }
}

