package com.example.myapplication

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.myapplication.fragments.HabitsFragment
import com.example.myapplication.fragments.HydrationFragment
import com.example.myapplication.fragments.MoodFragment
import com.example.myapplication.fragments.SettingsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {
    
    private var bottomNavigation: BottomNavigationView? = null
    private var navigationView: NavigationView? = null
    private var drawerLayout: DrawerLayout? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        
        setupWindowInsets()
        setupNavigation()
        
        // Load default fragment
        if (savedInstanceState == null) {
            loadFragment(HabitsFragment())
            // ensure bottom navigation reflects current tab
            bottomNavigation?.selectedItemId = R.id.nav_habits
        }
    }
    
    private fun setupWindowInsets() {
        val mainContent: View? = findViewById(R.id.main_content)
        if (mainContent != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainContent) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }
    }
    
    private fun setupNavigation() {
        // Setup bottom navigation for portrait mode
        bottomNavigation = findViewById(R.id.bottom_navigation)
        bottomNavigation?.setOnItemSelectedListener { item ->
            handleNavigation(item.itemId)
            true
        }
        
        // Setup navigation drawer for landscape mode
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)

        navigationView?.setNavigationItemSelectedListener { item ->
            handleNavigation(item.itemId)
            drawerLayout?.closeDrawers()
            true
        }
    }
    
    private fun handleNavigation(itemId: Int): Boolean {
        val fragment: Fragment = when (itemId) {
            R.id.nav_habits -> HabitsFragment()
            R.id.nav_mood -> MoodFragment()
            R.id.nav_hydration -> HydrationFragment()
            R.id.nav_settings -> SettingsFragment()
            else -> return false
        }
        
        loadFragment(fragment)
        return true
    }
    
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
    
    override fun onBackPressed() {
        val dl = drawerLayout
        val nv = navigationView
        if (dl != null && nv != null && dl.isDrawerOpen(nv)) {
            dl.closeDrawers()
        } else {
            super.onBackPressed()
        }
    }
}