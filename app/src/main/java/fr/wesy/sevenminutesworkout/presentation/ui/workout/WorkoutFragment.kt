package fr.wesy.sevenminutesworkout.presentation.ui.workout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import fr.wesy.sevenminutesworkout.R
import fr.wesy.sevenminutesworkout.databinding.FragmentWorkoutBinding
import fr.wesy.sevenminutesworkout.presentation.adapter.WorkoutAdapter
@AndroidEntryPoint
class WorkoutFragment : Fragment() {
    private val workoutViewModel: WorkoutViewModel by viewModels()
    private var _binding: FragmentWorkoutBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var workoutAdapter: WorkoutAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        workoutViewModel.loadWorkouts()

        recyclerView = binding.recyclerViewWorkouts

        recyclerView.layoutManager = if (resources.getBoolean(R.bool.is_portrait_only)) {
           LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        } else {
           LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        binding.cardViewMeditation?.setOnClickListener {
            findNavController().navigate(R.id.navigation_meditation)
        }

        binding.cardViewInformation?.setOnClickListener {
            findNavController().navigate(R.id.navigation_information)
        }

        workoutViewModel.workouts.observe(viewLifecycleOwner) { workouts ->
            workoutAdapter = WorkoutAdapter(workouts) { selectedWorkout ->
                val action = WorkoutFragmentDirections
                    .actionWorkoutFragmentToWorkoutDetailFragment(selectedWorkout)
                findNavController().navigate(action)
            }
            recyclerView.adapter = workoutAdapter
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            // Do nothing on back pressed
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}