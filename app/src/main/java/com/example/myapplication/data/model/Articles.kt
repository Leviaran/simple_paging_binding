package com.example.myapplication.data.model


import com.google.gson.annotations.SerializedName

data class Articles(
    @SerializedName("articles")
    val articles: List<Article> = listOf(),
    @SerializedName("status")
    val status: String = "",
    @SerializedName("totalResults")
    val totalResults: Int = 0
)