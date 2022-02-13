package com.example.cameraxapp.main.components

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cameraxapp.main.MainViewModel

@Composable
fun MainContent(viewModel: MainViewModel, context: Context) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Welcome to CameraX Enviroment Reading App")
            Spacer(modifier = Modifier.height(10.dp))
            Button(onClick = { viewModel.startCameraActivity(context) }) {
                Text(text = "Start the Camera")
            }
        }
    }
}