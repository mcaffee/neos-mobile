package com.ictis.neos.recognition

import java.io.FileNotFoundException
import android.app.Activity
import android.graphics.Bitmap
import android.util.Log
import com.ictis.neos.MainApplication
import com.ictis.neos.R
import org.koin.core.KoinComponent
import org.tensorflow.lite.DataType
import org.tensorflow.lite.InterpreterApi
import org.tensorflow.lite.InterpreterFactory
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.TensorProcessor
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.label.TensorLabel
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import com.ictis.neos.common.CommonConstants
import com.ictis.neos.core.ImageUtils
import java.io.File

class RecognitionService : KoinComponent {
    private lateinit var model: InterpreterApi

    fun loadModel(activity: Activity, path: String? = null) {

        try {
            model = if (path == null) {
                Log.d(TAG, "loadModel: loading the default model")
                val modelFile = FileUtil.loadMappedFile(activity, "model-base-220522.tflite")
                InterpreterFactory().create(modelFile, InterpreterApi.Options())
            } else {
                Log.d(TAG, "loadModel: loading user model from $path")
                val modelFile = File(path)
                InterpreterFactory().create(modelFile, InterpreterApi.Options())
            }

        } catch (e: FileNotFoundException) {
            Log.d(TAG, "loadModel: user model file is missing, loading the default, you can download it again")
            val modelFile = FileUtil.loadMappedFile(activity, "model-main.tflite")
            model = InterpreterFactory().create(modelFile, InterpreterApi.Options())
        }
    }


    fun getSuggestions(bitmap: Bitmap): Array<String> {

        var tensorImage = TensorImage(DataType.UINT8)
        val imageProcessor: ImageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(224, 224, ResizeOp.ResizeMethod.BILINEAR))
            .build()

        // DEBUG: dump files sent to classifier to Camera folder
//        val context = MainApplication.instance.applicationContext
//        if (context.getString(R.string.build_type) == "RELEASE") {
//            ImageUtils.saveToJpgCameraFolder(bitmap)
//            Log.d(TAG, "DEBUG: dumped CNN input file")
//        }

        tensorImage.load(bitmap)
        tensorImage = imageProcessor.process(tensorImage)

        val probabilityBuffer = TensorBuffer.createFixedSize(intArrayOf(1, 30), DataType.FLOAT32)

        model.run(tensorImage.buffer, probabilityBuffer.buffer)

        val probabilityProcessor = TensorProcessor.Builder().add(NormalizeOp(0f, 1f)).build()

        val labels = TensorLabel(CommonConstants.signChars, probabilityProcessor.process(probabilityBuffer))
        val floatMap = labels.mapWithFloatValue

        val predsSorted = sortPredictions(floatMap)
        val chosenPreds = choosePredictions(predsSorted)
        val matchingLabels = predsToLabels(chosenPreds)

        for ((key, value) in predsSorted) {
            println("$key = $value")
        }

        return matchingLabels.toTypedArray()
    }

    private fun sortPredictions(preds: Map<String, Float>): Map<String, Float> {
        return preds.toList().sortedBy { (_, value) -> value}.reversed().toMap()
    }

    private fun choosePredictions(preds: Map<String, Float>, threshold: Float = .1f): Map<String, Float> {
        return preds.toList().filter { (_, value) -> value >= threshold}.toMap()
    }

    private fun predsToLabels(preds: Map<String, Float>): List<String> {
        return preds.toList().map { (key, _) -> key}
    }

    companion object {
        private const val TAG = "RecognitionService"
    }
}