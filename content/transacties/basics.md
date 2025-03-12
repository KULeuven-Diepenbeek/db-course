---
title: Transaction Mgmt. Basics
weight: 2
author: Wouter Groenveld en Arne Duyver
draft: false
autonumbering: true
---

Moesten de database systemen voornamelijk **single-user** systemen zijn, dan was er geen probleem geweest want dan werden alle opdrachten gewoon sequentieel uitgevoerd. SQL Database Management Systems (DBMS) systemen zijn echter vooral **multi-user** systemen. Om zowel verschillende gebruikers te kunnen behandelen als nog steeds de **ACID regels** ondersteunen, is er een systeem nodig dat soms gebruikers "in wacht" zet. Stel je voor dat Jens en Jolien tegelijkertijd data lezen én updaten---in dezelfde tabel, hetzelfde record. Jens leest uit "de rekening staat op 100 EUR" en Jolien haalt er 10 EUR vanaf. Wie mag eerst? Kan dit tegelijkertijd? Jens krijgt te horen dat er 100 EUR op de rekening staat, terwijl in werkelijkheid dit 10 EUR minder is. 

## Waarom transacties?

Heel simpel:

<figure>
  <div style="display: flex; justify-content: space-around; align-items: center;">
    <img src="/slides/img/chaos.jpg" style="max-width: 99%; border: 2px solid black;"/>
    <img src="/slides/img/order.jpg" style="max-width: 99%; border: 2px solid black;"/>
  </div>
  <figcaption style="text-align: center; font-weight: bold;">Links: het verkeer zonder transacties, rechts: met transacties</figcation>
</figure>
<br/>

