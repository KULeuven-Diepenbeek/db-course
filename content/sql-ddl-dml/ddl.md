---
title: 1. DDL
---

**Data Defintion Language** is de taal die we gebruiken om de structuur van onze database te veranderen. We kunnen hiermee tabellen aanmaken, wijzigen of verwijderen. Maar ook indexen, views, triggers of stored procedures worden hiermee aangemaakt.

Zowat elke RDBMS heeft tooling om DDL te doen via een handige interface, in plaats van dit zelf uit te schrijven. In de praktijk ga je waarschijnlijk met beiden in contact komen. We gaan [DB Browser for SQLite](https://sqlitebrowser.org/dl/) gebruiken tijdens onze lessen. 

### Oefening

Kijk naar de Chinook database en maak een schematische voorstelling van hoe deze database eruit ziet. Je kan hiervoor [Mermaid](https://mermaid.live/) gebruiken, of een eigen tool of pen en papier.

## Views aanmaken

Een view is eigenlijk een specifieke query die we een vaste naam geven. 

Als ik een view wil maken van alle tracks van het Rock genre, dan doe ik dat als volgt:

```sql
CREATE VIEW rock_tracks AS
    SELECT tracks.* FROM tracks
    INNER JOIN genres ON genres.GenreId = tracks.GenreId
    WHERE genres.Name = 'Rock'
```

Vanaf dit punt kan ik in een nieuwe query het volgende uitvoeren:

```sql
SELECT * FROM rock_tracks
```

### Oefening

1. Schrijf een view die de naam van elke track geeft, alsook de naam van het album en de artiest. Gesorteerd op artiest, album en dan track.