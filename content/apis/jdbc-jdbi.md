---
title: "1. JDBC en JDBI"
---

## 1. Queries/Objecten in JDBC

Zie **[Transacties failures/rollbacks](/transacties/failures-rollbacks/)** voor de initiële setup van een eenvoudige Java JDBC applicatie. 

Stel dat we dezelfde studenten willen inladen in een `Student` klasse instantie: van de `TABLE STUDENT` naar de `class Student`. In geval van JDBC is dat veel handwerk: 

1. Maak een verbinding met de database. 
2. Voer de `SELECT` statements uit. 
3. Loop door de `ResultSet` en maak een nieuwe `Student` instantie aan. Vang alle mogelijke fouten zelf op: wat met lege kolommen, `null`? Wat met `INTEGER` kolommen die je wilt mappen op een `String` property? 

Om van de huidige resultatenrij naar de volgende te springen in `ResultSet` gebruikt men de methode `next()` in een typisch `while()` formaat:

```java
var result = statement.executeQuery("SELECT * FROM iets");
while(result.next()) {
    var eenString = result.getString("kolomnaam");
    // doe iets!
}
```

Zie ook [ResultSet Oracle Javadoc](https://docs.oracle.com/javase/7/docs/api/java/sql/ResultSet.html). Aangezien we reeds hebben kennis gemaakt met de (beperkte) API, schakelen we onmiddellijk over naar de oefeningen:

### Oefeningen

1. Maak (én test!) een klasse `StudentRepository` die de volgende methode implementeert. Zoals je ziet is het de bedoeling dat de JDBC `Connection` instance elders wordt aangemaakt, bijvoorbeeld in een **aparte** `ConnectionManager` klasse. 

```java
public class StudentRepository {
        public StudentRepository(Connection connection);
        public List<Student> getStudentsByName(String name);
}
```

2. Breid dit uit naar `public void saveNewStudent(Student student);`.
3. Breid dit uit naar `public void updateStudent(Student student);`. Wat moet je doen als deze student nog niet in de database zit? Welke gegevens update je wel en welke niet? 

## 2. Queries/Objecten in Jdbi 3

[Jdbi](https://jdbi.org) (Java DataBase Interface v3) is een lightweight library geschreven bovenop JDBC. Het gebruikt dus de interne Java API om te communiceren tussen de database en de Java applicatie. Echter, het maakt het leven voor ons als ontwikkelaar op heel wat vlakken véél _aangenamer_: waar JDBC eerder database-driven en dialect-afhankelijk is, is Jdbi eerder user-driven en met behulp van plugins dialect-onafhenkelijk. 
