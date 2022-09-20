package ru.ertel.scannerqr.app.view

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import me.dm7.barcodescanner.zbar.Result
import me.dm7.barcodescanner.zbar.ZBarScannerView
import ru.ertel.scannerqr.app.R
import ru.ertel.scannerqr.gear.NfcAct
import java.math.BigInteger

class ScanCardActivity : NfcAct(), ZBarScannerView.ResultHandler {

    companion object {
        const val SCANINFOCARD = "SCANINFOCARD"
    }

    private lateinit var zbView: ZBarScannerView
    private var resScan = ""
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        zbView = ZBarScannerView(this)
        zbView.setAutoFocus(true)
        zbView.flash = true
        setContentView(zbView)
        val fromCamera = intent.extras?.getString(SCANINFOCARD)
        mediaPlayer = MediaPlayer.create(this, R.raw.payment_succes)
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
        vibroFone()
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
        vibroFone()
        val intent = Intent(this@ScanCardActivity, MainActivity::class.java)
        if (resScan == "/") {
            intent.putExtra(SCANINFOCARD, "${decimalString}/")
        } else {
            intent.putExtra(SCANINFOCARD, decimalString)
        }
        zbView.flash = false
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

    private fun vibroFone() {
        val vibrator = this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val canVibrate: Boolean = vibrator.hasVibrator()
        val milliseconds = 300L
        mediaPlayer.start()
        if (canVibrate) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // API 26
                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        milliseconds,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            } else {
                // This method was deprecated in API level 26
                vibrator.vibrate(milliseconds)
            }
        }
    }
}