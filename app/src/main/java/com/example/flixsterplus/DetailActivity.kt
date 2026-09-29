package com.example.flixsterplus

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flixsterplus.databinding.ActivityDetailBinding
import com.example.flixsterplus.network.ApiClient
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.JsonParseException
import kotlinx.coroutines.launch
import java.io.IOException

/**
 * Shows the details of the [Person] passed in [PERSON_EXTRA], plus their movie credits.
 */
class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applySystemBarInsets(binding.root, binding.appBar, binding.scroll)
        binding.toolbar.setNavigationOnClickListener { supportFinishAfterTransition() }

        // Get the extra from the Intent
        val person = IntentCompat.getSerializableExtra(intent, PERSON_EXTRA, Person::class.java)
        if (person == null) {
            Log.e(TAG, "Started without a $PERSON_EXTRA extra")
            finish()
            return
        }

        // Hold the shared element transition until Glide has the photo, so it doesn't animate empty.
        supportPostponeEnterTransition()
        ViewCompat.setTransitionName(binding.personPhoto, photoTransitionName(person.id))
        binding.personPhoto.loadTmdbImage(person.profileUrl) { supportStartPostponedEnterTransition() }

        bindPerson(person)
        loadCredits(person.id)
    }

    private fun bindPerson(person: Person) {
        binding.toolbar.title = person.name
        binding.personPhoto.contentDescription =
            getString(R.string.profile_content_description, person.name.orEmpty())
        binding.personName.text = person.name
        binding.personDepartment.text =
            person.knownForDepartment ?: getString(R.string.unknown_department)
        binding.personPopularity.text = getString(R.string.popularity, person.popularity)

        val knownFor = person.knownFor.orEmpty()
        binding.knownForList.layoutManager = LinearLayoutManager(this)
        binding.knownForList.adapter = KnownForAdapter(knownFor)
        binding.knownForEmpty.visibility = if (knownFor.isEmpty()) View.VISIBLE else View.GONE

        binding.creditsList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }

    /*
     * Second API call: the person's full filmography, shown in its own RecyclerView.
     */
    private fun loadCredits(personId: Int) {
        if (BuildConfig.TMDB_API_KEY.isBlank()) {
            showCreditsMessage(R.string.missing_api_key)
            return
        }
        binding.creditsProgress.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val credits = ApiClient.fetchMovieCredits(personId)
                Log.i(TAG, "Loaded ${credits.size} credits for person $personId")
                if (credits.isEmpty()) {
                    showCreditsMessage(R.string.no_credits)
                } else {
                    binding.creditsList.adapter = MovieCreditAdapter(credits, ::showCreditDetails)
                }
            } catch (e: IOException) {
                Log.e(TAG, "Network error fetching credits", e)
                showCreditsMessage(R.string.credits_error)
            } catch (e: JsonParseException) {
                Log.e(TAG, "Failed to parse credits", e)
                showCreditsMessage(R.string.credits_error)
            } finally {
                binding.creditsProgress.visibility = View.GONE
            }
        }
    }

    private fun showCreditsMessage(@StringRes message: Int) {
        binding.creditsMessage.setText(message)
        binding.creditsMessage.visibility = View.VISIBLE
    }

    /** Shows the tapped movie's year, role and overview. */
    private fun showCreditDetails(credit: MovieCredit) {
        val title = credit.title ?: getString(R.string.untitled)
        val overview = credit.overview?.takeIf { it.isNotBlank() } ?: getString(R.string.no_overview)
        MaterialAlertDialogBuilder(this)
            .setTitle(credit.year?.let { getString(R.string.title_with_year, title, it) } ?: title)
            .setMessage(
                credit.role?.let { getString(R.string.credit_role_and_overview, it, overview) }
                    ?: overview
            )
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }

    companion object {
        private const val TAG = "DetailActivity"
        const val PERSON_EXTRA = "PERSON_EXTRA"

        /** Unique per person so the list item and detail photo pair up in the transition. */
        fun photoTransitionName(personId: Int) = "person_photo_$personId"
    }
}
