package org.stepik.core.ui

import org.w3c.dom.Node
import org.w3c.dom.html.HTMLCollection
import org.w3c.dom.html.HTMLInputElement
import org.w3c.dom.html.HTMLTextAreaElement

internal class Elements(private val elements: HTMLCollection) : Iterable<Node> {

    val action: String
        get() = (elements.namedItem("action") as? HTMLInputElement)?.value ?: ""

    val type: String
        get() = (elements.namedItem("type") as? HTMLInputElement)?.value ?: ""

    val isFromFile: Boolean
        get() = (elements.namedItem("isFromFile") as? HTMLInputElement)?.value == "true"

    val attemptId: Long
        get() = getInputValue("attemptId").toLongOrNull() ?: 0L

    val isLocked: Boolean
        get() = getInputValue("locked").toBoolean()

    fun getInputValue(name: String): String {
        val item = elements.namedItem(name)
        return when (item) {
            is HTMLInputElement -> item.value
            is HTMLTextAreaElement -> item.value
            else -> ""
        }
    }

    override fun iterator(): Iterator<Node> {
        return object : Iterator<Node> {
            private val size = elements.length
            private var index: Int = 0

            override fun hasNext(): Boolean {
                return index < size
            }

            override fun next(): Node {
                return elements.item(index++)
            }
        }
    }
}
