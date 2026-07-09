with open('app/src/main/res/drawable/ic_launcher_foreground.xml', 'r') as f:
    content = f.read()

import re
content = re.sub(r'android:drawable="@drawable/.*?"', 'android:drawable="@drawable/img_logo"', content)

with open('app/src/main/res/drawable/ic_launcher_foreground.xml', 'w') as f:
    f.write(content)
