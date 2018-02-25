package org.stepik.api.client

object StatusCodes {
    // --- 1xx Informational ---

    const val SC_CONTINUE = 100
    const val SC_SWITCHING_PROTOCOLS = 101
    const val SC_PROCESSING = 102

    // --- 2xx Success ---

    const val SC_OK = 200
    const val SC_CREATED = 201
    const val SC_ACCEPTED = 202
    const val SC_NON_AUTHORITATIVE_INFORMATION = 203
    const val SC_NO_CONTENT = 204
    const val SC_RESET_CONTENT = 205
    const val SC_PARTIAL_CONTENT = 206
    const val SC_MULTI_STATUS = 207

    // --- 3xx Redirection ---

    const val SC_MULTIPLE_CHOICES = 300
    const val SC_MOVED_PERMANENTLY = 301
    const val SC_MOVED_TEMPORARILY = 302
    const val SC_SEE_OTHER = 303
    const val SC_NOT_MODIFIED = 304
    const val SC_USE_PROXY = 305
    const val SC_TEMPORARY_REDIRECT = 307

    // --- 4xx Client Error ---

    const val SC_BAD_REQUEST = 400
    const val SC_UNAUTHORIZED = 401
    const val SC_PAYMENT_REQUIRED = 402
    const val SC_FORBIDDEN = 403
    const val SC_NOT_FOUND = 404
    const val SC_METHOD_NOT_ALLOWED = 405
    const val SC_NOT_ACCEPTABLE = 406
    const val SC_PROXY_AUTHENTICATION_REQUIRED = 407
    const val SC_REQUEST_TIMEOUT = 408
    const val SC_CONFLICT = 409
    const val SC_GONE = 410
    const val SC_LENGTH_REQUIRED = 411
    const val SC_PRECONDITION_FAILED = 412
    const val SC_REQUEST_TOO_LONG = 413
    const val SC_REQUEST_URI_TOO_LONG = 414
    const val SC_UNSUPPORTED_MEDIA_TYPE = 415
    const val SC_REQUESTED_RANGE_NOT_SATISFIABLE = 416
    const val SC_EXPECTATION_FAILED = 417
    const val SC_INSUFFICIENT_SPACE_ON_RESOURCE = 419
    const val SC_METHOD_FAILURE = 420
    const val SC_UNPROCESSABLE_ENTITY = 422
    const val SC_LOCKED = 423
    const val SC_FAILED_DEPENDENCY = 424

    // --- 5xx Server Error ---

    const val SC_INTERNAL_SERVER_ERROR = 500
    const val SC_NOT_IMPLEMENTED = 501
    const val SC_BAD_GATEWAY = 502
    const val SC_SERVICE_UNAVAILABLE = 503
    const val SC_GATEWAY_TIMEOUT = 504
    const val SC_HTTP_VERSION_NOT_SUPPORTED = 505
    const val SC_INSUFFICIENT_STORAGE = 507
}
