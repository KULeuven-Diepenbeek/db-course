Databases: <br/>NoSQL Deel II
=======================

<img src="/db-course/img/kul.svg" style="height: 80px;" />
<img src="/db-course/img/uhasselt.svg" style="width: 165px; height: 80px;"/>


---

## 1. NoSQL: Herinner je...

- key/value stores
- document stores

___

<img src="/db-course/slides/img/nosql-types.jpg" />

Bron: improgrammer.net


---

## 2. NoSQL querying: MapReduce

Wanneer `Mango` niet volstaat...

Hoe doe ik dit in NoSQL?

```
SELECT title, SUM(ECTS) FROM courses
WHERE country = "Belgium"
GROUP BY title
```

___

### 2.1 Wat is Map?

Weet je nog, BES/Python?

```python
range = [1, 2, 3, 4]
result = list(map(lambda x: x * 2, range))
print(result) # [2, 4, 6, 8]
```

Zelfde concept in JS:

```javascript
const range = [1, 2, 3, 4]
console.log(range.map(function(x) {
    return x * 2
})) // [2, 4, 6, 8]
```

**Voor elk document**, process & transform.

___

### 2.1.1 Map voorbeeld

Zoek leerlingen ouder dan 20 en geef hun naam terug:

```javascript
function(doc) {
    if(doc.age > 20) {
        emit(doc._id, doc.name);
    }
}
```

- `emit(key, value)`
- Merk op: dit is ook een **filter**:

```javascript
[{age: 11, name: 'jos'}, {age: 21, name: 'jef'}].filter(function(student) {
    return student.age > 20
}).map(function(student) {
    return student.name
})
```

**Functioneel** programmeren. 

___

### 2.2 Wat is Reduce?

**Reduceer** lijst van x elementen naar één aggregaat:

```javascript
[1, 2, 3, 4].reduce(function(a, b) {
    return a + b
}) // prints 10 
```

NoSQL alternatief voor `GROUP BY`/`COUNT`/...

```javascript
function(keys, values, rereduce) {
    return values.reduce(function(a, b) {
        return a + b
    })
}
```

Waar komen `keys` en `values` vandaan? 

___

### 2.2.2 Map + Reduce combo

Bereken gemiddelde leeftijd oudere studenten.

```javascript
function map(doc) {
    if(doc.age > 20) {
        emit(doc._id, doc.age);
    }
}
function reduce(keys, values, rereduce) {
    return sum(values) / values.length;
}
```

Let op: `reduce()` schiet (misschien) in actie als `map()` nog bezig is.

___

### Demo in CouchDB

![](/db-course/img/couchview.jpg)

---

## 3. Replication (PouchDB)

![](/db-course/img/pouchdb.jpg)

[https://pouchdb.com](https://pouchdb.com)

___

### 3.1 Wat is PouchDB? 

"The database _that syncs_":

> PouchDB is an open-source JavaScript database inspired by Apache CouchDB that is designed to run well within the browser.

```javascript
var db = new PouchDB('dbname');
db.replicate.to('http://example.com/mydb');
```

___

### 3.2 PouchDB, CouchDB, huh???

- Pouch: **JavaScript** client. (Node, Web)
- Beiden: Mango, MapReduce, Replication, ...
- Beiden: clustering, conflict management, compacting, ...

=> Pouch is de (_easy-to-setup_) Couch van het web.

Kleiner: [Who's using PouchDB?](https://pouchdb.com/users.html)

___

### 3.3 Pouch Replication types

1. **Unidirectional**: 1-way traffic master/slave.
2. **Bidirectional**: 2-way traffic.
3. **Live/continuous**: 2-way "live" traffic.

![](/db-course/slides/img/offline_replication.gif)

https://pouchdb.com/guides/replication.html

___

### 3.4 Hoe werkt dit intern?

`_rev`:

![](/db-course/slides/img/revs.jpg)

- Elke change aan elk document (`3-`...) wordt bijgehouden
- REST: [GET list of revisions](https://docs.couchdb.org/en/stable/api/document/common.html#getting-a-list-of-revisions)

http://127.0.0.1:5984/db/key

`?revs=true` of `?revs_info=true`


---

## 4. Column based DBs (Cassandra)

p331

- Grootste nadelen van **row-based** dbs bij queryen?
- **column-based**: "indexen in rijen"

```
id genre title price
1 Fantasy book bla 10
2 Fantasy another title 20
3 horror wow-book 10
```

```
genre: fantasy:1,2     horror: 3
title: book bla:1, another title:2 wow-book: 3
price: 10:1,3    20:2
```

___

### 4.1. Column based - waarom?

[https://cassandra.apache.org](https://cassandra.apache.org)

> Manage massive amounts of data, fast, without losing sleep

- Super-linked; waarbij col data > row data (vertical slices: "**business intelligence**" of BI)
- In-memory buffers
- Tracking & monitoring

In **Cassandra**: Cluster << Data Center << Rack << Server << Node

---

## 5 Case Studies

### 5.1 eBay (2012)

![](/db-course/slides/img/ebay.png)

https://www.benl.ebay.be

https://www.slideshare.net/jaykumarpatel/cassandra-at-ebay-13920376

___

### 5.2 Spotify (2015)

![](/db-course/slides/img/spotify.png)

https://spotify.com

https://engineering.atspotify.com/2015/01/09/personalization-at-spotify-using-cassandra/

___

### 5.3 Uber (2016)

![](/db-course/slides/img/uber.png)

https://uber.com

http://highscalability.com/blog/2016/9/28/how-uber-manages-a-million-writes-per-second-using-mesos-and.html

---

<!-- .slide: data-background="#008eb3" -->
## Knowledge.commit()

Transmission complete!
