package fr.wesy.sevenminutesworkout.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Workout(
    val title: String,
    val description: String,
    val level: String,
    val imageUrl: String,
    val bgColor: String
) : Parcelable
