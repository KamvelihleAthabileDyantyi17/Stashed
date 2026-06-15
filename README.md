# Stashed — Personal Budget Planner

A personal budget tracking Android app built in Kotlin using Room Database.

## App Features
- User registration and secure login
- Add and categorise expenses
- Set monthly minimum and maximum budget goals
- Dashboard showing spending progress with warnings
- Spending graph with min/max goal lines
- Gamification: earn badges for completing monthly budget goals
- Budget Champion badge with confetti celebration
- Visual progress tracking against budget goals

## Custom Features

### Feature 1: Badge Celebration System
When the user taps "Complete Budget for the Month", a full-screen 
celebration appears with animated confetti and a Budget Champion badge. 
The badge is saved to the database so the user can view all earned 
badges in the Badges screen. This was built to make budgeting feel 
rewarding and motivating, similar to Samsung Health's step goal celebration.

### Feature 2: Spending Graph with Goal Lines
A bar chart showing total spending for the current month, with 
horizontal lines marking the minimum and maximum budget goals. 
This gives the user an instant visual of whether they are within 
their target spending range.

## Demo Video
[Watch the full app demonstration here]https://youtu.be/VxECFd8pKQ8

## GitHub Actions
Automated build testing runs on every push via GitHub Actions.

## How to Run
1. Clone this repository
2. Open in Android Studio
3. Build and run on a physical Android device

## Technologies Used
- Kotlin
- Android Room Database
- MPAndroidChart (graphs)
- Konfetti (confetti animation)
- WorkManager (background budget checks)
- GitHub Actions (CI/CD)

## Team
- Member 1 (Katleho): Database foundation, Login/Register, 
  Badges, Gamification, Graph
- Member 2: Expense capture, Categories
- Member 3: Budget goals, Visual progress display<img width="720" height="1600" alt="WhatsApp Image 2026-06-15 at 13 16 40" src="https://github.com/user-attachments/assets/36472e74-eda7-4dc1-a63d-fbfbd8878124" />

