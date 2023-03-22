### Charakterystyka języka 

#### Proste typy danych
`int, float, bool, string`

#### Złożone typy danych
`List` -> `append(elem), remove(elem), removeAt(index), at(elem), pop(), insertAt(elem, index)`

#### Operacje
###### arytmetyczne
`+, -, *, /, <, >, <=, >=, ==, !=, +=, -=`
###### logiczne
`and, or, not`

#### Słowa kluczowe i pozostałe symbole
###### pętle
`for i in range(start, stop, step) {}` 
`for elem in List {}`
W rzeczywistości to samo, bo `range` może zwrócić odpowiednią listę

###### instrukcja warunkowa
`if () {}`

###### instrukcje
`{ciało funkcji/pętli/instrukcji warunkowej} w nawiasach klamrowych`

`(parametry funkcji w nawiasach okrągłych)`

`instrukcja kończy się średnikiem;`

Albo jako dowcip
```
dla i w zakres(a, b) {
    jeżeli (i > 2 lub i < -2)
    napisz("witaj świecie");
}
```
#### Zmienne
Typowanie statyczne -> `int i = 2`

Typowanie silne -> `2 == "2"` zwraca błąd // co jest trudniejsze? w sumie nie wiem z czym to się wiąże

Zmienne mutowalne // bo w sumie czemu to blokować? Ewentualnie słowo kluczowe `final`

#### Definiowanie funkcji
`int myFunction(int parameter) {}`

###### Przekazywanie argumentów
typy proste -> przez wartość

typy złożone -> przez referencję

#### Definiowanie własnych typów
Definiowanie klas jest trudne, więc w drodze kompromisu, coś na kształt javowego recordu:

klasa która posiada tylko atrybuty:
```
record Person {
    string name;
    int age;
    bool isMarried;
}
```
dostęp do atrybutów bezpośrednio:
```
int a = myPerson.name
```
albo przez gettery i settery    // opcja trudniejsza?
```
myPerson.setIsMarried(true);
int a = myPerson.getAge();
```
#### Obsługa wyjątków 
Tu trochę nie rozumiem jak to ma być 

Napisanie wyjątków dziedziczących po Exception nie jest dobrym rozwiązaniem?