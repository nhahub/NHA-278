package com.example.myapplication.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.example.myapplication.InfiniteScrollHandler
import com.example.myapplication.ui.components.MovieList
import com.example.myapplication.viewmodel.MovieViewModel

@Composable
fun MovieSearchScreen(
    viewModel: MovieViewModel,
    navController: NavHostController,
    innerPadding: PaddingValues,
    searchQuery: String
) {
    val results by viewModel.searchResults.observeAsState(emptyList())
    val isLoading by viewModel.isSearching.observeAsState(false)
    val listState = rememberLazyGridState()

    // Run search when query changes
    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotBlank()) {
            viewModel.searchMovies(searchQuery)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {

        when {
            searchQuery.isBlank() -> CenterMessage("Type to search a movie...")
            isLoading && results.isEmpty() -> CenterLoading()
            results.isEmpty() -> CenterMessage("No results found")
            else -> {
                MovieList(
                    movies = results,
                    listState = listState,
                    onItemClick = { movie ->
                        navController.navigate("movieDetails/${movie.id}")
                    },
                    onFavouriteClick = { movie ->
                        viewModel.toggleFavoriteInSearch(movie)
                    },
                    innerPadding = PaddingValues(0.dp)
                )

                InfiniteScrollHandler(
                    listState = listState,
                    onLoadMore = {
                        viewModel.searchMovies(searchQuery)
                    }
                )
            }
        }
    }
}
@Composable
fun CenterMessage(text: String) {
    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun CenterLoading() {
    Box(modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
