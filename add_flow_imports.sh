#!/bin/bash
sed -i '1s/^/import kotlinx.coroutines.flow.map\nimport kotlinx.coroutines.flow.stateIn\nimport kotlinx.coroutines.flow.SharingStarted\n/' app/src/main/java/com/example/ui/viewmodel/HeartsViewModel.kt
