package ru.ertel.scannerqr.app.view

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import ru.ertel.scannerqr.app.R

class SettingsActivity : AppCompatActivity() {

    companion object {
        const val SAVE_SETTINGS = "save_settings"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setContentView(R.layout.activity_settings)
        val buttonSaveSettings: Button = findViewById(R.id.buttonSaveSettings)
        val showURL: TextView = findViewById(R.id.showURL)

        buttonSaveSettings.setOnClickListener {
            val editURLBody: EditText = findViewById(R.id.editURLBody)
            val editURLPort: EditText = findViewById(R.id.editURLPort)
            var url = ""

            if (editURLBody.text.isNotEmpty() && editURLPort.text.isNotEmpty()) {
                url = "http://${editURLBody.text.toString()}:${editURLPort.text.toString()}"
                val settings: SharedPreferences = getSharedPreferences("URL", MODE_PRIVATE)
                val saveUrl: SharedPreferences.Editor = settings.edit()
                saveUrl.putString(SAVE_SETTINGS, url)
                saveUrl.commit()
            } else if (editURLBody.text.isNotEmpty()) {
                val settings: SharedPreferences = getSharedPreferences("URL", MODE_PRIVATE)
                val saveUrl: SharedPreferences.Editor = settings.edit()
                val body = editURLBody.text.toString()
                val port = settings.getString(SAVE_SETTINGS, "").toString().substringAfterLast(":")
                url = "http://$body:$port"
                saveUrl.putString(SAVE_SETTINGS, url)
                saveUrl.commit()
            } else if (editURLPort.text.isNotEmpty()) {
                val settings: SharedPreferences = getSharedPreferences("URL", MODE_PRIVATE)
                val saveUrl: SharedPreferences.Editor = settings.edit()
                val body = settings.getString(SAVE_SETTINGS, "").toString().substringAfter("//").substringBeforeLast(":")
                val port = editURLPort.text.toString()
                url = "http://$body:$port"
                saveUrl.putString(SAVE_SETTINGS, url)
                saveUrl.commit()
            }

            showURL.text = url
            Toast.makeText(this, "$url сохранён", Toast.LENGTH_SHORT).show()
        }

        val settings: SharedPreferences = getSharedPreferences("URL", MODE_PRIVATE)
        showURL.text = settings.getString(SAVE_SETTINGS, "").toString()
    }
}