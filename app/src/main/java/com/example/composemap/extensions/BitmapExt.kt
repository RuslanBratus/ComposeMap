package com.example.composemap.extensions

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.composemap.presentation.utils.Utils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception

fun Context.bitmapFromLocalStorage(path: String,
                                   imageWidth: Int,
                                   imageHeight: Int,
                                   doOnResourceReady: (bitmap: Bitmap) -> Unit,
                                   donOnCleared: () -> Unit = {  } ) {
    Glide.with(this)
        .asBitmap()
        .load(path)
        .into(object: CustomTarget<Bitmap>(imageWidth, imageHeight){
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                doOnResourceReady(resource)
            }

            override fun onLoadCleared(placeholder: Drawable?) {
                donOnCleared()
            }
        })
}

fun Context.getBitmapFromUri(uri: Uri, doOnException : () -> Unit): Bitmap? {
     return try {
         if (Build.VERSION.SDK_INT < 28) {
             MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
         } else {
             val source = ImageDecoder.createSource(this.contentResolver, uri)
             ImageDecoder.decodeBitmap(source)
         }
     } catch (E: Exception) {
         doOnException()
         null
     }
}

fun Context.saveMarkerImageInternally(bitmapImage: Bitmap, latitude: Double, longitude: Double,
                                      doOnException: () -> Unit): String? {
    val imageName = generateImageName(latitude.toString(), longitude.toString())
    val cw = ContextWrapper(this.applicationContext)
    val directory = cw.getDir(Utils.MARKERS_IMAGES_DIRECTORY_NAME, Context.MODE_PRIVATE)
    val mypath = File(directory, imageName)
    var fos: FileOutputStream? = null
    try {
        fos = FileOutputStream(mypath)
        bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos)
    } catch (e: java.lang.Exception) {
        doOnException()
        return null
    } finally {
        try {
            fos!!.close()
        } catch (e: IOException) {
            doOnException()
            return null
        }
    }
    return directory.absolutePath.plus("/").plus(imageName)
}

