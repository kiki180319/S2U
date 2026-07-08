with open('gradle/libs.versions.toml', 'r') as f:
    content = f.read()

new_deps = """firebase-database = { group = "com.google.firebase", name = "firebase-database" }
firebase-firestore = { group = "com.google.firebase", name = "firebase-firestore" }
firebase-storage = { group = "com.google.firebase", name = "firebase-storage" }"""
content = content.replace('firebase-database = { group = "com.google.firebase", name = "firebase-database" }', new_deps)

with open('gradle/libs.versions.toml', 'w') as f:
    f.write(content)

with open('app/build.gradle.kts', 'r') as f:
    gradle = f.read()

new_gradle = """  implementation(libs.firebase.database)
  implementation(libs.firebase.firestore)
  implementation(libs.firebase.storage)"""
gradle = gradle.replace('  implementation(libs.firebase.database)', new_gradle)

with open('app/build.gradle.kts', 'w') as f:
    f.write(gradle)
