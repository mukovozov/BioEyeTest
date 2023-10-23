package com.example.bioeyetest.ui

import androidx.navigation.NavDirections

interface Event {
    data class NavigateTo(val navDirections: NavDirections) : Event
}