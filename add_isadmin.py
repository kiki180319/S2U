import sys

with open('app/src/main/java/com/example/ui/viewmodel/HeartsViewModel.kt', 'r') as f:
    content = f.read()

content = content.replace(
    'val currentTab: StateFlow<String> = _currentTab.asStateFlow()',
    'val currentTab: StateFlow<String> = _currentTab.asStateFlow()\n    val isAdmin: StateFlow<Boolean> = repository.userProfile.map { it?.role == "admin" }.stateIn(viewModelScope, SharingStarted.Lazily, false)'
)

with open('app/src/main/java/com/example/ui/viewmodel/HeartsViewModel.kt', 'w') as f:
    f.write(content)

