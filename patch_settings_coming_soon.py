import re

with open('app/src/main/java/com/example/ui/screens/ProfileScreen.kt', 'r') as f:
    content = f.read()

settings_composables_old = """@Composable
fun SettingsActionRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit, value: String = "") {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = label, tint = PremiumWhite)
            Spacer(modifier = Modifier.width(12.dp))
            Text(label, color = PremiumWhite, fontSize = 16.sp)
        }
        if (value.isNotEmpty()) {
            Text(value, color = PremiumLightGray, fontSize = 14.sp)
        } else if (label != "Change Username" && label != "Clear Cache" && label != "Privacy Policy" && label != "Terms of Service" && label != "Licenses" && label != "Bahasa Indonesia" && label != "Korean") {
             // Let's just use empty string or "Coming Soon" if we want
        }
    }
}

@Composable
fun SettingsSwitchRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = label, tint = PremiumWhite)
            Spacer(modifier = Modifier.width(12.dp))
            Text(label, color = PremiumWhite, fontSize = 16.sp)
        }
        IOSSwitch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}"""

settings_composables_new = """@Composable
fun SettingsActionRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit, value: String = "") {
    val context = androidx.compose.ui.platform.LocalContext.current
    val isImplemented = label == "Edit Profile"
    val displayValue = if (!isImplemented && value.isEmpty()) "Coming Soon" else value
    
    val actualOnClick = if (isImplemented) onClick else {
        { android.widget.Toast.makeText(context, "$label: Coming Soon", android.widget.Toast.LENGTH_SHORT).show() }
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { actualOnClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = label, tint = if (isImplemented) PremiumWhite else PremiumLightGray)
            Spacer(modifier = Modifier.width(12.dp))
            Text(label, color = if (isImplemented) PremiumWhite else PremiumLightGray, fontSize = 16.sp)
        }
        if (displayValue.isNotEmpty()) {
            Text(displayValue, color = PremiumLightGray, fontSize = 14.sp)
        }
    }
}

@Composable
fun SettingsSwitchRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val isImplemented = label == "Push Notifications"
    
    val actualOnCheckedChange = if (isImplemented) onCheckedChange else {
        { _: Boolean -> android.widget.Toast.makeText(context, "$label: Coming Soon", android.widget.Toast.LENGTH_SHORT).show() }
    }
    
    Row(
        modifier = Modifier.fillMaxWidth().clickable(enabled = !isImplemented) { actualOnCheckedChange(checked) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = label, tint = if (isImplemented) PremiumWhite else PremiumLightGray)
            Spacer(modifier = Modifier.width(12.dp))
            Text(label, color = if (isImplemented) PremiumWhite else PremiumLightGray, fontSize = 16.sp)
        }
        if (!isImplemented) {
            Text("Coming Soon", color = PremiumLightGray, fontSize = 14.sp)
        } else {
            IOSSwitch(
                checked = checked,
                onCheckedChange = actualOnCheckedChange
            )
        }
    }
}"""

content = content.replace(settings_composables_old, settings_composables_new)

with open('app/src/main/java/com/example/ui/screens/ProfileScreen.kt', 'w') as f:
    f.write(content)
