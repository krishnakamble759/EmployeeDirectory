# Employee Directory App

A native Android application built in **Kotlin** that consumes the Employee Directory REST API, displays employee records in a responsive **RecyclerView**, and provides detailed profiles through Master-Detail navigation.

---

## 📱 Features

- **REST API Integration**: Asynchronously fetches employee data from `https://aamras.com/dummy/EmployeeDetails.json` using **Retrofit 2** and **Kotlin Coroutines**.
- **Modern Material 3 UI**: Clean cards, rounded corners, subtle elevation, and dynamic color-coded avatar badges.
- **Master-Detail Flow**: Smooth navigation from directory list to an employee's detailed profile passing data via Kotlin `@Parcelize`.
- **Live Search Filtering**: Search bar filters directory entries in real time by name, age, or salary.
- **Error Handling & Offline Caching**:
  - Loading indicators during network requests.
  - Connection status detection with a user-friendly error screen and Retry button.
  - Local caching fallback to maintain functionality offline.
  - Pull-to-refresh (`SwipeRefreshLayout`) for manual updates.

---

## 🛠️ Tech Stack & Architecture

- **Language**: Kotlin 1.9+
- **Minimum SDK**: Android 7.0 (API 24)
- **Target SDK**: Android 15 (API 35)
- **Architecture**: Modular structure separating Data (Model & API), UI (Activities & Adapters), and Utilities
- **Libraries**:
  - `Retrofit 2` + `Converter-Gson` for REST API consumption
  - `OkHttp3` with `HttpLoggingInterceptor`
  - `Kotlinx Coroutines` + `LifecycleScope` for background tasks
  - `ViewBinding` for safe view access
  - `Material Components 3` (MaterialCardView, MaterialToolbar, etc.)

---

## 🚀 Getting Started

1. Clone or download this repository:
   ```bash
   git clone https://github.com/krishnakamble759/EmployeeDirectory.git
   ```
2. Open the project in **Android Studio**.
3. Allow Gradle to sync dependencies.
4. Select an Android emulator or physical device.
5. Click **Run** (`Shift + F10`).

---

## 📦 Building the APK

To generate a debug APK in Android Studio:
1. Go to **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
2. The generated APK will be located in `app/build/outputs/apk/debug/app-debug.apk`.
