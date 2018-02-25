package org.stepik.api.objects.steps

import java.util.*


class Sample : ArrayList<String>() {

    var input: String
        get() {
            if (isEmpty()) {
                return ""
            }
            return first()
        }
        set(value) {
            if (isEmpty()) {
                add(value)
            } else {
                set(0, value)
            }
        }

    var output: String
        get() {
            if (size < 2) {
                return ""
            }
            return get(1)
        }
        set(value) {
            when {
                isEmpty() -> {
                    add("")
                }
                size == 1 -> add(value)
                else -> set(1, value)
            }
        }
}
