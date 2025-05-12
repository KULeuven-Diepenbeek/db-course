---
title: 'Big Data Basics'
weight: 1
---


De **Big** in _Big Data_ mag je letterlijk nemen. IBM berekende onlangs dat wij allemaal 2.5 quintillion bytes aan data genereren. Elke minuut meer dan 350.000 tweets, 75.000 uren van Netflix video streams, meer dan 35.000 Apple store apps gedownload, enzovoort. 

De term _Big Data_ is al sinds eind de jaren negentig aan een opmars bezig. We kunnen "grote datasets" categoriseren afhankelijk van wat we noemen de **vijf Vs**:

1. **Volume**---Het gaat (uiteraard) over een alsmaar groeiend "groot volume" aan data;
2. **Velocity**---De snelheid waarmee de data in en uit systemem vloeit die altijd maar toeneemt;
3. **Variety**---De range aan data types breidt altijd maar uit (JSON, XML, RDBMS, noSQL, files, ...);
4. **Veracity**---Hoe waarheidsgetrouw is de data eigenlijk wel? Data vertoont alsmaar vaker inconsistenties/ambiguiteit;
5. **Value**---Wat heb je aan al die data zonder er iets waardevol mee te doen (zoals een crutiale business beslissing)?

![](/img/5vs.jpg "src: tistory.com")

Denk aan de biljoenen mensen die dagelijks Facebook gebruiken en gigantische volumes aan foto's en tekst uploaden. Het aantal gebruikers dat gestaag steeg (velocity) en de variëteit van data zoals APIs tussen Facebook en anderen om elders in te loggen met je account, video en fotomateriaal in plaats van enkel tekst, integratie met WhatsApp/Instagram, ... Maar hoeveel fake data zit er wel niet tussen, en hoeveel accounts worden misbruikt? Hoeveel data lekken en gevoelige data bevat het? (veracity). Vooral uw persoonsgegevens is voor Meta, het bedrijf achter Facebook, letterlijk goud waard, en verkoopt het door aan derden (value), vaak zonder jouw toestemming!

Nog een leuk voorbeeld: de winkel https://ebay.com/ heeft een data warehouse van 45 petabytes---ofwel 45.000 terabytes!

Uit de vorige hoofdstukken blijkt dat een traditioneel RDBMS systeem niet goed kan scalen (zie [nosql introductie](/nosql/basics)). Echter, een NoSQL DB is slechts één component in het gehele Big Data ecosysteem. In de praktijk gaat het over veel pools, clusters, servers, heterogene DB systemen, files, APIs, ... allemaal gecombineerd (zie [data warehousing lakes](/bigdata/datawarehousing)). Het beheren van een Big Data systeem gaat niet over één DB systeem, maar over het beheren van verschillende parameters (snelheid, grootte van input, connecties tussen systemen, ...). De analyse van de inhoud ervan is vaak een taak voor gespecialiseerde **data scientists**.

