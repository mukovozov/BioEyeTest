package com.example.bioeyetest.ui.recognition

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.camera.view.PreviewView.StreamState
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.bioeyetest.R
import com.example.bioeyetest.databinding.FragmentFaceRecognitionBinding
import com.example.bioeyetest.data.face_recognition.FaceRecognitionResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FaceRecognitionFragment : Fragment() {
    private val viewModel: FaceRecognitionViewModel by viewModels()

    private var _binding: FragmentFaceRecognitionBinding? = null
    private val binding: FragmentFaceRecognitionBinding
        get() = _binding!!

    private lateinit var cameraController: LifecycleCameraController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentFaceRecognitionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.events.collect { events ->
                        onNewEvent(events)
                    }
                }

                launch {
                    viewModel.viewState.collect { viewState ->
                        render(viewState)
                    }
                }
            }
        }
    }

    private fun onNewEvent(events: FaceRecognitionEvents) {
        when (events) {
            is FaceRecognitionEvents.ProvideNextFrame -> {
                // Not sure if it's an optimal solution to extract the frame from the camera,
                // but the easiest I could find.
                // Tried using ImageAnalyzer as well, but it seems like it's more suitable
                // for continuous analysis during a camera session,
                // but not processing once per second as we do
                binding.cameraPreview.bitmap?.let { bitmap ->
                    viewModel.onNewFrameReady(bitmap)
                    binding.previewFrame.setImageBitmap(bitmap)
                }
            }
        }
    }

    private fun render(viewState: FaceRecognitionViewState) {
        when (viewState) {
            is FaceRecognitionViewState.Recognition -> {
                showFaceRecognitionState(viewState)
            }

            is FaceRecognitionViewState.PreparationFailed -> {
                showErrorState(viewState.reason)
            }

            is FaceRecognitionViewState.Preparing -> {
                binding.errorView.errorView.isVisible = false
                binding.cameraPreview.isVisible = false
                binding.recognitionResult.isVisible = false
                binding.completeButton.isVisible = false
            }
        }
    }

    private fun initUi() {
        binding.errorView.retryButton.setOnClickListener {
            viewModel.onRetryButtonClicked()
        }

        binding.completeButton.setOnClickListener {
            viewModel.onCompleteSessionButtonClicked()
        }

        binding.cameraPreview.implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        binding.cameraPreview.previewStreamState.observe(viewLifecycleOwner) { streamState ->
            when (streamState) {
                StreamState.STREAMING -> {
                    viewModel.onCameraReady()
                }

                else -> {
                    // do nothing
                }
            }
        }

        prepareCamera()
    }

    private fun prepareCamera() {
        val context = context ?: return

        cameraController = LifecycleCameraController(context).apply {
            bindToLifecycle(this@FaceRecognitionFragment)
            cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
        }

        binding.cameraPreview.controller = cameraController
    }

    private fun showFaceRecognitionState(viewState: FaceRecognitionViewState.Recognition) {
        binding.errorView.errorView.isVisible = false
        binding.recognitionResult.isVisible = false
        binding.cameraPreview.isVisible = true
        binding.completeButton.isVisible = true

        when (viewState.detectionResult) {
            FaceRecognitionResult.NO_RESULT -> {
                binding.recognitionResult.isVisible = false
            }

            FaceRecognitionResult.FACE_DETECTED -> {
                showFaceDetectedState()
            }

            FaceRecognitionResult.NO_FACE_DETECTED -> {
                showNoFaceDetectedState()
            }
        }
    }

    private fun showFaceDetectedState() = with(binding) {
        recognitionResult.isVisible = true
        faceRecognitionResultIcon.setImageResource(R.drawable.ic_face_recognition_success)
        faceRecognitionResultTitle.setText(R.string.face_recognition_success_title)
        faceRecognitionResultTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.face_recognition_success
            )
        )
    }

    private fun showNoFaceDetectedState() = with(binding) {
        recognitionResult.isVisible = true
        faceRecognitionResultIcon.setImageResource(R.drawable.ic_face_recognition_failure)
        faceRecognitionResultTitle.setText(R.string.face_recognition_failure_title)
        faceRecognitionResultTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.face_recognition_failure
            )
        )
    }

    private fun showErrorState(reason: PreparationFailedReason) = with(binding) {
        recognitionResult.isVisible = false
        errorView.errorView.isVisible = true
        cameraPreview.isVisible = false
        completeButton.isVisible = false

        val (titleResId, messageResId) = when (reason) {
            PreparationFailedReason.ROOM_IS_TOO_BRIGHT -> {
                R.string.face_recognition_room_is_too_bright_error_title to R.string.face_recognition_room_is_too_bright_error_message
            }

            PreparationFailedReason.ROOM_IS_TOO_DARK -> {
                R.string.face_recognition_room_is_too_dark_error_title to R.string.face_recognition_room_is_too_dark_error_message
            }
        }

        errorView.errorTitle.setText(titleResId)
        errorView.errorMessage.setText(messageResId)
    }

    override fun onStop() {
        viewModel.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}