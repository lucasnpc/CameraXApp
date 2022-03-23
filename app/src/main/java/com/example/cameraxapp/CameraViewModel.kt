package com.example.cameraxapp

import android.graphics.Bitmap
import android.media.Image
import android.util.Log
import androidx.camera.core.ImageProxy
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CameraViewModel : ViewModel() {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    private val _recognizedState =  MutableStateFlow("")
    val recognizedStateFlow : StateFlow<String> = _recognizedState

    @androidx.camera.core.ExperimentalGetImage
    fun onFrameReceived(imageProxy: ImageProxy) {

        val frame = imageProxy.image
        val rotationDegrees = imageProxy.imageInfo.rotationDegrees

        if (frame == null) {
            imageProxy.close()
            return
        }
        recognizer
            .processFrame(frame, rotationDegrees)
            .addOnCompleteListener { imageProxy.close() }
            .addOnSuccessListener {
                Log.e("Processing", "On-device realtime result:$it")
            }
            .addOnFailureListener { Log.e("Error processing", it.message.toString()) }
    }

    fun onFrameCaptured(bitmap: Bitmap, rotationDegrees: Int) {
        recognizer
            .processCapturedFrame(bitmap, rotationDegrees)
            .addOnCompleteListener { bitmap.recycle() }
            .addOnSuccessListener {
                Log.e("Frame Captured", "On-device from capture result:$it")
                _recognizedState.value = it.toString()
            }
            .addOnFailureListener { Log.e("Error Captured", it.message.toString()) }
    }
}

private fun TextRecognizer.processFrame(
    frame: Image,
    rotationDegrees: Int
): Task<List<RecognizedLine>> {
    val inputImage = InputImage.fromMediaImage(frame, rotationDegrees)

    return this
        .process(inputImage)
        .continueWith { task ->
            task.result
                .textBlocks
                .flatMap { block -> block.lines }
                .map { line -> line.toRecognizedLine() }
        }
}

private fun TextRecognizer.processCapturedFrame(
    bitmap: Bitmap,
    rotationDegrees: Int
): Task<List<RecognizedLine>> {
    val inputImage = InputImage.fromBitmap(bitmap, rotationDegrees)

    return this
        .process(inputImage)
        .continueWith { task ->
            task.result
                .textBlocks
                .flatMap { block -> block.lines }
                .map { line -> line.toRecognizedLine() }
        }
}

private fun Text.Line.toRecognizedLine(): RecognizedLine =
    RecognizedLine(text)
