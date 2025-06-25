# MoneyMatters
Part2 + Final POE



Money Matters
Money Matters is a personal finance tracking application designed to help users manage expenses, set budget goals, and analyze spending habits. The project is built using Kotlin, and features include 
User Authentication using Firebase for secure sign-up and login

Data Persistence powered by RoomDB for local storage of goals and expenses

Dark Mode Support that adapts to system theme preferences

Category-Based Visualizations for tracking spending patterns using charts

Frozen Budget Categories: Users can temporarily freeze specific categories to lock spending and stay disciplined

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

--Repository(com.example.moneymatters.repository)--
//expense data operations across Room and Firestore
ExpenseRepository.kt


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
Category Summary: activity_viewcategory_expensetotal.xml, category_item.xml, activity_goalcategory_chart.xml, activity_goalcategory_chart.xml



--Manifest (AndroidManifest.xml)--
Defines the app’s permissions, activities, and UI layouts.

--Features--
User Authentication
Expense Tracking with Custom Categories
Detailed Expense Entries (Name, Description, Amount, Date, Time, Image Upload)
Budget Goals (Min & Max Monthly Budget)
Expense Filtering by User-Selected Period
Category-Wise Expense Breakdown
Dark Mode
Freeze Category
Graphics

How to Run the App
--Clone the repository:--
git clone https://github.com/ST10361357/MoneyMatters.git
Open in Android Studio (ensure compileSdk = 35, targetSdk = 34).

Install dependencies via Gradle.

Run the project on Pixel 6 Pro Emulator (API 24, Portrait mode).

--Test data
congests17@gmail.com
Forbidden@1

Dark Mode Support
The app now automatically adjusts to the device’s system theme. When dark mode is enabled on the device, all UI elements adapt to a darker color palette and requires no manual toggling by the user.

 Freezing Budget Categories
Users can now freeze specific budget categories, locking their values to prevent spending over a specified time.

--Download App--
https://st10361357.github.io/MoneyMatters/
