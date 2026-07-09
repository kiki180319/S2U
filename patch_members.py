import json
import re

new_members = [
  {
    "stageName": "Jiwoo",
    "fullName": "Choi Ji-woo",
    "nativeName": "최지우",
    "birthday": "2006-09-07",
    "position": "Leader, Dancer, Rapper",
    "heightCm": 169.0,
    "bloodType": "A",
    "mbti": "ISTJ",
    "birthplace": "Seoul, South Korea",
    "nationality": "South Korean",
    "imageUrl": "https://raw.githubusercontent.com/kiki180319/S2U/main/assets/.aistudio/jiwoo.jpg"
  },
  {
    "stageName": "Carmen",
    "fullName": "Nyoman Ayu Carmenita",
    "nativeName": "Nyoman Ayu Carmenita",
    "birthday": "2006-03-28",
    "position": "Dancer, Vocalist",
    "heightCm": 168.0,
    "bloodType": "O",
    "mbti": "ENFJ",
    "birthplace": "Denpasar, Bali, Indonesia",
    "nationality": "Indonesian",
    "imageUrl": "https://raw.githubusercontent.com/kiki180319/S2U/main/assets/.aistudio/carmen.jpeg"
  },
  {
    "stageName": "Yuha",
    "fullName": "Yu Ha-ram",
    "nativeName": "유하람",
    "birthday": "2007-04-12",
    "position": "Main Vocalist, Lead Dancer",
    "heightCm": 164.0,
    "bloodType": "B",
    "mbti": "INTJ",
    "birthplace": "Wonju, Gangwon-do, South Korea",
    "nationality": "South Korean",
    "imageUrl": "https://raw.githubusercontent.com/kiki180319/S2U/main/assets/.aistudio/yuha.webp"
  },
  {
    "stageName": "Stella",
    "fullName": "Kim Da-hyun",
    "nativeName": "김다현",
    "birthday": "2007-06-18",
    "position": "Vocalist, Visual",
    "heightCm": 168.5,
    "bloodType": "O",
    "mbti": "INFP",
    "birthplace": "Ulsan, South Korea",
    "nationality": "South Korean / Canadian",
    "imageUrl": "https://raw.githubusercontent.com/kiki180319/S2U/main/assets/.aistudio/stella.jpg"
  },
  {
    "stageName": "Juun",
    "fullName": "Kim Ju-eun",
    "nativeName": "김주은",
    "birthday": "2008-12-03",
    "position": "Vocalist",
    "heightCm": 166.0,
    "bloodType": "A",
    "mbti": "ISFJ",
    "birthplace": "Ilsan, Gyeonggi-do, South Korea",
    "nationality": "South Korean",
    "imageUrl": "https://raw.githubusercontent.com/kiki180319/S2U/main/assets/.aistudio/juun.png"
  },
  {
    "stageName": "A-na",
    "fullName": "Roh Yu-na",
    "nativeName": "노유나",
    "birthday": "2008-12-20",
    "position": "Rapper, Dancer",
    "heightCm": 171.0,
    "bloodType": "A",
    "mbti": "ESFP",
    "birthplace": "Suwon, Gyeonggi-do, South Korea",
    "nationality": "South Korean",
    "imageUrl": "https://raw.githubusercontent.com/kiki180319/S2U/main/assets/.aistudio/ana.jpeg"
  },
  {
    "stageName": "Ian",
    "fullName": "Jeong Lee-an",
    "nativeName": "정이안",
    "birthday": "2009-10-09",
    "position": "Vocalist, Dancer",
    "heightCm": 164.0,
    "bloodType": "B",
    "mbti": "ENFP",
    "birthplace": "Seoul, South Korea",
    "nationality": "South Korean",
    "imageUrl": "https://raw.githubusercontent.com/kiki180319/S2U/main/assets/.aistudio/ian.jpeg"
  },
  {
    "stageName": "Ye-on",
    "fullName": "Kim Na-yeon",
    "nativeName": "김나연",
    "birthday": "2010-04-19",
    "position": "Vocalist, Maknae",
    "heightCm": 166.0,
    "bloodType": "A",
    "mbti": "INFJ",
    "birthplace": "Yangsan, Gyeongsangnam-do, South Korea",
    "nationality": "South Korean",
    "imageUrl": "https://raw.githubusercontent.com/kiki180319/S2U/main/assets/.aistudio/yeon.jpg"
  }
]

with open('app/src/main/assets/data/members.json', 'w') as f:
    json.dump(new_members, f, indent=2)

with open('app/src/main/java/com/example/ui/screens/MembersScreen.kt', 'r') as f:
    content = f.read()

