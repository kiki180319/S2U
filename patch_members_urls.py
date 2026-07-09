import re

with open('app/src/main/java/com/example/ui/screens/MembersScreen.kt', 'r') as f:
    content = f.read()

url_map = {
    "Jiwoo": "https://raw.githubusercontent.com/kiki180319/S2U/main/assets/.aistudio/jiwoo.jpg",
    "Carmen": "https://raw.githubusercontent.com/kiki180319/S2U/main/assets/.aistudio/carmen.jpeg",
    "Yuha": "https://raw.githubusercontent.com/kiki180319/S2U/main/assets/.aistudio/yuha.webp",
    "Stella": "https://raw.githubusercontent.com/kiki180319/S2U/main/assets/.aistudio/stella.jpg",
    "Juun": "https://raw.githubusercontent.com/kiki180319/S2U/main/assets/.aistudio/juun.png",
    "A-na": "https://raw.githubusercontent.com/kiki180319/S2U/main/assets/.aistudio/ana.jpeg",
    "Ian": "https://raw.githubusercontent.com/kiki180319/S2U/main/assets/.aistudio/ian.jpeg",
    "Ye-on": "https://raw.githubusercontent.com/kiki180319/S2U/main/assets/.aistudio/yeon.jpg"
}

for name, url in url_map.items():
    content = re.sub(
        r'(MemberProfile\(name = "' + name + r'",.*?)imageUrl = ".*?"(\))',
        r'\1imageUrl = "' + url + r'"\2',
        content
    )

with open('app/src/main/java/com/example/ui/screens/MembersScreen.kt', 'w') as f:
    f.write(content)
