package com.moneytree.app.common

import java.util.regex.Pattern

object EmailValidator {

    private const val EMAIL_PATTERN =
        "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"

    private val pattern = Pattern.compile(EMAIL_PATTERN)

    fun isValidEmail(email: String): Boolean {
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }
}