package com.example.composemap.extensions

import android.os.Bundle

fun Bundle.getBooleanFromString(key: String, defaultValue : Boolean = false): Boolean {
    val text = this.getString(key)
    return if (text == null) defaultValue
    else text != "false"
}