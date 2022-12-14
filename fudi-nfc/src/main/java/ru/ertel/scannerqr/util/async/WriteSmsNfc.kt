package ru.ertel.scannerqr.util.async

import android.content.Intent
import android.nfc.FormatException
import ru.ertel.scannerqr.gear.interfaces.OpCallback
import ru.ertel.scannerqr.gear.interfaces.TaskCallback
import ru.ertel.scannerqr.util.exceptions.InsufficientCapacityException
import ru.ertel.scannerqr.util.exceptions.ReadOnlyTagException
import ru.ertel.scannerqr.util.exceptions.TagNotPresentException
import ru.ertel.scannerqr.util.interfaces.NfcWriteUtility

class WriteSmsNfc : Nfc {
    /**
     * Instantiates a new WriteSmsNfc.
     *
     * @param taskCallback the async ui callback
     */
    constructor(taskCallback: TaskCallback?) : super(taskCallback) {}

    /**
     * Instantiates a new WriteSmsNfc.
     *
     * @param taskCallback the async ui callback
     * @param opCallback the async operation callback
     */
    constructor(taskCallback: TaskCallback?, opCallback: OpCallback) : super(
        taskCallback,
        opCallback
    ) {
    }

    /**
     * Instantiates a new WriteSmsNfc.
     *
     * @param taskCallback the async ui callback
     * @param opCallback the async operation callback
     * @param nfcWriteUtility the nfc write utility
     */
    constructor(
        taskCallback: TaskCallback?,
        opCallback: OpCallback,
        nfcWriteUtility: NfcWriteUtility
    ) : super(taskCallback, opCallback, nfcWriteUtility) {
    }

    override fun executeWriteOperation(intent: Intent?, vararg args: Any?) {
        if (checkStringArguments(args.javaClass) || args.size != 2 || intent == null) {
            throw UnsupportedOperationException("Invalid arguments")
        }
        asyncOperationCallback = object : OpCallback {
            @Throws(
                ReadOnlyTagException::class,
                InsufficientCapacityException::class,
                TagNotPresentException::class,
                FormatException::class
            )
            override fun performWrite(writeUtility: NfcWriteUtility?) = (writeUtility?.apply {
                writeSmsToTagFromIntent(args[0] as String, args[1] as String, intent)
            } ?: false) as Boolean
        }
        super.executeWriteOperation()
    }
}