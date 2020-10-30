---
title: 1. Zonder Transacties
---

Voorbereidende `CREATE` statements (Dit is SQLite syntax!) Zie [SQLite manual](https://sqlite.org/autoinc.html):

```SQL
DROP TABLE IF EXISTS student;
CREATE TABLE student(
    studnr INT NOT NULL PRIMARY KEY,
    naam VARCHAR(200) NOT NULL,
    voornaam VARCHAR(200),
    goedbezig BOOL
);
DROP TABLE IF EXISTS log;
CREATE TABLE log(
    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    date DATETIME DEFAULT CURRENT_TIMESTAMP,
    foreign_id INT NOT NULL,
    msg TEXT
);
INSERT INTO student(studnr, naam, voornaam, goedbezig) VALUES (123, 'Trekhaak', 'Jaak', 0);
INSERT INTO student(studnr, naam, voornaam, goedbezig) VALUES (456, 'Peeters', 'Jos', 0);
INSERT INTO student(studnr, naam, voornaam, goedbezig) VALUES (890, 'Dongmans', 'Ding', 1);
```


## 1. System failure simulatie

Gegeven een aantal SQL statements, waarvan niet alle statements kloppen, maar die wel allemaal bij elkaar horen als één **atomaire transactie**. Dat betekent dat als er één van die statements misloopt, de rest teruggedraait zou moeten worden. Het spreekt voor zich dat zonder speciale handelingen, zoals het beheren van transacties, dit niet gebeurt. Een eenvoudig voorbeeld demonstreert dit. 

```SQL
UPDATE student SET voornaam = 'Jaqueline' WHERE studnr = 123;
INSERT INTO oeitiskapot;
INSERT INTO log(foreign_id, msg) VALUES (123, 'Voornaam vergissing');
INSERT INTO student(studnr, naam, voornaam, goedbezig) VALUES (445, 'Klakmans', 'Jef', 1);
INSERT INTO log(foreign_id, msg) VALUES (445, 'Nieuwe student registratie');
```

Plak dit in de "Execute SQL" tab van de SQLite DB Browser. Het resultaat is een foutboodschap:

```
near ";": syntax error: 
INSERT INTO oeitiskapot;
```

**Maar**: het eerste `UPDATE` statement, voor de foute regel, is wel uitgevoerd:

![](/img/sqlite-fout.jpg)

### 1.1 Oefeningen

1. Probeer bovenstaande voorbeeld zelf uit in de SQLite DB Browser. Als je jezelf ervan verzekerd hebt dat inderdaad het eerste `UPDATE` statement wordt uitgevoerd, terwijl wij dat in één ACID blok willen, ga dan over naar de volgende oefening.
2. In SQLite is het starten van een transactie erg eenvoudig: zie [SQLite transaction tutorials](https://www.tutorialspoint.com/sqlite/sqlite_transactions.htm) van tutorialspoint.com. `BEGIN;` en `COMMIT;` of alternatief `BEGIN TRANSACTION;` en `END TRANSACTION;` zijn voldoende. Probeer dit uit in bovenstaande voorbeeld om er voor te zorgen dat de voornaam van Jaak niet wordt gewijzigd. Om met een "clean slate" te herbeginnen kan je gewoon de voorbereidende SQL code copy/pasten en opnieuw uitvoeren. Merk op dat dit nog steeds het ongewenst effect heeft dat de student zijn/haar naam wordt gewijzigd. We moeten expliciet zelf `ROLLBACK;` aanroepen. 
3. Probeer een nieuwe student toe te voegen: eentje met studentennummer, en eentje zonder. Dat tweede kan in principe niet door de `NOT NULL` constraint. Wrap beide statements in een transactie. 

**Let Op**: Het zou kunnen dat SQLite de volgende fout geeft: `cannot start a transaction within a transaction: BEGIN;`. Queries die geplakt worden in het "execute SQL" scherm worden meestal (onzichtbaar, achter de schermen) gewrapped in transacties. Stop de huidige transactie door `COMMIT;` uit te voeren met de knop "execute single SQL line". 

## 2. Media failure simulatie

### 2.1 Oefeningen

## Denkvragen

- De SQLite website beschrijft in detail hoe ze omgaan met "atomic commits" om aan de ACID regels te voldoen. Lees dit na op https://sqlite.org/atomiccommit.html Op welke manier gebruiken zij een rollback journal? Hoe is dat gelinkt aan de logfile van 14.2.3 op p.435?