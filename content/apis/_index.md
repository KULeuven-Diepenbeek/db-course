---
title: "Database APIs"
chapter: false
draft: false
weight: 4
---

## Casus: Tornooisysteem Tennis

Tennis Vlaanderen organiseert tennistornooien over heel Vlaanderen. Deze casus werkt een voorbeeld uit van een soort systeembeheer voor die tornooien. Wat dat systeembeheer software pakket juist moet kunnen staat hieronder beschreven. (Dit is natuurlijk maar een beperkte versie van hoe zo een werkelijk systeem er zou uitzien bv. een datum en uur voor de wedstrijden ontbreken ...)

Met de software moet je tennisspelers kunnen aanmaken. Elke **tennisspeler** heeft een `globale ID` bij Tennis Vlaanderen die gebruikt wordt als identifier wanneer een persoon zich inschrijft voor een toernooi. Een speler moet op zijn Tennis Vlaanderen-dashboard een globaal overzicht kunnen opvragen van het aantal gespeelde **matchen**, het aantal gewonnen matchen, verloren matchen en de hoogst bereikte plaats in een toernooi (bv. Finalist T.C.Ham reeks enkel Heren t.e.m. 5 punten OF Winnaar G.T.Tessenderlo reeks enkel Dames t.e.m. 10 punten). 

Naast de fysieke parameters van een tennisspeler zoals `leeftijd`, `geslacht`, ..., moet de applicatie ook de `ranking` van elke tennisspeler bijhouden. Deze ranking is een waarde van 1 tot en met 10 waarbij 1 de laagste ranking is en 10 de hoogste.

Daarnaast kan een tennisspeler ook een rol als **wedstrijdleider** en/of **scheidsrechter** opnemen.
- Een wedstrijdleider regelt de planning van het toernooi, is het aanspreekpunt voor vragen
en doet op de wedstrijddagen de inschrijvingen van de spelers.
Aan elk toernooi moet minstens één wedstrijdleider gekoppeld zijn.
- Aan elke finalewedstrijd van een toernooi (= halve finales en grote + kleine finale) moet één
scheidsrechter toegewezen worden.

_Een wedstrijdleider en scheidsrechter moeten ook wel een tennisspeler zijn! Je kan niet wedstrijdleider en scheidsrechter tegelijk zijn_

Elk toernooi vindt plaats op een bepaalde **Tennisclub** (met een bepaald `adres`, `telefoonnummer(s)`, `email adres`, ...) en bestaat uit minstens 4 reeksen waarvoor je kan inschrijven ("Enkel Heren t.e.m. 5 punten", "enkel Heren t.e.m. 10 punten", "enkel Dames t.e.m. 5 punten", "enkel Dames t.e.m. 10 punten"). Een speler kan altijd voor een hogere reeks inschrijven maar niet voor een lagere (bv. speler met 3 punten kan inschrijven in de reeks t.e.m. 10 punten, maar een speler met 6 punten kan niet inschrijven voor de reeks t.e.m. 5 punten).

Een toernooi wordt georganiseerd volgens het principe van rechtstreekse uitschakeling: de winnaar gaat door naar de volgende ronde _(ronde wil zeggen: ronde 4 is 1/4 finale, ronde 16 is 1/16 finale en ronde 1 is finale ...)_; voor de verliezer eindigt het toernooi. Dit proces loopt door tot er per **wedstrijdreeks** één winnaar overblijft. Indien het aantal spelers in een wedstrijdreeks geen match van twee is, worden in de eerste ronde zoveel spelers vrijgesteld als nodig. Zij kwalificeren zich rechtstreeks voor de tweede ronde. Dit wordt
per loting bepaald (random dus).

### EER-schema

![dab_tennis_eer](/img/dab_tennis_eer.jpg)

### (My)SQL database <!-- (+sqlite) -->

<details open>
<summary><i><b>Klik hier om de mysql code te zien/verbergen om de schema's aan te maken voor de 'tennisvlaanderen' database </b></i>🔽</summary>
<p>

