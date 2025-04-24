---
title: VERPLICHTE opdracht
draft: false
weight: 6
---

## Opdracht JDBC en JDBI met als deadline vrijdag 2 mei 2025 23u59

Voor de verplichte opdracht meld je je aan onder de correcte naam bij volgende [Github Classroom](https://classroom.github.com/classrooms/58585791-kuleuven-diepenbeek-databases-2425). En pull je de repository van [**Opdracht rond Database API's**](https://classroom.github.com/a/3hq1u2IC). Deze repository bevat een Java Gradle project met een aantal `TODO`'s die je moet oplossen. Hieronder staat de opdracht nog beschreven:

### Opdracht: tennisspelers, tornooien en wedstrijden

Voor de verplichte opdrachten rond Database API's in Java (met Gradle) gaan jullie een aantal TODO's in dit project moeten oplossen. De probleemstelling is een zeer beperkte versie van de [casus](/apis). De beperkte versie werkt als volgt:
- Je hebt enkel Spelers, Tornooien en Wedstrijden
- Een Speler heeft een unieke tennisvlaanderenId, een naam en een aantal punten.
- Een Tornooi heeft enkel een id en een naam van de tennisclub die dat tornooi organiseert.
- Een Wedstrijd bevat 2 spelers, 1 winnaar, de tornooiId, een score string en een finale nummer
  - De finale nummer geeft aan in de hoeveelste ronde de wedstrijd gespeeld werd: 1 = finale, 2 = halve finale, 4 = kwart finale ...
- Je kan je als Speler ook inschrijven voor een Tornooi

Het EER-schema van het project zie je hieronder:

![EER opdracht api](/img/eer_opdracht_api.png)

In het project zijn alle klassen die je nodig hebt al aangemaakt op de correcte manier. Het enige wat je nog moet doen is de TODO's oplossen bij de `SpelerRespositoryJDBCimpl`-klasse en de `SpelerRespositoryJDBIimpl`-klasse. Waarbij je natuurlijk de juiste technologieën toepast. Er is geen App.java code, je kan jezelf wel testen door de Testen die toegevoegd zijn te runnen. Je mag extra testen schrijven als die nodig is. (De testen werken met een in memory database met SQLite, maar dit zou geen probleem mogen geven bij jullie implementatie)

**BELANGRIJK** wanneer je SQL specifieke imports moet doen, gebruik dan de generieke `java.sql` imports en geen `com.mysql` imports aangezien de testen dan niet zullen werken !!!

**Push voor de deadline van vrijdag 2 mei 2025 23u59 je oplossingen naar je repository**

Veel succes! Je mag me altijd contacteren via [arne.duyver@kuleuven.be](mailto::arne.duyver@kuleuven.be) voor vragen of in de les aanspreken.

Er is ook een video met deze uitleg voorzien die je op [Toledo](https://toledo.kuleuven.be) bij de opdracht kan terugvinden.


## Opdracht JPA met als deadline vrijdag 2 mei 2025 23u59
Vul het bovenstaande project verder aan met een werkende `SpelerRespositoryJPAimpl`-klasse en bijhorende dependencies en `persistence.xml`-file op de correcte manier. De testen bijhorend bij dit deel volgen ASAP.

**Denk eraan dat je in de `persistence.xml` die bij de testen hoort ook de juiste DATABASE url gebruikt**

# BELANGRIJKE VERBETERINGEN en EXTRA TIPS

Tijdens vorige les kwamen er nog een aantal fouten/moeilijkheden naar boven. Daarom kan je hieronder de verbeterde files terugvinden die je kan/MOET **verwisselen** met je eigen klassen. Per klasse staat er ook bij wat juist aangepast is. 

Aangezien de opdrachtenlast samen met de andere vakken wat druk is (en aangezien de decoratoren iets complexer zijn dan wat we in de les gezien hebben), krijg je voor het deel van JPA al de decorators gegeven voor de volledige `Tornooi` en `Wedstrijd` klassen. Ook in de `Speler` klasse werden de decorators die je nodig hebt voor de relaties al toegevoegd. **Je moet in `Speler` dus enkel nog de basis decoratoren voor JPA toevoegen.**

VERBETERDE KLASSEN (rechterklik op de naam en kies 'save link as ...' om de file te downloaden): 
- [build.gradle](/files/dab-opdracht-api/build.gradle): Hier werden de correcte dependecies voor JPA toegevoegd.
Voor de main folder:
- [Speler](/files/dab-opdracht-api/Speler.java): Hier zijn `decorators` toegevoegd voor de relaties in JPA, de `setTennisvlaanderenId`-methode werd toegevoegd. `ArrayLists` werden naar `Lists` omgevormd in de type vermelding van de datamembers om compatibel te zijn met JPA/Hibernate. De spelfout in `getTennisvlaanderenid` is verbeterd naar `getTennisvlaanderenId` (Let op dit kan in je eigen code errors geven die je dan simpel kan oplossen)
- [Wedstrijd](/files/dab-opdracht-api/Wedstrijd.java): Hier zijn `decorators` toegevoegd voor JPA, de `setId`-methode werd toegevoegd.
- [Tornooi](/files/dab-opdracht-api/Tornooi.java): Hier zijn `decorators` toegevoegd voor JPA, en werd een naamconventie correct van snakecase naar camelcase omgevormd. De `setId`-methode werd toegevoegd. `ArrayLists` werden naar `Lists` omgevormd in de type vermelding van de datamembers om compatibel te zijn met JPA/Hibernate.
- [SpelerRepository](/files/dab-opdracht-api/SpelerRepository.java): Hier werden de parameters van de methoden `addSpelerToTornooi` en `removeSpelerFromTornooi` correct aangepast zodat je ook een `tennisvlaanderenId` meegeeft.
- **NIEUW:** [SpelerRepositoryJPAimpl](/files/dab-opdracht-api/SpelerRepositoryJPAimpl.java): Template file voor JPA implementatie, met al oplossing voor  `addSpelerToTornooi` en `removeSpelerFromTornooi`. (want anders dan in de les gezien)
- [initTableWithDummyData.sql](/files/dab-opdracht-api/main/initTableWithDummyData.sql): `NOT NULL` werd weggehaald bij Wedstrijd voor speler1 en speler2 dit overcompliceerde de JPA implementatie **LET OP: deze file heeft dezelfde naam als de SQL file in de test folder maar is niet dezelfde!!!**

Voor de test folder:
- [SpelerRepositoryTest](/files/dab-opdracht-api/SpelerRepositoryTest.java): De test `whenGetAllSpelers_assertThat8correctSpelersPresent` werd aangepast om compatibel te zijn met JDBI en JPA. De database url werd aangepast van "jdbc:sqlite::memory:" naar "jdbc:sqlite:testdatabase.db" om compatibel te zijn met JDBI en JPA (**Let erbij op dat je dezelfde url gebruikt in je `persistence.xml`-file**). De laatste 2 testen voor `addSpelerToTornooi` en `removeSpelerFromTornooi` zijn nu niet meer leeg.
- **NIEUW:** [SpelerRepositoryJPAimplTest](/files/dab-opdracht-api/SpelerRepositoryJPAimplTest.java): Testfile voor JPA, toe te voegen in dezelfde folder als de andere testen.
- [initTableWithDummyData.sql](/files/dab-opdracht-api/test/initTableWithDummyData.sql): `NOT NULL` werd weggehaald bij Wedstrijd voor speler1 en speler2 dit overcompliceerde de JPA implementatie **LET OP: deze file heeft dezelfde naam als de SQL file in de main folder maar is niet dezelfde!!!**


**_Vergeet niet dat je zelf nog wel de juiste `persistence.xml`-files moet toevoegen!_**