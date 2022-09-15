package ru.ertel.scannerqr.app.view

import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import me.dm7.barcodescanner.zbar.Result
import me.dm7.barcodescanner.zbar.ZBarScannerView
import org.koin.android.ext.android.inject
import org.koin.core.inject
import ru.ertel.scannerqr.gear.NfcAct
import ru.ertel.scannerqr.util.interfaces.NfcReadUtility
import java.math.BigInteger

class ScanCardActivity : NfcAct(), ZBarScannerView.ResultHandler {

    companion object {
        const val SCANINFOCARD = "SCANINFOCARD"
    }

    private lateinit var zbView: ZBarScannerView
    private val mNfcReadUtility: NfcReadUtility by inject()
    private var resScan = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        zbView = ZBarScannerView(this)
        zbView.setAutoFocus(true)
        zbView.flash = true
        setContentView(zbView)
        val fromCamera = intent.extras?.getString(SCANINFOCARD)
        resScan = fromCamera.toString()
    }

    override fun onResume() {
        super.onResume()
        zbView.setResultHandler(this)
        zbView.startCamera()
    }

    override fun onPause() {
        super.onPause()
        zbView.stopCamera()
    }

    override fun handleResult(result : Result?) {
        val intent = Intent(this@ScanCardActivity, MainActivity::class.java)
        if (resScan == "/") {
            intent.putExtra(SCANINFOCARD, "${result?.contents}/")
        } else {
            intent.putExtra(SCANINFOCARD, result?.contents)
        }
        startActivity(intent)
        finish()
    }

    public override fun onNewIntent(paramIntent: Intent) {
        super.onNewIntent(paramIntent)
        val dataFull = getMAC(intent.getParcelableExtra(NfcAdapter.EXTRA_TAG) as? Tag).replace(":", "")
        val decimalString = BigInteger(dataFull, 16).toString()
        val intent = Intent(this@ScanCardActivity, MainActivity::class.java)
        if (resScan == "/") {
            intent.putExtra(SCANINFOCARD, "${decimalString}/")
        } else {
            intent.putExtra(SCANINFOCARD, decimalString)
        }
        startActivity(intent)
        finish()
    }

    private fun getMAC(tag: Tag?): String =
        Regex("(.{2})").replace(
            String.format(
                "%0" + ((tag?.id?.size ?: 0) * 2).toString() + "X",
                BigInteger(1, tag?.id ?: byteArrayOf())
            ), "$1:"
        ).dropLast(1)
}