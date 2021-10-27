---
title: "2. Document stores"
weight: 2
---

### 2.1 Eenvoudige CouchDB Queries

![](/slides/img/couchdb.png)

Lui in die zetel liggen, en vanaf de bank met gemak query's lanceren? Geen probleem met CouchDB, een open source NoSQL JSON-based document store. 

#### Mango

CouchDB heeft een eenvoudige ingebouwde query syntax genaamd **Mango**. Documentatie op [https://github.com/cloudant/mango](https://github.com/cloudant/mango) en [http://127.0.0.1:5984/_utils/docs/intro/api.html#documents](http://127.0.0.1:5984/_utils/docs/intro/api.html#documents). Selecteer een database, klik op "run a query with Mango":

```javascript
{
   "selector": {
      "year": 3
   }
}
```

De `selector` attribute bepaalt op welke keys er wordt gefilterd. Indexen leggen op zwaar belaste "kolommen" (keys dus) is in geval van miljarden records zeker geen overbodige luxe. 

Mango werkt met een _selector syntax_ (zie documentatie) die impliciet bovenstaande omzet naar `{"year": {"$eq": 3}}`. Er zijn ook andere dollar-based operatoren. Geneste attributes kan je raadplegen met de `.` separator: `{"student.name": {"eq": "Joske"}}`. 

#### De CouchDB API interface: alles via HTTP(S)

`curl` is een snelle cmd-line tool waarbij je via `-X` kan meegeven of het over een HTTPs `GET`, `POST`, `PUT`, ... gaat. De DB locatie en poort met het juiste **endpoint** zijn hier de belangrijkste factoren. Een bepaald document raadplegen doe je met:

```
curl -X GET http://127.0.0.1:5984/[database]/[id]
```

Het resultaat is altijd een geldig `JSON` object (ook al geef je een ongeldige ID mee): `curl -X GET "http://127.0.0.1:5984/courses/aalto-university;bachelor-data-science;professional-development;1"`

```
{"_id":"aalto-university;bachelor-data-science;professional-development;1","_rev":"1-f7872c4254bfc2e0e5507502e2fafd6f","title":"Professional Development","url":"https://oodi.aalto.fi/a/opintjakstied.jsp?OpinKohd=1125443391&haettuOpas=-1","university":"Aalto University","country":"Finland","category":"professional","ECTS":5,"year":1,"optional":true,"skills":["motivate self","oral communication","self-directed learning","self-reflection","give/receive feedback","set/keep timelines","show initiative"],"course":"Bachelor Data Science","lo":"<br/>Learning Outcomes   <br/>Being able to effectively communicate one's strenghts and professional capacities<br/>Finding one’s own academic and professional interests and taking initiative in one’s own learning<br/>Planning and prototyping one's own professional development<br/> <br/>Content     <br/>The course is integrated to the Aaltonaut program to promote reflection, skill articulation and initiative. The course comprises workshops on different themes related to developing professional skills, independently building a learning portfolio, and taking part in feedback, reflection and goal setting activities.<br/><br/> "}
```

Indien ongeldig: `{"error":"not_found","reason":"missing"}`.

Indien geen toegang: `{"error":"unauthorized","reason":"You are not authorized to access this db."}`. Zie "LET OP" hieronder---gebruik het `-u` argument. 

### 2.2 Oefeningen: Voorbereidingswerk

1. Download CouchDB via [https://couchdb.apache.org](https://couchdb.apache.org).
2. Download de [testdatabase JSON file](/db/dump.db)
3. Maak een nieuwe databases aan via de Fauxton Web-based admin tool. Open CouchDB, ga naar "**Open Admin Console**" of surf zelf naar [http://127.0.0.1:5984/_utils/](http://127.0.0.1:5984/_utils/). Maak een database aan genaamd '_courses_'.
4. Importeer de test JSON met `curl` in cmdnline:

```
curl -d @dump.db -H "Content-Type: application/json" -X POST http://127.0.0.1:5984/courses/_bulk_docs
```

De ongelukkigen op Windows kunnen [curl for Windows](https://curl.haxx.se/windows/) downloaden, of Msys/MinGW/de besturingssystemen ISO gebruiken. 

**LET OP**: 

1. Bij het aanmaken van een database kan je kiezen tussen partitioned en non-partitioned. Kies hiervoor _non-partitioned_.
2. Het kan zijn dar CURL een security fout geeft. Bij het installeren van CouchDB moet je een admin username/password meegeven. Voeg aan het einde van je curl commando dit toe: `-u username:wachtwoord`.

Nadien kan je in Fauxton op `F5` drukken en zou je dit moeten zien:

![](/img/fauxton.jpg)

Ik heb voor jullie de dump genomen door het omgekeerde (exporteren) te doen:

```
curl -X GET http://127.0.0.1:5984/courses/_all_docs\?include_docs\=true > dump.db
```

Daarna volgt wat post-processing (`rows` wordt `docs`, elke `doc` moet in de root array zitten en `_rev` moet weg) om tot bovenstaande [dump.db](/db/dump.db) filte te komen. Dit hebben wij handmatig voor jullie gedaan, zodat de downloadbare file klaar is om te importeren. 

### 2.3 Oefeningen met Fauxton/Curl

1. Schrijf een Mango query die cursussen ophaalt waarbij het aantal `ECTS` punten groter is dan 5. 
2. Hoe voer je de query uit oefening 1 uit, _zonder_ de Admin console, maar met `curl`? 
3. Selecteer alle documenten die als `skill` de waarde `self-reflection` én `show initiative` bevatten.
4. Probeer zelf een dump te nemen van je eigen database zoals hierboven beschreven, met het `_all_docs` endpoint. Wat gebeurt er als je die dump opnieuw wilt importeren via het `_bulk_docs` endpoint?
5. Maak een nieuwe database genaamd `studenten`. `POST` via `curl` enkele nieuwe documenten, met als template `{ name: $naam, age: $age, favouriteCourses: [$course1, $course2]}` naar deze DB. Controleer in Fauxton of de records correct zijn ingegeven. Verzin zelf wat Mango queries om studenten te filteren. 
6. Maak een index aan op `age` voor je `studenten` database. Merk op dat indexes, zichtbaar in http://127.0.0.1:5984/_utils/#database/studenten/_index ook worden beschouwd als documenten op zich!

Tip: CouchDB heeft een eenvoudige ingebouwde query syntax genaamd **Mango**. Documentatie op [https://github.com/cloudant/mango](https://github.com/cloudant/mango) en [https://docs.couchdb.org/en/stable/api/database/find.html](https://docs.couchdb.org/en/stable/api/database/find.html). Lees eerst na hoe dit in elkaar zit! 

Een uitgewerkt voorbeeld van oefening 1 en 2 in begeleidende video:

<div style="position: relative; padding-bottom: 62.5%; height: 0;"><iframe src="https://www.loom.com/embed/723d495a34bb4a77aa8e406761a3ba4d" frameborder="0" webkitallowfullscreen mozallowfullscreen allowfullscreen style="position: absolute; top: 0; left: 0; width: 100%; height: 100%;"></iframe></div>


### 2.4 Java Client API

Als je geen toegang hebt tot de admin console, of je wenst vanuit een Java programma records weg te schrijven naar een Couch database (of query's uit te voeren), dan heb je de Java API nodig. 

In principe kan je met eender welke `HTTP` client REST calls uitvoeren en de responses zelf verwerken. Om het jezelf gemakkelijker te maken, gebruiken we hier ter illustratie [LightCouch](http://www.lightcouch.org).

Lees de [LightCouch Getting Started guide](http://www.lightcouch.org/getstarted.html). Maak een nieuw gradle 6 project met de volgende dependencies:

```
dependencies {
    implementation group: 'org.lightcouch', name: 'lightcouch', version: '0.2.0'
}
```

In je `java/main/resources` map dien je een `couchdb.properties` file aan te maken die verwijst naar de DB URL/poort/naam (zie getting started):

```
couchdb.name=testdb
couchdb.createdb.if-not-exist=true
couchdb.protocol=http
couchdb.host=127.0.0.1
couchdb.port=5984
couchdb.username=
couchdb.password=
```

Vanaf dan is het heel eenvoudig: Maak een `CouchDbClient` instantie aan. Nu kan je `.save()`, `.shutdown()` en `.find()` uitvoeren. Wat kan je bewaren? POJO (**Plain Old Java Objects**) klassen---of in geval van Kotlin, data objects---waarbij alle members automatisch worden geserialiseerd. 

#### LightCouch oefeningen

1. Maak zoals hierboven beschreven een nieuw gradle project aan (IntelliJ?) en voeg LightCouch toe als dependency. Probeer naar een nieuwe database enkele objecten weg te schrijven. Gebruik hiervoor een `Student` klasse met als velden `name` en `age` (respectievelijk `String` en `int` als type). Controleer of dit is aangekomen in de admin console. Dat ziet er dan hopelijk zo uit:

```javascript
{
  "_id": "387a34be062140e4be1390e846242114",
  "_rev": "1-742f438439fd68bc6c67ca0d615f1469",
  "name": "Joske",
  "age": 10
}
```

2. Probeer de views en query's even uit. Zoek bijvoorbeeld alle studenten in `List<Student>` en druk de namen af door middel van `println()`.

### Denkvragen

1. Wat is het verschil tussen een key/value store en een document store?
2. Kan je een verklaring geven waarom NoSQL databases zonder DB SCHEME werken, als je weet dat bijvoorbeeld CouchDB plain JSON objecten kan bewaren? 
3. Wat is het verschil tussen het bewaren van een JSON object via Curl en het bewaren van een POJO via LightCouc (De Client API verschillen zelf niet in rekening gebracht)? 
