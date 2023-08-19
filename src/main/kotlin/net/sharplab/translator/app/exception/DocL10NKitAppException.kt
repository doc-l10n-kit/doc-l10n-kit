package net.sharplab.translator.app.exception

class DocL10NKitAppException : RuntimeException {
    constructor(message: String?, cause: Throwable?) : super(message, cause)

    constructor(message: String?) : super(message)
}