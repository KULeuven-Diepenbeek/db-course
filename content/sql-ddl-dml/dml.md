---
title: 2. DML
---

**Data Modification Language** is de taal die we gebruiken om de data van onze database te bekijken of aan te passen. Met DML kunnen we CRUD operaties uitvoeren. Create, Read, Update en Delete.

## SELECT

`SELECT` is het commando dat we gebruiken om data op te vragen uit de database. 

```
    SELECT { DISTINCT } expression
    FROM table
    { WHERE condition }
```

### LIKE operator

`LIKE` wordt gebruikt om wildcard searches uit te voeren. Deze kan enkel gebruikt worden op alfanumerieke velden. 

`%` is een match anything character voor een onbeperkt aantal karakters (0 tot n).
Zo matcht `Gen%` met volgende waardes: Gen, Genk, Gent, Genève, Genua, ...

`_` is een match anything character voor één karakter.
Zo matcht `Gen_` met volgende waardes: Genk, Gent, ...
Maar niet met volgende waardes: Gen, Genève, Genua, ...

### NULL

`NULL` is het ontbreken van een waarde. Het is een onbekende. We kunnen in DML niet zomaar vergelijken met `NULL`. We kunnen namelijk niet zeggen dat een onbekende gelijk is aan een andere onbekende. 

Hieronder een overzicht van een binary AND table met `True`, `False` en `NULL` waardes.

|   AND   |   True    |   False   |   NULL    |
| ------- | --------- | --------- | --------- |
| True    | True      |	False     |	NULL      |
| False   |	False     |	False     |	False     |
| NULL    |	NULL      |	False	  | NULL      |

{{% task %}}
Denkvraag: Waarom is `False == NULL` gelijk aan `False`?
{{% /task %}}

Hieronder vinden we de binary OR table met `True`, `False` en `NULL` waardes.

|   OR    |   True    |   False   |   NULL    |
| ------- | --------- | --------- | --------- |
| True    | True      |	True      |	True      |
| False   |	True      |	False     |	NULL      |
| NULL    |	True      |	NULL	  | NULL      |

Als we willen vergelijken met `NULL` in queries, dan gebruiken we volgende code:

```<value> IS NULL```

en


```<value> IS NOT NULL```

{{% task %}}
1. Schrijf een query die alle Customers (volledige naam, customer ID en land) laat zien die niet wonen in de USA.
2. Schrijf een query die enkel de Customers laat zien die in Brazilië wonen.
3. Schrijf een query die alle Employees laat zien die werken in de Sales afdeling.
4. Schrijf een query die een unieke lijst van landen laat zien uit de Invoices tabel.
5. Schrijf een query die alle Tracks laat zien waarvoor er geen componist bekend is.
6. Schrijf een query van alle unieke Componisten. Als de componist niet bekend is, dan moet er 'Onbekend' weergegeven worden gesorteerd op naam.
7. Schrijf een query die het maximumbedrag van alle Invoices laat zien.
{{% /task %}}

### JOIN

Wanneer we informatie willen ophalen uit meerdere tabellen dan gebruiken we daar een JOIN statement voor.  Die syntax ziet er als volgt uit:

```
    SELECT { DISTINCT } expression
    FROM table
    INNER JOIN other_table ON join_condition
    { WHERE condition }
```

Hiermee matchen we alle data van de ene tabel met de andere tabel op de meegegeven conditie. Er bestaan drie verschillende types JOINs:

- `INNER JOIN` - Geeft alle resultaten die bestaan zowel in de ene als de andere tabel
- `LEFT JOIN` - Geeft alle resultaten die bestaan in de base tabel, ook al bestaan die niet in de tabel waarop we joinen
- `RIGHT JOIN` - Wordt in de praktijk zelden tot nooit gebruikt. Geeft alle resultaten die bestaan in de gejoinde tabel ook al bestaan ze niet in de base tabel.

