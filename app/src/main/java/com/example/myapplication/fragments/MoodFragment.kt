package com.example.myapplication.fragments

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
import com.example.myapplication.adapters.MoodAdapter
import com.example.myapplication.data.MoodEntry
import com.example.myapplication.utils.MoodManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*

class MoodFragment : Fragment() {
    
    private lateinit var moodButtons: List<MaterialButton>
    private lateinit var selectedMoodContainer: LinearLayout
    private lateinit var selectedMoodEmoji: TextView
    private lateinit var selectedMoodText: TextView
    private lateinit var etMoodNote: TextInputEditText
    private lateinit var btnSaveMood: MaterialButton
    private lateinit var btnCalendarView: MaterialButton
    private lateinit var btnListView: MaterialButton
    private lateinit var calendarCard: com.google.android.material.card.MaterialCardView
    private lateinit var calendarGrid: GridLayout
    private lateinit var tvMonthYear: TextView
    private lateinit var btnPrevMonth: ImageButton
    private lateinit var btnNextMonth: ImageButton
    private lateinit var recyclerMoodList: RecyclerView
    private lateinit var emptyMoodState: LinearLayout
    
    private lateinit var moodManager: MoodManager
    private lateinit var moodAdapter: MoodAdapter
    private var moodEntries = mutableListOf<MoodEntry>()
    private var selectedMoodType = ""
    private var selectedEmoji = ""
    private var isCalendarView = true
    private var currentCalendar = Calendar.getInstance()
    private val moodEmojis = mapOf(
        "Happy" to "😊",
        "Excited" to "🤩", 
        "Neutral" to "😐",
        "Sad" to "😢",
        "Angry" to "😠"
    )
    
