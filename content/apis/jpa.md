---
title: JPA en Hibernate
draft: true
---

## 2.1 Wat is JPA?

JPA of de **Jakarta Persistence API** _(vroeger de Java Persistence API genoemd)_ is een deel van Java EE (Java _Enterprise Platform_), een set van specificaties die initieel de JDK SE 8 versie uitbreidden met "enterprise" features zoals distributed computing en web services. J2EE wordt vooral ingezet als het gaat over grote applicaties die bedrijven ontwikkelen voor andere bedrijven (zogenaamde "B2B", Business 2 Business, of Enterprise Software Development). 

{{% notice note %}}
Ondertussen is J2EE omgevormd tot [Jakarta EE](https://jakarta.ee). Dat betekent ook dat JPA [recent officieel werd vervangen](https://blogs.oracle.com/javamagazine/post/transition-from-java-ee-to-jakarta-ee) door de Jakarta Persistence API. Je zal merken dat de `javax.persistence` dependency die wij gebruiken niet meer wordt geupdate. Pas dus op met recente Stack Overflow links!
{{% /notice %}}

### 2.1.1 Maar wat is de JPA API nu precies?

De API, levend in de package `javax.persistence`, is een manier om relationele data voor te stellen in enterprise Java applicaties, door middel van Objecten. Het is dus een mapping tool die object/relationale (meta)data bewerkt en verwerkt. JPA heeft ook zijn eigen query language, JPQL, die het eenvoudiger moet maken om queries te schrijven _in Java code zelf_ in plaats van in SQL, die vertaald worden naar SQL. Dit vereenvoudigt refactoring en vermindert mogelijke fouten in de SQL string die te laat naar boven komen (nu compiletime ipv runtime, aangezien Java statisch getypeerd is). 

JPA is niet meer dan een specificatie die de **interfaces** en **annotaties** voorziet. De implementatie, en de **klasses**, worden overgelaten aan _vendors_, waarvan voor JPA 2.2 [de volgende vendors](https://en.wikipedia.org/wiki/Jakarta_Persistence) beschikbaar zijn:

- DataNucleus
- EclipseLink
- Hibernate
- OpenJPA

JPA 2.2 Gradle dependency: `compile group: 'javax.persistence', name: 'javax.persistence-api', version: '2.2'`


## 2.2 Wat is Hibernate ORM?

Volgens de [hibernate.org](http://hibernate.org/orm/) website:

> Your relational data. Objectively. 

Hibernate is dé populairste object-relational mapper in Java die de JPA standaard implementeert. Hibernate heeft zowel een eigen API als een JPA specificatie, en kan dus overal waar JPA nodig is ingeschakeld worden. Het wordt vaak in omgevingen gebruikt waar performantie belangrijk is, en waar enorm veel data en gebruikers data transferreren. 

**Belangrijk startpunt:** [Hibernate getting started guide](https://docs.jboss.org/hibernate/orm/5.4/quickstart/html_single/) Ook Hibernate werkt met modules, zoals Jdbi3. We gebruiken `hibernate-core`; via Gradle: `compile group: 'org.hibernate', name: 'hibernate-core', version: '5.4.23.Final'`

Het gebruik van Hibernate geeft meestal een aantal mogelijkheden:

1. Gebruik de Native Hibernate API en `hbm.xml` mapping (Zie "2. Tutorial Using Native Hibernate APIs and hbm.xml Mapping")
2. Gebruik de Native Hibernate API en annotaties (Zie "3. Tutorial Using Native Hibernate APIs and Annotation Mappings")
3. Gebruik de JPA interface (Zie "4. Tutorial Using the Java Persistence API (JPA)")

Waarvan wij #3 gaan hanteren. 

### 2.2.1 Hibernate/JPA Bootstrapping

JPA bootstrappen kan - net zoals JDBC en Jdbi - vrij eenvoudig met een statische klasse `Persistence` die een `sessionFactory` object aanmaakt. Elke **session factory** stelt een verbinding voor tussen de Java code en de Database zelf. Om te kunnen werken met objecten moet je vanuit de session factory de **entity manager** creëren. Vanaf dan kan er worden gewerkt met de database via de entity manager instantie.

```java
var sessionFactory = Persistence.createEntityManagerFactory("be.kuleuven.mijnmooiepackage");
var entityManager = sessionFactory.createEntityManager();
// do stuff with it!
// entityManager.createQuery(...)
```

`javax.persistence.Persistence` gaat op zoek naar een `persistence.xml` bestand in de map `src/main/resources/META-INF`. Die bevat alle connectiegegevens en instellingen. De **persistence XML file** is de _belangrijkste_ file van je hele applicatie, waar caching strategie, driver management, table autocreation, ... allemaal in wordt bepaald! 

Een voorbeeld XML file:

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<persistence version="2.1"
             xmlns="http://xmlns.jcp.org/xml/ns/persistence" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence http://xmlns.jcp.org/xml/ns/persistence/persistence_2_1.xsd">
    <persistence-unit name="be.kuleuven.studenthibernate.domain">
        <description>Studenten JPA Test</description>
        <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>
        <properties>
            <property name="javax.persistence.jdbc.driver" value="org.sqlite.JDBC"/>
            <property name="javax.persistence.jdbc.url" value="jdbc:sqlite:studenten.db"/>
            <property name="javax.persistence.jdbc.user" value=""/>
            <property name="javax.persistence.jdbc.password" value=""/>
            <property name="javax.persistence.schema-generation.database.action" value="drop-and-create"/>

            <property name="hibernate.dialect" value="org.hibernate.dialect.SQLiteDialect" />
            <property name="hibernate.connection.autocommit" value="true"/>
            <property name="hibernate.show_sql" value="true"/>
            <property name="hibernate.flushMode" value="ALWAYS" />

            <property name="hibernate.cache.use_second_level_cache" value="false" />
            <property name="hibernate.cache.provider_class" value="org.hibernate.cache.NoCacheProvider" />
        </properties>
    </persistence-unit>
</persistence>
```

Bevat onder andere de volgende belangrijke properties:

- `javax.persistence` JDBC driver/url. Merk op dat achterliggend dus nog steeds JDBC wordt gebruikt! Dat betekent ook dat we de sqlite dependency `sqlite-jdbc` van de groep `org.xerial` nog steeds nodig hebben. 
- `schema-generation` properties: `drop-and-create` betekent dat tabellen die niet bestaan automatisch worden aangemaakt. Geen `CREATE TABLE` statements meer nodig, dus. 
- `hibernate.dialect`: voor vendor-specifieke queries te genereren moet Hibernate weten welke database wij hanteren. Dit staat los van de jdbc driver! Hiervoor gebruiken we het dialect van dependency `compile group: 'com.zsoltfabok', name: 'sqlite-dialect', version: '1.0'`.
- Flush modes, auto-commit instellingen, caching, e.a. Dit gaat ver buiten de scope van deze cursus. 
- `show_sql` print de gegenereerde queries af in de console, handig om te zien hoe Hibernate intern werkt, en om te debuggen.  

Er ontbreekt hierboven nog een belangrijk gegeven: elke entity (domein object dat een tabel voorstelt in de code) moet met fully qualified name in een `<class/>` tag onder `<persistence-unit/>` worden toegevoegd. Anders herkent JPA het object niet, en heeft hij geen idee welke kolommen te mappen op welke properties. Die metadata zit namelijk in de entity klasse zelf. 

Meer informatie: zie [hibernate.org documentatie](http://hibernate.org/orm/) en [A beginners guide to JPA persistence xml](https://thorben-janssen.com/jpa-persistence-xml/).

### 2.2.2 Hibernate/JPA Peristence/querying

Nu de verbinding tussen de DB en Hibernate/JPA tot stand werd gebracht, is het tijd om gebruik te maken van de kracht van de library. 

Om kolommen te kunnen mappen op properties voorziet JPA een aantal **annotaties** als meta-data op het domeinobject zelf. Dat betekent dat DB beschrijving netjes bij het object waar het hoort wordt geplaatst. Bijvoorbeeld:

<div class="devselect">

```kt
@Entity
data class Huis(
    @Column(name = "beschr") var beschrijving: String,
    @Column var prijs: Int? = null,
    @Column @Id @GeneratedValue var id: Int = 0)
```

```java
@Entity
public class Huis {
    @Column
    @Id
    @GeneratedValue
    private int id;
    @Column(name = "beschr")
    private String beschrijving;
    @Column
    private int prijs;
}
```
</div>

{{% notice note %}}
In Kotlin zijn types standaard not-nullable. Denk goed na over de mogelijke waardes van elk type: kan er ooit `null` in komen? Indien ja werk je met Kotlin's optional alternatief: suffixen met een `?`. Not-nullable types die later dan de constructor een waarde krijgen toegewezen worden aangeduid met `lateinit`. Zie [Null safety Kotlin docs](https://kotlinlang.org/docs/null-safety.html).<br/>
Om Kotlins `data class` te laten samenwerken met oudere Java APIs zoals JPA/Hibernate, die niet kunnen omgaan met immutability, moeten we nog een **extra plugin** installeren: [de no-arg plugin](https://kotlinlang.org/docs/no-arg-plugin.html): `id("org.jetbrains.kotlin.plugin.jpa") version "1.5.21"` in de gradle `plugins` block. Die plugin genereert de nodige no-arg constructoren die JPA nodig heeft.
{{% /notice %}}

Het datatype kan ook worden ingesteld met `@Column` (merk op dat de kolomnaam van de tabel in de DB kan en mag wijzigen van de property name in Java), bijvoorbeeld voor temporele waardes waar enkel de tijd of datum wordt bijgehouden op DB niveau. Merk op dat `@Id` nodig is op een `@Entity` - zonder primary key kan JPA geen object persisteren. `@GeneratedValue` is er omdat wij niet telkens de ID willen verhogen, maar dat willen overlaten aan de database vanwege de `AUTOINCREMENT`. Bij elke `persist()` gaat Hibernate de juiste volgende ID ophalen, dat zich vertaalt in de volgende queries in sysout:

```sql
Hibernate: select next_val as id_val from hibernate_sequence
Hibernate: update hibernate_sequence set next_val= ? where next_val=?
Hibernate: insert into Student (goedBezig, naam, voornaam, studnr) values (?, ?, ?, ?)
```

De tabelnaam kan je wijzigen met de `@Table` annotatie op klasse niveau. 

#### Inserts/updates

Hoe bewaar ik een entity? ``entityManager.persist(object)``. That's it!

Hoe update ik een entity, als properties zijn gewijzigd? `.merge(object)`

Merk op dat in de Sysout output _geen query_ wordt gegenereerd. Hibernate houdt alles in zijn interne cache bij, en zal pas flushen naar de database wanneer hij acht dat dat nodig is. Dat kan je zelf triggeren door `entityManager.flush()` te gebruiken (kan alleen in een transactie) - of het commando te wrappen met een transactie:

<div class="devselect">

```kt
with(entityManager) {
    getTransaction().begin()
    persist(dingdong)
    getTransaction().commit()
}
```

```java
entityManager.getTransaction().begin();
entityManager.persist(dingdong);
entityManager.getTransaction().commit();
```
</div>

Zonder dit, en met herbruik van dezelfde entity manager in SQLite, is er een kans dat je `SELECT` query niets teruggeeft, omdat de `INSERT` nog niet werd geflushed. De interne werking van combinatie JDBC+SQLite+JPA+Hibernate is zeer complex en zou een cursus van 20 studiepunten vereisen... 

#### Queries

Hoe query ik in JPA? Dit kan op verschillende manieren. We beperken ons hier tot de **JPA Criteria API**. Een voorbeeld. Gegeven een huizenlijst, waarvan we huizen willen teruggeven die onder de 200.000 kosten. In SQL zou dit `SELECT * FROM huizen WHERE prijs < 200000` zijn. In Criteria API:

<div class="devselect">

```kt
val criteriaBuilder = entityManager.getCriteriaBuilder()
val query = criteriaBuilder.createQuery(Huis::class.java)
val root = query.from(Huis::class.java)

query.where(criteriaBuilder.equal(root.get<Int>("prijs"), 200000))
return entityManager.createQuery(query).getResultList()
```

```java
var criteriaBuilder = entityManager.getCriteriaBuilder();
var query = criteriaBuilder.createQuery(Huis.class);
var root = query.from(Huis.class);

query.where(criteriaBuilder.equal(root.get("prijs"), 200000));
return entityManager.createQuery(query).getResultList();
```
</div>

Voor simpele queries zoals deze is dat inderdaad omslachtig, maar de API is zeer krachtig en kan automatisch complexe queries genereren zonder dat wij ons moe moeten maken. Merk op dat wij _geen enkele letter SQL_ zelf schrijven. Alles is **java code**, wat het eenvoudig maakt om te refactoren, redesignen, statische code analyse op te doen, unit testen, ... Lees meer over criteria API:

- Voorbeeld 2 [Programmatic Criteria Queries](https://www.initgrep.com/posts/java/jpa/create-programmatic-queries-using-criteria-api)
- Voorbeeld 3 [TutorialsPoint Criteria API](https://www.tutorialspoint.com/jpa/jpa_criteria_api.htm)
- Voorbeeld 4 [Using "In" in Criteria API](https://www.baeldung.com/jpa-criteria-api-in-expressions)

Controleer in de sysout output welke query Hibernate uiteindelijk genereert. Dat ziet er zo uit:

```sql
select student0_.studnr as studnr1_0_, student0_.goedBezig as goedbezi2_0_, student0_.naam as naam3_0_, student0_.voornaam as voornaam4_0_ from Student student0_ where student0_.naam=?
```

### 2.2.3 Oefeningen

**Quickstart project**: `examples/hibernate-jpa-start` in de [cursus repository](https://github.com/kuleuven-Diepenbeek/db-course) ([download repo zip](https://github.com/KULeuven-Diepenbeek/db-course/archive/refs/heads/main.zip))

1. Het is opnieuw tijd voor onze studentendatabase. Open of kopiëer het project van [1. jdbc en jdbi](/apis/jdbc-jdbi/) (of start met het quickstart project). We voorzien een **derde implementatie** van de `StudentRepository` interface, naast de JDBC en Jdbi3 versies: `StudentRepositoryJpaImpl`. Probeer een nieuwe student te bewaren én daarna dezelfde student op te halen en af te drukken in sysout. Wat heb je daar weer voor nodig? (Scroll up!)
    - Een configuratiebestand in `resources/META-INF`.
    - Entities, zoals onze `Student` klasse.
    - Bootstrappen van de `EntityManager`.
    - Een repository implementatie die de `EntityManager` gebruikt (tip: _constructor injectie!_)
2. Modelleer ook de log tabel uit [1. Failures/rollbacks](/transacties/failures-rollbacks). Bewaar een log record bij elke wijziging van student (aanmaken, wijzigen). Vergeet geen `<class/>` entry toe te voegen in je `persistence.xml`! Anders krijg je de volgende fout: 

```
Exception in thread "main" java.lang.IllegalArgumentException: Not an entity: class be.kuleuven.studenthibernate.domain.Student
    at org.hibernate.metamodel.internal.MetamodelImpl.entity(MetamodelImpl.java:566)
    at org.hibernate.query.criteria.internal.QueryStructure.from(QueryStructure.java:127)
    at org.hibernate.query.criteria.internal.CriteriaQueryImpl.from(CriteriaQueryImpl.java:158)
```


### 2.2.4 JDBC VS Jdbi3 VS JPA

- JDBC: Low-level relationele mapping tussen Java en DB. Wordt door alle high-level API's gebruikt. Omslachtig (checked Exceptions, beperkte interface), erg kort bij SQL. Functionaliteit: minimaal. 
- Jdbi3: High-level relationele mapping no-nonsense API bovenop JDBC die in feite alle negatieve kenmerken van JDBC wegwerkt. Nog steeds kort bij SQL, maar meer object-friendly door Fluent/Declarative API. Functionaliteit: nog steeds basic, maar wel met gebruiksgemak. 
- JPA en vendors: High-level object-relational mapper bovenop JDBC waarbij entities centraal staan en compatibiliteit met Java EE is ingebouwd. Genereert SQL  (zelf raw queries schrijven kan nog steeds, maar wordt afgeraden). Functionaliteit: enorm uitgebreid. 

Kies altijd **bewust** voor één bepaald framework in plaats van "random" of "uit ervaring": JPA/Hibernate is vaak overkill voor simpele applicaties, JDBC is vaak té low-level en bevat veel boilerplating, terwijl Jdbi3 daartussen ligt. 

Zijn er nog alternatieven? Uiteraard, en meer dan één... Maar dat reikt buiten de scope van deze cursus. 

## 2.3 Many-to-one relaties in Hibernate/JPA

De grootste kracht van JPA hebben we nog steeds niet gezien: in plaats van één of meerdere entiteiten _onafhankelijk_ te mappen, kunnen we ook **relaties mappen** en Hibernate alles op SQL-niveau laten afhandelen. Nooit meer `JOIN` statements schrijven, hoera!... Niet altijd. Soms wil je de queries anders aanpakken dan Hibernate ze genereert omwille van **performantie** redenen. In dat geval zal je nog steeds moeten teruggrijpen naar _native SQL_ - of veel tijd investeren in de correcte configuratie van de Hibernate/JPA/JDBC installatie. 

Een concreet voorbeeld. Een `Docent` entiteit geeft les aan verschillende studenten:

<div class="devselect">

```kt
@Entity
data class Docent(
    @Id @GeneratedValue var docentennummer: Int,
    @Column var naam: String,
    @OneToMany(mappedBy = "docent") var studenten: List<Student>
)
```

```java
@Entity
public class Docent {
    @Id
    @GeneratedValue
    private int docentennummer;

    @Column
    private String naam;

    @OneToMany(mappedBy = "docent")
    private List<Student> studenten;
}
```
</div>

De `@OneToMany` annotatie zorgt voor de link tussen de studenten- en docententabel: het is wel nog nodig om een omgekeerde mapping property genaamd `docent` toe te voegen op de `Student` klasse: 

<div class="devselect">

```kt
@Entity
data class Student(
    // ...
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "docent_nr") var docent: Docent?
    )
```

```java
@Entity
public class Student {
    // ...
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "docent_nr")
    private Docent docent;
}
```
</div>

Op die manier kan je van docenten onmiddellijk zien aan wie ze lesgeven (one to many), en van een student van wie hij of zij les krijgt (many to one). De data wordt in de studententabel bewaard, in kolom `docent_nr`. Andere configuraties zoals tussentabellen zijn uiteraard ook mogelijk. 

Persisteren we nu een nieuwe docent waar een reeds bewaarde student in de lijst werd toegevoegd:

<div class="devselect">

```kt
private fun docentenTest(entityManager: EntityManager, student: Student) {
    val wouter: Docent("Wouter").apply {
        geefLesAan(student)
    }

    with(entityManager) {
        begin()
        persist(wouter)
        commit()
    }
}
```

```java
private static void docentenTest(EntityManager entityManager, Student student) {
    var wouter = new Docent("Wouter");
    wouter.geefLesAan(student);

    entityManager.getTransaction().begin();
    entityManager.persist(wouter);
    entityManager.getTransaction().commit();
}
```
</div>

Geeft in Hibernate:

```sql
Hibernate: select next_val as id_val from hibernate_sequence
Hibernate: update hibernate_sequence set next_val= ? where next_val=?
Hibernate: insert into Docent (naam, docentnummer) values (?, ?)
```

SQLite browser:

![](/img/student-nodocent.jpg)

Waar is onze `docent_nr` data? De methode `geefLesAan` voegt enkel toe aan de studentenlijst, maar de relatie moet **voor beide entities** kloppen:

<div class="devselect">

```kt
fun geefLesAan(student: Student) {
    studenten.add(student)
    student.docent = this
}
```

```java
public void geefLesAan(Student student) {
    studenten.add(student);
    student.setDocent(this);
}
```
</div>

Zonder de tweede regel wordt de kolom niet ingevuld. Geeft nu:

```sql
Hibernate: select next_val as id_val from hibernate_sequence
Hibernate: update hibernate_sequence set next_val= ? where next_val=?
Hibernate: insert into Docent (naam, docentnummer) values (?, ?)
Hibernate: update Student set docent_nr=?, goedBezig=?, naam=?, voornaam=? where studnr=?
```

Bingo, een `UPDATE` statement in de studententabel. SQLite Browser:

![](/img/student-docent.jpg)

### 2.3.1 Oefeningen

1. Implementeer de `Docent` klasse zoals hierboven. 
    - Wat gebeurt er als je studenten opvraagt? 
    - Wat gebeurt er als je een docent van een student opvraagt? De `FetchType.LAZY` schiet in gang. Verander dit naar `EAGER`. Kijk in de Hibernate output wat er verandert op SQL niveau en verifiëer dat er nu een `LEFT OUTER JOIN` in de `SELECT` van de student staat. 

Pas op het moment dat de data van de docent effectief nodig is, zoals bij het afdrukken, wordt een `SELECT` query gelanceerd bij lazy-loading. Bijvoorbeeld:

<div class="devselect">

```kt
val student = entityManager.find(Student::class.java, jos.studentennummer)
println("student ${student.naam}");
println(" -- heeft als docent ${student.docent.naam}")
```

```java
var student = entityManager.find(Student.class, jos.getStudentenNummer());
System.out.println("student " + student.getNaam());
System.out.println(" -- heeft als docent " + student.getDocent().getNaam());
```
</div>

Geeft als sysout output:

```
Hibernate: select student0_.studnr as studnr1_1_0_, student0_.docent_nr as docent_n5_1_0_, student0_.goedBezig as goedbezi2_1_0_, student0_.naam as naam3_1_0_, student0_.voornaam as voornaam4_1_0_ from Student student0_ where student0_.studnr=?
student Lowiemans
Hibernate: select docent0_.docentnummer as docentnu1_0_0_, docent0_.naam as naam2_0_0_ from Docent docent0_ where docent0_.docentnummer=?
 -- heeft als docent Wouter
```

Merk op dat de eerste `System.out.println` vóór de docenten `SELECT` query komt. Een eager-loaded docent geeft andere output:

```
Hibernate: select student0_.studnr as studnr1_1_0_, student0_.docent_nr as docent_n5_1_0_, student0_.goedBezig as goedbezi2_1_0_, student0_.naam as naam3_1_0_, student0_.voornaam as voornaam4_1_0_, docent1_.docentnummer as docentnu1_0_1_, docent1_.naam as naam2_0_1_ from Student student0_ left outer join Docent docent1_ on student0_.docent_nr=docent1_.docentnummer where student0_.studnr=?
student Lowiemans
 -- heeft als docent Wouter
```

**Tip**: `entityManager.clear()` of `close()` een nieuwe aanmaken kan helpen om de persistence context te flushen ter test. 

