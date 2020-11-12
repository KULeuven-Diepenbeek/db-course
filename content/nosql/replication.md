---
title: "4. Replication"
weight: 4
---

![](/img/pouchdb.jpg)

Met _replication_ is het eenvoudig om clusters van clones te maken om de 99.9% uptime te kunnen garanderen, gegeven de juiste loadbalancing instellingen. Als voorbeeld gaan we een open-source JavaScript DB gebruiken genaamd [PouchDB](https://pouchdb.com). PouchDB draait goed client-side in de browser, en interfacet heel gemakkelijk met zijn inspirator, CouchDB. Met Pouch is het een kwestie van een paar regeltjes code om replication aan te zetten tussen Pouch en de "master" Couch database, zoals ook zichtbaar op de Pouch website:

```javascript
var db = new PouchDB('dbname');

db.put({
  _id: 'dave@gmail.com',
  name: 'David',
  age: 69
});

db.changes().on('change', function() {
  console.log('Ch-Ch-Changes');
});

db.replicate.to('http://example.com/mydb');
```

Wat is **het doel**? Replication op te zetten tussen de cursussen database van [2. document stores](/nosql/documentstores) en de PouchDB JS web-based client. Dat kan op verschillende manieren:

1. Unidirectional replication. Zie [PouchDB Docs](https://pouchdb.com/guides/replication.html)
2. Bidirectional replication.
3. Live/Continuous replication. 

Je kan bovenstaande demo code onmiddellijk proberen op [https://pouchdb.com](https://pouchdb.com): Druk op `F12` of `CTRL+SHIFT+J` (Mac: `OPT+CMD+J`) of ga naar menu Developer -> Developer Tools van je favoriete browser. In de tab "Console" wordt je begroet door de PouchDB welkomsttekst. Daar kan je je test commando's in uitvoeren: `var db = ...`. Om te controleren of het record het tot in de database heeft gehaald, zie hieronder, bij tips. 

Gebruik in de oefeningen de CDN versie om het jezelf gemakkelijk te maken. Maak een leeg `.html` bestand aan en kopieer de Quick Start code over:

```html
<script src="//cdn.jsdelivr.net/npm/pouchdb@7.2.1/dist/pouchdb.min.js"></script>
<script>
  var db = new PouchDB('my_database');
</script>
```

Vergeet niet dat je lokale CouchDB waarschijnlijk draait op poort `5984`.

**Opgelet**: het kan zijn dat je browser, zoals een strict ingestelde Firefox, klaagt over Cross-Origin domains wanneer replication aan staat, omdat die naar `127.0.0.1` gaat, en je browser de `.html` file servet vanuit `file:///` wat technisch gezien niet dezelfde hostname is. Oplossing 1: gebruik een andere browser. Oplossing 2: disable CORS [in de browser](https://dev.to/andypotts/avoiding-cors-errors-on-localhost-in-2020-4mfn) (zie artiel). Optie 3: gebruik een python3 webserver om je bestand te serven. Open een terminal en typ `python -m http.server` in de directory van je html bestand. Ga dan naar `localhost:8000/oefening.html`.

### Oefeningen

1. Start CouchDB opnieuw met de bestaande courses db. Stel PouchDB in op unidirectionele replication. Alle LOKALE wijzigingen worden nu bewaard in de remote DB. Schrijf in Javascript ter test een nieuw fictief document weg met `db.put()`. Vul alle JSON properties in: kijk naar een bestaand document in je Couch database. 
2. Maak een nieuw `.html` bestand aan, en stel een [remote URL](https://pouchdb.com/guides/databases.html) in om vanuit JS onmiddellijk op de remote DB te kunnen queryen. 
    - Gebruik de [Mango query API](https://pouchdb.com/guides/mango-queries.html) van Pouch om in CouchDB de oefeningen van [2. document stores](/nosql/documentstores) te implementeren. 
    - Gebruik de [Mapreduce query API](https://pouchdb.com/guides/queries.html) van Pouch om in CouchDB de oefeningen van [3. advanced map/red. queries](/nosql/mapreduce) te implementeren. Merk op dat voor map **en** reduce beiden uit te voeren, je een JSON object moet meegeven met beide functies: `{ map: function(doc) { emit(...); }, reduce: '_count}`. Zie docs in link. 
3. Maak een nieuw `.html` bestand aan, en stel continuous replication in. Voeg dan een nieuw document toe in de CouchDB Admin console. Maak in HTML een knop die gewoon records afdrukt via `console.log()`. Wordt het nieuwe document getoond? Gebruik deze boilerplate:

```html
<script src="//cdn.jsdelivr.net/npm/pouchdb@7.2.1/dist/pouchdb.min.js"></script>
<button id="btn">Print docs</button>
<pre id="pre">
...
</pre>
<script>
function print(doc) {
    document.querySelector('#pre').innerHTML = JSON.stringify(doc);
}

  var db = new PouchDB('my_database');
  // do your setup here

  function queryDocs() {
     // do your thing here
     print('goed bezig');
  }

document.querySelector("#btn").addEventListener("click", queryDocs);
</script>
```

**Tips**: Wanneer je een item hebt toegevoegd aan je lokale JavaScript database met `.put()`, maar replication _nog niet_ aan staat, kan het handig zijn om met Chrome/Opera/... Dev Tools te kijken naar de **local storage** databases. Deze zijn terug te vinden in de tab "Application", bij "IndexedDB":

![](/img/localstorage.jpg)

De volgende elementen zijn te herknnen in bovenstaande screenshot:

- Ik heb een "`mydb`" object aangemaakt (DB naam `_pouch_mydb`)
- Onder element "`by-sequence`" kan je de huidige elementen in de DB raadplegen, zoals bovenstaande demo code waarbij object met naam "David" werd toegevoegd. 

### Denkvragen

- Heb je een verschil gemerkt tussen bidirectionele en live replication in PouchDB? Probeer beide instellingen uit en kijk in de Chrome/Opera/Mozilla Dev Tools van je browser naar de uitgaande HTTP requests. Op welk moment gebeurt dit? Welke `POST`/`GET` metadata wordt verstuurd?
- Wat is het verschil tussen in Pouch alle documenten op te vragen en daarna als array MapReduces toe te passen, of dit in de Mango query rechtstreeks te doen?
