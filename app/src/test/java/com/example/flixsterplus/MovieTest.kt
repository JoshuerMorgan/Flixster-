package com.example.flixsterplus

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Local unit tests for [Movie], which execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class MovieTest {
    @Test
    fun posterUrl_prependsBaseUrlWithoutDoubleSlash() {
        val movie = Movie(id = 1, title = "A", overview = null, posterPath = "/abc.jpg")
        assertEquals("https://image.tmdb.org/t/p/w500/abc.jpg", movie.posterUrl)
    }

    @Test
    fun posterUrl_isNullWhenPosterPathMissing() {
        val movie = Movie(id = 1, title = "A", overview = null, posterPath = null)
        assertNull(movie.posterUrl)
    }
}
