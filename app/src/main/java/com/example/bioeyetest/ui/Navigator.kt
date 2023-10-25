package com.example.bioeyetest.ui

import android.os.Bundle
import androidx.annotation.IdRes
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

interface Navigator {
    val navEvents: SharedFlow<Direction>

    fun navigateTo(@IdRes actionId: Int, args: Bundle? = null)

    sealed class Direction {
        data class NavigateTo(@IdRes val actionId: Int, val args: Bundle? = null) : Direction()
    }
}

class NavigatorImpl @Inject constructor() : Navigator {
    override val navEvents: MutableSharedFlow<Navigator.Direction> = MutableSharedFlow(extraBufferCapacity = 1)

    override fun navigateTo(@IdRes actionId: Int, args: Bundle?) {
        navEvents.tryEmit(Navigator.Direction.NavigateTo(actionId, args))
    }

}