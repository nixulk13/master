package cs.ut.ee.fileencryption

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest.permission
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.view.View

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.my_toolbar))

        // Always require login - redirect to LoginActivity
        val prefs: SharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val isFirstTime = prefs.getBoolean("isFirstTime", true)

        if (isFirstTime) {
            // First time user, go to registration first
            val intent = Intent(this@MainActivity, InscriptionActivity::class.java)
            startActivity(intent)
            finish()
            return
        } else {
            // User is registered, always require login
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> startActivity(Intent(this, SettingsActivity()::class.java))
            R.id.action_logout -> {
                // Add logout functionality
                logout()
                return true
            }
        }
        return true
    }

    private fun logout() {
        // Clear any session data if needed
        // Navigate back to login
        val intent = Intent(this@MainActivity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    fun encryptFile(view: View) {
        openFileExplorer(10)
    }

    fun decryptFile(view: View) {
        val intent = Intent(this, FileList::class.java)
        startActivity(intent)
    }

    fun openFileExplorer(requestCode: Int) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent, requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result code correctly encrypted file
        if (resultCode == 10){
            Toast.makeText(applicationContext, R.string.toast_2, Toast.LENGTH_SHORT).show()
        }

        // File selected
        else if (resultCode == -1 && data != null) {
            val uri = data.data

            var name = ""
            var path = ""

            try {
                // This should try to get the absolute path. Seem to only work on older android versions
                if (uri!!.scheme.toString().compareTo("content") == 0) {
                    val cursor: Cursor = contentResolver.query(uri, null, null, null, null)!!
                    if (cursor.moveToFirst()) {
                        val columnIndex: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                        val filePathUri = Uri.parse(cursor.getString(columnIndex))
                        name = filePathUri.lastPathSegment.toString()
                        path = filePathUri.path!!
                    }
                }
            } catch (e : Exception) {
                path = uri!!.path!!.split(":")[1]

                val fileName = path.split("/")
                name = fileName[fileName.size-1]
            }

            // Decrypt file
            if (path.endsWith(".crypt")) {
                val intent = Intent(this, DecryptFile()::class.java)
                intent.putExtra("path", path)
                intent.putExtra("name", name.replace(".crypt", ""))
                startActivityForResult(intent, 1)

                // Encrypt file
            } else {
                val intent = Intent(this, EncryptFile()::class.java)
                intent.putExtra("path", path)
                intent.putExtra("name", name)
                startActivityForResult(intent, 1)
            }
        }
    }
}