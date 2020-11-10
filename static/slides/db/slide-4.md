Databases: <br/>Transacties
=======================

<img src="/db-course/img/kul.svg" style="height: 80px;" />
<img src="/db-course/img/uhasselt.svg" style="width: 165px; height: 80px;"/>


---

## **Zonder transacties**

<!-- .slide: data-background-image="/db-course/slides/img/chaos.jpg" -->

___

## **Met transactites**

<!-- .slide: data-background-image="/db-course/slides/img/order.jpg" -->

___

## 1. Wat is een transactie? (p431)

- Set van operaties voor 1 gebruiker => **1 "unit of work"**
- Ofwel _alle_ ops (succeed), ofwel _niets_ (fail).
- +1 transactie/moment: **concurrency**!

___

### 1.1 Waarvoor dienen transacties?

Afdwingen **ACID** regels:

1. In geval van failures
2. In geval van concurrency

---

## 2. Transactie lifecycle

<img src="/db-course/slides/img/transactions.png" style="width: 80%" />

[bron](https://gigaom2.files.wordpress.com/2014/07/tephra-transaction-life-cycle.png)

___

### 2.1 "Aflijnen" van transacties

In SQL:

```sql
BEGIN TRANSACTION;
UPDATE student SET name = "Joske" WHERE name = "Jos";
UPDATE student_score SET score = 10 where student_id = 124;
INSERT INTO logs(student_id, log) VALUES(124, 'naam gewijzigd');
COMMIT;
```

Kan crashen op SQL client/server niveau:

- Power outage (**system failure**, p436)
- HDD Crash (**media failure**, p436)
- ongeldige statements (**transaction failure**)
- ...

___

### 2.1 "Aflijnen" van transacties

In Java Code:

```java
public void doStuffInTransaction() {
    sessionManager.beginTransaction();
    try {
        // -- DO WORK
        repository.updateStudentName("Jos", "Joske");
        repository.updateStudentScore(124, 10); // BOEM
        logger.insertLogRecord(124, "naam gewijzigd");
        // -- DONE, commit stuff
        sessionManager.commit();
    } catch(Exception ex) {
        sessionManager.rollback();
        throw ex;
    } finally {
        sessionManager.close();
    }
}
```

Omslachtig?

___

Altijd dezelfde `try { ... }` code => **@Transactional** wrapper

```java
@Transactional
public void doStuffInTransaction() {
    repository.updateStudentName("Jos", "Joske");
    repository.updateStudentScore(124, 10); // BOEM
    logger.insertLogRecord(124, "naam gewijzigd");
}
```

Kan crashen op Java (=client) niveau:

- `updateStudentScore()` throws `RuntimeException` (**transaction failure**)
- Wat met `updateStudentName()`? Is Jos ondertussen Joske?

___

### 2.2 Transactie states

1. **begin_trans**
2. **end_trans**
3. **committed**/**aborted** => **rollback** nodig?

??

- Wie houdt "oude data" bij in geval van rollback?
- Wie beslist welke transacties uit te voeren op welk moment?

___

### 2.3 Transaction management

Fig 14.1 p 434

Veel "te managen":

1. Transaction management (high-level)
2. Schedule management
3. Stored data management
4. Recovery management
5. Database buffer management
6. ...

___

### 2.4 Logging

Het belang van de **Logfile** voor een transactie:

- Metadata, ids, ...
- **before**/**after** images van alle records involved!!
- Huidige transactie state

---

## 3. Transacties & Concurrency

<img src="/db-course/img/concurrency.png" style="width: 35%"/>

___

### 3.1 Typische Concurrency-related problemen

14.4.1

- **Lost Updates**: `UPDATE` (T1) terug overschreven (T2)
- **Dirty Reads**: rollbacked `UPDATE` (T1) te snel gezien door T2
- **Phantom Reads**: related, `INSERT`/`DELETE` (T1) on +1 rows
- ... 

Oplossing? Hint: BESC, process CPU scheduling

 => **Scheduling** van transacties!

___

### 3.2 Serial scheduling?

1. Eerst alles van `T1`
2. Dan alles van `T2`
3. ...

<img src="/db-course/img/serialtrans.png" style="width: 50%" />

([bron](https://www.quora.com/What-is-serializability-What-are-its-types))

**Te traag**! Hoe non-serial schedulers toch concurrent-safe maken?

___

### 3.3 Locking

Oplossing: **Locking**; optimistisch/pessimistisch.

- **Pessimistic** locking = "Alles gaat mislopen, schroef maar dicht". Serial scheduling dus. 
- **Optimistic** locking = "Het zijn uitzonderingen". Sneller, maar met kans op fouten.

___

### 3.4 Locking Granularity

- **exclusive** (`WRITE`) lock VS **shared** (`READ`) lock op DB "object"
- +1 lock kan `READ` lock op zelfde data item houden
- Locks op welk DB "object" niveau? 
    - **row** locks, op record (tuple-level).
    - **page** locks: _delen_ van tabel, as-stored in files.
    - **table** locks: _complete_ tabel lock.
    - **database** locks (bvb file lock SQLite)

Verantwoordelijke: DB _Lock Manager_, data in speciale lock table. 

=> Maximizing concurrency: row locks > DB locks!

___

<img src="/db-course/img/dblock.png" style="width: 70%" />

Oeps? Bron: [A beginners guide to DB deadlocks](https://vladmihalcea.com/database-deadlock/)

=> Oplossing? timeouts/priority.


___

### 3.5 In de praktijk: DBMS Isolation Levels

Van _laag_ (short-term) naar _hoog_ (long-term):

1. **Read uncommitted**. Zwakste level. Read-only transactions.
2. **Read committed**. longterm write/shortterm read. <br/>Fixes lost update/dirty reads.
3. **Repeatable read**. longterm write/read. 
4. **Serializable**. Sterkste level. <br/> Fixes phantom reads.

Zie 14.4.5.5 en tabel.

---

## 4. NoSQL en Transacties? 

Véél minder strict: **ACID** vs **BASE**

Couch/Pouch: HTTP API calls:

- Gegarandeerde retval, maar niet altijd verwachtte
- Locks op client niveau, zelf aan "connection pooling" doen
- Retry mechanismes inbouwen
- ...

___

### 4.1 Transacties over +1 DB?

Mogelijk! - **distributed transactions**.

- Over 1+ klassieke DBMS systemen
- Over 1 SQL en 1 NoSQL database (complex)
- Over SQL en messaging systemen
- ... 

Implementatie vb: **XA**. (Valt buiten cursus)

---

<!-- .slide: data-background="#008eb3" -->
## Knowledge.commit()

Transmission complete!
