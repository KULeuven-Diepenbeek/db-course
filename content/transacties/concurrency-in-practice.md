---
title: 4. Concurrency in de Praktijk
weight: 4
author: Wouter Groenveld
draft: true
---

## Meerdere threads met toegang tot de DB

**Quickstart project**: `examples/concurrency` in de [cursus repository](https://github.com/kuleuven-Diepenbeek/db-course) ([download repo zip](https://github.com/KULeuven-Diepenbeek/db-course/archive/refs/heads/main.zip)). Het bevat een JDBC implementatie van de gekende studenten opgave, inclusief een `Runnable` thread worker die `INSERT`, `UPDATE` of `DELETE` statements issuen naar de database. Het probleem wat we hier proberen te simuleren is **DIRTY READS**.

<!-- Begeleidend filmpje:

<div style="position: relative; padding-bottom: 62.5%; height: 0;"><iframe src="https://www.loom.com/embed/7bec2d4a5aab482bad0443e6e6f8d68d" frameborder="0" webkitallowfullscreen mozallowfullscreen allowfullscreen style="position: absolute; top: 0; left: 0; width: 100%; height: 100%;"></iframe></div> -->



### Oefeningen

1. Inspecteer de huidige code van het project en vergewis je ervan dat je alle stappen begrijpt. Voer het een aantal keer uit. Wat zie je in de Stdout? Waarom wordt de student wel of niet in de `SELECT` teruggegeven?
2. Speel met de parameter van `setAutoCommit()` in de `ConnectionManager`. Zie je een verschil? 
3. Speel met de parameter van `setTransactionIsolation()` op de connection. Merk op dat SQLite enkel `SERIALIZABLE` en `READ_UNCOMMITED` ondersteund, maar het onmogelijk is om dirty reads te simuleren. Zie ook https://github.com/changemyminds/Transaction-Isolation-Level-Issue en de [Oracle docs](https://docs.oracle.com/cd/E19830-01/819-4721/beamv/index.html) voor het verschil tussen bijvoorbeeld `READ_UNCOMMITTED` en `READ_COMMITTED`? Kan je uit de context opmaken wat een "_dirty read_" is?
4. Probeer naast een dirty read ook een **PHANTOM READ** te simuleren met de H2 setup van bovenstaand project. 
5. Maak in een `for {}` loop tientallen verschillende threads aan en laat ze verschillende acties op de DB uitvoeren (lezen, schrijven, updaten, ...). Loopt er iets mis? Wat kan je **programmatorisch** doen om de chaos tot het minimum te beperken?

{{% notice note %}}
Om concurrency problemen makkelijk te kunnen demonstreren gebruiken we géén SQLite vanwege SQLite's [shared cache mode](https://www.sqlite.org/sharedcache.html). Let dus op je SQL syntax en connection string. Kleine verschillen kunnen SQL Exceptions veroorzaken bij andere database implementaties. <br/>
{{% /notice %}}


### Connection Pooling

Uit de code blijkt dat alle threads momenteel éénzelfde connection instance delen via de `StudentRepository` implementatie. In de praktijk wordt er voor client/server applicaties altijd **connection pooling** toegepast: een aantal connections zijn beschikbaar in een _pool_, waar de clients uit kunnen kiezen. Als er een beschikbaar is, kan deze verder gaan. Als dat niet zo is, moet die bepaalde request wachten, tot een andere klaar is met zijn database acties en de connection terug vrijgeeft, opnieuw in de pool. Op die manier kan je bijvoorbeeld 6 connections verdelen over 10+ client threads, zoals in deze figuur (bron: oracle docs):

![](/img/connectionpool.gif "Bron: oracle docs")

Voor embedded single-file databases als SQLite is dit niet de gewoonte. Hibernate (zie 3. Database APIs) voorziet verschillende properties om connection pooling in te stellen, zoals aantal connections, aantal threads die kunnen requesten, timeout request, enzovoort. We gaan hier verder niet op in. 

