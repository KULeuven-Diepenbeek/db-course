---
title: 1. API Basics
---

## Layered Application Tiers

In software engineering worden applicaties logisch opgesplitst in verschillende "tiers". Een typische 3-Tier webapplicatie bestaat uit 3 lagen: de laag die de gebruiker te zien krijgt---de UI, bestaande uit HTML en CSS, de _backend_---een server waar de requests naartoe worden gestuurd en die de aanvragen verwerkt, en een _data laag_ die onze database voorstelt. Onderstaand schema vat dit samen (via [Trevor N. Mudge](https://www.researchgate.net/figure/A-Typical-3-Tier-Server-Architecture-Tier-1-Web-Server-Tier-2-Application-Server-Tier_fig1_221147997)):

![](/img/tier3.png)

In de praktijk variëert deze tier benadering van project tot project.

Tot nu toe in dit vak hebben we ons toegelegd op Tier 3: de data laag. Zonder frontend applicatie laag kan een gebruiker echter niet interageren met deze database; er it dus minstens één extra tier nodig. Bovenstaand schema kwam terug in het vak Software Engineering Skills; namelijk de [SESsy library webapp](https://kuleuven-diepenbeek.github.io/ses-course/extra/sessy/), die ook bestaat uit drie conceptuele lagen. 

Om ons in het vak databases te kunnen focusen op de data en de integratie van de data met de software gaan wij ons toeleggen op Tier 2 + 3. Het webgedeelte valt weg en om een UI te maken herbruiken we de opgebouwde JavaFX kennis van het eerstejaarsvak _Software Ontwerp in Java_ (INF1). We gaan dus terug **Java/Kotlin applicaties** maken en die verbinden met onze database schema's. Een simpele 2-tier applicatie is ook wel een client-server applicatie genoemd. In ons geval is de server de database, die in principe op een andere machine kan gedeployed worden. Voor de oefeningen vereenvoudigen we dit systeem door gebruik te maken van een _embedded_ database die in het lokaal geheugen kan draaien. 

We teren dus op de volgende kennis:

- Het opstellen van Gradle projecten in Java of Kotlin (_SES en AppDev_);
- JavaFX UIs bouwen (_INF1_);
- Databases ontwerpen en koppelen (_Databases_).

{{% notice warning %}}
Problemen met je JDK versie en Gradle versies? Raadpleeg de [Gradle Compatibiility Matrix](https://docs.gradle.org/current/userguide/compatibility.html). Gradle 6.7 of hoger ondersteunt JDK15. Gradle 7.3 of hoger ondersteunt JDK17. Let op met syntax wijzigingen bij Gradle 7+!<br/>
Je Gradle versie verhogen kan door de URL in `gradle/gradlew.properties` te wijzigen.
{{% /notice %}}

Gradle dependency of Git source control problemen? Grijp terug naar [de cursus van SES](https://kuleuven-diepenbeek.github.io/ses-course/).
