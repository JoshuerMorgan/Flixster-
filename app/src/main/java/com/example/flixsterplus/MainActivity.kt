package com.example.flixsterplus

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.flixsterplus.databinding.ActivityMainBinding
import com.example.flixsterplus.network.ApiClient
import com.google.gson.JsonParseException
import kotlinx.coroutines.launch
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val movieAdapter = MovieAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applyWindowInsets()

        // span_count is 1 in portrait (a plain list) and 2 in values-land (a grid).
        binding.movieList.layoutManager =
            GridLayoutManager(this, resources.getInteger(R.integer.span_count))
        binding.movieList.adapter = movieAdapter

        loadMovies()
    }

    // targetSdk 35+ forces edge-to-edge: pad the toolbar below the status bar, keep the list's
    // last item above the nav bar, and avoid side cutouts/nav bars in landscape.
    private fun applyWindowInsets() {
        val listPadding = binding.movieList.paddingBottom
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { root, windowInsets ->
            val insets = windowInsets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
            )
            root.updatePadding(left = insets.left, right = insets.right)
            binding.appBar.updatePadding(top = insets.top)
            binding.movieList.updatePadding(bottom = listPadding + insets.bottom)
            WindowInsetsCompat.CONSUMED
        }
    }

    private fun loadMovies() {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val movies = ApiClient.fetchNowPlaying()
                Log.i(TAG, "Loaded ${movies.size} movies")
                movieAdapter.submitMovies(movies)
            } catch (e: IOException) {
                Log.e(TAG, "Network error fetching movies", e)
                showError()
            } catch (e: JsonParseException) {
                // Caught narrowly (not RuntimeException) so coroutine cancellation still propagates.
                Log.e(TAG, "Failed to parse movies", e)
                showError()
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun showError() {
        Toast.makeText(this, R.string.load_error, Toast.LENGTH_LONG).show()
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
