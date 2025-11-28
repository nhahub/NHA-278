package com.example.myapplication.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import com.example.myapplication.MovieItem
import com.example.myapplication.model.Movie
import com.example.myapplication.network.Genre

@Composable
fun MovieList(
    movies: List<Movie>,
    genres: List<Genre> =listOf(),
    listState: LazyGridState,
    onItemClick: (Movie) -> Unit,
    onFavouriteClick: (Movie) -> Unit,
    innerPadding: PaddingValues
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        state = listState,
        contentPadding = innerPadding,
    ) {
        items(movies) { movie ->
            MovieItem(
                movie = movie,
                genres = genres,
                onClick = { onItemClick(movie) },
                onFavoriteClick = {
                    onFavouriteClick(movie)
                })
        }
    }
}