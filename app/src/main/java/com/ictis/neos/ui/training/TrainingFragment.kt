package com.ictis.neos.ui.training

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.camera.view.PreviewView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.eddystudio.scrollpicker.OnItemSelectedListener
import com.eddystudio.scrollpicker.ScrollPickerAdapter
import com.eddystudio.scrollpicker.ScrollPickerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.core.KoinComponent
import com.ictis.neos.R
import com.ictis.neos.BR
import com.ictis.neos.camera.CameraService
import com.ictis.neos.common.CommonConstants
import com.ictis.neos.common.ScrollPickerViewModel
import com.ictis.neos.training.TrainingService

class TrainingFragment : Fragment(), KoinComponent {
    private val trainingService: TrainingService by inject()
    private val cameraService: CameraService by inject()

    private lateinit var textViewCountdown: TextView

    private var currentChar: String = CommonConstants.signChars[0]
    private var secondsRecorded: Int = 0

    lateinit var mainHandler: Handler
    private val recordingCountdownTask = object : Runnable {
        override fun run() {
            Log.d(TAG, "recordingCountdownTask: recorded seconds $secondsRecorded")
            if (secondsRecorded >= RECORDING_LENGTH) {
                textViewCountdown.text = null
                mainHandler.removeCallbacks(this)
                cameraService.stopRecording()
            } else {
                secondsRecorded += 1
                textViewCountdown.text = (RECORDING_LENGTH - secondsRecorded).toString()
                mainHandler.postDelayed(this, 1000)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate fragment layout
        val root = inflater.inflate(R.layout.fragment_training, container, false)

        // Init camera
        cameraService.init(requireContext(), this as LifecycleOwner, this::onCameraBound)

        // FABs
        val fabCamera: FloatingActionButton = root.findViewById(R.id.fab_camera)
        fabCamera.setOnClickListener { startRecording() }

        // Countdown text
        textViewCountdown = root.findViewById(R.id.text_view_countdown)

        // Scroll picker
        val scrollPickerView = root.findViewById<ScrollPickerView<ScrollPickerViewModel>>(R.id.scrollpickerview)
        val scrollPickerAdapter = ScrollPickerAdapter(
            CommonConstants.signCharViewModelList, R.layout.scrollpicker_item_sign, BR.viewmodel
        )
        ScrollPickerView.Builder(scrollPickerView)
            .scrollViewAdapter(scrollPickerAdapter)
            .onItemSelectedListener(object : OnItemSelectedListener {
                override fun onSelected(view: View, layoutPosition: Int) {
                    val itemText = CommonConstants.signCharViewModelList[layoutPosition].index
                    Log.d(TAG, "scrollViewAdapter.onItemSelectedListener.onSelected: Item selected: $itemText")
                    currentChar = itemText
                }
            })
            .build()

        // Background shot handler
        mainHandler = Handler(Looper.getMainLooper())

        return root
    }

    private fun onCameraBound() {
        val previewView: PreviewView = requireView().findViewById(R.id.preview_view)
        cameraService.preview.setSurfaceProvider(previewView.surfaceProvider)
    }

    private fun startRecording() {
        Log.d(TAG, "startRecording: starting recording")
        mainHandler.post(recordingCountdownTask)

        cameraService.startRecording(
            {
                videoFile ->
                println("Saving Image ${videoFile.absolutePath}")

                // send training data
                GlobalScope.launch(Dispatchers.IO) {
                    trainingService.sendUserCharacterSign(currentChar, videoFile)
                }
            },
            {
                videoCaptureError, message, cause ->
                Log.e(TAG,"startRecording: could not record video: $message", cause)
            }
        )
    }

    companion object {
        private const val TAG = "TrainingFragment"
        private const val RECORDING_LENGTH = 30
    }
}
