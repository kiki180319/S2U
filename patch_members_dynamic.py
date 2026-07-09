import re

with open('app/src/main/java/com/example/ui/screens/MembersScreen.kt', 'r') as f:
    content = f.read()

# Replace static membersList with loading function and context
old_imports = """import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.*"""

new_imports = """import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.*
import org.json.JSONArray
import java.io.InputStreamReader"""

content = content.replace(old_imports, new_imports)

# Remove the static list
static_list_pattern = re.compile(r'val membersList = listOf\([\s\S]*?\)\n\n@Composable', re.MULTILINE)

new_func = """fun loadMembers(context: android.content.Context): List<MemberProfile> {
    return try {
        val inputStream = context.assets.open("data/members.json")
        val jsonString = InputStreamReader(inputStream).readText()
        val jsonArray = JSONArray(jsonString)
        val members = mutableListOf<MemberProfile>()
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            members.add(
                MemberProfile(
                    stageName = obj.optString("stageName", ""),
                    fullName = obj.optString("fullName", ""),
                    birthday = obj.optString("birthday", ""),
                    nationality = obj.optString("nationality", ""),
                    position = obj.optString("position", ""),
                    imageUrl = obj.optString("imageUrl", "")
                )
            )
        }
        members
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    }
}

@Composable"""

content = static_list_pattern.sub(new_func, content)

# Update MembersScreen
old_members_screen = """fun MembersScreen(onBack: () -> Unit) {
    var selectedMember by remember { mutableStateOf<MemberProfile?>(null) }"""

new_members_screen = """fun MembersScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val membersList = remember { loadMembers(context) }
    var selectedMember by remember { mutableStateOf<MemberProfile?>(null) }"""

content = content.replace(old_members_screen, new_members_screen)

with open('app/src/main/java/com/example/ui/screens/MembersScreen.kt', 'w') as f:
    f.write(content)
