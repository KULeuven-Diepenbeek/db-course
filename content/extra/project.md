---
title: "Project opdracht"
---

Het project voor dit vak bestaat uit **drie delen** met elk hun aparte deadline. Op die manier is er ruimte voor tussentijdse feedback. Hieronder volgt een overzicht van de delen, die in de volgende secties zijn beschreven:

| deel | opdracht			  | deadline    |
|------|----------------------|-------------|
| 1.   | domein model 	      | 17/10/2022  |
| 2.   | SQLite database      | 14/11/2022  |
| 3.   | Java uitwerking      | een week voor het examen (TBD) | 

Het is opnieuw een **groepsproject** waarbij je samenwerkt per 2. 

**Indienen** doe je gewoon door je Git project te pushen naar jullie repository en een mail te sturen naar het docententeam met link. Uploaden naar Toledo is dus _niet_ nodig. De opdrachten die daar officiëel geplaatst worden zijn dezelfde delen als hieronder. 

## De opdracht

Het functioneel kader van de opdracht is als volgt:

Wij, de docenten van het vak databases, zijn ook lid van de sportorganisatie _De Vrolijke Zweters_, die verschillende loopwedstrijden organiseren over Vlaanderen. We zijn op zoek naar bekwame programmeurs die ons kunnen helpen in het realiseren van software om de administratie van onze organisatie te ondersteunen.

We verwachten dat je met de software uiteraard lopers kan inschrijven, die een loopnummer krijgen toegekend dat uniek is per wedstrijd. Om de tijden van deze atleten te raadplegen moet het voor gebruikers mogelijk zijn om een klassement op te vragen: een running klassement per loopwedstrijd, én een algemeen klassement over alle wedstrijden heen. Daarnaast moet het ook mogelijk zijn om medewerkers/vrijwilligers op te kunnen vragen per wedstrijd, die elk een bepaalde functie hebben, zoals eerste hulp, bevoorraadingsdienst, masseur, enzovoort.

Uiteraard zijn er parameters voor een loper (zoals zijn fysieke status en gewicht, leeftijd, ...) en de loopwedstrijd. Elke wedstrijd is ingedeeld in een aantal etappes. Een parcours van bijvoorbeeld 20 kilometer kan bestaan uit 4 etappes waarvan eentje van kilometer 0 tot 4 met locatie x, en daarop volgend tot kilometer 6 met locatie y, enzovoort. We denken dat lopers graag hun tijden zowel per etappe raadplegen, als per wedstrijd en willen dit ook ondersteunen. 

Alvast bedankt voor de samenwerking en groetjes van _De Vrolijke Zweters_!

![](/img/zweters.jpg)

### Deel 1

Deadline: zie boven

Voor dit deel verwachten we dat je een **domein model** [DDL schema](/db-course/sql-ddl-dml/ddl/) tekent en indient. Dit mag (ingescand) op papier, met [Mermaid](https://mermaid-js.github.io/mermaid/#/) zoals in deze cursus, of met [draw.io](https://draw.io) gemaakt worden. Het schema bevat (1) entiteiten/modellen en (2) relaties tussen deze modellen, zoals jij de opdracht interpreteert. 
Het domein model bevat de entiteiten, maar ook de properties en het datatype. Bijvoorbeeld:

{{<mermaid align="left">}}
classDiagram
    class Book{
        isbn: string
        title: string
        author: string
        price: float
    }
{{< /mermaid >}}

Minimum vereisten: 

- Minstens 2 veel-op-veel relaties
- Minstens 2 1-op-veel of 0-op-veel relaties
- Minstens 4 blokken van entiteiten/modellen
- Ook properties van elk model uitwerken: wat zou interessant zijn voor elk model, en hoe kan je dit best mappen in de database? (Denk hierbij aan datatypes, kolommen of rijen, aparte tabellen of niet, ...)

Eventuele koppeltabellen moeten niet vermeld worden maar ga je in fase 2 wel nodig hebben. Dit zijn technische details. Het schema zou bespreekbaar moeten zijn met eender welk lid van _De Vrolijke Zweters_, ook met mensen zonder een technische achtergrond.

Naast het schema dien je ook een **verslag** in---van ongeveer 1000 woorden---waarin je je schema beschrijft én verklaart waarom je denkt dat bepaalde relaties nodig zijn. Het formaat van het verslag is [plaintext Markdown](https://docs.github.com/en/get-started/writing-on-github/getting-started-with-writing-and-formatting-on-github/basic-writing-and-formatting-syntax): commit dit onder de root van je project in `README.md`.

Indienen doe je via Git; voeg alle nodige bestanden (schema én verslag) toe aan je repository. Zie boven.

### Deel 2

Deadline: zie boven

Voor dit deel verwachten we dat je een uitgewerkte SQLite database bestand aanlevert waarin het **technisch ontwerp van de database** zichtbaar is---dus inclusief de eventuele koppeltabellen. Dit is een `.sqlite` bestand dat kan geopend worden met [SQLitebrowser](https://sqlitebrowser.org/).

Minimum vereisten:

- Uitwerking van het hele schema uit deel 1 in tabellen
- Minstens 2 constraints gebruiken
- Alle tabellen zijn voorzien van de nodige Primary en Foreign Keys
- Voeg een beperkte set van (fake) gegevens toe in de databases (bijvoorbeeld een drietal per tabel)

Denk ook goed na over eventuele (foute) ingave van gegevens---welke soort data verwacht je waar? 

Indienen doe je via Git; voeg alle nodige bestanden toe aan je repository. Zie boven.

### Deel 3

Deadline: zie boven

Voor dit deel verwachten we dat je het ontwerp van de database integreert in een **softwareprogramma**, uitgewerkt in Kotlin of Java naar keuze. Er zal een startproject voorzien worden, de UI wordt ontwikkeld in JavaFX (dit staat vast). 

Minimum vereisten:

- Start vanaf het JavaFX-enabled startproject in Kotlin of Java (dit volgt nog!).
- Gebruik als toegang naar de SQLite-embedded database JDBC, JDBI, of JPA/Hibernate.
- Dit project wordt _mondeling verdedigd_ in de week van het examen. We verwachten dat je de keuze van JDBC/JDBI/JPA goed kan motiveren!

Merk op dat het over het vak "databases" gaat, en we dus NIET de nadruk leggen op de UI, maar eerder op de backend. Denk echter wel opnieuw goed na over de invoer van gegevens. 

Indienen doe je via Git; voeg alle nodige bestanden toe aan je repository. Zie boven.
