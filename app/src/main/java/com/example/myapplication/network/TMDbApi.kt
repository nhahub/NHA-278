package com.example.myapplication.network

import com.example.myapplication.model.Movie
import com.example.myapplication.model.MovieResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TMDbApi {
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int
    ): MovieResponse

    @GET("genre/movie/list")
    suspend fun getGenres(
        @Query("api_key") apiKey: String
    ): GenreResponse

    @GET("movie/{movie_id}?append_to_response=videos,credits")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String
    ): MovieDetails
}

data class Genre(val id: Int, val name: String)
data class GenreResponse(val genres: List<Genre>)
data class MovieDetails(
    val id: Int,
    val title: String,
    val overview: String,
    val poster_path: String,
    val vote_average: Double,
    val genres: List<Genre>,
    val runtime: Int,
    val release_date: String,
    val credits: Credits,
    val videos: VideoResponse
)

data class Credits(val cast: List<CastMember>)
data class CastMember(val name: String, val profile_path: String?)

data class VideoResponse(val results: List<Video>)
data class Video(val key: String, val site: String, val type: String)
