# Projekt wstępny
Nazwa projektu: `EasyDżawa` \
Autor: Marcin Grabysz \
Opiekun projektu: Piotr Gawkowski 

Celem projektu jest napisanie interpretera języka o składni podobnej do C++ 
lub Java, z możliwością wykonywania podstawowych operacji na zmiennych typu 
int, float i bool, a także definiowania własnych funkcji i klas. Język EasyDżawa<sup>TM</sup> umożliwi używanie słów kluczowych i nazw funkcji 
w języku polskim, jak w poniższym przykładzie:
```
numery = nowy Lista();
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

###### instrukcje
`{ciało funkcji/pętli/instrukcji warunkowej} w nawiasach klamrowych`

`(parametry funkcji w nawiasach okrągłych)`

`instrukcja kończy się średnikiem;`

#### Zmienne
Typowanie dynamiczne \
Typowanie słabe \
Zmienne niemutowalne

#### Definiowanie funkcji
`mojaFunkcja(parametr1, parametr2) {}`

Przekazywanie parametrów do funkcji odbywa się przez wartość zarówno dla typów
prostych jak i złożonych.

#### Funkcje wbudowane
`napisz("Witaj świecie")` - wypisuje tekst lub wartość zmiennej w konsoli \
`zakres(start, stop, krok)` - zwraca listę zawierającą liczby całkowite od `start` do `stop` o podanym kroku
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
numery = nowy Lista();
numery.dodaj(10);
numery.dodaj(20);
numery.dodajNa(0, 0);
i = numery.pobierzNa(2);
numery.usuń(10);
numery.usuńNa(0);
numery.dodaj(1.2);

lista = nowy Lista();
lista.dodaj(prawda);
lista.dodaj(4 > 3);
lista.dodaj(4.20 * 10);
napisz(lista.pobierzNa(2));
```
#### Pętle i instrukcje warunkowe
```
lista = nowy Lista();
dla i w zakres(0, 10, 1) {
    lista.add(i * 10);
}
dla i w lista {   // i nie jest widoczne poza pętlą
    jeżeli (i > 50) {
        napisz(i);
        napisz("jest większe od 50 \n");
    } inaczej {
        napisz(i);
        napisz("nie jest większe od 50 \n");
    }
}
```
#### Definiowanie klasy i funkcji
```
klasa Ułamek {
    licznik;
    mianownik;
    jestWłaściwy;       // ułamek właściwy jest mnniejszy od 1
    
    Ułamek(l, m) {      // konstruktor
        licznik = l;
        mianownik = m;
        jeżeli (l < m) {
            jestWłaściwy = prawda;
        } inaczej {
            jestWłaściwy = fałsz;
        }
    }

    rozszerz(i) {
        nowyLicznik = licznik * i;
        nowyMianownik = mianownik * i;
        nowyUłamek = nowy Ułamek(nowyLicznik, nowyMianownik);
        zwróć nowyUłamek;
    }
}

x = nowy Ułamek(1, 2);
jeżeli (x.jestWłaściwy) {
    napisz("Zdefiniowano właśnie ułamek właściwy \n);
}
y = x.rozszerz(2);
napisz(y.licznik);      // 2
napisz(y.mianownik);    // 4
```
#### Słowo kluczowe zwróć
```
napiszWynikDzielenia(a, b) {
    jeżeli (b == 0) {
        napisz("Nie można dzielić przez zero! \n");
        zwróć;          // dalsze instrukcje nie wykonują się
    }
    c = a / b;
    napisz("Wynik dzielenia to: ");
    napisz(c);
} 
```
#### Przykrycie zmiennej
```
a = 20;
funkcja() {
    a = 40;
    napisz(a);      // 40
}
napisz(a);          // 20
```

### Niepoprawne konstrukcje i komunikaty wyjątków
#### na poziomie leksykalnym
```
var = ----;
var2 = @$abc;
Undefined expression starting at line x position y
```
```
var = 99999999999999999999999999999999999;
Numeric expression starting at line x position y exceeds limit
```
#### na poziomie składniowym
```
jeżeli (a > b {
    a = b;
}
Unclosed parenthesis starting at line x position y
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
Unsupported operation starting at line x position y
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
Division by 0 at line x postion y
```

### Gramatyka
Poniższa postać gramatyki ma być czytelna dla odbiorcy - może zawierać niejednoznaczności oraz rekurencję. Próba ich wyeliminowania zostanie podjęta w kolejnych iteracjach.

Zidentyfikowane przeze mnie potencjalne problemy:
* niejednoznaczność - kilka symboli zaczyna się od `<identifier>`
* rekursja lewostronna symbolu `<expression>` np. na ścieżce: `expression -> arithmetic-expression -> expression`
* redundancja (dla czytelności opisu gramatyki) `<parameter> == <identifier> == <declaration>`
* brak uwzględnienia priorytetów operacji

