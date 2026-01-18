# Sicherheits-Check: Verifizierung der Gegenmaßnahmen

Dieses Dokument beschreibt, wie die Sicherheitslücken der "unsicheren" Version in diesem Projekt geschlossen wurden. Wir dokumentieren hier die Verifizierung der **sicheren Implementierung** anhand von Screenshots und Logs.

---

## Inhaltsverzeichnis
1. [Technische Voraussetzungen](#1-technische-voraussetzungen)
2. [Umgebung einrichten](#2-umgebung-einrichten)
3. [Verifizierung 1: Login Bypass (Fehlgeschlagen)](#3-verifizierung-1-login-bypass-fehlgeschlagen)
4. [Verifizierung 2: Comment-Based Bypass (Fehlgeschlagen)](#4-verifizierung-2-comment-based-bypass-fehlgeschlagen)
5. [Verifizierung 3: UNION Injection (Fehlgeschlagen)](#5-verifizierung-3-union-injection-fehlgeschlagen)
6. [Verifizierung 4: Update Injection (Fehlgeschlagen)](#6-verifizierung-4-update-injection-fehlgeschlagen)
7. [Verifizierung 5: Error-Based Injection (Fehlgeschlagen)](#7-verifizierung-5-error-based-injection-fehlgeschlagen)
8. [Weitere Sicherheits-Checks](#8-weitere-sicherheits-checks)
9. [Ergebnis & Fazit](#9-ergebnis--fazit)

---

## 1. Technische Voraussetzungen

### 1.1 Systemanforderungen

| Komponente | Mindestanforderung |
|------------|-------------------|
| **Betriebssystem** | Windows 10/11, macOS 10.15+, oder Linux (Ubuntu 20.04+) |
| **RAM** | 8 GB (16 GB empfohlen) |
| **Festplatte** | 10 GB freier Speicherplatz |
| **CPU** | 64-bit Prozessor mit Virtualisierungsunterstützung |

### 1.2 Benötigte Software

#### Docker Desktop
Docker wird benötigt, um die MySQL-Datenbank in einem Container zu betreiben.

**Download:** https://www.docker.com/products/docker-desktop/

**Installation:**
1. Installer herunterladen und ausführen
2. Bei Windows: WSL 2 Backend aktivieren (wird während der Installation angeboten)
3. Nach der Installation: Docker Desktop starten
4. Warten bis Docker "Running" anzeigt (grünes Symbol unten links)

**Überprüfen:**
```powershell
docker --version
# Erwartete Ausgabe: Docker version 24.x.x oder höher
```

#### Java Development Kit (JDK 17+)
Das Backend ist eine Spring Boot Anwendung und benötigt Java.

**Download:** https://adoptium.net/de/temurin/releases/ (Temurin JDK 17 oder höher empfohlen)

**Installation:**
1. `.msi` (Windows) oder `.pkg` (macOS) Installer herunterladen
2. Installer ausführen und den Anweisungen folgen
3. Bei der Installation "Set JAVA_HOME variable" aktivieren

**Überprüfen:**
```powershell
java --version
# Erwartete Ausgabe: openjdk 17.x.x oder höher
```

#### Apache Maven
Maven wird benötigt, um das Java-Backend zu bauen und zu starten.

**Download:** https://maven.apache.org/download.cgi

**Installation (Windows):**
1. `apache-maven-x.x.x-bin.zip` herunterladen
2. Entpacken nach `C:\Program Files\Apache\maven`
3. Umgebungsvariablen setzen:
   - `MAVEN_HOME` = `C:\Program Files\Apache\maven`
   - `Path` um `%MAVEN_HOME%\bin` erweitern

**Installation (macOS/Linux):**
```bash
# macOS mit Homebrew
brew install maven

# Linux (Ubuntu/Debian)
sudo apt install maven
```

**Überprüfen:**
```powershell
mvn --version
# Erwartete Ausgabe: Apache Maven 3.9.x oder höher
```

#### Node.js (v18+)
Node.js wird für das Vue.js Frontend benötigt.

**Download:** https://nodejs.org/ (LTS Version empfohlen)

**Installation:**
1. Installer herunterladen und ausführen
2. Standardeinstellungen beibehalten
3. "Automatically install necessary tools" kann aktiviert werden

**Überprüfen:**
```powershell
node --version
# Erwartete Ausgabe: v18.x.x oder höher

npm --version
# Erwartete Ausgabe: 9.x.x oder höher
```

#### Postman
Postman wird verwendet, um die Requests und Angriffe durchzuführen.

**Download:** https://www.postman.com/downloads/

**Installation:**
1. Installer herunterladen und ausführen
2. Postman starten
3. Optional: Account erstellen (nicht zwingend erforderlich)


### 1.3 Schnellcheck: Alle Tools installiert?

Führe folgende Befehle aus, um zu überprüfen, ob alle Tools korrekt installiert sind:

```powershell
# Alle Versionen auf einmal prüfen
docker --version
java --version
mvn --version
node --version
npm --version
```

✅ Wenn alle Befehle eine Versionsnummer ausgeben, kannst du fortfahren!

---

## 2. Umgebung einrichten

### 2.1 Repository klonen
(falls noch nicht geschehen)
```powershell
git clone <repository-url>
cd PA02_SQL_injection
```

### 2.2 Datenbank starten
```powershell
docker compose up -d
```

### 2.3 Backend starten
```powershell
cd backend
mvn spring-boot:run
```

### 2.4 Frontend starten
(in neuem Terminal)
```powershell
cd frontend
npm install
npm run dev
```

### 2.5 MySQL CLI aufrufen
(in neuem Terminal)

Um direkt auf die Datenbank zuzugreifen, kannst du die MySQL CLI im Docker Container verwenden:

```powershell
# Container-ID herausfinden
docker ps

# MySQL CLI starten (Container-ID oder Container-Name verwenden)
docker exec -it mysql-db mysql -u demo -p demodb
# Passwort eingeben: password
```

> 💡 **Tipp:** Der Container-Name ist standardmäßig `mysql-db`.

Nützliche SQL-Befehle in der CLI:
```sql
-- Datenbank wechseln
USE demodb;

-- Alle Tabellen anzeigen
SHOW TABLES;

-- Alle Benutzer anzeigen
SELECT * FROM users;
```

### 2.6 Anwendung öffnen
- Frontend: http://localhost:5173
- Backend API: http://localhost:8080

### 2.7 Postman einrichten

**Schritt 1:** Postman öffnen

**Schritt 2:** Neue Collection erstellen (z.B. "SQL Secure Verification")

### 2.8 Testbenutzer anlegen (falls nicht vorhanden)

In Postman einen neuen Request erstellen:
- Method: `POST`
- URL: `http://localhost:8080/api/auth/register`
- Headers: `Content-Type: application/json`
- Body (raw JSON):
```json
{
  "username": "testuser",
  "password": "testpass",
  "name": "Test User",
  "email": "test@example.com"
}
```

**Schritt 3:** Login-Request erstellen (für spätere Tests):
1. In der Collection → "Add Request"
2. Name: `Login`
3. Method: `POST`
4. URL: `http://localhost:8080/api/auth/login`
5. Headers Tab: `Content-Type` = `application/json`
6. Body Tab → "raw" → "JSON" auswählen

---

## 3. Verifizierung 1: Login Bypass (Fehlgeschlagen)

### Der Angriff
Versuch, die Authentifizierung mit folgendem Payload zu umgehen:
- **Username:** `' OR '1'='1`
- **Password:** `' OR '1'='1`

### Testergebnis (Sichere Version)
Der Login wird erfolgreich blockiert. Die Anwendung verhält sich korrekt und lässt den Angreifer nicht passieren.

> 🖼️ **Beweis (Frontend):** Fehlermeldung statt erfolgreichem Login:
> ![Login Bypass Fehlgeschlagen](Data/1.png)
>
> 🖼️ **Beweis (Browser-Netzwerk):** Antwortstatus 400 (Bad Request) statt 200 (OK):
> ![Login Bypass Browser Log](Data/1_browserlog.png)
>
> 🖼️ **Beweis (Backend-Log):** Keine erfolgreiche Query-Injection sichtbar:
> ![Login Bypass Backend Log](Data/1_log.png)


### Warum es fehlschlägt
Das Backend verwendet `UserRepository.findByUsername(username)` mit **Prepared Statements**. Die Datenbank sucht nach einem Benutzer, der buchstäblich `' OR '1'='1` heißt.

---

## 4. Verifizierung 2: Comment-Based Bypass (Fehlgeschlagen)

### Der Angriff
Versuch, den Passwort-Check mittels Kommentar-Syntax auszublenden:
- **Username:** `testuser'-- `
- **Password:** `beliebig`

### Testergebnis (Sichere Version)
Der Login schlägt fehl, da der gesamte String als Username interpretiert wird.

> 🖼️ **Beweis (Frontend):** Login verweigert:
> ![Comment Bypass Fehlgeschlagen](Data/2.png)
>
> 🖼️ **Beweis (Browser-Netzwerk):** Fehlerhafte Anfrage bestätigt:
> ![Comment Bypass Browser Log](Data/2_browserlog.png)
>
> 🖼️ **Beweis (Backend-Log):** Sichere Verarbeitung der Eingabe:
> ![Comment Bypass Backend Log](Data/2_log.png)

### Warum es fehlschlägt
Das Passwort wird zusätzlich in Java mit `passwordEncoder.matches()` geprüft, was SQL-Kommentare wirkungslos macht.

---

## 5. Verifizierung 3: UNION Injection (Fehlgeschlagen)

### Der Angriff
Versuch, mittels UNION-Operator Daten aus anderen Tabellen zu extrahieren.
- **Payload:** `' UNION SELECT null-- `

### Testergebnis (Sichere Version)
Die Anwendung führt die Injection nicht aus. Es werden keine sensiblen Daten preisgegeben.

> 🖼️ **Beweis (Frontend):** Applikation bleibt stabil, kein unerwarteter Datenabfluss:
> ![UNION Injection Fehlgeschlagen](Data/3.png)
>
> 🖼️ **Beweis (Browser-Log):** Keine Daten im Response:
> ![UNION Injection Browser Log](Data/3_browserlog.png)
>
> 🖼️ **Beweis (Postman):** API verweigert die manipulierten Daten:
> ![UNION Injection Postman](Data/3_postman.png)
>
> 🖼️ **Beweis (Backend-Log):** Log zeigt saubere Parameter-Bindung:
> ![UNION Injection Backend Log](Data/3_log.png)

### Warum es fehlschlägt
Spring Data JPA verhindert strukturelle Änderungen an der Query.

---

## 6. Verifizierung 4: Update Injection (Fehlgeschlagen)

### Der Angriff
Versuch, bei einem User-Update das Passwort über das Email-Feld zu überschreiben:
- **Payload:** `test@example.com', password='HACKED`

### Testergebnis (Sichere Version)
Die Manipulation scheitert. Das Feld wird als reiner Text gespeichert, nicht als SQL-Befehl interpretiert.

> 🖼️ **Beweis (Frontend):** Darstellung der "kaputten" Email-Adresse (der Angriff wurde als Text gespeichert):
> ![Update Injection Frontend Beweis](Data/4.png)
>
> 🖼️ **Beweis (Browser-Log):** Request wird verarbeitet, aber semantisch sicher:
> ![Update Injection Browser Log](Data/4_browserlog.png)
>
> 🖼️ **Beweis (Postman):** API nimmt die Daten als String entgegen:
> ![Update Injection Postman](Data/4_postman.png)

### Warum es fehlschlägt
Hibernate nutzt Prepared Statements (`UPDATE ... SET email = ?`). Der gesamte Payload wird in das Email-Feld geschrieben. Das Passwort-Feld bleibt unberührt.

---

## 7. Verifizierung 5: Error-Based Injection (Fehlgeschlagen)

### Der Angriff
Versuch, Datenbank-interna durch Syntaxfehler zu leaken.

### Testergebnis (Sichere Version)
Keine SQL-Fehlermeldungen im Frontend oder in der API-Antwort.

> 🖼️ **Beweis (Postman):** Generische Fehlermeldung statt Stacktrace:
> ![Error Injection Postman](Data/5_postman.png)
>
> 🖼️ **Beweis (Backend-Log):** Logs fangen die Exception ab, geben sie aber nicht nach außen:
> ![Error Injection Log](Data/5_log.png)

### Warum es fehlschlägt
Spring Boot Production-Settings unterdrücken Stacktraces.

---

## 8. Weitere Sicherheits-Checks

Überprüfung weiterer Varianten und Randfälle.

> 🖼️ **Beweis (Postman):** Auch komplexe Payloads scheitern:
> ![Check Variante Postman](Data/6_postman.png)
>
> 🖼️ **Beweis (Backend-Log):** Saubere Logs:
> ![Check Variante Log](Data/6_log.png)

---

## 9. Ergebnis & Fazit

Die Sicherheitsmaßnahmen wurden erfolgreich verifiziert. Alle Angriffe, die in der unsicheren Version möglich waren, wurden wirksam unterbunden.

> 🖼️ **Gesamtergebnis:** Der finale Zustand zeigt keine Kompromittierung:
> ![Testergebnis](Data/Ergebnis.png)

1. **Login:** Sicher durch Prepared Statements.
2. **Daten:** Sicher, keine Leakage via UNION.
3. **Profil:** Sicher, keine Parameter Manipulation möglich.
4. **Fehler:** Sicher, Information Hiding aktiv.
