package com.example.flixsterplus

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.flixsterplus.databinding.ItemPersonBinding

/**
 * [RecyclerView.Adapter] that can display a [Person] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 */
class PersonRecyclerViewAdapter(
    private val people: List<Person>,
    private val listener: OnListFragmentInteractionListener?
) : RecyclerView.Adapter<PersonRecyclerViewAdapter.PersonViewHolder>() {

    // Inflate the item layout from XML
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
        val binding = ItemPersonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PersonViewHolder(binding)
    }

    // ViewHolder holds references to the item's views and, like campground's, handles its own clicks
    inner class PersonViewHolder(private val binding: ItemPersonBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            binding.root.setOnClickListener(this)
        }

        fun bind(person: Person) {
            val context = binding.root.context
            binding.personName.text = person.name
            binding.personDepartment.text = context.getString(
                R.string.department_popularity,
                person.knownForDepartment ?: context.getString(R.string.unknown_department),
                person.popularity
            )
            binding.personKnownFor.text = person.knownForTitles
                .takeIf { it.isNotEmpty() }
                ?.let { context.getString(R.string.known_for_list, it.joinToString()) }
                ?: context.getString(R.string.no_known_for)

            ViewCompat.setTransitionName(
                binding.personPhoto, DetailActivity.photoTransitionName(person.id)
            )
            binding.personPhoto.contentDescription =
                context.getString(R.string.profile_content_description, person.name.orEmpty())
            binding.personPhoto.loadTmdbImage(person.profileUrl)
        }

        override fun onClick(v: View?) {
            // NO_POSITION if the item was removed after the click was queued.
            val position = bindingAdapterPosition
            if (position == RecyclerView.NO_POSITION) return
            listener?.onItemClick(people[position], binding.personPhoto)
        }
    }

    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        holder.bind(people[position])
    }

    // Tells the RecyclerView how many items to display
    override fun getItemCount(): Int = people.size
}
