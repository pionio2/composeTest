package com.example.composetest.restful

import kotlinx.serialization.Serializable

@Serializable
data class Nature(
    val title: String,
    val location: String,
    val imageUrl: String
)
