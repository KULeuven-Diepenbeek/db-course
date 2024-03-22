---
title: 2. JDBC en JDBI
draft: false
---


## 1.1 Java Database Connectivity (JDBC)

### 1.1.1 Hoe verbind ik Java met de DB?

[JDBC](https://www.tutorialspoint.com/jdbc/index.htm) is een interface in de JDK die ons in staat stelt om een connectie te openen naar een database. **JDBC is een API**: een abstracitelaag of een _protocol_. Dit betekent dat we met JDBC kunnen verbinden naar eender welke server van eender welke flavor: een Oracle SQL, MSSQL, of SQLite database. De database _vendor_ wordt verborgen achter de JDBC laag. Voor deze oefeningen beperken we ons tot SQLite.

{{% notice info %}}
Update van de cursustekst en oefeningen zijn onderweg, tegen de volgende les van 29/03 zullen we alles overschakelen naar MYSQL dat jullie eerder al gebruikten met XAMP en phpmyadmin. Enige verschillen met MYSQL zijn:
| MYSQL   | SQLITE  |
|---|---|
| AUTO_INCREMENT | AUTOINCREMENT |
| BOOLEAN  | BOOL  |
{{% /notice %}}

Voor elke database moet er dus een vendor-specifieke driver als dependency worden toegevoegd. In het geval van SQLite is dit de [sqlite-jdbc](https://github.com/xerial/sqlite-jdbc) driver, de `sqlite-jdbc` package. JDBC zelf leeft in `java.sql` en is een integraal onderdeel van de JDK: dit moeten we dus niet apart oplijsten als dependency of downloaden.

{{<mermaid align="left">}}
graph LR;
    Java[Java]
    JDBC[JDBC]
    SQLITE[SQLite-JDBC]
    DB[(SQLite Database)]
    subgraph Java space
    subgraph JDK
    Java -.-> JDBC
    end
    JDBC --> SQLITE
    end
    subgraph DB space
    SQLITE --> DB
    end
{{< /mermaid >}}

De `sqlite-jdbc` package zorgt voor de brug tussen onze Java applicatie en de database, maar we spreken die aan via JDBC. 

Enkele belangrijke statements:

1. Een connectie naar een database vastleggen: `var connection = DriverManager.getConnection("jdbc:sqlite:mydb.db");`
2. Een `SELECT` query uitvoeren: `var s = connection.createStatement(); var result = s.executeQuery("..."); var cell = result.getString("column");`
3. Een `INSERT`/`UPDATE`/... query uitvoeren (die de structuur of inhoud van de database **wijzigt**): `var s = connection.createStatement(); s.executeUpdate("...");`

Het volgende voorbeeld opent een verbinding naar een DB, maakt een tabel aan, voegt een record toe, en telt het aantal records:

<div class="devselect">

```java
private Connection connection;
public void createDb() throws SQLException {
    connection = DriverManager.getConnection("jdbc:sqlite:mydb.db");
    var s = connection.createStatement();
    s.executeUpdate("CREATE TABLE mijntabel(nr INT); INSERT INTO mijntabel(nr) VALUES(1);")
    s.close();
}
public void verifyDbContents() throws SQLException {
    var s = connection.createStatement();
    var result = s.executeQuery("SELECT COUNT(*) FROM mijntabel;");
    var count = result.getInt(0);
    s.close();

    assert count == 1;
}
```

</div>

**Gradle** dependency: laatste versie van [sqlite-jdbc in mvnrepository.com](https://mvnrepository.com/artifact/org.xerial/sqlite-jdbc).


Merk op dat `SQLException` een **checked exception** is die je constant moet meespelen in de method signature of expliciet moet opvangen. Het probleem van een `try { } catch { } finally { }` block is dat in de finally je ook geen `close()` kan uitvoeren zonder opnieuw een `try` block te openen... Inception!

Het `connection.close()` statement moet er voor zorgen dat voor elke request de connection netjes wordt afgesloten. Een database heeft meestal een **connection pool** van x aantel beschikbare connections, bijvoorbeeld 5. Als een connection per request niet wordt gesloten, heeft de voglende bezoeker van onze website geen enkele kans om zijn zoekquery te lanceren, omdat de database dan zegt dat alle connecties zijn opgebruikt!

Merk op dat de String `jdbc:sqlite:mydb.db` een **lokale SQLite database file** aanmaakt op het huidig relatief pad, zodat je met SQLite Explorer data kan inspecteren. Deze file wordt herbruikt: indien je een tabel aanmaakt de eerste keer, gaat dit de tweede keer crashen met _table already exists_. Houd hier dus rekening mee (e.v.t. met `IF NOT EXISTS`). Je kan ook een **in-memory database** aanmaken, die volledig in RAM leeft en bij elke opstart opnieuw wordt aangemaakt, met de String `jdbc:sqlite:memory`.

Werk je met een andere database maar heb je geen idee hoe die speciale connection string te vormen? Geen probleem, daarvoor dient [https://www.connectionstrings.com/](https://www.connectionstrings.com/). Bijvoorbeeld, een connectie naar de Microsoft Azure cloud kan met de volgende syntax:

```
Server=tcp:myserver.database.windows.net,1433;Database=myDataBase;User ID=mylogin@myserver;Password=myPassword;Trusted_Connection=False;Encrypt=True;
```

Het is de connection string die bepaalt welke dependency binding gebruikt wordt! Dit noemen we _late binding_: er is **geen expliciete referentie** naar iets van SQLite in de Java code; we werken _enkel_ met JDBC zelf. Als je de vendor driver vergeet toe te voegen als Gradle dependency gebeurt er dit:

```
Exception in thread "main" java.sql.SQLException: No suitable driver found for jdbc:sqlite:mydb.db
    at java.sql/java.sql.DriverManager.getConnection(DriverManager.java:702)
    at java.sql/java.sql.DriverManager.getConnection(DriverManager.java:251)
    at Demo.main(Demo.java:8)
```

{{% notice note %}}
In-memory databases (ConStr. `jdbc:sqlite:memory`), die met een lege database vertrekken, en constant `CREATE TABLE()` statements issuen, vervuilen je broncode. Als je veel SQL moet uitvoeren is het beter om dit in een `.sql` bestand te bewaren in `src/main/resources` en eenmalig in te lezen als SQL met `new String(Files.readAllBytes(Paths.g));`, om te kunnen uitvoeren via `statement.executeUpdate()`. Zie het [jdbc-repo-start](https://github.com/KULeuven-Diepenbeek/db-course/tree/main/examples/java/jdbc-repo-start) project op GitHub als voorbeeld.
{{% /notice %}}

### 1.1.2 Queries/Objecten in JDBC


Stel dat we dezelfde studenten willen inladen in een `Student` klasse instantie: van de `TABLE STUDENT` naar de `class Student`. In geval van JDBC is dat veel handwerk: 

1. Maak een verbinding met de database. 
2. Voer de `SELECT` statements uit. 
3. Loop door de `ResultSet` en maak een nieuwe `Student` instantie aan. Vang alle mogelijke fouten zelf op: wat met lege kolommen, `null`? Wat met `INTEGER` kolommen die je wilt mappen op een `String` property? 

Om van de huidige resultatenrij naar de volgende te springen in `ResultSet` gebruikt men de methode `next()` in een typisch `while()` formaat:

<div class="devselect">

```java
var result = statement.executeQuery("SELECT * FROM iets");
while(result.next()) {
    var eenString = result.getString("kolomnaam");
    // doe iets!
}
```
</div>

Zie ook [ResultSet Oracle Javadoc](https://docs.oracle.com/javase/7/docs/api/java/sql/ResultSet.html). 

Aangezien we reeds hebben kennis gemaakt met de (beperkte) API, schakelen we onmiddellijk over naar de oefeningen:

### 1.1.3 Oefeningen

Gebruik voor de oefeningen de `student` tabel statements uit [RDBMS Transacties - Failures & Rollbacks](/transacties/failures-rollbacks/).

1. Maak (én test!) een klasse `StudentRepository` die de volgende methode implementeert. Zoals je ziet is het de bedoeling dat de JDBC `Connection` instance elders wordt aangemaakt, bijvoorbeeld in een **aparte** `ConnectionManager` klasse. 

<div class="devselect">

```java
public class StudentRepository {
        public StudentRepository(Connection connection);
        public List<Student> getStudentsByName(String name);
}
```
</div>

2. Hoe zou je bovenstaande `StudentRepository` unit (integratie) testen, zonder de "productie database" op te vullen met testdata? (Hint: kijk naar het constructor argument). Hoe kan je `getStudentsByName()` testen zonder de volgende oefening afgewerkt te hebben, die nieuwe studenten bewaren pas mogelijk maakt?
3. Breid dit uit met `saveNewStudent(Student)`.
4. Breid dit uit met `updateStudent(Student)`. Wat moet je doen als deze student nog niet in de database zit? Welke gegevens update je wel en welke niet? 
5. Merk op dat elke keer als je je project opstart je geen `CREATE TABLE student` kan uitvoeren als je een file-based SQLite bestand hanteert: eens de tabel is aangemaakt geeft een nieuwe create foutmeldingen. `DROP TABLE IF EXISTS student;` lost dit op, maar daardoor ben je ook altijd je data kwijt. Hoe los je dit probleem op?
6. Stel dat een `Student` is ingeschreven in een `Cursus` met properties `naam` (vb. "databases") en `ects` (vb. 4). 
    - Maak een `CursusRepository` om nieuwe cursussen te bewaren.
    - Hoe link je de `Student` klasse met de `Cursus` klasse? wat verandert er in de query van `getStudentsByName()`?

**Tips**: 

- `executeUpdate()` van een `Statement` is erg omslachtig als je een string moet stamenstellen die een `INSERT` query voorstelt (haakjes, enkele quotes, ...). Wat meer is, als de input van een UI komt, kan dit gehacked worden, door zelf de quote te sluiten in de string. Dit noemt men **SQL Injection**, en om dat te vermijden gebruik je in JDBC de `prepareStatement()` methode. Zie [JDBC Basics: Prepared Statements](https://docs.oracle.com/javase/tutorial/jdbc/basics/prepared.html). De String die je meegeeft bevat in de plaats van parameters een vraagteken: `INSERT INTO STUDENT(bla, bla) VALUES(?, ?)`. Die parameters vul je daarna aan met `preparedStatement.setString()` of `setInt()`. Op die manier is de code zowel _netjes_ als _injectie-vrij_!
- Als je data wenst op te halen dat is verspreid over verschillende tabellen, is de kans groot dat een `JOIN` SQL statement nodig is. Probeer eerst de query te schrijven in de _SQLite DB Browser_ tool. De Java objecten opvullen is de laatste taak.


