# Refluenceds — Influencer Marketing & Campaign Platform (Android)

**Refluenceds** is a modern, high-performance native Android application built with **Jetpack Compose**, **Clean Architecture**, **MVVM**, **Hilt Dependency Injection**, and **Kotlin Coroutines / StateFlow**.

---

## 📱 Application Overview & Key Features

### 🔐 1. Authentication & Onboarding
- **Login & Signup**: Secure user registration and authentication flow.
- **Forgot Password**: Password reset request with success dialog feedback.
- **Email & Social Verification**: Verification screens for OTP validation and linking Instagram / TikTok accounts.
- **Multi-Step Onboarding**: Step-by-step user onboarding covering First Name, Last Name, Gender, Country, Category Interests, Profile Photo Upload, and Terms acceptance.

### 🏠 2. Home Dashboard & Recommendations
- **Top Option Circles**: Quick navigation pills for Tutorials and Refluenced features.
- **Refer a Friend Banner**: Dedicated **"Refer a Friend and Earn! 20 EUR"** card launching referral bottom sheet.
- **Recommended Campaigns**: Dynamic campaign horizontal carousel.
- **Referenced Academy**: Educational video tutorial preview cards.
- **Explore Brands**: Featured brand list.

### 📲 3. Social Tab (Second Screen)
- **Top Category Navigation**: Pill filter row (`All`, `Collections 🔖`, `Fashion`, `Beauty`, `Gastronomy`, `Food & Drink`).
- **Interactive Feed Cards**: Rich social media post grid supporting index-specific badges:
  - Top text overlays (*"offizielle Geldleistung in..."*).
  - Purple earning circle badges (*"Bis zu CHF 2'016 pro Monat"*).
  - Brand badges (Lidl logo, Green shield avatar).
  - Bottom text overlays (*"PIXEL"*).
- **Options & Report Bottom Sheet**: Options dialog allowing users to view creator profiles, view brand profiles, and open the interactive **Report Content Bottom Sheet** with selectable chips (`Offensive material`, `False marketing`, `Other`).

### 🎯 4. Campaign Exploration & Details
- **Explore & Filter**: Category selector pills, grid/list layout toggle, and filter modal bottom sheet.
- **Campaign Detail Screen**: Hero banner image, campaign title, reward payout, detailed description, favorite heart toggle, brand follow button, share intent chooser, and report campaign bottom sheet.

### 💰 5. Earnings & Referral Management
- **Earnings Dashboard**: Total revenue overview, pending balance, and payout withdrawal dialog.
- **Your Referrals Screen**: Referral code generator, share link trigger, and referral earnings statistics.

### ⚙️ 6. Settings & Profile Customization
- **Edit Profile**: Update user name, email, bio, social connections, and avatar.
- **Multi-Language Support**: Instant language switching between **English (`en`)** and **Deutsch / German (`de`)**.
- **Dynamic Theme Modes**: Seamless switching between **Light Mode**, **Dark Mode**, and **System Default**.
- **Log Out & Account Deletion**: Modal confirmation dialogs with safe session cleanup.

### 🌐 7. Real-Time Network & Offline Handling
- **Network Monitor**: Reactive network callback observer tracking internet connectivity.
- **Offline Banner**: Animated alert banner notifying users when operating in offline/cached mode.

---

## 🏗️ Architecture & Tech Stack

```text
com.example.refluenceds
│
├── data
│   ├── remote
│   │   ├── api          ──> Retrofit ApiService definitions
│   │   ├── dto          ──> Request/Response DTOs & Mappers
│   │   └── datasource   ──> Safe API Execution & Fallback Data
│   │
│   ├── repository       ──> Concrete AppRepositoryImpl
│   └── local            ──> SessionManager, Room Database, DAOs
│
├── domain
│   ├── model            ──> Decoupled Business Models (Product, Campaign, User, etc.)
│   ├── repository       ──> AppRepository Interface
│   └── usecase          ──> Single Responsibility UseCases
│
├── presentation
│   ├── state            ──> Sealed UI States (Loading, Success, Error, Empty)
│   └── viewmodel        ──> @HiltViewModel + StateFlow + Coroutines
│
├── di                   ──> Hilt Modules (NetworkModule, RepositoryModule, AppModule)
└── utils                ──> Constants, ErrorHandler, NetworkResult, NetworkMonitor
```

