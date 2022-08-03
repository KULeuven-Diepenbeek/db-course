---
title: 2. Concurrency Control
---

De transactie management scheduler (zie [transacties - basics](/transacties/basics)) is verantwoordelijk om verschillende transacties correct in te plannen zonder dat er data problemen of clashes optreden. 

## 1. Problemen? Welke problemen?

Denk terug aan het bank transfer probleem van de vorige sectie. Veronderstel dat deze keer zowel Jens als Marianne `€10` willen overmaken naar Jolien. Als we dat als volgt doen:

1. Verminder bedrag van source rekening
2. Verhoog bedrag van destination rekening

Dan zou het kunnen dat bij het uitlezen van #2, Jolien's rekening op `€100` staat. Maar als de transactie van Marianne dit ook leest als `€100`, en niet wacht tot de `€110` die het zou moeten zijn na de commit van de transactie van Jens, dan gaat in totaal Marianne slechts `€10` rijker zijn in plaats van twee keer dat bedrag. _Oeps!_

{{<mermaid align="left">}}
graph LR;
    JN[Rekening van Jens]
    JL[Rekening van Jolien]
    ML[Rekening van Marianne]
    JN -->|10 EUR| JL
    ML -->|10 EUR| JL
{{< /mermaid >}}

Hieronder volgen een aantal veel voorkomende concurrency problemen die de transaction scheduler uitdagen.

### A. Lost Updates

Dit is exact bovenstaande situatie. Het `UPDATE` statement van Jens' transactie (verhoog Jolien's rekening met `10`) is "verloren", omdat Marianne hier tussen komt, en haar `UPDATE` die terug ongedaan maakt:

| time | `T1` (Marianne) | `T2` (Jens) | bedrag |
|------|----|----|--------|
| 1   |    						| begin trans   			|   100     |
| 2   |  begin trans  			| read(bedrag): `100`   	|   100     |
| 3   |  read(bedrag): `100`	| bedrag = bedrag + 10   	|   100     |
| 4   |  bedrag = bedrag + 10  	| write(bedrag)   			|   110     |
| 5   |  write(bedrag)  		| commit  				 	|   110 (**oeps**)    |
| 6   |  commit  				|    						|   110    |

Dit voorbeeld illustreert dat, alhoewel beide transacties 100% correct zijn, er toch nog problemen kunnen optreden als transacties elkaar gaan storen. Het spreekt voor zich dat als `T1` data zou lezen en schrijven uit een andere rij of tabel, dit geen probleem zou zijn---in dit specifieke voorbeeld.

### B. Dirty Reads

Een "dirty read" probleem is het lezen van "dirty" data---data die eigenlijk voor de andere transactie nog niet zichtbaar mat zijn omdat het hier over uncommitted data gaat. Als Marianne's `read(bedrag)` toch het juiste bedrag zou inlezen (110), **voordat** Jens' transactie compleet is, maar op een of andere manier is die transactie teruggedraaid, dan spreken we over een dirty read, en krijgt Jolien onterecht toch `€20`, terwijl Jens zijn `€10` mag houden. _It prints money!_


| time | `T1` (Marianne) | `T2` (Jens) | bedrag |
|------|----|----|--------|
| 1   |    						| begin trans   			|   100     |
| 2   |  			 			| read(bedrag): `100`   	|   100     |
| 3   |  						| bedrag = bedrag + 10   	|   100     |
| 4   |  begin trans		  	| write(bedrag)   			|   110     |
| 5   |  read(bedrag): `110`  	|   					 	|   110     |
| 6   |  bedrag = bedrag + 10  	| ROLLBACK!  				|   120 (**oeps**)    |
| 7   |  write(bedrag)  		|   						|   120    |
| 8   |  commit  				|   						|   120    |


### C. Inconsistent Analysis

Bij inconsistente analyse tussen twee transacties gaat het over een sequentie van verschillende dirty reads die de situatie alleen maar verergeren, zelfs zonder de eventuele rollback. Stel dat het over te schrijven bedrag in stukjes van `€2` wordt overgeschreven, waarbij telkens tussenin een `read(bedrag)` plaats vindt---die natuurlijk de data van de andere transactie inleest. Het resultaat is, opnieuw, een veel te grote som, en een mogelijks erg blije Jolien. 

