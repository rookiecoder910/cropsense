package com.example.cropsense

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cropsense.Retrofit.RetrofitClient
import com.example.cropsense.model.PredictionResponse
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class MainViewModel : ViewModel() {

    val result = mutableStateOf<PredictionResponse?>(null)
    val loading = mutableStateOf(false)

    fun uploadImage(context: Context, uri: Uri) {
        viewModelScope.launch {
            loading.value = true
            try {
                val imagePart = uriToMultipart(context, uri)
                val response =
                    RetrofitClient.api.predictDisease(imagePart)

                if (response.isSuccessful) {
                    result.value = response.body()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            loading.value = false
        }
    }
}

/* -------- IMAGE → MULTIPART -------- */

fun uriToMultipart(
    context: Context,
    uri: Uri
): MultipartBody.Part {

    val inputStream = context.contentResolver.openInputStream(uri)
    val file = File(context.cacheDir, "image.jpg")

    file.outputStream().use {
        inputStream?.copyTo(it)
    }

    val requestBody =
        file.asRequestBody("image/*".toMediaType())

    return MultipartBody.Part.createFormData(
        "file",          // MUST be "file" for FastAPI
        file.name,
        requestBody
    )
}
