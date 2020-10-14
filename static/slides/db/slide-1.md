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

### 2.1 NoSQL/RDMS?

Wat denk jij? NoSQL of RDMS? of ... ?

[https://www.vdab.be/vindeenjob/vacatures](https://www.vdab.be/vindeenjob/vacatures?sort=standaard)

[https://www.immoweb.be/nl](https://www.immoweb.be/nl)

[https://twitter.com](https://twitter.com) ([hint](https://www.8bitmen.com/what-database-does-twitter-use-a-deep-dive/))

[https://people.cs.kuleuven.be/~wouter.groeneveld/courses/](https://people.cs.kuleuven.be/~wouter.groeneveld/courses/)

---

### 3. Key/Value stores (Memcached)


---

### 4. Document stores (Mongo/CouchDB)

---

<!-- .slide: data-background="#008eb3" -->
## Knowledge.commit()

Transmission complete!
