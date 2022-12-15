---
title: 3. XPath
---

# XPath

XPath is een taal die we gebruiken om specifieke elementen of attributen te vinden in een XML bestand. Zoals je in de voorbije oefeningen al hebt gemerkt zijn XML bestanden nogal groot en niet zo makkelijk in een oogopslag om alle informatie uit te halen.

Laten we ons voorbeeldbestand nemen:

```xml
<boeken>
    <boek genre="Non-Fiction">
        <titel>Mythos</titel>
        <auteur>Stephen Fry</auteur>
        <jaar>2017</jaar>
    </boek>
    <boek genre="biography">
        <titel>Scar Tissue</titel>
        <auteur>Anthony Kiedis</auteur>
        <jaar>2004</jaar>
    </boek>
    <boek genre="fantasy">
        <titel>The Lost Metal</titel>
        <auteur>Brandon Sanderson</auteur>
        <jaar>2022</jaar>
    </boek>
</boeken>
```

## XPath voorbeelden

`/`

Dit selecteert het meest top level object. In de praktijk geeft dit dus het hele document terug.

`/boeken`

Dit zou het boeken element teruggeven. Wat dus ook het hele bestand is.

`/boeken/boek/titel`

Geef alle titels die vallen in de hiërarchie boeken -> boek.

`//titel`

Geef alle titels, eender waar in het document.

`//boek/@genre`

Geef alle genre attributen die gekoppeld zijn aan een boek element.

`//boek[@genre='fantasy']`

Geef alle boek elementen terug die in het genre fantasy vallen.

`//boek/*`

Geef alle subelementen van het boek element.

## Oefeningen

Hieronder vind je een xml bestand waarop we de XPath oefeningen gaan uitvoeren. Je kan je XPath valideren op deze [online XPath evaluator](https://www.freeformatter.com/xpath-tester.html).

```xml
<?xml version="1.0"?>
<catalog>
   <book id="bk101">
      <author>Gambardella, Matthew</author>
      <title>XML Developer's Guide</title>
      <genre fiction="0">
        <maingenre>Computer</maingenre>
        <subgenre>XML</subgenre>
      </genre>
      <price>44.95</price>
      <publish_date>2000-10-01</publish_date>
      <description>An in-depth look at creating applications 
      with XML.</description>
   </book>
   <book id="bk102">
      <author>Ralls, Kim</author>
      <title>Midnight Rain</title>
      <genre fiction="1">
        <maingenre>Fantasy</maingenre>
        <subgenre>Low Fantasy</subgenre>
      </genre>
      <price>5.95</price>
      <publish_date>2000-12-16</publish_date>
      <description>A former architect battles corporate zombies, 
      an evil sorceress, and her own childhood to become queen 
      of the world.</description>
   </book>
   <book id="bk103">
      <author>Corets, Eva</author>
      <title>Maeve Ascendant</title>
      <genre fiction="1">
        <maingenre>Fantasy</maingenre>
        <subgenre>Dystopian Fantasy</subgenre>
      </genre>
      <price>5.95</price>
      <publish_date>2000-11-17</publish_date>
      <description>After the collapse of a nanotechnology 
      society in England, the young survivors lay the 
      foundation for a new society.</description>
   </book>
   <book id="bk104">
      <author>Corets, Eva</author>
      <title>Oberon's Legacy</title>
      <genre fiction="1">
        <maingenre>Fantasy</maingenre>
        <subgenre>Dystopian Fantasy</subgenre>
      </genre>
      <price>5.95</price>
      <publish_date>2001-03-10</publish_date>
      <description>In post-apocalypse England, the mysterious 
      agent known only as Oberon helps to create a new life 
      for the inhabitants of London. Sequel to Maeve 
      Ascendant.</description>
   </book>
   <book id="bk105">
      <author>Corets, Eva</author>
      <title>The Sundered Grail</title>
      <genre fiction="1">
        <maingenre>Fantasy</maingenre>
        <subgenre>Dystopian Fantasy</subgenre>
      </genre>
      <price>5.95</price>
      <publish_date>2001-09-10</publish_date>
      <description>The two daughters of Maeve, half-sisters, 
      battle one another for control of England. Sequel to 
      Oberon's Legacy.</description>
   </book>
   <book id="bk106">
      <author>Randall, Cynthia</author>
      <title>Lover Birds</title>
      <genre fiction="1">
        <maingenre>Romance</maingenre>
      </genre>
      <price>4.95</price>
      <publish_date>2000-09-02</publish_date>
      <description>When Carla meets Paul at an ornithology 
      conference, tempers fly as feathers get ruffled.</description>
   </book>
   <book id="bk107">
      <author>Thurman, Paula</author>
      <title>Splish Splash</title>
      <genre fiction="1">
        <maingenre>Romance</maingenre>
      </genre>
      <price>4.95</price>
      <publish_date>2000-11-02</publish_date>
      <description>A deep sea diver finds true love twenty 
      thousand leagues beneath the sea.</description>
   </book>
   <book id="bk108">
      <author>Knorr, Stefan</author>
      <title>Creepy Crawlies</title>
      <genre fiction="1">
        <maingenre>Horror</maingenre>
        <subgenre>Short Stories</subgenre>
      </genre>
      <price>4.95</price>
      <publish_date>2000-12-06</publish_date>
      <description>An anthology of horror stories about roaches,
      centipedes, scorpions  and other insects.</description>
   </book>
   <book id="bk109">
      <author>Kress, Peter</author>
      <title>Paradox Lost</title>
      <genre fiction="1">
        <maingenre>Science Fiction</maingenre>
      </genre>
      <price>6.95</price>
      <publish_date>2000-11-02</publish_date>
      <description>After an inadvertant trip through a Heisenberg
      Uncertainty Device, James Salway discovers the problems 
      of being quantum.</description>
   </book>
   <book id="bk110">
      <author>O'Brien, Tim</author>
      <title>Microsoft .NET: The Programming Bible</title>
      <genre fiction="0">
        <maingenre>Computer</maingenre>
        <subgenre>.NET</subgenre>
      </genre>
      <price>36.95</price>
      <publish_date>2000-12-09</publish_date>
      <description>Microsoft's .NET initiative is explored in 
      detail in this deep programmer's reference.</description>
   </book>
   <book id="bk111">
      <author>O'Brien, Tim</author>
      <title>MSXML3: A Comprehensive Guide</title>
      <genre fiction="0">
        <maingenre>Computer</maingenre>
        <subgenre>XML</subgenre>
      </genre>
      <price>36.95</price>
      <publish_date>2000-12-01</publish_date>
      <description>The Microsoft MSXML3 parser is covered in 
      detail, with attention to XML DOM interfaces, XSLT processing, 
      SAX and more.</description>
   </book>
   <book id="bk112">
      <author>Galos, Mike</author>
      <title>Visual Studio 7: A Comprehensive Guide</title>
      <genre fiction="0">
        <maingenre>Computer</maingenre>
        <subgenre>Visual Studio</subgenre>
      </genre>
      <price>49.95</price>
      <publish_date>2001-04-16</publish_date>
      <description>Microsoft Visual Studio 7 is explored in depth,
      looking at how Visual Basic, Visual C++, C#, and ASP+ are 
      integrated into a comprehensive development 
      environment.</description>
   </book>
</catalog>
```

1. Geef alle prijzen weer. Schrijf hiervoor 2 verschillende XPath queries die hetzelfde resultaat geven.
2. Geef de titel van het boek met id bk110.
3. Geef de description van alle boeken.
4. Geef alle non-fictie boeken
5. Lijst alle subgenres van de fictie boeken op.