```sql
DROP TABLE IF EXISTS tennisspeler_speelt_wedstrijd;
DROP TABLE IF EXISTS tennisspeler_inschrijving_wedstrijdreeks;
DROP TABLE IF EXISTS wedstrijd;
DROP TABLE IF EXISTS wedstrijdreeks;
DROP TABLE IF EXISTS tornooi;
DROP TABLE IF EXISTS tennisspeler;
DROP TABLE IF EXISTS tennisclub_email;
DROP TABLE IF EXISTS tennisclub;

CREATE TABLE tennisclub (
    clubnummer INT PRIMARY KEY,
    nummer INT,
    straatnaam VARCHAR(255),
    gemeente VARCHAR(255)
);

CREATE TABLE tennisclub_email (
    id INT AUTO_INCREMENT PRIMARY KEY,
    clubnummer INT,
    email_address VARCHAR(255),
    FOREIGN KEY (clubnummer) REFERENCES tennisclub(clubnummer)
);

CREATE TABLE tennisspeler (
    tennisvlaanderenid INT AUTO_INCREMENT PRIMARY KEY,
    geslacht ENUM('M', 'V'),
    geboortedatum DATE,
    ranking INT,
    rol ENUM('Wedstrijdleider', 'Scheidsrechter') DEFAULT NULL,
    diploma TEXT,
	naam TEXT, 
    CHECK (ranking BETWEEN 0 AND 10)
);

CREATE TABLE tornooi (
    id INT AUTO_INCREMENT PRIMARY KEY,
    startdatum DATE,
    einddatum DATE,
    wedstrijdleider INT,
    tennisclub INT,
    FOREIGN KEY (wedstrijdleider) REFERENCES tennisspeler(tennisvlaanderenid),
    FOREIGN KEY (tennisclub) REFERENCES tennisclub(clubnummer)
);

CREATE TABLE wedstrijdreeks (
    id INT AUTO_INCREMENT PRIMARY KEY,
    max_punten INT,
    geslacht ENUM('M', 'V'),
    tornooi_id INT,
    FOREIGN KEY (tornooi_id) REFERENCES tornooi(id),
    CHECK (max_punten BETWEEN 0 AND 10)
);

CREATE TABLE wedstrijd (
    partial_id INT,
    wedstrijdreeks INT,
    scheidsrechter INT DEFAULT NULL,
    ronde INT,
    winnaar INT DEFAULT NULL,
    score TEXT DEFAULT NULL,
    PRIMARY KEY (partial_id, wedstrijdreeks),
    FOREIGN KEY (wedstrijdreeks) REFERENCES wedstrijdreeks(id),
    FOREIGN KEY (scheidsrechter) REFERENCES tennisspeler(tennisvlaanderenid),
    FOREIGN KEY (winnaar) REFERENCES tennisspeler(tennisvlaanderenid)
);

CREATE TABLE tennisspeler_inschrijving_wedstrijdreeks (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tennisspeler INT,
    wedstrijdreeks INT,
    inschrijvingsdatum DATE,
    FOREIGN KEY (tennisspeler) REFERENCES tennisspeler(tennisvlaanderenid),
    FOREIGN KEY (wedstrijdreeks) REFERENCES wedstrijdreeks(id)
);

CREATE TABLE tennisspeler_speelt_wedstrijd (
    id INT AUTO_INCREMENT PRIMARY KEY,
    speler1 INT,
    speler2 INT,
    partial_id INT,
    wedstrijdreeks INT,
    FOREIGN KEY (speler1) REFERENCES tennisspeler(tennisvlaanderenid),
    FOREIGN KEY (speler2) REFERENCES tennisspeler(tennisvlaanderenid),
    FOREIGN KEY (partial_id) REFERENCES wedstrijd(partial_id),
	FOREIGN KEY (wedstrijdreeks) REFERENCES wedstrijd(wedstrijdreeks)
);
```

