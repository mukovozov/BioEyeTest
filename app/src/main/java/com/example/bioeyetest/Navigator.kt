package com.example.bioeyetest

import androidx.navigation.NavDirections
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

interface Navigator {
    val navEvents: SharedFlow<Direction>

    fun navigateTo(navDirections: String)

    fun navigateBack()

    sealed class Direction {
        data class NavigateTo(val navDirections: String) : Direction()
        object NavigateBack : Direction()
    }
}

class NavigatorImpl @Inject constructor() : Navigator {
    override val navEvents: MutableSharedFlow<Navigator.Direction> = MutableSharedFlow(extraBufferCapacity = 1)

    override fun navigateTo(navDirections: String) {
        navEvents.tryEmit(Navigator.Direction.NavigateTo(navDirections))
    }

    override fun navigateBack() {
        navEvents.tryEmit(Navigator.Direction.NavigateBack)
    }
}