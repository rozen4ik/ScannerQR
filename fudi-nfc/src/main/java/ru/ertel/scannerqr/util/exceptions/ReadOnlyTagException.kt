package ru.ertel.scannerqr.util.exceptions

class ReadOnlyTagException : TagNotWritableException {
    constructor() {}
    constructor(message: String?) : super(message) {}
}