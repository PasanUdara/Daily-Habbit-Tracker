package com.example.myapplication.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.adapters.HabitsAdapter
import com.example.myapplication.data.Habit
import com.example.myapplication.utils.HabitManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.widget.ProgressBar

class HabitsFragment : Fragment() {
    
    private lateinit var progressBar: ProgressBar
    private lateinit var progressText: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAddHabit: FloatingActionButton
    private lateinit var emptyState: LinearLayout
    
    private lateinit var habitsAdapter: HabitsAdapter
    private lateinit var habitManager: HabitManager
    private var habitsList = mutableListOf<Habit>()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_habits, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        initializeViews(view)
        setupRecyclerView()
        setupClickListeners()
        loadHabits()
        updateProgress()
    }
    
    private fun initializeViews(view: View) {
        progressBar = view.findViewById(R.id.progress_bar)
        progressText = view.findViewById(R.id.progress_text)
        recyclerView = view.findViewById(R.id.recycler_habits)
        fabAddHabit = view.findViewById(R.id.fab_add_habit)
        emptyState = view.findViewById(R.id.empty_state)
        
        habitManager = HabitManager(requireContext())
    }
    
    private fun setupRecyclerView() {
        habitsAdapter = HabitsAdapter(habitsList) { habit, action ->
            when (action) {
                "check" -> setHabitChecked(habit, true)
                "uncheck" -> setHabitChecked(habit, false)
                "edit" -> showEditHabitDialog(habit)
                "delete" -> showDeleteConfirmation(habit)
            }
        }
        
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = habitsAdapter
    }
    
    private fun setupClickListeners() {
        fabAddHabit.setOnClickListener {
            showAddHabitDialog()
        }
    }
    
    private fun loadHabits() {
        habitsList.clear()
        habitsList.addAll(habitManager.getAllHabits())
        habitsAdapter.notifyDataSetChanged()
        
        // Show/hide empty state
        emptyState.visibility = if (habitsList.isEmpty()) View.VISIBLE else View.GONE
        recyclerView.visibility = if (habitsList.isEmpty()) View.GONE else View.VISIBLE
    }
    
    private fun updateProgress() {
        val completedHabits = habitsList.count { it.isCompletedToday }
        val totalHabits = habitsList.size
        
        if (totalHabits > 0) {
            val progress = (completedHabits * 100) / totalHabits
            progressBar.progress = progress
            progressText.text = "$completedHabits/$totalHabits"
        } else {
            progressBar.progress = 0
            progressText.text = "0/0"
        }
    }
    
    private fun setHabitChecked(habit: Habit, checked: Boolean) {
        // Update model immutably and persist
        habit.isCompletedToday = checked
        habitManager.updateHabit(habit)
        habitsAdapter.notifyDataSetChanged()
        updateProgress()
        
        // Update widget immediately when habit is toggled
        updateWidget()
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
            android.util.Log.e("HabitsFragment", "Error updating widget: ${e.message}")
        }
    }
    
    private fun showAddHabitDialog() {
        showHabitDialog(null)
    }
    
    private fun showEditHabitDialog(habit: Habit) {
        showHabitDialog(habit)
    }
    
    private fun showHabitDialog(habit: Habit?) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_add_habit, null)
        
        val etName = dialogView.findViewById<EditText>(R.id.et_habit_name)
        val etDescription = dialogView.findViewById<EditText>(R.id.et_habit_description)
        val chipGroup = dialogView.findViewById<com.google.android.material.chip.ChipGroup>(R.id.chip_group_frequency)
        val switchReminder = dialogView.findViewById<com.google.android.material.switchmaterial.SwitchMaterial>(R.id.switch_reminder)
        val btnSave = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_save)
        val btnCancel = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_cancel)
        
        // Pre-fill if editing
        habit?.let {
            etName.setText(it.name)
            etDescription.setText(it.description)
            switchReminder.isChecked = it.hasReminder
        }
        
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()
        
        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val description = etDescription.text.toString().trim()
            val frequency = when (chipGroup.checkedChipId) {
                R.id.chip_daily -> "Daily"
                R.id.chip_weekly -> "Weekly"
                R.id.chip_monthly -> "Monthly"
                else -> "Daily"
            }
            val hasReminder = switchReminder.isChecked
            
            if (name.isNotEmpty()) {
                if (habit == null) {
                    // Add new habit
                    val newHabit = Habit(
                        id = System.currentTimeMillis(),
                        name = name,
                        description = description,
                        frequency = frequency,
                        hasReminder = hasReminder,
                        isCompletedToday = false
                    )
                    habitManager.addHabit(newHabit)
                } else {
                    // Update existing habit
                    habit.name = name
                    habit.description = description
                    habit.frequency = frequency
                    habit.hasReminder = hasReminder
                    habitManager.updateHabit(habit)
                }
                
                loadHabits()
                updateProgress()
                dialog.dismiss()
            } else {
                etName.error = "Habit name is required"
            }
        }
        
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        
        dialog.show()
    }
    
    private fun showDeleteConfirmation(habit: Habit) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Habit")
            .setMessage("Are you sure you want to delete '${habit.name}'?")
            .setPositiveButton("Delete") { _, _ ->
                habitManager.deleteHabit(habit.id)
                loadHabits()
                updateProgress()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}

