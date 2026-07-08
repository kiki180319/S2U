#!/bin/bash
sed -i 's/                NavigationDrawerItem(/                if (isAdmin) {\n                    NavigationDrawerItem(/g' app/src/main/java/com/example/ui/screens/MainScreen.kt
sed -i 's/onClick = { viewModel.selectTab("adminpanel"); scope.launch { drawerState.close() } }/onClick = { viewModel.selectTab("adminpanel"); scope.launch { drawerState.close() } }/g' app/src/main/java/com/example/ui/screens/MainScreen.kt

# Wait, sed replacement will match all NavigationDrawerItems. Let's do a more precise sed.
