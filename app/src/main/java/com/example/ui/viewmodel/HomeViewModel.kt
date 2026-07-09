package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.HeartsApplication
import com.example.data.database.EventEntity
import com.example.data.repository.IHeartsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(private val repository: IHeartsRepository) : ViewModel() {

    // Query for the nearest event: timestamp >= current time, ordered ASC, limit 1.
    val nearestEvent: StateFlow<EventEntity?> = repository.getNearestEvent(System.currentTimeMillis())
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as HeartsApplication)
                HomeViewModel(repository = application.container.repository)
            }
        }
    }
}
