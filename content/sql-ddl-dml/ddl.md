---
title: DDL
weight: 1
draft: false
---

**Data Defintion Language** is de taal die we gebruiken om de structuur van onze database te veranderen. We kunnen hiermee tabellen aanmaken, wijzigen of verwijderen. Maar ook indexen, views, triggers of stored procedures worden hiermee aangemaakt.

Zowat elke RDBMS heeft tooling om DDL te doen via een handige interface, in plaats van dit zelf uit te schrijven. In de praktijk ga je waarschijnlijk met beiden in contact komen. We gaan [DB Browser for SQLite](https://sqlitebrowser.org/dl/) gebruiken tijdens onze lessen. 

{{% task %}}
Kijk naar [de Chinook database](/chinook.db) en maak een schematische voorstelling van hoe deze database eruit ziet. Je kan hiervoor [Mermaid](https://mermaid.live/) gebruiken, of een eigen tool of pen en papier.
{{% /task %}}

## Tabellen aanmaken en wijzigen

Met DDL _definiëer_ je structuur in SQL. Met DML _wijzig_ of manipuleer je de inhoud ervan. De Chinook database bevat natuurlijk reeds tabellen, maar alles begint met een `CREATE TABLE` statement. Je kan in SQLite Browser rechtsklikken op tabellen en **Copy Create Statement** kiezen om te reverse-engineeren hoe de tabellen aangemaakt werden. 

Bijvoorbeeld, voor albums:

```sql
CREATE TABLE "albums"
(
    [AlbumId] INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    [Title] NVARCHAR(160)  NOT NULL,
    [ArtistId] INTEGER  NOT NULL,
    FOREIGN KEY ([ArtistId]) REFERENCES "artists" ([ArtistId]) 
        ON DELETE NO ACTION ON UPDATE NO ACTION
)
```

Rekening houdend met de [vereenvoudigde datatypes van SQLite](https://www.sqlite.org/datatype3.html),  zou je het zelf waarschijnlijk ongeveer zo schrijven:

```sql
CREATE TABLE album (
    AlbumId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    Title TEXT NOT NULL,
    ArtistId INTEGER NOT NULL,
    FOREIGN KEY ArtistId REFERENCES artists (ArtistId)
)
```

De `NO ACTION` statements zijn nutteloos. Namen hoeven, afhankelijk van het dialect, al dan niet escaped als `[naam]` of `"naam"`. Merk ook op dat bovenstaande SQL zal falen als de tabel `artists` niet eerst gemaakt wordt, anders kan de DB geen foreign key constraint controle uitvoeren. Meerdere statements worden gebruikelijk gescheiden door puntkomma `;`.

{{% notice note %}}
Merk op dat in SQL DDL we keywords met HOOFDLETTER schrijven en tabel of kolomnamen met kleine letter.
{{% /notice %}}

- Probleem met je structuur? Geen probleem: `DROP TABLE album`. Als hier data in zit ben je die ook onherroepelijk kwijt! 
- Kolom vergeten? Geen probleem: `ALTER TABLE album ADD COLUMN blah TEXT`.
- Kolom te veel? Geen probleem: `ALTER TABLE album DROP COLUMN blah`.
- Not null constraint vergeten? Geen probleem: `ALTER TABLE album ALTER COLUMN blah NOT NULL`. Oei, syntaxfoutje? [SQLite ondersteunt geen alter in alter](https://sqlite.org/lang_altertable.html), andere vendors wel. 
- Check constraint vergeten? Geen probleem: `ALTER TABLE album ADD CONSTRAINT my_constraint CHECK(len(blah) > 9)` Oei, syntaxfoutje? Zelfde probleem---enkel op te lossen door `DROP` en re-create.

De `CREATE` en `DROP` statements kunnen ook gebruikt worden---afhankelijk van de compatibiliteit van je RDBMS---om indexen, aliases, tablespaces, synoniemen, sequenties, ... aan te maken en te verwijderen.

{{% task %}}
Maak twee nieuwe tabellen aan: een **licenses** tabel, die per album (en dus ook artiest) licenties en hun kostprijs opslaat, en een **memorabelia** tabel die merchandise voor artiesten bevat om te verkopen. Denk goed na over het gebruik van constraints. Voeg daarna met `ALTER TABLE` kolommen toe om het (variërend) BTW percentage voor beide tabellen te bewaren.
{{% /task %}}


## Views aanmaken

Een view is eigenlijk een specifieke query die we een vaste naam geven. 

Als ik een view wil maken van alle tracks van het Rock genre, dan doe ik dat als volgt:

```sql
CREATE VIEW rock_tracks AS
    SELECT * FROM tracks
    WHERE GenreId = 1
```

Vanaf dit punt kan ik in een nieuwe query het volgende uitvoeren:

```sql
SELECT * FROM rock_tracks
```

Merk op dat we vaak geen idee hebben welke ID het genre `Rock` heeft:

```sql
SELECT * FROM genres WHERE Name = 'Rock'
```

In plaats van de resultaten te limiteren op `GenreId`, kunnen we beide tabellen _joinen_:

```sql
CREATE VIEW rock_tracks AS
    SELECT tracks.* FROM tracks
    INNER JOIN genres ON genres.GenreId = tracks.GenreId
    WHERE genres.Name = 'Rock'
```

Dit kan ook met behulp van een subquery. Zie ook: [DDML - JOIN operator](/sql-ddl-dml/dml/#join) en verder, subqueries.


{{% task %}}
Schrijf een view die de naam van elke track geeft, alsook de naam van het album en de artiest, gesorteerd op artiest, album en dan track.
{{% /task %}}

