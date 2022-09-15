package ru.ertel.scannerqr.app.model

data class Card(
    var activity: String,
    var condition: String,
    var number: String,
    var ruleOfUse: String,
    var permittedRates: String,
    var startAction: String,
    var endAction: String
    )
