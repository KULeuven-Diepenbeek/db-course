---
title: '2. Data Warehousing & BI'
---

Tot nu toe hebben we ons toegelegd op het zo optimaal mogelijk _bewaren en ophalen van data_---rekening houdend met integriteit en anderen ACID/BASE principes. Maar wat zijn we hier nu allemaal mee, los van een werkende applicatie? In dit hoofdstuk gaan we **data benaderen vanuit business perspectief**. 

Een bedrijf kan gebaseerd op de miljoenen eenheden data dat het verzameld, op verschillende plekken en in verschillende formaten, beter beslissingen nemen. Strategische business beslissingen worden meestal op verschillende niveau's genomen:

1. Niveau 1 noemen we het **operationeel** niveau. Hier worden dagelijkse beslissingen genomen op korte termijn, vaak zonder al te veel naar de toekomst te kijken. Denk maar aan verschillende verkopen, implementatie beslissingen vanuit business perspectief, enzovoort. 
2. Niveau 2 noemen we het **tactisch niveau**. Middle management probeert hier op middellange termijn (bijvoorbeeld een maand/kwartaa/jaar) een vooruitblik te doen en gebaseerd daarop een partnerschap aan te gaan of af te wijzen.
3. Niveau 3 noemen we het **strategisch niveau**. Senior management neemt beslissingen op dit niveau om het bedrijf op lange termijn (5 jaar of misschien zelfs langer) op koers te houden. 

Stel dat jij een bedrijf van 100 man hebt. Hoe beslis je waarin te investeren, om binnen 5 jaar niet failliet te zijn, maar misschien uit te groeien tot een bedrijf van 150 man? Hier kan **data helpen met beslissen**. De term **Business Intelligence** (BI) slaat hier op: de set van activiteiten, technieken, en tools om (1) patronen in data van het verleden te herkennen en (2) voorspellingen te maken naar te toekomst toe.

Een bijkomend probleem is dat je groot bedrijf enorm veel data heeft:

- Het genereert dagelijks facturen en sales;
- Werknemers gebruiken de tikklok die weer data voorstelt;
- Je eindgebruikers interageren met jullie software, wat misschien verspreid zit over verschillende systemen;
- De helpdesk beheert tickets;
- HR houdt indexen van lonen bij en iemand beheert de fleet van bedrijfswagens;
- ...

Het is duidelijk dat één simpele `SELECT sales FROM income` niet voldoende gaat zijn voor het management om beslissingen te helpen maken. We hebben een _data warehouse_ nodig.

![](/img/dw.png "src: sqlhammer.com")