We laten een schematische voorstelling van dit probleem als oefening voor de student.  

### ... En meer

Er zijn nog verschillende andere mogelijkheden waarbij de scheduler de bal kan misslaan. Bijvoorbeeld door **unrepeateable reads**: transactie `T1` leest dezelfde rij verschillende keren in maar verkrijgt telkens andere waardes (wat niet zou mogen) vanwege andere transacties die ook met die rij bezig zijn. 

Of wat dacht je van **phantom reads**: transactie `T2` is bezig met rijen effectief te _verwijderen_, terwijl `T1` deze toch nog inleest. Het zou kunnen dat hierdoor verschillende rijen ontstaan (`T2` rollback, `T1` die een nieuwe rij maakt), of dat voorgaande bestaande rijen verdwijnen door `T2`, waardoor de transactie van `T1` mogelijks faalt. 

Teruggrijpende naar ons Jolien voorbeeld, stel dat Jens Jolien `€5` wilt betalen per klusje dat ze gedaan heeft. Klusjes worden opgeslagen in een aparte tabel, maar ondertussen kan Jolien een nieuw klusje afvinken, waardoor Jens' transactie een _phantom read_ krijgt:

| time | `T1` (Jens) | `T2` (Jolien) | klusjes |
|------|----|----|--------|
| 1   |  read 1: `SELECT * FROM klusjes WHERE naam = 'Jolien'` (2)		|    					|   2    |
| 2   |  			 													| 						|   2    |
| 3   |  random ander werk												| `INSERT INTO klusjes ...` 	|   3     |
| 4   |  		  														|    					|   3     |
| 5   |  read 2: `SELECT * FROM klusjes WHERE naam = 'Jolien'` (3, **oeps**) 	|   			|   3     |
| 6   |  sort 3x5 op rekening (teveel)								 	|			  			|   3    |
| 7   |  commit

Bijgevolg verkgijt `T1` inconsistente data: de ene keer 2, de andere keer 3---vergeet niet dat het goed zou kunnen dat `T2` nog teruggedraaid wordt. 


## 2. Scheduler oplossingen

Wat is de simpelste manier om bovenstaande problemen allemaal integraal te vermijden? Juist ja---sequentieel transacties verwerken.

### A. Serial scheduling

Met serial scheduling is `T2` verplicht te wachten op `T1` totdat die zijn zaakje op orde heeft. De **lost update** transactie flow van hierboven ziet er dan als volgt uit:

| time | `T1` (Marianne) | `T2` (Jens) | bedrag |
|------|----|----|--------|
| 1   |    						| begin trans   			|   100     |
| 2   |   			  			| read(bedrag): `100`   	|   100     |
| 3   |   						| bedrag = bedrag + 10   	|   100     |
| 4   |   					  	| write(bedrag)   			|   110     |
| 5   |   				  		| commit  				 	|   110     |
| 6   |  begin trans  			|    						|   110    |
| 7   |  read(bedrag): `110`  	|    						|   110    |
| 8   |  bedrag = bedrag + 10 	|    						|   120    |
| 9   |  write(bedrag)  		|    						|   120    |
| 10  |  commit 		 		|    						|   120    |

Wat valt je op als je naar de **time** kolom kijkt? Serial scheduling duurt nu `10` ticks t.o.v. `6` bij de potentiële lost update---da's een redelijk grote performance hit van 40%! Serial scheduling lost misschien wel alles op, maar daardoor verliezen we alle kracht van het woord parallel: dit is in essentie **non-concurrency**. 

Wat zijn dan betere scheduling alternatieven?

### B. Optimistic scheduling

Bij "optimistic" scheduling gaan we ervan uit dat conflicten tussen simultane transacties _nauwelijks voorkomen_. Met andere woorden, we benaderen het probleem (misschien te) optimistisch. Transacties mogen gerust tegelijkertijd lopen als ze bijvoorbeeld verschillende tabellen of stukken data in dezelfde tabel bewerken---zolang er geen conflict is, geniet paralellisatie de voorkeur. 

