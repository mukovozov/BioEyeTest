package com.example.bioeyetest.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.bioeyetest.Navigator
import com.example.bioeyetest.R
import com.example.bioeyetest.databinding.ActivityMainBinding
import com.example.bioeyetest.ui.recognition_result.FaceRecognitionResultFragment
import com.example.bioeyetest.ui.welcome.WelcomeFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var navigator: Navigator

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        val navController = binding.navHostFragment.getFragment<NavHostFragment>().navController

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                navigator.navEvents.collect { direction ->
                    when (direction) {
                        is Navigator.Direction.NavigateTo -> {
//                            navController.navigate(direction.navDirections)
                            val fragment = when (direction.navDirections) {
                                FaceRecognitionResultFragment.DIRECTION -> FaceRecognitionResultFragment.newInstance()
                                WelcomeFragment.DIRECTION -> WelcomeFragment.newInstance()
                                else -> throw IllegalStateException("Unknown direction")
                            }

                            openFragment(fragment)
                        }

                        is Navigator.Direction.NavigateBack -> {
//                            if (!navController.popBackStack()) {
                            if (supportFragmentManager.backStackEntryCount == 0) {
                                finish()
                            }
                        }
                    }
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        navigator.navigateTo(WelcomeFragment.DIRECTION)
    }

    private fun openFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}