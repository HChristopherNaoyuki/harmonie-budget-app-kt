# Harmonie Budget Tracker

## Table of Contents
- [1. Project Overview](#1-project-overview)
- [2. Team Members and Roles](#2-team-members-and-roles)
- [3. Project Timeline and Milestones](#3-project-timeline-and-milestones)
- [4. Folder Structure](#4-folder-structure)
- [5. System Architecture and Features](#5-system-architecture-and-features)
- [6. Contributing Guidelines](#6-contributing-guidelines)
- [7. Testing and Quality Assurance](#7-testing-and-quality-assurance)
- [8. Building and Running](#8-building-and-running)
- [9. Video Demonstration](#9-video-demonstration)

## 1. Project Overview
Harmonie Budget Tracker is an Android mobile application developed in
Kotlin for the OPSC6311 Introduction to Open-Source Coding module. The
application helps users track daily expenses, manage custom categories,
set monthly budget goals, and view spending summaries through a custom
pie chart. All data is persisted locally using JSON files to ensure
offline functionality and user-specific data isolation. The project
follows the Model-View-ViewModel architectural pattern and uses Material
Design components for a minimalistic user interface. The minimum SDK is
API 24, which corresponds to Android 7.0 Nougat. The project uses
Kotlin DSL for build configuration and targets API 36. Gson handles
JSON serialization, while RecyclerView and CardView components provide
modern list and container layouts. This repository contains the complete
source code, documentation, and demonstration materials for the Portfolio 
of Evidence.

## 2. Team Members and Roles
The team Code Blooded is structured into three distinct roles to ensure
clear accountability across every phase of the Research, Plan, Design,
Build, and Evaluate cycle.

- Project Manager, Researcher, and Designer:
  [Murendeni](https://github.com/MurendeniMakhavhu)
- Software Developer:
  [Naoyuki](https://github.com/HChristopherNaoyuki)
- Quality Assurance Tester, Documentation and Video Lead:
  [Makaya](https://github.com/Khayaguzu)

The Project Manager oversees the full project lifecycle, leads Part 1
research and design, manages deadlines, and ensures all submission
requirements are met. This role is responsible for the introduction,
conclusion, and references in the design document, as well as the final
comprehensive report.

The Software Developer builds the working Android application in
Kotlin, implements all required features, writes clean commented code,
initializes the GitHub repository, and manages version control. This
role also creates the app icon, produces final image assets, and ensures
the build system is correctly configured.

The QA Tester tests all functionality on real hardware, verifies
features against rubrics, writes automated unit tests, creates the
professional demonstration video, manages the README file, and prepares
the final submission package. This role ensures that invalid inputs do
not cause crashes and that the user experience remains consistent across
all screens.

## 3. Project Timeline and Milestones
Start of project: February 16, 2026

Part 1 submission: March 24, 2026

Part 2 submission: April 28, 2026

Final submission: May 29, 2026

The timeline ensures steady progress across all three submissions while
allowing adequate time for testing, documentation, and integration.
Each phase is designed to produce a deliverable that meets the POE
rubric requirements.

February 16 to March 24, 2026: Part 1 Research, Planning and Design.
This phase includes researching existing applications, creating an
infographic comparison, compiling feature lists, developing the planning
and design document, producing UI mockups, creating the navigation
diagram, and building the Gantt chart project plan. The output is a
complete design document with references and a clear project scope.

March 25 to April 28, 2026: Part 2 Prototype Development and Testing.
This phase covers building the fully working Android prototype,
implementing user authentication, expense management, category handling,
budget goals, local data persistence, and the visual pie chart. The
output is a functional application with source code, tests, and a
demonstration video.

April 29 to May 29, 2026: Part 3 Final App Development. This final
phase includes gamification, advanced graphs, final video production,
comprehensive testing, and submission of all required files. The output
is the complete Portfolio of Evidence with all rubric items addressed.

## 4. Folder Structure
The project follows this exact folder structure:

```
harmonie-budget-app-kt/
├── app/
│   ├── manifests/
│   │   └── AndroidManifest.xml
│   ├── kotlin+java/
│   │   ├── com.example.harmonie_budget_app_kt/
│   │   │   ├── BudgetFragment.kt
│   │   │   ├── BudgetsFragment.kt
│   │   │   ├── CategoryActivity.kt
│   │   │   ├── CategoryTotalActivity.kt
│   │   │   ├── DashboardActivity.kt
│   │   │   ├── ExpenseActivity.kt
│   │   │   ├── ExpenseListActivity.kt
│   │   │   ├── ForgotPasswordActivity.kt
│   │   │   ├── GoalActivity.kt
│   │   │   ├── HomeFragment.kt
│   │   │   ├── LoginActivity.kt
│   │   │   ├── MainActivity.kt
│   │   │   ├── MoreFragment.kt
│   │   │   ├── RegisterActivity.kt
│   │   │   ├── TransactionsFragment.kt
│   │   │   ├── models/
│   │   │   │   ├── Category.kt
│   │   │   │   ├── Expense.kt
│   │   │   │   ├── Goal.kt
│   │   │   │   └── User.kt
│   │   │   ├── viewmodels/
│   │   │   │   ├── CategoryViewModel.kt
│   │   │   │   ├── ExpenseViewModel.kt
│   │   │   │   ├── GoalViewModel.kt
│   │   │   │   ├── HomeViewModel.kt
│   │   │   │   ├── MoreViewModel.kt
│   │   │   │   └── UserViewModel.kt
│   │   │   └── utils/
│   │   │       └── JsonHelper.kt
│   │   ├── com.example.harmonie_budget_app_kt (androidTest)/
│   │   │   └── ExampleInstrumentedTest.kt
│   │   └── com.example.harmonie_budget_app_kt (test)/
│   │       └── ExampleUnitTest.kt
│   └── res/
│       ├── menu/
│       │   └── bottom_nav_menu.xml
│       ├── color/
│       │   ├── nav_icon_color.xml
│       │   └── nav_text_color.xml
│       ├── drawable/
│       │   ├── ic_home.xml
│       │   ├── ic_launcher_background.xml
│       │   ├── ic_launcher_foreground.xml
│       │   ├── rounded_button.xml
│       │   └── spinner_dropdown_background.xml
│       ├── layout/
│       │   ├── activity_category.xml
│       │   ├── activity_category_total.xml
│       │   ├── activity_dashboard.xml
│       │   ├── activity_expense.xml
│       │   ├── activity_expense_list.xml
│       │   ├── activity_forgot_password.xml
│       │   ├── activity_goal.xml
│       │   ├── activity_login.xml
│       │   ├── activity_main.xml
│       │   ├── activity_register.xml
│       │   ├── fragment_budget.xml
│       │   ├── fragment_budgets.xml
│       │   ├── fragment_home.xml
│       │   ├── fragment_more.xml
│       │   └── fragment_transactions.xml
│       ├── mipmap/
│       │   ├── ic_launcher/
│       │   │   ├── ic_launcher.webp (hdpi)
│       │   │   ├── ic_launcher.webp (mdpi)
│       │   │   ├── ic_launcher.webp (xhdpi)
│       │   │   ├── ic_launcher.webp (xxhdpi)
│       │   │   ├── ic_launcher.webp (xxxhdpi)
│       │   │   └── ic_launcher.xml (anydpi-v26)
│       │   └── ic_launcher_round/
│       │       ├── ic_launcher_round.webp (hdpi)
│       │       ├── ic_launcher_round.webp (mdpi)
│       │       ├── ic_launcher_round.webp (xhdpi)
│       │       ├── ic_launcher_round.webp (xxhdpi)
│       │       ├── ic_launcher_round.webp (xxxhdpi)
│       │       └── ic_launcher_round.xml (anydpi-v26)
│       ├── values/
│       │   ├── themes/
│       │   │   ├── themes.xml
│       │   │   └── themes.xml (night)
│       │   ├── colors.xml
│       │   └── strings.xml
│       └── xml/
│           ├── backup_rules.xml
│           └── data_extraction_rules.xml
└── Gradle Scripts/
    ├── build.gradle.kts (Project: harmonie-budget-app-kt)
    ├── build.gradle.kts (Module: app)
    ├── proguard-rules.pro
    ├── gradle.properties
    ├── gradle-wrapper.properties
    ├── libs.versions.toml
    ├── local.properties
    └── settings.gradle.kts
```

## 5. System Architecture and Features
The application follows the Model-View-ViewModel architectural pattern.
Activities and Fragments handle the user interface while ViewModels
manage business logic. Data operations are delegated to the JsonHelper
utility class, which manages all file input and output within the
application private budget_data directory. This separation improves
maintainability and testability.

The data layer consists of four core models. The User model stores
name, surname, username, password, and a unique sixteen character User
ID. The Expense model records amount, date, start time, end time,
description, category identifier, and an optional photo URI. The
Category model holds an integer identifier and a textual name. The Goal
model stores minimum and maximum monthly budget values. These models are
immutable Kotlin data classes that ensure type safety and clean
serialization.

The ViewModel layer includes CategoryViewModel, ExpenseViewModel,
GoalViewModel, HomeViewModel, MoreViewModel, and UserViewModel. Each
ViewModel exposes methods that accept an Android Context and username,
then delegate file operations to JsonHelper. This pattern keeps the UI
layer free of direct file input and output logic.

The prototype supports user registration and login with unique User ID
generation, custom category management, expense entry with optional
photo attachment via the system gallery, monthly budget goal setting
with minimum and maximum values, expense listing with period filtering,
category total calculations, and a custom Canvas pie chart with legend.
All data is isolated per user through username-specific JSON files.

The user interface employs Material Design components including
TextInputLayouts, MaterialCardViews, BottomNavigationViews, and custom
drawable resources. The design follows a minimalistic philosophy with
a restrained color palette of light grays, white surfaces, and blue
accent colors. The app icon features a minimalistic MB monogram on a
solid circular background.

## 6. Contributing Guidelines
This project is developed by the Code Blooded team as part of the
OPSC6311 Portfolio of Evidence. Contributions follow a structured
workflow across the Research, Plan, Design, Build, and Evaluate cycle.

The Project Manager leads Part 1 and overall coordination. This includes
researching existing applications, creating the infographic comparison,
compiling feature lists, developing the planning and design document,
producing UI mockups, creating the navigation diagram, building the
Gantt chart, managing deadlines, and ensuring the team follows all
POE instructions, referencing standards, and submission requirements.

The Software Developer builds the fully working Android application
for Part 2 and Part 3. This includes implementing all required
features such as user login, registration, expense categories, expense
entries with amount, date, description, category, and photo attachment,
monthly budget goals, category totals, local database persistence,
graphs, progress dashboard, and gamification elements. The developer
also adds at least two innovative features, creates the app icon and
final image assets, writes clean commented code with logging,
initializes the GitHub repository, commits regularly, and sets up
automation where required.

The Quality Assurance Tester handles testing, documentation, and video
production. This includes testing all functionality on a real mobile
phone to find bugs, crashes, and logical errors, verifying each
feature against the rubrics, checking UI and UX consistency, handling
invalid inputs without crashes, writing automated unit tests for main
functionality, creating the professional demonstration video with
voiceover showing all features running on a mobile phone, managing
the README file on GitHub, documenting the innovative features,
preparing the final APK, and ensuring all submission components are
correctly uploaded.

## 7. Testing and Quality Assurance
The project includes comprehensive unit tests and instrumented tests.
Unit tests verify password validation regular expressions, User ID
generation logic, expense identifier incrementation, goal validation
rules, and model construction. Instrumented tests run on an Android
device or emulator to verify file persistence, user creation, expense
storage, category management, goal handling, data export, and progress
reset functionality. All tests are written in Allman style with detailed
comments explaining the functionality and logic.

## 8. Building and Running
To clone this project directly in Android Studio:
1. Open Android Studio.
2. Click Get from VCS on the welcome screen.
3. Paste this URL:
   `https://github.com/HChristopherNaoyuki/harmonie-budget-app-kt.git`
4. Click Clone.
5. Once cloned, Android Studio will open the project.
6. Click Sync Project with Gradle Files.
7. Run the app on an emulator or physical device with API 24 or higher.

To build an APK for distribution, first ensure that Gradle sync
completes without errors. Then select Build from the top menu, click
Build Bundle(s) / APK(s), and choose Build APK(s). Android Studio
generates the debug APK in the app/build/outputs/apk/debug/ directory.
For a release APK, configure signing in build.gradle.kts with a valid
keystore, then select Build Bundle(s) / APK(s) and choose Build
Release APK. The release build applies code shrinking and optimization
settings defined in proguard-rules.pro.

## 9. Video Demonstration
A professional demonstration video showing all features running on a
mobile device is available on YouTube.

Title: OPSC6311 Part 2 | Harmonie Budget App Demo | Code Blooded

Link: https://youtu.be/Dnyl_DxfviM

The video is uploaded as an unlisted video and includes voiceover
demonstrating user registration, login, category management, expense
entry, budget goal setting, the pie chart visualization, data export,
reset functionality, and logout.

---

**End of Document**

---
