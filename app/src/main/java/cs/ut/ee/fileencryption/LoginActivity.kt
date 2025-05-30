package cs.ut.ee.fileencryption

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.security.MessageDigest

class LoginActivity : AppCompatActivity() {

    private lateinit var etPassword: EditText
    private lateinit var togglePassword: ImageButton
    private lateinit var btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize views
        etPassword = findViewById(R.id.etPassword)
        togglePassword = findViewById(R.id.togglePassword)
        btnLogin = findViewById(R.id.btnLogin)

        // Toggle password visibility
        togglePasswordVisibility(etPassword, togglePassword)

        // Handle login button click
        btnLogin.setOnClickListener {
            val enteredPassword = etPassword.text.toString()

            if (enteredPassword.isEmpty()) {
                Toast.makeText(this, "Please enter your password.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (validatePassword(enteredPassword)) {
                // Password is correct, proceed to HomeActivity
                val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                // Incorrect password
                Toast.makeText(this, "Incorrect password. Please try again.", Toast.LENGTH_SHORT).show()
                etPassword.text.clear()
            }
        }
    }

    private fun validatePassword(password: String): Boolean {
        val prefs: SharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val storedPasswordHash = prefs.getString("password_hash", "")

        return if (storedPasswordHash.isNullOrEmpty()) {
            // No password stored, something went wrong
            false
        } else {
            // Compare hashed passwords
            val enteredPasswordHash = hashPassword(password)
            storedPasswordHash == enteredPasswordHash
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