package com.example.cameraxapp.main

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import com.example.cameraxapp.CameraActivity

class MainViewModel : ViewModel() {
    fun startCameraActivity(context: Context) {
        context.startActivity(Intent(context, CameraActivity::class.java))
    }
}