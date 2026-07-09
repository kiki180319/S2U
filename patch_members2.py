with open('app/src/main/java/com/example/ui/screens/MembersScreen.kt', 'r') as f:
    content = f.read()

# We need to remove the expanding animation that shows birthDate, MBTI, etc.
# since we don't have that info anymore.

new_member_card = """@Composable
fun MemberCard(member: MemberProfile) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PremiumDarkGray),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = member.imageUrl,
                    contentDescription = member.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(PremiumMediumGray)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(member.name, color = PremiumWhite, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text(member.nativeName, color = PremiumLightGray, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(member.position, color = HeartsPink, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
"""

import re
content = re.sub(r'@Composable\nfun MemberCard\(member: MemberProfile\).*', new_member_card, content, flags=re.DOTALL)

with open('app/src/main/java/com/example/ui/screens/MembersScreen.kt', 'w') as f:
    f.write(content)
