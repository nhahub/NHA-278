package com.example.myapplication
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.ui.FavoritesScreen
import com.example.reg_with_firebase.AnonymousSignInScreen
import com.example.reg_with_firebase.HomeScreen
import com.example.reg_with_firebase.LoginScreen
import com.example.reg_with_firebase.SignupScreen
import java.util.Locale
import com.example.myapplication.ui.MovieDetailsScreen
import com.example.myapplication.ui.SettingsScreen
import coil.compose.rememberAsyncImagePainter
import com.example.myapplication.data.DataStoreManager
import com.example.myapplication.data.Language
import com.example.myapplication.repository.SettingsRepositoryImp
import com.example.myapplication.data.Theme
import com.example.myapplication.model.Movie
import com.example.myapplication.network.Genre
import com.example.myapplication.presentation.screens.MovieSearchScreen
import com.example.myapplication.ui.components.SearchTextField
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.util.updateLocale
import com.example.myapplication.viewmodel.MovieViewModel
import com.example.myapplication.viewmodel.SettingsViewModel
import com.example.myapplication.viewmodel.SettingsViewModelFactory
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    private val movieViewModel: MovieViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val dataStoreManager = remember { DataStoreManager(context) }
            val repository = remember { SettingsRepositoryImp(dataStoreManager) }
            val factory = remember { SettingsViewModelFactory(repository) }
            val settingsViewModel: SettingsViewModel = viewModel(factory = factory)

            val currentTheme by settingsViewModel.themeState.collectAsState()
            val currentLanguage by settingsViewModel.languageState.collectAsState()

            val updatedContext = LocalContext.current.updateLocale(
                when (currentLanguage) {
                    Language.ENGLISH -> "en"
                    Language.ARABIC -> "ar"
                }
            )
            val layoutDirection =
                if (currentLanguage == Language.ARABIC) LayoutDirection.Rtl else LayoutDirection.Ltr

            CompositionLocalProvider(
                LocalContext provides updatedContext,
                LocalLayoutDirection provides layoutDirection
            ) {
                MyApplicationTheme(
                    darkTheme = when (currentTheme) {
                        Theme.LIGHT -> false
                        Theme.DARK -> true
                        Theme.SYSTEM -> isSystemInDarkTheme()
                    }
                ) {
                    val auth = FirebaseAuth.getInstance()
                    var isLoggedIn by remember { mutableStateOf(auth.currentUser != null) }

                    DisposableEffect(auth) {
                        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
                            isLoggedIn = firebaseAuth.currentUser != null
                        }
                        auth.addAuthStateListener(listener)
                        onDispose { auth.removeAuthStateListener(listener) }
                    }

                    if (isLoggedIn) {
                        MovieApp(movieViewModel, settingsViewModel)
                    } else {
                        Authapp(settingsViewModel = settingsViewModel)
                    }
                }
            }

            movieViewModel.getPopularMovies("29ce302f6eca1821e86f58a948079f84")
        }
    }
}

