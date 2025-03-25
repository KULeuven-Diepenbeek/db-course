---
title: JDBI - Java DataBase Interface v3
draft: false
---

## Queries/Objecten in Jdbi 3

[Jdbi](https://jdbi.org) (Java DataBase Interface v3) is een lightweight library geschreven bovenop JDBC. Het gebruikt dus de interne Java API om te communiceren tussen de database en de Java applicatie. Echter, het maakt het leven voor ons als ontwikkelaar op heel wat vlakken véél _aangenamer_: waar JDBC eerder database-driven en dialect-afhankelijk is, is Jdbi eerder user-driven en met behulp van plugins dialect-onafhankelijk. 

JDBI3 is opgedeeld in modules, waarvan wij de volgende drie gaan gebruiken:

- `jdbi3-core` (altijd nodig) - voor JDBC zit dit in de JDK. 
- `jdb3-sqlobject` - voor de eenvoudige mapping naar Plain Old Java Objects (POJOs)
- Nog steeds je mysql driver: `implementation 'mysql:mysql-connector-java:8.0.33'`

{{% notice warning %}}
Voor SQLite heb je ook nog volgende implementation nodig `jdbi3-sqlite`.
{{% /notice %}}

```groovy
implementation 'mysql:mysql-connector-java:8.0.33'
// JDBI
implementation 'org.jdbi:jdbi3-core:3.45.0'
implementation 'org.jdbi:jdbi3-sqlobject:3.45.0'
```

Met JDBI3 wordt op de volgende manier Java met de DB verbonden:


{{<mermaid align="left">}}
graph LR;
    Java[Java]
    Jdbi[Jdbi3-core]
    JDBC[JDBC]
    JMYSQL[Jdbi3-MySQL]
    MYSQL[MySQL-JDBC]
    DB[(MyuSQL Database)]
    subgraph Java space
    Java --> Jdbi
    Jdbi --> JDBC
    Jdbi --> JMYSQL
    JMYSQL -.-> MYSQL
    JDBC -.-> MYSQL
    end
    subgraph DB space
    MYSQL --> DB
    end
{{< /mermaid >}}


Er komt dus één blokje bij tussen Java en JDBC: we gebruiken niet langer de ingebouwde JDK interfaces maar rechtstreeks de `jdbi-core` dependency die via JDBC de MySQL connectie maakt. 

Om voorgaand JDBC demo (met enkel een simpele student) te implementeren in Jdbi3 hebben we eerst een extractie van een interface nodig voor de repository acties, zodat we snel kunnen switchen tussen onze verschillende implementaties:

```java
public interface StudentRepository {
    List<Student> getStudentsByName(String student);
    void saveNewStudent(Student student);
    void updateStudent(Student student);
}
```

Nu kan `StudentRepositoryJdbcImpl` (hernoem bovenstaande) en onze nieuwe `StudentRepositoryJdbi3Impl` de interface `implements`-en. Afhankelijk van een instelling bijvoorbeeld kunnen we switchen van SQL leverancier, zolang de code overal de interface gebruikt. 

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


### JDBC vs Jdbi3

Geen idee waar te beginnen? Hier: http://jdbi.org/ 

#### **1. Connection openen**

In plaats van JDBC's `DriverManager.getConnection()` om de `Connection` instance te bootstrappen, gebruiken wij gewoon `Jdbi.create()` met ook drie parameters, namelijk dezelfde ConnectionString, user en password.
```java
Jdbi jdbi = Jdbi.create(connectionString, username, password);
```

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

Merk op dat Jdbi3 er voor kan zorgen dat de resultaten van je query automatisch worden vertaald naar een `Student` instantie door middel van **bean mapping**: de `mapToBean()` methode. Die gaat via reflectie alle kolomnamen 1-op-1 mappen op properties van je object dat je wenst te mappen (Je kolomnamen moeten dan wel overeenkomen met de attribuut namen van je Java Klasse). Er zijn ook nog andere mogelijkheden, zoals mappen op een `HashMap`, ea:

![](/img/jdbi-map.jpg)


<!-- ### EER-schema/database mapping naar Java Objects -->



<!-- ## Declaratieve API -->

<!-- ofwel de Declarative API, waarbij je met de `@SqlQuery` kan werken op een interface:

```java
//Data Access Object
public interface StudentDao {
    @SqlQuery("SELECT * FROM student")
    @RegisterBeanMapper(Student.class)
    List<Student> getStudenten();
}
```

Dit vereist dat je de plugin `SqlObjectPlugin` installeert na de `Jdbi.create()`: `jdbi.installPlugin(new SqlObjectPlugin());`. Zie [jdbi.org](https://jdbi.org) documentatie. -->


















<!-- **Quickstart project**: `jdbc-repo-start` vind je als zip [hier]. Deze bevat reeds bovenstaande JDBC implementatie en een aantal unit testen, waarvan er nog twee falen.

1. Fix eerst de falende unit testen!
2. Herimplementeer alle methodes van de `StudentRepository` interface hierboven, maar dan in Jdbi3 met de Fluent API (`jdbi.withHandle()`). Maak een tweede klasse genaamd `StudentRepositoryJdbi3`. Schrijf ook een bijhorende unit test klasse (kijk voor inspiratie naar de JDBC implementatie). Om te testen of het werkt in "productie" kan je je testcode van JDBC herbruiken door de code de **interface** te laten gebruiken in plaats van de implementatie. Bijvoorbeeld:


3. Implementeer opnieuw de `Cursus` link met de `Student`. Is het schrijven van `JOIN` queries in Jdbi3 eenvoudiger?
4. _Extra Oefening_: Maak een nieuwe implementatie van de repository interface die via de Jdbi3 Declaratie API de queries doorgeeft naar de SQLite DB. D.w.z., lees in de [Jdbi3 developer guide](http://jdbi.org/#_declarative_api) na hoe je de Declarative API gebruikt en verwerk dit. Tip: `jdbi.withExtension(StudentDao.class, ...)`. 

**Tip**:

- Neem de tijd om de JDBI documentatie uitvoerig te bekijken! -->


<!-- ## 1.3 Jdbi Backend + JavaFX Frontend

Met Java database access enigszins onder de knie kijken we verder dan alleen maar de "repository". Op welke manier kunnen we onze `STUDENT` tabel visueel weergeven, en er studenten aan toevoegen of uit verwijderen? 

Dat kan op verschillende manieren, van HTML (SESsy Library) en JavaScript API calls naar iets eenvoudiger vanuit het eerstejaarsvak INF1: **JavaFX**. Je kan in JavaFX eenvoudig `TableView` stukken positioneren op een `AnchorPane` en die vullen met de juiste kolommen en rijen. De data blijft uiteraard uit de SQLite DB komen via JDBC/Jdbi. De `StudentRepository` is dus slechts één deel van het verhaal: waar wordt deze gebruikt? In JavaFX controllers.  -->

<!-- ### 1.3.1 Een Gradle JavaFX Project

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

Bekijk een voorbeeld **Kotlin/JavaFX project** in de [github appdev-course repository](https://github.com/KULeuven-Diepenbeek/appdev-course/tree/main/examples/kotlin/walkerfx/src/main/kotlin/be/kuleuven/walkerfx). -->