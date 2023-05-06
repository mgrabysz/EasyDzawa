# Projekt wstępny
Nazwa projektu: `Żmija` \
Autor: Marcin Grabysz \
Opiekun projektu: Piotr Gawkowski 

Celem projektu jest napisanie interpretera języka o składni podobnej do języka Python,
z możliwością wykonywania podstawowych operacji na zmiennych typu 
int, float i bool, a także definiowania własnych funkcji i klas. Język Żmija<sup>TM</sup> umożliwi używanie słów kluczowych i nazw funkcji 
w języku polskim, jak w poniższym przykładzie:
```
numery = Lista();
numery.dodaj(1);
numery.dodaj(2);
dla numer w numery {
  napisz("Witaj świecie! Twój numer to: ", numer);
} 
```

## Charakterystyka języka 

#### Proste typy danych
`int, float, bool`

#### Złożone typy danych
`Lista` -> `dodaj(elem), usuń(elem), usuńNa(indeks), dodajNa(elem, indeks), pobierzNa(indeks)`

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
`jeżeli () {} inaczej {}`

###### obiekty
`klasa` - poprzedza definicję klasy \
`tenże` - przedrostek oznaczający atrybuty klasy \
Patrz: [przykład definiowania klasy](#definiowanie-klasy-i-funkcji)

#### Instrukcje
`{ciało funkcji/pętli/instrukcji warunkowej} w nawiasach klamrowych`

`(parametry funkcji w nawiasach okrągłych)`

`instrukcja kończy się średnikiem;`

#### Zmienne
Typowanie dynamiczne \
Typowanie słabe \
Zmienne mutowalne

#### Definiowanie funkcji
`mojaFunkcja(parametr1, parametr2) {}`

Przekazywanie parametrów do funkcji odbywa się przez wartość zarówno dla typów
prostych jak i złożonych.

#### Funkcje wbudowane
`napisz("Witaj świecie", ...)` - wypisuje tekst lub wartość zmiennej w konsoli, przyjmuje dowolną liczbę argumentów \
`zakres(start, stop, krok)` - zwraca listę zawierającą liczby całkowite od `start` do `stop` o podanym kroku \
`kończWaść()` - przerywa wykonanie programu

#### Definiowanie własnych typów
Użytkownik może definiować własne typy bez dziedziczenia. Klasy mogą agregować 
inne klasy

#### Komentarze
`// tekst po podwójnym ukośniku jest interpretowany jako komentarz`

### Przykłady konstrukcji językowych
#### Operacje matematyczne i logiczne
```
x = 10 + 20 - 5 * (3+2);
x += 5;
y = 10.5 / 2;
z = x * y;
var1 = z >= x;
var2 = var1 lub (x == 10);
var3 = ((y > 5) oraz var2) lub z < 0;
```
#### Operacje na obiekcie Lista
```
numery = Lista();
numery.dodaj(10);
numery.dodaj(20);
numery.dodajNa(0, 0);
i = numery.pobierzNa(2);
numery.usuń(10);
numery.usuńNa(0);
numery.dodaj(1.2);

lista = Lista();
lista.dodaj(prawda);
lista.dodaj(4 > 3);
lista.dodaj(4.20 * 10);
napisz(lista.pobierzNa(2));
```
#### Pętle i instrukcje warunkowe
```
lista = Lista();
dla i w zakres(0, 10, 1) {
  lista.add(i * 10);
}
dla i w lista {   // i nie jest widoczne poza pętlą
  jeżeli (i > 50) {
      napisz(i, " jest większe od 50 \n");
  } inaczej {
      napisz(i, " nie jest większe od 50 \n");
  }
}
```
#### Definiowanie klasy i funkcji
```
klasa Ułamek {
    
  Ułamek(l, m) {            // konstruktor
    tenże.licznik = l;
    tenże.mianownik = m;
    jeżeli (m == 0) {
        kończWaść();
    }
    jeżeli (l < m) {
        tenże.jestWłaściwy = prawda;    // ułamek właściwy to taki, który jest mniejszy od 1
    } inaczej {
        tenże.jestWłaściwy = fałsz;
    }
  }

  rozszerz(i) {
    tenże.licznik = tenże.licznik * i;
    tenże.mianownik = tenże.mianownik * i;
  }
}


main() {
  x = Ułamek(1, 2);
  jeżeli (x.jestWłaściwy) {
    napisz("Zdefiniowano właśnie ułamek właściwy");
  }
  x.rozszerz(2);
  napisz(y.licznik);      // 2
  napisz(y.mianownik);    // 4
}
```
#### Słowo kluczowe zwróć
```
silnia(x) {
  jeżeli (a < 0) {
    kończWaść();          // przerywa wykonanie programu
  }
  jeżeli (a == 0) {
    zwróć 1;          // dalsze instrukcje nie wykonują się
  }
  wynik = silnia(x-1) * x;
  zwróć wynik;
} 
```

### Niepoprawne konstrukcje i komunikaty wyjątków
#### na poziomie leksykalnym
```
var = 123abc;
var2 = @$abc;
Undefined expression: 123abc at line x position y
```
```
var = 2147483648;
Numeric expression: 214748364... at line 0 position 0 exceeds limit
```
```
"abcdefgh
End of file reached while parsing text: abcdefgh starting at line x position y
```
Oraz (maksymalna długość jest konfigurowana dla identyfikatorów, komentarzy i tekstów z osobna):
```
Identifier: aaaaaaaaaaaaaaaaaaaaaaaa... starting at line x position y exceeds maximal length
Text: aaaaaaaaaaaaaaaaaaaaaaaa... starting at line x position y exceeds maximal length
Comment: aaaaaaaaaaaaaaaaaaaaaaaa starting at line x position y exceeds maximal length
```
#### na poziomie składniowym
```
jeżeli (a > b {
  a = b;
}
While parsing statement starting at line x position y {{ jeżeli (a > b ... }} closing parenthesis not found
```
```
var = 2 + 2 8
Unexpected token starting at line x position y
```
#### na poziomie semantycznym
```
dodaj(a, b) {
  return a+b;
}
dodaj(2, true);
Unsupported operation in {{ dodaj(2, true) }} starting at line x position y
```
```
a = 2;
b = 3;
wynik = a + b + c;
Variable <name> at line x position y not defined
```
```
a = 2;
b = 0;
wynik = a / b;
Division by 0 in {{ wynik = a / b }} at line x postion y
```

## Gramatyka

```
program                 = {definition};

definition              = function-definition | class-definition

function-definition     = identifier, "(", [parameters-list], ")", block;

class-definition        = class-keyword, identifier, class-body;

class-body              = "{", {function-definition}, "}";

block                   = "{", {statement}, "}";

statement               = object-access, [assignment], ";"
                        | if-statement
                        | for-statement
                        | return-statement
                      
object-access           = (title | this-keyword), {".", title};
                        
title                   = identifier, ["(", arguments-list, ")"];

assignment              = ("=" | "+=" | "-="), expression;

if-statement            = if-keyword, "(", expression, ")", block, [else-keyword, block];

for-statement           = for-keyword, identifier, in-keyword, object-access, block;

return-statement        = return-keyword, [expression], ";";

expression              = or-expression;

or-expression           = and-expression, {or-keyword, and-expression};

and-expression          = relative-expression, {and-keyword, relative-expression};

relative-expression     = arithmetic-expression, [relative-operator, arithmetic-expression];

arithmetic-expression   = multiplicative-expression, {("+" | "-"), multiplicative-expression};

multiplicative-expr.    = factor, {("*" | "/"), factor};

factor                  = [negation], (literal | object-access | "(", expression, ")");

parameters-list         = identifier, {",", identifier};

arguments-list          = expression, {",", expression};

identifier              = letter, {letter | digit};

literal                 = integer
                        | float
                        | bool
                        | text;

integer                 = "0" | (non-zero-digit, {digit});

float                   = integer, ".", digit, {digit};

bool                    = true-keyword | false-keyword;

text                    = '"', {char}, '"';

char                    = ({letter} | {digit} | {special-symbol}), {char},

negation                = not-keyword | "-";

relative-operator       = "==" | "!=" | "<" | ">" | ">=" | "<=";

letter                  = "A" | "B" | "C" | ... | "Z" | "a" | "b" | "c" | ... | "z" | "_";

non-zero-digit          = "1" | "2" | "3" | ... | "9";

digit                   = "0" | non-zero-digit;

special-symbol          = " " | "\" | "!" | "@" | ...

class-keyword           = "klasa";

if-keyword              = "jeżeli";

else-keyword            = "inaczej";

for-keyword             = "dla";

in-keyword              = "w";

return-keyword          = "zwróć";

this-keyword            = "tenże";

or-keyword              = "lub";

and-keyword             = "oraz";

not-keyword             = "nie";

true-keyword            = "prawda";

false-keyword           = "fałsz";
```

## Sposób uruchamiania
Do uruchomienia interpretera służy skryp w `bash` przyjmujący jako argument plik `.txt` zawierający treść programu 
do zinterpretowania. Wyjściem programu jest wyjście standardowe

## Analiza wymagań
* Interpreter ogranicza wielkość pliku wejściowego oraz długość konkretnego tokenu, ma to na celu uniemożliwienie 
  przepełnienia bufora
* Interpreter umożliwia przerwanie wykonywania programu skrótem klawiszowym
* W celu dostarczenia precyzyjnej informacji o błędzie w programie, obiekty typu `Token` przechowują swoją pozycję 
  w pliku (numer linii, pozycja znaku w linii). Więcej w punkcie [Analizator leksykalny](#analizator-leksykalny)
* Aby informacja o pozycji błędu programu mogła być przekazana przez analizator semantyczny, pozycja tokenu jest 
  przekazywana do odpowiedniego węzła drzewa rozbioru składniowego
* Potencjalne problemy interpretacji to zapewnienie dostępu do zmiennych globalnych/lokalnych w zależności od kontekstu
  (zmienne lokalne "przykrywają" zmienne globalne) oraz zapewnienie unikalności nazw zmiennych i funkcji w tym samym
  kontekście. Próba odpowiedzi na te problemy znajduje się w sekcji [Realizacja analizatora semantycznego](#analizator-semantyczny).

## Realizacja
#### Analizator leksykalny
Analizator leksykalny (lekser) jest modułem odpowiedzialnym za przetworzenie pliku wejściowego na ciąg tokenów. 
Lekser oczekuje dwóch argumentów:
* `eventHandler` - wyrażenie lub funkcja określające, co należy wykonać przy napotkaniu błędu
* `bufferedReader` - obiekt typu BufferedReader stanowiący źródło znaków do interpretacji.

Lekser pobiera znaki _leniwie_, tj. w momencie w którym potrzebuje kolejnego znaku. Każdy znak po pobraniu jest poddany 
analizie, po której zakończeniu zostaje pobrany kolejny znak. Znak lub grupa znaków może zostać zinterpretowana jako Token.
Natrafienie na ciąg niemożliwy do zinterpretowania ciąg znaków lub token przekraczający dopuszczalną wielkość powoduje 
rzucenie wyjątku (patrz: [Niepoprawne konstrukcje i komunikaty wyjątków na poziomie leksykalnym](#na-poziomie-leksykalnym)).

Interfejs klasy Token
```java
public interface Token {
	TokenType getType();
	Position getPosition();
	<V> V getValue();
}
```
Token posiada swój typ, pozycję w pliku wejściowym oraz (dla pewnych typów) wartość.

Rozpoznawane typy tokenów to: 
* END_OF_FILE 
* COMMENT
* słowa kluczowe: RETURN, FOR, IF, ELSE, CLASS, THIS
* symbole: OPEN_BRACKET, CLOSE_BRACKET, OPEN_PARENTHESIS, CLOSE_PARENTHESIS, SEMICOLON, COMA, DOT, ASSIGN, ADD_AND_ASSIGN, SUBTRACT_AND_ASSIGN
* operatory porównania: EQUAL, NOT_EQUAL, GREATER, LESS, GREATER_OR_EQUAL, LESS_OR_EQUAL
* operatory logiczne: AND, OR, NOT
* operatory matematyczne: ADD, SUBTRACT, MULTIPLY, DIVIDE 
* wartości liczbowe i logiczne: INTEGER, FLOAT, BOOL, TEXT
* IDENTIFIER

Token typu `COMMENT` nie jest dołączany do listy wygenerowanych tokenów.

#### Analizator składniowy
Analizator składniowy (parser) jest modułem odpowiedzialnym za przetworzenie strumienia tokenów na drzewo rozbioru 
składniowego. Analiza odbywa się w sposób rekursywnie zstępujący. Drzewo jest zaimplementowane jako zbiór obiektów 
klasy `Node` (węzeł). Każdy z węzłów posiada "dzieci" (children) (uszeregowane) oraz "rodzica" (parent). 

Przykład drzewa dla wyrażenia arytmetycznego:

`a = (60 + 4) / 9 + 4`

![tree.png](pictures/tree.png)

Niemożliwość utworzenia poprawnego drzewa skutkuje podniesieniem wyjątku 
(patrz: [Niepoprawne konstrukcje i komunikaty wyjątków na poziomie składniowym](#na-poziomie-składniowym))

#### Analizator semantyczny
Analizator semantyczny jest modułem odpowiedzialnym za analizę drzewa rozbioru i wykonania zawartych w nim instrukcji.
Węzły drzewa przetwarzane są zgodnie ze wzorcem projektowym "Visitor", zgodnie z którym rozwój funkcjonalności
analizatora semantycznego odbywa się bez modyfikowania kodu drzewa. Węzły jedyne akceptują wywołanie na nich metod "visitora".

Za unikatowość nazw zmiennych i funkcji w jednym kontekście odpowiada utworzona dla tego kontekstu hashmapa, mapująca nazwę
zmiennej na jej wartość lub nazwę funkcji na jej ciało.

Za przykrycie zmiennych globalnych zmiennymi lokalnymi funkcji lub obiektu odpowiada stos, na którym są 
umieszczane hashmapy zawierające funkcje i zmienne danego kontekstu. Nie znalezienie szukanej zmiennej w danym kontekście
wywoła próbę szukania go w wyższym kontekście.

## Sposób testowania
Do przetestowania analizatora leksykalnego służą testy jednostkowę sprawdzające, czy na podstawie danego pliku 
wejściowego lekser zwróci oczekiwaną listę tokenów lub podniesie oczekiwany wyjątek.

Do przetestowania analizatora składniowego służą testy jednostkowe sprawdzające, czy na podstawie danego strumienia 
tokenów parser utworzy oczekiwane drzewo rozbioru składniowego lub podniesie oczekiwany wyjątek. Testy jednostkowe 
obsługują również integrację leksera z parserem (testowanie budowania drzewa na podstawie pliku wejściowego).

Analizator składniowy jest najtrudniejszy do przetestowania automatycznie, jednak testy jednostkowe mogą wykazać 
podniesienie odpowiednich wyjątków na podstawie danego wejścia (strumienia znaków, tokenów, lub drzewa rozbioru, 
w zależności od testowanego poziomu integracji).
