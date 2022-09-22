---
title: 1. Database Basics
---

Een **database** is niet meer dan een verzameling van gegevens. 
**DBMS** (Database Management System) is de software waarmee databases beheerd of aangemaakt kunnen worden.

## 1. Waarom een database gebruiken?

Een database wordt ook maar gewoon opgeslagen op een file system. Dus waarom kan ik dan niet zelf files gebruiken om mijn data op te slaan?

Databases bieden een aantal key features:

- Performant (index management)
- Betere integratie met andere applicaties
- DBMS voor bewerken of ophalen van data
- Concurrency ondersteuning
- Security & Privacy van data
- ...

In het tweedejaarsvak [Besturingssystemen en C](https://kuleuven-diepenbeek.github.io/osc-course/) leerde je dat IO manipulatie **heel dure operaties** zijn. Een erg groot bestand openen of een `seek()` operatie uitvoeren daarop, duizenden bestanden tegelijkertijd openen voor data access, ...---allemaal voorbeelden van nadelen waar een database de oplossing kan bieden.

## 2. Database Model

De data die zich in een database bevindt wordt op een specifieke manier opgeslagen. De structuur waarop deze data bijgehouden wordt, noemen we het **database model**.

Een database model bestaat uit meerdere **data modellen**. Een data model beschrijft één specifiek object.

We zien hetzeflde eigenlijk terug als we denken aan Java of Kotlin. We definiëren hoe een klasse eruit ziet. Bijvoorbeeld volgende klasse:

<div class="devselect">

```kt
data class Book(val isbn: string, val title: string, val author: string, val price: double)
```

```java
public class Book {
    String isbn;
    String title;
    String author;
    double price;

    public Book(isbn, title, author, price) 
    {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.price = price;
    }
}
```

</div>

Dit kunnen we ook in een **database** bepalen. Daar zou het **data model** van de tabel Book er bijvoorbeeld als volgt kunnen uitzien:

{{<mermaid align="left">}}
classDiagram
    class Book{
        isbn: NVARCHAR(50)
        title: NVARCHAR (500)
        author: NVARCHAR (500)
        price: DECIMAL(10,4)
    }
{{< /mermaid >}}

Net als we in code state kunnen hebben wanneer we onze klasses instantiëren:

<div class="devselect">

```kt
var book = Book("0765326353", "The Way of Kings", "Brandon Sanderson", 24.99)
```

```java
var book = new Book("0765326353", "The Way of Kings", "Brandon Sanderson", 24.99);
```

</div>

Zo kunnen we ook state hebben in onze database:

| isbn        | title            | author            | price   |
| ----------- | ---------------- | ----------------- | -------:| 
| 0765326353  | The Way of Kings | Brandon Sanderson | 24.99 |

Een voorbeeld van een simpel **database model** voor de inventaris van een bibliotheek zou er ongeveer als volgt kunnen uitzien:

{{<mermaid align="left">}}
classDiagram
    Book "0..*" --> "1..*" Genre
    Book "1..*" --> "1" Author

    class Author{
        id: INT
        name: NVARCHAR
        firstName: NVARCHAR
    }

    class Genre{
        id: INT
        description: NVARCHAR
    }

    class Book{
        isbn: NVARCHAR
        title: NVARCHAR
        author: INT
        price: DECIMAL
        genre: INT
    }
{{< /mermaid >}}