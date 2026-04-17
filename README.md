# Harmonie Budget Tracker

## Table of Contents
- [1. Project Overview](#1-project-overview)
- [2. Team Members and Roles](#2-team-members-and-roles)
- [3. Folder Structure](#3-folder-structure)
- [4. Documentation Summary](#4-documentation-summary)
- [5. How to Clone in Android Studio](#5-how-to-clone-in-android-studio)

## 1. Project Overview
Harmonie Budget Tracker is an Android mobile application developed in Kotlin. 
It helps users track expenses, manage categories, set monthly budget goals, 
and view spending summaries using local JSON file storage.

## 2. Team Members and Roles
- Project Manager / Researcher and Designer: [Murendeni](https://github.com/MurendeniMakhavhu)
- Software Developer: [Naoyuki](https://github.com/HChristopherNaoyuki)
- Quality Assurance Tester, Documentation and Video Lead: [Makaya](https://github.com/Khayaguzu)

## 3. Folder Structure
The project follows this exact folder structure:

```
harmonie-budget-app-kt/
├── app/
│ ├── manifests/
│ │ └── AndroidManifest.xml
│ ├── kotlin+java/
│ │ ├── documentation/
│ │ │ ├── contributing.md
│ │ │ ├── disclaimer.md
│ │ │ ├── documentation.md
│ │ │ └── project_plan.md
│ │ │
│ │ ├── com.example.harmonie_budget_app_kt/
│ │ │ ├── BudgetFragment.kt
│ │ │ ├── BudgetsFragment.kt
│ │ │ ├── CategoryActivity.kt
│ │ │ ├── CategoryTotalActivity.kt
│ │ │ ├── DashboardActivity.kt
│ │ │ ├── ExpenseActivity.kt
│ │ │ ├── ExpenseListActivity.kt
│ │ │ ├── ForgotPasswordActivity.kt
│ │ │ ├── GoalActivity.kt
│ │ │ ├── HomeFragment.kt
│ │ │ ├── LoginActivity.kt
│ │ │ ├── MainActivity.kt
│ │ │ ├── MoreFragment.kt
│ │ │ ├── RegisterActivity.kt
│ │ │ ├── TransactionsFragment.kt
│ │ │ │
│ │ │ ├── models/
│ │ │ │ ├── Category.kt
│ │ │ │ ├── Expense.kt
│ │ │ │ ├── Goal.kt
│ │ │ │ └── User.kt
│ │ │ │
│ │ │ ├── viewmodels/
│ │ │ │ ├── CategoryViewModel.kt
│ │ │ │ ├── ExpenseViewModel.kt
│ │ │ │ ├── GoalViewModel.kt
│ │ │ │ ├── HomeViewModel.kt
│ │ │ │ ├── MoreViewModel.kt
│ │ │ │ └── UserViewModel.kt
│ │ │ │
│ │ │ └── utils/
│ │ │ └── JsonHelper.kt
│ │ │
│ │ ├── com.example.harmonie_budget_app_kt (androidTest)/
│ │ │ └── ExampleInstrumentedTest.kt
│ │ │
│ │ └── com.example.harmonie_budget_app_kt (test)/
│ │ └── ExampleUnitTest.kt
│ │
│ └── res/
│ │
│ ├── menu/
│ │ └── bottom_nav_menu.xml
│ ├── color/
│ │ ├── nav_icon_color.xml
│ │ └── nav_text_color.xml
│ │
│ ├── drawable/
│ │ ├── ic_home.xml
│ │ ├── ic_launcher_background.xml
│ │ ├── ic_launcher_foreground.xml
│ │ └── rounded_button.xml
│ │
│ ├── layout/
│ │ ├── activity_category.xml
│ │ ├── activity_category_total.xml
│ │ ├── activity_dashboard.xml
│ │ ├── activity_expense.xml
│ │ ├── activity_expense_list.xml
│ │ ├── activity_forgot_password.xml
│ │ ├── activity_goal.xml
│ │ ├── activity_login.xml
│ │ ├── activity_main.xml
│ │ ├── activity_register.xml
│ │ ├── fragment_budget.xml
│ │ ├── fragment_budgets.xml
│ │ ├── fragment_home.xml
│ │ ├── fragment_more.xml
│ │ └── fragment_transactions.xml
│ │
│ ├── mipmap/
│ │ │
│ │ ├── ic_launcher/
│ │ │ ├── ic_launcher.webp (hdpi)
│ │ │ ├── ic_launcher.webp (mdpi)
│ │ │ ├── ic_launcher.webp (xhdpi)
│ │ │ ├── ic_launcher.webp (xxhdpi)
│ │ │ ├── ic_launcher.webp (xxxhdpi)
│ │ │ └── ic_launcher.xml (anydpi-v26)
│ │ │
│ │ └── ic_launcher_round/
│ │ ├── ic_launcher_round.webp (hdpi)
│ │ ├── ic_launcher_round.webp (mdpi)
│ │ ├── ic_launcher_round.webp (xhdpi)
│ │ ├── ic_launcher_round.webp (xxhdpi)
│ │ ├── ic_launcher_round.webp (xxxhdpi)
│ │ └── ic_launcher_round.xml (anydpi-v26)
│ │
│ ├── values/
│ │ ├── themes/
│ │ │ ├── themes.xml
│ │ │ └── themes.xml (night)
│ │ ├── colors.xml
│ │ └── strings.xml
│ │
│ └── xml/
│ ├── backup_rules.xml
│ └── data_extraction_rules.xml
│
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

## 4. Documentation Summary
Detailed project documentation is located in the documentation folder. 
See contributing.md for team roles, documentation.md for full technical 
details, project_plan.md for the complete schedule, and disclaimer.md 
for legal information.

## 5. How to Clone in Android Studio
To clone this project directly in Android Studio:
1. Open Android Studio.
2. Click Get from VCS on the welcome screen.
3. Paste this URL: `https://github.com/HChristopherNaoyuki/harmonie-budget-app-kt.git`
4. Click Clone.
5. Once cloned, Android Studio will open the project.
6. Click Sync Project with Gradle Files.
7. Run the app on an emulator or physical device with API 24 or higher.

---

**End of Document**

---
