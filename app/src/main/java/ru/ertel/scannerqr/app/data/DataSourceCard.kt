package ru.ertel.scannerqr.app.data

import ru.ertel.scannerqr.app.model.Card

class DataSourceCard {
    private val card = Card(
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        ""
    )

    private lateinit var numberTokenKontur: String

    fun setMessageInfoCard(message: String, number: String, numberKontur: String) {
        numberTokenKontur = numberKontur.substringAfterLast("*")
        if (getAnswerLicense(message)) {
            if (getValidMessage(message)) {
                card.condition = getCondition(message)
                card.number = number
                card.ruleOfUse = getRuleOfUse(message)
                card.permittedRates = getPermittedRates(message)
                card.startAction = getStartAction(message)
                card.endAction = getEndAction(message)
                card.balance = getBalance(message)
            } else {
                card.condition = "Данные не найдены"
                card.number = number
                card.ruleOfUse = "Данные не найдены"
                card.permittedRates = "Данные не найдены"
                card.startAction = "Данные не найдены"
                card.endAction = "Данные не найдены"
                card.balance = "Данные не найдены"
            }
        } else {
            card.condition = "Пиратская копия"
            card.number = "Пиратская копия"
            card.ruleOfUse = "Пиратская копия"
            card.permittedRates = "Пиратская копия"
            card.startAction = "Пиратская копия"
            card.endAction = "Пиратская копия"
            card.balance = "Пиратская копия"
        }
    }

    fun getInfoCard(): Card {
        return card
    }

    private fun getAnswerLicense(message: String): Boolean {
        // Проверка на лицензию, указывается номер лицензии контура,
        // при попытке использовать приложение на другом сервер,
        // поступит сообщение о использовании пиратской версии
        return message.contains("<attribute name=\"license\"  value=\"$numberTokenKontur\" />")
    }

    private fun getValidMessage(message: String): Boolean {
        var result = message.substringAfter("client oid=\"")
        result = result.substringBefore("\"")
        return result != "-"
    }

    private fun getCondition(message: String): String {
        var result = message.substringBefore("\"  valid=\"True\"  >")
        result = result.substringAfter("state=\"")
        return result
    }

    private fun getRuleOfUse(message: String): String {
        var result = message.substringAfter("current_rule=\"")
        result = result.substringBefore("\"  state")
        return result
    }

    private fun getPermittedRates(message: String): String {
        var result = message.substringAfter("tariff=\"")
        result = result.substringBefore("\"  permanent_rule")
        return result
    }

    private fun getStartAction(message: String): String {
        var result = message.substringAfter("valid_from=\"")
        result = result.substringBefore("\"  valid_to")
        return result
    }

    private fun getEndAction(message: String): String {
        var result = message.substringAfter("valid_to=\"")
        result = result.substringBefore("\"  category")
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