package com.example.myapplication.presentation.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavController
import com.example.myapplication.InfiniteScrollHandler
import com.example.myapplication.MovieItem
import com.example.myapplication.model.Movie
import com.example.myapplication.network.Genre
import com.example.myapplication.viewmodel.MovieViewModel

@Composable
fun MovieListScreen(
    viewModel: MovieViewModel,
    navController: NavController,
    paddingValues: PaddingValues,
    onLoadMore: () -> Unit
) {
    val movies: List<Movie> by viewModel.movies.observeAsState(initial = emptyList())
    val genres: List<Genre> by viewModel.genres.observeAsState(initial = emptyList())
    val listState = rememberLazyGridState()

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        state = listState,
        contentPadding = paddingValues,
    ) {
        items(movies) { movie ->
            MovieItem(
                movie = movie,
                genres = genres,
                onClick = { navController.navigate("movieDetails/${movie.id}") },
                onFavoriteClick = {
                    viewModel.toggleFavorite(movie)
                })
        }
    }

    InfiniteScrollHandler(listState = listState, onLoadMore = onLoadMore)

}