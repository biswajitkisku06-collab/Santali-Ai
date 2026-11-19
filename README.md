# SANTALI JARVIS AI — Android starter

This is the first Android app foundation for the SANTALI JARVIS project.

## Current status
- Native Android app using Kotlin + Jetpack Compose
- Dark professional chat interface
- Language preference selector (Santali default, Bengali, English, Hindi)
- Placeholder chat response only

## Not connected yet
The app does **not** yet call Gemini, ElevenLabs, a Santali speech model, or a backend. The next development stage is to build a secure backend and connect a multilingual model. API keys must stay on the backend, never inside the APK.

## Build APK with GitHub Actions
1. Create a new GitHub repository named `Santali-JARVIS`.
2. Upload the contents of this ZIP to the repository root (not the ZIP file itself).
3. Open the repository's **Actions** tab.
4. Select **Build Santali JARVIS APK** and run the workflow, or push to `main`.
5. Open the successful workflow run, find **Artifacts**, and download `Santali-JARVIS-debug`.
6. Extract the artifact ZIP to get `app-debug.apk`.

## Build versions
- Android Gradle Plugin 8.7.3
- Gradle 8.9
- Kotlin 2.0.21
- JDK 17
- Compile SDK 35
