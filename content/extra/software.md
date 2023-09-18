---
title: Gebruikte Software
---

## SQL

SQL-gebaseerde oefeningen nemen plaats in de [SQLite DB Browser](https://sqlitebrowser.org/) omgeving, een visuele open source tool om eenvoudig database files te maken en bewerken die compatibel zijn met SQLite, een SQL variant. 

Vanuit SQLiteBrowser kan je een nieuwe database aanmaken, een bestaande `.SQL` file openen en uitvoeren, of een bestaande `.db` SQLite database openen. 

Voor het hoofdstuk SQL DDL & DML gebruiken we het `chinook.db` bestand dat je hier kan downloaden: [chinook.db](/chinook.db).

## Java/Kotlin/Gradle

Zie het 2dejaarsvak **Software Engineering Skills**, [SES/Gebruikte software](https://kuleuven-diepenbeek.github.io/ses-course/extra/software/). Voor SQL API software development oefeningen gebruiken we dezelfde tools: dezelfde Java JDK, _optioneel_ een Kotlin SDK, ook de IntelliJ IDE, en ook [Gradle als build tool](https://kuleuven-diepenbeek.github.io/ses-course/dependency-management/gradle/). Weet je niet meer hoe deze te gebruiken, volg dan de links om je geheugen te verfrissen. 

Tijdens labo's gebruiken we vaak Gradle dependencies om te experimenteren met nieuwe soorten databases zoals Memcached, SQLite, JDBI en Hibernate. Deze hoef je niet apart te downloaden en worden beheerd door de dependency manager. 

We verwachten nog steeds dat je vlot kan werken met **git**---als dit niet (meer) het geval is, zie de [SES/versiebeheer labo noties](https://kuleuven-diepenbeek.github.io/ses-course/versiebeheer/). Alle voorbeeldoefeningen zitten in de _"Course Git Repository"_ pagina (zie menu links, onder "More").

## NoSQL

### CouchDB

Installeer CouchDB 3.2.2 op je eigen systeem via https://couchdb.apache.org/#download. 

### PouchDB

Download de vereiste JavaScript files lokaal via https://pouchdb.com/download.html **of** gebruik gewoon de quick-start `jsdeliver.net` source:

```html
<script src="//cdn.jsdelivr.net/npm/pouchdb@7.3.0/dist/pouchdb.min.js"></script>
```

Zie de [PouchDB oefeningen labo noties](/nosql/replicataion).