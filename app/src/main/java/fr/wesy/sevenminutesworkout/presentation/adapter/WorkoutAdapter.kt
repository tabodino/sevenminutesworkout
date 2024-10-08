package fr.wesy.sevenminutesworkout.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.wesy.sevenminutesworkout.R
import fr.wesy.sevenminutesworkout.databinding.ItemWorkoutBinding
import fr.wesy.sevenminutesworkout.domain.model.Workout
import fr.wesy.sevenminutesworkout.util.ColorUtil
import fr.wesy.sevenminutesworkout.util.ImageLoader

class WorkoutAdapter(
    private val workouts: List<Workout>,
    private val onItemClick: (Workout) -> Unit
) : RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder>() {

    inner class WorkoutViewHolder(
        private val binding: ItemWorkoutBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(workout: Workout) {
            binding.tvWorkoutTitle.text = workout.title
            binding.tvWorkoutLevel.text = binding.root.context.getString(
                R.string.workout_level, workout.level
            )

            ImageLoader.loadUrlImage(binding.ivWorkoutImage, workout.imageUrl)

            ColorUtil.applyBackgroundColor(binding.layoutWorkout, workout.bgColor)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val binding = ItemWorkoutBinding.inflate(LayoutInflater.from(parent.context),
            parent, false)
        return WorkoutViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        val workout = workouts[position]
        holder.bind(workout)
        holder.itemView.setOnClickListener {
            onItemClick(workout)
        }
    }

    override fun getItemCount(): Int = workouts.size
}