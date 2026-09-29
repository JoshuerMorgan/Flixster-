package com.example.flixsterplus

/** The {time_window} path segment of TMDB's trending endpoints. */
enum class TimeWindow(val apiValue: String) {
    DAY("day"),
    WEEK("week"),
}
