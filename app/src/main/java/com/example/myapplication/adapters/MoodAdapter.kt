package com.example.myapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.data.MoodEntry
import java.text.SimpleDateFormat
import java.util.*

class MoodAdapter(
    private val moodEntries: List<MoodEntry>,
    private val onShareClick: (MoodEntry) -> Unit,
    private val onDeleteClick: (MoodEntry) -> Unit
) : RecyclerView.Adapter<MoodAdapter.MoodViewHolder>() {
    
    class MoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMoodEmoji: TextView = itemView.findViewById(R.id.tv_mood_emoji)
        val tvMoodType: TextView = itemView.findViewById(R.id.tv_mood_type)
        val tvMoodDate: TextView = itemView.findViewById(R.id.tv_mood_date)
        val tvMoodNote: TextView = itemView.findViewById(R.id.tv_mood_note)
        val btnShare: ImageButton = itemView.findViewById(R.id.btn_share_mood)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btn_delete_mood)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mood_entry, parent, false)
        return MoodViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: MoodViewHolder, position: Int) {
        val moodEntry = moodEntries[position]
        
        holder.tvMoodEmoji.text = moodEntry.emoji
        holder.tvMoodType.text = moodEntry.moodType
        holder.tvMoodDate.text = formatDate(moodEntry.timestamp)
        holder.tvMoodNote.text = moodEntry.note
        
        holder.btnShare.setOnClickListener {
            onShareClick(moodEntry)
        }
        
        holder.btnDelete.setOnClickListener {
            onDeleteClick(moodEntry)
        }
    }
    
    override fun getItemCount(): Int = moodEntries.size
    
    private fun formatDate(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        
        return when {
            diff < 60 * 1000 -> "Just now"
            diff < 60 * 60 * 1000 -> "${diff / (60 * 1000)}m ago"
            diff < 24 * 60 * 60 * 1000 -> "${diff / (60 * 60 * 1000)}h ago"
            else -> {
                val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
                dateFormat.format(Date(timestamp))
            }
        }
    }
}

