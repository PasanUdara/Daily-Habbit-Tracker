package com.example.myapplication.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.myapplication.LoginActivity
import com.example.myapplication.R
import com.example.myapplication.utils.HabitManager
import com.example.myapplication.utils.HydrationManager
import com.example.myapplication.utils.MoodManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class SettingsFragment : Fragment() {
    
    private lateinit var ivProfileAvatar: ImageView
    private lateinit var tvProfileName: TextView
    private lateinit var tvProfileEmail: TextView
    private lateinit var tvMemberSince: TextView
    private lateinit var btnEditProfile: MaterialButton
    private lateinit var btnLogout: MaterialButton
    private lateinit var btnNotificationsSettings: ImageButton
    private lateinit var btnThemeSettings: ImageButton
    private lateinit var btnExportData: ImageButton
    private lateinit var btnImportData: ImageButton
    private lateinit var btnAbout: ImageButton
    
    private lateinit var habitManager: HabitManager
    private lateinit var moodManager: MoodManager
    private lateinit var hydrationManager: HydrationManager
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        initializeViews(view)
        setupClickListeners()
        loadUserData()
    }
    
    private fun initializeViews(view: View) {
        ivProfileAvatar = view.findViewById(R.id.iv_profile_avatar)
        tvProfileName = view.findViewById(R.id.tv_profile_name)
        tvProfileEmail = view.findViewById(R.id.tv_profile_email)
        tvMemberSince = view.findViewById(R.id.tv_member_since)
        btnEditProfile = view.findViewById(R.id.btn_edit_profile)
        btnLogout = view.findViewById(R.id.btn_logout)
        btnNotificationsSettings = view.findViewById(R.id.btn_notifications_settings)
        btnThemeSettings = view.findViewById(R.id.btn_theme_settings)
        btnExportData = view.findViewById(R.id.btn_export_data)
        btnImportData = view.findViewById(R.id.btn_import_data)
        btnAbout = view.findViewById(R.id.btn_about)
        
        habitManager = HabitManager(requireContext())
        moodManager = MoodManager(requireContext())
        hydrationManager = HydrationManager(requireContext())
    }
    
    private fun setupClickListeners() {
        btnEditProfile.setOnClickListener {
            showEditProfileDialog()
        }
        
        btnLogout.setOnClickListener {
            showLogoutDialog()
        }
        
        btnNotificationsSettings.setOnClickListener {
            showNotificationsSettings()
        }
        
        btnThemeSettings.setOnClickListener {
            showThemeSettings()
        }
        
        btnExportData.setOnClickListener {
            exportData()
        }
        
        btnImportData.setOnClickListener {
            importData()
        }
        
        btnAbout.setOnClickListener {
            showAboutDialog()
        }
    }
    
    private fun loadUserData() {
        // Load user profile data from SharedPreferences
        // SharedPreferences stores user profile information persistently
        val prefs = requireContext().getSharedPreferences("wellness_tracker", android.content.Context.MODE_PRIVATE)
        
        // Retrieve user data with default values if keys don't exist
        // getString() returns null if key doesn't exist, so we provide defaults
        val userName = prefs.getString("user_name", "John Doe") ?: "John Doe"
        val userEmail = prefs.getString("user_email", "john.doe@example.com") ?: "john.doe@example.com"
        val memberSince = prefs.getString("member_since", "Dec 2024") ?: "Dec 2024"
        
        tvProfileName.text = userName
        tvProfileEmail.text = userEmail
        tvMemberSince.text = "Member since $memberSince"
    }
    
    private fun showEditProfileDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(android.R.layout.simple_list_item_2, null)
        
        val etName = EditText(requireContext()).apply {
            hint = "Full Name"
            setText(tvProfileName.text)
        }
        
        val etEmail = EditText(requireContext()).apply {
            hint = "Email"
            setText(tvProfileEmail.text)
            inputType = android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        }
        
        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 50, 50, 50)
            addView(etName)
            addView(etEmail)
        }
        
        AlertDialog.Builder(requireContext())
            .setTitle("Edit Profile")
            .setView(layout)
            .setPositiveButton("Save") { _, _ ->
                val name = etName.text.toString().trim()
                val email = etEmail.text.toString().trim()
                
                if (name.isNotEmpty() && email.isNotEmpty()) {
                    // Save updated user profile data to SharedPreferences
                    // This persists the user's name and email changes
                    val prefs = requireContext().getSharedPreferences("wellness_tracker", android.content.Context.MODE_PRIVATE)
                    prefs.edit()
                        .putString("user_name", name)     // Store updated user name
                        .putString("user_email", email)   // Store updated user email
                        .apply()                          // Save asynchronously
                    
                    tvProfileName.text = name
                    tvProfileEmail.text = email
                    
                    Toast.makeText(requireContext(), "Profile updated!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun showNotificationsSettings() {
        val options = arrayOf("All notifications", "Habits only", "Hydration only", "Mood only", "None")
        var selectedIndex = 0
        
        AlertDialog.Builder(requireContext())
            .setTitle("Notification Settings")
            .setSingleChoiceItems(options, selectedIndex) { _, which ->
                selectedIndex = which
            }
            .setPositiveButton("Save") { _, _ ->
                // Save notification preference to SharedPreferences
                // This stores the user's notification frequency setting
                val prefs = requireContext().getSharedPreferences("wellness_tracker", android.content.Context.MODE_PRIVATE)
                prefs.edit().putString("notification_setting", options[selectedIndex]).apply()
                Toast.makeText(requireContext(), "Notification settings saved!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun showThemeSettings() {
        val themes = arrayOf("Light", "Dark", "System Default")
        var selectedIndex = 0
        
        AlertDialog.Builder(requireContext())
            .setTitle("Theme Settings")
            .setSingleChoiceItems(themes, selectedIndex) { _, which ->
                selectedIndex = which
            }
            .setPositiveButton("Apply") { _, _ ->
                // Save theme preference to SharedPreferences
                // This stores the user's selected app theme setting
                val prefs = requireContext().getSharedPreferences("wellness_tracker", android.content.Context.MODE_PRIVATE)
                prefs.edit().putString("theme", themes[selectedIndex]).apply()
                Toast.makeText(requireContext(), "Theme will be applied on app restart", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun exportData() {
        AlertDialog.Builder(requireContext())
            .setTitle("Export Data")
            .setMessage("This will export all your wellness data to a file. Continue?")
            .setPositiveButton("Export") { _, _ ->
                // TODO: Implement actual data export functionality
                // This would typically involve creating a JSON/CSV file with all user data
                Toast.makeText(requireContext(), "Data export feature coming soon!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun importData() {
        AlertDialog.Builder(requireContext())
            .setTitle("Import Data")
            .setMessage("This will import wellness data from a backup file. Continue?")
            .setPositiveButton("Import") { _, _ ->
                // TODO: Implement actual data import functionality
                // This would typically involve reading a JSON/CSV file and restoring user data
                Toast.makeText(requireContext(), "Data import feature coming soon!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun showAboutDialog() {
        val appVersion = "1.0.0"
        val aboutText = """
            Wellness Tracker v$appVersion
            
            A comprehensive app to help you track your daily wellness habits, mood, and hydration.
            
            Features:
            • Daily habit tracking
            • Mood journal with emoji selector
            • Hydration reminders
            • Progress statistics
            • Data export/import
            
            Built with ❤️ for your wellness journey.
        """.trimIndent()
        
        AlertDialog.Builder(requireContext())
            .setTitle("About Wellness Tracker")
            .setMessage(aboutText)
            .setPositiveButton("OK", null)
            .show()
    }
    
    private fun showLogoutDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Logout") { _, _ ->
                performLogout()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun performLogout() {
        // Clear user session data from SharedPreferences
        // This performs a complete logout by removing authentication state
        val prefs = requireContext().getSharedPreferences("wellness_tracker", android.content.Context.MODE_PRIVATE)
        prefs.edit()
            .putBoolean("is_logged_in", false)    // Mark user as logged out
            .putBoolean("is_guest", false)        // Clear guest mode status
            .remove("user_email")                 // Remove stored user email
            .apply()                              // Save logout state
        
        // Navigate to login activity
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }
}

