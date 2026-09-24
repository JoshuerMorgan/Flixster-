package com.example.flixsterplus.network

import com.example.flixsterplus.BuildConfig
import com.example.flixsterplus.Movie
import com.example.flixsterplus.NowPlayingResponse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

object ApiClient {
    private const val NOW_PLAYING_URL = "https://api.themoviedb.org/3/movie/now_playing"

    private val client = OkHttpClient()
    private val gson = Gson()

    /** Fetches now-playing movies on the IO dispatcher. Throws IOException on network/HTTP failure. */
    suspend fun fetchNowPlaying(): List<Movie> = withContext(Dispatchers.IO) {
        val url = NOW_PLAYING_URL.toHttpUrl().newBuilder()
            .addQueryParameter("api_key", BuildConfig.TMDB_API_KEY)
            .build()
        val request = Request.Builder().url(url).build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("HTTP ${response.code}")
            val body = response.body?.string() ?: throw IOException("Empty response body")
            gson.fromJson(body, NowPlayingResponse::class.java)?.results.orEmpty()
        }
    }
}
