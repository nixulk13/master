package cs.ut.ee.fileencryption

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class InscriptionActivity : AppCompatActivity() {

    private lateinit var etNewPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var toggleNewPassword: ImageButton
    private lateinit var toggleConfirmPassword: ImageButton
    private lateinit var btnRegister: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_inscription) // Link to the XML layout

        // SharedPreferences to check if it's the first time the user is opening the app
        val prefs: SharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val isFirstTime = prefs.getBoolean("isFirstTime", true)

        if (!isFirstTime) {
            // If it's not the first time, skip InscriptionActivity and go to the main activity
            val intent = Intent(this@InscriptionActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        // Initialize views
        etNewPassword = findViewById(R.id.etNewPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        toggleNewPassword = findViewById(R.id.toggleNewPassword)
        toggleConfirmPassword = findViewById(R.id.toggleConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)
        progressBar = findViewById(R.id.progressBar)

        // Toggle password visibility logic
        togglePasswordVisibility(etNewPassword, toggleNewPassword)
        togglePasswordVisibility(etConfirmPassword, toggleConfirmPassword)

        // Handle the register button click
        btnRegister.setOnClickListener {
            val newPassword = etNewPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()

            // Check if the password is at least 8 characters
            if (newPassword.length < 8) {
                // Show a Toast message or error if password length is less than 8
                Toast.makeText(this, "Password must be at least 8 characters long.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if the passwords match
            if (newPassword != confirmPassword) {
                // Show a Toast message or error if passwords do not match
                Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // After registration, set the first-time flag to false
            val editor = prefs.edit()
            editor.putBoolean("isFirstTime", false)
            editor.apply()

            // Optionally, move to the main activity after successful registration
            val intent = Intent(this@InscriptionActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun togglePasswordVisibility(editText: EditText, imageButton: ImageButton) {
        imageButton.setOnClickListener {
            if (editText.inputType == android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD) {
                editText.inputType = android.text.InputType.TYPE_CLASS_TEXT
                imageButton.setImageResource(R.drawable.ic_visibility) // Change icon to visible
            } else {
                editText.inputType = android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                imageButton.setImageResource(R.drawable.ic_visibility_off) // Change icon to invisible
            }
            editText.setSelection(editText.text.length)
        }
    }
}
