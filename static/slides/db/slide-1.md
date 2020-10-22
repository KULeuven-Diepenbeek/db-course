Databases: <br/>NoSQL Intro
=======================

<img src="/db-course/img/kul.svg" style="height: 80px;" />
<img src="/db-course/img/uhasselt.svg" style="width: 165px; height: 80px;"/>


---

## 1. Het probleem met RDMS

Data _validity_? **ACID** (ch14.5):

- **Atomicity**; operaties = 1 blok
- **Consistency**; data = _altijd_ valid!
- **Isolation**; concurrency moet kunnen
- **Durability**; commit is commit, ook bij crashes

Hoe schalen?

___

### 1.1 Vertical scaling

<img src="/db-course/slides/img/moar.jpg" style="width: 60%" />

- "scaling up": moar powah! **storage**, **CPU** cycles++, ed
- Hardware-limitations...


___

### 1.2 Horizontal scaling

<img src="/db-course/slides/img/cluster.jpg" style="width: 50%" />

- "scaling out": databases in **clusters**
- Data consistentie _vermoeilijkt_ dit proces! p.301

___

### 1.3 Oplossing: NoSQL

- **Géén** relationele data. 
- **Géén** (onmiddellijke) consistentie: _eventual consistency_.
- Zeer goed **scalable**.

___

### 1.4 RDMS VS NoSQL p303

| Eigenschap     | Relationeel | NoSQL |
|----------------|-------------|-------|
| **Data paradigma** | relationeel | key/value, doc-based, ... |
| **Distributie** |  Single-node | Distributed |
| **Scalability** | Vertical | Horizontal, replication |
| **Structuur** | Schema-based | Flexible |
| **Querty taal** | SQL | Specialized (JavaScript) |
| **Transacties** | ACID | BASE |
| **Features** | views/procs/... | basic API |
| **Data vol.** | "normal" | "huge amounts" |

Big Data?

---

## 2. Wanneer NoSQL?

Niet altijd de "**beste**" oplossing. 

- Bevat data veel/weinig relaties? 
- Komt er enorm veel data/sec. binnen?
- Replication vereisten? 
- Scripting mogelijkheden?
- Bestaande kennis in bedrijf? 
- ... 

___

### 2.1 DB-Engines "ranking"

https://db-engines.com/en/ranking_trend

