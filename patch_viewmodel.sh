#!/bin/bash
sed -i 's/val currentTab = _currentTab.asStateFlow()/val currentTab = _currentTab.asStateFlow()\n    val isAdmin: StateFlow<Boolean> = repository.userProfile.map { it?.role == "admin" }.stateIn(viewModelScope, SharingStarted.Lazily, false)/' app/src/main/java/com/example/ui/viewmodel/HeartsViewModel.kt
