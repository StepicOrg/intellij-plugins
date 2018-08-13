package org.stepik.api.exceptions

open class StepikClientException : RuntimeException {
    constructor(message: String?) : super(message)
    
    constructor(message: String?, cause: Throwable?) : super(message, cause)
}
