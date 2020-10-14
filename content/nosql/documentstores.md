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
curl -X http://127.0.0.1:5984/[database]/[id]
```

Het resultaat is altijd een geldig `JSON` object (ook al geef je een ongeldige ID mee): `curl -X GET "http://127.0.0.1:5984/courses/aalto-university;bachelor-data-science;professional-development;1"`

```
{"_id":"aalto-university;bachelor-data-science;professional-development;1","_rev":"1-f7872c4254bfc2e0e5507502e2fafd6f","title":"Professional Development","url":"https://oodi.aalto.fi/a/opintjakstied.jsp?OpinKohd=1125443391&haettuOpas=-1","university":"Aalto University","country":"Finland","category":"professional","ECTS":5,"year":1,"optional":true,"skills":["motivate self","oral communication","self-directed learning","self-reflection","give/receive feedback","set/keep timelines","show initiative"],"course":"Bachelor Data Science","lo":"<br/>Learning Outcomes   <br/>Being able to effectively communicate one's strenghts and professional capacities<br/>Finding one’s own academic and professional interests and taking initiative in one’s own learning<br/>Planning and prototyping one's own professional development<br/> <br/>Content     <br/>The course is integrated to the Aaltonaut program to promote reflection, skill articulation and initiative. The course comprises workshops on different themes related to developing professional skills, independently building a learning portfolio, and taking part in feedback, reflection and goal setting activities.<br/><br/> "}
```

Indien ongeldig: `{"error":"not_found","reason":"missing"}`.

### 2.2 Oefeningen: Voorbereidingswerk

1. Download CouchDB via [https://couchdb.apache.org](https://couchdb.apache.org).
2. Download de [testdatabase JSON file](/db/dump.db)
3. Maak een nieuwe databases aan via de Fauxton Web-based admin tool. Open CouchDB, ga naar "**Open Admin Console**" of surf zelf naar [http://127.0.0.1:5984/_utils/](http://127.0.0.1:5984/_utils/). Maak een database aan genaamd '_courses_'.
4. Importeer de test JSON met `curl` in cmdnline:

```
curl -d @dump.db -H "Content-Type: application/json" -X POST http://127.0.0.1:5984/courses/_bulk_docs
```

De ongelukkigen op Windows kunnen [curl for Windows](https://curl.haxx.se/windows/) downloaden, of Msys/MinGW/de besturingssystemen ISO gebruiken. 

Nadien kan je in Fauxton op `F5` drukken en zou je dit moeten zien:

![](/img/fauxton.jpg)

Ik heb voor jullie de dump genomen door het omgekeerde (exporteren) te doen:

```
curl -X GET http://127.0.0.1:5984/courses/_all_docs\?include_docs\=true > dump.db
```

En daarna wat post-processing (`rows` wordt `docs`, elke `doc` moet in de root array zitten en `_rev` moet weg). 

### 2.3 Oefeningen

1. Schrijf een Mango query die cursussen ophaalt waarbij het aantal `ECTS` punten groter is dan 5. 
2. Hoe voer je de query uit oefening 1 uit, _zonder_ de Admin console, maar met `curl`? 
3. Selecteer alle documenten die als `skill` de waarde `self-reflection` én `show initiative` bevatten.
4. Probeer zelf een dump te nemen van je eigen database zoals hierboven beschreven, met het `_all_docs` endpoint. Wat gebeurt er als je die dump opnieuw wilt importeren via het `_bulk_docs` endpoint?
5. Maak een nieuwe database genaamd `studenten`. `POST` via `curl` enkele nieuwe documenten, met als template `{ name: $naam, age: $age, favouriteCourses: [$course1, $course2]}` naar deze DB. Controleer in Fauxton of de records correct zijn ingegeven. Verzin zelf wat Mango queries om studenten te filteren. 
6. Maak een index aan op `age` voor je `studenten` database. Merk op dat indexes, zichtbaar in http://127.0.0.1:5984/_utils/#database/studenten/_index ook worden beschouwd als documenten op zich!

Tip: CouchDB heeft een eenvoudige ingebouwde query syntax genaamd **Mango**. Documentatie op [https://github.com/cloudant/mango](https://github.com/cloudant/mango) en [http://127.0.0.1:5984/_utils/docs/intro/api.html#documents](http://127.0.0.1:5984/_utils/docs/intro/api.html#documents). Lees eerst na hoe dit in elkaar zit! 

