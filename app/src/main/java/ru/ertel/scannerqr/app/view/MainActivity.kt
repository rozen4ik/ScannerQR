package ru.ertel.scannerqr.app.view

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.FrameLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import ru.ertel.scannerqr.app.R
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import org.koin.core.KoinComponent
import ru.ertel.scannerqr.app.controller.KonturController
import ru.ertel.scannerqr.app.data.DataSourceCard
import ru.ertel.scannerqr.app.data.DataSourceCatalogPackage
import ru.ertel.scannerqr.gear.NfcAct

class MainActivity : NfcAct(), KoinComponent {

    private val bundle = Bundle()
    private var messageAnswerKontur = ""
    private lateinit var infoCard: Button
    private lateinit var passageCard: Button
    private val infoCardFragment: InfoCardFragment = InfoCardFragment()
    private val passageCardFragment: PassageCardFragment = PassageCardFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        infoCard = findViewById(R.id.infoCard)
        passageCard = findViewById(R.id.passageCard)

        var count = 0
        val frame: FrameLayout = findViewById(R.id.place_fragments)
        frame.setOnClickListener {
            count++
            if (count == 5) {
                count = 0
                val intent = Intent(this@MainActivity, SettingsActivity::class.java)
                startActivity(intent)
            }
        }

        var resultScanInfoCard = intent?.extras?.getString(ScanCardActivity.SCANINFOCARD)
        val dataSourceCard = DataSourceCard()
        val dataSourceCatalogPackage = DataSourceCatalogPackage()
        val konturController = KonturController()
        var bodyURL = ""
        var url = ""
        var urlPassage = ""
        var messageInfoCard =
            "<?xml version=\"1.0\" encoding=\"windows1251\" ?>" +
                    "<spd-xml-api>" +
                    "<request version=\"1.0\" ruid=\"739F9F2B-AEDD-4D94-93FF-EB59A9A1FCF5\">" +
                    "<client identifier=\"$resultScanInfoCard\">" +
                    "<identifiers />" +
                    "</client>" +
                    "</request>" +
                    "</spd-xml-api>"
        val messageBlockDevice = "<?xml version=\"1.0\" encoding=\"Windows-1251\"?> " +
                "<script session=\"85D323F3-8EBD-48E6-A085-4E652468B8D6\"> " +
                "<command name=\"cLockDevice\" device=\"5\" guid=\"95D454F3-8EBD-50E6-A085-4E644468B8D6\"> " +
                "<param name=\"cpLocker\">Карта Тройка</param> " +
                "<param name=\"cpDuration\">30000</param> " +
                "<param name=\"cpSession\">85D323F3-8EBD-48E6-A085-4E652468B8D6</param> " +
                "</command> " +
                "</script>"
        var messagePassageCard = "<?xml version=\"1.0\" encoding=\"Windows-1251\"?> " +
                "<script session=\"85D323F3-8EBD-48E6-A085-4E652468B8D6\"> " +
                "<command name=\"cRequest\" device=\"5\" guid=\"44871464-8EBD-56E6-A085-4E654768B8D6\"> " +
                "<param name=\"cpCard\">$resultScanInfoCard</param> " +
                "<param name=\"cpCardType\">1</param> " +
                "<param name=\"cpDirection\">1</param> " +
                "<param name=\"cpText\">Запрос по карте Тройка</param> " +
                "</command> " +
                "</script>"
        val messageUnBlockDevice = "<?xml version=\"1.0\" encoding=\"Windows-1251\"?> " +
                "<script session=\"85D323F3-8EBD-48E6-A085-4E652468B8D6\"> " +
                "<command name=\"cUnlockDevice\" device=\"5\" guid=\"98545167-8EBD-6578-A085-4E633368B8D6\"> " +
                "<param name=\"cpLocker\">Карта Тройка</param> " +
                "<param name=\"cpDuration\">30000</param> " +
                "<param name=\"cpSession\">85D323F3-8EBD-48E6-A085-4E652468B8D6</param> " +
                "</command> " +
                "</script>"
        val answerDevice = "<?xml version=\"1.0\" encoding=\"Windows-1251\"?> " +
                "<script session=\"85D323F3-8EBD-48E6-A085-4E652468B8D6\"> " +
                "<wait delay=\"20000\" device=\"5\"/> " +
                "</script>"

