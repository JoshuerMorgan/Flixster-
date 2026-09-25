package com.example.flixsterplus

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.flixsterplus.databinding.FragmentMovieBinding

/**
 * [RecyclerView.Adapter] that can display a [Movie] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 */
class MovieRecyclerViewAdapter(
    private val movies: List<Movie>,
    private val listener: OnListFragmentInteractionListener?
) : RecyclerView.Adapter<MovieRecyclerViewAdapter.MovieViewHolder>() {

    // Inflate the item layout from XML
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = FragmentMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    // ViewHolder class holds references to all UI elements inside the list item layout
    inner class MovieViewHolder(val binding: FragmentMovieBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]
        val binding = holder.binding

        binding.movieTitle.text = movie.title
        binding.movieOverview.text = movie.overview?.takeIf { it.isNotBlank() }
            ?: binding.root.context.getString(R.string.no_overview)

        // A null URL makes Glide show the fallback drawable instead of attempting a load.
        Glide.with(binding.moviePoster)
            .load(movie.posterUrl)
            .placeholder(R.drawable.poster_placeholder)
            .error(R.drawable.poster_error)
            .fallback(R.drawable.poster_error)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.moviePoster)

        // Sets up click listener for this movie item
        binding.root.setOnClickListener {
            listener?.onItemClick(movie)
        }
    }

    // Tells the RecyclerView how many items to display
    override fun getItemCount(): Int = movies.size
}
