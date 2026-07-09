import re

with open('app/src/main/java/com/example/ui/screens/ProfileScreen.kt', 'r') as f:
    content = f.read()

# Add imports
imports_to_add = """import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.AccountCircle
"""

# Insert imports after existing icon imports
content = re.sub(r'import androidx.compose.material.icons.filled.Person', r'import androidx.compose.material.icons.filled.Person\n' + imports_to_add, content)

# Replace the Settings section
old_settings_pattern = re.compile(r'// Settings\s+item \{.*?Spacer\(modifier = Modifier.height\(80.dp\)\)\n\s+\}', re.DOTALL)

new_settings = """// Settings
            item {
                Text(
                    text = "Account",
                    color = PremiumWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                )
                
                Surface(
                    color = PremiumDarkGray,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SettingsActionRow(
                            icon = Icons.Default.Person,
                            label = "Edit Profile",
                            onClick = { showEditProfile = true }
                        )
                        HorizontalDivider(color = PremiumMediumGray, modifier = Modifier.padding(vertical = 12.dp))
                        SettingsActionRow(
                            icon = Icons.Default.AccountCircle,
                            label = "Change Username",
                            onClick = { /* TODO */ }
                        )
                        HorizontalDivider(color = PremiumMediumGray, modifier = Modifier.padding(vertical = 12.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showLogoutDialog = true },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.ExitToApp, contentDescription = "Logout", tint = DestructiveRed)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("Logout", color = DestructiveRed, fontSize = 16.sp)
                            }
                        }
                    }
                }
            }
            
            item {
                Text(
                    text = "Appearance",
                    color = PremiumWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp, top = 8.dp)
                )
                
                Surface(
                    color = PremiumDarkGray,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SettingsSwitchRow(
                            icon = Icons.Default.Palette,
                            label = "Dark Mode",
                            checked = true,
                            onCheckedChange = { /* TODO */ }
                        )
                        HorizontalDivider(color = PremiumMediumGray, modifier = Modifier.padding(vertical = 12.dp))
                        SettingsActionRow(
                            icon = Icons.Default.ColorLens,
                            label = "Theme Color",
                            onClick = { /* TODO */ },
                            value = "Pink"
                        )
                    }
                }
            }
            
            item {
                Text(
                    text = "Notifications",
                    color = PremiumWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp, top = 8.dp)
                )
                
                Surface(
                    color = PremiumDarkGray,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SettingsSwitchRow(
                            icon = Icons.Default.Notifications,
                            label = "Push Notifications",
                            checked = notificationsEnabled,
                            onCheckedChange = { notificationsEnabled = it }
                        )
                        HorizontalDivider(color = PremiumMediumGray, modifier = Modifier.padding(vertical = 12.dp))
                        SettingsSwitchRow(
                            icon = Icons.Default.Notifications,
                            label = "News Notifications",
                            checked = true,
                            onCheckedChange = { /* TODO */ }
                        )
                        HorizontalDivider(color = PremiumMediumGray, modifier = Modifier.padding(vertical = 12.dp))
                        SettingsSwitchRow(
                            icon = Icons.Default.Notifications,
                            label = "Live Notifications",
                            checked = true,
                            onCheckedChange = { /* TODO */ }
                        )
                    }
                }
            }
            
            item {
                Text(
                    text = "Language",
                    color = PremiumWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp, top = 8.dp)
                )
                
                Surface(
                    color = PremiumDarkGray,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SettingsActionRow(
                            icon = Icons.Default.Language,
                            label = "English",
                            onClick = { /* TODO */ },
                            value = "Selected"
                        )
                        HorizontalDivider(color = PremiumMediumGray, modifier = Modifier.padding(vertical = 12.dp))
                        SettingsActionRow(
                            icon = Icons.Default.Language,
                            label = "Bahasa Indonesia",
                            onClick = { /* TODO */ }
                        )
                        HorizontalDivider(color = PremiumMediumGray, modifier = Modifier.padding(vertical = 12.dp))
                        SettingsActionRow(
                            icon = Icons.Default.Language,
                            label = "Korean",
                            onClick = { /* TODO */ }
                        )
                    }
                }
            }
            
            item {
                Text(
                    text = "Data",
                    color = PremiumWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp, top = 8.dp)
                )
                
                Surface(
                    color = PremiumDarkGray,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SettingsActionRow(
                            icon = Icons.Default.Delete,
                            label = "Clear Cache",
                            onClick = { /* TODO */ }
                        )
                        HorizontalDivider(color = PremiumMediumGray, modifier = Modifier.padding(vertical = 12.dp))
                        SettingsActionRow(
                            icon = Icons.Default.Storage,
                            label = "Image Cache Size",
                            onClick = { /* TODO */ },
                            value = "124 MB"
                        )
                    }
                }
            }
            
            item {
                Text(
                    text = "About",
                    color = PremiumWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp, top = 8.dp)
                )
                
                Surface(
                    color = PremiumDarkGray,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SettingsInfoRow(
                            icon = Icons.Default.Info,
                            label = "App Version",
                            value = "1.0.0"
                        )
                        HorizontalDivider(color = PremiumMediumGray, modifier = Modifier.padding(vertical = 12.dp))
                        SettingsActionRow(
                            icon = Icons.Default.Security,
                            label = "Privacy Policy",
                            onClick = { /* TODO */ }
                        )
                        HorizontalDivider(color = PremiumMediumGray, modifier = Modifier.padding(vertical = 12.dp))
                        SettingsActionRow(
                            icon = Icons.Default.Description,
                            label = "Terms of Service",
                            onClick = { /* TODO */ }
                        )
                        HorizontalDivider(color = PremiumMediumGray, modifier = Modifier.padding(vertical = 12.dp))
                        SettingsActionRow(
                            icon = Icons.Default.Gavel,
                            label = "Licenses",
                            onClick = { /* TODO */ }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(80.dp))
            }"""

content = old_settings_pattern.sub(new_settings, content)

# Add SettingsRow Composables at the end
settings_composables = """

@Composable
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
}

@Composable
fun SettingsInfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
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
        Text(value, color = PremiumLightGray, fontSize = 14.sp)
    }
}
"""

content += settings_composables

with open('app/src/main/java/com/example/ui/screens/ProfileScreen.kt', 'w') as f:
    f.write(content)
