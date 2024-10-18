package fr.wesy.sevenminutesworkout.presentation.ui.workout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import fr.wesy.sevenminutesworkout.R
import fr.wesy.sevenminutesworkout.databinding.FragmentWorkoutCongratulationBinding
import fr.wesy.sevenminutesworkout.presentation.animation.AnimationManager
import fr.wesy.sevenminutesworkout.presentation.ui.MainActivity

@AndroidEntryPoint
class WorkoutCongratulationFragment : Fragment() {
    private var _binding: FragmentWorkoutCongratulationBinding? = null
    private val binding get() = _binding!!
    private val animationManager = AnimationManager()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutCongratulationBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        animationManager.startKonfettiAnimation(binding.konfettiView)

        binding.btnFinish.setOnClickListener {
            findNavController().navigate(R.id.navigation_home)
            onDestroyView()
        }

        (activity as MainActivity).hideBottomNavigationView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        (activity as MainActivity).showBottomNavigationView()
    }
}