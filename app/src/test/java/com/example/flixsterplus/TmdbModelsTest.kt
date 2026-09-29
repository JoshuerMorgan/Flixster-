package com.example.flixsterplus

import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Local unit tests for the TMDB models, which execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class TmdbModelsTest {
    private val gson = Gson()

    @Test
    fun imageUrl_prependsBaseUrlWithoutDoubleSlash() {
        assertEquals("https://image.tmdb.org/t/p/w500/abc.jpg", TmdbImage.url("/abc.jpg"))
    }

    @Test
    fun imageUrl_isNullWhenPathMissing() {
        assertNull(TmdbImage.url(null))
        assertNull(TmdbImage.url(""))
    }

    @Test
    fun trendingPeople_parsesPersonAndKnownFor() {
        val json = """
            {"page":1,"results":[{
              "id":287,"name":"Brad Pitt","profile_path":"/pitt.jpg",
              "known_for_department":"Acting","popularity":42.5,"media_type":"person",
              "known_for":[
                {"id":550,"media_type":"movie","title":"Fight Club","release_date":"1999-10-15",
                 "overview":"An insomniac...","poster_path":"/fc.jpg","vote_average":8.4},
                {"id":1399,"media_type":"tv","name":"Some Show","first_air_date":"2011-04-17",
                 "overview":"","poster_path":null,"vote_average":7.0}
              ]}]}
        """.trimIndent()

        val person = gson.fromJson(json, TrendingPeopleResponse::class.java).results!!.single()

        assertEquals("Brad Pitt", person.name)
        assertEquals("Acting", person.knownForDepartment)
        assertEquals(42.5, person.popularity, 0.0)
        assertEquals("https://image.tmdb.org/t/p/w500/pitt.jpg", person.profileUrl)
        assertEquals(listOf("Fight Club", "Some Show"), person.knownForTitles)

        val (movie, show) = person.knownFor!!
        assertEquals("1999", movie.year)
        assertTrue(show.isTv)
        assertEquals("2011", show.year)
        assertNull(show.posterUrl)
    }

    @Test
    fun trendingPeople_missingKnownForGivesNoTitles() {
        val person = gson.fromJson("""{"id":1,"name":"A","popularity":1}""", Person::class.java)
        assertTrue(person.knownForTitles.isEmpty())
        assertNull(person.profileUrl)
    }

    @Test
    fun movieCredits_mergesCastAndCrewByPopularity() {
        val json = """
            {"cast":[
               {"id":1,"title":"Low","popularity":1.0,"character":"Bob"},
               {"id":2,"title":"High","popularity":9.0,"character":""}
             ],
             "crew":[
               {"id":2,"title":"High","popularity":9.0,"job":"Producer"},
               {"id":3,"title":"Mid","popularity":5.0,"job":"Director","release_date":""}
             ]}
        """.trimIndent()

        val credits = gson.fromJson(json, MovieCreditsResponse::class.java).allCredits()

        assertEquals(listOf("High", "Mid", "Low"), credits.map { it.title })
        // "High" is kept once; its blank character falls back to null (no job on the cast entry).
        assertNull(credits[0].role)
        assertEquals("Director", credits[1].role)
        assertNull(credits[1].year)
        assertEquals("Bob", credits[2].role)
    }
}
