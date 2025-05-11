# MoneyMatters
Part2



Money Matters
Money Matters is a personal finance tracking application designed to help users manage expenses, set budget goals, and analyze spending habits. The project is built using Kotlin, with data persistence handled by RoomDB.

-Project Overview
-GitHub Repository: Money Matters

--Compatible with:--
androidx.core-ktx 1.15.0
compileSdk = 35
targetSdk = 34

--Runs on: Pixel 6 Pro Emulator (Android 7.8 x86, Portrait Setup for Best Experience)

--Project Structure--
The project follows a structured MVC architecture, ensuring a clear separation between UI components, data handling, and business logic.

--Database Package (com.example.moneymatters.database)--
Contains Room Database setup:
AppDb.kt – The main database instance.
User.kt, Category.kt, Expense.kt, Goal.kt – Entity classes defining database tables.

--Adapter Package (com.example.moneymatters.adapter)--
Adapters bind data between AppDb and UI components:
CategoryExpenseAdapter.kt
ExpenseAdapter.kt
GoalAdapter.kt

--Data Package (com.example.moneymatters.data)--
Holds entity classes used throughout the app:
User.kt, Category.kt, Expense.kt, CategoryExpenseTotal.kt, Goal.kt

--UI Activities (com.example.moneymatters.uiActivity)--
Contains screen-specific Kotlin classes for handling user interactions:
UserActivity.kt, CategoryActivity.kt, ExpenseActivity.kt
ViewCategoryExpenseTotalActivity.kt, GoalActivity.kt
ViewExpensesActivity.kt, LoginActivity.kt, RegisterActivity.kt
MainActivity.kt

--ViewModel (com.example.moneymatters.viewmodel)--
AppViewModel.kt – Manages business logic and data handling.

--Provider Package (com.example.moneymatters.provider)--
Handles pickers and file storage to store user selections for future retrieval.

--Layout XML Files (com.example.moneymatters.layout)--
Defines UI elements for screens in res/layout/:
Authentication: activity_login.xml, activity_register.xml
Dashboard: activity_main.xml, activity_navmenu.xml
Expense Management: activity_category_expense.xml, activity_item_expense.xml, activity_view_expense.xml
Goals: activity_goal.xml, item_goal.xml, goal_item.xml
Category Summary: activity_viewcategory_expensetotal.xml, category_item.xml

--Manifest (AndroidManifest.xml)--
Defines the app’s permissions, activities, and UI layouts.

--Features--
User Authentication
Expense Tracking with Custom Categories
Detailed Expense Entries (Name, Description, Amount, Date, Time, Image Upload)
Budget Goals (Min & Max Monthly Budget)
Expense Filtering by User-Selected Period
Category-Wise Expense Breakdown

How to Run the App
--Clone the repository:--
git clone https://github.com/ST10361357/MoneyMatters.git
Open in Android Studio (ensure compileSdk = 35, targetSdk = 34).

Install dependencies via Gradle.

Run the project on Pixel 6 Pro Emulator (API 24, Portrait mode).
