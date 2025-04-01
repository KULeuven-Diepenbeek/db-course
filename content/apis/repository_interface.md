---
title: Interface
draft: true
weight: 5
---

## Wat is een interface?
Een Java interface is een referentietype dat een contract definieert voor klassen door een set **methodsignatures** (en optioneel constante waarden) vast te leggen, zonder daarbij een concrete implementatie te leveren. Klassen die deze interface **implementeren**, moeten alle gedeclareerde methoden voorzien van een concrete implementatie, waardoor consistent gedrag wordt gegarandeerd en polymorfisme mogelijk wordt. Interfaces maken het ook mogelijk om meerdere gedragingen te combineren, aangezien een klasse meerdere interfaces kan implementeren.

## Wat betekend dit voor onze StudentRepository
We hebben nu verschillende methoden gezien waarmee we zo een StudentRepository kunnen implementeren. Het is dan een goed idee om een `Interface` voor `StudentRepository` te voorzien zodat we de specifieke implementaties (JDBC, JDBI, JPA) die interface dan kunnen laten implementeren zodat je later in je main programma gewoon kan kiezen met welke methode je wil werken, want elke methode heeft wel zijn voor en nadelen.

Onze StudentRepository interface ziet er dan als volgt uit:
```java
public interface StudentRepository {
  public void addStudentToDb(Student student);

  public Student getStudentsByStudnr(int stud_nr);

  public List<Student> getAllStudents();

  public void updateStudentInDb(Student student);

  public void deleteStudentInDb(int studnr);

}
```

En onze JDBC implementatie kan er dan als volgt uitzien: (waarbij de `StudentRepositoryJDBCimpl`-klasse de interface implementeert)

```java
public class StudentRepositoryJDBCimpl implements StudentRepository {
    ...
}
```

In de main van je programma kan je dus overal waar je een StudentRepository nodig hebt een van de implementatie klassen gebruiken! Bijvoorbeeld: `StudentRepository sr = new StudentRepositoryJDBCimpl(...);`