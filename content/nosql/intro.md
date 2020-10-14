---
title: "1. Key/Value & Document stores"
weight: 1
---



## 1. Key/Value Stores

## 2. Document Stores

### 2.1 Eenvoudige CouchDB Queries

![](/slides/img/couchdb.png)

Lui in die zetel liggen, en vanaf de bank met gemak query's lanceren? Geen probleem met CouchDB, een open source NoSQL JSON-based document store. 

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

Tip: CouchDB heeft een eenvoudige ingebouwde query syntax genaamd **Mango**. Documentatie op [https://github.com/cloudant/mango](https://github.com/cloudant/mango) en [http://127.0.0.1:5984/_utils/docs/intro/api.html#documents](http://127.0.0.1:5984/_utils/docs/intro/api.html#documents). Lees eerst na hoe dit in elkaar zit! 

