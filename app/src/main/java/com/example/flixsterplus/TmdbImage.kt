package com.example.flixsterplus

object TmdbImage {
    private const val BASE_URL = "https://image.tmdb.org/t/p/w500/"

    /** Full image URL for a TMDB path like "/abc.jpg", or null if there is no image. */
    fun url(path: String?): String? =
        path?.takeIf { it.isNotBlank() }?.let { BASE_URL + it.trimStart('/') }
}
