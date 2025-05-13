---
title: XML Basics
weight: 1
---

# XML Data Storage
XML staat voor **Extensible Markup Language**. Het is een taal die we gebruiken om met tags gegevens te structureren. Een tag opent zich op volgende manier: `<boek>` en sluit op deze manier: `</boek>`.
Je herkent misschien het gebruik van deze tags van **HTML**? Dat komt omdat HTML en XML allebei gegroeid zijn uit dezelfde taal (SGML). 

## Waarom XML gebruiken?
XML is nog steeds een vaak voorkomende manier om data te structureren en definiëren. Denk bijvoorbeeld aan webhook calls waarbij je kan kiezen tussen een response te krijgen in XML of JSON. 
Al krijgt JSON de laatste jaren meer en meer voorkeur omwille van zijn makkelijke en leesbare syntax, toch zijn er nog instanties waarbij XML de enige optie is.

### Toepassingen van XML
XML wordt vaak gebruikt in verschillende domeinen zoals:

- **Configuratiebestanden**: Veel softwaretoepassingen gebruiken XML om configuratie-instellingen op te slaan.
- **Data-uitwisseling**: XML is platformonafhankelijk en wordt vaak gebruikt om gegevens tussen verschillende systemen te verzenden.
- **Documentopslag**: XML wordt gebruikt in formaten zoals Microsoft Office-documenten (.docx, .xlsx) en andere opmaakstandaarden zoals EPUB.
- **Webservices**: SOAP-webservices maken gebruik van XML voor het structureren van berichten.

### Voordelen van XML
- **Menselijk leesbaar**: XML is eenvoudig te begrijpen en te lezen door mensen.
- **Flexibel**: Het is uitbreidbaar en kan worden aangepast aan specifieke behoeften.
- **Breed ondersteund**: XML wordt ondersteund door een breed scala aan tools en programmeertalen.
- **Hiërarchische structuur**: Het biedt een natuurlijke manier om gegevens met een hiërarchische relatie te modelleren.

Hoewel XML niet altijd de meest efficiënte keuze is, blijft het een krachtige en veelzijdige standaard voor gegevensrepresentatie.

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

- Voeg zelf een boek toe aan het bovenstaande voorbeeld

<!-- EXSOL -->
<details closed>
<summary><i><b><span style="color: #03C03C;">Solution:</span> Klik hier om de oplossing te zien/verbergen</b></i>🔽</summary>
<p>

```XML
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
 <boek>
    <titel>Databases boek</titel>
    <auteur>Kris Aerts</auteur>
    <jaar>2024</jaar>
  </boek>
</boeken>
```

</p>
</details>

- Schrijf zelf een XML bestand dat studenten omschrijft. Volgende elementen moeten aanwezig zijn: studnr, naam, voornaam en goedbezig (boolean).

<!-- EXSOL -->
<details closed>
<summary><i><b><span style="color: #03C03C;">Solution:</span> Klik hier om de oplossing te zien/verbergen</b></i>🔽</summary>
<p>

```XML
<school>
    <student>
        <studnr>123</studnr>
        <naam>Trekhaak</naam>
        <voornaam>Jaak</voornaam>
        <goedbezig>false</goedbezig>
    </student>
    <student>
        <studnr>456</studnr>
        <naam>Peeters</naam>
        <voornaam>Jos</voornaam>
        <goedbezig>false</goedbezig>
    </student>
    <student>
        <studnr>890</studnr>
        <naam>Dongmans</naam>
        <voornaam>Ding</voornaam>
        <goedbezig>true</goedbezig>
    </student>
</school>
```

</p>
</details>

{{% notice info %}}
Meer info over XML vind je [hier](https://www.w3schools.com/xml/default.asp)
{{% /notice %}}