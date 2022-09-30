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

Denkvraag: Waarom is `False == NULL` gelijk een `False`?

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

#### Oefeningen

1. Schrijf een query die alle Customers (volledige naam, customer ID en land) laat zien die niet wonen in de USA.
2. Schrijf een query die enkel de Customers laat zien die in Brazilië wonen.
3. Schrijf een query die alle Employees laat zien die werken in de Sales afdeling.
4. Schrijf een query die een unieke lijst van landen laat zien uit de Invoices tabel.
5. Schrijf een query die alle Tracks laat zien waarvoor er geen componist bekend is.
6. Schrijf een query van alle unieke Componisten. Als de componist niet bekend is, dan moet er 'Onbekend' weergegeven worden gesorteerd op naam.
7. Schrijf een query die het maximumbedrag van alle Invoices laat zien.

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

#### Oefeningen

1. Schrijf een query die alle Invoices laat zien van alle Customers uit Brazilië. Het resultaat moet de volledige naam van de Customer, Invoice ID, Invoice Datum en Billing Country weergeven.
2. Schrijf een query die alle Invoices laat zien voor elke Sales Agent. Het resultaat moet de volledige naam van de Sales Agent weergeven.
3. Schrijf een query die het Invoice Totaal, de Customer naam en land en de naam van de Sales Agent weergeeft voor alle Invoices en Customers.
4. Schrijf een query die het aantal invoice lijnen weergeeft voor Invoice ID 37.
5. Schrijf een query die de track naam weergeeft langs elke invoice lijn.
6. Schrijf een query die de track naam en de artiest naam weergeeft langs elke invoice lijn.
7. Schrijf een query die alle tracks laat zien, maar geen ID's. Het resultaat moet de album titel, het media type en het genre bevatten.
8. Schrijf een query die alle genres weergeeft waarvoor er geen tracks bestaan.

### GROUP BY

Soms willen we data aggregeren. Daarvoor bestaan een aantal verschillende functies. De meest courante zijn hieronder te vinden:

- `MAX()`
- `MIN()`
- `COUNT()`
- `AVG()`
- `SUM()`

Elke waarde die je extra selecteert in een query bovenop een aggregate function, moet in een `GROUP BY` clause komen. Hoe ziet dit er dan bijvoorbeeld uit?

```
SELECT BillingCity, SUM(Total)
FROM Invoices
GROUP BY BillingCity
```

#### Oefeningen

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
15. Schrijf een query die het aantal invoices laat zien die bestaan uit verschillende tracks met meerdere genres.