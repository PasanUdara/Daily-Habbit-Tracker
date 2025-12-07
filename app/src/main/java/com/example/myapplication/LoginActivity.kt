package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import android.widget.TextView

class LoginActivity : AppCompatActivity() {

    private lateinit var tilEmail: TextInputLayout
    private lateinit var tilPassword: TextInputLayout
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var cbRememberMe: MaterialCheckBox
    private lateinit var btnLogin: MaterialButton
    private lateinit var tvForgotPassword: TextView
    private lateinit var tvSignUp: TextView
    private lateinit var tvSkipLogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Check if user is already logged in using SharedPreferences
        // SharedPreferences is Android's built-in key-value storage for app settings
        // MODE_PRIVATE ensures only this app can access the data
        val prefs = getSharedPreferences("wellness_tracker", MODE_PRIVATE)
        
        // Check if user has previously logged in successfully
        // getBoolean returns false if the key doesn't exist (default value)
        if (prefs.getBoolean("is_logged_in", false)) {
            // User is already logged in, skip login screen and go to main app
            navigateToMain()
            return
        }
        
        setContentView(R.layout.activity_login)
        
        initializeViews()
        setupListeners()
        loadRememberedCredentials()
    }

    private fun initializeViews() {
        tilEmail = findViewById(R.id.til_email)
        tilPassword = findViewById(R.id.til_password)
        etEmail = findViewById(R.id.et_email)
        etPassword = findViewById(R.id.et_password)
        cbRememberMe = findViewById(R.id.cb_remember_me)
        btnLogin = findViewById(R.id.btn_login)
        tvForgotPassword = findViewById(R.id.tv_forgot_password)
        tvSignUp = findViewById(R.id.tv_sign_up)
        tvSkipLogin = findViewById(R.id.tv_skip_login)
    }

    private fun setupListeners() {
        // Real-time validation: validate fields as user types
        // This provides immediate feedback without waiting for form submission
        etEmail.addTextChangedListener(createTextWatcher { validateEmail() })
        etPassword.addTextChangedListener(createTextWatcher { validatePassword() })

        // Login button: validate form before attempting login
        btnLogin.setOnClickListener {
            // Only proceed with login if all validations pass
            if (validateForm()) {
                performLogin()
            }
        }

        // Forgot password
        tvForgotPassword.setOnClickListener {
            showForgotPasswordDialog()
        }

        // Sign up
        tvSignUp.setOnClickListener {
            Toast.makeText(this, "Sign up feature coming soon!", Toast.LENGTH_SHORT).show()
        }

        // Skip login - allow user to use app as guest
        tvSkipLogin.setOnClickListener {
            // Get SharedPreferences instance for storing guest mode preference
            val prefs = getSharedPreferences("wellness_tracker", MODE_PRIVATE)
            
            // Save guest mode preference using SharedPreferences editor
            // apply() saves asynchronously (faster than commit())
            prefs.edit().putBoolean("is_guest", true).apply()
            
            // Navigate to main app in guest mode
            navigateToMain()
        }
    }

    /**
     * Creates a TextWatcher for real-time field validation
     * 
     * This factory method creates a TextWatcher that triggers validation
     * after the user finishes typing in a field. This provides immediate
     * feedback without being too aggressive during typing.
     * 
     * @param validationFunc The validation function to call after text changes
     * @return TextWatcher that triggers validation on afterTextChanged
     */
    private fun createTextWatcher(validationFunc: () -> Unit): TextWatcher {
        return object : TextWatcher {
            // Not used - validation happens after text changes
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            
            // Not used - validation happens after text changes
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            
            // Trigger validation after user finishes changing the text
            override fun afterTextChanged(s: Editable?) {
                validationFunc()
            }
        }
    }

    /**
     * Validates the email input field
     * 
     * This function performs two validation checks:
     * 1. Checks if email field is not empty
     * 2. Validates email format using Android's built-in EMAIL_ADDRESS pattern
     * 
     * @return true if email is valid, false otherwise
     */
    private fun validateEmail(): Boolean {
        // Get email text and remove leading/trailing whitespace
        val email = etEmail.text.toString().trim()
        
        return when {
            // Check if email field is empty
            email.isEmpty() -> {
                tilEmail.error = "Email is required"
                false
            }
            // Check if email format is valid using Android's email pattern matcher
            // This validates standard email format: user@domain.com
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                tilEmail.error = "Please enter a valid email address"
                false
            }
            // Email is valid - clear any previous error messages
            else -> {
                tilEmail.error = null
                true
            }
        }
    }

    /**
     * Validates the password input field
     * 
     * This function performs two validation checks:
     * 1. Checks if password field is not empty
     * 2. Ensures password meets minimum length requirement (6 characters)
     * 
     * Note: For production apps, consider adding complexity requirements
     * such as uppercase, lowercase, numbers, and special characters
     * 
     * @return true if password is valid, false otherwise
     */
    private fun validatePassword(): Boolean {
        // Get password text (don't trim as spaces might be intentional)
        val password = etPassword.text.toString()
        
        return when {
            // Check if password field is empty
            password.isEmpty() -> {
                tilPassword.error = "Password is required"
                false
            }
            // Check if password meets minimum length requirement
            // Minimum 6 characters for basic security
            password.length < 6 -> {
                tilPassword.error = "Password must be at least 6 characters"
                false
            }
            // Password is valid - clear any previous error messages
            else -> {
                tilPassword.error = null
                true
            }
        }
    }

    /**
     * Validates the entire login form
     * 
     * This function orchestrates the validation of both email and password fields.
     * It also handles focus management to guide the user to the first invalid field.
     * 
     * Validation flow:
     * 1. Validate email field first
     * 2. If email is invalid, focus on email field
     * 3. If email is valid but password is invalid, focus on password field
     * 4. Return true only if both fields are valid
     * 
     * @return true if both email and password are valid, false otherwise
     */
    private fun validateForm(): Boolean {
        // Validate both fields
        val isEmailValid = validateEmail()
        val isPasswordValid = validatePassword()
        
        // Focus management: guide user to first invalid field
        if (!isEmailValid) {
            // Email is invalid - focus on email field first
            etEmail.requestFocus()
        } else if (!isPasswordValid) {
            // Email is valid but password is invalid - focus on password field
            etPassword.requestFocus()
        }
        
        // Return true only if both validations pass
        return isEmailValid && isPasswordValid
    }

    private fun performLogin() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()
        
        // Simple validation (In production, use proper authentication)
        // For demo purposes: accept any valid email/password format
        // Get SharedPreferences instance for storing login data
        val prefs = getSharedPreferences("wellness_tracker", MODE_PRIVATE)
        
        // Handle "Remember Me" functionality using SharedPreferences
        if (cbRememberMe.isChecked) {
            // Save user credentials for future login sessions
            // Using SharedPreferences to store sensitive data (in production, use encryption)
            prefs.edit()
                .putString("saved_email", email)        // Store email for auto-fill
                .putString("saved_password", password)  // Store password for auto-fill
                .putBoolean("remember_me", true)        // Remember user preference
                .apply()                                // Save asynchronously
        } else {
            // User doesn't want to be remembered, clear stored credentials
            prefs.edit()
                .remove("saved_email")      // Remove stored email
                .remove("saved_password")   // Remove stored password
                .putBoolean("remember_me", false)  // Update remember preference
                .apply()
        }
        
        // Save current login session state using SharedPreferences
        // This tracks the user's current session status
        prefs.edit()
            .putBoolean("is_logged_in", true)    // Mark user as logged in
            .putBoolean("is_guest", false)       // Mark as not guest mode
            .putString("user_email", email)      // Store current user's email
            .apply()                             // Save session state
        
        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
        navigateToMain()
    }

    private fun loadRememberedCredentials() {
        // Get SharedPreferences instance to retrieve saved credentials
        val prefs = getSharedPreferences("wellness_tracker", MODE_PRIVATE)
        
        // Check if user previously chose to be remembered
        if (prefs.getBoolean("remember_me", false)) {
            // Auto-fill email and password fields with saved credentials
            // getString() returns empty string if key doesn't exist (default value)
            etEmail.setText(prefs.getString("saved_email", ""))
            etPassword.setText(prefs.getString("saved_password", ""))
            
            // Pre-check the "Remember Me" checkbox
            cbRememberMe.isChecked = true
        }
    }

    private fun showForgotPasswordDialog() {
        val email = etEmail.text.toString().trim()
        
        if (email.isEmpty()) {
            tilEmail.error = "Please enter your email first"
            etEmail.requestFocus()
            return
        }
        
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.error = "Please enter a valid email address"
            etEmail.requestFocus()
            return
        }
        
        // Show success message (In production, send actual reset email)
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Password Reset")
            .setMessage("Password reset instructions have been sent to $email")
            .setPositiveButton("OK", null)
            .show()
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onBackPressed() {
        // Prevent going back to login after skipping
        finishAffinity()
    }
}
