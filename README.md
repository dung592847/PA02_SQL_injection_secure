# PA02: SQL Injection — Sichere Implementierung

> ✅ **HINWEIS:** Dieses Projekt ist die **gesicherte Version** der Anwendung. Es zeigt, wie SQL Injection Schwachstellen durch den Einsatz moderner Frameworks und Sicherheitsmechanismen verhindert werden.

## 📄 Projektübersicht

| Attribut | Wert |
|----------|------|
| **Hochschule** | HTW Berlin |
| **Modul** | Informationssicherheit |
| **Semester** | 5 |
| **Projektaufgabe** | PA02 — SQL Injection |
| **Variante** | Sichere Implementierung (mit Gegenmaßnahmen) |

## 🎯 Projektziel

Dieses Projekt demonstriert die **Absicherung einer Webanwendung** gegen SQL Injection. Es dient als **"Best Practice" Beispiel** und zeigt:

- Einsatz von **Spring Data JPA** zur Vermeidung von SQL Injection
- Verwendung von **Prepared Statements** (automatisch durch JPA/Hibernate)
- Sicheres **Password Hashing** (BCrypt)
- Vermeidung von Information Disclosure durch generische Fehlermeldungen

## 🏗️ Architektur

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│    Frontend     │────▶│    Backend      │────▶│    Database     │
│   (Vue.js)      │     │  (Spring Boot)  │     │    (MySQL)      │
│   Port: 5174    │     │   Port: 8081    │     │   Port: 3307    │
└─────────────────┘     └─────────────────┘     └─────────────────┘
```

## 🛠️ Technologie-Stack

| Komponente | Technologie | Version |
|------------|-------------|---------|
| **Frontend** | Vue.js 3 + Vite | Node 18+ |
| **Backend** | Spring Boot | Java 17+ |
| **Datenbank** | MySQL | 8.0 |
| **Sicherheit** | Spring Security Crypto | - |
| **IRM / ORM** | Spring Data JPA / Hibernate | - |

## 🚀 Anleitung

Die vollständige Dokumentation zur Verifizierung der Sicherheitsmaßnahmen findest du hier:

### 📖 [walkthrough.md](walkthrough.md)

**Inhalt:**
- Erklärung der implementierten Sicherheitsmaßnahmen
- Verifizierung: Warum die Angriffe aus der unsicheren Version hier scheitern
- Code-Vergleich (Unsicher vs. Sicher)

## 🛡️ Implementierte Sicherheitsmaßnahmen

Im Gegensatz zur unsicheren Variante werden hier folgende Techniken eingesetzt:

| Schutzmaßnahme | Datei | Beschreibung |
|----------------|-------|--------------|
| **Spring Data JPA** | `UserRepository.java` | Nutzt Prepared Statements statt String-Konkatenation. Eingaben werden als Parameter behandelt, nicht als ausführbarer Code. |
| **Password Hashing** | `AuthController.java` | Passwörter werden gehasht (BCrypt) gespeichert und verglichen, niemals im Klartext. |
| **Objekt-Mapping** | `UserController.java` | Updates erfolgen über JPA-Objekte (`userRepository.save()`). Injection in Felder wie `email` ist nicht möglich. |

## 📁 Projektstruktur

```
PA02_SQL_injection/
├── backend/                    # Spring Boot Backend (Secure)
│   └── src/main/java/.../
│       ├── controller/         # Sichere REST Controller
│       ├── repository/         # JPA Repositories
│       └── entity/             # Datenbank-Entities
├── frontend/                   # Vue.js Frontend
├── data/                       # Materialien
├── walkthrough.md              # 📖 Verifizierungs-Anleitung
├── docker-compose.yml          # MySQL Container
└── README.md                   # Diese Datei
```

## ⚠️ Sicherheitshinweise

Auch wenn dieses Projekt sicher implementiert ist, gelten allgemeine Best Practices:
- Regelmäßige Updates der Dependencies
- Sichere Konfiguration der Produktionsumgebung
- Verwendung von HTTPS in Produktion

---

*Erstellt im Rahmen des Moduls "Informationssicherheit" an der HTW Berlin.*
