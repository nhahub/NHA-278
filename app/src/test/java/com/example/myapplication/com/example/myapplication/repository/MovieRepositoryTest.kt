package com.example.myapplication.com.example.myapplication.repository

// ---------- IMPORTS ----------
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.database.MovieDao
import com.example.myapplication.model.Movie
import com.example.myapplication.model.MovieResponse
import com.example.myapplication.network.Credits
import com.example.myapplication.network.Genre
import com.example.myapplication.network.GenreResponse
import com.example.myapplication.network.MovieDetails
import com.example.myapplication.network.TMDbApi
import com.example.myapplication.network.VideoResponse
import com.example.myapplication.repository.MovieRepository
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertSame
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.lang.reflect.Field

@OptIn(ExperimentalCoroutinesApi::class)
class MovieRepositoryTest {

    // Needed for LiveData testing
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var api: TMDbApi
    private lateinit var dao: MovieDao
    private lateinit var repository: MovieRepository

    @Before
    fun setup() {
        api = mock()
        dao = mock()
        repository = MovieRepository(dao)

        // Inject mocked API into repository using reflection
        val field: Field = MovieRepository::class.java.getDeclaredField("tmdbApi")
        field.isAccessible = true
        field.set(repository, api)
    }

    // ------------------------------------------------------------
    // getPopularMovies()
    // ------------------------------------------------------------
    @Test
    fun `getPopularMovies returns list successfully`() = runTest {
        val movies = listOf(Movie(1, "A", "B", "C", 8.0, listOf(1)))
        val response = MovieResponse(movies)
        whenever(api.getPopularMovies("KEY", 1)).thenReturn(response)

        val result = repository.getPopularMovies("KEY", 1)

        assertEquals(1, result.size)
        assertEquals("A", result.first().title)
    }

    @Test
    fun `getPopularMovies throws exception`() = runTest {
        whenever(api.getPopularMovies(any(), any())).thenThrow(RuntimeException("API error"))

        assertThrows<RuntimeException> {
            runTest { repository.getPopularMovies("KEY", 1) }
        }
    }

    // ------------------------------------------------------------
    // searchForMovies()
    // ------------------------------------------------------------
    @Test
    fun `searchForMovies returns results successfully`() = runTest {
        val movies = listOf(Movie(2, "Query movie", "Desc", "P", 7.0, listOf(2)))
        val response = MovieResponse(movies)
        whenever(api.searchMovies("KEY", "Avengers", 1)).thenReturn(response)

        val result = repository.searchForMovies("KEY", "Avengers", 1)

        assertEquals(1, result.size)
        assertEquals("Query movie", result.first().title)
    }

    @Test
    fun `searchForMovies throws exception`() = runTest {
        whenever(api.searchMovies(any(), any(), any()))
            .thenThrow(RuntimeException("API Error"))

        assertThrows<RuntimeException> {
            runTest { repository.searchForMovies("KEY", "Bad", 1) }
        }
    }

    // ------------------------------------------------------------
    // getGenres()
    // ------------------------------------------------------------
    @Test
    fun `getGenres loads genres first time`() = runTest {
        val genres = listOf(Genre(1, "Action"), Genre(2, "Drama"))
        val response = GenreResponse(genres)
        whenever(api.getGenres("KEY")).thenReturn(response)

        val result = repository.getGenres("KEY")

        assertEquals(2, result.size)
        verify(api, times(1)).getGenres("KEY")
    }

    @Test
    fun `getGenres returns cached genres second time`() = runTest {
        val genres = listOf(Genre(1, "Action"))
        whenever(api.getGenres("KEY")).thenReturn(GenreResponse(genres))

        val first = repository.getGenres("KEY")
        val second = repository.getGenres("KEY")

        assertSame(first, second)
        verify(api, times(1)).getGenres("KEY") // only called once
    }

    @Test
    fun `getGenres throws exception`() = runTest {
        whenever(api.getGenres(any())).thenThrow(RuntimeException("Network Error"))

        assertThrows<RuntimeException> {
            runTest { repository.getGenres("KEY") }
        }
    }

    // ------------------------------------------------------------
    // getMovieDetails()
    // ------------------------------------------------------------
    @Test
    fun `getMovieDetails returns details`() = runTest {
        val details = MovieDetails(
            id = 10,
            title = "Movie",
            overview = "Overview",
            poster_path = "/",
            vote_average = 8.5,
            genres = listOf(Genre(1, "Action")),
            runtime = 120,
            release_date = "2024-01-01",
            credits = Credits(cast = emptyList()),
            videos = VideoResponse(emptyList())
        )

        whenever(api.getMovieDetails(10, "KEY")).thenReturn(details)

        val result = repository.getMovieDetails("KEY", 10)

        assertEquals(10, result.id)
        assertEquals("Movie", result.title)
    }

    @Test
    fun `getMovieDetails throws exception`() = runTest {
        whenever(api.getMovieDetails(any(), any()))
            .thenThrow(RuntimeException("Bad response"))

        assertThrows<RuntimeException> {
            runTest { repository.getMovieDetails("KEY", 10) }
        }
    }

    // ------------------------------------------------------------
    // Favorite DAO operations
    // ------------------------------------------------------------
    @Test
    fun `addFavorite calls DAO`() = runTest {
        val movie = Movie(1, "A", "B", "C", 8.0, listOf())
        repository.addFavorite(movie)

        verify(dao).insertFavorite(movie)
    }

    @Test
    fun `removeFavorite calls DAO`() = runTest {
        val movie = Movie(1, "A", "B", "C", 8.0, emptyList())
        repository.removeFavorite(movie)

        verify(dao).deleteFavorite(movie)
    }

    @Test
    fun `removeFavoriteById calls DAO`() = runTest {
        repository.removeFavoriteById(5)

        verify(dao).deleteFavoriteById(5)
    }

    @Test
    fun `getAllFavorites returns LiveData`() {
        val data = MutableLiveData(listOf(Movie(1, "A", "B", "C", 8.0, listOf())))
        whenever(dao.getAllFavorites()).thenReturn(data)

        val result = repository.getAllFavorites()

        assertNotNull(result)
        assertEquals(1, result!!.value!!.size)
    }

    @Test
    fun `isFavorite returns TRUE when exists`() = runTest {
        whenever(dao.getFavoriteById(1)).thenReturn(Movie(1, "A", "B", "C", 8.0, listOf()))

        val result = repository.isFavorite(1)

        assertTrue(result)
    }

    @Test
    fun `isFavorite returns FALSE when not exists`() = runTest {
        whenever(dao.getFavoriteById(2)).thenReturn(null)

        val result = repository.isFavorite(2)

        assertFalse(result)
    }

    // ------------------------------------------------------------
    // DAO = null cases
    // ------------------------------------------------------------
    @Test
    fun `DAO null - addFavorite should not crash`() = runTest {
        val repo = MovieRepository(null)
        repo.addFavorite(Movie(1, "x", "", "", 0.0, listOf()))
    }

    @Test
    fun `DAO null - getAllFavorites returns null`() {
        val repo = MovieRepository(null)
        assertNull(repo.getAllFavorites())
    }

    @Test
    fun `DAO null - isFavorite returns false`() = runTest {
        val repo = MovieRepository(null)
        val result = repo.isFavorite(5)
        assertFalse(result)
    }
}
