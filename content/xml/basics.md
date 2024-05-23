---
title: 1. XML Basics
---

# XML Data Storage

XML staat voor **Extensible Markup Language**. Het is een taal die we gebruiken om met tags gegevens te structureren. Een tag opent zich op volgende manier: `<boek>` en sluit op deze manier: `</boek>`.
Je herkent misschien het gebruik van deze tags van **HTML**? Dat komt omdat HTML en XML allebei gegroeid zijn uit dezelfde taal (SGML). 

## Waarom XML gebruiken?

XML is nog steeds een vaak voorkomende manier om data te structureren en definiëren. Denk bijvoorbeeld aan webhook calls waarbij je kan kiezen tussen een response te krijgen in XML of JSON. 
Al krijgt JSON de laaste jaren meer en meer voorkeur omwille van zijn makkelijke en leesbare syntax, toch zijn er nog instanties waarbij XML de enige optie is.

## Voorbeeld

```xml
<boeken>
  <boek>
    <titel>De gouden kooi</titel>
    <auteur>A.F.Th. van der Heijden</auteur>
    <jaar>1981</jaar>
  </boek>
  <boek>
    <titel>Het diner</titel>
    <auteur>Herman Koch</auteur>
    <jaar>2009</jaar>
  </boek>
  <boek>
    <titel>De ontdekking van de hemel</titel>
    <auteur>Harry Mulisch</auteur>
    <jaar>1982</jaar>
  </boek>
</boeken>
```

Wat opvalt is dat we in XML maar 1 root element hebben. De `<boeken>` tag is het root element van dit voorbeeld en er is geen enkele andere tag op dit niveau. 
Daarbinnen kunnen we dan wel meerdere keren het `<boek>` element definiëren.

### Oefening

* Voeg zelf een boek toe aan het bovenstaande voorbeeld
* Schrijf zelf een XML bestand dat studenten omschrijft. Volgende elementen moeten aanwezig zijn: studnr, naam, voornaam en goedbezig (boolean).