Om Atomicity, Consistency, Isolation, Durability te garanderen is er dus een **transactie manager** nodig die alles in goede banen leidt op het moment dat verschillende gebruikers data gaan raadplegen en/of manipuleren. Dit principe is ruwweg **hetzelfde als task management** van het vak [Besturingssystemen en C](https://kuleuven-diepenbeek.github.io/osc-course/ch6-tasks/)---maar dan op database-applicatie niveau.

## Wat is een transactie?

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

## Het beheren van transacties

Formeel gezien wordt er een transactie **afgelijnd** door aan te kondigen wanneer een transactie begint en wanneer hij stopt, zodat de manager de juiste acties kan doorvoeren. De gebruiker kan met SQL ook zelf een transactie terugdraaien als er een programmafout voorkomt, bijvoorbeeld met een `try { ... } catch(Exception ex) { rollback }`. Meer hierover in sectie [failures/rollbacks](/transacties/failures-rollbacks/).

De transactie manager, die afgelijnde transacties ontvangt, kan dit dan in een **schedule** zetten, om te beslissen welke (delen van) transacties wanneer moeten worden uitgevoerd, net zoals Round Robin CPU scheduling algoritmes. Uiteindelijk wordt er een status toegekend aan een transactie:

1. Committed---het is gelukt en de data is permanent gepersisteerd.
2. Aborted---Een error tijdens het uitvoeren van de transactie.

Indien er halverwege de abort data is gewijzigd moet dit worden teruggezet, of worden **rollbacked**. Vorige versies van data moeten dus ook worden bijgehouden. Jens' rekening kan bijvoorbeeld `€90` zijn initieel, hij haalt er `€10` af om over te maken wat dit tot `€80` maakt, maar er loopt iets mis: de rollback triggert het terugdraaien van het getal `80` naar de originele `90`. 

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

![](/img/statetransitiondiagram.png "State transition diagram illustrating the states for transaction execution.")

- BEGIN_TRANSACTION: Dit markeert het begin van de uitvoering van een transactie.
- READ of WRITE: Deze commando's specificeren lees- of schrijfoperaties op de database-items die worden uitgevoerd als onderdeel van een transactie.
- END_TRANSACTION: Dit specificeert dat de lees- en schrijfoperaties van de transactie zijn beëindigd en markeert het einde van de uitvoering van de transactie. **Op dit punt kan het nodig zijn om te controleren of de wijzigingen die door de transactie zijn geïntroduceerd permanent kunnen worden toegepast op de database (gecommit) of dat de transactie moet worden afgebroken vanwege schending van serialiseerbaarheid of om een andere reden**.
- COMMIT_TRANSACTION: Dit signaleert een succesvol einde van de transactie, zodat alle wijzigingen (updates) die door de transactie zijn uitgevoerd veilig kunnen worden gecommit naar de database en niet ongedaan zullen worden gemaakt.

## Wat als er iet misgaat?

Hoe werkt dat blokje "recovery manager" precies? Een DBMS systeem gebruikt achterliggend een **logfile** als belangrijk element om eventuele recoveries te kunnen doorvoeren. Een logfile bevat in principe redundante data: in het beste geval wordt dit nooit gebruikt. Maar in het geval dat er ook maar iets misloopt is het van groot belang dat de log entries kunnen worden gebruikt om de data terug te zetten. 

Voor **elke transactie** EN **operatie** wordt relevante informatie geregistreerd in de logfile als een **log record**. Deze bevat de volgende informatie: 

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

### Hoe werkt de write-ahead logging nu precies (WAL)
1. Logging: Wanneer een transactie wordt gestart, worden alle bewerkingen eerst naar het transaction log geschreven. Dit logboek bevindt zich in non-volatile memory, zodat de gegevens veilig zijn.
2. Commit: Zodra alle bewerkingen succesvol zijn uitgevoerd, wordt de transactie "gecommit" en worden de wijzigingen permanent toegepast op de database.
3. Recovery: Als er een systeemfout optreedt, kan het DBMS het transaction log gebruiken om de database te herstellen naar een consistente staat. Het systeem voert de acties uit het logboek opnieuw uit om de database terug te brengen naar de laatste bekende goede staat.

Stel je voor dat je een transactie hebt die een nieuwe bestelling toevoegt aan een e-commerce database. De stappen zouden als volgt zijn:
1. Loggen van acties:
```SQL
INSERT INTO orders (order_id, customer_id, order_date) VALUES (1, 123, '2025-03-10');
UPDATE inventory SET stock = stock - 1 WHERE product_id = 456;
INSERT INTO order_items (order_id, product_id, quantity) VALUES (1, 456, 1);
```
2. Commit: Zodra alle acties zijn gelogd **en succesvol uitgevoerd**, wordt de transactie gecommit.
3. Recovery: Als er een systeemcrash optreedt, gebruikt het DBMS het transaction log om de database te herstellen door de gelogde acties opnieuw uit te voeren, REDO. (Als een systeemcrash optreedt voordat de commit is geschreven, wordt de transactie als niet voltooid beschouwd en worden alle wijzigingen teruggedraaid tijdens het herstelproces. UNDO)

Kortom, door de combinatie van write‐ahead logging, het gebruik van commitmarkers en het toepassen van **zorgvuldig ontworpen herstelprocedures (die rekening houden met de exacte volgorde en status van logrecords)**, zorgt een DBMS ervoor dat acties niet dubbel worden uitgevoerd en dat gedeeltelijk uitgevoerde transacties volledig worden teruggedraaid.

#### System Recovery

Veronderstel dat er 5 transacties door de DBMS worden verwerkt. `Tc` duidt een checkpoint aan van de data in de buffer manager van de afbeelding hierboven. Er treedt een systeemfout op rechts bij `Tf`:

![](/img/undoredo.jpg "UNDO/REDO operaties voor 5 'kapotte' transacties. src: pdbmbook.com")

We bekijken de 5 transacties afzonderlijk:

1. `T1`---Aangezien deze succesvol werd gecommit voor de systeemfout én voor de snapshot `Tc`, hoeft hier niets voor te gebeuren na een crash.
2. `T2`---Deze transactie is ook compleet voor `Tf`, maar er zit nog data in de buffer van de snapshot `Tc` die niet naar disk geschreven werd. Hier is dus een `REDO` nodig.
3. `T3`---Deze transactie was nog bezig bij de crash van `Tf`. We hebben niet alle data in de buffer: er is dus transactie data verloren gegaan: een `UNDO` dus.
4. `T4`---Deze transactie is gelukt maar alle data is nog steeds pending to disk (bij `T2` was dit een deel): `REDO`.
5. `T5`---De DBMS scheduler had deze net ingepland toen het systeem crashte. Het is niet nodig om hier is van terug te draaien omdat er niks pending was (na de `Tc` checkpoint).

_Meer info [hier](https://www.tutorialspoint.com/no-undo-redo-recovery-based-on-deferred-update), maar is enkel ter info en moet niet in detail gekend zijn_

#### Media Recovery

Wat doe jij als er een harde schijf gerashed is van je computer? Bidden voor een werkende backup? Indien fysieke files niet meer toegankelijk zijn is een "media recovery" afhankelijk van **data redundancy** principes. Hopelijk heb je een backup strategie voorzien. Voor database systemen wordt dit streng toegepast en moeten we een recovery doorvoeren via een mirror op een andere HDD in RAID, op een cloud backup, een offline backup, ... 

Om de **failover time** te minimaliseren wordt dit vaak automatisch opgezet. Een harde schijf kan in een server in `RAID-1` modus geplaatst worden: die functioneert als 100% clone van de andere. Indien er één van beide HDDs faalt, neemt onmiddellijk de andere het werk over, zodat gebruikers zo goed als geen last hebben van de media failure. Systemen als backup copies door iets te "archiveren" (een `.tar.gz` of `.zip` op bepaalde tijdstippen aan de kant zetten) vereist meestal meer werk om terug te zetten. Backups nemen betekent ook beslissen of het een **full** of **incremental** backup zal zijn, waarbij je (1) alle files apart zet, of (2) enkel de wijzigingen ten opzichte van vorige snapshots (denk aan git bijvoorbeeld).  

Een goede backup strategie---dit geldt zowel voor databases als voor je eigen data!!---volgt [het 1-2-3 backup principe](https://www.backblaze.com/blog/the-3-2-1-backup-strategy/):

- Zorg voor minstens **drie backups** van je data, waarvan
- **twee lokaal** maar op **verschillende media** (bvb, verschillende servers, op HDD en op USB, ...), en
- **één off-site kopie**, zoals cloud-based backup systemen als Dropbox, Google Drive, [Backblaze's Cloud Storage](https://www.backblaze.com/b2/cloud-storage.html).

En dan hebben we het nog niet over security gehad... 

### Denkvragen

- Waarom kan je niet gewoon media recovery strategieën toepassen bij systeemcrashes? Waarom wel, maar is dat misschien geen goed idee?
    <!-- EXSOL -->
    <span style="color: #03C03C;">Solution:</span>
    - Media recovery strategieën (zoals herstel vanuit back-ups) zijn primair bedoeld voor fysieke schade aan opslagmedia. Bij een systeemcrash gaat het erom de consistente staat van de database snel te herstellen, wat met media recovery vaak te traag en omslachtig is. Bovendien kan een dergelijk herstel risico’s met zich meebrengen, zoals het herstellen van een oude, mogelijk inconsistente staat.
    - In principe kan media recovery ingezet worden als laatste redmiddel. Echter, vanwege de langere hersteltijd en de kans op inconsistente data (bijvoorbeeld als sommige transacties al deels uitgevoerd waren) is het in de praktijk verstandiger om gespecialiseerde crash recovery technieken (zoals log-gebaseerde UNDO/REDO mechanismen) te gebruiken.

- Als er verschillende transacties tegelijkertijd aan één record iets wijzigen, welke problemen zie jij dan zoal? Hoe zou je dat door de transaction manager laten oplossen? Kan je zo twee verschillende oplossingen bedenken?
    <!-- EXSOL -->
    <span style="color: #03C03C;">Solution:</span>
    - Eén transactie overschrijft de wijzigingen van een andere, waardoor data verloren gaat.
    - Een transactie leest onvoltooide (nog niet gecommitte) data van een andere transactie.
    - Herhaalde leesoperaties geven verschillende resultaten, wat leidt tot inconsistenties.   

    - Een record vergrendelen zodra een transactie eraan werkt. Andere transacties moeten wachten tot de vergrendeling wordt opgeheven, waardoor conflicten worden voorkomen.
    - Transacties voeren wijzigingen uit zonder directe vergrendeling. Bij het committen controleert het systeem of er tussentijds wijzigingen hebben plaatsgevonden. Als er een conflict wordt gedetecteerd, wordt de transactie teruggedraaid.

- Wat is alweer het verschil tussen `UNDO` en `REDO` recovery technieken?
    <!-- EXSOL -->
    <span style="color: #03C03C;">Solution:</span>
    - UNDO: Wordt gebruikt om de effecten van transacties die niet succesvol zijn afgerond of die foutief zijn uitgevoerd, ongedaan te maken. Dit zorgt ervoor dat de database terugkeert naar een consistente staat.
    - REDO: Wordt toegepast om de wijzigingen van transacties die **wel gecommit** waren opnieuw toe te passen. Dit is vooral belangrijk na een crash wanneer sommige wijzigingen nog niet permanent op de schijf waren vastgelegd.

- Als ier iets misgaat op applicatieniveau, bijvoorbeeld een crash in je Java applicatie, moet de DBMS dan iets doen, of moet de programmeur dan iets doen, om ACID te blijven garanderen? 
    <!-- EXSOL -->
    <span style="color: #03C03C;">Solution:</span>
    - Het DBMS is primair verantwoordelijk voor het waarborgen van ACID-eigenschappen via zijn transactie- en recoverymechanismen.  
        - Bij een crash op applicatieniveau (bijvoorbeeld in een Java-applicatie) zorgt het DBMS er met behulp van commit/rollback en log-based recovery voor dat de database in een consistente staat blijft.
        - De programmeur moet wel zorgen voor een correcte afhandeling van transacties in de applicatie, zoals het op de juiste wijze starten, committen en, indien nodig, terugdraaien van transacties.

- Wat zou er volgens jou moeten gebeuren als twee personen tegelijkertijd op `bol.com` een item bestellen waarvan maar één hoeveelheid in stock is? Wie trekt hier aan het korte eind? Hoe vangen grote webwinkels dit probleem op?
    <!-- EXSOL -->
    <span style="color: #03C03C;">Solution:</span>
    - Het voorraadbeheersysteem (onderdeel van het DBMS of een gekoppeld systeem) moet atomische transacties gebruiken. Dit zorgt ervoor dat slechts één transactie succesvol het laatste item kan reserveren, terwijl de andere transactie een foutmelding krijgt of wordt teruggedraaid.