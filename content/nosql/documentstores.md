---
title: Document Stores met MongoDB
draft: false
weight: 2
---

## MongoDB

### Introductie

Wat is MongoDB en hoe verschilt het van andere soorten databases? MongoDB is een NoSQL gedistribueerde database. Omdat gegevens niet binnen de strikte grenzen van een relationeel model hoeven te passen, kan MongoDB functioneren als een algemene gegevensopslag. Dit biedt verschillende voordelen.

In MongoDB worden gegevens opgeslagen in een **flexibel schema**. Als de behoeften van je applicatie veranderen, kun je eenvoudig de structuur van je gegevens aanpassen. Dankzij **schema-validatie** kun je bepalen hoe strikt of flexibel je schema moet zijn. Dit maakt MongoDB geschikt voor uiteenlopende databehoeften.

In relationele databases worden **relaties** tussen gegevens in verschillende tabellen gerealiseerd via joins. In hiërarchische databases zijn relaties tussen knooppunten vaak onmogelijk. MongoDB biedt echter de mogelijkheid om documenten te koppelen via operaties zoals `$lookup` of door middel van referenties.

Daarnaast heeft MongoDB **geen single point of failure**, wat betekent dat het systeem robuuster is. Bovendien ondersteunt MongoDB transacties, wat de atomische uitvoering van lees- en schrijfbewerkingen over meerdere documenten garandeert. Dit is vooral handig bij complexe query's over meerdere documenten.

MongoDB is ontworpen voor applicaties in het internet era, waar gebruikers gegevens vanaf verschillende locaties kunnen manipuleren. Met **ingebouwde ondersteuning voor replicatie, load balancing en aggregatie** is MongoDB een veelzijdig onderdeel van moderne softwarearchitectuur.

#### Voordelen van MongoDB Atlas: Cloud omgeving

MongoDB Atlas is een multi-cloud documentdatabaseservice. Het is een volledig beheerde dienst die wordt uitgevoerd door een team van MongoDB-systeembeheerders, zodat jij je kunt richten op je eigen applicatie. MongoDB Atlas is beschikbaar op cloudproviders zoals AWS, Microsoft Azure en Google Cloud Platform, waardoor het flexibel inzetbaar is.

#### Bedrijven die MongoDB gebruiken

Enkele bekende bedrijven die MongoDB gebruiken zijn eBay, Forbes en FEMA.

### Installatie