Bij optimistische schedulers worden alle transactie operaties doorgevoerd. Wanneer deze klaar zijn voor een eventuele `COMMIT`, wordt er gecontroleerd op potentiële conflicten. Indien geen, success. Indien wel, abort en `ROLLBACK`. 

{{% notice note %}}
Merk op dat rollback operaties erg dure operaties zijn: de manager moet graven in de logfile, moet beslissen of er UNDO/REDO operaties moeten worden uitgevoerd, er is mogelijke trage disc access (I/O), ... Als je vaak rollbacks hebt/verwacht is optimistic scheduling nog steeds geen performant alternatief. 
{{% /notice %}}


### C. Pessimistic scheduling

Bij "pessimistic" scheduling gaan we van het omgekeerde uit: transacties gaan heel zeker conflicteren. De scheduler probeert transactie executies te verlaten om dit zo veel mogelijk te vermijden. Een serial scheduler is een extreem geval van een pessimistic scheduler. 

#### Locking

In de praktijk wordt **locking** gebruikt op een _pre-emptive_ manier (zie [besturingssystemen: scheduling algorithms](https://kuleuven-diepenbeek.github.io/osc-course/ch7-scheduling/algorithms/)) om toch transacties waar mogelijk concurrent te laten doorlopen. Bij transacties die schrijven in plaats van die enkel lezen zullen locks eerder nodig zijn. Er bestaan uiteraard erg veel verschillende locking technieken/algoritmes. 

We maken onderscheid tussen twee groepen:

1. **exclusive locks**: als `T1` een exclusieve lock neemt, kan er geen enkele andere transactie op die database worden uitgevoerd. Dit is een erg strict systeem, denk aan serial scheduling. 
2. **shared locks**: zolang `T1` een lock neemt op een _object_ (zie onder), krijgt het de garantie dat geen enkele andere transactie dat object kan manipuleren totdat de lock terug wordt vrijgegeven (eventueel ook door middel van een rollback). Locks worden "gedeeld": `T1` krijgt write access, en `T2` moet wachten met schrijven, maar mag wel lezen. 

De database "objecten" waar een lock op genomen kan worden zijn onder andere (van kleine locks naar groot):

1. row locks;
2. column locks;
3. page locks (delen van een tabel zoals stored in files);
4. table locks;
5. databas locks (bvb een file lock in SQLite);

Een lock op één rij in beslag genomen door `T1` betekent dat voor diezelfde tabel `T2` nog steeds bedragen kan wijzigen van een andere rekening. Column locks kunnen tot meer wachttijd leiden. Page locks zijn "stukken" van een tabel (rijen voor `x` en erna). Een page is een chunk van een tabel die op die manier wordt opgeslaan. Dit verschilt van DB implementatie tot implementatie. Tenslotte kan een hele tabel gelockt worden---of de hele tablespace---wat meer pessimistisch dan optimistisch is. 


{{% notice note %}}
Locks zijn onderhevig aan dezelfde nadelen als rollbacks: ze zijn duur. Als er erg veel row locks zijn op een bepaalde tabel kan dit veel CPU/RAM in beslag nemen. In dat geval kan de lock manager beslissen om aan **lock escalation** te doen: de 100 row locks worden één page of table lock. Dit vermindert resource gebruik drastisch---maar zou transacties ook langer kunnen laten wachten. _You win some, you lose some..._
{{% /notice %}}


#### Problemen bij oplossen van problemen: deadlocks

Stel dat Jens cash geld wilt deponeren, en Marianne hem ook geld wenst over te schrijven. Voordat Marianne dat doet koopt ze eerst nog een cinema ticket. Jens wil ook een ticket in dezelfde transactie. Het gevolg is dat `T1` en `T2` oneindig op elkaar blijven wachten. Dat ziet er zo uit:

{{<mermaid align="left">}}
graph LR;
    C{Cinema tabel}
    R{Rekening tabel}
    J[Jens]
    M[Marianne]
    J -->|deposit 10| R
    M -->|transfer 10| R
    M -->|koop ticket| C
    J -->|koop ticket| C
{{< /mermaid >}}

De kruisende pijlen duiden al op een conflict: 

| time | `T1` (Jens) | `T2` (Marianne) |
|------|----|----|--------|
| 1    | begin tarns | | 
| 2    |  | begin tarns | 
| 3    | update `rekening` (**acquire lock R**) | |
| 4    | | update `cinema` (**acquire lock C**) | 
| 5    | poging tot update `cinema` (WACHT) | | 
| 6    | | poging tot update `rekening` (WACHT) |

Deze (simpele) **deadlock** kan gelukkig gedetecteerd worden door het DBMS systeem. Die zal één van beide transacties als "slachtoffer" kiezen en die transactie terugdraaien---meestal gebaseerd op welke het makkelijkste is om terug te draaien. Het probleem is dat de meeste applicaties niet onmiddellijk voorzien zijn op zo'n onverwachte rollback. Dit resulteert meestal in een negatieve gebruikerservaring. 

Deadlocks en algoritmes om dit te detecteren en op te lossen zijn erg complexe materie. Het volstaat om bovenstaand eenvoudig voorbeeld te kennen, en te weten hoe dat aangepakt zou kunnen worden---bijvoorbeeld met _starvation_, _timeouts_, en _priority shift_ principes zoals ook gezien in het vak Besturingssystemen en C. 

Zie ook [A beginners guide to DB deadlocks](https://vladmihalcea.com/database-deadlock/).

#### Isolation levels

Pessimistic locking kan veel problemen opvangen, maar ten koste van performantie---wat meestal belangrijker is. Voor veel transacties is het oké om met een minimum aan conflicten de throughput van transacties zo hoog mogelijk te houden. De meeste DBMS systemen zijn hier flexibel in: je kan wat we noemen **isolation levels** instellen, dat meestal bestaat uit de volgende vier opties (van low naar high isolation):

1. **Read uncommited**---Dit laat toe om "uncommited" data te lezen (dat problemen geeft, zie boven), en wordt meestal gebruikt in combinatie met read-only transacties. 
2. **Read committed**---Gebruikt _short-term_ read locks en _long-term_ write locks, en lost zo het inconsistent analysis/lost update probleem op, maar is niet krachtig genoeg om phantom reads tegen te houden. 
3. **Repeatable read**---Gebruikt _long-term_ read & write locks. Een transactie kan zo dezelfde rij verschillende keren opnieuw lezen zonder conflicten van insert/updates van andere transacties. Het phantom read probleem is echter nog steeds niet opgelost. 
4. **Serializable**---het krachtigste isolation level dat in theorie aansluit met serial scheduling. 

Een _short-term_ lock wordt enkel vastgehouden gedurende het interval dat nodig is om een operatie af te ronden, terwijl _long-term_ locks een protocol volgen en meestal tot de transactie commited/rollbacked is vastgelegd zijn. 

{{% notice warning %}}
Merk op dat voor elke database implementatie de selectie van een isolation level andere gevolgen kan hebben! Zie de tabellen in https://github.com/changemyminds/Transaction-Isolation-Level-Issue. Het is dus van belang de documentatie van je DB te raadplegen, zoals [deze van Oracle](https://docs.oracle.com/cd/E19830-01/819-4721/beamv/index.html), waarin de volgende beschrijving staat voor `TRANSACTION_SERIALIZABLE`: "Dirty reads, non-repeatable reads and phantom reads are prevented.".
{{% /notice %}}


We komen hier nog later op terug in [concurrency in practice](/transacties/concurrency-in-practice/) wanneer we bijvoorbeeld bij JPA of Hibernate aangeven welk isolation level gewenst is. 

### Denkvragen

- Waarom is het phantom read probleem niet opgelost bij isolation level 3 (repeatable read)?
- Kan je nog andere situaties verzinnen waarin een deadlock kan voorkomen? Welk isolation level of scheduling algoritme lost dit op?
- Wat is de verantwoordelijkheid van de DBMS's transactie management systeem in verhouding tot de ACID eigenschappen?
- Welke impact heeft lock granulariteit op transactie throughput? 
