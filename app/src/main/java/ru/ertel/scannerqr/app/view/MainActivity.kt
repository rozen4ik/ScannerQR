package ru.ertel.scannerqr.app.view

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
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
import ru.ertel.scannerqr.app.view.StartActivity.Companion.SAVE_TOKEN
import ru.ertel.scannerqr.gear.NfcAct

class MainActivity : NfcAct(), KoinComponent {

    private val bundle = Bundle()
    private var messageAnswerKontur = ""
    private lateinit var url: String
    private lateinit var urlPassage: String
    private lateinit var infoCard: Button
    private lateinit var passageCard: Button
    private val infoCardFragment: InfoCardFragment = InfoCardFragment()
    private val passageCardFragment: PassageCardFragment = PassageCardFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        infoCard = findViewById(R.id.infoCard)
        passageCard = findViewById(R.id.passageCard)

        val settings: SharedPreferences = getSharedPreferences("URL", MODE_PRIVATE)
        val setDeivce: SharedPreferences = getSharedPreferences("DEVICE", MODE_PRIVATE)
        val bodyURL = settings.getString(SettingsActivity.SAVE_SETTINGS, "").toString()
        val device = setDeivce.getString(SettingsActivity.SAVE_SETTINGS, "").toString()

        if (bodyURL.isEmpty()) {
            Toast.makeText(this, "ip и порт не настроены", Toast.LENGTH_SHORT).show()
        }

        var resultScanInfoCard = intent?.extras?.getString(ScanCardActivity.SCANINFOCARD)
        val dataSourceCard = DataSourceCard()
        val dataSourceCatalogPackage = DataSourceCatalogPackage()
        val konturController = KonturController()
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
                "<command name=\"cLockDevice\" device=\"$device\" guid=\"95D454F3-8EBD-50E6-A085-4E644468B8D6\"> " +
                "<param name=\"cpLocker\">Карта Тройка</param> " +
                "<param name=\"cpDuration\">30000</param> " +
                "<param name=\"cpSession\">85D323F3-8EBD-48E6-A085-4E652468B8D6</param> " +
                "</command> " +
                "</script>"
        var messagePassageCard = "<?xml version=\"1.0\" encoding=\"Windows-1251\"?> " +
                "<script session=\"85D323F3-8EBD-48E6-A085-4E652468B8D6\"> " +
                "<command name=\"cRequest\" device=\"$device\" guid=\"44871464-8EBD-56E6-A085-4E654768B8D6\"> " +
                "<param name=\"cpCard\">$resultScanInfoCard</param> " +
                "<param name=\"cpCardType\">1</param> " +
                "<param name=\"cpDirection\">1</param> " +
                "<param name=\"cpText\">Запрос по карте</param> " +
                "</command> " +
                "</script>"
        val messageUnBlockDevice = "<?xml version=\"1.0\" encoding=\"Windows-1251\"?> " +
                "<script session=\"85D323F3-8EBD-48E6-A085-4E652468B8D6\"> " +
                "<command name=\"cUnlockDevice\" device=\"$device\" guid=\"98545167-8EBD-6578-A085-4E633368B8D6\"> " +
                "<param name=\"cpLocker\">Карта Тройка</param> " +
                "<param name=\"cpDuration\">30000</param> " +
                "<param name=\"cpSession\">85D323F3-8EBD-48E6-A085-4E652468B8D6</param> " +
                "</command> " +
                "</script>"
        val answerDevice = "<?xml version=\"1.0\" encoding=\"Windows-1251\"?> " +
                "<script session=\"85D323F3-8EBD-48E6-A085-4E652468B8D6\"> " +
                "<wait delay=\"20000\" device=\"$device\"/> " +
                "</script>"

        infoCard.setOnClickListener {
            val intent = Intent(this@MainActivity, ScanCardActivity::class.java)
            checkCameraPermission(intent)
        }

        passageCard.setOnClickListener {
            val intent = Intent(this@MainActivity, ScanCardActivity::class.java)
            intent.putExtra(ScanCardActivity.SCANINFOCARD, "/")
            checkCameraPermission(intent)
        }

