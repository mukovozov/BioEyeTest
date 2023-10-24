package com.example.bioeyetest.ui.session_summary

import android.content.Context
import android.content.Intent
import android.net.Uri
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

        binding.shareButton.setOnClickListener {
            viewModel.onShareButtonClicked()
        }

        binding.doneButton.setOnClickListener {
            viewModel.onDoneButtonClicked()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.viewState.collect { viewState ->
                    binding.summaryDurationTotal.text = getString(
                        R.string.session_summary_duration_title,
                        viewState.sessionTotalDurationSeconds
                    )

                    binding.summaryDurationSuccess.text = getString(
                        R.string.session_summary_face_detected_title,
                        viewState.faceDetectedDurationSeconds
                    )

                    binding.summaryDurationFail.text = getString(
                        R.string.session_summary_no_face_detected_title,
                        viewState.noFaceDetectedDurationSeconds
                    )
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { event ->
                    when (event) {
                        is SessionSummaryEvents.ShareCSV -> {
                            val intent = context?.createSharedIntent(event.contentUri)
                            startActivity(
                                Intent.createChooser(
                                    intent,
                                    getString(R.string.session_summary_share_chooser_title)
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    private fun Context.createSharedIntent(uri: Uri): Intent {
        return Intent().apply {
            action = Intent.ACTION_SEND
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            setDataAndType(uri, contentResolver.getType(uri))
            putExtra(Intent.EXTRA_STREAM, uri)
        }
    }


    companion object {
        const val DIRECTION = "toSessionSummaryFragment"

        fun newInstance(): SessionSummaryFragment {
            return SessionSummaryFragment()
        }
    }
}