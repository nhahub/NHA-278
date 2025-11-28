package com.example.myapplication

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.myapplication.database.AppDatabase
import com.example.myapplication.database.MovieDao
import com.example.myapplication.model.Movie
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MovieDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: MovieDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        )
        .allowMainThreadQueries()
        .build()

        dao = database.movieDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    // ----------------------------------------------------------
    // TEST: Insert Favorite
    // ----------------------------------------------------------
    @Test
    fun insertFavorite_insertsMovieCorrectly() = runBlocking {
        val movie = Movie(1, "Movie A", "Desc", "poster.jpg", 7.5, listOf(1))

        dao.insertFavorite(movie)

        val result = dao.getFavoriteById(1)

        Assert.assertNotNull(result)
        Assert.assertEquals("Movie A", result!!.title)
    }

    // ----------------------------------------------------------
    // TEST: Delete Favorite
    // ----------------------------------------------------------
    @Test
    fun deleteFavorite_removesMovieCorrectly() = runBlocking {
        val movie = Movie(1, "Movie A", "Desc", "poster.jpg", 7.5, listOf(1))
        dao.insertFavorite(movie)

        dao.deleteFavorite(movie)

        val result = dao.getFavoriteById(1)
        Assert.assertNull(result)
    }

    // ----------------------------------------------------------
    // TEST: Delete by ID
    // ----------------------------------------------------------
    @Test
    fun deleteFavoriteById_removesMovieCorrectly() = runBlocking {
        val movie = Movie(1, "Movie A", "Desc", "poster.jpg", 7.5, listOf(1))
        dao.insertFavorite(movie)

        dao.deleteFavoriteById(1)

        val result = dao.getFavoriteById(1)
        Assert.assertNull(result)
    }

    // ----------------------------------------------------------
    // TEST: getFavoriteById returns NULL if not exist
    // ----------------------------------------------------------
    @Test
    fun getFavoriteById_returnsNullWhenNotExists() = runBlocking {
        val result = dao.getFavoriteById(999)

        Assert.assertNull(result)
    }
}