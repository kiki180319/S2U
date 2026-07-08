#!/bin/bash
sed -i '1s/^/import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset\n/' app/src/main/java/com/example/ui/screens/MainScreen.kt
sed -i '1s/^/import kotlinx.coroutines.launch\n/' app/src/main/java/com/example/ui/screens/MainScreen.kt
sed -i '1s/^/import androidx.compose.material3.DrawerValue\n/' app/src/main/java/com/example/ui/screens/MainScreen.kt
sed -i '1s/^/import androidx.compose.material3.rememberDrawerState\n/' app/src/main/java/com/example/ui/screens/MainScreen.kt
sed -i '1s/^/import androidx.compose.material3.ModalNavigationDrawer\n/' app/src/main/java/com/example/ui/screens/MainScreen.kt
sed -i '1s/^/import androidx.compose.material3.ModalDrawerSheet\n/' app/src/main/java/com/example/ui/screens/MainScreen.kt
sed -i '1s/^/import androidx.compose.material3.NavigationDrawerItem\n/' app/src/main/java/com/example/ui/screens/MainScreen.kt
sed -i '1s/^/import androidx.compose.runtime.rememberCoroutineScope\n/' app/src/main/java/com/example/ui/screens/MainScreen.kt
