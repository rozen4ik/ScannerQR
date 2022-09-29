package ru.ertel.scannerqr.app.view

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import ru.ertel.scannerqr.app.R
import ru.ertel.scannerqr.app.controller.KonturController
import ru.ertel.scannerqr.app.data.DataSourceCard
import ru.ertel.scannerqr.app.data.DataSourceDevice

class DeviceManagementActivity : AppCompatActivity() {

    private lateinit var settings: SharedPreferences
    private lateinit var setDeivce: SharedPreferences
    private lateinit var bodyURL: String
    private lateinit var device: String
    private lateinit var url: String
    private lateinit var messageListDevices: String
    private lateinit var konturController: KonturController
    private lateinit var bundle: Bundle
    private lateinit var listDeviceFragment: ListDeviceFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_management)

        bundle = Bundle()
        settings = getSharedPreferences("URL", MODE_PRIVATE)
        setDeivce = getSharedPreferences("DEVICE", MODE_PRIVATE)
        bodyURL = settings.getString(SettingsActivity.SAVE_SETTINGS, "").toString()
        device = setDeivce.getString(SettingsActivity.SAVE_SETTINGS, "").toString()
        listDeviceFragment = ListDeviceFragment()
        konturController = KonturController()
        url = "$bodyURL/monitor?script=True"
        Log.d("TAG", url)
        messageListDevices = "<?xml version=\"1.0\" encoding=\"Windows-1251\"?>\n" +
                "<script/>"

        updateInfo(konturController, url,  messageListDevices)


        device = arrayListOf("Турникет", "Дверь").toString()

        bundle.putString("device", device)
        listDeviceFragment.arguments = bundle
        openFragment(listDeviceFragment)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.add(Menu.NONE, 2, 2, "Ручной ввод")
        menu?.add(Menu.NONE, 3, 2, "Автоматический")
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_about -> {
                val intent = Intent(this@DeviceManagementActivity, AboutActivity::class.java)
                startActivity(intent)
                finish()
                return true
            }
            2 -> {
                val intent = Intent(this@DeviceManagementActivity, ManualActivity::class.java)
                startActivity(intent)
                finish()
                return true
            }
            3 -> {
                val intent = Intent(this@DeviceManagementActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private fun openFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.placeDevicefragments, fragment)
            .commit()
    }

    private fun updateInfo(
        konturController: KonturController,
//        dataSourceDevice: DataSourceDevice,
        url: String,
        messageDevice: String
    ) {
        runBlocking {
            launch(newSingleThreadContext("MyOwnThread")) {
                try {
                    Log.d("TAG", konturController.requestPOST(url, messageDevice))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}