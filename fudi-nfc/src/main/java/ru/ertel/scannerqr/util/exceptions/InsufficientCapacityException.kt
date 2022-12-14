package ru.ertel.scannerqr.util.exceptions

class InsufficientCapacityException : Exception {
    constructor() {}
    constructor(message: String?) : super(message) {}
    constructor(stackTraceElements: Array<StackTraceElement?>?) {
        stackTrace = stackTraceElements
    }
}