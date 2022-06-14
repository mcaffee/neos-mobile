package com.ictis.neos.core

import android.content.ContentResolver
import android.content.ContentValues
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import com.ictis.neos.MainApplication
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class FileService {
    fun getCacheFile(name: String): File {
        val context = MainApplication.instance.applicationContext
        return File(context.cacheDir, name)
    }

    fun getCameraFolderFile(name: String): OutputStream {
        val context = MainApplication.instance.applicationContext

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver: ContentResolver = context.contentResolver
            val contentValues = ContentValues()
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
            contentValues.put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                Environment.DIRECTORY_DCIM.toString() + File.separator + "Camera")
            val imageUri =
                resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                    ?: throw IOException("Failed to create new MediaStore record.")
            return resolver.openOutputStream(imageUri)!!
        } else {
            val imagesDir = File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM).toString() + File.separator + "Camera")
            if (!imagesDir.exists()) imagesDir.mkdir()
            val imageFile = File(imagesDir, name)
            return FileOutputStream(imageFile)
        }
    }
}