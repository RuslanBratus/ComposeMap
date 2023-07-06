package com.example.composemap.extensions

import java.io.File

fun generateImageName(latitude: String, longitude: String): String {
    val millis = System.currentTimeMillis()
    val firstWord = if (latitude.length > 5) latitude.substring(0, 6)
    else latitude.substring(0, latitude.length)
    val secondWord = if (longitude.length > 5) longitude.substring(0, 6)
    else longitude.substring(0, longitude.length)
    return firstWord.plus(secondWord).plus(millis)
}

fun deleteMarkerImage(filePath: String): Boolean {
    val myFile = File(filePath)
    return myFile.delete()
}