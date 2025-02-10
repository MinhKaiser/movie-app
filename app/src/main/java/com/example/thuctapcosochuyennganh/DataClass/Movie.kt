package com.example.thuctapcosochuyennganh.DataClass

import java.util.Date
data class Movie(
    val movieId: String,
    val title: String,
    val description: String,
    val director: String,
    val releaseYear: Int,
    val genres: List<String>,
    val view: Int,
    val rating: Double,
    val image: String,
)

