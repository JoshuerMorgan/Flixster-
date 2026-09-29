package com.example.flixsterplus

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.flixsterplus.databinding.ItemMovieCreditBinding

/** Horizontal filmography row on the detail screen; [onClick] fires when a movie is tapped. */
class MovieCreditAdapter(
    private val credits: List<MovieCredit>,
    private val onClick: (MovieCredit) -> Unit
) : RecyclerView.Adapter<MovieCreditAdapter.CreditViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreditViewHolder {
        val binding = ItemMovieCreditBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CreditViewHolder(binding)
    }

    inner class CreditViewHolder(private val binding: ItemMovieCreditBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            binding.root.setOnClickListener(this)
        }

        fun bind(credit: MovieCredit) {
            val context = binding.root.context
            val title = credit.title ?: context.getString(R.string.untitled)
            binding.creditTitle.text = title
            binding.creditRole.text = credit.role.orEmpty()
            binding.creditPoster.contentDescription =
                context.getString(R.string.poster_content_description, title)
            binding.creditPoster.loadTmdbImage(credit.posterUrl)
        }

        override fun onClick(v: View?) {
            val position = bindingAdapterPosition
            if (position == RecyclerView.NO_POSITION) return
            onClick(credits[position])
        }
    }

    override fun onBindViewHolder(holder: CreditViewHolder, position: Int) {
        holder.bind(credits[position])
    }

    override fun getItemCount(): Int = credits.size
}
