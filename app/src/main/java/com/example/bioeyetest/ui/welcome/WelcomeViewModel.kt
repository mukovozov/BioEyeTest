package com.example.bioeyetest.ui.welcome

import androidx.lifecycle.ViewModel
import com.example.bioeyetest.Navigator
import com.example.bioeyetest.ui.recognition.FaceRecognitionFragment
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val navigator: Navigator
) : ViewModel() {

    fun onLaunchButtonClicked() {
        navigator.navigateTo(FaceRecognitionFragment.DIRECTION)
    }
}