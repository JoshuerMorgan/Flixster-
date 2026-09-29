package com.example.flixsterplus.network

import com.example.flixsterplus.BuildConfig
import com.example.flixsterplus.MovieCredit
import com.example.flixsterplus.MovieCreditsResponse
import com.example.flixsterplus.Person
import com.example.flixsterplus.TimeWindow
import com.example.flixsterplus.TrendingPeopleResponse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

object ApiClient {
    private const val BASE_URL = "https://api.themoviedb.org/3/"

    private val client = OkHttpClient()
    private val gson = Gson()

    /** Fetches trending people for [timeWindow]. Throws IOException on network/HTTP failure. */
    suspend fun fetchTrendingPeople(timeWindow: TimeWindow): List<Person> =
        get("trending/person/${timeWindow.apiValue}", TrendingPeopleResponse::class.java)
            ?.results.orEmpty()

    /** Fetches every movie a person acted in or worked on, most popular first. */
    suspend fun fetchMovieCredits(personId: Int): List<MovieCredit> =
        get("person/$personId/movie_credits", MovieCreditsResponse::class.java)
            ?.allCredits().orEmpty()

    /** GETs [path] on the IO dispatcher and parses the JSON body as [type]. */
    private suspend fun <T> get(path: String, type: Class<T>): T? = withContext(Dispatchers.IO) {
        val url = (BASE_URL + path).toHttpUrl().newBuilder()
            .addQueryParameter("api_key", BuildConfig.TMDB_API_KEY)
            .build()
        val request = Request.Builder().url(url).build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("HTTP ${response.code}")
            val body = response.body?.string() ?: throw IOException("Empty response body")
            gson.fromJson(body, type)
        }
    }
}