</p>
</details>


<details open>
<summary><i><b>Klik hier om de mysql code te zien/verbergen om de 'tennisvlaanderen' database te in te vullen met wat test/dummy data </b></i>🔽</summary>
<p>

```sql
DELETE FROM tennisspeler_speelt_wedstrijd;
DELETE FROM tennisspeler_inschrijving_wedstrijdreeks;
DELETE FROM wedstrijd;
DELETE FROM wedstrijdreeks;
DELETE FROM tornooi;
DELETE FROM tennisspeler;
DELETE FROM tennisclub_email;
DELETE FROM tennisclub;

-- Insert tennis clubs
INSERT INTO tennisclub (clubnummer, nummer, straatnaam, gemeente)
VALUES
(1, 12, 'Tennis Straat', 'Antwerpen'),
(2, 5, 'Racketweg', 'Gent');

-- Insert club emails
INSERT INTO tennisclub_email (clubnummer, email_address)
VALUES
(1, 'club1@example.com'),
(1, 'contact@club1.com'),
(2, 'info@club2.be');

-- Insert tennis players
INSERT INTO tennisspeler (tennisvlaanderenid, geslacht, geboortedatum, ranking, rol, diploma, naam)
VALUES
(1, 'M', '1990-05-15', 5, 'Wedstrijdleider', 'Diploma X', 'Test Wedstrijdleider'),
(2, 'V', '1995-08-20', 8, NULL, NULL, 'Testspeler A'),
(3, 'M', '1985-03-10', 3, 'Scheidsrechter', 'Diploma Y', 'Test Scheidsrechter'),
(4, 'V', '2000-12-05', 2, NULL, NULL, 'Testspeler B'),
(5, 'M', '1996-02-18', 2, NULL, NULL, 'Testspeler C'),
(6, 'M', '1992-07-14', 7, NULL, NULL, 'Testspeler D'),
(7, 'V', '1998-11-23', 4, NULL, NULL, 'Testspeler E'),
(8, 'M', '1989-01-30', 5, NULL, NULL, 'Testspeler F'),
(9, 'V', '1993-04-18', 5, NULL, NULL, 'Testspeler G'),
(10, 'M', '1997-09-05', 3, NULL, NULL, 'Testspeler H'),
(11, 'V', '2001-06-12', 2, NULL, NULL, 'Testspeler I'),
(12, 'M', '1994-03-22', 8, NULL, NULL, 'Testspeler J'),
(13, 'V', '1990-12-01', 1, NULL, NULL, 'Testspeler K'),
(14, 'M', '1987-05-09', 4, NULL, NULL, 'Testspeler L'),
(15, 'V', '1999-08-15', 6, NULL, NULL, 'Testspeler M');

-- Insert tournaments
INSERT INTO tornooi (id, startdatum, einddatum, wedstrijdleider, tennisclub)
VALUES
(1, '2023-10-01', '2023-10-07', 1, 1),
(2, '2023-11-01', '2023-11-15', 1, 2),
(3, '2024-03-05', '2024-03-12', NULL, 2);

-- Insert competition series
INSERT INTO wedstrijdreeks (id, max_punten, geslacht, tornooi_id)
VALUES
(1, 5, 'M', 1),
(2, 5, 'V', 1),
(3, 10, 'M', 1),
(4, 10, 'V', 1),
(5, 5, 'M', 2),
(7, 10, 'M', 2),
(8, 10, 'V', 2);

-- Insert player registrations
INSERT INTO tennisspeler_inschrijving_wedstrijdreeks (tennisspeler, wedstrijdreeks, inschrijvingsdatum)
VALUES
(5, 1, '2023-09-20'),
(8, 1, '2023-09-20'),
(14, 1, '2023-09-21'),
(10, 1, '2023-09-21'),
(4, 2, '2023-09-19'),
(11, 2, '2023-09-20'),
(13, 2, '2023-09-20'),
(9, 2, '2023-09-21'),
(7, 2, '2023-09-21'),
(6, 3, '2023-09-18'),
(8, 3, '2023-09-18'),
(12, 3, '2023-09-18'),
(1, 3, '2023-09-20'),
(3, 3, '2023-09-20'),
(10, 3, '2023-09-20'),
(5, 3, '2023-09-20'),
(14, 3, '2023-09-21'),
(2, 4, '2023-09-17'),
(9, 4, '2023-09-18'),
(15, 4, '2023-09-18'),
(7, 4, '2023-09-18'),
(11, 4, '2023-09-20'),
(6, 7, '2023-09-18'),
(8, 7, '2023-09-18'),
(12, 7, '2023-09-18');


-- Insert matches
INSERT INTO wedstrijd (partial_id, wedstrijdreeks, scheidsrechter, ronde, winnaar, score)
VALUES
(1, 1, NULL, 2, 5, '6-4, 6-2'),
(2, 1, NULL, 2, 14, '6-4, 5-7, 6-0'),
(3, 1, 3, 1, 14, '6-1, 6-4'),
(1, 2, NULL, 4, 4, '6-0, 6-1'),
(2, 2, NULL, 2, 9, '7-6(3), 6-4'),
(3, 2, NULL, 2, 7, '6-0, 6-0'),
(4, 2, 3, 1, 9, '6-2, 7-5'),
(1, 3, NULL, 4, 1, '6-2, 6-2'),
(2, 3, NULL, 4, 1, '5-7, 6-2, 6-0'),
(3, 3, NULL, 4, 1, '6-3, 6-2'),
(4, 3, NULL, 4, 1, '7-5, 6-4'),
(5, 3, NULL, 2, 1, '6-2, 6-3'),
(6, 3, NULL, 2, 1, '6-2, 6-4'),
(7, 3, 3, 1, 1, '6-4, 2-6, 7-5'),
(1, 4, NULL, 4, 7, 'walkover'),
(2, 4, NULL, 2, NULL, NULL),
(3, 4, NULL, 2, NULL, NULL),
(1, 7, NULL, 2, NULL, NULL),
(2, 7, NULL, 1, NULL, NULL);


-- Insert player-match participation
INSERT INTO tennisspeler_speelt_wedstrijd (speler1, speler2, partial_id, wedstrijdreeks)
VALUES
(5, 10, 1, 1),
(8, 14, 2, 1),
(5, 14, 3, 1),
(4, 11, 1, 2),
(4, 9, 2, 2),
(13, 7, 3, 2),
(7, 9, 4, 2),
(1, 3, 1, 3),
(6, 5, 2, 3),
(14, 8, 3, 3),
(10, 12, 4, 3),
(1, 6, 5, 3),
(8, 12, 6, 3),
(6, 12, 7, 3),
(7, 11, 1, 4),
(7, 2, 2, 4),
(15, 9, 3, 4),
(6, 8, 1, 7),
(12, NULL, 2, 7);
```

