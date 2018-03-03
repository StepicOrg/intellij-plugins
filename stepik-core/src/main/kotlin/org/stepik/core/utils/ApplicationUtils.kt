package org.stepik.core.utils

import com.intellij.openapi.application.Application
import com.intellij.openapi.util.Computable

fun <T> Application.runWriteActionAndWait(block: () -> T?): T? {
    var result: T? = null
    invokeAndWait {
        result = runWriteAction(Computable<T> {
            block.invoke()
        })
    }
    return result
}

fun Application.runWriteActionLater(block: () -> Unit) {
    invokeLater {
        runWriteAction {
            block.invoke()
        }
    }
}
