# Android Notes App

A modern, feature-rich notes application built with Kotlin and Jetpack Compose, similar to Google Keep.

## 🚀 Features

### Core Features
- ✅ **Create, edit, delete notes**
- ✅ **Rich note content** with title and body
- ✅ **Checklist functionality** - add/remove/check items
- ✅ **Image attachments** from gallery
- ✅ **Voice recording** and attach voice notes
- ✅ **Clean minimal UI** with Material 3 design
- ✅ **Smooth animations** - app launch and note transitions
- ✅ **Dark mode** - system + manual toggle
- ✅ **PDF export** functionality

### Extra Features
- ✅ **Runtime permissions** handling (mic, storage, camera)
- ⏳ **Camera capture** (UI ready, implementation pending)
- ⏳ **Home screen widget** (planned)

## 🏗️ Architecture

- **MVVM** pattern with clean separation
- **Room database** for local storage
- **Hilt** for dependency injection
- **Navigation Compose** for screen navigation
- **Coroutines + Flow** for async operations

## 📱 Tech Stack

- **Kotlin** - Modern Android development
- **Jetpack Compose** - Declarative UI with Material 3
- **Room** - Local database with proper schema
- **Hilt** - Dependency injection
- **Navigation Compose** - Type-safe navigation
- **Coroutines** - Asynchronous programming
- **Material 3** - Modern design system

## 🔧 Setup Instructions

### Prerequisites
- Android Studio Hedgehog | 2023.1.1 or newer
- Android SDK API 24+ (Android 7.0)
- Kotlin 1.8.20+

### Installation
1. Clone the repository
2. Open in Android Studio
3. Sync project with Gradle files
4. Build and run on emulator/device

### Build APK
```bash
./gradlew assembleDebug
```

## 📁 Database Schema

### Notes Table
- `id` (Long, Primary Key)
- `title` (String)
- `content` (String)
- `timestamp` (Long)
- `color` (Int)
- `audioPath` (String?)
- `images` (List<String>)

### Checklist Items Table
- `id` (Long, Primary Key)
- `noteId` (Long, Foreign Key)
- `text` (String)
- `isChecked` (Boolean)
- `order` (Int)

## 🔐 Permissions

- `RECORD_AUDIO` - Voice recording
- `CAMERA` - Camera access
- `READ_MEDIA_IMAGES` - Image access (API 33+)
- `READ_EXTERNAL_STORAGE` - Legacy storage support
- `WRITE_EXTERNAL_STORAGE` - File operations

## 🎨 UI Components

### Main Screens
- **NotesListScreen** - Grid/list view with animations
- **AddEditNoteScreen** - Full note editor with all features

### Key Components
- **ChecklistSection** - Interactive checklist management
- **AudioRecorderSection** - Voice recording UI
- **ImageAttachmentSection** - Gallery integration
- **ColorPickerSection** - Note color customization

## 🌙 Dark Mode

- **System-based** - Follows device settings
- **Manual toggle** - User override option
- **Dynamic colors** - Material You support (API 31+)

## 📤 Export Features

- **PDF Export** - Complete note with all attachments
- **File locations** - Downloads folder for easy access
- **Metadata preservation** - Timestamps and formatting

## 🔄 Animations

- **App launch** - Smooth entrance effects
- **Screen transitions** - Slide and fade animations
- **Note interactions** - Micro-animations for better UX
- **Loading states** - Progress indicators

## 🧪 Testing

- **Unit tests** - Repository and ViewModel logic
- **UI tests** - Compose testing
- **Integration tests** - Database operations
- **Permission tests** - Runtime permission handling

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## 📞 Support

For questions and support, please open an issue in the repository.
