with open('app/src/main/java/com/example/HeartsApplication.kt', 'r') as f:
    content = f.read()

import_statement = "import com.example.data.repository.FirebaseHeartsRepository\n"
content = content.replace('import com.example.data.repository.HeartsRepository', 'import com.example.data.repository.HeartsRepository\n' + import_statement)

# Replace repository instantiation
old_repo = """    override val repository: IHeartsRepository by lazy {
        HeartsRepository(
            database.userDao(),
            database.eventDao(),
            database.forumDao(),
            database.commentDao(),
            database.videoDao(),
            database.eventCommentDao(),
            database.eventAttendeeDao(),
            database.communityUpdateDao()
        )
    }"""

new_repo = """    override val repository: IHeartsRepository by lazy {
        FirebaseHeartsRepository(
            application,
            database.userDao(),
            database.eventDao(),
            database.forumDao(),
            database.commentDao(),
            database.videoDao(),
            database.eventCommentDao(),
            database.eventAttendeeDao(),
            database.communityUpdateDao()
        )
    }"""

content = content.replace(old_repo, new_repo)

with open('app/src/main/java/com/example/HeartsApplication.kt', 'w') as f:
    f.write(content)
