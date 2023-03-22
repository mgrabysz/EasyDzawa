# Projekt wstępny
Nazwa projektu: `EasyDżawa` \
Autor: Marcin Grabysz \
Opiekun projektu: Piotr Gawkowski 

Celem projektu jest napisanie interpretera języka o składni podobnej do C++ 
lub Java, z możliwością wykonywania podstawowych operacji na zmiennych typu 
int, float i bool, a także definiowania własnych funkcji i klas. Język  
EasyDżawa<sup>TM</sup> umożliwi używanie słów kluczowych i nazw funkcji 
w języku polskim, jak w poniższym przykładzie:
```
numery = nowy Lista;
numery.dodaj(1);
numery.dodaj(2);
dla numer w numery {
    napisz(numer);
    napisz("Witaj świecie");
} 
```

### Charakterystyka języka 

#### Proste typy danych
`int, float, bool`

#### Złożone typy danych
`Lista` -> `dodaj(elem), usuń(elem), usuńNa(indeks), na(elem), włóżNa(elem, indeks)`

#### Operacje
###### arytmetyczne
`+, -, *, /, <, >, <=, >=, ==, !=, +=, -=`
###### logiczne
`oraz, lub, nie`

#### Słowa kluczowe i pozostałe symbole
###### pętle
`dla i w zakres(start, stop, krok) {}` 
`dla elem w Lista {}`

###### instrukcja warunkowa
`jeżeli () {}`

###### instrukcje
`{ciało funkcji/pętli/instrukcji warunkowej} w nawiasach klamrowych`

`(parametry funkcji w nawiasach okrągłych)`

`instrukcja kończy się średnikiem;`

#### Zmienne
Typowanie dynamiczne \
Typowanie słabe \
Zmienne niemutowalne

#### Definiowanie funkcji
`int mojaFunkcja(int parametr) {}`

Przekazywanie parametrów do funkcji odbywa się przez wartość zarówno dla typów
prostych jak i złożonych.

#### Funkcja wbudowana
`napisz("Witaj świecie");` - wypisuje tekst lub wartość zmiennej w konsoli
#### Definiowanie własnych typów

Użytkownik może definiować własne typy bez dziedziczenia. Klasy mogą agregować 
inne klasy
```
klasa Ułamek {
    int licznik;
    int mianownik;

    Ułamek rozszerz(int i) {
        nowyLicznik = licznik * i;
        nowyMianownik = mianownik * i;
        nowyUłamek = nowy Ułamek(nowyLicznik, nowyMianownik);
        zwróć nowyUłamek;
    }
}
```
#### Komentarze
`// tekst po podwójnym ukośniku jest interpretowany jako komentarz`
#### Obsługa wyjątków 
Lambdę należy podać lekserowi