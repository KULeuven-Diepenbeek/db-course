---
title: 4. Extra Oefeningen
draft: true
---

## SESsy Library - Revisited

**Voorbereiding**: Maak uzelf opnieuw vertrouwd met [de SESsy Library applicatie](https://kuleuven-diepenbeek.github.io/ses-course/extra/sessy/), de geïntegreerde oefening uit het tweedejaarsvak Software Engineering Skills. 

![](https://kuleuven-diepenbeek.github.io/ses-course/img/teaching/ses/sessy.png)

Het is interessant om de libraries die de app gebruikt eens in detail te inspecteren. Dat kan met `gradlew dependencies`. In context van het vak _Databasess_ zien we dat de webapp gebouwd is met de volgende relevante tools en libraries:

1. Als database endpoint wordt `com.h2database.h2` gebruikt. 
2. De Dropwizard `jdbi3` dependency verwijst op zijn beurt naar een variant van `jdbc`
3. De `sqlobject` plugin van Jdbi3 wordt gebruikt. 

Zoals een snippet van de dependency tree aangeeft:

```
...
+--- io.dropwizard:dropwizard-jdbi3:2.0.0-rc12
|    +--- io.dropwizard:dropwizard-db:2.0.0-rc12
|    |    +--- io.dropwizard:dropwizard-core:2.0.0-rc12 (*)
|    |    \--- org.apache.tomcat:tomcat-jdbc:9.0.27
|    |         \--- org.apache.tomcat:tomcat-juli:9.0.27
...
```

### Oefeningen

1. Inspecteer de [H2 Database Engine](http://h2database.com/html/main.html) documentatie. Wat is het verschil tussen H2 en MySQL? En wat zijn de _gelijkenissen_ tussen H2 en SQLite dat elders in deze cursus wordt gebruikt? 
2. Zoek in de SEssy code waar de Jdbi3 sqlobject annotaties worden gebruikt. Dit is dus de Jdbi _Declarative API_ (zie [docs](https://jdbi.org)). Wat zou je moeten doen om dit te refactoren naar de _Fluent API_? Voer de veranderingen door voor de methode `findBooksByTitle()`.
3. Worden ergens _transacties_ gebruikt? Wordt er ergens in de code bewaakt tegen concurrency? Indien niet, bouw dit in voor het uitlenen van boeken in de methode `borrow()`. Moet transactie beheer op niveau van _Resource_ klasses staan, of behoort het volgens jou toe aan de _Repository_ klasses? Welke code heb je specifiek nodig om in Jdbi3 transacties te hanteren? 
4. Stel dat ik in een nieuwe repository klasse een low-level SQL statement zelf wil schrijven en uitvoeren, rechtstreeks naar de database, in plaats van via Jdbi3. Hoe kan ik dat doen? Probeer dit zelf in de code door iets met de `books` tabel te doen (bijvoorbeeld een `COUNT`).
5. Is er een manier om de queries die Jdbi3 uitvoert in H2 ook af te drukken, zoals Hibernate's `show_sql` config flag? 
6. We zijn niet meer tevreden over de performantie en het gebruiksgemak van H2. We wensen in de plaats daarvan JPA + Hibernate te gebruiken. Voer de nodige veranderingen door. **Test de applicatie** ook uitvoerig door de webserver op te starten! De integratie testen die falen mag je negeren, dat valt buiten deze cursus om die ook te converteren. Denk eerst goed na over welke wijzigingen allemaal moeten gebeuren:
    - Welke config file wordt nu gebruikt om de database file/connectionstring op te geven? Hoe kan je dat vervangen door `persistence.xml`?
    - Kan je interfaces, strategies, of facades gebruiken om _eerst_ de H2 implementatie te verbergen, en dan te refactoren, of is dat niet meer nodig?
    - Is er een manier [in Dropwizard](https://www.dropwizard.io/en/latest/manual/hibernate.html?highlight=hibernate) om eenvoudiger Hibernate session factories aan te maken? (ja dus!) 
    - ... 





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

Geen idee waar te beginnen? Hier: http://jdbi.org/ 

#### **1. Connection openen**

In plaats van JDBC's `DriverManager.getConnection()` om de `Connection` instance te bootstrappen, gebruiken wij gewoon `Jdbi.create()` met ook één parameter, namelijk dezelfde ConnectionString. 

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

3. Implementeer opnieuw de `Cursus` link met de `Student`. Is het schrijven van `JOIN` queries in Jdbi3 eenvoudiger?
4. _Extra Oefening_: Maak een nieuwe implementatie van de repository interface die via de Jdbi3 Declaratie API de queries doorgeeft naar de SQLite DB. D.w.z., lees in de [Jdbi3 developer guide](http://jdbi.org/#_declarative_api) na hoe je de Declarative API gebruikt en verwerk dit. Tip: `jdbi.withExtension(StudentDao.class, ...)`. 

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
    id 'org.openjfx.javafxplugin' version '0.0.13'
}

repositories {
    mavenCentral()
}

javafx {
    version = "17"
    modules = [ 'javafx.controls', 'javafx.fxml' ]
}

dependencies {
    implementation group: 'org.xerial', name: 'sqlite-jdbc', version: '3.43.2.0'
    implementation group: 'org.jdbi', name: 'jdbi3-core', version: '3.41.3'
    implementation group: 'org.jdbi', name: 'jdbi3-sqlite', version: '3.41.3'
    implementation group: 'org.jdbi', name: 'jdbi3-sqlobject', version: '3.41.3'

    testImplementation 'org.junit.jupiter:junit-jupiter-api:5.9.0'
    testRuntimeOnly 'org.junit.jupiter:junit-jupiter-engine'    
}

group 'be.kuleuven.javasql'
version '1.0-SNAPSHOT'
sourceCompatibility = 1.13
mainClassName = 'be.kuleuven.javasql.SqlFxMain'

jar {
    manifest {
        attributes 'Implementation-Title': project.getProperty('name'),
                'Implementation-Version': project.getProperty('version'),
                'Main-Class': project.getProperty('mainClassName')
    }
}
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