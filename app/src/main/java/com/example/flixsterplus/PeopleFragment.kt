package com.example.flixsterplus

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.StringRes
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.flixsterplus.databinding.FragmentPeopleListBinding
import com.example.flixsterplus.network.ApiClient
import com.google.gson.JsonParseException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.IOException

/*
 * The fragment that shows the Day/Week toggle, progress bar and RecyclerView,
 * and performs the network call to TMDB's trending/person endpoint.
 */
class PeopleFragment : Fragment(), OnListFragmentInteractionListener {

    private var _binding: FragmentPeopleListBinding? = null
    private val binding get() = _binding!!

    private var timeWindow = TimeWindow.WEEK
    private var loadJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val saved = savedInstanceState?.getString(KEY_TIME_WINDOW)
        timeWindow = TimeWindow.entries.find { it.name == saved } ?: TimeWindow.WEEK
    }

    /*
     * Constructing the view
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPeopleListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // span_count is 1 in portrait (a plain list) and 2 in values-land (a grid).
        binding.list.layoutManager =
            GridLayoutManager(requireContext(), resources.getInteger(R.integer.span_count))

        // Check the saved button before adding the listener so it doesn't trigger a second load.
        binding.timeWindowToggle.check(
            if (timeWindow == TimeWindow.DAY) R.id.buttonDay else R.id.buttonWeek
        )
        binding.timeWindowToggle.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener
            timeWindow = if (checkedId == R.id.buttonDay) TimeWindow.DAY else TimeWindow.WEEK
            loadPeople()
        }
        binding.retryButton.setOnClickListener { loadPeople() }

        loadPeople()
    }

    /*
     * Fetches trending people and hands them to the RecyclerView adapter.
     */
    private fun loadPeople() {
        // Local ref so the coroutine never touches _binding after onDestroyView nulls it.
        val b = binding
        if (BuildConfig.TMDB_API_KEY.isBlank()) {
            Log.e(TAG, "TMDB_API_KEY is missing from local.properties")
            showMessage(b, R.string.missing_api_key, canRetry = false)
            return
        }

        // Switching Day/Week mid-request drops the older request.
        loadJob?.cancel()
        b.message.visibility = View.GONE
        b.retryButton.visibility = View.GONE
        b.progress.visibility = View.VISIBLE

        // viewLifecycleOwner cancels the request if the view is destroyed (e.g. on rotation).
        loadJob = viewLifecycleOwner.lifecycleScope.launch {
            try {
                val people = ApiClient.fetchTrendingPeople(timeWindow)
                Log.i(TAG, "Loaded ${people.size} people for $timeWindow")
                if (people.isEmpty()) {
                    showMessage(b, R.string.empty_people, canRetry = true)
                } else {
                    b.progress.visibility = View.GONE
                    b.list.visibility = View.VISIBLE
                    b.list.adapter = PersonRecyclerViewAdapter(people, this@PeopleFragment)
                }
            } catch (e: IOException) {
                Log.e(TAG, "Network error fetching people", e)
                showMessage(b, R.string.load_error, canRetry = true)
            } catch (e: JsonParseException) {
                // Caught narrowly (not RuntimeException) so coroutine cancellation still propagates.
                Log.e(TAG, "Failed to parse people", e)
                showMessage(b, R.string.load_error, canRetry = true)
            }
        }
    }

    /** Replaces the list with an error or empty-state message. */
    private fun showMessage(
        b: FragmentPeopleListBinding,
        @StringRes message: Int,
        canRetry: Boolean
    ) {
        b.progress.visibility = View.GONE
        b.list.visibility = View.GONE
        b.message.setText(message)
        b.message.visibility = View.VISIBLE
        b.retryButton.visibility = if (canRetry) View.VISIBLE else View.GONE
    }

    /*
     * What happens when a particular person is clicked.
     */
    override fun onItemClick(item: Person, sharedImage: ImageView) {
        // Navigate to the details screen and pass the selected person
        val intent = Intent(requireContext(), DetailActivity::class.java)
            .putExtra(DetailActivity.PERSON_EXTRA, item)
        // Animate the clicked photo into the detail screen's photo (same transitionName).
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
            requireActivity(), sharedImage, DetailActivity.photoTransitionName(item.id)
        )
        startActivity(intent, options.toBundle())
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_TIME_WINDOW, timeWindow.name)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "PeopleFragment"
        private const val KEY_TIME_WINDOW = "time_window"
    }
}
