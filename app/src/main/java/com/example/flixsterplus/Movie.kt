package com.example.flixsterplus

import com.google.gson.annotations.SerializedName

data class Movie(
    val id: Int,
    val title: String?,
    val overview: String?,
    // Nullable: TMDB returns null for movies without a poster.
    @SerializedName("poster_path") val posterPath: String?,
) {
    val posterUrl: String?
        get() = posterPath?.let { IMAGE_BASE_URL + it.trimStart('/') }

    companion object {
        const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500/"
    }
}
