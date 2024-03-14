---
title: 1. Transaction Mgmt. Basics
weight: 1
author: Wouter Groenveld
draft: false
---

SQL DBMS systemen zijn eerst en vooral **multi-user** systemen. Om zowel verschillende gebruikers te kunnen behandelen als nog steeds de **ACID regels** ondersteunen, is er een systeem nodig dat soms gebruikers "in wacht" zet. Stel je voor dat Jens en Jolien tegelijkertijd data lezen én updaten---in dezelfde tabel, hetzelfde record. Jens leest uit "de rekening staat op 100 EUR" en Jolien haalt er 10 EUR vanaf. Wie mag eerst? Kan dit tegelijkertijd? Jens krijgt te horen dat er 100 EUR op de rekening staat, terwijl in werkelijkheid dit 10 EUR minder is. 

## 0. Waarom transacties?

Heel simpel. Dit is het verkeer zonder transacties:

![](/slides/img/chaos.jpg)

Dit met:

![](/slides/img/order.jpg)


Om Atomicity, Consistency, Isolation, Durability te garanderen is er dus een **transactie manager** nodig die alles in goede banen leidt op het moment dat verschillende gebruikers data gaan raadplegen en/of manipuleren. Dit principe is ruwweg **hetzelfde als task management** van het vak [Besturingssystemen en C](https://kuleuven-diepenbeek.github.io/osc-course/ch6-tasks/)---maar dan op database-applicatie niveau.

## 1. Wat is een transactie?

Een transactie is een set van database operaties (bij relationele databases dus een aantal SQL operaties), dat door één gebruiker of applicatie als één **unit of work** aanzien wordt. Bijvoorbeeld, Jens wilt geld overschrijven van zijn rekening naar die van Jolien. Dit gaat meestal in verschillende stappen:

1. Haal `€10` van balans van Jens;
2. Stort `€10` op balans van Jolien.

{{<mermaid align="left">}}
graph LR;
    JN[Rekening van Jens]
    JL[Rekening van Jolien]
    JN -->|10 EUR| JL
{{< /mermaid >}}


Dit is één transactie---het zou nooit mogen gebeuren dat er `€10` verdwijnt van Jens' account zonder dat Jolien dat geld ontvangt. Met andere woorden, er kan niets tussen stap 1 en stap 2 komen: geen systeem crash, geen andere gebruiker (bijvoorbeeld Marianne die Jens `€20` voor zijn verjaardag stort op de rekening). Dit is één "unit of work": als er iets misloopt zou alles teruggedraaid moeten worden. Dus, een transactie heeft als **resultaat** ofwel **success**, ofwel **failure**, maar niets tussenin. Indien dit succesvol wordt afgerond zou het DBMS systeem moeten garanderen dat het geld effectief op Jolien's rekening staat. Indien het faalde zou het DBMS systeem moeten garanderen dat Jens zijn geld niet kwijt is. 

In de praktijk worden veel verschillende transacties tegelijkertijd uitgevoerd---eventueel door verschillende gebruikers. Er moet dus iemand zijn die dit beheert, en dat is de transactie manager. Wie beslist of Jens eerst zijn geld mag afhalen, of dat Marianne eerst zijn verjaardagscadeau mag overmaken op de rekening? Wie beslist dat tussen stap 1 en 2 bij de transfer van het geld van Jens naar Jolien niemand mag tussenkomen? Juist: de transactie manager. Hier zijn verschillende strategieën voor, zoals we later zullen zien in [Concurrency Control](/transacties/concurrency-control).

## 2. Het beheren van transacties

Formeel gezien wordt er een transactie **afgelijnd** door aan te kondigen wanneer een transactie begint en wanneer hij stopt, zodat de manager de juiste acties kan doorvoeren. De gebruiker kan met SQL ook zelf een transactie terugdraaien als er een programmafout voorkomt, bijvoorbeeld met een `try { ... } catch(Exception ex) { rollback }`. Meer hierover in sectie [failures/rollbacks](/transacties/failures-rollbacks/).

De transactie manager, die afgelijnde transacties ontvangt, kan dit dan in een **schedule** zetten, om te beslissen welke (delen van) transacties wanneer moeten worden uitgevoerd, net zoals Round Robin CPU scheduling algoritmes. Uiteindelijk wordt er een status toegekend aan een transactie:

1. Committed---het is gelukt en de data is permanent gepersisteerd.
2. Aborted---Een error tijdens het uitvoeren van de transactie.

Indien er halverwege de abort data is gewijzigd moet dit worden teruggezet, of worden **rollbacked**. Vorige versies van data moet dus ook worden bijgehouden. Jens' rekening kan bijvoorbeeld `€90` zijn initieel, hij haalt er `€10` af om over te maken wat dit tot `€80` maakt, maar er loopt iets mis: de rollback triggert het terugdraaien van het getal `80` naar de originele `90`. 

Dit is een pseudocode voorbeeld van bovenstaande afgelijnde transactie:

```
BEGIN TRANSACTION;
UPDATE account SET waarde = waarde - :over_te_maken_bedrag
WHERE eigenaar = 'Jens'

UPDATE account SET waarde = waarde + :over_te_maken_bedrag
WHERE eigenaar = 'Jolien'
COMMIT;
```

Transacties kosten CPU en RAM en zijn configureerbaar maar dus gelimiteerd in aantal. De manager kan worden ingesteld tot bijvoorbeeld ondersteunen van maximum 10 transacties tegelijkertijd. 

#### DBMS Componenten bij een transactie

Een voorbeeld van een transactie workflow (de nummers komen overeen met het schema):

![](/img/dbmscomponents.jpg "DBMS componenten aan het werk tijdens een transactie. src: pdbmbook.com")

1. De manager waarschuwt de **scheduler** dat er een nieuwe transactie moet worden ingepland. Deze optimaliseert dit naar throughput (zie BESC);
2. De **recovery** en **stored data** manager worden op de hoogte gebracht. Deze optimaliseert disk access: het zou kunnen dat DB reads/writes via een buffer verlopen omdat fysieke file operaties duur zijn;
3. De scheduler is nu klaar om **input** te ontvangen en uit te voeren in de juiste volgorde;
4. Dit triggert mogelijks interactie met de **stored data** manager (het effectief wegschrijven van wijzigingen);
5. De uitkomst wordt doorgegevan via een **output area**: ofwel is het gelukt (5b), ofwel is het mislukt (5a), waardoor de recovery manager zijn werk moet doen om dingen terug te draaien. 

## 3. Wat als er iet misgaat?

Hoe werkt dat blokje "recovery manager" precies? Een DBMS systeem gebruikt achterliggend een **logfile** als belangrijk element om eventuele recoveries te kunnen doorvoeren. Een logfile bevat in principe redundante data: in het beste geval wordt dit nooit gebruikt. Maar in het geval dat er ook maar iets misloopt is het van groot belang dat de log entries kunnen worden gebruikt om de data terug te zetten. 

Voor elke transactie en operatie wordt relevante informatie geregistreerd in de logfile als een **log record**. Deze bevat de volgende informatie: 

- Een unieke Log ID;
- Een unieke Transactie identifier om de records te kunnen koppelen aan de transacties;
- Een markering voor de start van de transactie + tijd + type (read/write/read-write combo) aan te duiden;
- Database record identifiers en operaties (select/insert/....) die bij de transactie horen;
- **before** images: een snapshot van de data voordat de transactie werd doorgevoerd. Deze worden gebruikt om een _undo_ uit te voeren;
- **after** images: een snapshot van de data nadat de transactie werd doorgevoerd. Deze worden gebruikt om een _redo_ uit te voeren, moest bijvoorbeeld een fysieke file write mislukken en hier een retry op worden toegepast. 

Er wordt altijd _eerst_ in de logfile geschreven: dit noemen we een **write-ahead log strategy**. Op die manier is de DBMS manager voorbereid op een mogelijke rollback. Alle updates worden dus eerst naar de logfile geschreven voordat er iets fysiek veranderd op de harde schijf. Merk op dat de "logFILE" ook (gedeeltelijk) in-memory kan zijn. 

Wat kan er zoal misgaan? Failures worden in drie groepen opgedeeld:

1. **Transaction failures**: fouten in de logica van de transactie, zoals verkeerde input, incorrecte variabelen, statements die niet kloppen, etc. Sommige applicaties vangen dit al op voordat het naar de DBMS gaat. 
2. **System failures**: DB of OS crashes op de server, bijvoorbeeld door stroomonderbrekingen of bugs in de database zelf. Het zou kunnen dat de DBMS buffer inhoud hierdoor leeg raakt en delen van data niet teruggezet kunnen worden. 
3. **Media failures**: Storage access fouten door bijvoorbeeld disk crashes, een storing in het netwerk, etc. Het zou kunnen dat de logfile niet toegankelijk is. 

Het is de moeite waard om even te kijken naar de laatste 2 groepen en hoe daar recovery processen precies werken.

#### System Recovery

Veronderstel dat er 5 transacties door de DBMS worden verwerkt. `Tc` duidt een checkpoint aan van de data in de buffer. Er treedt een systeemfout op rechts bij `Tf`:

![](/img/undoredo.jpg "UNDO/REDO operaties voor 5 'kapotte' transacties. src: pdbmbook.com")

We bekijken de 5 transacties afzonderlijk:

1. `T1`---Aangezien deze succesvol werd gecommit voor de systeemfout én voor de snapshot `Tc`, hoeft hier niets voor te gebeuren na een crash.
2. `T2`---Deze transactie is ook compleet voor `Tf`, maar er zit nog data in de buffer van de snapshot `Tc` die niet naar disk geschreven werd. Hier is dus een `REDO` nodig.
3. `T3`---Deze transactie was nog bezig bij de crash van `Tf`. We hebben niet alle data in de buffer: er is dus transactie data verloren gegaan: een `UNDO` dus.
4. `T4`---Deze transactie is gelukt maar alle data is nog steeds pending to disk (bij `T2` was dit een deel): `REDO`.
5. `T5`---De DBMS scheduler had deze net ingepland toen het systeem crashte. Het is niet nodig om hier is van terug te draaien omdat er niks pending was (na de `Tc` checkpoint).

#### Media Recovery

Wat doe jij als er een harde schijf gerashed is van je computer? Bidden voor een werkende backup? Indien fysieke files niet meer toegankelijk zijn is een "media recovery" afhenkelijk van **data redundancy** principes. Hopelijk heb je een backup strategie voorzien. Voor database systemen wordt dit streng toegepast en moeten we een recovery doorvoeren via een mirror op een andere HDD in RAID, op een cloud backup, een offline backup, ... 

Om de **failover time** te minimaliseren wordt dit vaak automatisch opgezet. Een harde schijf kan in een server in `RAID-1` modus geplaatst worden: die functioneert als 100% clone van de andere. Indien er één van beide HDDs faalt, neemt onmiddellijk de andere het werk over, zodat gebruikers zo goed als geen last hebben van de media failure. Systemen als backup copies door iets te "archiveren" (een `.tar.gz` of `.zip` op bepaalde tijdstippen aan de kant zetten) vereist meestal meer werk om terug te zetten. Backups nemen betekent ook beslissen of het een **full** of **incremental** backup zal zijn, waarbij je (1) alle files apart zet, of (2) enkel de wijzigingen ten opzichte van vorige snapshots.  

Een goede backup strategie---dit geldt zowel voor databases als voor je eigen data!!---volgt [het 1-2-3 backup principe](https://www.backblaze.com/blog/the-3-2-1-backup-strategy/):

- Zorg voor minstens **drie backups** van je data, waarvan
- **twee lokaal** maar op **verschillende media** (bvb, verschillende servers, op HDD en op USB, ...), en
- **één off-site kopie**, zoals cloud-based backup systemen als Dropbox, Google Drive, [Backblaze's Cloud Storage](https://www.backblaze.com/b2/cloud-storage.html).

Dan hebben we het nog niet over security gehad... 

### Denkvragen

- Waarom kan je niet gewoon media recovery strategieën toepassen bij systeemcrashes? Waarom wel, maar is dat misschien geen goed idee?
- Als er verschillende transacties tegelijkertijd aan één record iets wijzigen, welke problemen zie jij dan zoal? Hoe zou je dat door de transaction manager laten oplossen? Kan je zo twee verschillende oplossingen bedenken?
- Wat is alweer het verschil tussen `UNDO` en `REDO` recovery technieken?
- Als ier iets misgaat op applicatieniveau, bijvoorbeeld een crash in je Java applicatie, moet de DBMS dan iets doen, of moet de programmeur dan iets doen, om ACID te blijven garanderen? 
- Wat zou er volgens jou moeten gebeuren als twee personen tegelijkertijd op `bol.com` een item bestellen waarvan maar één hoeveelheid in stock is? Wie trekt hier aan het korte eind? Hoe vangen grote webwinkels dit probleem op? 