Om MongoDB te installeren:
- Download de communityversie via [MongoDB Community Download](https://www.mongodb.com/try/download/community). Deze versie biedt krachtige manieren om gegevens te analyseren en te bevragen, inclusief ondersteuning voor ad-hoc queries, secundaire indexering en real-time aggregaties.
- Installeer ook de GUI-tool [MongoDB Compass](https://www.mongodb.com/products/compass) om je gegevens visueel te beheren.
- Volg de installatiehandleiding: [Install MongoDB op Windows](https://www.mongodb.com/docs/manual/tutorial/install-mongodb-on-windows/#std-label-install-mdb-community-windows). Deze handleiding biedt stapsgewijze instructies voor het installeren van MongoDB Community Edition op Windows.
- (Bekijk de installatievideo: [MongoDB Installatie op Windows](https://www.youtube.com/watch?v=gB6WLkSrtJk&pp=ygUPbW9uZ29kYiB3aW5kb3dz).)

Voor gebruik in VSCode kun je de extensie "MongoDB for VSCode" installeren. Hiermee kun je gegevens exporteren naar bijvoorbeeld Java code.

### Overzicht van Gebruik

MongoDB is een NoSQL-database zonder vast schema. Gegevens worden opgeslagen in **BSON (Binary JSON)**, een JSON-formaat met uitgebreide datatypes. In MongoDB worden tabellen **"collecties"** genoemd en rijen **"documenten"**. Elk document heeft een unieke `_id`.

### Voorbeeld 

Een voorbeeld van een collectie met 3 documents in MongoDB:

```json
[
  {
    "_id": "00000020f51bb4362eee2a4d", 
    "studnr": 123,
    "naam": "Trekhaak",
    "voornaam": "Jaak",
    "goedBezig": false,
    "opleiding": {
      "naam": "IIW",
      "keuze": "Informatica"
    },
    "vakken": ["DAB", "SES", "FSWEB"]
  },
  {
    "_id": "507f191e810c19729de860ea", 
    "studnr": 456,
    "naam": "Peeters",
    "voornaam": "Jos",
    "goedBezig": false,
    "opleiding": {
      "naam": "IIW",
      "keuze": "EA-ICT"
    },
    "vakken": ["DAB", "SES"]
  },
  {
    "_id": "6592008029c8c3e4dc76256c", 
    "studnr": 890,
    "naam": "Dongmans",
    "voornaam": "Ding",
    "goedBezig": true,
    "vakken": ["DAB"]
  }
]
```

**Merk op dat voor de derde student de Opleiding ontbreekt, dit kan dus in NoSql document stores. Het is dan wel belangrijk dat je software hier rekening mee houdt!**


<!-- TODO mongosh installeren: https://www.mongodb.com/try/download/shell, https://www.mongodb.com/docs/mongodb-shell/install/ -->
### Belangrijke Commando's

De querytaal van MongoDB is **JavaScript**, of meer specifiek: mongosh (de MongoDB Shell) is een volwaardige JavaScript-omgeving. Dat betekent dat je in mongosh gewone JavaScript-code kunt schrijven: variabelen declareren, loops gebruiken, functies definiëren, enz.

De query-objecten die je meegeeft aan `find()`, `insertOne()`, `updateOne()` e.d. zijn gewone **JavaScript-objecten** (letterlijk JSON-structuren). MongoDB vertaalt die intern naar BSON voor opslag, maar als gebruiker schrijf je altijd in JSON-notatie.

```javascript
// Dit is gewone JavaScript in mongosh
let prijsgrens = 500;
db.producten.find({ price: { $gt: prijsgrens } }).forEach(doc => print(doc.item));
```

```bash
# Versie controleren
$ mongosh --version

# Verbinden met een database
$ mongosh "<connection string>" --username <username>

# Lokale verbinding
$ mongosh

# IN MONGOSH
# Databases weergeven
show dbs
# Database selecteren
use <db name>
# Database verwijderen
db.dropDatabase()
# MongoSH beëindigen
exit

# Collectie aanmaken
db.createCollection("<collection name>")
# Document toevoegen
db.<collection name>.insertOne(<json>)
# Meerdere documenten toevoegen
db.<collection name>.insertMany(<jsonArray>)

# Documenten opvragen
db.<collection name>.find()
db.<collection name>.find({<keyname>: "<value>"})
db.<collection name>.find({<keyname>: {<operation>}})

db.<collection name>.find().toArray()[0]._id
db.<collection name>.find(…).sort({<key>: 1})  or -1 for descending order
db.<collection name>.find(…).count()
db.<collection name>.find(…).limit(2)
db.<collection name>.find(…).skip(<nr>)
db.<collection name>.findOne({<keyname>: {<operation>}})
  # Operation example: {$gt: 3}
  # other operators: $gt, $lt, $gte, $lte, $eq, $in, $exists: true, 


# Documenten bijwerken
db.<collection name>.updateOne({<criteria>}, {$set: {<key>: <value>}})
  # criteria: search criteria like you use in find
  # without $set the whole document is replaced with the new JSON and not only the corresponding values change
db.<collection name>.updateMany()

# Documenten verwijderen
db.<db name>.deleteOne(<query>)
db.<db name>.deleteMany(<query>)

db.<db name>.replaceOne(<query>)
db.<db name>.replaceMany(<query>)
```

Meer informatie over query- en updateoperators is te vinden in de [MongoDB-documentatie](https://www.mongodb.com/docs/development/).

### Aggregaties

MongoDB ondersteunt aggregaties, waarmee je complexe bewerkingen op gegevens kunt uitvoeren. Aggregaties zijn een krachtige manier om gegevens te transformeren en te analyseren. Ze worden vaak gebruikt om samenvattingen, statistieken of andere berekeningen uit te voeren op grote datasets. Aggregaties worden uitgevoerd met behulp van een aggregatie-pipeline, die bestaat uit een reeks stappen die gegevens verwerken en transformeren.

Enkele veelgebruikte aggregatiestappen zijn:
- `$match`: Filteren van gegevens op basis van criteria.
- `$sort`: Sorteren van gegevens op een specifieke volgorde.
- `$project`: Selecteren en transformeren van specifieke velden in documenten.
- `$group`: Groeperen van gegevens en uitvoeren van berekeningen zoals som, gemiddelde of telling.
- `$limit`: Beperken van het aantal resultaten.
- `$skip`: Overslaan van een bepaald aantal resultaten.

Een voorbeeld van een aggregatie in Java en in de console.

<!-- 
db.mijnCollectie.insertMany([
  { item: "Laptop",     price: 900, quantity: 3, date: new Date("2014-01-15") },
  { item: "Laptop",     price: 950, quantity: 2, date: new Date("2014-03-10") },
  { item: "Phone",      price: 600, quantity: 5, date: new Date("2014-02-20") },
  { item: "Phone",      price: 650, quantity: 4, date: new Date("2014-07-05") },
  { item: "Tablet",     price: 400, quantity: 6, date: new Date("2014-04-12") },
  { item: "Tablet",     price: 420, quantity: 3, date: new Date("2014-09-18") },
  { item: "Monitor",    price: 250, quantity: 7, date: new Date("2014-05-22") },
  { item: "Keyboard",   price: 80,  quantity: 10, date: new Date("2014-06-30") },
  { item: "Mouse",      price: 50,  quantity: 12, date: new Date("2014-08-14") },
  { item: "Printer",    price: 300, quantity: 2, date: new Date("2014-11-11") },

  // outside 2014 (to test filter)
  { item: "Laptop",     price: 1000, quantity: 1, date: new Date("2015-01-10") },
  { item: "Phone",      price: 700,  quantity: 2, date: new Date("2013-12-25") }
]);
-->

**Aggregatie in Java:**
```java
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import java.util.Arrays;

public class AggregationExample {
    public static void main(String[] args) {
        String uri = "mongodb://localhost:27017";
        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase("mijnDatabase");
            MongoCollection<Document> collectie = database.getCollection("mijnCollectie");

            collectie.aggregate(Arrays.asList(
                new Document("$match", new Document("date", new Document("$gte", "2014-01-01").append("$lt", "2015-01-01"))),
                new Document("$group", new Document("_id", "$item")
                    .append("totalSaleAmount", new Document("$sum", new Document("$multiply", Arrays.asList("$price", "$quantity"))))),
                new Document("$sort", new Document("totalSaleAmount", -1)),
                new Document("$limit", 5)
            )).forEach(doc -> System.out.println(doc.toJson()));
        }
    }
}
```

**Aggregatie in de console (mongosh)**
```javascript
use mijnDatabase;
db.mijnCollectie.aggregate([
  { $match: { date: { $gte: new Date('2014-01-01'), $lt: new Date('2015-01-01') } } },
  { $group: { _id: "$item", totalSaleAmount: { $sum: { $multiply: [ "$price", "$quantity" ] } } } },
  { $sort: { totalSaleAmount: -1 } },
  { $limit: 5 }
]);
```

**Let wel op! De volgorde bij aggregaties zeer belangrijk is: Stel dat je in bovenstaande voorbeeld eerst een `$limit 5` zou doen en daarna pas een `$match`, dan zou je dus enkel matchen zoeken binnen de 5 documenten die nog over hebt na je voorgaande `$limit` commando.** 

Meer informatie over aggregaties is te vinden in de [MongoDB-documentatie](https://www.mongodb.com/docs/manual/aggregation/).

#### Oefeningen

_De gegeven oplossingen zijn EEN mogelijke oplossing, soms zijn meerdere mogelijkheden juist. Is het gewenste gedrag bereikt, dan is je oplossing correct!_

Voor de volgende oefeningen gebruik je de onderstaande dataset. Maak eerst een database aan genaamd `winkel`, selecteer ze en voeg de data in met het volgende commando:

```javascript
use winkel;
db.producten.insertMany([
  { item: "Laptop",   price: 900,  quantity: 3,  date: new Date("2014-01-15") },
  { item: "Laptop",   price: 950,  quantity: 2,  date: new Date("2014-03-10") },
  { item: "Phone",    price: 600,  quantity: 5,  date: new Date("2014-02-20") },
  { item: "Phone",    price: 650,  quantity: 4,  date: new Date("2014-07-05") },
  { item: "Tablet",   price: 400,  quantity: 6,  date: new Date("2014-04-12") },
  { item: "Tablet",   price: 420,  quantity: 3,  date: new Date("2014-09-18") },
  { item: "Monitor",  price: 250,  quantity: 7,  date: new Date("2014-05-22") },
  { item: "Keyboard", price: 80,   quantity: 10, date: new Date("2014-06-30") },
  { item: "Mouse",    price: 50,   quantity: 12, date: new Date("2014-08-14") },
  { item: "Printer",  price: 300,  quantity: 2,  date: new Date("2014-11-11") },
  { item: "Laptop",   price: 1000, quantity: 1,  date: new Date("2015-01-10") },
  { item: "Phone",    price: 700,  quantity: 2,  date: new Date("2013-12-25") }
]);
```

##### Oefeningenreeks 1: Queries

- Toon alle documenten in `producten`.
<!-- EXSOL -->
_**<span style="color: #03C03C;">Solution:</span>**_ ```db.producten.find()```
- Toon enkel de documenten waar `item` gelijk is aan `"Laptop"`.
<!-- EXSOL -->
_**<span style="color: #03C03C;">Solution:</span>**_ ```db.producten.find({ item: "Laptop" })```
- Toon alle documenten met een `price` groter dan 500.
<!-- EXSOL -->
_**<span style="color: #03C03C;">Solution:</span>**_ ```db.producten.find({ price: { $gt: 500 } })```
- Toon alle documenten met een `price` tussen 200 en 500 (inclusief grenzen).
<!-- EXSOL -->
_**<span style="color: #03C03C;">Solution:</span>**_ ```db.producten.find({ price: { $gte: 200, $lte: 500 } })```
- Toon het eerste document in de collectie.
<!-- EXSOL -->
_**<span style="color: #03C03C;">Solution:</span>**_ ```db.producten.findOne()```
- Tel het totaal aantal documenten in de collectie.
<!-- EXSOL -->
_**<span style="color: #03C03C;">Solution:</span>**_ ```db.producten.find().count()```
- Tel het aantal documenten waar `item` gelijk is aan `"Phone"`.
<!-- EXSOL -->
_**<span style="color: #03C03C;">Solution:</span>**_ ```db.producten.find({ item: "Phone" }).count()```
- Toon alle documenten gesorteerd op `price` van laag naar hoog.
<!-- EXSOL -->
_**<span style="color: #03C03C;">Solution:</span>**_ ```db.producten.find().sort({ price: 1 })```

<br>

- Toon de 3 duurste documenten (gesorteerd op `price` van hoog naar laag, maximum 3 resultaten).
<!-- EXSOL -->
_**<span style="color: #03C03C;">Solution:</span>**_  
```javascript
db.producten.find().sort({ price: -1 }).limit(3)
```
- Toon alle documenten met een `quantity` groter dan 5, gesorteerd op `quantity` aflopend, en sla de eerste 2 resultaten over.
<!-- EXSOL -->
_**<span style="color: #03C03C;">Solution:</span>**_  
```javascript
db.producten.find({ quantity: { $gt: 5 } }).sort({ quantity: -1 }).skip(2)
```

##### Oefeningenreeks 2: Updates & Deletes

- Zet de `price` van de `"Mouse"` op `55`.
<!-- EXSOL -->
_**<span style="color: #03C03C;">Solution:</span>**_ ```db.producten.updateOne({ item: "Mouse" }, { $set: { price: 55 } })```
- Verhoog de `quantity` van alle `"Keyboard"`-documenten met `5` (gebruik `$inc`).
<!-- EXSOL -->
_**<span style="color: #03C03C;">Solution:</span>**_ ```db.producten.updateMany({ item: "Keyboard" }, { $inc: { quantity: 5 } })```
- Voeg een veld `inStock: true` toe aan alle documenten met een `quantity` groter dan 5.
<!-- EXSOL -->
_**<span style="color: #03C03C;">Solution:</span>**_ ```db.producten.updateMany({ quantity: { $gt: 5 } }, { $set: { inStock: true } })```
- Verwijder het document met `item` gelijk aan `"Printer"`.
<!-- EXSOL -->
_**<span style="color: #03C03C;">Solution:</span>**_ ```db.producten.deleteOne({ item: "Printer" })```

<br>

- Verwijder alle documenten waarvan de `date` buiten het jaar 2014 valt (dus vóór 2014 of na 2014).
<!-- EXSOL -->
_**<span style="color: #03C03C;">Solution:</span>**_  
```javascript
db.producten.deleteMany({
  $or: [
    { date: { $lt: new Date("2014-01-01") } },
    { date: { $gte: new Date("2015-01-01") } }
  ]
})
```

##### Oefeningenreeks 3: Aggregaties

- Bereken per `item` het totale verkoopbedrag (`price * quantity`) en sorteer de resultaten van hoog naar laag.
<!-- EXSOL -->
<details closed>
<summary><i><b><span style="color: #03C03C;">Solution:</span> Klik hier om de code te zien/verbergen</b></i>🔽</summary>
<p>

```javascript
db.producten.aggregate([
  { $group: { _id: "$item", totalSaleAmount: { $sum: { $multiply: ["$price", "$quantity"] } } } },
  { $sort: { totalSaleAmount: -1 } }
])
```

</p>
</details>

- Bereken de gemiddelde `price` per `item`.
<!-- EXSOL -->
<details closed>
<summary><i><b><span style="color: #03C03C;">Solution:</span> Klik hier om de code te zien/verbergen</b></i>🔽</summary>
<p>

```javascript
db.producten.aggregate([
  { $group: { _id: "$item", avgPrice: { $avg: "$price" } } }
])
```

</p>
</details>

- Toon enkel de documenten uit het jaar 2014, groepeer ze per `item`, bereken het totale verkoopbedrag en toon enkel de top 3 resultaten (gesorteerd van hoog naar laag).
<!-- EXSOL -->
<details closed>
<summary><i><b><span style="color: #03C03C;">Solution:</span> Klik hier om de code te zien/verbergen</b></i>🔽</summary>
<p>

```javascript
db.producten.aggregate([
  { $match: { date: { $gte: new Date("2014-01-01"), $lt: new Date("2015-01-01") } } },
  { $group: { _id: "$item", totalSaleAmount: { $sum: { $multiply: ["$price", "$quantity"] } } } },
  { $sort: { totalSaleAmount: -1 } },
  { $limit: 3 }
])
```

</p>
</details>

### Schema-validatie

Schema-validatie in MongoDB biedt een manier om de structuur van documenten in een collectie te definiëren en te valideren. Dit helpt om de integriteit van gegevens te waarborgen en fouten te voorkomen. Met schema-validatie kun je regels instellen voor de velden in een document, zoals het type, de vereiste velden en de toegestane waarden.

**Hoe schema-validatie aanbrengen?**<br/>
Schema-validatie wordt ingesteld op het niveau van een collectie. Bij het aanmaken van een collectie kun je een validatieregelspecificatie opgeven met behulp van het `validator`-veld. Dit veld bevat een query die de validatieregels definieert.

**Syntax voor schema-validatie in Java**<br/>
Hier is een voorbeeld van hoe je schema-validatie kunt instellen in Java voor de `students`-collectie:
```java
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class SchemaValidationExample {
    public static void main(String[] args) {
        String uri = "mongodb://localhost:27017";
        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase("mijnDatabase");

            Document schema = new Document("bsonType", "object")
                .append("required", List.of("studnr", "naam", "voornaam", "goedBezig", "vakken"))
                .append("properties", new Document()
                    .append("studnr", new Document("bsonType", "int")
                        .append("description", "Studentnummer is verplicht en moet een geheel getal zijn."))
                    .append("naam", new Document("bsonType", "string")
                        .append("description", "Naam is verplicht en moet een string zijn."))
                    .append("voornaam", new Document("bsonType", "string")
                        .append("description", "Voornaam is verplicht en moet een string zijn."))
                    .append("goedBezig", new Document("bsonType", "bool")
                        .append("description", "goedBezig is verplicht en moet een boolean zijn."))
                    .append("opleiding", new Document("bsonType", "object")
                        .append("description", "Opleiding is optioneel maar moet een object zijn indien aanwezig.")
                        .append("properties", new Document()
                            .append("naam", new Document("bsonType", "string"))
                            .append("keuze", new Document("bsonType", "string"))))
                    .append("vakken", new Document("bsonType", "array")
                        .append("items", new Document("bsonType", "string"))
                        .append("description", "Vakken is verplicht en moet een array van strings zijn.")));

            database.createCollection("students", new Document("validator", new Document("$jsonSchema", schema)));
        }
    }
}
```

**Syntax voor schema-validatie in de console (mongosh)**
```javascript
use mijnDatabase;
db.createCollection("students", {
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: ["studnr", "naam", "voornaam", "goedBezig", "vakken"],
      properties: {
        studnr: {
          bsonType: "int",
          description: "Studentnummer is verplicht en moet een geheel getal zijn."
        },
        naam: {
          bsonType: "string",
          description: "Naam is verplicht en moet een string zijn."
        },
        voornaam: {
          bsonType: "string",
          description: "Voornaam is verplicht en moet een string zijn."
        },
        goedBezig: {
          bsonType: "bool",
          description: "goedBezig is verplicht en moet een boolean zijn."
        },
        opleiding: {
          bsonType: "object",
          description: "Opleiding is optioneel maar moet een object zijn indien aanwezig.",
          properties: {
            naam: { bsonType: "string" },
            keuze: { bsonType: "string" }
          }
        },
        vakken: {
          bsonType: "array",
          items: { bsonType: "string" },
          description: "Vakken is verplicht en moet een array van strings zijn."
        }
      }
    }
  }
});
```

**Validatie aanpassen**<br/>
Je kunt de validatieregels van een bestaande collectie aanpassen met het commando `collMod`:

```javascript
db.runCommand({
  collMod: "students",
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: ["studnr", "naam", "voornaam", "goedBezig", "vakken"],
      properties: {
        studnr: { bsonType: "int" },
        naam: { bsonType: "string" },
        voornaam: { bsonType: "string" },
        goedBezig: { bsonType: "bool" },
        opleiding: {
          bsonType: "object",
          properties: {
            naam: { bsonType: "string" },
            keuze: { bsonType: "string" }
          }
        },
        vakken: {
          bsonType: "array",
          items: { bsonType: "string" }
        }
      }
    }
  }
});
```

**Validatie uitschakelen**<br/>
Als je schema-validatie tijdelijk wilt uitschakelen, kun je de validatieactie instellen op "warn" in plaats van "error":

```javascript
db.runCommand({
  collMod: "mijnCollectie",
  validationAction: "warn"
});
```

Met deze aanpak kun je schema-validatie flexibel toepassen en aanpassen aan de behoeften van je applicatie. We beperkten ons hier tot een simpel voorbeeld, maar voor een volledige lijst van JSON schema object fields kan je [deze link](https://www.mongodb.com/docs/manual/reference/operator/query/jsonSchema/#json-schema) gebruiken.

### Indexes

Een **index** is een aparte datastructuur die MongoDB bijhoudt naast je collectie. Die structuur bevat een gesorteerde kopie van de waarden van één of meer velden, samen met een verwijzing naar het bijhorende document. Dit maakt het mogelijk om snel te zoeken op die velden, vergelijkbaar met de index achteraan een boek: je hoeft het hele boek niet te lezen, maar gaat meteen naar de juiste pagina.

Zonder een index moet MongoDB een **collection scan** uitvoeren: elk document in de collectie wordt één voor één bekeken om te controleren of het aan de query voldoet. Bij een kleine collectie is dit nauwelijks merkbaar, maar bij miljoenen documenten loopt dit snel op.

Met een index kan MongoDB via de gesorteerde structuur in een paar stappen naar de juiste waarde navigeren - de complexiteit daalt van $O(n)$ naar $O(\log n)$.

**Voorbeeld:** Stel je hebt een collectie met 1 000 000 producten en je zoekt op `item: "Laptop"`.
- *Zonder index:* MongoDB leest alle 1 000 000 documenten.
- *Met een index op `item`:* MongoDB navigeert direct naar alle documenten met `item = "Laptop"` - typisch slechts een handvol stappen.

**Nadelen van indexes**

Indexes versnellen lezen, maar brengen kosten mee:
- Ze nemen **extra schijfruimte** in.
- Bij elke **schrijfoperatie** (insert, update, delete) moeten alle betrokken indexes bijgewerkt worden, wat schrijven iets trager maakt.

Indexes aanmaken op velden die weinig of niet gebruikt worden in queries is dus geen goed idee.

Standaard maakt MongoDB automatisch een index aan op het `_id`-veld. Je kunt zelf extra indexes aanmaken op andere velden.

**Index aanmaken**

```javascript
// Enkelvoudige index op één veld (oplopend)
db.producten.createIndex({ item: 1 })

// Enkelvoudige index (aflopend)
db.producten.createIndex({ price: -1 })

// Samengestelde index op meerdere velden
db.producten.createIndex({ item: 1, price: -1 })

// Unieke index (geen duplicaten toegestaan)
db.producten.createIndex({ item: 1 }, { unique: true })
```

**Indexes bekijken**

```javascript
db.producten.getIndexes()
```

**Index verwijderen**

```javascript
db.producten.dropIndex({ item: 1 })
```

**Query-uitvoeringsplan bekijken**<br/>
Met `explain()` kun je zien of een query gebruik maakt van een index:

```javascript
db.producten.find({ item: "Laptop" }).explain("executionStats")
```

In de output zie je bij `winningPlan` of er een `IXSCAN` (index scan) of een `COLLSCAN` (collection scan) gebruikt wordt. Een `IXSCAN` betekent dat de index gebruikt wordt, wat veel sneller is bij grote datasets.

Meer informatie over indexes is te vinden in de [MongoDB-documentatie](https://www.mongodb.com/docs/manual/indexes/).

#### Oefeningen

##### Oefeningenreeks 1: Schema-validatie 

- Voeg schema-validatie toe aan de bestaande `producten`-collectie in de `winkel`-database (gebruik `collMod`). Elk document moet voldoen aan de volgende regels:
  - `item` (string, verplicht)
  - `price` (double of int, verplicht, minimum 0)
  - `quantity` (int, verplicht, minimum 0)
  - `date` (date, verplicht)
  - `inStock` (bool, optioneel)

<!-- EXSOL -->
<!-- <details closed>
<summary><i><b><span style="color: #03C03C;">Solution:</span> Klik hier om de code te zien/verbergen</b></i>🔽</summary>
<p>

```javascript
use winkel;
db.runCommand({
  collMod: "producten",
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: ["item", "price", "quantity", "date"],
      properties: {
        item: {
          bsonType: "string",
          description: "item is verplicht en moet een string zijn."
        },
        price: {
          bsonType: ["double", "int"],
          minimum: 0,
          description: "price is verplicht en moet een getal zijn groter dan of gelijk aan 0."
        },
        quantity: {
          bsonType: "int",
          minimum: 0,
          description: "quantity is verplicht en moet een geheel getal zijn groter dan of gelijk aan 0."
        },
        date: {
          bsonType: "date",
          description: "date is verplicht en moet een datum zijn."
        },
        inStock: {
          bsonType: "bool",
          description: "inStock is optioneel maar moet een boolean zijn indien aanwezig."
        }
      }
    }
  }
});
```

</p>
</details> -->

##### Oefeningenreeks 2: Indexes 

- Maak een index aan op het veld `item` in de `producten`-collectie.
<!-- EXSOL -->
<!-- _**<span style="color: #03C03C;">Solution:</span>**_ ```db.producten.createIndex({ item: 1 })``` -->
- Bekijk alle bestaande indexes op de `producten`-collectie.
<!-- EXSOL -->
<!-- _**<span style="color: #03C03C;">Solution:</span>**_ ```db.producten.getIndexes()``` -->

<br>

- Maak een samengestelde index aan op `item` (oplopend) en `price` (aflopend), en controleer daarna via `explain()` of een query op `item: "Laptop"` gebruik maakt van deze index.
<!-- EXSOL -->
<!-- _**<span style="color: #03C03C;">Solution:</span>**_  
```javascript
db.producten.createIndex({ item: 1, price: -1 })
db.producten.find({ item: "Laptop" }).explain("executionStats")
// Controleer in de output of winningPlan een IXSCAN toont
``` -->

### MongoDB Compass

MongoDB Compass is een grafische gebruikersinterface (GUI) waarmee je eenvoudig je MongoDB-databases kan beheren en verkennen. Met Compass kan je:

- **Databases en collecties bekijken**: Je kan de structuur van je database en collecties visueel inspecteren.
- **Query's uitvoeren**: Compass biedt een intuïtieve interface om query's te schrijven en resultaten te bekijken.
- **Documenten bewerken**: Je kan individuele documenten in je collecties bekijken, bewerken en verwijderen.
- **Indexen beheren**: Compass laat je indexen bekijken en beheren om de prestaties van je database te optimaliseren.
- **Aggregaties uitvoeren**: Met de ingebouwde aggregatie-pipeline-builder kan je complexe aggregaties maken en testen.

Om MongoDB Compass te gebruiken:
1. Start Compass en verbind met je MongoDB-database door de connection string in te voeren.
2. Navigeer door je databases en collecties in de linkernavigatiebalk.
3. Gebruik de zoekbalk om query's te schrijven en resultaten te filteren.
4. Klik op een document om het te bewerken of te verwijderen.

Meer informatie over MongoDB Compass is te vinden op de [officiële website](https://www.mongodb.com/products/compass).

### MongoDB VSCode Extensie

De MongoDB VSCode-extensie integreert MongoDB-functionaliteit direct in Visual Studio Code. Hiermee kun je:

- **Verbinden met MongoDB**: Maak verbinding met een lokale of cloudgebaseerde MongoDB-database.
- **Gegevens verkennen**: Bekijk databases, collecties en documenten in een boomstructuur.
- **Query's uitvoeren**: Schrijf en voer query's uit in een geïntegreerde editor.
- **Gegevens exporteren**: Exporteer gegevens naar JSON of andere formaten.
- **Code genereren**: Genereer codefragmenten voor je applicatie, zoals Java of Node.js.

Om de extensie te gebruiken:
1. Installeer de extensie "MongoDB for VSCode" via de VSCode Marketplace.
2. Open de MongoDB Explorer in de zijbalk.
3. Voeg een nieuwe verbinding toe door de connection string in te voeren.
4. Navigeer door je databases en voer query's uit in de editor.

Meer informatie over de extensie is te vinden op de [VSCode Marketplace](https://marketplace.visualstudio.com/items?itemName=mongodb.mongodb-vscode).

### MongoDB in Java

Om een verbinding te maken met een MongoDB-database in Java, heb je de volgende Gradle-dependency nodig:

```groovy
dependencies {
    implementation 'com.google.code.gson:gson:2.10.1'
    implementation 'org.mongodb:mongodb-driver-sync:4.9.0'
}
```

Hier is een voorbeeld van hoe je een verbinding kunt maken en een eenvoudige CRUD-operatie kunt uitvoeren:

```java
import com.mongodb.client.*;
import org.bson.Document;

public class MongoDBExample {
    public static void main(String[] args) {
        // Verbinden met de MongoDB-database
        String uri = "mongodb://localhost:27017";
        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase("mijnDatabase");
            MongoCollection<Document> collectie = database.getCollection("students");

            // Create: Document toevoegen
            Document nieuwDocument = new Document("studnr", 999)
                    .append("naam", "Janssen")
                    .append("voornaam", "Lisa")
                    .append("goedBezig", true)
                    .append("vakken", java.util.List.of("DAB", "FSWEB"));
            collectie.insertOne(nieuwDocument);

            // Read: Alle documenten opvragen
            for (Document doc : collectie.find()) {
                System.out.println(doc.toJson());
            }

            // Read: Documenten opvragen met een query (enkel studenten die goedBezig zijn)
            for (Document doc : collectie.find(new Document("goedBezig", true))) {
                System.out.println(doc.toJson());
            }

            // Update: Document bijwerken
            collectie.updateOne(new Document("naam", "Janssen"),
                    new Document("$set", new Document("goedBezig", false)));

            // Delete: Document verwijderen
            collectie.deleteOne(new Document("naam", "Janssen"));
        }
    }
}
```
<!-- TODO: hierboven ook een voorbeeld van een find met een query ingeven volgend jaar -->
{{% notice info %}}
Meer informatie kan je [hier in de documentatie](https://www.mongodb.com/docs/drivers/java/sync/current/get-started/) terugvinden!
{{% /notice %}}

#### CRUD-commando's

Hier zijn de belangrijkste commando's die je nodig hebt om een kleine CRUD-applicatie te maken:

1. **Create**:
   ```java
   collectie.insertOne(new Document("key", "value"));
   ```

2. **Read**:
   ```java
   for (Document doc : collectie.find()) {
       System.out.println(doc.toJson());
   }
   ```

3. **Update**:
   ```java
   collectie.updateOne(new Document("key", "value"),
           new Document("$set", new Document("key", "newValue")));
   ```

4. **Delete**:
   ```java
   collectie.deleteOne(new Document("key", "value"));
   ```

Met deze tools en voorbeelden kun je eenvoudig aan de slag met MongoDB in Java en andere ontwikkelomgevingen.

#### Java Objecten en JSON

Om een Java-object om te zetten naar JSON en omgekeerd, kun je gebruik maken van Gson. Deze bibliotheek maakt het eenvoudig om objecten te serialiseren en deserialiseren. Hier is een voorbeeld met de klasse `Student` en het bijbehorende JSON-document.

**Gradle Dependencies**<br/>
Voor Gson:

```groovy
dependencies {
    implementation 'com.google.code.gson:gson:2.10.1'
    implementation 'org.mongodb:mongodb-driver-sync:4.9.0'
}
```

**Klasse `Student`**<br/>
Hier is een voorbeeld van hoe je de klasse `Student` kunt definiëren:

```java
import com.google.gson.annotations.SerializedName;
import org.bson.types.ObjectId;
import java.util.List;

public class Student {

    @SerializedName("_id")
    private ObjectId id;
    private int studnr;
    private String naam;
    private String voornaam;
    private boolean goedBezig;
    private Opleiding opleiding;
    private List<String> vakken;

    // Getters en setters

    public static class Opleiding {
        private String naam;
        private String keuze;

        // Getters en setters
    }
}
```

**Object naar JSON en JSON naar Object**<br/>
Hier is een voorbeeld van hoe je een `Student`-object kunt omzetten naar JSON en hoe je JSON kunt omzetten naar een `Student`-object met Gson:

```java
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bson.Document;

public class JsonExample {
    public static void main(String[] args) {
        Gson gson = new GsonBuilder().create();

        // Voorbeeld: Student-object naar JSON
        Student student = new Student();
        student.setStudnr(123);
        student.setNaam("Trekhaak");
        student.setVoornaam("Jaak");
        student.setGoedBezig(false);

        Student.Opleiding opleiding = new Student.Opleiding();
        opleiding.setNaam("IIW");
        opleiding.setKeuze("Informatica");
        student.setOpleiding(opleiding);

        student.setVakken(List.of("DAB", "SES", "FSWEB"));

        // Object naar JSON
        String json = gson.toJson(student);
        System.out.println("JSON: " + json);

        // JSON naar Object
        String jsonString = "{\"_id\":\"00000020f51bb4362eee2a4d\",\"studnr\":123,\"naam\":\"Trekhaak\",\"voornaam\":\"Jaak\",\"goedBezig\":false,\"opleiding\":{\"naam\":\"IIW\",\"keuze\":\"Informatica\"},\"vakken\":[\"DAB\",\"SES\",\"FSWEB\"]}";
        Student deserializedStudent = gson.fromJson(jsonString, Student.class);
        System.out.println("Student: " + deserializedStudent.getNaam());
    }
}
```

**Opslaan en Ophalen in MongoDB**<br/>
Hier is een voorbeeld van hoe je een `Student`-object kunt opslaan in en ophalen uit een MongoDB-database met Gson:

```java
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoDBJsonExample {
    public static void main(String[] args) {
        String uri = "mongodb://localhost:27017";
        Gson gson = new GsonBuilder().create();

        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase("mijnDatabase");
            MongoCollection<Document> collectie = database.getCollection("studenten");

            // Student-object naar JSON en opslaan in MongoDB
            Student student = new Student();
            student.setStudnr(123);
            student.setNaam("Trekhaak");
            student.setVoornaam("Jaak");
            student.setGoedBezig(false);

            Student.Opleiding opleiding = new Student.Opleiding();
            opleiding.setNaam("IIW");
            opleiding.setKeuze("Informatica");
            student.setOpleiding(opleiding);

            student.setVakken(List.of("DAB", "SES", "FSWEB"));

            String json = gson.toJson(student);
            Document document = Document.parse(json);
            collectie.insertOne(document);

            // Ophalen uit MongoDB en JSON naar Student-object
            Document dbDocument = collectie.find().first();
            if (dbDocument != null) {
                String dbJson = dbDocument.toJson();
                Student deserializedStudent = gson.fromJson(dbJson, Student.class);
                System.out.println("Ophalen uit MongoDB: " + deserializedStudent.getNaam());
            }
        }
    }
}
```

##### Oefeningen

Maak een nieuw **Gradle-project** aan en voeg de juiste dependencies toe in `build.gradle`.

Maak een klasse `Main` aan met een `main`-methode. Zet in die methode alle eerdere oefeningen om van mongosh naar Java. Verbind telkens met de database `winkel` en de collectie `producten`. Druk de resultaten af via `System.out.println()`.

**Oefeningenreeks 1 — Queries:**
- Haal alle producten op.
- Haal alle producten op met een prijs groter dan 500.
- Haal alle producten op waar `inStock` gelijk is aan `true`.
- Haal het eerste product op met de naam `"Laptop"`.
- Haal alle producten op gesorteerd op prijs (aflopend).
- Haal alleen de velden `item` en `price` op van alle producten (projectie — gebruik `Projections.include(...)`).

**Oefeningenreeks 2 — Updates & Deletes:**
- Verhoog de prijs van alle producten van het type `"Laptop"` met 100.
- Stel voor alle producten met `quantity` kleiner dan 5 het veld `inStock` in op `false`.
- Verwijder het product met de naam `"Muis"`.

**Oefeningenreeks 3 — Aggregaties:**
- Bereken de totale waarde van de voorraad (som van `price * quantity`) voor alle producten.
- Groepeer producten per type (`item`) en bereken de gemiddelde prijs per type.
- Filter producten met `inStock: true`, groepeer ze daarna per `item` en tel per groep het totale aantal (`quantity`).

{{% notice tip %}}
Gebruik `com.mongodb.client.model.Filters`, `com.mongodb.client.model.Updates`, `com.mongodb.client.model.Sorts`, `com.mongodb.client.model.Projections` en `com.mongodb.client.model.Aggregates` voor een overzichtelijker code.
{{% /notice %}}

<br>

###### Deel 2: `Product`-klasse en `ProductRepository`

**Stap 1 — Klasse `Product`**

Maak een klasse `Product` aan met de volgende velden (gebruik Gson-annotaties waar nodig voor `_id`):

| Veld | Type |
|------|------|
| `id` | `ObjectId` |
| `item` | `String` |
| `price` | `double` |
| `quantity` | `int` |
| `date` | `java.util.Date` |
| `inStock` | `boolean` |

Voorzie een constructor, getters en setters.

**Stap 2 — Klasse `ProductRepository`**

Maak een klasse `ProductRepository` aan die bij instantiëren een `MongoCollection<Document>` opent op de collectie `producten` in de database `winkel`. Voeg de volgende methoden toe:

| Methode | Beschrijving |
|---------|-------------|
| `List<Product> findAll()` | Geeft alle producten terug als lijst van `Product`-objecten. |
| `Product findByItem(String item)` | Geeft het eerste product terug met de opgegeven `item`-naam, of `null` als het niet bestaat. |
| `void insert(Product product)` | Voegt een nieuw product toe aan de collectie. |
| `void updatePrice(String item, double newPrice)` | Past de prijs aan van het product met de opgegeven `item`-naam. |
| `void delete(String item)` | Verwijdert het product met de opgegeven `item`-naam. |

Gebruik **Gson** om te serialiseren/deserialiseren tussen `Product`-objecten en MongoDB-documenten (zie het eerdere voorbeeld met `Document.parse(gson.toJson(...))` en `gson.fromJson(doc.toJson(), Product.class)`).

**Stap 3 — Testen in `Main`**

Test alle methoden van `ProductRepository` in de `main`-methode:
1. Voeg een nieuw product toe via `insert()`.
2. Haal het zojuist ingevoegde product op via `findByItem()` en druk het af.
3. Pas de prijs aan via `updatePrice()` en controleer het resultaat met `findByItem()`.
4. Haal alle producten op via `findAll()` en druk ze af.
5. Verwijder het product via `delete()` en controleer met `findAll()` dat het weg is.

### Replicatie

Bij gebruik van MongoDB Atlas is **replicatie** standaard inbegrepen. Dit zorgt voor een hogere beschikbaarheid en betrouwbaarheid. Replicatie in MongoDB wordt gerealiseerd via een replica set, een groep `mongod`-processen die dezelfde dataset onderhouden. Een replica set bevat meerdere data-dragers en optioneel een arbiter. 

Replicatie houdt in dat gegevens automatisch worden gekopieerd van de primaire node naar secundaire nodes. Dit biedt niet alleen fouttolerantie, maar verhoogt ook de leescapaciteit, omdat leesbewerkingen naar secundaire nodes kunnen worden gestuurd. In het geval van een storing van de primaire node, wordt een secundaire node automatisch gepromoveerd tot primaire node via een verkiezingsproces. Dit garandeert dat de database beschikbaar blijft, zelfs bij hardware- of netwerkproblemen.

Meer informatie over replicatie is te vinden in de [MongoDB-documentatie](https://www.mongodb.com/docs/manual/replication/) en in deze [video](https://www.youtube.com/watch?v=QPFlGswpyJY).

### Het concept van Foreign Keys in MongoDb
<!-- TODO: tegen volgend jaar verder uitbreiden: Foreign key:  eg. company_id: db.info.find(…)._id => …find({_id: …find(…).company_id}) -->
In document stores gaan we hier zo min mogelijk van gebruik maken, maar het kan wel geïmplementeerd worden. We gaan daar hier echter niet verder op in en daarvoor kijk je best de videos over dit onderwerp hieronder.

<!-- TODO: Transactions -->

### Interessante videos
- [Algemene tutorial over CMD-commands, Mongo Atlas, Aggregations, MongoDb Compass en VSCode extention](https://www.youtube.com/watch?v=2QQGWYe7IDU)
- [Tutorial met meer complexe queries](https://www.youtube.com/watch?v=ofme2o29ngU)
- [Meer advanced tutorial over Mongo Atlas, auto replication, ACID & Transaction, aggregations, schema-validation, relationships, join, indexes](https://www.youtube.com/watch?v=QPFlGswpyJY)
  - [Praktisch gebruik van Foreign keys in MongoDb](https://www.youtube.com/watch?v=vQnhkuRuVkw)
- [Tutorial over het gebruik van MongoDb in Java inclusief aggregaties](https://www.youtube.com/watch?v=L5ORfm4i350&list=PLdnyVeMcpY7_Q3ms_ykCBgXOeCFGDleS2)
- [Tutorial over de VSCode extensie](https://www.youtube.com/watch?v=MLWlWrRAb4w)
- [Extra video over de installatie van de GUI voor mongoDb (Compass) op Windows](https://www.youtube.com/watch?v=wJRV2hlXpSY&list=PLmCsXDGbJHdg-In6CP3FsRjusanyhzZwZ*)
- [Extra video over Replication](https://www.youtube.com/watch?v=ZGHowQHMOoM)
- [Extra tutorial voor beginners 1](https://www.youtube.com/watch?v=ExcRbA7fy_A&list=PL4cUxeGkcC9h77dJ-QJlwGlZlTd4ecZOA)
- [Extra tutorial voor beginners 2](https://www.youtube.com/watch?v=c2M-rlkkT5o)

### Demo
<!-- TODO volgend jaar: with blogposts: author, title, tekst, categories, date, likes, dislikes, public (true or false) AND users with schema: name, age, contact: {phone: … , email …}, password, pinned post (foreign key or just a copy of the object) -->

<!-- EXSOL -->
**_[Hier](/files/dab-mongo-demo.zip) vind je een zipfolder met een oplossing voor MongoDb demo Student_**

<!-- TODO: volgend jaar opsplitsen in meerdere pagina's -->