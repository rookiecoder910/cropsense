package com.example.cropsense

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import java.io.File
import java.io.FileOutputStream

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }
}

@Composable
fun MainScreen(viewModel: MainViewModel = viewModel()) {

    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    /* -------- Gallery Picker -------- */
    val imagePickerLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->
            imageUri = uri
        }

    /* -------- Camera Picker -------- */
    val cameraLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.TakePicturePreview()
        ) { bitmap ->
            bitmap?.let {
                imageUri = saveBitmap(context, it)
            }
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "CropSense",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(20.dp))

        Row {
            Button(onClick = {
                imagePickerLauncher.launch("image/*")
            }) {
                Text("Gallery")
            }

            Spacer(Modifier.width(12.dp))

            Button(onClick = {
                cameraLauncher.launch(null)
            }) {
                Text("Camera")
            }
        }

        Spacer(Modifier.height(20.dp))

        imageUri?.let {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = null,
                modifier = Modifier.size(220.dp)
            )

            Spacer(Modifier.height(12.dp))

            Button(onClick = {
                viewModel.uploadImage(context, it)
            }) {
                Text("Predict")
            }
        }

        Spacer(Modifier.height(20.dp))

        if (viewModel.loading.value) {
            CircularProgressIndicator()
        }

        viewModel.result.value?.let {
            Spacer(Modifier.height(16.dp))
            Text("Crop: ${it.crop}")
            Text("Disease: ${it.disease}")
            Text("Confidence: ${(it.confidence * 100).toInt()}%")
        }
    }
}

/* -------- Save Camera Bitmap -------- */

fun saveBitmap(context: android.content.Context, bitmap: Bitmap): Uri {
    val file = File(context.cacheDir, "camera_image.jpg")
    FileOutputStream(file).use {
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it)
    }
    return Uri.fromFile(file)
}
