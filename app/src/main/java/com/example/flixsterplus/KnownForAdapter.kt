package com.example.flixsterplus

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.flixsterplus.databinding.ItemKnownForBinding

/** Lists the movies/shows a person is known for on the detail screen. */
class KnownForAdapter(
    private val items: List<KnownFor>
) : RecyclerView.Adapter<KnownForAdapter.KnownForViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KnownForViewHolder {
        val binding = ItemKnownForBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return KnownForViewHolder(binding)
    }

    inner class KnownForViewHolder(private val binding: ItemKnownForBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: KnownFor) {
            val context = binding.root.context
            val title = item.displayTitle ?: context.getString(R.string.untitled)
            binding.knownForTitle.text = item.year
                ?.let { context.getString(R.string.title_with_year, title, it) }
                ?: title
            binding.knownForMeta.text = context.getString(
                R.string.media_rating,
                context.getString(if (item.isTv) R.string.media_type_tv else R.string.media_type_movie),
                item.voteAverage
            )
            binding.knownForOverview.text = item.overview?.takeIf { it.isNotBlank() }
                ?: context.getString(R.string.no_overview)

            binding.knownForPoster.contentDescription =
                context.getString(R.string.poster_content_description, title)
            binding.knownForPoster.loadTmdbImage(item.posterUrl)
        }
    }

    override fun onBindViewHolder(holder: KnownForViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
