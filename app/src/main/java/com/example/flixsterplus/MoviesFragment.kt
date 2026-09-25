package com.example.flixsterplus

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.flixsterplus.databinding.FragmentMoviesListBinding
import com.example.flixsterplus.network.ApiClient
import com.google.gson.JsonParseException
import kotlinx.coroutines.launch
import java.io.IOException

/*
 * The class for the only fragment in the app, which contains the progress bar,
 * recyclerView, and performs the network call to TMDB's now_playing endpoint.
 */
class MoviesFragment : Fragment(), OnListFragmentInteractionListener {

    private var _binding: FragmentMoviesListBinding? = null
    private val binding get() = _binding!!

    /*
     * Constructing the view
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoviesListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // span_count is 1 in portrait (a plain list) and 2 in values-land (a grid).
        binding.list.layoutManager =
            GridLayoutManager(requireContext(), resources.getInteger(R.integer.span_count))
        updateAdapter()
    }

    /*
     * Fetches movies and hands them to the RecyclerView adapter.
     */
    private fun updateAdapter() {
        if (BuildConfig.TMDB_API_KEY.isBlank()) {
            Log.e(TAG, "TMDB_API_KEY is missing from local.properties")
            showError(R.string.missing_api_key)
            return
        }

        // Local refs so the finally block never touches _binding after onDestroyView nulls it.
        val progress = binding.progress
        val list = binding.list
        progress.visibility = View.VISIBLE

        // viewLifecycleOwner cancels the request if the view is destroyed (e.g. on rotation).
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val movies = ApiClient.fetchNowPlaying()
                Log.i(TAG, "Loaded ${movies.size} movies")
                list.adapter = MovieRecyclerViewAdapter(movies, this@MoviesFragment)
            } catch (e: IOException) {
                Log.e(TAG, "Network error fetching movies", e)
                showError(R.string.load_error)
            } catch (e: JsonParseException) {
                // Caught narrowly (not RuntimeException) so coroutine cancellation still propagates.
                Log.e(TAG, "Failed to parse movies", e)
                showError(R.string.load_error)
            } finally {
                progress.visibility = View.GONE
            }
        }
    }

    private fun showError(@StringRes message: Int) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    /*
     * What happens when a particular movie is clicked.
     */
    override fun onItemClick(item: Movie) {
        Toast.makeText(requireContext(), item.title, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "MoviesFragment"
    }
}
