---
title: 2. Concurrency
---

## Meerdere threads met toegang tot de DB

Download het [concurrency startproject](/concurrency.zip). Het bevat een JDBC implementatie van de gekende studenten opgave, inclusief een aantal random `Runnable` thread workers die `INSERT`, `UPDATE` en `DELETE` statements issuen naar de database. De volgorde is een deel van de chaos... 

### Oefeningen

1. Inspecteer de huidige code van het project en vergewis je ervan dat je alle stappen begrijpt. Voer het een aantal keer uit. Wat zie je in de Stdout? Zijn de queries telkens dezelfde, in dezelfde volgorde? 
2. Transacties op elke query/statement toepassen is een eenvoudige oplossing maar lang niet de beste: de toegevoegde grote performantie hit kan bij miljoenen requests de applicatie volledig tot stilstand doen brengen. Welke repository calls zouden in een transactie moeten worden uitgevoerd, en bij welke calls is dat _niet_ nodig?
3. Speel met de parameter van `setAutoCommit()` in de `ConnectionManager`. Zie je een verschil? 
4. Speel met de parameter van `setTransactionIsolation()` op de connection. Welke vier mogelijke waardes worden ondersteund door SQLite? Wat is volgens de [Oracle docs](https://docs.oracle.com/cd/E19830-01/819-4721/beamv/index.html) het verschil tussen bijvoorbeeld `READ_UNCOMMITTED` en `READ_COMMITTED`? Kan je uit de context opmaken wat een "_dirty read_" is?
5. De [sqlite-jdbc driver](https://github.com/xerial/sqlite-jdbc) die we gebruiken heeft nog een aantal verborgen opties, waarvan `setTransactionMode` en `setLockingMode` het interessantste zijn om mee te prutsen. Kijk goed naar de Sysout output om het verschil te bepalen. Dat doe je door een `SQLiteConfig` object aan te maken en op de volgende manier mee te geven aan de `DriverManager`:

<div class="devselect">

```kt
val config = SQLiteConfig().apply {
    setX(val1)
    setY(val2)
}
connection = DriverManager.getConnection("jdbc:sqlite:...", config.toProperties())
```

```java
SQLiteConfig config = new SQLiteConfig();
config.setX(val1);
config.setY(val2);
connection = DriverManager.getConnection("jdbc:sqlite:...", config.toProperties());
```
</div>

**Extra**:

- Implementeer zelf de nodige Jdbi3 details in `StudentRepositoryJdbi3Impl`. In Jdbi stel je de transaction isolation level in met `.inTransaction(TransactionIsolationLevel.SERIALIZABLE, ...)` (zie [docs](https://jdbi.org)).

**Tip**: SQLite config, transactie en locking modes zijn uitgelegd in de [SQLite docs, pragma pagina](https://sqlite.org/pragma.html). 

### Connection Pooling

Uit de code blijkt dat alle threads momenteel éénzelfde connection instance delen via de `StudentRepository` implementatie. In de praktijk wordt er voor client/server applicaties altijd **connection pooling** toegepast: een aantal connections zijn beschikbaar in een _pool_, waar de clients uit kunnen kiezen. Als er een beschikbaar is, kan deze verder gaan. Als dat niet zo is, moet die bepaalde request wachten, tot een andere klaar is met zijn database acties en de connection terug vrijgeeft, opnieuw in de pool. Op die manier kan je bijvoorbeeld 6 connections verdelen over 10+ client threads, zoals in deze figuur (bron: oracle docs):

![](/img/connectionpool.gif "Bron: oracle docs")

Voor embedded single-file databases als SQLite is dit niet de gewoonte. Hibernate (zie 3. Database APIs) voorziet verschillende properties om connection pooling in te stellen, zoals aantal connections, aantal threads die kunnen requesten, timeout request, enzovoort. We gaan hier verder niet op in. 

