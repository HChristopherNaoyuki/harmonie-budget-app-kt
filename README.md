# Harmonie Budget Tracker

## Table of Contents

- [1. Terms and Conditions](#1-terms-and-conditions)
- [2. Project Overview](#2-project-overview)
- [3. Team Members and Roles](#3-team-members-and-roles)
- [4. Project Timeline and Milestones](#4-project-timeline-and-milestones)
- [5. Folder Structure](#5-folder-structure)
- [6. System Architecture and Features](#6-system-architecture-and-features)
- [7. Contributing Guidelines](#7-contributing-guidelines)
- [8. Testing and Quality Assurance](#8-testing-and-quality-assurance)
- [9. Building and Running](#9-building-and-running)
- [10. Video Demonstration](#10-video-demonstration)
- [11. Sources](#11-sources)

## 1. Terms and Conditions

The Harmonie Budget Tracker application is open-source software licensed under the MIT
License. You are granted permission to use, copy, modify, merge, publish, distribute,
sublicense, and/or sell copies of the software, subject to the following conditions.

The above copyright notice and this permission notice shall be included in all copies
or substantial portions of the software. The software is provided "as is", without
warranty of any kind, express or implied, including but not limited to the warranties
of merchantability, fitness for a particular purpose, and noninfringement.

In no event shall the authors or copyright holders be liable for any claim, damages,
or other liability, whether in an action of contract, tort, or otherwise, arising from,
out of, or in connection with the software or the use or other dealings in the software.

All visual media, including screenshots and images of the application, must be stored
in a dedicated folder within the project directory. This folder should be clearly
structured and named accordingly to indicate that it contains all visual content
related to the application (for example, a folder named images, screenshots, or media).

The maintainers are not liable or responsible for any malfunctions, defects, or issues
that may occur as a result of copying, modifying, or using this software. If you
encounter any problems or errors, please do not attempt to fix them silently or outside
the project. Instead, kindly submit a pull request or open an issue on the corresponding
GitHub repository, so that it can be addressed appropriately by the maintainers or
contributors.

## 2. Project Overview

Harmonie Budget Tracker is an Android mobile application developed in Kotlin for the
Introduction to Open-Source Coding module. The application helps users track daily
expenses, manage custom categories, set monthly budget goals, and view spending
summaries through a custom pie chart. All data is persisted locally using JSON files
to ensure offline functionality and user-specific data isolation. The project follows
the Model-View-ViewModel architectural pattern and uses Material Design components
for a minimalistic user interface. The minimum SDK is API 24, which corresponds to
Android 7.0 Nougat. The project uses Kotlin DSL for build configuration and targets
API 36. Gson handles JSON serialization, while RecyclerView and CardView components
provide modern list and container layouts. This repository contains the complete
source code, documentation, and demonstration materials for the Portfolio of Evidence.

The development process follows a structured Research, Plan, Design, Build, and
Evaluate cycle as prescribed by the module. Each phase builds upon the previous one,
ensuring methodical progress while allowing for iterative refinements. The app is
designed to run on physical mobile devices rather than emulators for the final
submission, as specified in the POE requirements. This approach validates that all
features function correctly under real-world conditions.

## 3. Team Members and Roles

The team Code Blooded is structured into three distinct roles to ensure clear
accountability across every phase of the Research, Plan, Design, Build, and Evaluate
cycle.

Project Manager, Researcher, and Designer: Murendeni
(https://github.com/MurendeniMakhavhu)

Software Developer: Naoyuki
(https://github.com/HChristopherNaoyuki)

Quality Assurance Tester, Documentation and Video Lead: Makaya
(https://github.com/Khayaguzu)

The Project Manager oversees the full project lifecycle, leads Part 1 research and
design, manages deadlines, and ensures all submission requirements are met. This role
is responsible for the introduction, conclusion, and references in the design document,
as well as the final comprehensive report.

The Software Developer builds the working Android application in Kotlin, implements
all required features, writes clean commented code, initializes the GitHub repository,
and manages version control. This role also creates the app icon, produces final image
assets, and ensures the build system is correctly configured. The developer follows
clean code principles as recommended by Skeen and Greenhalgh (2018) to maintain
readability and maintainability throughout the project.

The QA Tester tests all functionality on real hardware, verifies features against
rubrics, writes automated unit tests, creates the professional demonstration video,
manages the README file, and prepares the final submission package. This role ensures
that invalid inputs do not cause crashes and that the user experience remains consistent
across all screens.

## 4. Project Timeline and Milestones

Start of project: February 16, 2026

Part 1 submission: March 24, 2026

Part 2 submission: April 28, 2026

Final submission: June 15, 2026

The timeline ensures steady progress across all three submissions while allowing
adequate time for testing, documentation, and integration. Each phase is designed to
produce a deliverable that meets the POE rubric requirements.

February 16 to March 24, 2026: Part 1 Research, Planning and Design. This phase
includes researching existing applications, creating an infographic comparison,
compiling feature lists, developing the planning and design document, producing UI
mockups, creating the navigation diagram, and building the Gantt chart project plan.
The research phase examines three existing budgeting apps to identify strengths,
weaknesses, and innovative features that inform the design of Harmonie Budget.

March 25 to April 28, 2026: Part 2 Prototype Development and Testing. This phase
covers building the fully working Android prototype, implementing user authentication,
expense management, category handling, budget goals, local data persistence, and the
visual pie chart. The prototype is tested on both emulators and physical devices to
verify stability and performance.

April 29 to June 15, 2026: Part 3 Final App Development. This final phase includes
gamification, advanced graphs, final video production, comprehensive testing, and
submission of all required files. The final app must run on a mobile phone and include
badges for meeting budget goals and consistent expense logging.

## 5. Folder Structure

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
│   │   │   ├── ExpenseHistoryAdapter.kt
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
│   │   │   │   ├── Badge.kt
│   │   │   │   ├── Category.kt
│   │   │   │   ├── Expense.kt
│   │   │   │   ├── Goal.kt
│   │   │   │   ├── StreakData.kt
│   │   │   │   └── User.kt
│   │   │   ├── viewmodels/
│   │   │   │   ├── CategoryViewModel.kt
│   │   │   │   ├── ExpenseViewModel.kt
│   │   │   │   ├── GamificationViewModel.kt
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
│       │   ├── ic_home_house.xml
│       │   ├── ic_launcher_background.xml
│       │   ├── ic_launcher_foreground.xml
│       │   ├── ic_pie_chart.xml
│       │   ├── ic_receipt.xml
│       │   ├── ic_settings_gear.xml
│       │   ├── rounded_button.xml
│       │   ├── spinner_dropdown_background.xml
│       │   └── table_header_background.xml 
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
│       │   ├── fragment_transactions.xml 
│       │   └── item_expense_history.xml
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
│           ├── data_extraction_rules.xml
│           └── file_paths.xml
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

## 6. System Architecture and Features

The application follows the Model-View-ViewModel architectural pattern. Activities and
Fragments handle the user interface while ViewModels manage business logic. Data
operations are delegated to the JsonHelper utility class, which manages all file input
and output within the application private budget_data directory. This separation
improves maintainability and testability, as recommended by Späth (2018) for modern
Android development with Kotlin.

The data layer consists of four core models. The User model stores name, surname,
username, password, and a unique sixteen character User ID. The Expense model records
amount, date, start time, end time, description, category identifier, and an optional
photo URI. The Category model holds an integer identifier and a textual name. The Goal
model stores minimum and maximum monthly budget values. These models are immutable
Kotlin data classes that ensure type safety and clean serialization.

The ViewModel layer includes CategoryViewModel, ExpenseViewModel, GoalViewModel,
HomeViewModel, MoreViewModel, and UserViewModel. Each ViewModel exposes methods that
accept an Android Context and username, then delegate file operations to JsonHelper.
This pattern keeps the UI layer free of direct file input and output logic.

The prototype supports user registration and login with unique User ID generation,
custom category management, expense entry with optional photo attachment via the system
gallery, monthly budget goal setting with minimum and maximum values, expense listing
with period filtering, category total calculations, and a custom Canvas pie chart with
legend. All data is isolated per user through username-specific JSON files.

The user interface employs Material Design components including TextInputLayouts,
MaterialCardViews, BottomNavigationViews, and custom drawable resources. The design
follows a minimalistic philosophy as described by Malewicz (2021), with a restrained
color palette of light grays, white surfaces, and blue accent colors. The app icon
features a minimalistic MB monogram on a solid circular background.

## 7. Contributing Guidelines

This project is developed by the Code Blooded team as part of the Portfolio of Evidence.
Contributions follow a structured workflow across the Research, Plan, Design, Build,
and Evaluate cycle. Sharp, Preece, Rogers, and Preece (2019) emphasize that structured
interaction design processes improve user satisfaction, which guided the team approach.

The Project Manager leads Part 1 and overall coordination. This includes researching
existing applications, creating the infographic comparison, compiling feature lists,
developing the planning and design document, producing UI mockups, creating the
navigation diagram, building the Gantt chart, managing deadlines, and ensuring the team
follows all instructions, referencing standards, and submission requirements.

The Software Developer builds the fully working Android application for Part 2 and
Part 3. This includes implementing all required features such as user login,
registration, expense categories, expense entries with amount, date, description,
category, and photo attachment, monthly budget goals, category totals, local database
persistence, graphs, progress dashboard, and gamification elements. The developer also
adds at least two innovative features, creates the app icon and final image assets,
writes clean commented code with logging following the principles outlined by
Pluralsight (2022), initializes the GitHub repository, commits regularly, and sets up
automation where required.

The Quality Assurance Tester handles testing, documentation, and video production. This
includes testing all functionality on a real mobile phone to find bugs, crashes, and
logical errors, verifying each feature against the rubrics, checking UI and UX
consistency, handling invalid inputs without crashes, writing automated unit tests for
main functionality, creating the professional demonstration video with voiceover showing
all features running on a mobile phone, managing the README file on GitHub,
documenting the innovative features, preparing the final APK, and ensuring all
submission components are correctly uploaded.

### How to Clone and Fork the Repository

Cloning the repository creates a local copy on your computer. To clone using Android
Studio, open Android Studio and click Get from VCS on the welcome screen. Paste the
repository URL https://github.com/HChristopherNaoyuki/harmonie-budget-app-kt.git into
the URL field and click Clone. After cloning completes, click Sync Project with Gradle
Files to resolve dependencies.

To clone using the Git command line, open a terminal and navigate to your desired
directory. Run git clone https://github.com/HChristopherNaoyuki/harmonie-budget-app-kt.git
Then navigate into the cloned directory with cd harmonie-budget-app-kt.

Forking the repository creates a copy under your own GitHub account. To fork, log into
your GitHub account and navigate to the repository URL. Click the Fork button at the
top right corner of the page. Select your personal account as the destination for the
fork. GitHub will create the forked copy and redirect you to
https://github.com/YOUR_USERNAME/harmonie-budget-app-kt. You can now clone your forked
copy to your local machine using the instructions above, replacing the URL with your
forked repository URL.

The APK file for the application can be found in the Releases section of this
repository. Please navigate to the Releases page to download the latest APK.

### Git Commit Guidelines

All commits to this repository must follow the established commit message format below.

Header line: Explain the commit in one line using the imperative mood. For example,
"Add expense filtering by date range" rather than "Added expense filtering".

Body of commit message is a few lines of text, explaining things in more detail,
possibly giving some background about the issue being fixed, etc. The body of the
commit message can be several paragraphs. Please do proper word-wrap and keep columns
shorter than about 74 characters. That way git log will show things nicely even when
the output is indented.

Make sure you explain your solution and why you are doing what you are doing, as
opposed to describing what you are doing. Reviewers and your future self can read the
patch, but might not understand why a particular solution was implemented.

Commit message format:

```
Header line: Explain the commit in one line (use the imperative)

Body of commit message is a few lines of text, explaining things
in more detail, possibly giving some background about the issue
being fixed, etc.

The body of the commit message can be several paragraphs, and
please do proper word-wrap and keep columns shorter than about
74 characters or so. That way "git log" will show things
nicely even when it's indented.

Make sure you explain your solution and why you're doing, what you're
doing, as opposed to describing what you're doing. Reviewers and your
future self can read the patch, but might not understand why a
particular solution was implemented.

Reported-by: whoever-reported-it
Signed-off-by: Your Name
```

## 8. Testing and Quality Assurance

The project includes comprehensive unit tests and instrumented tests. Unit tests verify
password validation regular expressions, User ID generation logic, expense identifier
incrementation, goal validation rules, and model construction. Password storage follows
secure hashing principles documented by Spring (n.d.), which recommends PBKDF2 for
credential storage. Instrumented tests run on an Android device or emulator to verify
file persistence, user creation, expense storage, category management, goal handling,
data export, and progress reset functionality. All tests are written in Allman style
with detailed comments explaining the functionality and logic. Whitman and Mattord
(2022) highlight that information security principles must be embedded throughout the
development lifecycle, which informed the testing strategy for user data protection.

## 9. Building and Running

To clone this project directly in Android Studio, open Android Studio and click Get
from VCS on the welcome screen. Paste the repository URL
https://github.com/HChristopherNaoyuki/harmonie-budget-app-kt.git into the URL field
and click Clone. Once cloned, Android Studio will open the project automatically.
Click Sync Project with Gradle Files to resolve dependencies. Run the app on an
emulator or physical device with API 24 or higher.

To build an APK for distribution, ensure that Gradle sync completes without errors.
Select Build from the top menu, click Build Bundle(s) / APK(s), and choose Build APK(s).
Android Studio generates the debug APK in the app/build/outputs/apk/debug/ directory.
For a release APK, configure signing in build.gradle.kts with a valid keystore, then
select Build Bundle(s) / APK(s) and choose Build Release APK.

For the latest APK, please visit the Releases section of this repository.

## 10. Video Demonstration

Two professional demonstration videos are available showing all features running on a
physical mobile device. The Part 2 video covers the prototype functionality, while the
Part 3 video includes all final features, gamification, and UI refinements.

Part 2 Video - Prototype Demonstration:
Title: OPSC6311 Part 2 | Harmonie Budget App Demo | Code Blooded
Link: https://youtu.be/Dnyl_DxfviM

Part 3 Video - Final Submission with Gamification and ZAR Currency:
Title: Harmonie Budget Tracker | Part 3 Final Submission | Full Demo | Code Blooded
Link: https://www.youtube.com/watch?v=Ze9LMSgqsG0

Both videos are uploaded as unlisted and include voiceover demonstrating user
registration, login, category management, expense entry, budget goal setting, pie chart
visualization, data export, reset functionality, and logout. The Part 3 video adds
demonstrations of streak tracking, badges, category filtering, month/year filtering,
sortable tables, and dark mode compatibility.

## 11. Sources

Kotlin Docs. (2021, March 2). Kotlin. Retrieved May 7, 2026, from
https://kotlinlang.org/docs/home.html

Malewicz, D. (2021, May 30). A guide to the Modern Minimal UI style. UX Collective.
Retrieved May 7, 2026, from
https://uxdesign.cc/a-guide-to-the-modern-minimal-ui-style-531ac1e9fbfe

Password Storage. (n.d.). Spring. Retrieved May 7, 2026, from
https://docs.spring.io/spring-security/reference/features/authentication/
password-storage.html

Sharp, H., Preece, J., Rogers, Y., & Preece, J. (2019). Interaction Design: Beyond
Human-Computer Interaction. Wiley.

Skeen, J., & Greenhalgh, D. (2018). Kotlin Programming: The Big Nerd Ranch Guide.
Pearson Education.

Späth, P. (2018). Pro Android with Kotlin: Developing Modern Mobile Apps. Apress.

10 Tips for Writing Clean Code. (2022, October 20). Pluralsight. Retrieved May 7,
2026, from https://www.pluralsight.com/resources/blog/software-development/
10-steps-to-clean-code

Whitman, M. E., & Mattord, H. J. (2022). Principles of Information Security. Cengage.

---

End of Document

---
