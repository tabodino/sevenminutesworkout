package fr.wesy.sevenminutesworkout.presentation.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import fr.wesy.sevenminutesworkout.databinding.ItemWorkoutBinding
import fr.wesy.sevenminutesworkout.domain.model.Workout

class WorkoutAdapter(private val workouts: List<Workout>)
    : RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder>() {

    inner class WorkoutViewHolder(
        private val binding: ItemWorkoutBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(workout: Workout) {
            binding.tvWorkoutTitle.text = workout.title
            binding.tvWorkoutLevel.text = "${workout.level} level"
            // TODO Refactor this part
            Glide.with(binding.root.context)
                .load(workout.imageUrl)
                //.placeholder(R.drawable.placeholder)
                //.error(R.drawable.error)
                .into(binding.ivWorkoutImage)

            val colorHex = workout.bgColor

            try {
                val colorInt = Color.parseColor(colorHex)
                binding.layoutWorkout.setBackgroundColor(colorInt)
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val binding = ItemWorkoutBinding.inflate(LayoutInflater.from(parent.context),
            parent, false)
        return WorkoutViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        holder.bind(workouts[position])
    }

    override fun getItemCount(): Int = workouts.size
}