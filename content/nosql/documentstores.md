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

### Belangrijke Commando's

Hieronder een overzicht van veelgebruikte commando's in MongoDB:

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

db.<collection name>.find()[0]._id
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

Meer informatie over query- en updateoperators is te vinden in de [MongoDB-documentatie](https://www.mongodb.com/docs/manual/reference/operator/).

#### Aggregaties

MongoDB ondersteunt aggregaties, waarmee je complexe bewerkingen op gegevens kunt uitvoeren. Aggregaties zijn een krachtige manier om gegevens te transformeren en te analyseren. Ze worden vaak gebruikt om samenvattingen, statistieken of andere berekeningen uit te voeren op grote datasets. Aggregaties worden uitgevoerd met behulp van een aggregatie-pijplijn, die bestaat uit een reeks stappen die gegevens verwerken en transformeren.

Enkele veelgebruikte aggregatiestappen zijn:
- `$match`: Filteren van gegevens op basis van criteria.
- `$sort`: Sorteren van gegevens op een specifieke volgorde.
- `$project`: Selecteren en transformeren van specifieke velden in documenten.
- `$group`: Groeperen van gegevens en uitvoeren van berekeningen zoals som, gemiddelde of telling.
- `$limit`: Beperken van het aantal resultaten.
- `$skip`: Overslaan van een bepaald aantal resultaten.

Een voorbeeld van een aggregatie in Java en in de console.

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

#### Schema-validatie

Schema-validatie in MongoDB biedt een manier om de structuur van documenten in een collectie te definiëren en te valideren. Dit helpt om de integriteit van gegevens te waarborgen en fouten te voorkomen. Met schema-validatie kun je regels instellen voor de velden in een document, zoals het type, de vereiste velden en de toegestane waarden.

**Hoe schema-validatie aanbrengen?**<br/>
Schema-validatie wordt ingesteld op het niveau van een collectie. Bij het aanmaken van een collectie kun je een validatieregelspecificatie opgeven met behulp van het `validator`-veld. Dit veld bevat een query die de validatieregels definieert.

**Syntax voor schema-validatie in Java**<br/>
Hier is een voorbeeld van hoe je schema-validatie kunt instellen in Java:
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
                .append("required", List.of("naam", "leeftijd"))
                .append("properties", new Document("naam", new Document("bsonType", "string")
                        .append("description", "Naam is verplicht en moet een string zijn."))
                    .append("leeftijd", new Document("bsonType", "int")
                        .append("minimum", 0)
                        .append("description", "Leeftijd is verplicht en moet een positief geheel getal zijn.")));

            database.createCollection("mijnCollectie", new Document("validator", new Document("$jsonSchema", schema)));
        }
    }
}
```

**Syntax voor schema-validatie in de console (mongosh)**
```javascript
use mijnDatabase;
db.createCollection("mijnCollectie", {
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: ["naam", "leeftijd"],
      properties: {
        naam: {
          bsonType: "string",
          description: "Naam is verplicht en moet een string zijn."
        },
        leeftijd: {
          bsonType: "int",
          minimum: 0,
          description: "Leeftijd is verplicht en moet een positief geheel getal zijn."
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
  collMod: "mijnCollectie",
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: ["naam", "leeftijd"],
      properties: {
        naam: {
          bsonType: "string"
        },
        leeftijd: {
          bsonType: "int",
          minimum: 0
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

Met deze aanpak kun je schema-validatie flexibel toepassen en aanpassen aan de behoeften van je applicatie.

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
            MongoCollection<Document> collectie = database.getCollection("mijnCollectie");

            // Create: Document toevoegen
            Document nieuwDocument = new Document("naam", "John Doe")
                    .append("leeftijd", 30)
                    .append("beroep", "Software Engineer");
            collectie.insertOne(nieuwDocument);

            // Read: Documenten opvragen
            for (Document doc : collectie.find()) {
                System.out.println(doc.toJson());
            }

            // Update: Document bijwerken
            collectie.updateOne(new Document("naam", "John Doe"),
                    new Document("$set", new Document("leeftijd", 31)));

            // Delete: Document verwijderen
            collectie.deleteOne(new Document("naam", "John Doe"));
        }
    }
}
```
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
<!-- TODO: add correct file
**_[Hier](/files/) vind je een zipfolder met een oplossing voor MongoDb demo Student_** -->

<!-- TODO: volgend jaar opsplitsen in meerdere pagina's -->