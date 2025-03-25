---
title: Failures-Rollbacks
weight: 4
author: Wouter Groenveld
draft: false
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


## System failure simulatie

### In SQLite met DB Browser

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

#### Oefeningen

1. Probeer bovenstaande voorbeeld zelf uit in de SQLite DB Browser. Als je jezelf ervan verzekerd hebt dat inderdaad het eerste `UPDATE` statement wordt uitgevoerd, terwijl wij dat in één ACID blok willen, ga dan over naar de volgende oefening.
2. In SQLite is het starten van een transactie erg eenvoudig: zie [SQLite transaction tutorials](https://www.tutorialspoint.com/sqlite/sqlite_transactions.htm) van tutorialspoint.com. `BEGIN;` en `COMMIT;` zijn voldoende. Probeer dit uit in bovenstaande voorbeeld om er voor te zorgen dat de voornaam van Jaak niet wordt gewijzigd. Om met een "clean slate" te herbeginnen kan je gewoon de voorbereidende SQL code copy/pasten en opnieuw uitvoeren. Merk op dat dit nog steeds het ongewenst effect heeft dat de student zijn/haar naam wordt gewijzigd. We moeten expliciet zelf `ROLLBACK;` aanroepen. 
3. Probeer een nieuwe student toe te voegen: eentje met studentennummer, en eentje zonder. Dat tweede kan in principe niet door de `NOT NULL` constraint. Wrap beide statements in een transactie. 

**Let Op**: Het zou kunnen dat SQLite de volgende fout geeft: `cannot start a transaction within a transaction: BEGIN;`. Queries die geplakt worden in het "execute SQL" scherm worden meestal (onzichtbaar, achter de schermen) gewrapped in transacties. Stop de huidige transactie door `COMMIT;` uit te voeren met de knop "execute single SQL line". 

**Let Op**: Het zou kunnen dat `BEGIN TRANSACTION;` de transactie niet goed encapsuleert, maar simpelweg `BEGIN;` wel. Het `TRANSACTION` keyword is optioneel [volgens de SQLite docs](https://www.sqlite.org/lang_transaction.html) en lijkt, afhankelijk van de geïnstalleerde SQLite versie, ander gedrag te vertonen. 

### In SQLite met Java/JDBC

SQLite/JDBC uitleg: zie APIs - JDBC.

Gebruik nu  `connection.setAutoCommit(false)`. Deze regel is nodig omdat in JDBC standaard elke SQL statement aanschouwd wordt als een onafhankelijke transactie, die automatisch wordt gecommit:

> When a connection is created, it is in auto-commit mode. This means that each individual SQL statement is treated as a transaction and is automatically committed right after it is executed. (To be more precise, the default is for a SQL statement to be committed when it is completed, not when it is executed. A statement is completed when all of its result sets and update counts have been retrieved. In almost all cases, however, a statement is completed, and therefore committed, right after it is executed.)

Zie JDBC Basics in Oracle docs: https://docs.oracle.com/javase/tutorial/jdbc/basics/transactions.html

#### Oefeningen

1. Maak een nieuw Gradle project aan en connecteer naar je SQLite database. Merk op dat, bij connectionstring `"jdbc:sqlite:sample.db"`, automatisch een lege `.db` file wordt aangemaakt indien de database niet bestaat. Probeer met behulp van `executeUpdate()` en `executeQuery()` bovenstaande system failure te veroorzaken. Je kan de "foute SQL" (met "oeitiskapot") gewoon in een string in java copy/pasten. `executeUpdate()` kan verschillende statements tegelijkertijd verwerken. Verifieer dat de naam foutief toch wordt gewijzigd met een `SELECT()` nadat je de fout hebt opgevangen in een `try { }` block.
{{% notice warning %}}
Je moet nu dan ook wel de juiste aanpassingen doen om met een SQLite database te connecteren zie [hier](/apis/jdbc-jdbi#jdbc-met-sqlite)
{{% /notice %}}

2. Het probleem is op te lossen met één welgeplaatste regel: `connection.rollback()`. De vraag is echter: waar plaatsen we die? En ja, `rollback()` throwt ook de checked `SQLException`... Verifieer of je oplossing werkt door de naam na de rollback terug op te halen en te vergelijken met de juiste waarde: "Jaak".

De `DROP TABLE IF EXISTS` statements kan je in je project in een aparte SQL file bewaren en als een String inlezen, om in één keer te laten uitvoeren na het openen van de connectie:

```java
private void initTables() throws Exception {
    URI path = Objects.requireNonNull(App.class.getClassLoader().getResource("create_db.sql")).toURI();
    var create_db_sql = new String(Files.readAllBytes(Paths.get(path)));
    System.out.println(create_db_sql);

    var s = connection.createStatement();
    s.executeUpdate(create_db_sql);
    s.close();
}
```

De verwachte fout (met de ongeldige SQL regel) die SQLite doorgeeft aan Java genereert de volgende stacktrace:

```
org.sqlite.SQLiteException: [SQLITE_ERROR] SQL error or missing database (near ";": syntax error)
    at org.sqlite.core.DB.newSQLException(DB.java:1010)
    at org.sqlite.core.DB.newSQLException(DB.java:1022)
    at org.sqlite.core.DB.throwex(DB.java:987)
    at org.sqlite.core.NativeDB._exec_utf8(Native Method)
    at org.sqlite.core.NativeDB._exec(NativeDB.java:94)
    at org.sqlite.jdbc3.JDBC3Statement.executeUpdate(JDBC3Statement.java:109)
    at SQLiteTransactionTest.doStuff(SQLiteTransactionTest.java:54)
    at SQLiteMain.main(SQLiteMain.java:7)
``` 


## Denkvragen

- De SQLite website beschrijft in detail hoe ze omgaan met "atomic commits" om aan de ACID regels te voldoen. Lees dit na op https://sqlite.org/atomiccommit.html Op welke manier gebruiken zij een rollback journal? Hoe is dat gelinkt aan de logfile van 14.2.3 op p.435?