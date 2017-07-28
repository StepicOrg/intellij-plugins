package org.stepik.core.courseFormat.stepHelpers

enum class Actions(val value: String) {
    GET_ATTEMPT("get_attempt"),
    GET_FIRST_ATTEMPT("get_first_attempt"),
    NEED_LOGIN("need_login"),
    SUBMIT("submit"),
    NOTHING("");

    override fun toString() = value
}
