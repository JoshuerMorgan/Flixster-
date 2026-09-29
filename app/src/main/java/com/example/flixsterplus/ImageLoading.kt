package com.example.flixsterplus

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

/**
 * Loads a TMDB image with rounded corners and the shared placeholder/error drawables.
 * A null [url] makes Glide show the fallback drawable instead of attempting a load.
 *
 * If [onFinished] is given it runs once the image (or the error drawable) is showing, and the
 * cross-fade is skipped so a shared element transition animates the final image.
 */
fun ImageView.loadTmdbImage(url: String?, onFinished: (() -> Unit)? = null) {
    val radius = resources.getDimensionPixelSize(R.dimen.image_corner_radius)
    val request = Glide.with(this)
        .load(url)
        .placeholder(R.drawable.poster_placeholder)
        .error(R.drawable.poster_error)
        .fallback(R.drawable.poster_error)
        .transform(CenterCrop(), RoundedCorners(radius))

    if (onFinished == null) {
        request.transition(DrawableTransitionOptions.withCrossFade()).into(this)
        return
    }

    request.dontAnimate()
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>,
                isFirstResource: Boolean
            ): Boolean {
                onFinished()
                return false
            }

            override fun onResourceReady(
                resource: Drawable,
                model: Any,
                target: Target<Drawable>?,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {
                onFinished()
                return false
            }
        })
        .into(this)
}
