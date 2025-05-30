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
import java.security.MessageDigest

class InscriptionActivity : AppCompatActivity() {

    private lateinit var etNewPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var toggleNewPassword: ImageButton
    private lateinit var toggleConfirmPassword: ImageButton
    private lateinit var btnRegister: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_inscription)

        // SharedPreferences to check if it's the first time the user is opening the app
        val prefs: SharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val isFirstTime = prefs.getBoolean("isFirstTime", true)

        if (!isFirstTime) {
            // If it's not the first time, go to login activity
            val intent = Intent(this@InscriptionActivity, LoginActivity::class.java)
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
                Toast.makeText(this, "Password must be at least 8 characters long.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if the passwords match
            if (newPassword != confirmPassword) {
                Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Hash and store the password
            val passwordHash = hashPassword(newPassword)
            val editor = prefs.edit()
            editor.putString("password_hash", passwordHash)
            editor.putBoolean("isFirstTime", false)
            editor.apply()

            Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()

            // Move to login activity after successful registration
            val intent = Intent(this@InscriptionActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(password.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    private fun togglePasswordVisibility(editText: EditText, imageButton: ImageButton) {
        imageButton.setOnClickListener {
            if (editText.inputType == android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD) {
                editText.inputType = android.text.InputType.TYPE_CLASS_TEXT
                imageButton.setImageResource(R.drawable.ic_visibility)
            } else {
                editText.inputType = android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                imageButton.setImageResource(R.drawable.ic_visibility_off)
            }
            editText.setSelection(editText.text.length)
        }
    }
}