package ru.ertel.scannerqr.gear.interfaces

import android.nfc.FormatException
import ru.ertel.scannerqr.util.exceptions.InsufficientCapacityException
import ru.ertel.scannerqr.util.exceptions.ReadOnlyTagException
import ru.ertel.scannerqr.util.exceptions.TagNotPresentException
import ru.ertel.scannerqr.util.interfaces.NfcWriteUtility

interface OpCallback {
    /**
     * Executed on background thread, do NOT perform any UI logic here !
     * @return
     */
    @Throws(
        ReadOnlyTagException::class,
        InsufficientCapacityException::class,
        TagNotPresentException::class,
        FormatException::class
    )
    fun performWrite(writeUtility: NfcWriteUtility?): Boolean
}