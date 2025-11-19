package com.example.myapplication.model

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val poster_path: String,
    val vote_average: Double,
    val genre_ids: List<Int>,
    var isFavorite: Boolean = false
)

data class MovieResponse(val results: List<Movie>)