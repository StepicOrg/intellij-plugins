package org.stepik.core.common

import com.intellij.openapi.diagnostic.Logger

interface Loggable {
    val logger
        get() = Logger.getInstance(javaClass.name)
}
