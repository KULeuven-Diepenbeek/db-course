---
title: 4. Advanced map-red. queries
---

Deze oefeningen gaan verder op de database die je hebt opgezet in [2. document stores](/db-course/nosql/documentstores). Herinstalleer indien nodig en download de benodigde gegevens via de instructies (2.2 Oefeningen: **voorbereidingswerk**) in die link. Start voor onderstaande oefeningen de lokale _CouchDB Server_ en de _Admin Console_ (Project Fauxton) opnieuw op. 

Zoals ook op de [PouchDB docs](https://pouchdb.com/guides/queries.html) vermeld staat; zijn **mapreduce** queries niet altijd nodig:

- Documenten op `_id` raadplegen gaat door middel van de Curl REST API
- Documenten sorteren of simpele queries uitvoeren gaat door middel van de Mango API, zoals reeds gezien. Dit zijn simpele queries, maar die volstaan meestal.
- Indien de DB store < `100.000` records bevat, zoals de onze, kan je ook simpelweg alles **in-memory** inladen (bijvoorbeeld in de browser), en met javascript zelf verder filteren:

```javascript
const db = pouchdb.get(); // zoiets
// ...
const skillsOfBigCourses = db.filter(doc => {
    return doc.ECTS > 6
}).map(doc => {
    return skills
})
// gebruik dit in een template HTML factory
```


#### Emit

Een mapreduce query is in PouchDB uitvoerbaar met `db.query()` en in CouchDB deel van de `_view` API. Klik dus op het plusje `+` bij All Documents en dan op "new view":

![](/img/couchview.jpg)

Daar kan je een nieuwe "map" functie aanmaken:

```javascript
function (doc) {
  emit(doc._id, 1);
}
```

Merk op dat hier de **JavaScript** syntax geldt. `emit()` betekent "geef als key deze waarde terug voor elk gevonden document". Als je dit verandert naar `doc.title` wordt er een view aangemaakt die documenten op titel bewaart, om daar zeer snel in te kunnen zoeken. Bovenstaande functie wordt uitgevoerd **voor elk document**, vandaar de "map" in de naam. Het zou kunnen dat je filtert, vandaar de "reduce" in de naam. 

Ik kan dus gewoon `if()` gebruiken, en zo documenten filteren. Alle cursussen gegeven in het tweede jaar of later:

```javascript
function (doc) {
    if(doc.year > 1) {
        emit(doc.title, 1);
    }
}
```

#### Aggregeren

Stel dat ik de totale `ECTS` punten wil verzamelen van alle Belgische vakken in de database. Dus: eerst filteren op `country` property, en daarna de som nemen van alle `ECTS` properties. Hoe doe je zoiets in SQL? Met `SUM()` en `GROUP BY`:

```sql
SELECT SUM(ECTS) FROM courses
WHERE country = "Belgium"
GROUP BY country
```

Hoe doe je zoiets in NoSQL/Mongo/CouchDB? Met [Reduce Functions](http://127.0.0.1:5984/_utils/docs/ddocs/ddocs.html#reduce-and-rereduce-functions). Je kan in Fauxton bij het bewerken van je view een **CUSTOM** waarde in de Reduce combobox selecteren:

![](/img/couchreduce.jpg)

Wat is die derde `rereduce` parameter? Volgens de docs:

> Reduce functions take two required arguments of keys and values lists - the result of the related map function - and an optional third value which indicates if rereduce mode is active or not. Rereduce is used for additional reduce values list, so when it is true there is no information about related keys (first argument is null).

Rereducen wordt typisch uitgevoerd bij een cluster met verschillende CouchDB Nodes die de data verdeelt. CouchDB ontvangt _groepen van inputs_ in plaats van alles in één vanwege performantie optimalisatie. Dit systeem is [visueel uitgelegd in deze primer](https://blog.pablobm.com/2019/07/18/map-reduce-with-couchdb-a-visual-primer.html), maar is voor ons niet van toepassing.

Dus, map functie om te filteren op België:

```javascript
function (doc) {
    if(doc.country == "Belgium") {
      emit(doc._id, doc.ECTS);
    }
}
```

Door `ECTS` in `emit()` mee te geven (als VALUE!) kunnen we in de `reduce` functie de array `values` manipuleren. En de reduce functie om de ECTS punten op te tellen:

```javascript
function (keys, values, rereduce) { 
  return sum(values);
}
```

`sum()` is een ingebouwde CouchDB functie. Dit kan ook manueel op de functionele JS `reduce()` manier:

```javascript
function (keys, values, rereduce) { 
  return values.reduce((a, b) => a + b);
}
````

Klik op "Run Query". De resultaten zijn de resultaten van de MAP - de Reduce value moet je expliciet enablen door vanboven rechts op "Options" te klikken, en dan "Reduce" aan te vinken:

![](/img/queryreduce.jpg)

Merk op dat je met "Group Level" moet spelen (Op `None` zetten) om de groepering te doen werken, anders gaat de reduce functie de som nemen op elk indiviudeel document, wat uiteraard geen correct som is. 

Merk op dat reduce functies _verschillende keren kunnen worden opgeroepen_ - en dat reduce reeds kan beginnen voordat map klaar is met zijn werk. Deze maatregelen zijn genomen om vlot om te kunnen gaan met miljarden records, verticaal verspreid over verschillende clusters. 

### Oefeningen

1. Maak een nieuwe view die documenten teruggeeft die in de titel het woord "project" bevatten. Werk case-_insensistive_. Vergeet niet dat het zou kunnen dat sommige documenten géén `title` property hebben, of deze `null` is. Wat dan? 
2. Schrijf een reduce query die voor alle bovenstaande titels het aantal cursussen weergeeft (enkel het aantal is voldoende) dat `explicit` op `true` heeft staan. 
3. Schrijf een view die de som neemt van alle `ECTS` punten van alle cursussen. Doe dit op drie manieren:
    - Met de ingebouwde `_sum` functie.
    - Met een custom reduce en `sum()` zoals hierboven in het voorbeeld
    - Met een custom reduce die `values.reduce()` gebruikt: zie docs [Array.prototype.reduce()](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/Reduce).
    - Wat is volgens jou het fundamentele verschil tussen deze 3 opties? Op welk gebied? 
4. Kopieer je LightCouch oefening van [2. Document stores](/db-course/nosql/documentstores) als een nieuw Java project. Programmeer nu in Java om de view die je hebt gemaakt in oefening 2 op te roepen met `dbClient.view()`. Zie [LightCouch docs](http://www.lightcouch.org/getstarted.html).
5. Schrijf een view die het aantal optionele cursussen weergeeft waarvan "motivate others" een skill is. 

### Denkvragen

1. Waarom is het niet mogelijk in NoSQL databases om een simpele query uit te voeren die bijvoorbeeld auteurs opvraagt ouder dan een bepaalde leeftijd, en dan alle titels per auteur teruggeeft? (Hint: p. 321)
2. Wat is het verschil tussen `emit(doc._id, 1)` en `emit(doc._id, doc.year)`?
3. Wat is het verschil tussen `map()`, `reduce()` en `filter()` in Javascript? Hint: Zie Mozilla [MDN Web Docs](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/filter). 
