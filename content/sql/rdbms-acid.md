---
title: 3. ACID
---

![](/slides/img/acid.jpg)

ACID is een term die we gebruiken binnen databases dat een lijst van voorwaarden omschrijft waar dat database systeem aan moet voldoen. De regels van ACID worden over het algemeen geïmplementeerd door het concept van [Transacties](/transacties/basics). ACID omschrijft vier principes:

- Atomicity
- Consistency
- Isolation
- Durability

## Atomicity

Transacties bestaan vaak uit meerdere **statements**. Atomicity verwacht dat al deze statements als **één geheel** worden beschouwd. Ofwel faalt alles, ofwel slaagt alles. Zo wordt er nooit slechts een deel van de changes bewaard. 

Het gevolg hiervan is dat de staat van een transactie niet gezien kan worden door andere gebruikers. Stel we hebben een typische bankoverschrijving die €10 gaat overschrijven van de rekening van Alice naar de rekening van Bob. 

{{<mermaid align="left">}}
sequenceDiagram
    Note over Application,DB: BEGIN TRANSACTION
    DB-->DB: Rekening van Alice: €100
    DB-->DB: Rekening van Bob: €150
    Application->>DB: UPDATE Account SET Amount -= 10 WHERE Owner = 'Alice'
    DB-->DB: Rekening van Alice: €100
    DB-->DB: Rekening van Bob: €150
    Application->>DB: UPDATE Account SET Amount += 10 WHERE Owner = 'Bob'
    Note over Application,DB: COMMIT TRANSACTION
    DB-->DB: Rekening van Alice: €90
    DB-->DB: Rekening van Bob: €160
{{< /mermaid >}}

## Consistency

Consistency zorgt ervoor dat een database altijd in een consistente staat moet blijven. Met andere woorden, er moet altijd voldaan worden aan de **constraints** die op de database gedfinieerd zijn. Dit kan een referentiële constraint zijn, als er een rij wordt verwijderd die nog gebruikt wordt in een andere tabel waar onderliggend een referentiële constraint op ligt. 

Neem bovenstaand voorbeeld waarbij Alice geld overschrijft naar de rekening van Bob. Maar nu wil ze €150 overschrijven. We hebben op de Account tabel een **CHECK CONSTRAINT** gedefinieerd dat de Amount altijd groter of gelijk aan 0 moet zijn. Wat gebeurt er dan?

{{<mermaid align="left">}}
sequenceDiagram
    Note over Application,DB: BEGIN TRANSACTION
    DB-->DB: Rekening van Alice: €100
    DB-->DB: Rekening van Bob: €150
    Application->>DB: UPDATE Account SET Amount -= 150 WHERE Owner = 'Alice'
    rect rgb(194, 24, 7)
    DB-->DB: Error: Amount should be >= 0
    end
    Note over Application,DB: ROLLBACK TRANSACTION
    DB-->DB: Rekening van Alice: €100
    DB-->DB: Rekening van Bob: €150
{{< /mermaid >}}

De transactie wordt teruggedraaid. Er werd geen geld afgehaald van de rekening van Alice en Bob heeft ook geen geld gekregen. We zitten opnieuw in een geldige consistente staat.

## Isolation

Als je een query of transactie uitvoert op een database, ga je zelden als enige gebruiker actief zijn. Er zullen wellicht andere transacties uitgevoerd worden terwijl jij de jouwe uitvoert. Om ervoor te zorgen dat deze geen invloed hebben op elkaar worden er **locks** gelegd op een set van data. Daar bovenop worden **isolation levels** gedefinieerd die bepalen wat andere gebruikers mogen zien en lezen van andere transacties.

Dit komt meer detail aan bod in een [volgend hoofdstuk](/transacties/concurrency-control/#locking).

## Durability

Als we een transactie afronden en hij wordt gecommit dan is die nog steeds committed ook in het geval van een crash of stroomonderbreking. Veel DBMS providers lossen dit op door data weg te schrijven in [non-volatile memory](https://en.wikipedia.org/wiki/Non-volatile_memory) en door gebruik te maken van een **transaction log**. 

Dat laatste is een logboek van alle acties die uitgevoerd werden op een database, die opnieuw uitgevoerd kunnen worden als een database gerestored moet worden vanaf een eerdere staat.