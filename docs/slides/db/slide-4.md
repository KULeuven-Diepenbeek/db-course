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