[![](/db-course/slides/img/ranking.jpg)](https://db-engines.com/en/ranking_trend)

___

### 2.2 NoSQL/RDMS?

Wat denk jij? NoSQL of RDMS? of ... ?

[https://www.vdab.be/vindeenjob/vacatures](https://www.vdab.be/vindeenjob/vacatures?sort=standaard)

[https://www.immoweb.be/nl](https://www.immoweb.be/nl)

[https://twitter.com](https://twitter.com) ([hint](https://www.8bitmen.com/what-database-does-twitter-use-a-deep-dive/))

[https://people.cs.kuleuven.be/~wouter.groeneveld/courses/](https://people.cs.kuleuven.be/~wouter.groeneveld/courses/)

[https://uber.com](https://uber.com) ([hint](http://highscalability.com/blog/2016/9/28/how-uber-manages-a-million-writes-per-second-using-mesos-and.html))

---

<img src="/db-course/slides/img/nosql-types.jpg" />

Bron: improgrammer.net

---

## 3. Key/Value stores (Memcached)

```java
Map<String, Integer> leeftijden = new HashMap<>();
leeftijden.put("Jos", 20);
leeftijden.put("Jaqueline", 28);
leeftijden.put("Lolbroek", 66);
```

- Belang van **hash** functie. p.305: bewaard in Hash/Key tabel
- Hoe **horizontaal** scalen? 

___

### 3.1 Memcache**d** (1)

memory-driven **distributed** hash table. 

- Oplossing voor cachen van traditionele SQL!
- Later: ook peristente hashtable mogelijk

Dus: mix-and-match

___

### 3.1 Memcache**d** (2)

```java
var memClient = new MemcachedClient(AddrUtil
    .getAddresses(["server1:port", "server2:port"]));
memClient.add("Jos", 0, 20); // expiration ZERO
memClient.add("Jaqcuieline", 28); 
```

- `.add()`, `.set()`, `.replace()`, ... p.307
- Probleem: **client** moet weten naar welke servers te pushen

<div class="mermaid" align="center">
graph LR;
    A[memClient] --> B[server1]
    A --> C[server2]
</div>

___

### Key/value request-coords

- Oplossing: **request coordination** & **membership protocol**
- Servers kennen elkaar (uiteindelijk)

<div class="mermaid" align="center">
graph LR;
    A[memClient] --> B[server1]
    B --> C[server2]
    C --> B
</div>

- Ringvormig netwerk: **consistent hashing** 

---

## 3.2 Consistent hashing

OpenStack Swift: [https://docs.openstack.org/swift/](https://docs.openstack.org/swift/)

> Swift is a highly available, distributed, eventually consistent object/blob store. Organizations can use Swift to store lots of data efficiently, safely, and cheaply.

Swift [Consisteny Analsysis](https://julien.danjou.info/openstack-swift-consistency-analysis/) (p.310)

https://julien.danjou.info/openstack-swift-consistency-analysis/

___

### Ring-partitioning

<img src="/db-course/slides/img/swift.png" style="width: 50%" />

Pre-req: _consistent hashing_.

- Zo weinig mogelijk **data moving** bij adding/removal node
- Data **replication**: nodes kennen elkaar
- Probleem: distributed system kan _nooit_ én consistent, én available, én partition tolerant zijn. 

___

### Oplossing: BASE

- **Basically Available**; elk request ontvangt een respons, `200`/`4`/`50x`
- **Soft state**; state kan wijzigen, ook zonder input!
- **Eventually consistent**; "kan" op bepaald moment zo zijn.

_Write_ Request: onmiddellijk `201`, niet alle nodes updated?

_Read_ Request: soms out-of-date?

=> In de praktijk: vaak eerder ACID-like. 

---

## 4. Document stores (Mongo/CouchDB)

Zelfde als key/value stores: key/**data blob**

```
jos -> (124356, 'Jos Klakmans', 18)
var jos = { _id: 124356, name: 'Jos Klakmans', age: 18 }
```

1. **Collecties**: Studenten, Vacatures, Boeken, ... => _optioneel!_
2. **Documents**: (meestal JSON)-structured vector data

___

### 4.1 Eigenschappen van doc. stores (1)

**dynamisch**: (_geen_ schema)

`{ name: 'Jos' }` -> `{ name: 'Jos', well-behaved: true }`

Hoezo, `INSERT INTO STUDENT(name) VALUES ("Jos")`?

___

### 4.1 Eigenschappen van doc. stores (2)

**hashing**: nog steeds. "primary key"

`{ name: 'Jos' }` -> `{ name: 'Jos', _id: 23235435 }`

- Default behaviour in Mongo ea. Instelbaar. 
- Data retrieval **Snelheid** in miljarden records blijft belangrijk
    + indexes!

---

### 4.2 Query's? (1)

Géén SQL. Client-based API (Java/JS/...) p.317

<img src="/db-course/slides/img/mongodb.png" style="position: absolute; z-index: 99; right: 1; width: 30%" />

```java
var client = new MongoClient();
var db = client.getDatabase("db");
// ...
var results = db.getCollection("students").find(
    and(
        eq("name", "jaakmans"),
        eq("well-behaved", true)
    ));
for(Document doc in results) {
    System.out.println(doc.get("_id") + " name: " + doc.get("name"));
}
```

Java, CouchDB API

___

### 4.2 Query's? (2)

<img src="/db-course/slides/img/couchdb.png" style="position: absolute; z-index: 99; right: 1; width: 30%" />

```javascript
db.find({
  selector: {name: 'jaakmans'},
  fields: ['_id', 'name', 'well-behaved'],
  // sort: ['name']  - aha
}).then((result) => {
  result.docs.forEach(doc => {
    console.log(doc._id + " name: " + doc.name);
  })
}).catch((err) => {
  console.log(err);
});
```

JavaScript, PouchDB API

---

### 4.3 Complex querying (MapReduce)

Volgend hoorcollege!

---

<!-- .slide: data-background="#008eb3" -->
## Knowledge.commit()

Transmission complete!
