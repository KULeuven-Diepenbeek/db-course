---
title: 2. Key-value stores
draft: false
---

## 1.1 Persistente Hashmaps

De eenvoudigst mogelijke noSQL database die gebruik maakt van key/values is een simpele `HashMap<K,V>` die je zelf serialiseert naar een flat file op de HDD. Een netwerk share kan dit bestand delen, maar locking systemen zullen moeten ingebouwd worden om te voorkomen dat dit bestand corrupt wordt. 

De "oude" manier om dit te doen op de JVM is gebruik te maken van `FileOutputStream`:

<div class="devselect">

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
</div>

Inlezen werkt op dezelfde manier, met `FileInputStream` en `ObjectInputStream`. Hoe je `Student` klasse wordt geserialiseerd kan je **zelf kiezen**, maar een vereiste is dat je de interface `Serializable` implementeert! 

Met bovenstaande interface kan je de student terug uitlezen:

<div class="devselect">

```java
var s = new ObjectInputStream(new FileInputStream("database.db"));
Map<String, Object> map = (Map<String, Object>) s.readObject();
s.close();
Student joske = (Student) map.get("joske");
System.out.println(joske.getName());
```

</div>

### 1.1.1 Oefeningen

1. Werk bovenstaand voorbeeld uit en persisteer een aantal studenten met de volgende klasse:

<div class="devselect">

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
</div>

## 1.2 Distributed Hashmaps: Memcached

We kunnen eenvoudig een verbinding maken met een (of meerdere) Memcached server via de Memcached Java client van `net.spy.spymemcached` (zie mvn repo: [https://mvnrepository.com/artifact/net.spy/spymemcached](https://mvnrepository.com/artifact/net.spy/spymemcached)). Dit hoeft maar één regel code te zijn: (zie [memcached javadocs](http://dustin.github.io/java-memcached-client/apidocs/net/spy/memcached/MemcachedClient.html))

```java
var client = new MemcachedClient(new InetSocketAddress("127.0.0.1", 11211));
// bewaar een student onder key "joskey" voor één uur (3600s)
client.set("joskey", 3600, new Student("Jos", 20));
// retrieve object
var restoredStudent = (Student) client.get("Jos");
```

De client code vereist een werkende memcached server zoals [https://www.memcached.org](https://www.memcached.org), in bovenstaand voorbeeld draaiend op poort `11211`. Je kan dit zelf compileren onder UNIX of Msys in Windows. We gaan voor de oefeningen hier niet verder op in. 

### Denkvragen

1. Welke beperkingen zijn er verbonden aan het geserialiseerd database bestand doorgeven aan andere medestudenten? Op welke manier kan je zo verschillende 'clients' verbinden aan één database 'server'?