### Core Technologies
- **UI Framework**: Jetpack Compose (100% Kotlin)
- **Architecture**: Clean Architecture + MVVM
- **Dependency Injection**: Hilt (`@HiltAndroidApp`, `@HiltViewModel`, `@Inject`)
- **Asynchronous & State**: Kotlin Coroutines, `StateFlow`, `SharedFlow`
- **Networking**: Retrofit 2, OkHttp 4 (with Logging Interceptor), Moshi
- **Image Loading**: Coil (`AsyncImage`)
- **Navigation**: Navigation3 (`NavDisplay`, `NavBackStack`)

---

## 🔌 API Architecture & Endpoints

**Base URL**: `https://api.refluenceds.com/v1/`

| Feature / Screen | HTTP Method | Endpoint | DTO | UseCase |
| :--- | :---: | :--- | :--- | :--- |
| **Login** | `POST` | `auth/login` | `LoginRequestDto` → `AuthResponseDto` | `LoginUseCase` |
| **Signup** | `POST` | `auth/signup` | `SignupRequestDto` → `AuthResponseDto` | `SignupUseCase` |
| **Forgot Password** | `POST` | `auth/forgot-password` | `ForgotPasswordRequestDto` | `ForgotPasswordUseCase` |
| **Email Verify** | `POST` | `auth/verify-email` | `VerifyEmailRequestDto` | `VerifyEmailUseCase` |
| **Social Verify** | `POST` | `auth/verify-social` | `SocialVerificationRequestDto` | `VerifySocialUseCase` |
| **Onboarding** | `POST` | `user/onboarding` | `OnboardingRequestDto` | `SubmitOnboardingUseCase` |
| **Log Out** | `POST` | `auth/logout` | None | `LogoutUseCase` |
| **Delete Account** | `DELETE` | `user/delete-account` | None | `DeleteAccountUseCase` |
| **Get Profile** | `GET` | `user/profile` | `ProfileResponseDto` | `GetProfileUseCase` |
| **Edit Profile** | `PUT` | `user/edit-profile` | `ProfileResponseDto` | `EditProfileUseCase` |
| **Home Data** | `GET` | `home` | `HomeResponseDto` | `GetHomeDataUseCase` |
| **Campaigns** | `GET` | `campaigns` | `List<CampaignDto>` | `GetCampaignsUseCase` |
| **Campaign Detail** | `GET` | `campaigns/{id}` | `CampaignDto` | `GetCampaignDetailUseCase` |
| **Favorite Campaign** | `POST` | `campaigns/{id}/favorite` | `FavoriteRequestDto` | `ToggleFavoriteUseCase` |
| **Report Campaign** | `POST` | `campaigns/{id}/report` | `ReportRequestDto` | `ReportCampaignUseCase` |
| **Follow Brand** | `POST` | `brands/{id}/follow` | `FollowRequestDto` | `FollowBrandUseCase` |
| **Referrals** | `GET` | `referrals` | `ReferralResponseDto` | `GetReferralsUseCase` |
| **Earnings** | `GET` | `earnings` | `EarningsResponseDto` | `GetEarningsUseCase` |
| **Payout Withdraw** | `POST` | `earnings/withdraw` | `WithdrawRequestDto` | `WithdrawPayoutUseCase` |
| **Notifications** | `GET` | `user/notifications` | `NotificationListResponseDto` | `GetNotificationsUseCase` |

---

## 🛠️ Building & Running the App

### Prerequisites
- **Android Studio**: Ladybug / Jellyfish or higher
- **JDK**: Java 17+
- **Gradle**: 8.x
- **Min SDK**: API 24 (Android 7.0)
- **Target SDK**: API 35 (Android 15)

### Build & Run Commands

```bash
# 1. Clone the project
git clone https://github.com/your-org/refluenceds-android.git
cd refluenceds-android

# 2. Build the Debug APK
./gradlew assembleDebug

# 3. Install on connected device/emulator
./gradlew installDebug

# 4. Launch the application
adb shell am start -n com.example.refluenceds/.MainActivity
```

---

## 📜 License
Copyright © 2026 Refluenceds. All rights reserved.
