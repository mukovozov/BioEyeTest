package com.example.bioeyetest.ui.recognition_result

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.bioeyetest.databinding.FragmentFaceRecognitionBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FaceRecognitionResultFragment : Fragment() {
    private val viewModel: FaceRecognitionResultViewModel by viewModels()

    private var _binding: FragmentFaceRecognitionBinding? = null
    private val binding: FragmentFaceRecognitionBinding
        get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentFaceRecognitionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    companion object {
        const val DIRECTION = "toFaceRecognitionFragment"

        fun newInstance(): FaceRecognitionResultFragment {
            return FaceRecognitionResultFragment()
        }
    }
}