</p>
</details>

{{% notice info %}}
Je kan bovenstaande code kopiëren en opslaan onder filenames zoals `create_tables.sql` en `populate_tables_with_testdata.sql`. Door deze onder `./resources`-directory van Gradle projecten bijvoorbeeld op te slaan zal het heel simpel zijn om tijdens de oefeningen steeds snel en repeatable testdatabases aan te maken die we kunnen gebruiken om onze code te testen.
{{% /notice %}}

### De dummy data
Enkele opmerkingen bij de dummy data die weergeeft welke verschillende scenario's allemaal ingevuld zijn die dan in software getest kunnen worden:
- Tornooi 1, wedstrijdreeks M5 heeft 4 inschrijvingen (dus perfecte halve finales) en is volledig uitgespeeld.
- Tornooi 1, wedstrijdreeks V5 heeft 5 inschrijvingen (dus gedeeltelijk kwartfinale) en is volledig uitgespeeld.
- Tornooi 1, wedstrijdreeks M10 heeft 8 inschrijvingen (dus perfecte kwart finales) en is volledig uitgespeeld.
  - Speler met id 5, doet ook al in tornooi 1 reeks 1 mee. (net als een heel deel andere spelers)
- Tornooi 1, wedstrijdreeks V10 5 inschrijvingen (dus gedeeltelijk kwartfinale) en is gedeeltelijk al gespeeld maar is nog niet volledig uitgespeeld.
  - bevat een 'walkover' als resultaat. (iemand die opgeeft)
