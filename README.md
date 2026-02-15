# QR Mailer — Android App

QR-based document email sharing: generate a QR linked to your email, let others scan it and send documents to that email.

## Tech Stack

- **Language:** Kotlin  
- **UI:** Jetpack Compose  
- **Architecture:** MVVM  
- **Navigation:** Jetpack Navigation Compose  
- **QR:** ZXing (generation/decoding), CameraX + ML Kit (scanning)  
- **Email:** Intent to email app, or in-app API fallback  

## Project Structure

```
com.qrmailer
├── data/          (models, repository, network)
├── domain/        (usecases)
├── ui/
│   ├── home/      (Phase 1)
│   ├── qrOwner/   (Phase 2)
│   ├── qrScanner/ (Phase 3)
│   ├── sendOptions/
│   └── common/
└── MainActivity.kt
```

## Phase 0 — Setup Complete

- Kotlin 1.9.20, Compose BOM, Navigation Compose  
- MVVM structure with `data`, `domain`, `ui` packages  
- Dependencies: ZXing, CameraX, ML Kit, Retrofit  
- `QRMailerTheme` and `MainActivity` with Compose  

## How to Build & Test

**Gradle wrapper** is included (`gradle/wrapper/gradle-wrapper.jar` and `gradlew.bat`), so you can build from the command line without installing Gradle.

1. **Android SDK**  
   - **Option A:** Open the project in **Android Studio** (File → Open → select this folder). It will create `local.properties` with `sdk.dir` and you can build/run from the IDE.  
   - **Option B:** From the command line, set the SDK path. Create `local.properties` in the project root with:
     ```properties
     sdk.dir=C\:\\Users\\YourUser\\AppData\\Local\\Android\\Sdk
     ```
     (Use your actual Android SDK path; on Windows use double backslashes.)

2. **Build from command line**  
   ```bash
   .\gradlew.bat assembleDebug   # Windows
   ./gradlew assembleDebug       # macOS/Linux
   ```
   APK output: `app/build/outputs/apk/debug/app-debug.apk`.

3. **Run the app**  
   - In Android Studio: select a device/emulator and click **Run** (green triangle).  
   - Or: `.\gradlew.bat installDebug` then launch “QR Mailer” from the device.

4. **Tests**  
   - Unit tests: `.\gradlew.bat testDebugUnitTest`  
   - Instrumented (UI) tests: connect device/emulator, then `.\gradlew.bat connectedDebugAndroidTest`  
   - See **TESTING.md** for full testing instructions.  

## Development Phases

- **Phase 0** — Project setup ✅  
- **Phase 1** — Home screen & navigation  
- **Phase 2** — QR owner flow (email → QR)  
- **Phase 3** — QR scanner flow  
- **Phase 4** — File selection  
- **Phase 5** — Review & compose  
- **Phase 6** — Email sending options  
- **Phase 7** — In-app email API fallback  
- **Phase 8** — Sending & success  
- **Phase 9** — Error handling  
