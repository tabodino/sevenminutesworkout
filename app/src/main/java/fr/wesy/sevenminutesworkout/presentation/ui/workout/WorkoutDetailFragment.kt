package fr.wesy.sevenminutesworkout.presentation.ui.workout

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import fr.wesy.sevenminutesworkout.R
import fr.wesy.sevenminutesworkout.databinding.FragmentWorkoutDetailBinding
import fr.wesy.sevenminutesworkout.presentation.ui.MainActivity
import fr.wesy.sevenminutesworkout.util.ColorUtil
import fr.wesy.sevenminutesworkout.util.ImageLoader

@AndroidEntryPoint
class WorkoutDetailFragment : Fragment() {

    private val args: WorkoutDetailFragmentArgs by navArgs()
    private var _binding: FragmentWorkoutDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutDetailBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val selectedWorkout = args.selectedWorkout

        ColorUtil.applyBackgroundColor(binding.layoutWorkoutDetail, selectedWorkout.bgColor)

        ImageLoader.loadUrlImage(binding.ivWorkoutImage, selectedWorkout.imageUrl)

        binding.tvWorkoutTitle.text = selectedWorkout.title
        binding.tvWorkoutDescription.text = selectedWorkout.description
        binding.tvWorkoutLevel.text = getString(R.string.workout_level, selectedWorkout.level)

        handleBackPressed()

        (activity as MainActivity).hideBottomNavigationView()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        (activity as MainActivity).showBottomNavigationView()
    }

    private fun handleBackPressed() {
        binding.ivBackArrow.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }
}