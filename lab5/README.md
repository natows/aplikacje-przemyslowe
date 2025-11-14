# System do zarządzania pracownikami z możliwością importu danych z plików CSV oraz synchronizacji z REST API.


## Technologie

- **Java 17**
- **Maven 3.8+**
- **Gson 2.10.1** - parsowanie JSON
- **JUnit 5.8.2** - testy jednostkowe
- **HttpClient** (Java 11+) - komunikacja z REST API

## Wymagania

- Java JDK 17 lub nowszy
- Maven 3.6 lub nowszy

### Kompilacja projektu
```bash
mvn clean compile
```

### Uruchomienie testów
```bash
mvn test
```

### Uruchomienie pliku Main.java - przykład działania aplikacji
```bash
mvn exec:java
```

**Lub w IDE (Visual Studio Code, IntelliJ):**
- Otwórz plik `Main.java`
- Kliknij "Run" / uruchom metodę `main`