package com.ictis.neos.core

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.Image
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object ImageUtils: KoinComponent {
    private val fileService: FileService by inject()

    fun imageToBitmap(image: Image): Bitmap {
        val buffer: ByteBuffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.capacity())
        buffer.get(bytes)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, null)
    }

    fun rotate(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    fun saveToJpgCache(bitmap: Bitmap): File {
        val outputFile = fileService.getCacheFile("neos-char-sign-tmp.jpg")
        val outputStream = FileOutputStream(outputFile)

        bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)
        outputStream.flush()
        outputStream.close()

        return outputFile
    }

    @SuppressLint("NewApi")
    fun saveToJpgCameraFolder(bitmap: Bitmap) {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")
        val formatted = current.format(formatter)
        val outputStream = fileService.getCameraFolderFile("neos-$formatted.jpg")

        bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)
        outputStream.flush()
        outputStream.close()
    }
}