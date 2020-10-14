Software ontwerp in C/C++: #1
=============================

<img src="/cpp-course/img/kul.svg" style="height: 80px;" />
<img src="/cpp-course/img/uhasselt.svg" style="width: 165px; height: 80px;"/>

([video link](https://www.loom.com/share/c81929c12682431aa40e1df6c999d47e))

---

## Het vak

- <i class='fa fa-pencil'></i> 4 studiepunten, 4 colleges, 11 labo's
- <i class='fa fa-graduation-cap'></i> wouter.groeneveld@kuleuven.be
- <i class="fa fa-link" aria-hidden="true"></i> [https://kuleuven-diepenbeek.github.io/cpp-course/](https://kuleuven-diepenbeek.github.io/cpp-course/)

___

## Het vak: Agenda

* Introductie in C
* Pointers
* Introductie in C++
* Wat is "Software Ontwerpen"
* Toepassing: GBA Game

___

## Het vak: Praktisch

* Hoorcolleges 4x, opgenomen (online)
* Labo's on-campus H-E214, 3u + pauze
* **Eigen laptop** meebrengen!
* **Voorbereiding** per blok: zie cursus online 

---

## Waarom C(++) leren?

#### Daarom: embedded systems

<img src="/cpp-course/slides/cpp/img/embedded.png"/>

___

#### Daarom: embedded systems

<img src="/cpp-course/img/teaching/cpp/gba.jpg"/>

___

#### Daarom: als performantie een prioriteit is

<img src="/cpp-course/slides/cpp/img/unrealengine.jpg"/>

___

#### Daarom: TIOBE Index

<img src="/cpp-course/slides/cpp/img/index.png"/>

___

#### Daarom: 

- Historiek
  - Lang in gebruik t.o.v. andere moderne talen
  - Andere moderne talen zijn geschreven in... C! 
- Eenvoud
  - C heeft weinig om het lijf ([Ref. card](https://kuleuven-diepenbeek.github.io/cpp-course/c-refcard.pdf))
  - Te leren in 1 dag
- Kracht
  - Rechtstreeks hardware aanspreken
  - Pointers

---

## Een voorbeeld in Java:

```Java
import java.io.IOException;
import java.nio.*;

class FileReader {

    @Override
    public String read(String file) throws IOException {
        return new String(Files.readAllBytes(Paths.get(file)));
    }
}

class Main {
    public static void main(String[] args) {
        System.out.println("reading file: ");
        System.out.println(new FileReader().read("sup.txt"));
    }
}
```

___

## C equivalent?

```C
#include <stdio.h>
#include <stdlib.h>

char* read_file(FILE* ptr) {
  char* buff = malloc(255);
  fgets(buff, 255, ptr);
  fclose(ptr);
  return buff;
}
int main() {
  printf("%s\n", read_file((FILE*) fopen("bla.txt", "r")));
  return 0;
}
```

- Gebruik van `*`
- Gebruik van `malloc`, includes, geen klasses, ...

___

## En in C++?

```C
#include <iostream>
#include <string>

class FileReader {
  public:
    std::string read(std::string file);
};
std::string FileReader::read(std::string file) {
  //...
}
int main() {
  FileReader reader;
  std::cout << "reading: " << reader.read("sup.txt") << endl;
  return 0;
}
```

- Mix van Java en C: Object-Oriented met low-lvl access

___

## Hoop syntax leren, hoera!

<img src="/cpp-course/img/teaching/cpp/cppbooks.jpg"/>

---

## Het C(++) ecosysteem

- Hoezo, IDEs? 
- Hoezo, alles gaat automatisch?

___

## Files in een typisch C programma

<pre>
    README.md
    Makefile
    include/
      file.h
      debug.h
    test/
      fileTest.c
    src/
      file.c
      debug.c
    build/
      program.o
      program.bin
</pre>

___

### C compileren

1. stap 1: compile `gcc -c file.c -o file.o`
2. stap 2: link `gcc -o file.o file.bin`

Unix: "**GNU C Compiler**". Alternatieven:

- Unix: `clang`
- Windows: MS' [`cl`](https://msdn.microsoft.com/en-us/library/bb384838.aspx)

GNU op Windows? [MinGW](http://www.mingw.org)

___

### Builden: met Makefiles

Builden: `make [all, install, clean, ...]`

```
.DEFAULT_GOAL := all

CC=gcc

clean:
    rm -rf *.o
    rm -rf *.out

compile:
    $(CC) -c main.c -o main.o

link:
    $(CC) -o main.out main.o

all: clean compile link
```

Flexibiliteit naar compiler keuze!

___

### include: H files

Bevatten **Definities**, geen **Declaraties**.

```C
// file.h

char* read_file(FILE* ptr);
void utility_func(char* string);
```

- Zeg tegen compiler: "ze bestaan, trek uw plan".
- Zeg tegen linker: "Ok, zoek ze nu maar in alle .o files"

___

### source: C files

Bevatten **Declaraties**. 

```C
// file.c
#include "file.h"

char* read_file(FILE* ptr) {
  // ... impl
}
void utility_func(char* string) {
  // impl
}
```

Implementatie kan door andere source file gebruikt worden via **header include**:

```C
#include "file.h"   // vanaf hier "bestaat" read_file en utility_func

char* result = read_file(file_ptr);
```

---

## IDEs? Jazeker

`gdb` beu?

___

### Lichtgewicht: TextEditors

- Sublime Text 3
- Visual Studio Code

Of als jullie écht cool willen zijn: (g)VIM, Emacs

___

### Zwaargewicht: CLion

<img src="/cpp-course/img/teaching/cpp/clion.png"/>

___

### Een ding is zeker...

Leer de Unix shell kennen!

- Mac: [iTerm2](https://www.iterm2.com)
- Windows: [Cmder](http://cmder.net)

---

## Evaluatie

/20

___

### 1/2de Examen

- **Schriftelijk**
- **Open boek**

Tip: kijk naar denkvragen in labo's.

Overdracht vanaf 10/20

___

### 1/2de Project

- **Samenwerken** aangemoedigd (2 pers.), mag alleen
- **C++ Gameboy Advance spel** schrijven!

<img src="/cpp-course/img/teaching/cpp/aria-of-sorrow.gif" style="width: 50%" s/>

Opdracht volgt. Overdracht vnaf 12/20 

___


#### Resultaat 2019-20 (1/2)

<iframe title="vimeo-player" src="https://player.vimeo.com/video/392922578" width="640" height="428" frameborder="0" allowfullscreen></iframe>

___

#### Resultaat 2018-19  (2/2)

<iframe title="vimeo-player" src="https://player.vimeo.com/video/314203871" width="640" height="428" frameborder="0" allowfullscreen></iframe>


---

<!-- .slide: data-background="#008eb3" -->
## Oef... Gemotiveerd?

Dat dacht ik al!
