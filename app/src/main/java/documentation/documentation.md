# Harmonie Budget Tracker
# Technical Documentation

## Table of Contents
- [1. Project Overview](#1-project-overview)
- [2. Application Features](#2-application-features)
- [3. System Architecture](#3-system-architecture)
- [4. Data Persistence](#4-data-persistence)
- [5. User Interface Flow](#5-user-interface-flow)
- [6. Building and Running](#6-building-and-running)
- [7. Folder Structure](#7-folder-structure)
- [8. Key Components](#8-key-components)
- [9. Testing and Quality](#9-testing-and-quality)
- [10. Next Steps](#10-next-steps)

## 1. Project Overview
Harmonie Budget Tracker is an Android mobile
application developed in Kotlin for the
OPSC6311 Introduction to Open-Source Coding
module. The app helps users track expenses,
manage budgets, and monitor spending habits
using local JSON file storage. This document
describes the current prototype delivered for
Part 2 of the Portfolio of Evidence.

## 2. Application Features
The prototype supports user registration and
login, category management, expense entry
with optional photo attachment, monthly
budget goal setting, expense listing by
period, category total calculations, and a
visual pie chart with legend. All data is
isolated per user. Gamification and advanced
graphs are planned for the final submission.

## 3. System Architecture
The application follows the MVVM pattern.
Activities and Fragments handle the user
interface while ViewModels manage business
logic. Data operations are delegated to a
JsonHelper utility class. This separation
improves maintainability and testability.
Username is passed through all navigation to
ensure data isolation.

## 4. Data Persistence
All user data is stored locally in JSON
files inside the apps private budget_data
folder. Separate files are created for each
user (username.json, username_categories.json,
username_expenses.json, username_goals.json).
No cloud services or external databases are
used in this prototype.

## 5. User Interface Flow
Users start at MainActivity and navigate to
LoginActivity or RegisterActivity. Successful
authentication opens DashboardActivity with
bottom navigation. Fragments provide Home,
Budget, Transactions, Budgets, and More
screens. Each screen passes the username to
maintain user-specific context.

## 6. Building and Running
Open the project in Android Studio. Sync
Gradle, then build and run on an emulator or
physical device with API 24 or higher. The
launcher activity is MainActivity. No
additional configuration is required beyond
standard Android setup.

## 7. Folder Structure
Source code is located under
app/kotlin+java/com.example.harmonie_budget_app_kt.
Documentation files are in the documentation
subfolder. Layouts, drawables, and resources
are in app/res. Gradle scripts are at the
root level. Full structure matches the
updated folder layout provided.

## 8. Key Components
Models define data classes for User, Expense,
Category, and Goal. ViewModels encapsulate
logic for each feature. JsonHelper handles
all file read and write operations. Custom
views such as PieChartView provide visual
elements. Activities and Fragments form the
user interface layer.

## 9. Testing and Quality
The project includes basic unit and
instrumented tests from the Android template.
Code follows Allman style with detailed
comments. UI matches the provided design
images. All reported warnings and errors have
been resolved in the current version.

## 10. Next Steps
The current prototype fulfills Part 2
requirements. Future work for the final POE
includes implementing the Repository pattern,
adding Coroutines for background I/O,
integrating gamification, and enhancing
graphs and dashboard visuals. All changes
will be documented in this file.

---

**End of Document**

---