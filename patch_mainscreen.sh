#!/bin/bash
sed -i 's/val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()/val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()\n    val isAdmin by viewModel.isAdmin.collectAsStateWithLifecycle()/' app/src/main/java/com/example/ui/screens/MainScreen.kt
