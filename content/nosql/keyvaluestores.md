---
title: "1. Key/value stores"
weight: 1
---

## 1.1 Persistente Hashmaps

De eenvoudigst mogelijke noSQL database die gebruik maakt van key/values is een simpele `HashMap<K,V>` die je zelf serialiseert naar een flat file op de HDD. Een netwerk share kan dit bestand delen, maar locking systemen zullen moeten ingebouwd worden om te voorkomen dat dit bestand corrupt wordt. 

De "oude" manier om dit te doen in java is gebruik te maken van `FileOutputStream`:

```java
public static void main(String[] args) throws IOException {
    var db = new HashMap<String, Object>();
    db.put("joske", new Student("Joske", 11));

    var file = new File("database.db");
    var f = new FileOutputStream(file);
    var s = new ObjectOutputStream(f);
    s.writeObject(db);
    s.close();
}
```

Inlezen werkt op dezelfde manier, met `FileInputStream` en `ObjectInputStream`. Hoe je `Student` klasse wordt geserialiseerd kan je **zelf kiezen** als je de interface `Serializable` implementeert! 

Met bovenstaande interface kan je de student terug uitlezen:

```java
Map<String, Object> map = (Map<String, Object>) s.readObject();
Student joske = (Student) map.get("joske");
System.out.println(joske.getName());
```

### 1.1.1 Oefeningen

1. Werk bovenstaand voorbeeld uit en persisteer een aantal studenten met de volgende klasse:

```java
public class Student {
      private final String name;
      private final int age;
      public Student(String name, int age) {
            this.name = name;
            this.age = age;
      }
}
```


## 1.2 Distributed Hashmaps: Memcached

Met de Java voorbeeldcode op pagina 307 kunnen we een verbinding maken met een (of meerdere) Memcached servers. De Memcached client van `net.spy.spymemcached` (zie mvn repo: [https://mvnrepository.com/artifact/net.spy/spymemcached](https://mvnrepository.com/artifact/net.spy/spymemcached)). 

De client code vereist een werkende memcached server - [https://www.memcached.org](https://www.memcached.org). Je kan dit zelf compileren onder UNIX of Msys in Windows. We gaan voor de oefeningen hier niet verder op in. 

### Denkvragen

1. Welke beperkingen zijn er verbonden aan het geserialiseerd database bestand doorgeven aan andere medestudenten? Op welke manier kan je zo verschillende 'clients' verbinden aan één database 'server'?