    private val moodTypes = mapOf(
        "😠" to "Angry",
        "😢" to "Sad", 
        "😐" to "Neutral",
        "😊" to "Happy",
        "🤩" to "Excited"
    )
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mood, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        initializeViews(view)
        setupMoodButtons()
        setupRecyclerView()
        setupClickListeners()
        loadMoodEntries()
        updateView()
        updateCalendar()
    }
    
    private fun initializeViews(view: View) {
        moodButtons = listOf(
            view.findViewById(R.id.btn_mood_angry),
            view.findViewById(R.id.btn_mood_sad),
            view.findViewById(R.id.btn_mood_neutral),
            view.findViewById(R.id.btn_mood_happy),
            view.findViewById(R.id.btn_mood_excited)
        )
        
        selectedMoodContainer = view.findViewById(R.id.selected_mood_container)
        selectedMoodEmoji = view.findViewById(R.id.selected_mood_emoji)
        selectedMoodText = view.findViewById(R.id.selected_mood_text)
        etMoodNote = view.findViewById(R.id.et_mood_note)
        btnSaveMood = view.findViewById(R.id.btn_save_mood)
        btnCalendarView = view.findViewById(R.id.btn_calendar_view)
        btnListView = view.findViewById(R.id.btn_list_view)
        calendarCard = view.findViewById(R.id.calendar_card)
        calendarGrid = view.findViewById(R.id.calendar_grid)
        tvMonthYear = view.findViewById(R.id.tv_month_year)
        btnPrevMonth = view.findViewById(R.id.btn_prev_month)
        btnNextMonth = view.findViewById(R.id.btn_next_month)
        recyclerMoodList = view.findViewById(R.id.recycler_mood_list)
        emptyMoodState = view.findViewById(R.id.empty_mood_state)
        
        moodManager = MoodManager(requireContext())
    }
    
    private fun setupMoodButtons() {
        val emojis = listOf("😠", "😢", "😐", "😊", "🤩")
        
        moodButtons.forEachIndexed { index, button ->
            button.text = emojis[index]
            button.setOnClickListener {
                selectMood(emojis[index], moodTypes[emojis[index]] ?: "")
            }
        }
    }
    
    private fun setupRecyclerView() {
        moodAdapter = MoodAdapter(moodEntries, 
            onShareClick = { moodEntry -> shareMoodEntry(moodEntry) },
            onDeleteClick = { moodEntry -> showDeleteMoodDialog(moodEntry) }
        )
        
        recyclerMoodList.layoutManager = LinearLayoutManager(requireContext())
        recyclerMoodList.adapter = moodAdapter
    }
    
    private fun setupClickListeners() {
        btnSaveMood.setOnClickListener {
            saveMoodEntry()
        }
        
        btnCalendarView.setOnClickListener {
            switchToCalendarView()
        }
        
        btnListView.setOnClickListener {
            switchToListView()
        }
        
        btnPrevMonth.setOnClickListener {
            currentCalendar.add(Calendar.MONTH, -1)
            updateCalendar()
        }
        
        btnNextMonth.setOnClickListener {
            currentCalendar.add(Calendar.MONTH, 1)
            updateCalendar()
        }
    }
    
    private fun selectMood(emoji: String, moodType: String) {
        selectedEmoji = emoji
        selectedMoodType = moodType
        
        // Update UI
        selectedMoodEmoji.text = emoji
        selectedMoodText.text = moodType
        selectedMoodContainer.visibility = View.VISIBLE
        btnSaveMood.isEnabled = true
        
        // Update button states
        moodButtons.forEach { button ->
            button.isSelected = button.text == emoji
        }
    }
    
    private fun saveMoodEntry() {
        if (selectedMoodType.isNotEmpty()) {
            val note = etMoodNote.text.toString().trim()
            val moodEntry = MoodEntry(
                id = System.currentTimeMillis(),
                moodType = selectedMoodType,
                emoji = selectedEmoji,
                note = note
            )
            
            moodManager.addMoodEntry(moodEntry)
            loadMoodEntries()
            clearForm()
            updateCalendar() // Update calendar to show new mood entry
            
            Toast.makeText(requireContext(), "Mood saved!", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun clearForm() {
        selectedMoodType = ""
        selectedEmoji = ""
        selectedMoodContainer.visibility = View.GONE
        etMoodNote.text?.clear()
        btnSaveMood.isEnabled = false
        
        moodButtons.forEach { button ->
            button.isSelected = false
        }
    }
    
    private fun loadMoodEntries() {
        moodEntries.clear()
        moodEntries.addAll(moodManager.getAllMoodEntries().sortedByDescending { it.timestamp })
        moodAdapter.notifyDataSetChanged()
        
        // Show/hide empty state
        emptyMoodState.visibility = if (moodEntries.isEmpty()) View.VISIBLE else View.GONE
    }
    
    private fun switchToCalendarView() {
        isCalendarView = true
        calendarCard.visibility = View.VISIBLE
        recyclerMoodList.visibility = View.GONE
        btnCalendarView.isSelected = true
        btnListView.isSelected = false
        updateCalendar()
    }
    
    private fun switchToListView() {
        isCalendarView = false
        calendarCard.visibility = View.GONE
        recyclerMoodList.visibility = View.VISIBLE
        btnCalendarView.isSelected = false
        btnListView.isSelected = true
    }
    
    private fun updateView() {
        if (isCalendarView) {
            switchToCalendarView()
        } else {
            switchToListView()
        }
    }
    
    private fun shareMoodEntry(moodEntry: MoodEntry) {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", Locale.getDefault())
        val dateString = dateFormat.format(Date(moodEntry.timestamp))
        
        val shareText = "I'm feeling ${moodEntry.emoji} ${moodEntry.moodType} today! $dateString" +
                if (moodEntry.note.isNotEmpty()) "\n\nNote: ${moodEntry.note}" else ""
        
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        
        startActivity(Intent.createChooser(shareIntent, "Share your mood"))
    }
    
    private fun showDeleteMoodDialog(moodEntry: MoodEntry) {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Delete Mood Entry")
            .setMessage("Are you sure you want to delete this mood entry? This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                deleteMoodEntry(moodEntry)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun deleteMoodEntry(moodEntry: MoodEntry) {
        // Remove the mood entry from storage
        val allEntries = moodManager.getAllMoodEntries().toMutableList()
        allEntries.removeAll { it.id == moodEntry.id }
        
        // Save updated entries to SharedPreferences
        // This removes the deleted mood entry from persistent storage
        val prefs = requireContext().getSharedPreferences("wellness_tracker", android.content.Context.MODE_PRIVATE)
        val gson = com.google.gson.Gson()
        val entriesJson = gson.toJson(allEntries)
        
        // Save the modified mood entries list back to SharedPreferences
        // apply() saves asynchronously for better performance
        prefs.edit().putString("mood_entries", entriesJson).apply()
        
        // Update UI
        loadMoodEntries()
        updateCalendar() // Update calendar to remove deleted mood entry
        
        Toast.makeText(requireContext(), "Mood entry deleted", Toast.LENGTH_SHORT).show()
    }
    
    private fun updateCalendar() {
        // Update month/year display
        val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        tvMonthYear.text = monthYearFormat.format(currentCalendar.time)
        
        // Clear existing calendar cells
        calendarGrid.removeAllViews()
        
        // Add day headers (Sun, Mon, Tue, etc.)
        val dayHeaders = arrayOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        for (day in dayHeaders) {
            val headerView = createCalendarHeaderView(day)
            calendarGrid.addView(headerView)
        }
        
        // Get calendar data
        val calendar = currentCalendar.clone() as Calendar
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        
        // Add empty cells for days before the first day of the month
        for (i in 1 until firstDayOfWeek) {
            val emptyView = createEmptyCalendarCell()
            calendarGrid.addView(emptyView)
        }
        
        // Add days of the month
        for (day in 1..daysInMonth) {
            val dayView = createCalendarDayView(day)
            calendarGrid.addView(dayView)
        }
    }
    
    private fun createCalendarHeaderView(day: String): TextView {
        return TextView(requireContext()).apply {
            text = day
            textSize = 12f
            setTextColor(resources.getColor(R.color.text_secondary, null))
            gravity = android.view.Gravity.CENTER
            layoutParams = GridLayout.LayoutParams().apply {
                width = 0
                height = 40
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                setMargins(2, 2, 2, 2)
            }
        }
    }
    
    private fun createEmptyCalendarCell(): View {
        return View(requireContext()).apply {
            layoutParams = GridLayout.LayoutParams().apply {
                width = 0
                height = 60
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                setMargins(2, 2, 2, 2)
            }
        }
    }
    
    private fun createCalendarDayView(day: Int): TextView {
        val dayView = TextView(requireContext()).apply {
            text = day.toString()
            textSize = 14f
            gravity = android.view.Gravity.CENTER
            layoutParams = GridLayout.LayoutParams().apply {
                width = 0
                height = 60
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                setMargins(2, 2, 2, 2)
            }
        }
        
        // Check if there's a mood entry for this day
        val calendar = currentCalendar.clone() as Calendar
        calendar.set(Calendar.DAY_OF_MONTH, day)
        val moodForDay = getMoodForDate(calendar.timeInMillis)
        val isFuture = isFutureDate(calendar.timeInMillis)
        
        if (moodForDay != null) {
            // Show mood emoji
            dayView.text = "${day}\n${moodEmojis[moodForDay.moodType] ?: "😐"}"
            dayView.setTextColor(resources.getColor(R.color.primary_blue, null))
            dayView.background = resources.getDrawable(R.drawable.mood_emoji_background, null)
            dayView.textSize = 10f
        } else if (isFuture) {
            // Future date - show as disabled
            dayView.text = day.toString()
            dayView.setTextColor(resources.getColor(R.color.text_secondary, null))
            dayView.alpha = 0.5f
            dayView.background = null
        } else {
            // Regular day (past or today)
            dayView.text = day.toString()
            dayView.setTextColor(resources.getColor(R.color.text_primary, null))
            dayView.alpha = 1.0f
            dayView.background = null
        }
        
        // Add click listener
        dayView.setOnClickListener {
            onCalendarDayClick(day, calendar.timeInMillis)
        }
        
        return dayView
    }
    
    private fun getMoodForDate(date: Long): MoodEntry? {
        val startOfDay = date - (date % (24 * 60 * 60 * 1000))
        val endOfDay = startOfDay + (24 * 60 * 60 * 1000)
        
        return moodEntries.find { 
            it.timestamp in startOfDay until endOfDay 
        }
    }
    
    private fun onCalendarDayClick(day: Int, date: Long) {
        val existingMood = getMoodForDate(date)
        
        if (existingMood != null) {
            // Show existing mood entry
            showMoodEntryDialog(existingMood)
        } else {
            // Check if the date is in the future
            if (isFutureDate(date)) {
                // Show message that future dates are not allowed
                showFutureDateMessage(date)
            } else {
                // Show add mood dialog for this date (past or today)
                showAddMoodDialog(date)
            }
        }
    }
    
    private fun showMoodEntryDialog(moodEntry: MoodEntry) {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val date = dateFormat.format(Date(moodEntry.timestamp))
        
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Mood Entry - $date")
            .setMessage("Mood: ${moodEntry.moodType} ${moodEmojis[moodEntry.moodType]}\n\nNote: ${moodEntry.note}")
            .setPositiveButton("Edit") { _, _ ->
                // TODO: Implement edit functionality
                Toast.makeText(requireContext(), "Edit functionality coming soon!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Delete") { _, _ ->
                showDeleteMoodDialog(moodEntry)
            }
            .setNeutralButton("Close", null)
            .show()
    }
    
    private fun isFutureDate(date: Long): Boolean {
        val today = Calendar.getInstance()
        today.set(Calendar.HOUR_OF_DAY, 23)
        today.set(Calendar.MINUTE, 59)
        today.set(Calendar.SECOND, 59)
        today.set(Calendar.MILLISECOND, 999)
        
        val selectedDate = Calendar.getInstance()
        selectedDate.timeInMillis = date
        
        return selectedDate.after(today)
    }
    
    private fun showFutureDateMessage(date: Long) {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val dateStr = dateFormat.format(Date(date))
        
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Cannot Add Mood")
            .setMessage("You cannot add mood entries for future dates.\n\nSelected date: $dateStr\n\nMood entries can only be added for today and past dates.")
            .setPositiveButton("OK", null)
            .show()
    }
    
    private fun showAddMoodDialog(date: Long) {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val dateStr = dateFormat.format(Date(date))
        
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Add Mood for $dateStr")
            .setMessage("Would you like to add a mood entry for this date?")
            .setPositiveButton("Add Mood") { _, _ ->
                // Set the date and show mood selection
                // For now, just show a message
                Toast.makeText(requireContext(), "Mood entry for $dateStr - coming soon!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}

