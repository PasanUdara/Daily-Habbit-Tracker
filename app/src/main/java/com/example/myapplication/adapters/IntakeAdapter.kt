package com.example.myapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.data.HydrationEntry
import java.text.SimpleDateFormat
import java.util.*

class IntakeAdapter(
    private var intakeEntries: List<HydrationEntry>,
    private val onDeleteClick: (HydrationEntry) -> Unit
) : RecyclerView.Adapter<IntakeAdapter.IntakeViewHolder>() {

    class IntakeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvAmount: TextView = view.findViewById(R.id.tv_intake_amount)
        val tvTime: TextView = view.findViewById(R.id.tv_intake_time)
        val btnDelete: ImageButton = view.findViewById(R.id.btn_delete_intake)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntakeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_intake_entry, parent, false)
        return IntakeViewHolder(view)
    }

    override fun onBindViewHolder(holder: IntakeViewHolder, position: Int) {
        val entry = intakeEntries[position]
        
        // Set amount text
        val amountText = if (entry.amount == 1) "1 glass" else "${entry.amount} glasses"
        holder.tvAmount.text = amountText
        
        // Set time text (relative time)
        holder.tvTime.text = getRelativeTime(entry.timestamp)
        
        // Set delete button click listener
        holder.btnDelete.setOnClickListener {
            onDeleteClick(entry)
        }
    }

    override fun getItemCount(): Int = intakeEntries.size

    fun updateEntries(newEntries: List<HydrationEntry>) {
        intakeEntries = newEntries
        notifyDataSetChanged()
    }

    private fun getRelativeTime(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        
        return when {
            diff < 60000 -> "Just now" // Less than 1 minute
            diff < 3600000 -> {
                val minutes = (diff / 60000).toInt()
                if (minutes == 1) "1 minute ago" else "$minutes minutes ago"
            }
            diff < 86400000 -> {
                val hours = (diff / 3600000).toInt()
                if (hours == 1) "1 hour ago" else "$hours hours ago"
            }
            else -> {
                val days = (diff / 86400000).toInt()
                if (days == 1) "1 day ago" else "$days days ago"
            }
        }
    }
}
