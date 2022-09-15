package ru.ertel.scannerqr.util.async

import android.content.Intent
import android.os.AsyncTask
import ru.ertel.scannerqr.gear.GenericTask
import ru.ertel.scannerqr.gear.interfaces.OpCallback
import ru.ertel.scannerqr.gear.interfaces.TaskCallback
import ru.ertel.scannerqr.util.interfaces.NfcToOperation
import ru.ertel.scannerqr.util.interfaces.NfcWriteUtility

abstract class Nfc : NfcToOperation {
    protected var nfcWriteUtility: NfcWriteUtility? = null
    protected var asyncOperationCallback: OpCallback? = null
    protected var asyncUiCallback: TaskCallback? = null

    constructor(taskCallback: TaskCallback?) {
        asyncUiCallback = taskCallback
    }

    constructor(taskCallback: TaskCallback?, nfcWriteUtility: NfcWriteUtility?) {
        asyncUiCallback = taskCallback
        this.nfcWriteUtility = nfcWriteUtility
    }

    constructor(taskCallback: TaskCallback?, opCallback: OpCallback) : this(taskCallback) {
        asyncOperationCallback = opCallback
    }

    constructor(
        taskCallback: TaskCallback?, opCallback: OpCallback,
        nfcWriteUtility: NfcWriteUtility
    ) : this(taskCallback, nfcWriteUtility) {
        asyncOperationCallback = opCallback
    }

    override fun executeWriteOperation() {
        nfcWriteUtility?.let {
            asyncOperationCallback?.let { callback ->
                GenericTask(asyncUiCallback, callback, it)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
            }
        } ?: run {
            asyncOperationCallback?.let { callback ->
                GenericTask(asyncUiCallback, callback)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
            }
        }
    }

    abstract override fun executeWriteOperation(intent: Intent?, vararg args: Any?)
    protected fun checkStringArguments(type: Class<*>): Boolean {
        return type == Array<String>::class.java
    }

    protected fun checkDoubleArguments(type: Class<*>): Boolean {
        return type == Array<Double>::class.java
    }
}