        infoCard.setOnClickListener {
            finish()
            val intent = Intent(this@MainActivity, ScanCardActivity::class.java)
            checkCameraPermission(intent)
        }

        passageCard.setOnClickListener {
            finish()
            val intent = Intent(this@MainActivity, ScanCardActivity::class.java)
            intent.putExtra(ScanCardActivity.SCANINFOCARD, "/")
            checkCameraPermission(intent)
        }

        if (resultScanInfoCard != null) {
            if (resultScanInfoCard.contains("/", ignoreCase = true)) {
                messageInfoCard = messageInfoCard.replace(
                    "$resultScanInfoCard",
                    resultScanInfoCard.replace("/", "")
                )
                messagePassageCard = messagePassageCard.replace(
                    "$resultScanInfoCard",
                    resultScanInfoCard.replace("/", "")
                )
                val settings: SharedPreferences = getSharedPreferences("URL", MODE_PRIVATE)
                bodyURL = settings.getString(SettingsActivity.SAVE_SETTINGS, "").toString()
                url = "$bodyURL/spd-xml-api"
                urlPassage = "$bodyURL/monitor?script=True"
                runBlocking {
                    launch(newSingleThreadContext("MyOwnThread")) {
                        try {
                            konturController.requestPOST(urlPassage, messageBlockDevice)
                            konturController.requestPOST(urlPassage, messagePassageCard)
                            dataSourceCatalogPackage.setMessagePassageCard(
                                konturController.requestPOST(
                                    urlPassage,
                                    messagePassageCard
                                )
                            )
                            dataSourceCatalogPackage.setAnswerDevice(
                                konturController.requestPOST(
                                    urlPassage,
                                    answerDevice
                                )
                            )
                            konturController.requestPOST(urlPassage, messageUnBlockDevice)
                            dataSourceCatalogPackage.setInfoCard(
                                konturController.requestPOST(
                                    url,
                                    messageInfoCard
                                )
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
                bundle.putString("deviceName", dataSourceCatalogPackage.getPassageCard().deviceName)
                bundle.putString("requestPassage", resultScanInfoCard)
                bundle.putString("solution", dataSourceCatalogPackage.getPassageCard().solution)
                bundle.putString("capt", dataSourceCatalogPackage.getPassageCard().capt)
                bundle.putString(
                    "numberOfPasses",
                    dataSourceCatalogPackage.getPassageCard().numberOfPasses
                )
                bundle.putString("datePasses", dataSourceCatalogPackage.getPassageCard().datePasses)
                passageCardFragment.arguments = bundle
                openFragment(passageCardFragment)
            } else {
                val settings: SharedPreferences = getSharedPreferences("URL", MODE_PRIVATE)
                bodyURL = settings.getString(SettingsActivity.SAVE_SETTINGS, "").toString()
                url = "$bodyURL/spd-xml-api"
                urlPassage = "$bodyURL/monitor?script=True"
                updateInfo(konturController, dataSourceCard, url, messageInfoCard)
                bundle.putString("condition", dataSourceCard.getInfoCard().condition)
                bundle.putString("number", dataSourceCard.getInfoCard().number)
                bundle.putString("ruleOfUse", dataSourceCard.getInfoCard().ruleOfUse)
                bundle.putString(
                    "permittedRates",
                    dataSourceCard.getInfoCard().permittedRates
                )
                bundle.putString("startAction", dataSourceCard.getInfoCard().startAction)
                bundle.putString("endAction", dataSourceCard.getInfoCard().endAction)
                infoCardFragment.arguments = bundle
                openFragment(infoCardFragment)
            }
        }

        enableBeam()
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_about -> {
                val intent = Intent(this@MainActivity, AboutActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item);
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 12) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            }
        }
    }

    private fun checkCameraPermission(intent: Intent) {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.CAMERA), 12)
        } else {
            startActivity(intent)
        }
    }

    private fun openFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.place_fragments, fragment)
            .commit()
    }

    private fun updateInfo(
        konturController: KonturController,
        dataSourceCard: DataSourceCard,
        url: String,
        messageInfoCard: String
    ) {
        runBlocking {
            launch(newSingleThreadContext("MyOwnThread")) {
                try {
                    messageAnswerKontur = konturController.requestPOST(url, messageInfoCard)
                    dataSourceCard.setMessageInfoCard(messageAnswerKontur)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}