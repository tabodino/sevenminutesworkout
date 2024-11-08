package fr.wesy.sevenminutesworkout.domain.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class Workout(
    val title: String,
    val description: String,
    val level: String,
    val imageUrl: String,
    val bgColor: String,
    val exercises: List<Exercise>
) : Parcelable
