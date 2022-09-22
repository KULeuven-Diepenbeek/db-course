---
title: 1. DDL
---

**Data Defintion Language** is de taal die we gebruiken om de structuur van onze database te veranderen. We kunnen hiermee tabellen aanmaken, wijzigen of verwijderen. Maar ook indexen, views, triggers of stored procedures worden hiermee aangemaakt.

Zowat elke RDBMS heeft tooling om DDL te doen via een handige interface, in plaats van dit zelf uit te schrijven. In de praktijk ga je waarschijnlijk met beiden in contact komen. We gaan [DB Browser for SQLite](https://sqlitebrowser.org/dl/) gebruiken tijdens onze lessen. 

## Oefening

Herinner je nog ons bibliotheek datamodel?

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
