Stashed 💰

Stashed is a modern, secure, and intuitive Android application designed to help users track their daily expenses, set monthly budget goals, and monitor their financial health in real-time.

🎥 Video Demonstration

Watch the full app walkthrough and API demonstration here:
👉 [Insert your YouTube Link Here] 👈

✨ Key Features

Secure Authentication: User accounts are protected using BCrypt password hashing, ensuring that sensitive login credentials are safe.

Expense Tracking: Easily log daily transactions with categories, descriptions, and custom UI cards.

Budget Management: Set minimum and maximum monthly budget goals.

Live Exchange Rates: Integrates a live REST API to view real-time currency exchange rates.

Cloud Syncing: Powered by Firebase, ensuring user data is reliably stored and accessible.

Automated Logging: Comprehensive background logging (Log.d, Log.e) implemented across repositories and view models for system monitoring and debugging.

🎨 UI & Design System

Stashed features a custom-built, standard XML Dark Mode interface inspired by modern retail and banking applications (e.g., Woolworths app styling).

Main Backgrounds: Deep Dark #050506

Surfaces & Cards: Elevated #111113

Typography: High-contrast #F4F0E6 (Primary) and #8B8A8E (Secondary)

Accents: Gold (#E2B13C) for primary actions and Red (#FF6B5E) for expense deductions.

Components: Rounded CardView layouts (12dp-13dp corners) for transaction items.

🛠 Technical Architecture

Language: Kotlin

UI Toolkit: Standard Android XML (reverted from Jetpack Compose for optimized performance and stability).

Database: Firebase (Migrated to cloud infrastructure for robust data handling).

Networking: Retrofit2 & Gson for REST API integration.

Security: BCrypt for cryptographic password hashing.

REST API Integration

Stashed utilizes an external Exchange Rate REST API to provide users with up-to-date currency conversion metrics.

Implementation: Handled via Retrofit with standard HTTP GET requests.

Error Handling: The UI gracefully handles network timeouts and invalid inputs without crashing, backed by detailed system logs.

🚀 How to Run the App (Side-loading)

Due to virtual machine constraints, the most efficient way to test Stashed is to run the compiled APK directly on a physical Android device.

Download the app-debug.apk file from the /app/build/outputs/apk/debug/ directory.

Transfer the APK to your physical Android device (via Google Drive, Email, or USB).

On your Android device, ensure "Install from Unknown Sources" is enabled in Settings > Security.

Tap the APK file to install and launch Stashed.

🤖 AI Usage Statement

As required by the project rubric, this section documents the use of AI tools during development.

During the development of Stashed, Generative AI (Google Gemini) was utilized strictly as a collaborative coding assistant and tutor.

Debugging & Environment: AI was used to troubleshoot Virtual Machine storage limitations, Gradle cache corruptions (KSP), and ADB deployment issues.

UI Translation: AI assisted in translating high-level design specifications (hex codes, Woolworths-style dark mode) into standard Android XML layout boilerplate.

Architecture Decisions: AI provided structural advice for migrating from Jetpack Compose back to XML and implementing Retrofit for the REST API.
All core logic, database management, and final code integration were manually reviewed, assembled, and tested by the developer.
