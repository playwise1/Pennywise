# 🎈 Pennywise: Smart Expense Tracker

Pennywise is a native Android application designed to automate personal finance management. It securely parses incoming bank and UPI transaction SMS alerts to automatically track and categorize your daily expenses, completely eliminating the need for manual data entry.

## ✨ Features

* **Automated SMS Parsing:** Uses Regex to intelligently extract transaction amounts and merchant details directly from bank alerts.
* **Dynamic Dashboard:** Get a quick overview of your current monthly spending at a glance.
* **Visual Statistics:** View detailed expense breakdowns with an interactive Pie Chart on the Stats Screen.
* **Time Travel:** Easily navigate to previous months to review past financial habits and compare spending.
* **Manual Entry:** Option to manually add cash transactions or expenses that don't trigger an SMS.

## 🛠️ Tech Stack & Architecture

This project was built entirely in **Kotlin** and follows modern Android development practices:

* **UI Toolkit:** Jetpack Compose for a declarative, responsive user interface.
* **Architecture:** MVVM (Model-View-ViewModel) for clean separation of concerns and maintainability.
* **Local Database:** Room Database for efficient, offline-first data storage.
* **Asynchronous Tasks:** Kotlin Coroutines & Flow for smooth background operations and database queries.
* **Core Logic:** Regular Expressions (Regex) for precise text extraction from SMS strings.

## 🚀 How It Works

1.  **SMS Permission:** Upon launch, Pennywise requests permission to read SMS messages (processed strictly locally on the device for privacy).
2.  **Regex Engine:** When a new SMS arrives from a recognized bank or UPI sender, the app's Regex engine scans the text for keywords (e.g., "debited", "spent", "INR", "Rs.") and extracts the amount.
3.  **Categorization & Storage:** The extracted data is stored securely in the local Room Database and immediately reflected on the user's dashboard.

## 📸 Screenshots

<p align="center">
  <img src="https://github.com/user-attachments/assets/ea484800-bd2e-4024-8c99-746f7f8246b1" width="220" alt="Pennywise Screenshot 1" />
  <img src="https://github.com/user-attachments/assets/52be9e93-bd5e-4a47-928c-2ecbd56726ae" width="220" alt="Pennywise Screenshot 2" />
  <img src="https://github.com/user-attachments/assets/fa739d29-2940-45c9-a809-be95e13a405e" width="220" alt="Pennywise Screenshot 3" />

</p>

## 💻 Getting Started

To run this project locally:

1. Clone this repository:
   ```bash
   git clone [https://github.com/playwise1/Pennywise.git](https://github.com/playwise1/Pennywise.git)


3. Open the project in Android Studio.

4. Build the project to sync Gradle dependencies.

5. Run the app on an Android emulator or physical device.
