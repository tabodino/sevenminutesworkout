package fr.wesy.sevenminutesworkout.presentation.ui.meditation

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import fr.wesy.sevenminutesworkout.R
import fr.wesy.sevenminutesworkout.databinding.FragmentMeditationBinding
import fr.wesy.sevenminutesworkout.presentation.animation.BreathingAnimationView

private const val DEFAULT_MINUTES_DURATION : Long = 7

@AndroidEntryPoint
class MeditationFragment : Fragment() {

    private val meditationViewModel: MeditationViewModel by viewModels()
    private var _binding: FragmentMeditationBinding? = null
    private val binding get() = _binding!!
    private lateinit var breathingAnimationView: BreathingAnimationView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMeditationBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSelectDuration.setOnClickListener {
            showDurationPopupMenu(it)
        }

        startMeditation(DEFAULT_MINUTES_DURATION)

        setupObservers()

        breathingAnimationView = binding.breathingAnimationView

        binding.btnSelectDuration.text = getDurationText(DEFAULT_MINUTES_DURATION)

        binding.switchMusic.setOnCheckedChangeListener { _, isChecked ->
            meditationViewModel.toggleSoundMute(isChecked)
        }

        binding.btnPlayPause.setOnClickListener {
            meditationViewModel.togglePauseResumeMeditation()
        }
    }

    private fun setupObservers() {
        meditationViewModel.isPaused.observe(viewLifecycleOwner) { isPaused ->
            if (isPaused) {
                binding.btnPlayPause.setImageResource(R.drawable.ic_play_48dp)
                breathingAnimationView.pauseAnimation()
            } else {
                binding.btnPlayPause.setImageResource(R.drawable.ic_pause_48dp)
                breathingAnimationView.resumeAnimation()
            }
        }

        meditationViewModel.timerText.observe(viewLifecycleOwner) { time ->
            binding.tvTimer.text = time
        }

        meditationViewModel.isMeditationComplete.observe(viewLifecycleOwner) { isComplete ->
            if (isComplete) {
                showMeditationCompleteDialog()
            }
        }
    }

    private fun showDurationPopupMenu(view: View) {
        val popup = PopupMenu(requireContext(), view)
        popup.menuInflater.inflate(R.menu.duration_menu, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            val durationInMinutes = when (item.itemId) {
                R.id.duration_1_minute -> 1
                R.id.duration_3_minutes -> 3
                R.id.duration_5_minutes -> 5
                R.id.duration_7_minutes -> 7
                else -> return@setOnMenuItemClickListener false
            }
            updateMeditationDuration(durationInMinutes.toLong())
            true
        }
        popup.show()
    }

    private fun updateMeditationDuration(durationInMinutes: Long) {
        val durationText = getDurationText(durationInMinutes)
        binding.btnSelectDuration.text = durationText
        resetMeditation()
        startMeditation(durationInMinutes)
    }

    private fun startMeditation(duration: Long) {
        meditationViewModel.startMeditation(
            duration, binding.switchMusic.isChecked, requireContext()
        )
    }

    private fun showMeditationCompleteDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.meditation_completed)
            .setMessage(R.string.meditation_session_over)
            .setPositiveButton(R.string.ok) { dialog, _ ->
                dialog.dismiss()
                findNavController().navigate(R.id.navigation_home)
            }
            .show()
    }

    private fun getDurationText(durationInMinutes: Long): String {
        return resources.getQuantityString(
            R.plurals.meditation_duration,
            durationInMinutes.toInt(),
            durationInMinutes
        )
    }

    private fun resetMeditation() {
        meditationViewModel.resetPause()
        meditationViewModel.stopMusic()
        binding.btnPlayPause.setImageResource(R.drawable.ic_pause_48dp)
    }

    override fun onPause() {
        super.onPause()
        meditationViewModel.stopMusic()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        resetMeditation()
        _binding = null
    }
}