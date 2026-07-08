import re

with open('app/src/main/java/com/example/ui/viewmodel/HeartsViewModel.kt', 'r') as f:
    content = f.read()

# Replace constructor and base class
content = content.replace(
    'class HeartsViewModel(application: Application) : AndroidViewModel(application) {',
    'import androidx.lifecycle.ViewModel\nimport com.example.data.repository.IHeartsRepository\nimport androidx.lifecycle.ViewModelProvider\nimport androidx.lifecycle.viewmodel.initializer\nimport androidx.lifecycle.viewmodel.viewModelFactory\nimport com.example.HeartsApplication\n\nclass HeartsViewModel(private val repository: IHeartsRepository) : ViewModel() {'
)

# Remove old repository and database initialization
content = re.sub(r'\s*private val database = AppDatabase\.getDatabase\(application\)\n', '\n', content)
content = re.sub(r'\s*private val repository = HeartsRepository\([\s\S]*?communityUpdateDao\(\)\n\s*\)\n', '\n', content)

# Remove `import android.app.Application` and `import androidx.lifecycle.AndroidViewModel` if we want, or just leave them.
content = content.replace('import android.app.Application', '')
content = content.replace('import androidx.lifecycle.AndroidViewModel', '')

# Add ViewModel factory companion object
factory_code = """
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as HeartsApplication)
                HeartsViewModel(repository = application.container.repository)
            }
        }
    }
}
"""

# Append to the very end before the last brace
content = content.rsplit('}', 1)[0] + factory_code

with open('app/src/main/java/com/example/ui/viewmodel/HeartsViewModel.kt', 'w') as f:
    f.write(content)

