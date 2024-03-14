---
title: "Project 2023-2024"
draft: true
---

Het project voor dit vak bestaat uit **drie delen** met elk hun aparte deadline. Op die manier is er ruimte voor tussentijdse feedback. Hieronder volgt een overzicht van de delen, die in de volgende secties zijn beschreven:

| deel | opdracht			  | deadline    |
|------|----------------------|-------------|
| 1.   | domein model 	      | 03/11/2023  |
| 2.   | SQLite database      | 01/12/2023  |
| 3.   | Uitwerking in Java/Kotlin      | een week voor het examen (TBD) | 

Het is opnieuw een **groepsproject** waarbij je samenwerkt per 2 of per 3. 

**Indienen** doe je gewoon door je **Git** (!) project te pushen naar jullie repository en een mail te sturen naar het docententeam met link. Uploaden naar Toledo is dus _niet_ nodig (je kan daar eventueel de link naar de repository delen). De opdrachten die daar officiëel geplaatst worden zijn dezelfde delen als hieronder. 

## De opdracht


Het [Video Game History Foundation](https://gamehistory.org/) (VGHF) is een non-profit organisatie die is toegewijd aan het preserveren van video game gerelateerd materiaal: dit gaat van de games zelf (in cartridge, CD, digitaal formaat, ...) tot memorabilia gelinkt aan de cultuur van video games (magazines, props, guides, ...). 

Buiten het bewaren van de games en hun cultuur wilt het VGHF ook de huidige en volgende generatie onderwijzen in de rijke geschiedenis van games. Dat doen ze door verschillende musea te openen, waar je in ruil voor een kleine bijdrage kan genieten van oude en nieuwe opgestelde media. 

Het VGHF is op zoek naar software om de organisatie en het beheer van hun collectie en musea te vergemakkelijken. Het is momenteel bijna onmogelijk om bijvoorbeeld te weten welke SEGA MegaDrive kopie van [Sonic The Hedgehog 3](https://en.wikipedia.org/wiki/Sonic_the_Hedgehog_3) uitgeleend is aan welk museum, en in welk warenhuis de andere kopieën zijn opgeslagen. Bijkomend wenst het VGHF een betere kijk te hebben op hun gegevens:

- hoeveel bezoekers komen per jaar kijken in welk museum;
- wat brengt dat op en welke donaties zijn ontvangen;
- welke dubbele kopieën zijn verkocht aan privaat collectors;
- welke genres of video game consoles worden het meeste uitgeleend;
- ...

<img src="/img/vghf.jpg" style="max-width: 60%" />

{{% notice note %}}
Bekijk de VGHF website eens en maak je bekend met [hun projecten](https://gamehistory.org/what-were-doing/) (research library, source code preservation, public education, archival collections, recovery & restoration, ...). Focus je op één van deze projecten bij het opstellen van het model en de software: je mag de richting zelf bepalen. 
{{% /notice %}}

### Deel 1

Deadline: zie boven

Voor dit deel verwachten we dat je een **domein model** [DDL schema](/sql-ddl-dml/ddl/) tekent en indient. Dit mag (ingescand) op papier, met [Mermaid](https://mermaid-js.github.io/mermaid/#/) of de [Mermaid Live Editor](https://mermaid.live/edi) zoals in deze cursus, of met [draw.io](https://draw.io) gemaakt worden. Het schema bevat (1) entiteiten/modellen en (2) relaties tussen deze modellen, zoals jij de opdracht interpreteert. 
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

Eventuele koppeltabellen moeten niet vermeld worden maar ga je in fase 2 wel nodig hebben. Dit zijn technische details. Het schema zou bespreekbaar moeten zijn met eender welk lid van de Video Game History Foundation, ook met mensen zonder een technische achtergrond.

Naast het schema dien je ook een **verslag** in---van ongeveer 1000 woorden---waarin je je schema beschrijft én verklaart waarom je denkt dat bepaalde relaties nodig zijn. Het formaat van het verslag is [plaintext Markdown](https://docs.github.com/en/get-started/writing-on-github/getting-started-with-writing-and-formatting-on-github/basic-writing-and-formatting-syntax): commit dit onder de root van je project in `README.md`.

Indienen doe je via Git; voeg alle nodige bestanden (schema én verslag) toe aan je repository. Zie boven.

### Deel 2

Deadline: zie boven

Voor dit deel verwachten we dat je een uitgewerkte SQLite database bestand aanlevert waarin het **technisch ontwerp van de database** zichtbaar is---dus inclusief de eventuele koppeltabellen. Dit is een `.sqlite` bestand dat kan geopend worden met [SQLitebrowser](https://sqlitebrowser.org/).

Minimum vereisten:

- Uitwerking van het hele schema uit deel 1 in tabellen
- Minstens 2 constraints gebruiken
- Alle tabellen zijn voorzien van de nodige keys én constraints
- Voeg een beperkte set van (fake) gegevens toe in de databases (bijvoorbeeld een drietal per tabel)

Denk ook goed na over eventuele (foute) ingave van gegevens---welke soort data verwacht je waar? 

Indienen doe je via Git; voeg alle nodige bestanden toe aan je repository. Zie boven.

### Deel 3

Deadline: zie boven

Voor dit deel verwachten we dat je het ontwerp van de database integreert in een **softwareprogramma**, uitgewerkt in Kotlin of Java naar keuze. Er zal een startproject voorzien worden, de UI wordt ontwikkeld in JavaFX (dit staat vast---niet om vervelend te doen, maar om het jullie makkelijker te maken!). 

Minimum vereisten:

- Gebruik net zoals in het eerstejaars INF1 vak [OpenJavaFX](https://openjfx.io/) als UI.
- Gebruik als toegang naar de SQLite-embedded database JDBC, JDBI, of JPA/Hibernate. Zie [hoofdstuk 4: DB APIs](/apis/).
- Dit project wordt _mondeling verdedigd_ in de week van het examen. We verwachten dat je de keuze van JDBC/JDBI/JPA goed kan motiveren!

Starten kan vanaf het JavaFX-enabled startproject: https://github.com/KULeuven-Diepenbeek/db-course/tree/main/examples/java/project-template

Merk op dat het over het vak "databases" gaat, en we dus NIET de nadruk leggen op de UI, maar eerder op de backend. Denk echter wel opnieuw goed na over de invoer van gegevens, en vergeet de software engineering lessen niet: we verwachten duidelijke, propere code die herbruikt kan worden. 

Indienen doe je via Git; voeg alle nodige bestanden toe aan je repository. Zie boven.


