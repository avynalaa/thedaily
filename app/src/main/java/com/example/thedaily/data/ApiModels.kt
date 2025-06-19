package com.example.thedaily.data

import kotlinx.serialization.Serializable

@Serializable
data class ModelListResponse(
    val data: List<Model>
)

@Serializable
data class Model(
    val id: String
)