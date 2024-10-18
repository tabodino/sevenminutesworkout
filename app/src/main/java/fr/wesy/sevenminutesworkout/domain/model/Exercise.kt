package fr.wesy.sevenminutesworkout.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Exercise(
    val title: String,
    val description: String,
    val imageUrl: String,
    val duration: Int
) : Parcelable
