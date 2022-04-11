package dev.iskandev.message_obtain_app.exception

class MessageNotFoundException(message: String? = null)
    : Exception(message)

class BadResponseStatusException(code: Int)
    : Exception("Bad response status code: $code")