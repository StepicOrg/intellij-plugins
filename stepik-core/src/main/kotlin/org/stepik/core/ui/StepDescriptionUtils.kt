package org.stepik.core.ui

import org.stepik.api.objects.submissions.Attachment
import org.stepik.api.objects.submissions.Choice
import org.stepik.api.objects.submissions.Column
import org.stepik.api.objects.submissions.Reply
import org.stepik.core.courseFormat.StepNode
import org.stepik.core.courseFormat.StepType
import org.w3c.dom.html.HTMLInputElement
import org.w3c.dom.html.HTMLSelectElement
import org.w3c.dom.html.HTMLTextAreaElement
import java.util.*
import java.util.Collections.emptyList
import java.util.function.Consumer

internal object StepDescriptionUtils {

    fun getReply(
            stepNode: StepNode,
            type: StepType,
            elements: Elements,
            data: String?): Reply? {
        val reply = Reply()
        when (type) {
            StepType.CHOICE -> {
                val choices = getChoiceData(elements)
                reply.setChoices(choices)
            }
            StepType.STRING -> {
                val text = getStringData(elements)
                reply.setText(text)
            }
            StepType.FREE_ANSWER -> {
                val text = getStringData(elements)
                reply.setText(text)
                reply.setAttachments(emptyList<Attachment>())
            }
            StepType.NUMBER -> {
                val number = getStringData(elements)
                reply.setNumber(number)
            }
            StepType.DATASET -> {
                val dataset: String = data ?: getStringData(elements)
                reply.setFile(dataset)
            }
            StepType.TABLE -> {
                val tableChoices = getChoices(elements)
                reply.setChoices(tableChoices)
            }
            StepType.FILL_BLANKS -> {
                val blanks = getBlanks(elements)
                reply.setBlanks(blanks)
            }
            StepType.SORTING, StepType.MATCHING -> {
                val ordering = getOrderingData(elements)
                reply.setOrdering(ordering)
            }
            StepType.MATH -> {
                val formula = getStringData(elements)
                reply.setFormula(formula)
            }
            else -> return null
        }
        stepNode.lastReply = reply
        return reply
    }

    private fun forEachInputElement(elements: Elements, consumer: Consumer<HTMLInputElement>) {
        for (node in elements) {
            if (node is HTMLInputElement) {
                consumer.accept(node)
                node.disabled = true
            }
        }
    }

    private fun disableAllInputs(elements: Elements) {
        for (node in elements) {
            if (node is HTMLInputElement) {
                node.disabled = true
            } else if (node is HTMLTextAreaElement) {
                node.disabled = true
            }
        }
    }

    private fun getChoiceData(elements: Elements): List<Boolean> {
        val choices = ArrayList<Boolean>()

        forEachInputElement(elements, Consumer {
            if (it.name == "option") {
                choices.add(it.checked)
            }
        })

        return choices
    }

    private fun getOrderingData(elements: Elements): List<Int> {
        val ordering = ArrayList<Int>()

        forEachInputElement(elements, Consumer {
            if (it.name == "index") {
                val indexAttr = it.value
                ordering.add(Integer.valueOf(indexAttr))
            }
        })
        return ordering
    }

    private fun getStringData(elements: Elements): String {
        disableAllInputs(elements)
        return elements.getInputValue("text")
    }

    private fun getChoices(elements: Elements): List<Choice> {
        val choices = HashMap<String, MutableList<Column>>()
        val rows = ArrayList<String>()

        forEachInputElement(elements, Consumer {
            val type = it.type

            if (type in listOf("checkbox", "radio")) {
                val columns = choices.computeIfAbsent(it.name) {
                    rows.add(it)
                    mutableListOf()
                }
                val column = Column(it.value, it.checked)
                columns.add(column)
            }
        })

        return choices.entries
                .map { entry -> Choice(entry.key, entry.value) }
                .sortedBy { rows.indexOf(it.nameRow) }
    }

    private fun getBlanks(elements: Elements): List<String> {
        val blanks = ArrayList<String>()

        for (node in elements) {
            if (node is HTMLInputElement) {
                if (node.type == "text") {
                    blanks.add(node.value)
                }
                node.disabled = true
            } else if (node is HTMLSelectElement) {
                blanks.add(node.value)
                node.disabled = true
            }
        }

        return blanks
    }
}