```
program                 = {statement};

statement               = assignment, ";",
                        | function-definition
                        | class-definition
                        | function-call, ";",
                        | object-creation, ";",

assignment              = {field-access}, identifier, ("=" | "+=" | "-="), expression;

function-definition     = identifier, "(", [parameters-list], ")", function-body;

class-definition        = "klasa", identifier, class-body;

function-call           = {field-access}, identifier, "(", ([arguments-list] | '"', print-message, '"'), ")";

object-creation         = "nowy", identifier, "(", [arguments-list], ")";

identifier              = letter, {letter | digit};

expression              = "(", expression, ")"
                        | boolean-expression
                        | arithmetic-expression
                        | relational-expression
                        | function-call
                        | object-creation
                        | identifier
                        | literal;
                        
field-access            = identifier, ".";                        

parameters-list         = parameter, {",", parameter};

parameter               = identifier;

function-body           = "{", {function-statement}, "}";

class-body              = "{", {declaration | assignment}, {function-definition}, "}";

declaration             = identifier;

arguments-list          = expression, {",", expression};

boolean-expression      = expression, ("oraz" | "lub"), expression
                        | "nie", expression;

arithmetic-expression   = expression, ("+" | "-" | "*" | "/"), expression;

relational-expression   = expression, ("<" | ">" | "<=" | ">=" | "==" | "!="), expression;

letter                  = "A" | "B" | "C" | ... | "Z" | "a" | "b" | "c" | ... | "z" | "_";

non-zero-digit          = "1" | "2" | "3" | ... | "9";

digit                   = "0" | non-zero-digit;

literal                 = integer
                        | float
                        | bool;

integer                 = "0" | (non-zero-digit, {digit});

float                   = integer, ".", digit, {digit};

bool                    = "prawda" | "fałsz";

print-message           = ({letter} | {digit} | {special-symbol}), [print-message];

special-symbol          = " " | "\" | "!" | "@" | ...
```

### Sposób uruchamiania
Do uruchomienia interpretera służy skryp w `bash` przyjmujący jako argument plik `.txt` zawierający treść programu 
do zinterpretowania. Wyjściem programu jest wyjście standardowe

### Analiza wymagań
* Interpreter ogranicza wielkość pliku wejściowego oraz długość konkretnego tokenu, ma to na celu uniemożliwienie 
  przepełnienia bufora
* Interpreter umożliwia przerwanie wykonywania programu skrótem klawiszowym
* W celu dostarczenia precyzyjnej informacji o błędzie w programie, obiekty typu `Token` przechowują swoją pozycję 
  w pliku (numer linii, pozycja znaku w linii). Więcej w punkcie [Analizator leksykalny](#analizator-leksykalny)
* Potencjalne problemy interpretacji to zapewnienie dostępu do zmiennych globalnych/lokalnych w zależności od kontekstu
  (zmienne lokalne "przykrywają" zmienne globalne) oraz zapewnienie unikalności nazw zmiennych i funkcji w tym samym
  kontekście. Próba odpowiedzi na te problemy znajduje się w sekcji [Realizacja analizatora semantycznego](#analizator-semantyczny).

### Realizacja
#### Analizator leksykalny
Analizator leksykalny (lekser) jest modułem odpowiedzialnym za przetworzenie pliku wejściowego na ciąg tokenów. 
Lekser oczekuje dwóch argumentów:
* `eventHandler` - wyrażenie lub funkcja (obiekt Function lub dziedziczący po nim) określające, co należy wykonać
  przy napotkaniu błędu
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
* słowa kluczowe: RETURN, FOR, IF, ELSE
* symbole: OPEN_BRACKET, CLOSE_BRACKET, OPEN_PARENTHESIS, CLOSE_PARENTHESIS, SEMICOLON, COMA, DOUBLE_QUOTE, ASSIGN
* operatory logiczne: AND, OR, NOT
* operatory matematyczne: ADD, SUBTRACT, MULTIPLY, DIVIDE, ADD_AND_ASSIGN, SUBTRACT_AND_ASSIGN
* operatory porównania: EQUAL, NOT_EQUAL, GREATER, LESS, GREATER_OR_EQUAL, LESS_OR_EQUAL
* wartości liczbowe i logiczne: INTEGER, FLOAT, BOOL, TEXT
* IDENTIFIER

Token typu `COMMENT` nie jest dołączany do listy wygenerowanych tokenów.

#### Analizator składniowy
Analizator składniowy (parser) jest modułem odpowiedzialnym za przetworzenie strumienia tokenów na drzewo rozbioru 
składniowego. Analiza odbywa się w sposób rekursywnie zstępujący. Drzewo jest zaimplementowane jako zbiór obiektów 
klasy `Node` (węzeł). Każdy z węzłów posiada "dzieci" (children) (uszeregowane) oraz "rodzica" (parent). 

Przykład drzewa dla wyrażenia arytmetycznego:

`a = (60 + 4) / 9 + 4` 

![tree.png](pictures%2Ftree.png)

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

### Sposób testowania
Do przetestowania analizatora leksykalnego służą testy jednostkowę sprawdzające, czy na podstawie danego pliku 
wejściowego lekser zwróci oczekiwaną listę tokenów lub podniesie oczekiwany wyjątek.

Do przetestowania analizatora składniowego służą testy jednostkowe sprawdzające, czy na podstawie danego strumienia 
tokenów parser utworzy oczekiwane drzewo rozbioru składniowego lub podniesie oczekiwany wyjątek. Testy jednostkowe 
obsługują również integrację leksera z parserem (testowanie budowania drzewa na podstawie pliku wejściowego).

Analizator składniowy jest najtrudniejszy do przetestowania automatycznie, jednak testy jednostkowe mogą wykazać 
podniesienie odpowiednich wyjątków na podstawie danego wejścia (strumienia znaków, tokenów, lub drzewa rozbioru, 
w zależności od testowanego poziomu integracji).
