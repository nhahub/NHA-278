package com.example.myapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.Movie
import com.example.myapplication.network.Genre
import com.example.myapplication.network.MovieDetails
import com.example.myapplication.repository.MovieRepository
import kotlinx.coroutines.launch

class MovieViewModel : ViewModel() {
    private val repository = MovieRepository()
    private val _movies = MutableLiveData<List<Movie>>()
    val movies: LiveData<List<Movie>> = _movies

    private val _genres = MutableLiveData<List<Genre>>()
    val genres: LiveData<List<Genre>> = _genres

    private val _movieDetails = MutableLiveData<MovieDetails>()
    val movieDetails: LiveData<MovieDetails> = _movieDetails

    private var currentPage = 1
    private var isFetching = false

    init {
        getGenres("29ce302f6eca1821e86f58a948079f84")
    }

    fun getPopularMovies(apiKey: String) {
        if (isFetching) return
        isFetching = true
        viewModelScope.launch {
            val newMovies = repository.getPopularMovies(apiKey, currentPage)
            val currentMovies = _movies.value ?: emptyList()
            _movies.postValue(currentMovies + newMovies)
            currentPage++
            isFetching = false
        }
    }

    private fun getGenres(apiKey: String) {
        viewModelScope.launch {
            _genres.postValue(repository.getGenres(apiKey))
        }
    }

    fun getMovieDetails(apiKey: String, movieId: Int) {
        viewModelScope.launch {
            _movieDetails.postValue(repository.getMovieDetails(apiKey, movieId))
        }
    }
}
