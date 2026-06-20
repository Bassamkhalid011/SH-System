# 🎓 Student Helping System (SHS)

A JavaFX desktop application that helps Pakistani students evaluate their academic eligibility and apply to top universities based on their Matric, FSC, and entry test scores.

Built as a final project for an Object-Oriented Programming (OOP) lab course.

---

## 📋 Table of Contents

- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Project Structure](#-project-structure)
- [OOP Concepts Used](#-oop-concepts-used)
- [Getting Started](#-getting-started)
- [How to Run](#-how-to-run)
- [Universities Supported](#-universities-supported)
- [Screenshots](#-screenshots)
- [Authors](#-authors)

---

## ✨ Features

- **User Authentication** — Secure signup and login system with credential validation
- **Academic Profile** — Enter and save Matric, FSC, NTS/ECAT, and NUST NET marks
- **Aggregate Calculator** — Automatically computes university-specific aggregates:
  - General formula: Matric 10% + FSC 40% + NTS 50%
  - NUST formula: Matric 10% + FSC 15% + NET 75%
  - UET/ECAT formula: Matric 17% + FSC 50% + ECAT 33%
- **University Finder** — Browse and filter universities based on your eligibility
- **Save & Apply** — Bookmark universities and track your applications
- **Dashboard** — Overview of saved and applied universities with counters
- **Persistent Storage** — Student data and university lists saved to local files

---

## 🛠 Tech Stack

| Technology | Version |
|---|---|
| Java | 25 |
| JavaFX | 23.0.1 |
| Maven | 3.x (Maven Wrapper included) |
| Google Guava | 33.4.0-jre |
| JUnit Jupiter | 5.10.2 |

---

## 📁 Project Structure

```
SHS/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/example/shs/
│       │       ├── Main.java                  # Application entry point
│       │       ├── Student.java               # Student model (Serializable)
│       │       ├── University.java            # University model with merit types
│       │       ├── DataManager.java           # Singleton data layer (file I/O)
│       │       ├── LoginController.java       # Login screen controller
│       │       ├── SignupController.java      # Signup screen controller
│       │       ├── DashboardController.java   # Main dashboard controller
│       │       ├── HelloController.java       # Helper controller
│       │       └── Files.java                 # File path constants
│       └── resources/
│           └── com/example/shs/
│               ├── login.fxml
│               ├── Signup.fxml
│               └── dashboard.fxml
├── data/
│   ├── students.txt        # Persisted student records
│   └── universities.txt    # University list with merit criteria
├── pom.xml
└── mvnw / mvnw.cmd
```

---

## 🧱 OOP Concepts Used

| Concept | Where Applied |
|---|---|
| **Encapsulation** | `Student` and `University` classes with private fields and public getters/setters |
| **Inheritance** | JavaFX controller classes extending framework base types |
| **Polymorphism** | `calculateAggregate()` behaves differently based on `MeritType` enum |
| **Abstraction** | `DataManager` hides all file I/O behind a clean API |
| **Singleton Pattern** | `DataManager.getInstance()` ensures a single shared data instance |
| **Serialization** | `Student` and `University` implement `Serializable` for persistence |
| **Enum** | `University.MeritType` (GENERAL, NUST, UET) for merit formula selection |
| **MVC Pattern** | FXML views separated from Java controllers |

---

## 🚀 Getting Started

### Prerequisites

- **Java 21+** (project targets Java 25 — JDK 21 LTS is the minimum recommended)
- **Maven** (or use the included `mvnw` wrapper — no installation needed)

### Clone the Repository

```bash
git clone https://github.com/your-username/student-helping-system.git
cd student-helping-system/SHS
```

---

## ▶️ How to Run

### Using Maven Wrapper (recommended)

**Linux / macOS:**
```bash
./mvnw javafx:run
```

**Windows:**
```cmd
mvnw.cmd javafx:run
```

### Using Maven directly

```bash
mvn javafx:run
```

### Building a JAR

```bash
mvn package
java -jar target/SHS-1.0-SNAPSHOT.jar
```

> **Note:** A `data/` folder will be created automatically in the project root on first run to store student and university data.

---

## 🏫 Universities Supported

| University | Location | Type | Merit Formula |
|---|---|---|---|
| NUST | Islamabad | Public | NET (Matric 10% + FSC 15% + NET 75%) |
| UET Lahore | Lahore | Public | ECAT (Matric 17% + FSC 50% + ECAT 33%) |
| COMSATS University | Islamabad/Lahore/Wah | Public | General |
| FAST-NUCES | Islamabad/Lahore/Karachi | Private | General |
| University of Punjab | Lahore | Public | General |
| Quaid-i-Azam University | Islamabad | Public | General |
| IBA Karachi | Karachi | Public | General |
| University of Karachi | Karachi | Public | General |
| LUMS | Lahore | Private | General |
| University of Peshawar | Peshawar | Public | General |

---

## 👥 Authors

Developed as an OOP Lab Final Project.

---

## 📄 License

This project is for academic/educational use only.