- Tornooi 2, wedstrijdreeks M5 heeft geen inschrijvingen.
- Tornooi 2, wedstrijdreeks V5 bestaat (nog) niet.
- Tornooi 2, wedstrijdreeks M10 heeft inschrijvingen en wedstrijden gepland, maar er zijn nog geen wedstrijden gespeeld.
- Tornooi 2, wedstrijdreeks V10 heeft inschrijvingen maar er zijn nog geen wedstrijden gepland.
- Tornooi 3, wat ook een tornooi van clubnummer 1 is bevat nog geen inschrijvingen, en heeft nog geen wedstrijdleider.

## Nadenken over de structuur van de database en de interactie met je applicatie
Je zal wellicht al gemerkt hebben dat de inhoud van de database niet al te leesbaar is. Zo zie je voor matchen enkel `tennisvlaanderenid`'s wat natuurlijk super onhandig is. Als ik mijn wedstrijd opzoek wil ik de naam van mijn tegenstander zien en niet enkel zijn/haar nummer. Daarvoor dienen dus goede SQL-queries gecombineerd met wat programmeermagic om dit zo weer te geven in de UI.

Bovendien missen er ook nog wat logische elementen die je niet rechtstreeks uit de database kan uitlezen maar die je er wel kan van afleiden. Zo weet je dat als er een wedstrijd van een speler voor een bepaalde reeks te zien is in de `wedstrijd`-tabel maar de winnaar nog `NULL` is, dat die wedstrijd dan wel ingepland is, maar nog niet gespeeld is. 

## Waarom is het nog belangrijk om na te denken over eigen software?
Sommige constraints kunnen niet toegevoegd worden in de definitie van de MySQL-database bijvoorbeeld:
- zorgen dat een speler met meer dan 5 punten niet kan inschrijven in een reeks tot en met 5 punten;
- zorgen dat een man niet kan inschrijven in een vrouwenreeks en vice versa;
- zorgen dat een scheidsrechter niet zijn eigen match kan beoordelen;
- ...

Hoe bouw je dit nu in? Dit kan over het algemeen op 2 manieren: **voorkomen** of **error messaging**:
- Je kan simpelweg voorkomen dat een vrouw in een mannenreeks kan inschrijven door het onmogelijk te maken in de UI dat een vrouw een inschrijvingsknop heeft voor een mannenreeks.
- Je kan een error geven wanneer je bijvoorbeeld voor een te lage reeks wil inschrijven.

{{% notice important %}}
Over het algemeen is het dus belangrijk dat je op zoveel mogelijk plaatsen en in zoveel mogelijk lagen restricties controleert zodat er geen fouten kunnen gebeuren. Dat doe je door constraints mee te geven in je database, error handling te doen, je UI zo te voorzien dat fouten maken bijna onmogelijk wordt ... Dit vraagt natuurlijk heel wat denkwerk voor programmeur, maar dit hoort zeker in deze gevallen tot zijn/haar job. Een werkende database en UI voorzien kan 20% van het werk zijn en ervoor zorgen dat je enkel de juiste dingen kan doen in de UI zal wellicht uit 80% van het werk in beslag nemen. (Daarom is het ook belangrijk om goede en voldoende testen te schrijven!)
{{% /notice %}}

## Tips

Test queries die je wil gebruiken in je programma eerst uit in een GUI tool zoals PHPMyAdmin of Adminer om te testen of ze werken en al een idee te hebben van hoe de output eruit ziet zodat je de returns correct kan processen.