Probeer op bovenstaand schema, een voorbeeld van hoe moderne data warehouses werken in een groot bedrijf, alle verschillende componenten te identificeren. Links staat de data die binnenkomt, en rechts het resultaat van de analyse. We bespreken hieronder kort elk blok. Experimentatie met de praktische kant, door middel van libraries als [Apache Hadoop](https://hadoop.apache.org/), is een optionele oefening voor de student.

We beginnen met de grote blauwe blok: een "data warehouse".

# Een Data Warehouse (DW)

Wat is een data warehouse? Volgens de uitvinder, Bill Inmon:

> A data warehouse is a (1) subject-oriented, (2) integrated, (3) time-variant, and (4) non-volatile collection of data in support of management's decision-making process.

Dat klinkt ingewikkelder dan het is:

1. **Subject-oriented**; de data is gecentreerd rond _subjecten_ ofwel klanten, producten, sales, etc. De data is _niet_ gecentreerd rond transacties of applicaties. Het moet managers helpen een beslissing maken, niet developers helpen ontwikkelen. 
2. **Integrated**; het is één grote structuur met als input verschillende andere DB sources met elk hun eigen formaat. Hier is dus conversie voor nodig: zie verder.
3. **Time-variant**; een data warehouse bewaart data als een series van _snapshots_: bij intervallen van bijvoorbeeld elk kwartaal wordt data vernieuwd.
4. **Non-volatile**; data is voornamelijk _read-only_ (na initieel inladen): de belangrijkste operaties zijn dus loading & retrieval.

Bekijk de volgende schematische weergave van een typische data warehouse:

{{<mermaid align="left">}}
graph BT;
    WH[(Data Warehouse)]
    RDBMS1[[RDBMS 1]]
    RDBMS2[[RDBMS 2]]
    XML[/XML\]
    MAIL[/E-MAIL\]
    ETL1{{ETL}}
    ETL2{{ETL}}
    ETL3{{ETL}}
    ETL4{{ETL}}
    ETL1 --> WH
    ETL2 --> WH
    ETL3 --> WH
    ETL4 --> WH
    RDBMS1 --> ETL1
    RDBMS2 --> ETL2
    XML --> ETL3
    MAIL --> ETL4
{{< /mermaid >}}

Aan de onderkant zie je verschillende bronnen van data---zeer waarschijnlijk op zichzelf relationele of niet-relationele databases, XML, emails, HTML, andere losse CSV bestanden, ... Die moeten vanwege het **integrated** principe allemaal in één grote blok als snapshot worden bewaard zodat het management deze makkelijk kan queryen. Daarom moet er **voor élke data source een ETL** of Extraction, Transformation, and Loading proces worden opgestart: bepaalde attributen in de XML veranderen van structuur, de datum in RDBMS1 is opgeslaan als `YYYY-MM-DD` maar in RDBMS2 als `DD-MM-YYYY`, afrondingsverschillen worden weggewerkt, etc etc. Merk op dat niet altijd alle gegevens genormaliseerd worden. Een data warehouse kan dus best ook gedenormaliseerde data bevatten, maar dat komt minder frequent voor. 

Een data warehouse is dus _geaggregeerde_ en opgekuiste data dankzij de ETLs. Er is vooral high-level data te vinden die kan gebruikt worden voor tactische, en vooral strategische beslissingen, maar minder voor operationele. Daarvoor kijk je best gewoon naar de bronnen zelf. 

Het opzetten van de _ETL_ is waarschijnlijk `80%` van het werk, dit is héél tijdsintensief!  

{{% notice note %}}
Q: Over hoeveel data gaat het in een data warehouse? A: _Veel_. _Erg_ veel. Er zijn warehouses (vandaar ook "warenhuis" en niet zomaar "database" of "servertje") van 12 petabytes, wat 12000 terabytes is. Bedenk hoeveel data wij elke minuut genereren: hoeveel tweets, hoeveel beurstransacties, hoeveel commits, ... 
{{% /notice %}}

In sommige gevallen wenst men niet onmiddellijk alle data in de productie warehouse te bewaren, maar eest nog een **staging area** op te zetten. Deze warehouse staat voor de "echte" en en kan bijvoorbeeld worden gebruikt om intensieve machine learning algoritmes op te laten draaien zonde dat de performantie van de effectieve warehouse in het gedrang komt. Zo'n staging warehouse noemen we een **operational data store**. (zie schema bovenaan: links van het Hadoop logo).

Voor BI systemen om effectief te zijn, moet de data ook **kwalitatief** zijn. Gebaseerd op "rommel" een grote toekomstgerichte business planning maken is een recept voor mislukking---en mogelijks faillisement. Dus: _Garbage In, Garbage Out_ (GIGO). ETLs moeten correct zijn, en het is ook een kwestie van de _juiste_ data op te nemen in je warehouse. Niet alles hoeft of kan van belang zijn. 

## RDBMS vs DW

Hoe verhoudt een data warehouse zich ten opzichte van een typisch transactionele database?

| | **Transactional system** | **Data Warehouse** |
|-----|-----|-----|
| **Usage** | Day to day ops | Decision support mngt |
| **Latency** | real-time | periodic snapshots |
| **Design** | app-oriented | subject-oriented |
| **Normalization** | normalized | (sometimes also) denormalized data |
| **Manipulation** | insert/update/delete/select | insert (once)/select |
| **Transaction mngt** | important | less of a concern |
| **Type of queries** | many simple | few complex, some ad-hoc |

## Mini DWs: Data Marts

In de praktijk is één gigantische data warehouse, afhankelijk van de grootte van het bedrijf, niet bruikbaar. Data snapshot storage wordt meestal in functionele stukken opgeslagen, misschien opgedeeld per business unit, zoals het bedrijf ook is ingedeeld:


{{<mermaid align="left">}}
graph BT;
    WH[(Data Warehouse)]
    DM1[(Mart 1: Sales)]
    DM2[(Mart 2: Accounting)]
    DM3[(Mart 3: Finances)]
    DM1 --> WH
    DM2 --> WH
    DM3 --> WH
{{< /mermaid >}}

Elke mart wordt op zijn beurt gevoed via een ETL zoals het eerste schema in dit hoofdstuk. 

Het voordeel van zo'n opdeling in data marts is (1) _focused content_ en (2) uiteraard _performantiewinst_. De managers van sales moeten niet onnodig in de accounting mart queries afvuren en omgekeerd, alhoewel het hoger management natuurlijk nog steeds graag een overzicht van _alles_ heeft in bepaalde verslagen. 

## Data Lakes

Soms zijn data warehouses en hun marts niet flexibel genoeg om de gigantische (en eindeloze) stroom aan data te kunnen opslaan. Vergeet niet dat ETLs ook veel processorkracht vereisten, en een snapshot maken maar op een vaste periode gebeurt. In dat geval is een **data lake** handig: letterlijk een meer waar alle inkomende data (relatief) ongestructureerd wordt ingedumpt (zie lichtblauwe balk op schema bovenaan). 

Wanneer voedt men een lake en wanneer een warehouse? Een lake wordt vooral gebruikt voor native data---in zijn ruw formaat. Hier is nog geen ETL aan bod gekomen. Als het over ruwe signaaldata gaat, clickstreams, social media feeds, server logs, etc, dan is een data lake interessanter. 

Net omdat dit allemaal ruwe data is, is het analyseren van die data werk voor specialisten: dit zijn de "advanced analytics" op het bovenstaande schema. Hier zijn data scientists mee bezig. De gemiddelde bedrijfsmanager heeft hier echter niets aan! Een data lake is dus **niet voldoende** om bedrijfsbeslissingen te kunnen helpen maken---de voornaamste reden waarom we met warehousing bezig zijn. Vaak wordt data van een lake nog doorgesluisd naar een operational data store, die op zijn beurt data laat doordruppelen naar de productie warehouse. Het wordt ingewikkeld... 

{{% notice note %}}
Data warehousing wordt ook aangeboden in de cloud. Data lakes worden vaak in de cloud gehost om kosten te drukken aangezien hier nog grotere hoeveelheden data wordt bewaard. Een voorbeeld hiervan is _Amazon Redshift_, dat wordt gebruikt door Nasdaq. Ze verwerken er **70 miljoen records per dag**: https://aws.amazon.com/solutions/case-studies/nasdaq-case-study/.
{{% /notice %}}



