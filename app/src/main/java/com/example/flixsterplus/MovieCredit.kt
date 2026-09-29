package com.example.flixsterplus

import com.google.gson.annotations.SerializedName

/** One movie from TMDB's person/{person_id}/movie_credits endpoint (cast or crew). */
data class MovieCredit(
    val id: Int,
    val title: String?,
    val overview: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("release_date") val releaseDate: String?,
    val popularity: Double,
    // Cast entries have a character; crew entries have a job instead.
    val character: String?,
    val job: String?,
) {
    val role: String?
        get() = character?.takeIf { it.isNotBlank() } ?: job?.takeIf { it.isNotBlank() }

    val year: String?
        get() = releaseDate?.take(4)?.takeIf { it.length == 4 }

    val posterUrl: String?
        get() = TmdbImage.url(posterPath)
}

data class MovieCreditsResponse(
    val cast: List<MovieCredit>?,
    val crew: List<MovieCredit>?,
) {
    /** Cast and crew merged with one entry per movie, most popular first. */
    fun allCredits(): List<MovieCredit> =
        (cast.orEmpty() + crew.orEmpty())
            .sortedByDescending { it.popularity }
            .distinctBy { it.id }
}
