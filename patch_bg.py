with open('app/src/main/res/drawable/ic_launcher_background.xml', 'r') as f:
    content = f.read()

import re
content = re.sub(r'android:fillColor=".*?"', 'android:fillColor="#FFEB3B"', content)

with open('app/src/main/res/drawable/ic_launcher_background.xml', 'w') as f:
    f.write(content)
