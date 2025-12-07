package com.example.myapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.data.Habit
import com.google.android.material.checkbox.MaterialCheckBox

class HabitsAdapter(
    private val habits: List<Habit>,
    private val onHabitAction: (Habit, String) -> Unit
) : RecyclerView.Adapter<HabitsAdapter.HabitViewHolder>() {
    
    class HabitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivIcon: ImageView = itemView.findViewById(R.id.iv_habit_icon)
        val tvName: TextView = itemView.findViewById(R.id.tv_habit_name)
        val tvDescription: TextView = itemView.findViewById(R.id.tv_habit_description)
        val checkbox: MaterialCheckBox = itemView.findViewById(R.id.checkbox_habit)
        val btnEdit: ImageButton = itemView.findViewById(R.id.btn_edit_habit)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btn_delete_habit)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_habit, parent, false)
        return HabitViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val habit = habits[position]
        
        holder.tvName.text = habit.name
        holder.tvDescription.text = habit.description
        holder.checkbox.isChecked = habit.isCompletedToday
        
        // Set icon based on habit type
        val iconRes = when (habit.name.lowercase()) {
            "drink water", "water" -> R.drawable.ic_water
            "exercise", "workout" -> R.drawable.ic_exercise
            "meditation", "meditate" -> R.drawable.ic_meditation
            else -> R.drawable.ic_habits
        }
        holder.ivIcon.setImageResource(iconRes)
        
        // Set click listeners (do not mutate here; delegate desired state)
        holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
            onHabitAction(habit, if (isChecked) "check" else "uncheck")
        }
        
        holder.btnEdit.setOnClickListener {
            onHabitAction(habit, "edit")
        }
        
        holder.btnDelete.setOnClickListener {
            onHabitAction(habit, "delete")
        }

        holder.itemView.setOnLongClickListener {
            onHabitAction(habit, "delete")
            true
        }
    }
    
    override fun getItemCount(): Int = habits.size
}