# Update MemberProfile data class
old_dataclass = """data class MemberProfile(
    val stageName: String,
    val fullName: String,
    val birthday: String,
    val nationality: String,
    val position: String,
    val imageUrl: String
)"""

new_dataclass = """data class MemberProfile(
    val stageName: String,
    val fullName: String,
    val nativeName: String,
    val birthday: String,
    val position: String,
    val heightCm: Double,
    val bloodType: String,
    val mbti: String,
    val birthplace: String,
    val nationality: String,
    val imageUrl: String
)"""

content = content.replace(old_dataclass, new_dataclass)

# Update loadMembers parsing
old_parsing = """                    stageName = obj.optString("stageName", ""),
                    fullName = obj.optString("fullName", ""),
                    birthday = obj.optString("birthday", ""),
                    nationality = obj.optString("nationality", ""),
                    position = obj.optString("position", ""),
                    imageUrl = obj.optString("imageUrl", "")"""

new_parsing = """                    stageName = obj.optString("stageName", ""),
                    fullName = obj.optString("fullName", ""),
                    nativeName = obj.optString("nativeName", ""),
                    birthday = obj.optString("birthday", ""),
                    position = obj.optString("position", ""),
                    heightCm = obj.optDouble("heightCm", 0.0),
                    bloodType = obj.optString("bloodType", ""),
                    mbti = obj.optString("mbti", ""),
                    birthplace = obj.optString("birthplace", ""),
                    nationality = obj.optString("nationality", ""),
                    imageUrl = obj.optString("imageUrl", "")"""

content = content.replace(old_parsing, new_parsing)

# Update MemberDetailScreen to show new fields
old_detail_card = """        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = PremiumDarkGray),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                DetailRow("Stage Name", member.stageName)
                HorizontalDivider(color = PremiumMediumGray, modifier = Modifier.padding(vertical = 12.dp))
                DetailRow("Full Name", member.fullName)
                HorizontalDivider(color = PremiumMediumGray, modifier = Modifier.padding(vertical = 12.dp))
                DetailRow("Position", member.position)
                HorizontalDivider(color = PremiumMediumGray, modifier = Modifier.padding(vertical = 12.dp))
                DetailRow("Birthday", member.birthday)
                HorizontalDivider(color = PremiumMediumGray, modifier = Modifier.padding(vertical = 12.dp))
                DetailRow("Nationality", member.nationality)
            }
        }"""

new_detail_card = """        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = PremiumDarkGray),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                DetailRow("Stage Name", member.stageName)
                HorizontalDivider(color = PremiumMediumGray, modifier = Modifier.padding(vertical = 12.dp))
                DetailRow("Full Name", "${member.fullName} (${member.nativeName})")
                HorizontalDivider(color = PremiumMediumGray, modifier = Modifier.padding(vertical = 12.dp))
                DetailRow("Position", member.position)
                HorizontalDivider(color = PremiumMediumGray, modifier = Modifier.padding(vertical = 12.dp))
                DetailRow("Birthday", member.birthday)
                HorizontalDivider(color = PremiumMediumGray, modifier = Modifier.padding(vertical = 12.dp))
                DetailRow("Birthplace", member.birthplace)
                HorizontalDivider(color = PremiumMediumGray, modifier = Modifier.padding(vertical = 12.dp))
                DetailRow("Nationality", member.nationality)
                HorizontalDivider(color = PremiumMediumGray, modifier = Modifier.padding(vertical = 12.dp))
                DetailRow("Height", "${member.heightCm} cm")
                HorizontalDivider(color = PremiumMediumGray, modifier = Modifier.padding(vertical = 12.dp))
                DetailRow("Blood Type", member.bloodType)
                HorizontalDivider(color = PremiumMediumGray, modifier = Modifier.padding(vertical = 12.dp))
                DetailRow("MBTI", member.mbti)
            }
        }"""

content = content.replace(old_detail_card, new_detail_card)

# Let's fix the Card content scrolling by wrapping DetailScreen Column content inside a scrollable container
old_detail_screen_start = """fun MemberDetailScreen(member: MemberProfile, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {"""

new_detail_screen_start = """import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@Composable
fun MemberDetailScreen(member: MemberProfile, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {"""

# We need to make sure to replace it properly without duplicating imports.
content = re.sub(r'fun MemberDetailScreen\(member: MemberProfile, onBack: \(\) -> Unit\) \{\n    Column\(modifier = Modifier\.fillMaxSize\(\)\.padding\(16\.dp\)\) \{', 
                 r'fun MemberDetailScreen(member: MemberProfile, onBack: () -> Unit) {\n    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(androidx.compose.foundation.rememberScrollState())) {', content)

with open('app/src/main/java/com/example/ui/screens/MembersScreen.kt', 'w') as f:
    f.write(content)
