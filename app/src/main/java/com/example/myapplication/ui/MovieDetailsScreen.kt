package com.example.myapplication.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.myapplication.R
import com.example.myapplication.viewmodel.MovieViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailsScreen(
    movieId: Int,
    viewModel: MovieViewModel,
    navController: NavController
) {
    val movieDetails = viewModel.movieDetails.observeAsState()
    val context = LocalContext.current
    var isFavorite by remember { mutableStateOf(false) }
    var isExpanded by remember { mutableStateOf(false) }


    LaunchedEffect(key1 = movieId) {
        viewModel.getMovieDetails("29ce302f6eca1821e86f58a948079f84", movieId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = movieDetails.value?.title ?: "Movie Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { isFavorite = !isFavorite }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) Color.Red else LocalContentColor.current
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        movieDetails.value?.let { movie ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                Image(
                    painter = rememberAsyncImagePainter("https://image.tmdb.org/t/p/w780${movie.poster_path}"),
                    contentDescription = movie.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(500.dp),
                    contentScale = ContentScale.Fit
                )

                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = movie.title, style = MaterialTheme.typography.headlineLarge)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = "Rating", tint = Color.Yellow)
                        Text(text = "${movie.vote_average}", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(start = 4.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Icon(Icons.Default.DateRange, contentDescription = "Release Date")
                        Text(text = movie.release_date, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(start = 4.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(text = "${movie.runtime} min", style = MaterialTheme.typography.bodyMedium)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Overview", style = MaterialTheme.typography.headlineSmall)
                    Column(modifier = Modifier.clickable { isExpanded = !isExpanded }) {
                        Text(
                            text = movie.overview,
                            style = MaterialTheme.typography.bodyLarge,
                            maxLines = if (isExpanded) Int.MAX_VALUE else 3,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.animateContentSize()
                        )
                    }

                    val trailer = movie.videos.results.firstOrNull { it.site == "YouTube" && it.type == "Trailer" }
                    if (trailer != null) {
                        Button(onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=${trailer.key}"))
                            context.startActivity(intent)
                        }) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Play Trailer")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Watch Trailer")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Cast", style = MaterialTheme.typography.headlineSmall)
                    LazyRow(modifier = Modifier.padding(top = 8.dp)) {
                        items(movie.credits.cast) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(end = 16.dp)
                            ) {
                                Image(
                                    painter = rememberAsyncImagePainter("https://image.tmdb.org/t/p/w185${it.profile_path}"),
                                    contentDescription = it.name,
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                                Text(text = it.name, style = MaterialTheme.typography.bodySmall, maxLines = 1)
                            }
                        }
                    }
                }
            }
        }
    }
}
