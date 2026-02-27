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
    val error = mutableStateOf<String?>(null)

    fun uploadImage(context: Context, uri: Uri) {
        viewModelScope.launch {
            // Clear stale state before a new analysis
            result.value = null
            error.value = null
            loading.value = true

            try {
                val imagePart = uriToMultipart(context, uri)
                val response = RetrofitClient.api.predictDisease(imagePart)

                if (response.isSuccessful) {
                    result.value = response.body()
                } else {
                    error.value = "Server error (${response.code()}). Please try again."
                }
            } catch (e: java.net.UnknownHostException) {
                error.value = "No internet connection. Please check your network."
            } catch (e: java.net.SocketTimeoutException) {
                error.value = "Request timed out. The server may be starting up — try again in a moment."
            } catch (e: Exception) {
                error.value = "Something went wrong. Please try again."
                e.printStackTrace()
            } finally {
                loading.value = false
            }
        }
    }

    fun clearAll() {
        result.value = null
        error.value = null
    }
}


fun uriToMultipart(
    context: Context,
    uri: Uri
): MultipartBody.Part {
    val file = File(context.cacheDir, "upload_${System.currentTimeMillis()}.jpg")

    context.contentResolver.openInputStream(uri)?.use { input ->
        file.outputStream().use { output ->
            input.copyTo(output)
        }
    } ?: throw IllegalStateException("Unable to read selected image")

    val requestBody = file.asRequestBody("image/*".toMediaType())

    return MultipartBody.Part.createFormData(
        "file",
        file.name,
        requestBody
    )
}
