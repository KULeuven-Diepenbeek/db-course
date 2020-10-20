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

