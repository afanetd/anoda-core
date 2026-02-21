# Anoda Proxima Core | High-Performance Game Backend

![Java](https://img.shields.io/badge/Java-21-orange) ![Quarkus](https://img.shields.io/badge/Quarkus-Supersonic-blue) ![License](https://img.shields.io/badge/License-GPLV3-green)

**Anoda Core** is a high-performance microservice backend for **GTA V (FiveM)** game server.
The project is built on **Event-Driven architecture**, providing instant processing of inventory data, economy, and player statistics.

## 🏗 Architecture

The project is divided into two independent layers:

1. **Anoda Core (this repository):** The "brain" of the system. Handles business logic, database operations, and heavy computations.
2. **Anoda Bridge:** The "hands" of the system. A TypeScript adapter inside the game engine that translates commands to Core.

Communication is done through **Redis Pub/Sub**, which allows physically separating the game server and backend on different machines (e.g., ARM for backend and x86 for game).

## 🛠 Technology Stack

* **Language:** Java 21
* **Framework:** Quarkus (Supersonic Subatomic Java)
* **Database:** PostgreSQL (Hibernate ORM / Panache)
* **Cache & Bus:** Redis (Pub/Sub)
* **Build:** Gradle

## 🚀 How to Run (Dev Mode)

For development with **Live Coding** support (changes applied without restart):
```bash
./gradlew quarkusDev
```

**Note:** Quarkus will start Dev UI at: http://localhost:8080/q/dev/

## 📦 Build (Production)

Creating an optimized JAR file:
```bash
./gradlew build
```

## 📄 License

This project is distributed under the GNU General Public License v3.0.
