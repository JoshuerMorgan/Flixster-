package com.example.flixsterplus

import android.widget.ImageView

/**
 * This interface is used by the [PersonRecyclerViewAdapter] to ensure
 * it has an appropriate Listener.
 *
 * In this app, it's implemented by [PeopleFragment]
 */
interface OnListFragmentInteractionListener {
    /** [sharedImage] is the clicked item's photo, used for the shared element transition. */
    fun onItemClick(item: Person, sharedImage: ImageView)
}
