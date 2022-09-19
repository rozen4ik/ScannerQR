package ru.ertel.scannerqr.app.data

import android.util.Log
import ru.ertel.scannerqr.app.model.CatalogPackage

class DataSourceCatalogPackage {
    private val catalogPackage: CatalogPackage = CatalogPackage(
        "",
        "",
        "",
        "",
        "",
        "",
        ""
    )

    fun setMessagePassageCard(message: String) {
        if (getValidMessage(message)) {
            catalogPackage.solution = getSolution(message)
            catalogPackage.capt = getCapt(message)
        } else {
            catalogPackage.solution = "Данные не найдены"
            catalogPackage.capt = "Данные не найдены"
        }
    }

    fun setInfoCard(message: String) {
        if (getValidInfoCard(message)) {
            catalogPackage.numberOfPasses = getNumberOfPasses(message)
            catalogPackage.passageBalance = getBalance(message)
        } else {
            catalogPackage.numberOfPasses = "Данные не найдены"
            catalogPackage.passageBalance = "Данные не найдены"
        }
    }

    fun setAnswerDevice(message: String) {
        if (getValidMessageAnswer(message)) {
            catalogPackage.deviceName = getDeviceName(message)
            catalogPackage.datePasses = getDatePasses(message)
        } else {
            catalogPackage.deviceName = "Данные не найдены"
            catalogPackage.datePasses = "Данные не найдены"
        }
    }

    fun getPassageCard(): CatalogPackage {
        return catalogPackage
    }

    private fun getValidMessage(message: String): Boolean {
        return message.contains("<Message>", ignoreCase = true)
    }

    private fun getValidInfoCard(message: String): Boolean {
        return message.contains("<package id", ignoreCase = true)
    }

    private fun getValidMessageAnswer(message: String): Boolean {
        return message.contains("<msg", ignoreCase = true)
    }

    private fun getDeviceName(message: String): String {
        var result = message.substringBefore("\" Unit=")
        result = result.substringAfter("<State Name=\"")
        return result
    }

    private fun getSolution(message: String): String {
        var result = message.substringAfter("<Reason>")
        result = result.substringBefore("</Reason>")
        return result
    }

    private fun getCapt(message: String): String {
        var result = ""
        if (message.contains("cpRuleUse", ignoreCase = true)) {
            Log.d("TAG", message)
            result =
                message.substringAfter("<Parameter name=\"cpRuleUse\" type=\"String\" values=\"")
            result = result.substringBefore("\">")
            result = result.replace("capt=", "")
        } else {
            result = "Данные не найдены"
        }
        return result
    }

    private fun getNumberOfPasses(message: String): String {
        var use_count = message.substringAfter("use_count=\"")
        use_count = use_count.substringBefore("\"  description")
        var used_count = message.substringAfter("used_count=\"")
        used_count = used_count.substringBefore("\"  eid")
        val result: Int = use_count.toInt() - used_count.toInt()
        return result.toString()
    }

    private fun getDatePasses(message: String): String {
        var result = message.substringAfter("time=\"")
        result = result.substringBefore("\" text=")
        return result
    }

    private fun getBalance(message: String): String {
        if (message.contains("<currency name=\"RUB\"", ignoreCase = true)) {
            var result = message.substringAfter("\"Российский рубль\"  value=\"")
            result = result.substringBefore("\" />")
            return "$result(RUB)"
        } else {
            return "Данные не найдены"
        }
    }
}