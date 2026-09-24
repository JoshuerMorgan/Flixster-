package com.example.flixsterplus

import com.google.gson.annotations.SerializedName

data class NowPlayingResponse(
    @SerializedName("results") val results: List<Movie> = emptyList(),
)
