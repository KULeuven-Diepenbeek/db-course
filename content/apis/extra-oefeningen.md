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
