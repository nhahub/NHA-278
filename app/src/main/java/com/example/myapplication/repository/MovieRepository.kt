package com.example.myapplication.repository

import androidx.lifecycle.LiveData
import com.example.myapplication.database.MovieDao
import com.example.myapplication.model.Movie
import com.example.myapplication.network.Genre
import com.example.myapplication.network.MovieDetails
import com.example.myapplication.network.TMDbApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MovieRepository(private val movieDao: MovieDao? = null) {
    private val tmdbApi: TMDbApi
    private var genres: List<Genre>? = null

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        tmdbApi = retrofit.create(TMDbApi::class.java)
    }

    suspend fun getPopularMovies(apiKey: String, page: Int): List<Movie> {
        return tmdbApi.getPopularMovies(apiKey, page).results
    }

    suspend fun getGenres(apiKey: String): List<Genre> {
        if (genres == null) {
            genres = tmdbApi.getGenres(apiKey).genres
        }
        return genres!!
    }

    suspend fun getMovieDetails(apiKey: String, movieId: Int): MovieDetails {
        return tmdbApi.getMovieDetails(movieId, apiKey)
    }
    suspend fun addFavorite(movie: Movie) {
        movieDao?.insertFavorite(movie)
    }

    suspend fun removeFavorite(movie: Movie) {
        movieDao?.deleteFavorite(movie)
    }

    suspend fun removeFavoriteById(movieId: Int) {
        movieDao?.deleteFavoriteById(movieId)
    }

    fun getAllFavorites(): LiveData<List<Movie>>? {
        return movieDao?.getAllFavorites()
    }

    suspend fun isFavorite(movieId: Int): Boolean {
        return movieDao?.getFavoriteById(movieId) != null
    }
}
