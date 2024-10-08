package fr.wesy.sevenminutesworkout.util

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

object ImageLoader {

    fun loadUrlImage(
        imageView: ImageView,
        imageUrl: String,
        placeholderResId: Int? = null,
        errorResId: Int? = null
    ) {
        val requestOptions = RequestOptions()

        placeholderResId?.let {
            requestOptions.placeholder(it)
        }

        errorResId?.let {
            requestOptions.error(it)
        }

        Glide.with(imageView.context)
            .setDefaultRequestOptions(requestOptions)
            .load(imageUrl)
            .into(imageView)
    }
}