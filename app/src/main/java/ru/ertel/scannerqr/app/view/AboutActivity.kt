package ru.ertel.scannerqr.app.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import ru.ertel.scannerqr.app.R

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        var count = 0
        val imageView: ImageView = findViewById(R.id.imageView)
        imageView.setOnClickListener {
            count++
            if (count == 5) {
                count = 0
                val intent = Intent(this@AboutActivity, SettingsActivity::class.java)
                startActivity(intent)
            }
        }
    }
}