package com.example.myapplication.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_movies")
data class Movie(
    @PrimaryKey
    val id: Int,
    val title: String,
    val overview: String,
    val poster_path: String,
    val vote_average: Double,
    val genre_ids: List<Int>,
    var isFavorite: Boolean = false
)

data class MovieResponse(val results: List<Movie>)