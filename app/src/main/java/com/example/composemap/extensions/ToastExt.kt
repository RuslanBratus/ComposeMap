package com.example.composemap.extensions

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable

@Composable
fun Context.ToastShort(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}


fun Context.toastShort(messageResource: Int) {
    Toast.makeText(this, this.getText(messageResource), Toast.LENGTH_SHORT).show()
}