        if (resultScanInfoCard != null) {
            val set = getSharedPreferences("konturToken", MODE_PRIVATE)
            val numberKontur = set.getString(SAVE_TOKEN, "no").toString()

            if (resultScanInfoCard.contains("/", ignoreCase = true)) {
                messageInfoCard = messageInfoCard.replace(
                    "$resultScanInfoCard",
                    resultScanInfoCard.replace("/", "")
                )
                messagePassageCard = messagePassageCard.replace(
                    "$resultScanInfoCard",
                    resultScanInfoCard.replace("/", "")
                )
                url = "$bodyURL/spd-xml-api"
                urlPassage = "$bodyURL/monitor?script=True"
                updatePassageCard(
                    konturController,
                    dataSourceCatalogPackage,
                    urlPassage,
                    url,
                    messageBlockDevice,
                    messagePassageCard,
                    answerDevice,
                    messageUnBlockDevice,
                    messageInfoCard,
                    numberKontur,
                )
                if (dataSourceCatalogPackage.getPassageCard().solution == "Пиратская копия") {
                    val intent = Intent(this@MainActivity, LicenseActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    bundle.putString(
                        "deviceName",
                        dataSourceCatalogPackage.getPassageCard().deviceName
                    )
                    bundle.putString("requestPassage", resultScanInfoCard)
                    bundle.putString("solution", dataSourceCatalogPackage.getPassageCard().solution)
                    bundle.putString("capt", dataSourceCatalogPackage.getPassageCard().capt)
                    bundle.putString(
                        "numberOfPasses",
                        dataSourceCatalogPackage.getPassageCard().numberOfPasses
                    )
                    bundle.putString(
                        "datePasses",
                        dataSourceCatalogPackage.getPassageCard().datePasses
                    )
                    bundle.putString(
                        "passageBalance",
                        dataSourceCatalogPackage.getPassageCard().passageBalance
                    )
                    passageCardFragment.arguments = bundle
                    openFragment(passageCardFragment)
                }
            } else {
                url = "$bodyURL/spd-xml-api"
                urlPassage = "$bodyURL/monitor?script=True"
                updateInfo(konturController, dataSourceCard, url, messageInfoCard, resultScanInfoCard, numberKontur)
                if (dataSourceCard.getInfoCard().condition == "Пиратская копия") {
                    val intent = Intent(this@MainActivity, LicenseActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    bundle.putString("condition", dataSourceCard.getInfoCard().condition)
                    bundle.putString("number", dataSourceCard.getInfoCard().number)
                    bundle.putString("ruleOfUse", dataSourceCard.getInfoCard().ruleOfUse)
                    bundle.putString(
                        "permittedRates",
                        dataSourceCard.getInfoCard().permittedRates
                    )
                    bundle.putString("startAction", dataSourceCard.getInfoCard().startAction)
                    bundle.putString("endAction", dataSourceCard.getInfoCard().endAction)
                    bundle.putString("balance", dataSourceCard.getInfoCard().balance)
                    infoCardFragment.arguments = bundle
                    openFragment(infoCardFragment)
                }
            }
        }

        enableBeam()
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.add(Menu.NONE, 2, 2, "Ручной ввод")
//        menu?.add(Menu.NONE, 3, 2, "Управление устройствами")
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_about -> {
                val intent = Intent(this@MainActivity, AboutActivity::class.java)
                startActivity(intent)
                finish()
                return true
            }
            2 -> {
                val intent = Intent(this@MainActivity, ManualActivity::class.java)
                startActivity(intent)
                finish()
                return true
            }
//            3 -> {
//                val intent = Intent(this@MainActivity, DeviceManagementActivity::class.java)
//                startActivity(intent)
//                finish()
//                return true
//            }
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
            finish()
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
        messageInfoCard: String,
        number: String,
        numberKontur: String
    ) {
        runBlocking {
            launch(newSingleThreadContext("MyOwnThread")) {
                try {
                    messageAnswerKontur = konturController.requestPOST(url, messageInfoCard)
                    dataSourceCard.setMessageInfoCard(messageAnswerKontur, number, numberKontur)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun updatePassageCard(
        konturController: KonturController,
        dataSourceCatalogPackage: DataSourceCatalogPackage,
        urlPassage: String,
        url: String,
        messageBlockDevice: String,
        messagePassageCard: String,
        answerDevice: String,
        messageUnBlockDevice: String,
        messageInfoCard: String,
        numberKontur: String

    ) {
        runBlocking {
            launch(newSingleThreadContext("MyOwnThread")) {
                try {
                    konturController.requestPOST(urlPassage, messageBlockDevice)
                    var msg = konturController.requestPOST(urlPassage, messagePassageCard)
                    dataSourceCatalogPackage.setMessagePassageCard(msg, numberKontur)
                    msg = msg.substringAfter("<Message>")
                    msg = msg.substringBefore("</Message>")
                    msg = msg.replace("rPrior", "rFinal")
                    msg = "<?xml version=\"1.0\" encoding=\"Windows-1251\"?> " +
                            "<script>" +
                            "<Message>$msg</Message>" +
                            "</script>"
                    konturController.requestPOST(urlPassage, msg)
                    dataSourceCatalogPackage.setAnswerDevice(
                        konturController.requestPOST(
                            urlPassage,
                            answerDevice,
                        ),
                        numberKontur
                    )
                    konturController.requestPOST(urlPassage, messageUnBlockDevice)
                    dataSourceCatalogPackage.setInfoCard(
                        konturController.requestPOST(
                            url,
                            messageInfoCard
                        ),
                        numberKontur
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}