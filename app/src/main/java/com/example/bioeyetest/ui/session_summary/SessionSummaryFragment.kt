package com.example.bioeyetest.ui.session_summary

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
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

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // handle backPressed in order to clean the cache of the current session.
        requireActivity().onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                viewModel.onBackPressed()
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSessionSummaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.events.collect { event ->
                        onNewEvent(event)
                    }
                }

                launch {
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
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    private fun initUi() {
        binding.shareButton.setOnClickListener {
            viewModel.onShareButtonClicked()
        }

        binding.doneButton.setOnClickListener {
            viewModel.onDoneButtonClicked()
        }
    }

    private fun onNewEvent(event: SessionSummaryEvents) {
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

    // Ideally should be moved to some utils file,
    // but since it's been used only here -- I decided to keep it closer to implementation
    private fun Context.createSharedIntent(uri: Uri): Intent {
        return Intent().apply {
            action = Intent.ACTION_SEND
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            setDataAndType(uri, contentResolver.getType(uri))
            putExtra(Intent.EXTRA_STREAM, uri)
        }
    }
}