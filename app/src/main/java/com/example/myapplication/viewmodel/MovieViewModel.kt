package com.example.myapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myapplication.database.AppDatabase
import com.example.myapplication.model.Movie
import com.example.myapplication.network.Genre
import com.example.myapplication.network.MovieDetails
import com.example.myapplication.repository.MovieRepository
import kotlinx.coroutines.launch
import kotlin.collections.emptyList

class MovieViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = MovieRepository(database.movieDao())
    private val _movies = MutableLiveData<List<Movie>>()
    val movies: LiveData<List<Movie>> = _movies

    private val _genres = MutableLiveData<List<Genre>>()
    val genres: LiveData<List<Genre>> = _genres

    private val _movieDetails = MutableLiveData<MovieDetails>()
    val movieDetails: LiveData<MovieDetails> = _movieDetails

    private var currentPage = 1
    private var isFetching = false
    var favoriteMovies: LiveData<List<Movie>>? = repository.getAllFavorites()
    init {
        getGenres("29ce302f6eca1821e86f58a948079f84")
    }

    fun getPopularMovies(apiKey: String) {
        if (isFetching) return
        isFetching = true
        viewModelScope.launch {
            val newMovies = repository.getPopularMovies(apiKey, currentPage)
            // Check if each movie is a favorite
            newMovies.forEach { movie ->
                movie.isFavorite = repository.isFavorite(movie.id)
            }
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



    fun toggleFavorite(movie: Movie) {
        viewModelScope.launch {
            val newFavoriteStatus = !movie.isFavorite

            if (newFavoriteStatus) {
                repository.addFavorite(Movie(
                    id = movie.id,
                    genre_ids = movie.genre_ids,
                    overview = movie.overview,
                    poster_path = movie.poster_path,
                    title = movie.title,
                    vote_average = movie.vote_average,
                    isFavorite = newFavoriteStatus,
                ))

            } else {
//                movie.isFavorite = true
                repository.removeFavorite(movie)
            }

            // Update the movies list to reflect the change
            val currentList = _movies.value ?: emptyList()
            val updatedList = currentList.map { movieElement ->
                if (movie.id == movieElement.id) {
                    movieElement.copy(
                        id = movieElement.id,
                        title = movieElement.title,
                        overview = movieElement.overview,
                        poster_path = movieElement.poster_path,
                        vote_average = movieElement.vote_average,
                        genre_ids = movieElement.genre_ids,
                        isFavorite = newFavoriteStatus
                    )
                } else {
                    movieElement
                }
            }
            _movies.postValue(updatedList)
        }
    }
}
