package org.stepik.core.courseFormat.stepHelpers

enum class Actions(val value: String) {
    AUTO_FIRST_ATTEMPT("auto_first_attempt"),
    ACTIVE("active"),
    GET_ATTEMPT("get_attempt"),
    GET_FIRST_ATTEMPT("get_first_attempt"),
    NEED_LOGIN("need_login"),
    SUBMIT("submit");

    override fun toString() = value

    fun contain(value: String?) = value == this.value
}
