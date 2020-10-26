Databases: <br/>NoSQL Deel III
=======================

<img src="/db-course/img/kul.svg" style="height: 80px;" />
<img src="/db-course/img/uhasselt.svg" style="width: 165px; height: 80px;"/>

---

## 1. Graph-based NoSQL DBs (Neo4J)

p. 333

<img src="/db-course/slides/img/neo4j.jpg" style="width: 75%" />

___

### 1.1 Waarom Graph-based?

- Terug **meer** ipv minder relaties? (RDMS vs NoSQL)
- Query **relaties** in plaats van data zelf. (relatie = data)
- Géén `JOIN` statements nodig.

Typische toepassing: _social graphs_.

___

### 1.2 Queryen in Neo4J

Geen **SQL**:

```
SELECT book, title FROM book, author, books_authors
WHERE author.id = books_authors.author_id
AND book.id = books_authors.book_id
AND author.name = "De Jos"
```

Maar **Cypher**: 

```
MATCH (b:Book) <- [ :WRITTEN_BY]-(a:Author)
WHERE a.name = "De Jos"
RETURN b.title
```

Op basis van `WRITTEN_BY` relatie eigenschap dus!

---

## 2. Meer Use Cases

### 2.1 U.S. Army (2019)

Waarom Neo4J? **Logistiek**

![](/db-course/slides/img/usarmy.png)

https://go.neo4j.com/rs/710-RRC-335/images/Neo4j-case-study-US-army-EN-US.pdf

___

### 2.2 Argenta (2014)

Backend applicatie voor bankiers

![](/db-course/slides/img/argenta.jpg)

https://argenta.be

<div class="mermaid" align="center">
graph LR;
    A[Legacy DBs] --> B[ServerApp 1]
    A --> C[ServerApp 2]
    B --> D[Argenta ClientApp]
</div>


---

<!-- .slide: data-background="#008eb3" -->
## Knowledge.commit()

Transmission complete!
