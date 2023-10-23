package com.example.bioeyetest.ui.recognition

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
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
import com.example.bioeyetest.face_recognition.FaceRecognitionResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

                            when (viewState.detectionResult) {
                                FaceRecognitionResult.NO_RESULT -> {
                                    binding.recognitionResult.isVisible = false
                                }

                                FaceRecognitionResult.FACE_DETECTED -> {
                                    binding.recognitionResult.isVisible = true
                                    binding.faceRecognitionResultIcon.setImageResource(R.drawable.ic_face_recognition_success)
                                    binding.faceRecognitionResultTitle.setText(R.string.face_recognition_success_title)
                                    binding.faceRecognitionResultTitle.setTextColor(
                                        ContextCompat.getColor(
                                            requireContext(),
                                            R.color.face_recognition_success
                                        )
                                    )
                                }

                                FaceRecognitionResult.NO_FACE_DETECTED -> {
                                    binding.recognitionResult.isVisible = true
                                    binding.faceRecognitionResultIcon.setImageResource(R.drawable.ic_face_recognition_failure)
                                    binding.faceRecognitionResultTitle.setText(R.string.face_recognition_failure_title)
                                    binding.faceRecognitionResultTitle.setTextColor(
                                        ContextCompat.getColor(
                                            requireContext(),
                                            R.color.face_recognition_failure
                                        )
                                    )
                                }
                            }
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

    // TODO: remove!
    private var isCameraStarted = false

    private fun startCamera() {
        if (isCameraStarted) {
            return
        }

        isCameraStarted = true
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener(
            {
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(binding.cameraPreview.surfaceProvider)
                    }

                val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
                try {
                    cameraProvider.unbindAll()

                    cameraProvider.bindToLifecycle(this, cameraSelector, preview)

                } catch (e: Exception) {
                    Log.e(TAG, "startCamera: ${e.message}", e)
                }
            },
            ContextCompat.getMainExecutor(requireContext())
        )
    }

    private fun startPeriodicRecognition() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Default) {
            var count = 0
            while (count < 30) {
                count++
                withContext(Dispatchers.Main) {
                    binding.cameraPreview.bitmap?.let {
                        viewModel.onNewFrameReady(it)
                        binding.previewFrame.setImageBitmap(it)
                    }
                }
                delay(1000)
            }
        }
    }

    private fun setupUi() {
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
                    startPeriodicRecognition()
                }

                else -> {
                    // do nothing
                }
            }
        }

        startCamera()
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
        private const val TAG = "FaceRecognitionFragment"

        fun newInstance(): FaceRecognitionFragment {
            return FaceRecognitionFragment()
        }
    }
}