{{% task %}}
1. Schrijf een query die alle Invoices laat zien van alle Customers uit Brazilië. Het resultaat moet de volledige naam van de Customer, Invoice ID, Invoice Datum en Billing Country weergeven.
2. Schrijf een query die alle Invoices laat zien voor elke Sales Agent. Het resultaat moet de volledige naam van de Sales Agent weergeven.
3. Schrijf een query die het Invoice Totaal, de Customer naam en land en de naam van de Sales Agent weergeeft voor alle Invoices en Customers.
4. Schrijf een query die het aantal invoice lijnen weergeeft voor Invoice ID 37.
5. Schrijf een query die de track naam weergeeft langs elke invoice lijn.
6. Schrijf een query die de track naam en de artiest naam weergeeft langs elke invoice lijn.
7. Schrijf een query die alle tracks laat zien, maar geen ID's. Het resultaat moet de album titel, het media type en het genre bevatten.
8. Schrijf een query die alle genres weergeeft waarvoor er geen tracks bestaan.
{{% /task %}}

### GROUP BY

Soms willen we data **aggregeren**. In Basic Engineering Skills in Python werd aggregratie gebruikt om bijvoorbeeld de som van een lijst te nemen, of met `funtools.reduce()` een custom functie los te laten op een lijst. (Dit gaan we ook nog zien in het hoofdstuk rond [NoSQL -- Advanced map-reduce queries](/db-course/nosql/mapreduce/)).

In RDBMS bestaan hiervoor een aantal verschillende functies. De meest courante zijn hieronder te vinden:

- `MAX()`
- `MIN()`
- `COUNT()`
- `AVG()`
- `SUM()`

Elke waarde die je extra selecteert in een query bovenop een aggregate function, moet in een `GROUP BY` clause komen. Hoe ziet dit er dan bijvoorbeeld uit?

```sql
SELECT BillingCity, SUM(Total)
FROM Invoices
GROUP BY BillingCity
```

Zonder `GROUP BY` statement krijg je ofwel een fout ofwel maar één record terug, zoals in SQLite. 

### HAVING

Als we willen filteren op een grouping function, dan gaat dat niet via een `WHERE` clause, dan krijg je namelijk een foutmelding:

```sql
SELECT BillingCity, count(*) FROM invoices
WHERE count(*) > 2
GROUP BY BillingCity
```

![](/slides/img/where_error.png)

Om te filteren op een grouping function schrijven we dit in een `HAVING` clause die de query gebruikerlijks afsluit: 

```sql
SELECT BillingCity, count(*) FROM invoices
GROUP BY BillingCity
HAVING count(*) > 2
```

{{% task %}}
1. Schrijf een query die het aantal Invoices laat zien voor 2009 en 2011.
2. Schrijf een query die het aantal invoices per land laat zien.
3. Schrijf een query die per Invoice ID het aantal invoice lijnen laat zien.
4. Schrijf een query die de naam van elke playlist laat zien, alsook het aantal tracks in elke playlist.
5. Schrijf een query die alle data uit de Invoices tabel laat zien, aangevuld met het aantal invoice lijnen.
6. Schrijf een query die de totale verkoopcijfers per Sales Agent laat zien.
7. Schrijf een query die laat zien welke Sales Agent de grootste verkoopcijfers heeft voor 2009.
8. Schrijf een query die laat zien welke Sales Agent de grootste verkoopcijfers heeft voor 2010.
9. Schrijf een query die laat zien welke Sales Agent de grootste verkoopcijfers heeft over alle jaren heen.
10. Schrijf een query die het aantal Customers laat zien per Sales Agent.
11. Schrijf een query die de totale verkoopcijfers per land laat zien. In welk land werd er het meest uitgegeven?
12. Schrijf een query die laat zien welke track er in 2013 het meest werd verkocht.
13. Schrijf een query die laat zien wat de top 5 tracks zijn die ooit werden verkocht.
14. Schrijf een query die laat zien wie de top 3 artiesten zijn die het meest verkocht werden.
14. Schrijf een query die laat zien welk media type het meest verkocht werd.
15. Schrijf een query die de tracks laat zien die meer dan 4 keer verkocht zijn.
{{% /task %}}

### Subqueries

Een query die we uitvoeren geeft een set van resultaten terug. Die set kunnen we opnieuw gebruiken als input voor een nieuwe query. We kunnen die set op verschillende plaatsen gebruiken als input voor een nieuwe query. Hieronder een aantal voorbeelden.

