package com.example.cropsense.model

data class PredictionResponse(
    val crop: String,
    val disease: String,
    val confidence: Float
)