// ------------------------ MovieApp ------------------------
@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun MovieApp(viewModel: MovieViewModel, settingsViewModel: SettingsViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    var searchQuery by rememberSaveable() {
        mutableStateOf("")
    }
    Scaffold(
        topBar = {
            if (currentRoute == "home" || currentRoute == "search"||currentRoute == "favourites"||
                 currentRoute == "settings") {
                TopAppBar(
                    title = {
                        when (currentRoute) {
                            "home" -> {
                                Text(stringResource(R.string.movie_box))
                            }
                            "favourites" -> {
                                Text(stringResource(R.string.favourites))
                            }
                            "settings" -> {
                               Text(stringResource(R.string.settings_title))
                            }
                            else -> {
                                SearchTextField {
                                    searchQuery = it
                                }
                            }
                        }
                    }
                )
            }
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        NavHost(navController = navController, startDestination = "home", modifier = Modifier) {
            composable("home") {
                MovieListScreen(
                    viewModel,
                    navController,
                    innerPadding
                ) { viewModel.getPopularMovies("29ce302f6eca1821e86f58a948079f84") }
            }
            composable("search") {
                MovieSearchScreen(
                    viewModel,
                    navController,
                    innerPadding,
                    searchQuery
                )
            }
            composable("favourites") {

                val favoriteMovies: List<Movie> by viewModel.favoriteMovies?.observeAsState(initial = emptyList())
                    ?: remember { mutableStateOf(emptyList()) }
                FavoritesScreen(
                    favoriteMovies = favoriteMovies,
                    onMovieClick = { movie ->
                        navController.navigate("movieDetails/${movie.id}")
                    },
                    onBackClick = {
                        navController.navigateUp()
                    },
                    onDiscoverClick = {
                        navController.navigate("home") {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onToggleFavorite = { movie ->
                        viewModel.toggleFavorite(movie)
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            composable("settings") {
                Box(Modifier.padding(innerPadding)) {
                    SettingsScreen(settingsViewModel, navController)
                }
            }
            composable(
                "movieDetails/{movieId}",
                arguments = listOf(navArgument("movieId") {
                    type = androidx.navigation.NavType.IntType
                })
            ) { backStackEntry ->
                val movieId = backStackEntry.arguments?.getInt("movieId")
                if (movieId != null) {
                    MovieDetailsScreen(movieId, viewModel, navController)
                }
            }
        }
    }
}

// ------------------------ Authapp ------------------------
@Composable
fun Authapp(modifier: Modifier = Modifier, settingsViewModel: SettingsViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login", modifier = modifier) {
        composable("login") { LoginScreen(navController) }
        composable("signup") { SignupScreen(navController) }
        // This screen is reached on successful login/signup,
        // which triggers the state change in MainActivity to show MovieApp
        composable("home") { HomeScreen(navController) }
        composable("anonymous") { AnonymousSignInScreen(navController) }
        composable("settings") { SettingsScreen(settingsViewModel, navController) }
    }
}

// ------------------------ BottomNavigationBar ------------------------
@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        NavigationItem.Home,
        NavigationItem.Search,
        NavigationItem.Favourites,
        NavigationItem.Settings
    )
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(imageVector = item.icon, contentDescription = null) },
                label = { Text(stringResource(item.title)) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

sealed class NavigationItem(var route: String, var icon: ImageVector, var title: Int) {
    object Home : NavigationItem("home", Icons.Default.Home, R.string.home)
    object Search : NavigationItem("search", Icons.Default.Search, R.string.search)
    object Favourites : NavigationItem("favourites", Icons.Default.Favorite, R.string.favourites)
    object Settings : NavigationItem("settings", Icons.Default.Settings, R.string.settings_title)
}

// ------------------------ MovieListScreen ------------------------
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
        contentPadding = paddingValues
    ) {
        items(movies) { movie ->
            MovieItem(
                movie = movie,
                genres = genres,
                onClick = { navController.navigate("movieDetails/${movie.id}") },
                onFavoriteClick = { viewModel.toggleFavorite(movie) }
            )
        }
    }

    InfiniteScrollHandler(listState = listState, onLoadMore = onLoadMore)
}

@Composable
fun InfiniteScrollHandler(listState: LazyGridState, onLoadMore: () -> Unit) {
    val shouldLoadMore by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            val totalItemsCount = layoutInfo.totalItemsCount
            lastVisibleItemIndex >= totalItemsCount - 5 && totalItemsCount > 0
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) onLoadMore()
    }
}


@Composable
fun MovieItem(movie: Movie, genres: List<Genre>, onClick: () -> Unit, onFavoriteClick: () -> Unit) {
    val genreNames = movie.genre_ids.mapNotNull { genreId ->
        genres.find { it.id == genreId }?.name
    }.joinToString(", ")

    Card(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column {
            Image(
                painter = rememberAsyncImagePainter("https://image.tmdb.org/t/p/w500" + movie.poster_path),
                contentDescription = movie.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.67f),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = String.format(Locale.getDefault(), "%.1f", movie.vote_average),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onFavoriteClick) {
                        Icon(
                            imageVector = if (movie.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Like button",
                            tint = if (movie.isFavorite) Color.Red else Color.Gray
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = genreNames,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