#### In een WHERE clause

```sql
SELECT * FROM invoice_items
WHERE invoice_items.TrackId IN (
	SELECT tracks.TrackId FROM tracks
	WHERE name LIKE '%hell%'
)
```

#### In een FROM clause

```sql
SELECT * FROM (
	SELECT tracks.TrackId, tracks.Name FROM tracks
	WHERE name LIKE '%hell%'
)
```

#### In een JOIN clause

```sql
SELECT * FROM tracks
INNER JOIN (
	SELECT tracks.TrackId, tracks.Name FROM tracks
	WHERE name LIKE '%hell%'
) hell_tracks ON hell_tracks.TrackId = tracks.TrackId
```

{{% notice warning %}}
Subqueries in een `WHERE IN` statement worden geëvauleerd voor elke voor elke rij uit de outer query, dus zijn eigenlijk niet zo heel performant. We kunnen dat iets verbeteren door dat te herschrijven naar een `WHERE EXISTS` statement. Zie hieronder.
{{% /notice %}}

```sql
SELECT * FROM invoice_items
WHERE EXISTS (
	SELECT 1 FROM tracks
	WHERE name LIKE '%hell%' AND tracks.TrackId = invoice_items.TrackId
)
```

De `IN` clause gaat een subquery volledig ophalen om alle rijen te hebben om dan in die lijst van rijen te kunnen zoeken. Een `EXISTS` clause gaat een subquery maar zo lang uitvoeren tot er een resultaat gevonden is. Als de tabel uit de subquery 1000 rijen bevat en er wordt een match gevonden op rij 200, dan gaan de andere 800 niet meer geëvauleerd worden.

{{% task %}}
De meeste van deze queries kunnen ook geschreven worden met een `JOIN` statement. Dit is echter niet waar we hier op willen oefenen. Los dus volgende oefeningen op met minstens één subquery. Als je hier moeite mee hebt kan her handig zijn om eerst een werkende query te bekomen met `JOIN` en die dan om te vormen naar een subquery.

1. Schrijf een query die alle invoices laat zien die een track bevatten van Iron Maiden.
2. Schrijf een query die alle invoices laat zien die verkocht werden door Margaret Park.
3. Schrijf een query die alle genres laat zien waarvoor er geen track bestaat.
4. Schrijf een query die alle invoices laat zien waarvan de prijs groter is dan het gemiddelde van alle invoices.
5. Schrijf een query die alle invoices laat zien waarin een Metallica track verkocht is, waarvan de prijs groter is dan het gemiddelde van alle invoices waarin een Metallica track verkocht is.
{{% /task %}}

### Data manipulatie

#### INSERT

Met een `INSERT` Statement gaan we data toevoegen in de database. We gebruiken een column listing om aan te geven welke kolommen, in welke volgorde, we van een waarde kan voorzien. Kolommen die `NULL` values ondersteunen mogen uit de column listing gelaten worden. 

```sql
INSERT INTO Genres(Name)
VALUES('Rock')
```

{{% task %}}
Voeg je favoriete album (inclusief artiest en tracks) toe aan de database.
{{% /task %}}

#### UPDATE

Met een `UPDATE` statement kunnen we één of meerdere waardes in een set van data aanpassen.

```sql
UPDATE Tracks 
SET MediaTypeId = 1
WHERE AlbumId = 2
```

{{% task %}}
Wijzig de UnitPrice en de Composer voor de 3e track van je toegevoegde album.<br/>
 Wijzig de titel van je favoriete album (zie oefening hierboven).
{{% /task %}}

#### DELETE

Hiermee kunnen we een set van data verwijderen. 

LET OP! Een `DELETE` statement zonder `WHERE` clause verwijdert alles uit de tabel!

```sql
DELETE FROM Genre
WHERE Name = 'Rock'
```


{{% task %}}
Verwijder het album (inclusief artiest en tracks) dat je hierboven hebt toegevoegd.
{{% /task %}}

Wat is volgens jou het verschil tussen `DELETE FROM` zonder `WHERE` en `DROP TABLE`?
