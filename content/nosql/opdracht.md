---
title: VERPLICHTE opdracht
draft: false
weight: 4
---

## Opdracht MongoDb met als deadline vrijdag 9 mei 2025 23u59

Voor de verplichte opdracht meld je je aan onder de correcte naam bij volgende `Github Classroom`. En pull je de repository van [**Opdracht rond MongoDb**](https://classroom.github.com/a/QOmM-zNf). Deze repository bevat een Java Gradle project met een aantal `TODO`'s die je moet oplossen. Hieronder staat de opdracht nog beschreven:

### Opdracht: 
<!-- TODO: Volgend jaar met schema-validatie, letten op ontbrekende eigenschappen in de JSON ... -->
Je krijgt alweer een startproject met de `Speler` en `Club` klasse gegeven, je krijgt ook al de start van de `SpelerRepository` klasse waarvan je weer zelf de implementaties van de gevraagde methoden moet programmeren volgens de `TODO`s zodat alle testen slagen. Alle dependencies en imports zijn al ingevoegd en correct, het kan echter zijn dat voor jouw manier een extra import nodig is. **Je moet er ook wel voor zorgen dat je op je lokale MongoDb server al een database hebt met de naam "Tennisvlaanderen" en een collectie "spelers". Dit kan je eenvoudig doen via de MongoDb Compass GUI zoals we in de les gezien hebben.**


Maak voor de twee laatste methoden `getFilteredSpelers` en `getAveragePointsOfAllPlayers` gebruik van aggregaties.

Een speler ziet er als volgt uit (en je mag ervan uitgaan dat elk json object altijd de nodige properties heeft om het om te zetten naar een Java Speler object):
```json
{
    "tennisvlaanderenId": 1,
    "naam": "Tom Janssens",
    "punten": 10,
    "prof": true,
    "club": {
      "naam": "T.P.Tessenderlo",
      "gemeente": "Tessenderlo-Ham"
    },
    "likedTennisvlaanderenIds": [
      2,
      3
    ]
  }
```


**Push voor de deadline van vrijdag 9 mei 2025 23u59 je oplossingen naar je repository**

Veel succes! Je mag me altijd contacteren via [arne.duyver@kuleuven.be](mailto::arne.duyver@kuleuven.be) voor vragen of in de les aanspreken.
