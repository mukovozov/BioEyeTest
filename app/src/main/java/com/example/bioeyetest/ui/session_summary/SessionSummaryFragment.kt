package com.example.bioeyetest.ui.session_summary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.bioeyetest.R
import com.example.bioeyetest.databinding.FragmentSessionSummaryBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SessionSummaryFragment : Fragment() {

    private val viewModel: SessionSummaryViewModel by viewModels()

    private var _binding: FragmentSessionSummaryBinding? = null
    private val binding: FragmentSessionSummaryBinding
        get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSessionSummaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.viewState.collect { viewState ->
                    val totalDurationTitle = getString(R.string.session_summary_duration_title, viewState.sessionTotalDurationSeconds)
                    val faceDurationTitle = getString(R.string.session_summary_face_detected_title, viewState.faceDetectedDurationSeconds)
                    val noFaceDurationTitle = getString(R.string.session_summary_no_face_detected_title, viewState.noFaceDetectedDurationSeconds)
                    binding.summaryDurationTotal.text = totalDurationTitle
                    binding.summaryDurationSuccess.text = faceDurationTitle
                    binding.summaryDurationFail.text = noFaceDurationTitle
                }
            }
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    companion object {
        const val DIRECTION = "toSessionSummaryFragment"

        fun newInstance(): SessionSummaryFragment {
            return SessionSummaryFragment()
        }
    }
}