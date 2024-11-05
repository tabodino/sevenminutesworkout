package fr.wesy.sevenminutesworkout.presentation.ui.workout

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import fr.wesy.sevenminutesworkout.R
import fr.wesy.sevenminutesworkout.databinding.FragmentWorkoutExerciseBinding
import fr.wesy.sevenminutesworkout.databinding.CustomAlertDialogBinding
import fr.wesy.sevenminutesworkout.domain.model.Workout
import fr.wesy.sevenminutesworkout.presentation.ui.MainActivity
import fr.wesy.sevenminutesworkout.presentation.ui.network.NetworkViewModel
import fr.wesy.sevenminutesworkout.util.Constants.MILLIS_TIME
import fr.wesy.sevenminutesworkout.util.Constants.REST_TIME
import fr.wesy.sevenminutesworkout.util.ImageLoader

@AndroidEntryPoint
class WorkoutExerciseFragment : Fragment() {
    private val workoutExerciseViewModel: WorkoutExerciseViewModel by viewModels()
    private val networkViewModel: NetworkViewModel by viewModels()
    private val args: WorkoutExerciseFragmentArgs by navArgs()
    private var _binding: FragmentWorkoutExerciseBinding? = null
    private val binding get() = _binding!!
    private lateinit var workout: Workout
    private var exerciseTitle: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutExerciseBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        networkViewModel.isConnected.observe(viewLifecycleOwner) { isConnected ->
            if (!isConnected) {
                (activity as MainActivity).showNoConnectionDialog()
            }
        }

        handleBackPressed()

        (activity as MainActivity).setBottomNavVisibility(false)
        (activity as MainActivity).setHamburgerButtonVisibility(false)

        setupObservers()

        workout = args.workout

        workoutExerciseViewModel.startWorkout(workout)

        binding.btnPause.setOnClickListener {
            workoutExerciseViewModel.togglePauseResumeTimer()
        }

        binding.btnNext.setOnClickListener {
            workoutExerciseViewModel.goToNextExercise()
        }

        binding.btnPrevious.setOnClickListener {
            workoutExerciseViewModel.goToPreviousExercise()
        }
    }

    private fun setupObservers() {
        // Observer for pause/resume state
        workoutExerciseViewModel.isPaused.observe(viewLifecycleOwner) { isPaused ->
            if (isPaused) {
                binding.btnPause.setImageResource(R.drawable.ic_play_48dp)
            } else {
                binding.btnPause.setImageResource(R.drawable.ic_pause_48dp)
            }
        }

        workoutExerciseViewModel.exerciseTitle.observe(viewLifecycleOwner) { title ->
            exerciseTitle = title
            binding.tvExerciseTitle.text = title
        }

        workoutExerciseViewModel.exerciseImageUrl.observe(viewLifecycleOwner) { imageUrl ->
            ImageLoader.loadUrlImage(
                binding.ivWorkoutImage,
                imageUrl,
                R.drawable.no_image_placeholder
            )
        }

        workoutExerciseViewModel.currentExerciseIndex.observe(viewLifecycleOwner) { index ->
            binding.btnPrevious.isEnabled = index != 0
        }

        workoutExerciseViewModel.timerText.observe(viewLifecycleOwner) { timerText ->
            binding.tvTimer.text = timerText
        }

        workoutExerciseViewModel.isWorkoutCompleted.observe(viewLifecycleOwner) { isCompleted ->
            if (isCompleted) {
                findNavController().navigate(R.id.navigation_congratulations)
            }
        }

        workoutExerciseViewModel.progressBarProgress.observe(viewLifecycleOwner) { progress ->
            updateProgressBar(progress)
        }
    }

    private fun updateProgressBar(progress: Int) {
        if (workoutExerciseViewModel.isRestPhase.value == true) {
            binding.ivWorkoutImage.setImageResource(R.drawable.rest_time)
            binding.tvExerciseTitle.text =
                getString(R.string.rest_for_n_seconds, (REST_TIME / MILLIS_TIME).toString())
        }
        binding.progressBarCircle.max = workoutExerciseViewModel.progressBarMax.value!!
        binding.progressBarCircle.progress = progress
    }

    private fun handleBackPressed() {
        binding.ivBackArrow.setOnClickListener {
            showExitConfirmationDialog()
        }
    }

    private fun showExitConfirmationDialog() {
        workoutExerciseViewModel.togglePauseResumeTimer()
        val binding = CustomAlertDialogBinding.inflate(LayoutInflater.from(context))
        val alertDialog = AlertDialog.Builder(context)
            .setView(binding.root)
            .setCancelable(false)
            .create()

        binding.btnCancel.setOnClickListener {
            alertDialog.dismiss()
            workoutExerciseViewModel.togglePauseResumeTimer()
        }
        binding.btnConfirm.setOnClickListener {
            alertDialog.dismiss()
            findNavController().navigate(R.id.navigation_workouts)
            onDestroyView()
        }

        alertDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

        val navController = findNavController()
        if (navController.currentDestination?.id != R.id.navigation_congratulations) {
            (activity as MainActivity).setBottomNavVisibility(true)
            (activity as MainActivity).setHamburgerButtonVisibility(true)
        }
    }
}