---
title: Timeseries database met InfluxDB
draft: false
weight: 1
---

## Timeseries database basics

### Introductie

Een timeseries database (TSDB) is een database die geoptimaliseerd is voor het opslaan en ophalen van tijdgestempelde gegevens. Dit type database wordt vaak gebruikt in toepassingen zoals IoT, monitoring, en financiële analyses, waar gegevens in de tijd worden verzameld en geanalyseerd. TSDB's zijn ontworpen om efficiënt om te gaan met grote hoeveelheden data die continu worden toegevoegd en om snelle queries uit te voeren op basis van tijd.

#### Verschil met SQL- en NoSQL-databases

Time Series Databases (TSDB's) onderscheiden zich van traditionele SQL- en NoSQL-databases door hun focus op tijdgestempelde gegevens. Terwijl SQL-databases sterk zijn in relationele, gestructureerde data en NoSQL-databases flexibel omgaan met verschillende datatypes, zijn TSDB's specifiek ontworpen voor tijdsgebaseerde workloads.

- **Indexering en opslag**: TSDB's gebruiken opslagformaten en indexering die geoptimaliseerd zijn voor tijdsfilters en append-heavy workloads.
- **Lees- en schrijfbewerkingen**: TSDB's bieden snelle ingest (veel writes) en efficiiente queries over tijdvensters.
- **Waarom niet gewoon SQL of NoSQL?**: Het kan, maar vaak met meer overhead en minder performantie voor typische tijdreeksanalyses.

In een wereld waar tijdsgevoelige gegevens steeds belangrijker worden, bieden TSDB's een sterke oplossing voor efficiënt beheer en analyse.

#### Link tussen IoT, Industry 4.0 en timeseries databases

IoT (Internet of Things) en Industry 4.0 genereren enorme hoeveelheden tijdgestempelde gegevens. Sensoren in IoT-apparaten en industriële machines verzamelen continu waarden zoals temperatuur, druk en trillingen. Die data is essentieel om processen te monitoren, te analyseren en te optimaliseren.

Timeseries databases spelen hierin een cruciale rol door:
- **Efficient opslaan van data** met lage overhead.
- **Snelle tijdsqueries** op recente en historische data.
- **Geavanceerde analyses** zoals aggregaties, trenddetectie en anomaliedetectie.

## InfluxDB: een open source timeseries database

### Belangrijk over querytaal

In recente InfluxDB-versies (InfluxDB 3) werk je primair met **SQL** voor query's. 

### Andere populaire timeseries databases

Naast InfluxDB zijn er andere populaire TSDB's zoals:
- **Prometheus**: gericht op monitoring en alerting.
- **Graphite**: sterk in metriekvisualisatie.
- **OpenTSDB**: gebouwd op HBase voor schaalbaarheid.

### Overzicht van gebruik

In InfluxDB werk je doorgaans met:
- **Line Protocol** om data te schrijven.
- **SQL** om data op te vragen en te aggregeren.
- Eventueel dashboards/visualisatie via externe tools.

### Voorbeeld: CO2-sensor voor een klaslokaal

Stel dat je een CO2-sensor in een klaslokaal hebt. Je kunt de data logisch modelleren met:
- **Measurement/table**: `CO2_concentration`
- **Tags/dimensies**: `location`, `sensor_id`
- **Fields/metriekwaarde**: `value`

#### Voorbeelddata in Line Protocol formaat

Sla het onderstaande op als `co2_data.lp` en importeer het in InfluxDB. Dit is het native **Line Protocol** formaat: `measurement,tag=waarde field=waarde unix-timestamp-seconden`.

```
CO2_concentration,location=Room_101,sensor_id=s1 value=412.0 1778400000
CO2_concentration,location=Room_101,sensor_id=s1 value=438.0 1778403600
CO2_concentration,location=Room_101,sensor_id=s1 value=875.0 1778407200
CO2_concentration,location=Room_101,sensor_id=s1 value=1120.0 1778410800
CO2_concentration,location=Room_101,sensor_id=s1 value=980.0 1778414400
CO2_concentration,location=Room_101,sensor_id=s1 value=430.0 1778418000
CO2_concentration,location=Room_101,sensor_id=s1 value=820.0 1778421600
CO2_concentration,location=Room_101,sensor_id=s1 value=1050.0 1778425200
CO2_concentration,location=Room_403,sensor_id=s2 value=420.0 1778400000
CO2_concentration,location=Room_403,sensor_id=s2 value=610.0 1778403600
CO2_concentration,location=Room_403,sensor_id=s2 value=1340.0 1778407200
CO2_concentration,location=Room_403,sensor_id=s2 value=1580.0 1778410800
CO2_concentration,location=Room_403,sensor_id=s2 value=1210.0 1778414400
CO2_concentration,location=Room_403,sensor_id=s2 value=450.0 1778418000
CO2_concentration,location=Room_403,sensor_id=s2 value=990.0 1778421600
CO2_concentration,location=Room_403,sensor_id=s2 value=1420.0 1778425200
CO2_concentration,location=Room_101,sensor_id=s1 value=415.0 1778486400
CO2_concentration,location=Room_101,sensor_id=s1 value=455.0 1778490000
CO2_concentration,location=Room_101,sensor_id=s1 value=890.0 1778493600
CO2_concentration,location=Room_403,sensor_id=s2 value=425.0 1778486400
CO2_concentration,location=Room_403,sensor_id=s2 value=780.0 1778490000
CO2_concentration,location=Room_403,sensor_id=s2 value=1650.0 1778493600
```

De data stelt twee lokalen voor over twee dagen (2026-05-10 en 2026-05-11), met metingen per uur. Waarden boven 1000 ppm duiden op slechte luchtkwaliteit.

Importeer via de `influx3` CLI:

```bash
influx3 write --database classroom_data --precision second --file co2_data.lp
```

Of via de InfluxDB UI: ga naar **Load Data → Line Protocol** en plak de inhoud van het bestand.

### Basics: bucket/database, measurement, tags, fields, retention

Belangrijke concepten in InfluxDB:
- **Database (of bucket, afhankelijk van versie)**: container voor tijdreeksdata.
- **Measurement**: logische naam van een reeks metingen (vergelijkbaar met een tabelconcept).
- **Tags**: dimensionele metadata (bijv. `location`) waarop je vaak filtert/groepeert.
- **Fields**: de echte meetwaarden (bijv. `value = 460`).
- **Retention**: hoe lang data bewaard blijft.

## Influx query syntax (SQL)

Voorbeeld: alle CO2-metingen van de afgelopen 7 dagen in lokaal Room_101.

```sql
SELECT AVG(value) AS avg_co2
FROM "CO2_concentration"
WHERE time >= now() - INTERVAL '7 days'
  AND location = 'Room_101';
```

### Stap-voor-stap uitleg

1. **`FROM "CO2_concentration"`**
   Selecteert de measurement/table waarin de CO2-data staat.

2. **`WHERE time >= now() - INTERVAL '7 days'`**
   Beperkt de query tot de laatste 7 dagen.

3. **`AND location = 'Room_101'`**
   Filtert op een specifieke tag/dimensie.

4. **`AVG(value)`**
   Berekent het gemiddelde van het veld `value`.

### Output

Een typische output bevat een rij met het berekende gemiddelde:

```text
avg_co2
-------
716.81
```

Meer info over SQL in InfluxDB vind je in de [officiële InfluxDB 3-documentatie](https://docs.influxdata.com/influxdb3/core/query-data/sql/).

## Measurements

### Wat zijn measurements?

Measurements in InfluxDB zijn logische namen om tijdreeksdatapunten te groeperen. In SQL-context kun je ze benaderen zoals tabellen.

### Werken met measurements

```sql
SELECT time, location, value
FROM "CO2_concentration"
WHERE time >= now() - INTERVAL '7 days';
```

## Tags

### Wat zijn tags?

Tags zijn key-value metadata om data te beschrijven, filteren en groeperen. Denk aan `location`, `sensor_id`, `room_type`.

### Werken met tags

```sql
SELECT time, value
FROM "CO2_concentration"
WHERE location = 'Room_101'
  AND time >= now() - INTERVAL '7 days';
```

## Fields

### Wat zijn fields?

Fields zijn de effectieve meetwaarden (numeriek, string, boolean, ...). In dit voorbeeld is `value` de CO2-concentratie.

### Werken met fields

```sql
SELECT time, value
FROM "CO2_concentration"
WHERE value > 800
  AND time >= now() - INTERVAL '7 days';
```

## Aggregaties

Aggregaties zijn essentieel in tijdreeksanalyse om trends en samenvattingen te berekenen.

### Wat zijn aggregaties?

Aggregaties zijn bewerkingen op een set metingen, zoals `AVG`, `SUM`, `COUNT`, `MIN`, `MAX`.

### Voorbeeld van een aggregatie

```sql
SELECT AVG(value) AS avg_co2
FROM "CO2_concentration"
WHERE time >= now() - INTERVAL '7 days';
```

### Aggregaties combineren

```sql
SELECT location,
       AVG(value) AS avg_co2,
       MAX(value) AS max_co2,
       MIN(value) AS min_co2
FROM "CO2_concentration"
WHERE time >= now() - INTERVAL '7 days'
GROUP BY location
ORDER BY location;
```

## Demo in Java

### Gradle dependencies

Voeg de volgende dependencies toe aan `build.gradle`:

```groovy
plugins {
    id 'application'
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

dependencies {
    // Add InfluxDB 3 Java client dependency
    implementation 'com.influxdb:influxdb3-java:1.1.0'
}

run {
    standardInput = System.in
    jvmArgs = [
        '--add-opens=java.base/java.nio=org.apache.arrow.memory.core,ALL-UNNAMED'
    ]
}
```

Meer info: [InfluxDB 3 Java client op GitHub](https://github.com/InfluxCommunity/influxdb3-java).

### Code

Het onderstaand voorbeeld gebruikt de officiële `influxdb3-java` client om:
1. CO2-datapunten te schrijven via `Point`.
2. Data op te vragen met een SQL-query.

```java
package be.kuleuven;

import com.influxdb.v3.client.InfluxDBClient;
import com.influxdb.v3.client.Point;

import java.time.Instant;
import java.util.Arrays;
import java.util.stream.Stream;

public class App {

    // Vind je cluster-URL via: InfluxDB Cloud UI → "Load Data" → "API Tokens" → cluster hostname
    private static final String HOST = "https://<your-cluster>.influxdata.com";
    private static final String DATABASE = "classroom_data";
    private static final String TOKEN = "<YOUR_TOKEN_HERE>";

    public static void main(String[] args) throws Exception {
        try (InfluxDBClient client = InfluxDBClient.getInstance(HOST, TOKEN.toCharArray(), DATABASE)) {
            writeSampleData(client);
            queryAverage(client);
        }
    }

    // Schrijft 2 CO2-datapunten.
    private static void writeSampleData(InfluxDBClient client) throws Exception {
        Point point1 = Point.measurement("CO2_concentration")
                .setTag("location", "Room_403")
                .setTag("sensor_id", "s1")
                .setField("value", 460L)
                .setTimestamp(Instant.now().minusSeconds(60));

        Point point2 = Point.measurement("CO2_concentration")
                .setTag("location", "Room_403")
                .setTag("sensor_id", "s1")
                .setField("value", 440L)
                .setTimestamp(Instant.now());

        client.writePoint(point1);
        client.writePoint(point2);
        System.out.println("Data geschreven.");
    }

    // Vraagt gemiddelde CO2 op via SQL.
    private static void queryAverage(InfluxDBClient client) throws Exception {
        String sql = "SELECT AVG(value) AS avg_co2 " +
                     "FROM \"CO2_concentration\" " +
                     "WHERE time >= now() - INTERVAL '7 days' " +
                     "AND location = 'Room_403'";

        try (Stream<Object[]> rows = client.query(sql)) {
            rows.forEach(row -> System.out.println("avg_co2 = " + row[0]));
        }
    }
}
```

### Tijd omzetten in Java

```java
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public static Instant localDateTimeToInstant(LocalDateTime time) {
    return time.atZone(ZoneId.systemDefault()).toInstant();
}

public static LocalDateTime instantToLocalDateTime(Instant instant) {
    return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
}
```

### SQL-queries in Java (voorbeeld)

```java
String sql = "SELECT time, location, value " +
             "FROM \"CO2_concentration\" " +
             "WHERE time >= now() - INTERVAL '7 days' " +
             "ORDER BY time DESC";

try (Stream<Object[]> rows = client.query(sql)) {
    rows.forEach(row -> System.out.println(Arrays.toString(row)));
}
```

### Aggregaties in Java (voorbeeld)

```java
String sql = "SELECT location, AVG(value) AS avg_co2, MAX(value) AS max_co2 " +
             "FROM \"CO2_concentration\" " +
             "WHERE time >= now() - INTERVAL '7 days' " +
             "GROUP BY location";

try (Stream<Object[]> rows = client.query(sql)) {
    rows.forEach(row ->
        System.out.println("location=" + row[0] + ", avg=" + row[1] + ", max=" + row[2])
    );
}
```

Met deze aanpak voer je aggregaties uit via SQL en verwerk je de resultaten in Java.

### Kolommen omzetten naar Java-types (voorbeeld)

De `query()`-methode geeft een `Stream<Object[]>` terug. Elke `Object[]` bevat de kolomwaarden in de volgorde van de `SELECT`. Je cast elke waarde naar het verwachte type:

- `time` → `java.time.Instant`
- `location` / `sensor_id` → `String`
- `value` / aggregaten → `Double`

```java
String sql = "SELECT time, location, sensor_id, value " +
             "FROM \"CO2_concentration\" " +
             "WHERE time >= now() - INTERVAL '7 days' " +
             "ORDER BY time ASC";

try (Stream<Object[]> rows = client.query(sql)) {
    rows.forEach(row -> {
        Instant time     = (Instant) row[0];
        String location  = (String)  row[1];
        String sensorId  = (String)  row[2];
        Double value     = (Double)  row[3];

        LocalDateTime ldt = LocalDateTime.ofInstant(time, ZoneId.systemDefault());
        System.out.printf("%s | %-8s | %-4s | %.1f ppm%n", ldt, location, sensorId, value);
    });
}
```

Voorbeeld output:

```text
2026-05-10T10:00 | Room_101 | s1   | 875.0 ppm
2026-05-10T11:00 | Room_101 | s1   | 1120.0 ppm
...
```

> **Let op**: het exacte runtime-type hangt af van de JDBC/Arrow-laag van de client. Gebruik `row[i].getClass().getSimpleName()` om het type te inspecteren als een cast mislukt.

## Interessante video's

- [Algemene tutorial over CMD-commands met InfluxDB](https://www.youtube.com/watch?v=Vq4cDIdz_M8)
- [Full overview en visualisatie met Grafana](https://www.youtube.com/watch?v=gb6AiqCJqP0)
- [InfluxDB met Java 1](https://www.youtube.com/watch?v=89YOT0Cgtp4)
- [InfluxDB met Java 2](https://www.youtube.com/watch?v=EFnG7rUDvR4)

## Demo

<!-- TODO volgend jaar: -->

<!-- EXSOL -->
**_[Hier](/files/influxdb-demo.zip) vind je een zipfolder met een oplossing voor InfluxDB demo CO2_**

<!-- TODO: volgend jaar opsplitsen in meerdere pagina's -->

## Oefeningen

### Oefening 1

Maak een klein appje dat CO2-gegevens aanmaakt in een Java-project en doorstuurt naar InfluxDB. Lees de waarden uit en zet een boolean op `true` wanneer CO2 een bepaalde drempel overschrijdt (raam open). Zet die terug op `false` wanneer de waarde weer daalt.

### Oefening 2

MongoDB kan ook gebruikt worden als Time Series databank. Vergelijk de werking van MongoDB met InfluxDB voor dit CO2-scenario. Maak een kopie van de Java-applicatie en pas ze aan zodat ze met MongoDB werkt.