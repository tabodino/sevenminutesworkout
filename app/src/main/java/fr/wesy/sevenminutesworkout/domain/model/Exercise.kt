package fr.wesy.sevenminutesworkout.domain.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class Exercise(
    val title: String,
    val description: String,
    val imageUrl: String,
    val duration: Int
) : Parcelable
