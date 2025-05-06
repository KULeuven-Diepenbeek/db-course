---
title: Timeseries database met InfluxDb
draft: false
weight: 1
---

## Timeseries database basics

### Introductie

Een timeseries database (TSDB) is een database die is geoptimaliseerd voor het opslaan en ophalen van tijdgestempelde gegevens. Dit type database wordt vaak gebruikt in toepassingen zoals IoT, monitoring, en financiële analyses, waar gegevens in de tijd worden verzameld en geanalyseerd. TSDB's zijn ontworpen om efficiënt om te gaan met grote hoeveelheden gegevens die continu worden toegevoegd en om snelle query's uit te voeren op basis van tijd.

#### Verschil met SQL en NoSQL databases

Time Series Databases (TSDB's) onderscheiden zich van traditionele SQL- en NoSQL-databases door hun focus op tijdgestempelde gegevens. Terwijl SQL-databases goed zijn in het beheren van gestructureerde gegevens en NoSQL-databases flexibel zijn in het omgaan met verschillende datatypes, zijn TSDB's specifiek ontworpen voor tijdgebaseerde gegevens.

- **Indexering**: TSDB's gebruiken gespecialiseerde indexstructuren zoals R-trees of Bitmap-indexen, die zijn geoptimaliseerd voor tijdgebaseerde query's. Dit maakt ze efficiënter dan de B-trees van SQL of de hash-indexen van NoSQL.
- **Lees- en schrijfbewerkingen**: TSDB's bieden een balans tussen snelle schrijfbewerkingen en efficiënte leesbewerkingen. Ze zijn geoptimaliseerd voor sequentiële schrijfbewerkingen en tijdgebaseerde query's.
- **Beperkingen van SQL en NoSQL**: Hoewel SQL en NoSQL kunnen worden gebruikt voor tijdgebaseerde gegevens, missen ze de gespecialiseerde structuren en optimalisaties van TSDB's. Het gebruik van een TSDB is vergelijkbaar met het gebruik van een pizzasnijder in plaats van een botermes om een pizza te snijden: het is ontworpen voor de taak en werkt efficiënter.

In een wereld waar tijdgevoelige gegevens steeds belangrijker worden, bieden TSDB's een unieke oplossing voor het efficiënt beheren en analyseren van deze gegevens.

#### Link tussen IoT, Industry4.0 en Timeseries Databases

IoT (Internet of Things) en Industry 4.0 genereren enorme hoeveelheden tijdgestempelde gegevens. Sensoren in IoT-apparaten en industriële machines verzamelen continu gegevens zoals temperatuur, druk, en trillingen. Deze gegevens zijn essentieel voor het monitoren, analyseren en optimaliseren van processen.

Timeseries databases spelen een cruciale rol in deze context door:
- **Efficiënt opslaan van gegevens**: TSDB's kunnen grote hoeveelheden gegevens opslaan met minimale overhead.
- **Snelle query's**: Ze bieden snelle toegang tot historische gegevens, wat essentieel is voor analyses en rapportages.
- **Geavanceerde analyses**: Met functies zoals aggregaties en trendanalyses kunnen TSDB's waardevolle inzichten bieden.

Door de groei van IoT en Industry 4.0 wordt het gebruik van TSDB's steeds belangrijker voor bedrijven die concurrerend willen blijven.

## InfluxDb: een open source timeseries database 

### Installatie lokaal

Om InfluxDB lokaal te installeren op Windows:
1. Download de InfluxDB OSS v2 van de [officiële website](https://docs.influxdata.com/influxdb/v2/install/?t=Windows#download-and-install-influxdb-v2).
2. Pak het gedownloade ZIP-bestand uit in de map `C:\Program Files\InfluxData\influxdb`.
3. Open een terminal en navigeer naar de map:
   ```bash
   cd "C:\Program Files\InfluxData\influxdb"
   ```
4. Start de InfluxDB-server:
   ```bash
   ./influxd
   ```
5. Open de GUI in je browser via [http://localhost:8086](http://localhost:8086).
6. Maak een "all-purpose token" aan om toegang te krijgen tot de database.

### Andere populaire timeseries databases

Naast InfluxDB zijn er andere populaire TSDB's zoals:
- **Prometheus**: Gericht op monitoring en alerting.
- **Graphite**: Geschikt voor het visualiseren van tijdreeksen.
- **OpenTSDB**: Gebouwd op HBase voor schaalbaarheid.

### Overzicht van Gebruik met de web gui

De web GUI van InfluxDB biedt een intuïtieve interface voor het beheren van gegevens. Je kunt:
- **Buckets maken en beheren**.
- **Query's uitvoeren met Flux**.
- **Dashboards maken voor visualisaties**.
- **Alerting instellen**.

### Voorbeeld: een CO2 sensor voor een klaslokaal

Stel je hebt een CO2-sensor in een klaslokaal. Je kunt de gegevens opslaan in InfluxDB met de volgende structuur:
- **Measurement**: "CO2_levels"
- **Tags**: "room", "sensor_id"
- **Fields**: "CO2_concentration"

Een voorbeeld query om de gemiddelde CO2-concentratie van de afgelopen 24 uur te berekenen:

```flux
from(bucket: "classroom_data")
  |> range(start: -24h)
  |> filter(fn: (r) => r["_measurement"] == "CO2_levels")
  |> mean()
```

#### Stap-voor-stap uitleg

1. **`from(bucket: "sensor_data")`**  
   Deze regel specificeert de bron van de gegevens. Hier wordt de bucket "sensor_data" gebruikt, wat een container is voor tijdreeksgegevens.

2. **`|> range(start: -1h)`**  
   Beperkt de gegevens tot de afgelopen 1 uur. Dit zorgt ervoor dat alleen recente gegevens worden geanalyseerd.

3. **`|> filter(fn: (r) => r["_measurement"] == "CO2_levels")`**  
   Filtert de gegevens om alleen de metingen met de naam "CO2_levels" te selecteren. Measurements groeperen gegevenspunten met dezelfde naam.

4. **`|> filter(fn: (r) => r["location"] == "Room 101")`**  
   Beperkt de gegevens verder tot alleen die met de tag "location" gelijk aan "Room 101". Tags zijn metadata die gegevens beschrijven.

5. **`|> mean()`**  
   Berekening van het gemiddelde van de geselecteerde gegevenspunten. Dit geeft een samenvattend getal terug dat de gemiddelde waarde vertegenwoordigt.

#### Output

De output van deze query is een enkel getal dat het gemiddelde van de CO2-concentratie in "Room 101" over de afgelopen 1 uur weergeeft. Bijvoorbeeld:

```flux
_time                _value
2025-05-06T10:00:00Z 450.5
```

- **`_time`**: De tijdstempel waarop het gemiddelde is berekend.
- **`_value`**: Het gemiddelde van de CO2-concentratie.

### Basics: buckets, measurements, tags, fields, retention ...

InfluxDB gebruikt een aantal kernconcepten:
- **Buckets**: Containers voor gegevens met een ingestelde retentieperiode.
- **Measurements**: De naam van een verzameling gegevenspunten (bijvoorbeeld "temperatuur").
- **Tags**: Metadata die gegevens beschrijft (bijvoorbeeld "locatie").
- **Fields**: De werkelijke gegevenswaarden (bijvoorbeeld "23.5°C").
- **Retention Policies**: Regels die bepalen hoe lang gegevens worden bewaard.

### Influx query syntax

InfluxDB gebruikt de Flux-taal voor query's. Een voorbeeld:

```flux
from(bucket: "sensor_data")
  |> range(start: -1h)
  |> filter(fn: (r) => r["_measurement"] == "CO2_levels")
  |> filter(fn: (r) => r["location"] == "Room 101")
  |> mean()
```

Meer info over de query syntax vind je [hier](https://docs.influxdata.com/influxdb/v2/query-data/get-started/query-influxdb/)

#### Measurements

##### Wat zijn measurements?

Measurements in InfluxDB zijn de logische namen die worden gebruikt om een verzameling gegevenspunten te groeperen. Ze fungeren als tabellen in een relationele database en bevatten velden en tags die de gegevens beschrijven. Een measurement kan bijvoorbeeld "temperature" of "CO2_levels" heten.

##### Werken met measurements

Measurements worden gebruikt om gegevens te organiseren en te groeperen. Hier is een voorbeeld van hoe je een measurement kunt gebruiken in een Flux-query:

```flux
from(bucket: "sensor_data")
  |> range(start: -1h)
  |> filter(fn: (r) => r["_measurement"] == "CO2_levels")
```

In dit voorbeeld worden alleen de gegevens uit de measurement "CO2_levels" geselecteerd.


#### Tags

##### Wat zijn tags?

Tags in InfluxDB zijn key-value paren die worden gebruikt om metadata aan gegevens toe te voegen. Ze worden vaak gebruikt om gegevens te groeperen of te filteren. Tags zijn geïndexeerd, wat betekent dat ze efficiënt kunnen worden gebruikt in query's.

##### Werken met tags

Tags worden gebruikt om gegevens te filteren of te groeperen. Hier is een voorbeeld van een Flux-query die gegevens filtert op basis van een tag:

```flux
from(bucket: "sensor_data")
  |> range(start: -1h)
  |> filter(fn: (r) => r["location"] == "Room 101")
```

In dit voorbeeld worden alleen de gegevens geselecteerd die de tag "location" hebben met de waarde "Room 101".


#### Fields

##### Wat zijn fields?

Fields in InfluxDB zijn de werkelijke gegevenswaarden die worden opgeslagen in een measurement. Ze bevatten numerieke, string-, of booleaanse waarden en worden niet geïndexeerd. Fields worden gebruikt voor berekeningen en analyses.

##### Werken met fields

Fields worden gebruikt om de werkelijke gegevenswaarden op te slaan. Hier is een voorbeeld van een Flux-query die gegevens filtert op basis van een field:

```flux
from(bucket: "sensor_data")
  |> range(start: -1h)
  |> filter(fn: (r) => r["_field"] == "value")
```

In dit voorbeeld worden alleen de gegevens geselecteerd die het field "value" bevatten.

#### Aggregaties

InfluxDB ondersteunt aggregaties, waarmee je complexe bewerkingen op tijdreeksen kunt uitvoeren. Aggregaties zijn essentieel voor het analyseren van trends, het berekenen van gemiddelden, en het samenvatten van grote datasets. In Flux, de querytaal van InfluxDB, kun je aggregaties uitvoeren met behulp van functies zoals `mean()`, `sum()`, `count()`, en `median()`.

##### Wat zijn aggregaties?

Aggregaties zijn bewerkingen die worden uitgevoerd op een verzameling gegevens om samenvattende informatie te verkrijgen. In de context van InfluxDB worden aggregaties vaak gebruikt om trends te analyseren, pieken te identificeren, en statistieken te berekenen over tijdreeksen. Aggregaties kunnen worden toegepast op velden binnen een meting en kunnen worden gecombineerd met filters en groeperingen.

##### Voorbeeld van een aggregatie

Hier is een voorbeeld van een Flux-query die de gemiddelde temperatuur berekent over de afgelopen 7 dagen:

```flux
from(bucket: "sensor_data")
  |> range(start: -7d)
  |> filter(fn: (r) => r["_measurement"] == "CO2_levels")
  |> mean()
```

In dit voorbeeld:
- **`range(start: -7d)`**: Selecteert gegevens van de afgelopen 7 dagen.
- **`filter(fn: (r) => r["_measurement"] == "CO2_levels")`**: Filtert alleen de CO2-metingen.
- **`mean()`**: Berekent het gemiddelde van de geselecteerde gegevens.

##### Aggregaties combineren

Je kunt meerdere aggregaties combineren in één query. Bijvoorbeeld, om zowel het gemiddelde als de maximale temperatuur te berekenen:

```flux
from(bucket: "sensor_data")
  |> range(start: -7d)
  |> filter(fn: (r) => r["_measurement"] == "CO2_levels")
  |> group(columns: ["location"])
  |> pivot(rowKey: ["_time"], columnKey: ["_field"], valueColumn: "_value")
  |> map(fn: (r) => ({
      location: r.location,
      avg_temp: mean(r.CO2_levels),
      max_temp: max(r.CO2_levels)
  }))
```

Hier worden de gegevens gegroepeerd per locatie en worden zowel het gemiddelde als de maximale temperatuur berekend.

### Demo in Java

Om InfluxDB te gebruiken in een Java-toepassing, voeg je de volgende Gradle-dependency toe:

```groovy
dependencies {
  implementation 'com.influxdb:influxdb-client-java:7.2.0'
}
```

Een voorbeeld van het schrijven en lezen van gegevens:

```java
/*
 * This source file was generated by the Gradle 'init' task
 */
package be.kuleuven;

import java.util.concurrent.TimeUnit;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.QueryApi;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.Bucket;
import com.influxdb.client.domain.BucketRetentionRules;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.query.FluxRecord;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class App {

    private static final String DB_URI = "http://localhost:8086";
    private static final String USERNAME = "influxdb";
    private static final String ORG = "influxdb";
    private static final String PASSWORD = "influxdb";
    private static final String TOKEN = "Rp5cZ0FlsUrZi94Mbyx881rLyrgqR2ig2weiot6TWaj_sjMDNmsXCt5gPyBvsMYZdnCraN5QoDs73RUoY1Yn7Q==";
    private static final String BUCKET = "co2";

    public static void main(String[] args) {
        InfluxDBClient influxDBClient = InfluxDBClientFactory.create(DB_URI, TOKEN.toCharArray(), ORG, BUCKET);
        clearBucket(influxDBClient);
        // Set correct time
        LocalDateTime customDateTime1 = LocalDateTime.of(2025, 5, 6, 8, 30, 0);
        Instant time1 = customDateTime1.atZone(ZoneId.systemDefault()).toInstant();

        // ADD a value (point)
        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();
        Point point1 = Point.measurement("co2_concentration")
                .addTag("location", "Room 403")
                .addField("value", 460)
                .time(time1.minusSeconds(60), WritePrecision.MS);
        Point point2 = Point.measurement("co2_concentration")
                .addTag("location", "Room 403")
                .addField("value", 440)
                .time(System.currentTimeMillis(), WritePrecision.MS);
        writeApi.writePoint(point1);
        writeApi.writePoint(point2);

        // GET all values in range
        List<CO2Point> points = getPointsInRange(influxDBClient,
                LocalDateTime.of(2025, 5, 4, 13, 30, 0),
                LocalDateTime.of(2025, 5, 10, 14, 30, 0));

        for (CO2Point co2Point : points) {
            System.out.println(co2Point);
        }

        // Print SUM
        System.out.println(getSumValues(influxDBClient,
                LocalDateTime.of(2025, 5, 4, 13, 30, 0),
                LocalDateTime.of(2025, 5, 10, 14, 30, 0)));

        influxDBClient.close();

    }

    public static List<CO2Point> getPointsInRange(InfluxDBClient influxDBClient, LocalDateTime startDate,
            LocalDateTime endDate) {
        QueryApi queryApi = influxDBClient.getQueryApi();
        String fluxQuery = String.format(
                "from(bucket: \"%s\") |> range(start: %s, stop: %s) |> filter(fn: (r) => r._measurement == \"co2_concentration\")",
                BUCKET, startDate.atZone(ZoneId.systemDefault()).toInstant().toString(),
                endDate.atZone(ZoneId.systemDefault()).toInstant().toString());

        List<CO2Point> myObjects = new ArrayList<>();
        queryApi.query(fluxQuery, ORG).forEach(table -> table.getRecords().forEach(record -> {
            LocalDateTime time = LocalDateTime.ofInstant(record.getTime(), ZoneId.systemDefault());
            double value = ((Number) record.getValueByKey("_value")).doubleValue();
            myObjects.add(new CO2Point(value, time));
        }));

        return myObjects;

    }

    public static double getSumValues(InfluxDBClient influxDBClient, LocalDateTime startDate,
            LocalDateTime endDate) {
        QueryApi queryApi = influxDBClient.getQueryApi();
        String fluxQuery = String.format(
                "from(bucket: \"%s\") " +
                        "|> range(start: %s, stop: %s) " +
                        "|> filter(fn: (r) => r._measurement == \"co2_concentration\") " +
                        "|> sum(column: \"_value\")",
                BUCKET, startDate.atZone(ZoneId.systemDefault()).toInstant().toString(),
                endDate.atZone(ZoneId.systemDefault()).toInstant().toString());

        List<FluxRecord> records = new ArrayList<>();
        queryApi.query(fluxQuery, ORG).forEach(table -> records.addAll(table.getRecords()));
        if (records.get(0).getValueByKey("_value") != null) {
            return (long) records.get(0).getValueByKey("_value");
        } else {
            return -1.0;
        }
    }

    public static void clearBucket(InfluxDBClient influxDBClient) {
        // Use DeleteApi to clear all data in the bucket
        influxDBClient.getDeleteApi().delete(
                OffsetDateTime.ofInstant(Instant.ofEpochMilli(0), ZoneId.systemDefault()), // Start time (epoch 0 to delete all data)
                OffsetDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()), // End time (current time)
                "", // Empty predicate to delete all measurements
                BUCKET, // Bucket name
                ORG // Organization name
        );
    }
}

```


**Vaak wil je omvormen van LocalDateTime naar milis of omgekeerd:**
```Java
// Method to convert LocalDateTime to milliseconds
public static long timeToMillis(LocalDateTime time) {
  return time.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
}

// Method to convert milliseconds to LocalDateTime
public static LocalDateTime millisToTime(long millis) {
  return LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(millis), java.time.ZoneId.systemDefault());
}
```

Meer informatie over tijdformaten in InfluxDB is te vinden in de [officiële documentatie](https://docs.influxdata.com/flux/v0/data-types/basic/time/).

#### Measurements in Java

Met de InfluxDB Java-client kun je gegevens schrijven naar een specifieke measurement. Hier is een voorbeeld:

```java
Point point = Point.measurement("CO2_levels")
    .addTag("location", "Room 101")
    .addField("value", 450)
    .time(Instant.now(), WritePrecision.MS);
writeApi.writePoint(point);
```

#### Tags in Java

Met de InfluxDB Java-client kun je tags toevoegen aan een gegevenspunt. Hier is een voorbeeld:

```java
Point point = Point.measurement("CO2_levels")
    .addTag("location", "Room 101")
    .addField("value", 450)
    .time(Instant.now(), WritePrecision.MS);
writeApi.writePoint(point);
```

#### Fields in Java

Met de InfluxDB Java-client kun je fields toevoegen aan een gegevenspunt. Hier is een voorbeeld:

```java
Point point = Point.measurement("CO2_levels")
    .addTag("location", "Room 101")
    .addField("value", 450)
    .time(Instant.now(), WritePrecision.MS);
writeApi.writePoint(point);
```

#### Influx Queries in Java

```java
public static void main(String[] args) {
        // Example usage
        LocalDateTime start = LocalDateTime.of(2023, 5, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 5, 7, 23, 59);

        List<FluxRecord> records = getPointsInRange(start, end);
        records.forEach(record -> System.out.println(record.getTime() + " -> " + record.getValueByKey("_value")));

        double sum = getSumOfValuesInRange(start, end);
        System.out.println("Sum of values: " + sum);
    }

public static List<FluxRecord> getPointsInRange(LocalDateTime start, LocalDateTime end) {
    QueryApi queryApi = influxDBClient.getQueryApi();
    String fluxQuery = String.format(
        "from(bucket: \"%s\") |> range(start: %s, stop: %s) |> filter(fn: (r) => r._measurement == \"my_measurement\")", BUCKET, startDate.atZone(ZoneId.systemDefault()).toInstant().toString(), endDate.atZone(ZoneId.systemDefault()).toInstant().toString());

    List<MyClass> myObjects = new ArrayList<>();
    queryApi.query(fluxQuery, ORG).forEach(table -> table.getRecords().forEach(record -> {
      LocalDateTime time = LocalDateTime.ofInstant(record.getTime(), ZoneId.systemDefault());
      double value = ((Number) record.getValueByKey("_value")).doubleValue();
      myObjects.add(new MyClass(value, time));
    }));

    return myObjects;
    
  }

```

#### Aggregaties in Java

Met de InfluxDB Java-client kun je ook aggregaties uitvoeren. Hier is een voorbeeld:

```java
import com.influxdb.client.*;
import com.influxdb.query.FluxTable;
import java.util.List;

public class AggregationExample {
    public static void main(String[] args) {
        String url = "http://localhost:8086";
        String token = "your-token";
        String org = "your-org";

        try (InfluxDBClient client = InfluxDBClientFactory.create(url, token.toCharArray())) {
            String flux = "from(bucket: \"sensor_data\") " +
                          "|> range(start: -7d) " +
                          "|> filter(fn: (r) => r[\"_measurement\"] == \"temperature\") " +
                          "|> mean()";

            List<FluxTable> tables = client.getQueryApi().query(flux, org);
            tables.forEach(table -> table.getRecords().forEach(record -> {
                System.out.println("Gemiddelde temperatuur: " + record.getValueByKey("_value"));
            }));
        }
    }
}
```

Met deze aanpak kun je aggregaties uitvoeren en analyseren in zowel de Flux-querytaal als in Java.

### Interessante videos
- [Algemene tutorial over CMD-commands met InfluxDb](https://www.youtube.com/watch?v=Vq4cDIdz_M8)
- [Full overview en visualisatie met Graphana](https://www.youtube.com/watch?v=gb6AiqCJqP0)
- [InfluxDb met Java 1](https://www.youtube.com/watch?v=89YOT0Cgtp4)
- [InfluxDb met Java 2](https://www.youtube.com/watch?v=EFnG7rUDvR4)
- [InfluxDb met VSCode: Flux](https://www.youtube.com/watch?v=yI5zCkhk6a0)


### Demo
<!-- TODO volgend jaar: -->

<!-- EXSOL -->
**_[Hier](/files/influxdb-demo.zip) vind je een zipfolder met een oplossing voor InfluxDb demo CO2**

<!-- TODO: volgend jaar opsplitsen in meerdere pagina's -->

### Oefening:
Maak een klein appje dat CO2 gegevens aanmaakt in een Java project en doorstuurt naar InfluxDb. Lees de waarden uit een zet een boolean op true wanneer CO2 een bepaalde hoeveelheid overschrijdt (= een raam open zetten). En zet weer op false wanneer de waarde weer daalt.