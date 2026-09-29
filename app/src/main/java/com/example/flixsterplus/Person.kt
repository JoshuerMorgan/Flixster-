package com.example.flixsterplus

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * One entry from TMDB's trending/person endpoint.
 * Serializable (like campground's model) so it can be passed to [DetailActivity] as an Intent extra.
 */
data class Person(
    val id: Int,
    val name: String?,
    // Nullable: TMDB returns null for people without a photo.
    @SerializedName("profile_path") val profilePath: String?,
    @SerializedName("known_for_department") val knownForDepartment: String?,
    val popularity: Double,
    // Nullable: Gson skips Kotlin defaults, so a missing array arrives as null.
    @SerializedName("known_for") val knownFor: List<KnownFor>?,
) : Serializable {
    val profileUrl: String?
        get() = TmdbImage.url(profilePath)

    val knownForTitles: List<String>
        get() = knownFor.orEmpty().mapNotNull { it.displayTitle }
}

/** A movie or TV show a [Person] is known for. */
data class KnownFor(
    val id: Int,
    @SerializedName("media_type") val mediaType: String?,
    // Movies have a title and release_date; TV shows have a name and first_air_date.
    val title: String?,
    val name: String?,
    val overview: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("first_air_date") val firstAirDate: String?,
    @SerializedName("vote_average") val voteAverage: Double,
) : Serializable {
    val displayTitle: String?
        get() = title ?: name

    val year: String?
        get() = (releaseDate ?: firstAirDate)?.take(4)?.takeIf { it.length == 4 }

    val isTv: Boolean
        get() = mediaType == "tv"

    val posterUrl: String?
        get() = TmdbImage.url(posterPath)
}

data class TrendingPeopleResponse(
    @SerializedName("results") val results: List<Person>?,
)
