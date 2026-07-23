# Local Explorer AI Pro

**An Intelligent Student City Management System — built for Coimbatore, tailored for students.**

A JavaFX desktop application that helps students discover nearby places, track spending, get weather updates, and reach emergency services — all in one app, backed by a real MySQL database.

---

## Table of Contents
- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Features Implemented](#features-implemented)
- [Features Not Yet Implemented](#features-not-yet-implemented)
- [Architecture](#architecture)
- [Design Patterns & Algorithms Used](#design-patterns--algorithms-used)
- [Project Structure](#project-structure)
- [Setup Instructions](#setup-instructions)
- [Running the App](#running-the-app)
- [Troubleshooting](#troubleshooting)
- [Extending the App](#extending-the-app)

---

## Overview

Local Explorer AI Pro is a desktop application aimed at college students navigating a new city. It combines a live map, a weighted recommendation engine, a personal budget tracker, and a one-tap emergency panel into a single JavaFX app talking to a local MySQL database.

The build deliberately keeps things **simple and flat**: no login/authentication layer, and no separate Controller/Service split — each screen is one file that owns its UI, its logic, and its own direct database calls.

---

## Tech Stack

### Frontend
| Technology | Purpose |
|---|---|
| **JavaFX 21** | Desktop UI framework |
| **FXML** | Declarative screen layouts (`Dashboard.fxml`, `Map.fxml`, `Budget.fxml`, `Emergency.fxml`) |
| **CSS** | Custom dark-themed styling (`style.css`) |
| **JavaFX WebView** | Embeds a live Leaflet/OpenStreetMap map inside the desktop app |

### Backend
| Technology | Purpose |
|---|---|
| **Core Java 17** | Application logic |
| **JDBC** | Direct MySQL access (no ORM) |
| **Java Collections & Streams API** | Filtering/sorting/grouping (e.g. expenses by category, scoring places) |
| **ExecutorService** | Background thread for weather API calls, keeping the UI responsive |
| **java.net.http.HttpClient** | Calling the OpenWeather REST API |
| **Gson** | Parsing JSON responses from the weather API |

### Database
| Technology | Purpose |
|---|---|
| **MySQL** | Stores places, expenses, reviews, emergency contacts |
| `database/schema.sql` | One script to create the DB, tables, and seed sample data |

### External APIs
| API | Purpose |
|---|---|
| **OpenStreetMap** (via Leaflet.js) | Map tiles and markers |
| **OpenWeather API** | Live weather on the Dashboard (optional — works without a key too, with a placeholder) |

### Build Tooling
| Tool | Purpose |
|---|---|
| **Maven** | Dependency management and build lifecycle |
| `javafx-maven-plugin` | Enables `mvn javafx:run` |
| `maven-jar-plugin` | Packages a runnable jar with the correct main class |
| **NetBeans 17+** | IDE — opens directly as a Maven project |

---

## Features Implemented

- **Dashboard** — landing screen, async weather widget (doesn't freeze the UI while fetching), navigation tiles to other modules
- **Interactive Map** — WebView-embedded Leaflet map centered on Coimbatore, category filters (Restaurant / Hospital / Pharmacy / Library / Bus Stop), live markers pulled from MySQL
- **Recommendation Engine** — weighted scoring: 50% rating, 35% proximity (Haversine distance formula), 15% student-discount bonus; ranks and surfaces the best matches first
- **Budget Tracker** — add expenses by category, live PieChart breakdown, running total, MySQL-backed persistence
- **Emergency Module** — one-tap Hospital / Police / Fire / Blood Bank actions, "send current location" stub

## Features Not Yet Implemented

These were in the original 17-module spec but are out of scope for this build (each is realistically its own multi-day feature):

- Login / registration / OTP / email verification
- Socket-based live notifications
- Voice assistant (speech recognition)
- Offline mode / map caching
- Admin dashboard
- Route Planner (Dijkstra / A* / BFS pathfinding)
- Reward system / leaderboard
- Community review system (schema exists, no UI yet)
- Analytics dashboard (visit/spend graphs beyond the budget PieChart)
- Smart notification engine (scheduled background alerts)

See [Extending the App](#extending-the-app) for how to add these using the existing pattern.

---

## Architecture

```
        JavaFX Desktop (Launcher → App)
                    │
        FXML Views (Dashboard, Map, Budget, Emergency)
                    │
   One Screen class per view (UI + logic + DB combined)
                    │
        DatabaseConnection (Singleton, JDBC)
                    │
                 MySQL
```

- **No login / auth layer.** The app opens straight into the Dashboard. Expenses are recorded against a fixed demo user (`user_id = 1`, seeded by `schema.sql`).
- **No separate Controller/Service split.** `DashboardScreen`, `MapScreen`, `BudgetScreen`, and `EmergencyScreen` each handle their own FXML event methods, business logic, and raw JDBC calls — deliberately flat, so there's one place to look per feature.
- **Singleton DB connection.** `DatabaseConnection` is the only class that opens a MySQL connection; every screen reuses it.
- **Launcher class.** `Launcher.java` exists purely so JavaFX runs correctly off a plain classpath — see [Troubleshooting](#troubleshooting).

---

## Design Patterns & Algorithms Used

**Patterns**
- **Singleton** — `DatabaseConnection` (one shared MySQL connection)
- **MVC-lite** — FXML (View) + Screen classes (Controller+logic combined) + `models/` (data)

**Algorithms**
- **Haversine formula** — great-circle distance between the user's location and each place (`Place.distanceFromKm`)
- **Weighted multi-factor scoring** — combines rating, proximity, and discount into a single recommendation score

---

## Project Structure

```
LocalExplorerAI/
├── pom.xml
├── database/
│   └── schema.sql                 # run this in MySQL first
├── src/main/java/com/localexplorer/
│   ├── Launcher.java               # actual main() entry point
│   ├── App.java                    # JavaFX Application subclass
│   ├── app/
│   │   ├── DashboardScreen.java    # weather + navigation
│   │   ├── MapScreen.java          # map, filters, recommendation scoring
│   │   ├── BudgetScreen.java       # expense CRUD + PieChart
│   │   └── EmergencyScreen.java    # one-tap emergency actions
│   ├── models/
│   │   ├── Place.java
│   │   ├── Expense.java
│   │   └── EmergencyContact.java
│   ├── dao/
│   │   └── DatabaseConnection.java # Singleton JDBC connection
│   └── utilities/
│       └── SceneManager.java       # screen navigation helper
└── src/main/resources/com/localexplorer/
    ├── fxml/                       # Dashboard, Map, Budget, Emergency
    ├── css/style.css
    └── map/map.html                # Leaflet + OpenStreetMap
```

---

## Setup Instructions

### Prerequisites
- **JDK 17+**
- **NetBeans 17+** (built-in Maven support)
- **MySQL Server**, running locally
- Internet connection (map tiles load live from OpenStreetMap)

### 1. Set up the database
Run `database/schema.sql` in MySQL Workbench or the CLI:
```
mysql -u root -p < database\schema.sql
```
This creates the `local_explorer_ai` database, all tables, a seeded demo user, and sample places around Coimbatore.

### 2. Confirm DB credentials
Already set in `src/main/java/com/localexplorer/dao/DatabaseConnection.java`:
```java
private static final String USERNAME = "root";
private static final String PASSWORD = "Shiva@2008";
```
Update this if your local MySQL uses a different username/password.

### 3. (Optional) Add a weather API key
Get a free key at https://openweathermap.org/api, then paste it into
`src/main/java/com/localexplorer/app/DashboardScreen.java`:
```java
private static final String OPENWEATHER_API_KEY = "YOUR_OPENWEATHER_API_KEY";
```
Without a key, the Dashboard still works — it just shows a placeholder instead of live weather.

### 4. Open in NetBeans
**File → Open Project** → select the `LocalExplorerAI` folder (the one containing `pom.xml`). Wait for Maven dependencies to resolve.

---

## Running the App

**Option A — NetBeans:** Right-click the project → **Clean and Build**, then **Run** (F6).

**Option B — Terminal**, from the project root:
```
mvn clean javafx:run
```

Either way, it opens straight into the Dashboard (no login).

---

## Troubleshooting

**`Error: JavaFX runtime components are missing`**
This happens if the main class run directly is a `javafx.application.Application` subclass launched off a plain classpath. This project already works around it with `Launcher.java` — make sure `pom.xml`'s `mainClass` property and the jar manifest both point to `com.localexplorer.Launcher`, not `com.localexplorer.App`.

**Database connection errors on startup**
Almost always means step 1 (`schema.sql`) wasn't run, or the credentials in `DatabaseConnection.java` don't match your local MySQL setup.

**Map doesn't show tiles**
The map loads OpenStreetMap tiles live over the internet — check your connection.

---

## Extending the App

Add new features the same way the existing ones are built: one FXML file + one matching class in `app/` that wires its own events and talks to MySQL directly via `DatabaseConnection.getInstance().getConnection()`.

Suggested order, easiest to hardest:
1. **Reviews** — new `Review` model, a review form on the Map screen, inline SQL like the other screens (table already exists in `schema.sql`)
2. **Route Planner** — Dijkstra/A* over `Place` coordinates, triggered from a new button on the Map screen
3. **Scheduled Notifications** — `ScheduledExecutorService` polling weather/events on a timer, same pattern as `DashboardScreen`'s weather task
4. **Login** — reintroduce a `Users` table with password hashing and a Login/Register FXML pair if the project requirements change
5. **Socket server** — a `ServerSocket` accept-loop on its own thread for live cross-device notifications
