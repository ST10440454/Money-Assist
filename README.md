Youtube Presentation: https://youtu.be/jlf9D2g74i8

MoneyAssist

A personal finance Android app built in Kotlin that helps users track expenses, manage bills, set savings missions, and stay within their budget goals — all with an offline-first approach and a gamified points system to keep them motivated.

---
Features

Authentication
- Register and log in with email and password
- Passwords are hashed with SHA-256 + a random salt — never stored in plain text
- Sessions are persisted in EncryptedSharedPreferences (Android Keystore / AES-256-GCM)

Onboarding
- First-run wizard walks new users through:
  - Setting a monthly income
  - Choosing an avatar
  - Creating their first savings mission
  - Setting up recurring bills

Dashboard (Home)
- Live net balance (total income minus total expenses)
- Assist Points balance
- Last 5 transactions at a glance
- Upcoming 3 bills
- Active missions with progress bars
- Contextual Coach Cash tip based on spending behaviour

Expense Tracking
- Log income and expense entries manually
- Attach a photo receipt from the gallery or camera
- Filter entries by a custom date range
- Edit or delete existing entries
- Per-category spending breakdown

Spending Report
- Bar chart showing amount spent per category over a user-selectable period
- Minimum and maximum goal lines overlaid on each bar so you can instantly see whether spending is on track
- Visual budget goals tracker — a horizontal thermometer bar per category showing:
  - Actual spend (colour-coded: green within / yello below min / red over max)
  - Min goal marker (dashed amber line)
  - Max goal marker (solid red line)
  - Status badge 
- All charts are custom `Canvas`-drawn — no external charting library needed

Bills
- Add recurring or one-off bills with a due date and amount
- Mark bills as paid (records the payment date)
- Running total of upcoming unpaid bills

Missions (Savings Goals)
- Create a savings mission with a target amount and deadline
- App automatically calculates the recommended monthly contribution
- Log progress against a mission; contribution recalculates as you go
- Points awarded at 50% and 100% completion milestones

Assist Points (Gamification)
 Action | Points 
 Daily manual transaction log | +10 |
 Daily streak maintenance | +5 |
 Mission reaches 50% | +50 |
 Mission completed | +100 |
 Approve an imported transaction | +5 |
 Strict-mode budget overspend | −pts |
 Hub shop purchase | −pts |

Points are stored in a ledger table so the full history is auditable.

Hub
- In-app rewards shop where users spend Assist Points on items/perks

Learn
- Financial literacy articles served in-app

Settings
- Update monthly income
- Change avatar
- Toggle between Flexible and Strict budget modes
- Strict mode applies a points penalty when you overspend a category maximum
- Log out

---

Architecture

MoneyAssist follows the Model-View-ViewModel.

```
┌─────────────────────────────────────────────────┐
│                   UI Layer                       │
│  Activities / Fragments  ←→  ViewModels          │
│  (LoginActivity, MainActivity, OnboardingActivity│
│   + 12 Fragments)         (LiveData observers)   │
└───────────────────┬─────────────────────────────┘
                    │
┌───────────────────▼─────────────────────────────┐
│              Repository Layer                    │
│            AppRepository (singleton)             │
└───────────────────┬─────────────────────────────┘
                    │
┌───────────────────▼─────────────────────────────┐
│                Data Layer                        │
│  AppDatabase (Room)  ←  9 DAOs  ←  9 Entities   │
│  EncryptedSharedPreferences (PrefsManager)       │
└─────────────────────────────────────────────────┘
                    │
┌───────────────────▼─────────────────────────────┐
│             Background Work                      │
│  BillNotificationWorker  |  SyncWorker           │
│  (WorkManager)                                   │
└─────────────────────────────────────────────────┘
```

Key design decisions
- Single repository singleton — all ViewModels obtain `AppRepository.getInstance(app)` so every LiveData stream shares the same Room database connection.
- Offline-first — all data lives in a local Room database; `SyncWorker` handles any background sync.
- No external charting library — both the spending bar chart and the budget goals tracker are drawn entirely with Android `Canvas` APIs.
- Secure storage — credentials and session tokens never touch plain SharedPreferences.

---

Project Structure

```
app/src/main/java/com/moneyassist/app/
│
├── data/
│   ├── dao/               Room DAOs (9 interfaces)
│   ├── db/                AppDatabase — Room database singleton
│   ├── entity/            Room entities (User, Category, ExpenseEntry,
│   │                      Bill, Mission, BudgetCategory, HubItem,
│   │                      Transaction, PointsLedger)
│   ├── model/             Non-entity data classes (CategorySpending,
│   │                      CategorySpendingWithGoals)
│   └── repository/        AppRepository — single source of truth
│
├── engine/
│   └── PointsManager.kt   All Assist Points logic (awards + penalties)
│
├── ui/
│   ├── adapter/           RecyclerView adapters (Bill, Mission,
│   │                      Transaction, Budget, Article)
│   ├── fragment/          All screen fragments
│   ├── view/              Custom Canvas views (SpendingBarChartView,
│   │                      BudgetGoalsTrackerView)
│   └── viewmodel/         ViewModels (one per screen)
│
├── util/
│   ├── PasswordUtils.kt   SHA-256 + salt hashing & verification
│   └── PrefsManager.kt    Encrypted SharedPreferences wrapper (singleton)
│
└── worker/
    ├── BillNotificationWorker.kt    Upcoming bill reminders
    └── SyncWorker.kt                Background data sync
```

Database Schema
 
users | Registered user accounts 
-categories | Spending categories with min/max budget goals 
-expense_entries | Individual income and expense transactions 
-bills | Recurring and one-off bills 
-missions | Savings goals with targets and deadlines 
-budget_categories | Per-period budget allocations 
-hub_items | Rewards shop items 
-transactions | Imported/synced transaction records 
-points_ledger | Full audit log of every Assist Points award and deduction 

---

Security

- Passwords are never stored in plain text. They are salted with a 16-byte cryptographically random salt and hashed with SHA-256 before being stored.
- Session data (logged-in user ID, monthly income, streak info) is stored in EncryptedSharedPreferences backed by the Android Keystore using AES-256-GCM.
- The `EncryptedSharedPreferences` instance is initialised once (singleton) to avoid performing Keystore I/O on the main thread.
