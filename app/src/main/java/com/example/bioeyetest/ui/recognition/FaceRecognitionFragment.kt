package com.example.bioeyetest.ui.recognition

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.bioeyetest.R
import com.example.bioeyetest.databinding.FragmentFaceRecognitionBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FaceRecognitionFragment : Fragment() {
    private val viewModel: FaceRecognitionViewModel by viewModels()

    private var _binding: FragmentFaceRecognitionBinding? = null
    private val binding: FragmentFaceRecognitionBinding
        get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentFaceRecognitionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.viewState.collect { viewState ->
                    when (viewState) {
                        is FaceRecognitionViewState.Recognition -> {
                            // do nothing for now
                            // TODO: make it look less ugly
                            binding.errorView.errorView.isVisible = false
                            binding.recognitionResult.isVisible = false
                            binding.cameraPreview.isVisible = true
                            binding.completeButton.isVisible = true
                        }

                        is FaceRecognitionViewState.PreparationFailed -> {
                            showError(viewState.reason)
                        }

                        is FaceRecognitionViewState.Preparing -> {
                            // do nothing
                            binding.errorView.errorView.isVisible = false
                            binding.cameraPreview.isVisible = false
                            binding.recognitionResult.isVisible = false
                            binding.completeButton.isVisible = false
                        }
                    }
                }
            }
        }
    }

    private fun setupUi() {
        binding.errorView.retryButton.setOnClickListener {
            viewModel.onRetryButtonClicked()
        }
    }

    private fun showError(reason: PreparationFailedReason) {
        binding.recognitionResult.isVisible = false
        binding.errorView.errorView.isVisible = true
        binding.cameraPreview.isVisible = false
        binding.completeButton.isVisible = false

        val (titleResId, messageResId) = when (reason) {
            PreparationFailedReason.ROOM_IS_TOO_BRIGHT -> {
                R.string.face_recognition_room_is_too_bright_error_title to R.string.face_recognition_room_is_too_bright_error_message
            }

            PreparationFailedReason.ROOM_IS_TOO_DARK -> {
                R.string.face_recognition_room_is_too_dark_error_title to R.string.face_recognition_room_is_too_dark_error_message
            }
        }

        binding.errorView.errorTitle.setText(titleResId)
        binding.errorView.errorMessage.setText(messageResId)
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    companion object {
        const val DIRECTION = "toFaceRecognitionFragment"

        fun newInstance(): FaceRecognitionFragment {
            return FaceRecognitionFragment()
        }
    }
}