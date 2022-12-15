---
title: 2. XSD
---

# XML Schema Definition

Wanneer we met XML communiceren tussen twee verschillende partijen, hebben we natuurlijk ook spelregels nodig. Daarvoor kunnen we een **XML schema** of **XSD** gebruiken. Daarin leggen we vast:

* welke tags wel of niet mogen voorkomen, 
* in welke volgorde die moeten staan, 
* hoe vaak een element mag voorkomen,
* of een element optioneel of verplicht is,
* het datatype van het element,
* welke attributen op een tag toegelaten zijn

Ons vorige XML representatie van een collectie boeken kan met volgende XSD beschreven worden:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema">
  <xs:element name="boeken" type="boekenType"/>
  <xs:complexType name="boekenType">
    <xs:sequence>
      <xs:element name="boek" type="boekType" maxOccurs="unbounded"/>
    </xs:sequence>
  </xs:complexType>
  <xs:complexType name="boekType">
    <xs:sequence>
      <xs:element name="titel" type="xs:string"/>
      <xs:element name="auteur" type="xs:string"/>
      <xs:element name="jaar" type="xs:integer"/>
    </xs:sequence>
  </xs:complexType>
</xs:schema>
```

Het `boeken` element, ons root element wordt als eerste beschreven en krijgt een custom type toegekend, namelijk een zelf gedefinieerd type `boekenType`. 
Daaronder beschrijven we precies wat een `boekenType` is. In dit geval is het een **complexType**. Het bestaat namelijk uit meerdere andere elementen. We geven hier mee dat enkel het element `boek` mag voorkomen in ons `boekType`.
Wat valt nog op? Dat is de toevoeging van `maxOccurs="unbounded"`. Hiermee geven we aan dat dit element ongelimiteerd gebruikt mag worden. De tegenhanger van `maxOccurs` is `minOccurs`, door die de waarde 1 mee te geven bijvoorbeeld, maken we een element verplicht. De combinatie kan ook gebruikt worden. Stel dat we een ISBN nummer zouden toevoegen, dan zouden we dat als volgt kunnen doen:

```xml
      <xs:element name="isbn" type="xs:string" minOccurs="1" maxOccurs="1"/>
```

Hierbij geven we dus aan dat het ISBN nummer een verplicht veld is, en ik maar maximaal 1 keer kan voorkomen.

Het `boekType` ten slotte heeft ook zijn eigen definitie. Dit type bevat een `sequence`, dat betekent dat de elementen die gedefinieerd zijn onder het `boekType` moeten voorkomen in de volgorde dat ze zijn gedefinieerd. 
De elementen zelf zijn hier primitieve elementen. Daarom krijgen ze reeds bestaande types, zoals string en integer.

Je kan je XML bestanden laten valideren tegen een XSD schema, door daar zelf logica voor te schrijven. In typische Enterprise applicaties, waarbij XML gebruikt wordt als communicatiemiddel ga je dat steeds willen doen om te verifiëren dat alle data doorgegeven werd op de op voorhand beschreven manier. Of je kan een online validatie tool gebruiken. Hiervoor kan je gedurende deze les deze [XSD Validator](https://www.freeformatter.com/xml-validator-xsd.html) gebruiken

### Oefeningen
1. Maak een XSD schema voor de studenten XML die jullie in de vorige opgave hebben gemaakt.
2. Voeg een nieuw element `Gender` toe aan XML en XSD. 

We kunnen ons XSD schema uitbreiden door er ook attributen aan toe te voegen. Laten we een `Genre` attribuut definiëren op ons `boek` element.

```xml
  <xs:complexType name="boekType">
    <xs:sequence>
      <xs:element name="titel" type="xs:string"/>
      <xs:element name="auteur" type="xs:string"/>
      <xs:element name="jaar" type="xs:integer"/>
    </xs:sequence>
    <xs:attribute name="genre" type="xs:string" />
  </xs:complexType>
```

In XML vorm zou dat er dan als volgt uitzien:

```xml
<boek genre="fantasy">
    <titel>The Lost Metal</titel>
    <auteur>Brandon Sanderson</auteur>
    <jaar>2022</jaar>
</boek>
```

## Validaties

We kunnen aan XSD ook nog validatieregels toevoegen die de data-integriteit verzekeren. Hieronder volgen een aantal voorbeelden:

### Minimum en maximumwaarde voor getal

```xml
  <xs:element name = "score">
   <xs:simpleType>
      <xs:restriction base = "xs:integer">
         <xs:minInclusive value = "0"/>
         <xs:maxInclusive value = "100"/>
      </xs:restriction>
   </xs:simpleType>
</xs:element>
```

### Enumeratie van waardes

```xml
  <xs:element name = "Score">
   <xs:simpleType>
      <xs:restriction base = "xs:string">
         <xs:enumeration value = "Laag"/>
         <xs:enumeration value = "Gemiddeld"/>
         <xs:enumeration value = "Hoog"/>
      </xs:restriction>
   </xs:simpleType>
</xs:element>
```

### Regex

```xml
  <xs:element name = "Naam">
   <xs:simpleType>
      <xs:restriction base = "xs:string">
         <xs:pattern value = "[a-z]"/>
      </xs:restriction>
   </xs:simpleType>
</xs:element>
```

### Oefeningen
1. Voeg een attribuut toe aan de studenten XML dat aangeeft of de student een Bachelor of Master volgt.
2. Voeg voor elke student nu ook een lijst van vakken toe. Per vak willen we zien voor hoeveel studiepunten dit meetelt en een score? Voeg dit ook toe in je XML en valideer dit tegen je XSD.