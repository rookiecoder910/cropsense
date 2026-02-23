package com.example.cropsense.model
//
data class PredictionResponse(
    val is_leaf: Boolean? = true,
    val message: String? = null,
    val crop: String? = null,
    val disease: String? = null,
    val confidence: Float? = null
)
