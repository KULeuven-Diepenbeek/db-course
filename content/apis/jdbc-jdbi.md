---
title: 2. JDBC en JDBI
---


## 1.1 Java Database Connectivity (JDBC)

### 1.1.1 Hoe verbind ik Java met de DB?

[JDBC](https://www.tutorialspoint.com/jdbc/index.htm) is een interface in de JDK die ons in staat stelt om een connectie te openen naar een database. **JDBC is een API**: een abstracitelaag of een _protocol_. Dit betekent dat we met JDBC kunnen verbinden naar eender welke server van eender welke flavor: een Oracle SQL, MSSQL, of SQLite database. De database _vendor_ wordt verborgen achter de JDBC laag. Voor deze oefeningen beperken we ons tot SQLite.

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

```kt
private lateinit connection: Connection
fun createDb() {
    connection = DriverManager.getConnection("jdbc:sqlite:mydb.db")
    // note that this requires "stdlib-jdk7" as kotlin runtime dependency
    connection.createStatement().use {
      it.executeUpdate("CREATE TABLE mijntabel(nr INT); INSERT INTO mijntabel(nr) VALUES(1);")  
    }
}
fun verifyDbContents() {
    connection.createStatement().use {
        val result = it.executeQuery("SELECT COUNT(*) FROM mijntabel;")
        assert result.getInt(0) == 1
    }
}
```

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

{{% notice note %}}
Vergelijk de Kotlin implementatie met de standaard Java versie. Als het aan komt op efficiënt gebruik van oudere Java APIs zoals de SQL methodes, is Kotlin véél eenvoudiger, zowel in gebruik als in leesbaarheid. Hier maken we handig gebruik van [de use extension](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.io/use.html) om geen `close()` te moeten oproepen, en `with {}` om de connectionManager in te stellen zonder telkens `connection.` te moeten herhalen. Nog een voordeel: er is geen verschil tussen checked en unchecked exceptions. <br/>Het loont dus om ook voor dit vak te opteren voor Kotlin---maar het is niet verplicht.
{{% /notice %}}

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

```kt
val result = statement.executeQuery("SELECT * FROM iets")
while(result.next()) {
    val eenString = result.getString("kolomnaam")
    // doe iets!
}
```

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

1. Maak (én test!) een klasse `StudentRepository` die de volgende methode implementeert. Zoals je ziet is het de bedoeling dat de JDBC `Connection` instance elders wordt aangemaakt, bijvoorbeeld in een **aparte** `ConnectionManager` klasse. 

<div class="devselect">

```kt
class StudentRepository(val connection: Connection) {
    fun getStudentsByName(name: String): List<Student>
}
```

```java
public class StudentRepository {
        public StudentRepository(Connection connection);
        public List<Student> getStudentsByName(String name);
}
```
</div>

2. Breid dit uit met `saveNewStudent(Student)`.
3. Breid dit uit met `updateStudent(Student)`. Wat moet je doen als deze student nog niet in de database zit? Welke gegevens update je wel en welke niet? 

**Tip**: 

- `executeUpdate()` van een `Statement` is erg omslachtig als je een string moet stamenstellen die een `INSERT` query voorstelt (haakjes, enkele quotes, ...). Wat meer is, als de input van een UI komt, kan dit gehacked worden, door zelf de quote te sluiten in de string. Dit noemt men **SQL Injection**, en om dat te vermijden gebruik je in JDBC de `prepareStatement()` methode. Zie [JDBC Basics: Prepared Statements](https://docs.oracle.com/javase/tutorial/jdbc/basics/prepared.html). De String die je meegeeft bevat in de plaats van parameters een vraagteken: `INSERT INTO STUDENT(bla, bla) VALUES(?, ?)`. Die parameters vul je daarna aan met `preparedStatement.setString()` of `setInt()`. Op die manier is de code zowel _netjes_ als _injectie-vrij_!

## 1.2 Queries/Objecten in Jdbi 3

[Jdbi](https://jdbi.org) (Java DataBase Interface v3) is een lightweight library geschreven bovenop JDBC. Het gebruikt dus de interne Java API om te communiceren tussen de database en de Java applicatie. Echter, het maakt het leven voor ons als ontwikkelaar op heel wat vlakken véél _aangenamer_: waar JDBC eerder database-driven en dialect-afhankelijk is, is Jdbi eerder user-driven en met behulp van plugins dialect-onafhenkelijk. 

JDBI3 is opgedeeld in modules, waarvan wij de volgende drie gaan gebruiken:

- `jdbi3-core` (altijd nodig) - voor JDBC zit dit in de JDK. 
- `jdbi3-sqlite` (voor de SQLite verbinding) - of andere DB driver
- `jdb3-sqlobject` - voor de eenvoudige mapping naar Plain Old Java Objects (POJOs)

Met JDBI3 wordt op de volgende manier Java met de DB verbonden:

{{<mermaid align="left">}}
graph LR;
    Java[Java]
    Jdbi[Jdbi3-core]
    JDBC[JDBC]
    JSQLITE[Jdbi3-SQLite]
    SQLite[SQLite-JDBC]
    DB[(SQLite Database)]
    subgraph Java space
    Java --> Jdbi
    Jdbi --> JDBC
    Jdbi --> JSQLITE
    JSQLITE -.-> SQLite
    JDBC -.-> SQLite
    end
    subgraph DB space
    SQLite --> DB
    end
{{< /mermaid >}}

Er komt dus één blokje bij tussen Java en JDBC: we gebruiken niet langer de ingebouwde JDK interfaces maar rechtstreeks de `jdbi-core` dependency die via JDBC de SQLite connectie maakt. De `jdbi3-sqlite` package is afhankelijk van `sqlite-jdbc`: zie [artifact dependency info](https://mvnrepository.com/artifact/org.jdbi/jdbi3-sqlite/3.34.0). Met andere worden, het wordt een _transitieve_ dependency: deze verdwijnt uit onze `build.gradle`, maar wordt nog steeds meegetrokken met de rest.

Er is ook support voor spring, jpa, guava, kotlin, ...

Om bovenstaande JDBC oefening te implementeren in Jdbi3 hebben we eerst een extractie van een interface nodig voor de repository acties:

<div class="devselect">

```kt
interface StudentRepository {
    fun getStudentsByName(student String): List<Student>
    fun saveNewStudent(student: Student)
    fun updateStudent(student: Student)
}
```

```java
public interface StudentRepository {
    List<Student> getStudentsByName(String student);
    void saveNewStudent(Student student);
    void updateStudent(Student student);
}
```
</div>

Nu kan `StudentRepositoryJdbcImpl` (hernoem bovenstaande) en onze nieuwe `StudentRepositoryJdbi3Impl` de interface `implements`-en. Denk aan de **Strategy design pattern** van SES: afhankelijk van een instelling kunnen we switchen van SQL leverancier, zolang de code overal de interface gebruikt. 

{{<mermaid align="left">}}
graph LR;
    Main[Controlller]
    Interface{StudentRepository}
    Jdbc[StudentRepositoryJdbcImpl]
    Jdbi[StudentRepositoryJdbi3Impl]
    Main --> Interface
    Interface --> Jdbc
    Interface --> Jdbi
{{< /mermaid >}}

### 1.2.1 JDBC vs Jdbi3

#### **1. Connection openen**

In plaats van JDBC's `DriverManager.getConnection()` om de `Connection` instance te bootstrappen, gebruiken wij gewoon `Jdbi.create()` met ook één paremter, namelijk dezelfde connectionstring. 

#### **2. Query uitvoeren**

In plaats van de vervelende checked `SQLException`s en de `createStatement()` code, heb je nu de keuze om ofwel de Fluent API te gebruiken:

```java
return jdbi.withHandle(handle -> {
   return handle.createQuery("SELECT * FROM student WHERE naam = :naam")
           .bind("naam", student)
           .mapToBean(Student.class)
           .list();
});
```

ofwel de Declarative API, waarbij je met de `@SqlQuery` kan werken op een interface:

```java
public interface StudentDao {
    @SqlQuery("SELECT * FROM student")
    @RegisterBeanMapper(Student.class)
    List<Student> getStudenten();
}
```

Dit vereist dat je de plugin `SqlObjectPlugin` installeert na de `Jdbi.create()`: `jdbi.installPlugin(new SqlObjectPlugin());`. Zie [jdbi.org](https://jdbi.org) documentatie.

{{% notice note %}}
Jdbi ondersteunt Kotlin met twee modules: `jdbi3-kotlin` en `jdbi3-kotlin-sqlobject` om data classes direct te binden aan een bepaalde tabel. Bovenstaande Java code (met `.bind()` werken) is analoog. Om verwarring te voorkomen zijn de Jdbi voorbeelden uitgewerkt in Java. Lees meer op http://jdbi.org/#_kotlin
{{% /notice %}}


Herinner je je nog de **SESsy Library**? Die werkte ook op die manier! Kijk nog eens in https://github.com/kuleuven-diepenbeek/sessylibrary in de map `src.main.java.be.kuleuven.sessylibrary.domain` in klasse `BooksRepository`!

Merk op dat Jdbi3 er voor kan zorgen dat de resultaten van je query automatisch worden vertaald naar een `Student` instantie door middel van **bean mapping**: de `mapToBean()` methode of de `@RegisterBeanMapper` annotatie. Die gaat via reflectie alle kolomnamen 1-op-1 mappen op properties van je object dat je wenst te mappen. Er zijn ook nog andere mogelijkheden, zoals mappen op een `HashMap`, ea:

![](/img/jdbi-map.jpg)

#### **3. Transacties, insert/update queries, ...**

Zelfstudie. Zie [jdbi.org](https://jdbi.org) documentatie.

### 1.2.2 Oefeningen

**Quickstart project**: `examples/jdbc-repo-start` in in de [cursus repository](https://github.com/kuleuven-Diepenbeek/db-course) ([download repo zip](https://github.com/KULeuven-Diepenbeek/db-course/archive/refs/heads/main.zip)). Deze bevat reeds bovenstaande JDBC implementatie en een aantal unit testen, waarvan er nog twee falen.

1. Fix eerst de falende unit testen!
2. Herimplementeer alle methodes van de `StudentRepository` interface hierboven, maar dan in Jdbi3 met de Fluent API (`jdbi.withHandle()`). Maak een tweede klasse genaamd `StudentRepositoryJdbi3`. Schrijf ook een bijhorende unit test klasse (kijk voor inspiratie naar de JDBC implementatie). Om te testen of het werkt in "productie" kan je je testcode van JDBC herbruiken door de code de **interface** te laten gebruiken in plaats van de implementatie. Bijvoorbeeld:

<div class="devselect">

```kt
fun main(args: Array<String>) {
    val jdbcRepo = StudentRepositoryJdbc(...)
    val jdbiRepo = StudentRepositoryJdbi3(...)
    doStuff(jdbcRepo)
}
fun doStuff(repository: StudentRepository) {
    // argunent = interface!
    // uw repository.getStudentsByName, saveNewStudent, ... tests hier
}
```

```java
public class OefeningMain {
        public static void main(String[] args) {
            var jdbcRepo = new StudentRepositoryJdbc(...);
            var jdbiRepo = new StudentRepositoryJdbi3(...);
            doStuff(jdbcRepo);
        }
        public static void doStuff(StudentRepository repository) {
            // argunent = interface!
            // uw repository.getStudentsByName, saveNewStudent, ... tests hier
        }
}
```
</div>

3. _Extra Oefening_: Maak een nieuwe implementatie van de repository interface die via de Jdbi3 Declaratie API de queries doorgeeft naar de SQLite DB. D.w.z., lees in de [Jdbi3 developer guide](http://jdbi.org/#_declarative_api) na hoe je de Declarative API gebruikt en verwerk dit. Tip: `jdbi.withExtension(StudentDao.class, ...)`. 

**Tip**:

- Neem de tijd om de JDBI documentatie uitvoerig te bekijken!

## 1.3 Jdbi Backend + JavaFX Frontend

Met Java database access enigszins onder de knie kijken we verder dan alleen maar de "repository". Op welke manier kunnen we onze `STUDENT` tabel visueel weergeven, en er studenten aan toevoegen of uit verwijderen? 

Dat kan op verschillende manieren, van HTML (SESsy Library) en JavaScript API calls naar iets eenvoudiger vanuit het eerstejaarsvak INF1: **JavaFX**. Je kan in JavaFX eenvoudig `TableView` stukken positioneren op een `AnchorPane` en die vullen met de juiste kolommen en rijen. De data blijft uiteraard uit de SQLite DB komen via JDBC/Jdbi. De `StudentRepository` is dus slechts één deel van het verhaal: waar wordt deze gebruikt? In JavaFX controllers. 

### 1.3.1 Een Gradle JavaFX Project

Er zijn een aantal aanpassingen nodig aan je `build.gradle` file om van een gewone Java applicatie over te schakelen naar een JavaFX-enabled applicatie. We hebben de **application** en **javafxplugin** plugins nodig onder `plugins {}`, verder ook een `javafx {}` property groep die bepaalt welke modules van JavaFX worden ingeladen:

```
plugins {
    id 'application'
    id 'org.openjfx.javafxplugin' version '0.0.10'
}

repositories {
    mavenCentral()
}

javafx {
    version = "15"
    modules = [ 'javafx.controls', 'javafx.fxml' ]
}

dependencies {
    implementation group: 'org.xerial', name: 'sqlite-jdbc', version: '3.36.0.3'
    implementation group: 'org.jdbi', name: 'jdbi3-core', version: '3.24.1'
    implementation group: 'org.jdbi', name: 'jdbi3-sqlite', version: '3.24.1'
    implementation group: 'org.jdbi', name: 'jdbi3-sqlobject', version: '3.24.1'
    testImplementation group: 'junit', name: 'junit', version: '4.12'
}

group 'be.kuleuven.javasql'
version '1.0-SNAPSHOT'
sourceCompatibility = 1.13
mainClassName = 'be.kuleuven.javasql.SqlFxMain'
```

Herinner je het volgende over JavaFX:

- De main klasse leidt af van `Application` en laadt de hoofd-`.fxml` file in.
- Controllers hebben een `public void initialize()` methode waar action binding in wordt gedefinieerd. 
- `.fxml` files beheer je met SceneBuilder. Vergeet hier niet de link naar de fully qualified name van je controller klasse te plaatsen als `AnchorPane` attribuut: `fx:controller="be.kuleuven.javasql.controller.StudentController"`.

{{% notice warning %}}
Problemen met je JDK versie en Gradle versies? Raadpleeg de [Gradle Compatibiility Matrix](https://docs.gradle.org/current/userguide/compatibility.html). Gradle 6.7 of hoger ondersteunt JDK15. Gradle 7.3 of hoger ondersteunt JDK17. Let op met syntax wijzigingen bij Gradle 7+!<br/>
Je Gradle versie verhogen kan door de URL in `gradle/gradlew.properties` te wijzigen.<br/>
De laatste versie van JavaFX is 17---backwards compatible met JDK15 en hoger.
{{% /notice %}}

Voor onze studententabel visualisatie hebben we een `TableView` nodig. Daarnaast eventueel `Button`s om te editeren/toe te voegen/... Vergeet de `fx:id` van de tabel niet:

![](/img/javafx-id.jpg)

Kolommen (en de inhoud van de rijen) definiëren we in de controller zelf:

<div class="devselect">

```kt
@FXML
private lateinit var tblStudent: TableView<Student>

fun initialize() {
    tblStudent.getColumns().clear()
    val col: TableColumn<Student, String> = TableColumn<>("Naam").apply {
        setCellValueFactory(f -> ReadOnlyObjectWrapper<>(f.getValue().getMaam()))
    }
    with(tblStudent) {
        getColumns().add(col)
        getItems().add(Student("Joske", "Josmans", 124, true))
    }
}
```

```java
@FXML
private TableView<Student> tblStudent;

public void initialize() {
    tblStudent.getColumns().clear();
    TableColumn<Student, String> col = new TableColumn<>("Naam");
    col.setCellValueFactory(f -> new ReadOnlyObjectWrapper<>(f.getValue().getMaam()));
    tblStudent.getColumns().add(col);
    
    tblStudent.getItems().add(new Student("Joske", "Josmans", 124, true));
}
```
</div>

Merk op dat `TableView` een generisch type heeft, en we zo dus heel eenvoudig onze eigen POJO rechtstreeks kunnen mappen op de `Student` klasse! Als we dit opstarten krijgen we alvast één kolom te zien met de naam (`f` in de `CellValueFactory` is een wrapper waarvan de waarde de huidige student in de rij is. `getNaam()` zorgt ervoor dat de juiste waarde in de juiste cel komt te staan)

![](/img/fxmltable.jpg)

### 1.3.2 Oefeningen

**Quickstart project**: `examples/jdbc-fxml-start` in de [cursus repository](https://github.com/kuleuven-Diepenbeek/db-course) ([download repo zip](https://github.com/KULeuven-Diepenbeek/db-course/archive/refs/heads/main.zip)). Deze bevat reeds bovenstaande JDBC implementatie en een leeg gekoppeld JavaFx project. Om uit te voeren, klik op "Gradle" en voer target "run" uit (dus niet op "Play" in de main klasse!).

1. Werk bovenstaande voorbeeld verder uit voor alle kolommen. Voeg eerst testdata toe (`getItems().add(new student...`).
2. Probeer nu de controller te linken met de repository. De tabel items moeten overeenkomen met de repository items. Proficiat, je kijkt naar "live data"!
3. Voeg een knop **Voeg Toe** toe op het scherm, dat een ander FXML venster opent, waar je gegevens van de nieuwe student kan ingeven, en kan bewaren. De "bewaren" knop persisteert naar de database, sluit het venster, én refresht het studentenadmin overzichtsscherm. 

**Tip**: Vanuit een JavaFX controller een ander scherm openen is een kwestie van een nieuwe `Stage` en `Scene` object aan te maken:

<div class="devselect">

```kt
private fun showScherm() {
    val resourceName = "bla.fxml"
    val root = FXMLLoader.load(this::class.java..getResource(resourceName)) as AnchorPane;
    val stage = Stage().apply {
        setScene(Scene(root))
        setTitle("dinges")
        initModality(Modality.WINDOW_MODAL)
        show()
    }
}
```

```java
private void showScherm() {
    var resourceName = "bla.fxml";
    try {
        var stage = new Stage();
        var root = (AnchorPane) FXMLLoader.load(getClass().getClassLoader().getResource(resourceName));
        stage.setScene(new Scene(root));
        stage.setTitle("dinges");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.show();

    } catch (Exception e) {
        throw new RuntimeException(e);
    }
}
```
</div>

Zit je vast? Raadpleeg de **TableView JavaDocs**: https://openjfx.io/javadoc/13/javafx.controls/javafx/scene/control/TableView.html

Bekijk een voorbeeld **Kotlin/JavaFX project** in de [github appdev-course repository](https://github.com/KULeuven-Diepenbeek/appdev-course/tree/main/examples/kotlin/walkerfx/src/main/kotlin/be/kuleuven/walkerfx).
