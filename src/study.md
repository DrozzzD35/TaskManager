### 1. Теория

#### Обобщённые классы и методы

Generics появились в Java 5, чтобы обеспечить **type‑safety** (проверка типов на этапе компиляции) и убрать тонны кастов.

```java
List<String> names = new ArrayList<>();
```

-   `String` — это **аргумент типа**, `List<T>` — **параметризованный тип**, `T` — **формальный параметр типа**.

-   У класса может быть несколько параметров: `Map<K,V>`.

-   Обобщённые **методы** объявляют собственный `<T>` перед возвращаемым типом:

    ```java
    public static <T> T first(List<T> list) { … }
    ```

-   **Ограничения (bounds)**: `<T extends Comparable<T>>` — `T` обязан быть сравнимым. Можно комбинировать интерфейсы через `&`: `<T extends Number & Serializable>`.


> ⚙️ Под капотом действует **type erasure** — все параметры стираются до raw‑типа (`List`), поэтому нельзя `new T()` и `instanceof List<String>`.

#### Wildcards (символы подстановки)

-   **`?`** — «не знаю какой тип».

-   **`? extends T`** — ковариантность, «что‑то, что *расширяет* T» (читать можно, писать нельзя).

-   **`? super T`** — контравариантность, «что‑то, что *предок* T» (писать можно, читать как `Object`).

-   Запомнить просто: **PECS** — *Producer extends, Consumer super*.


```java
void copy(Collection<? extends T> src, Collection<? super T> dst) { … }
```

#### Ограничения и особенности

| Ограничение | Почему | Обход |
| --- | --- | --- |
| `new T()` запрещён | нет реального типа во время выполнения | передайте `Supplier<T>` или `Class<T>` |
| Нельзя `List<String>[] arr = ...` | массивы ковариантны, generics — нет | используйте `List<List<String>>` |
| Статические поля не могут использовать T | параметр типа связан с экземпляром, а не классом | вынесите логику в инстанс |
| Heap‑pollution при raw‑типах | теряется информация о типах | избегайте `List raw = ...` |

##### Краткое резюме

-   **Generics** = проверка типов во время компиляции + меньше кастов.

-   **Классы и методы** могут иметь один или несколько параметров типа с **bounds**.

-   **Wildcards**: `?`, `? extends`, `? super` — запомнить через **PECS**.

-   Под капотом всё стирается (type erasure) → ограничения (`new T()`, массивы).

-   Не смешивайте raw‑типы, иначе схлопочете **heap pollution**!


---

### 2. Практический пример

```java
// File: GenericStackDemo.java
import java.util.*;

public class GenericStack<E> {
    private final Deque<E> data = new ArrayDeque<>();

    public void push(E elem) { data.push(elem); }

    public E pop() { return data.pop(); }

    // Producer: читаем из чужой коллекции
    public void pushAll(Iterable<? extends E> src) {
        src.forEach(this::push);          // ? extends E  — только читаем
    }

    // Consumer: кладём в чужую коллекцию
    public void popAll(Collection<? super E> dst) {
        while (!data.isEmpty()) {
            dst.add(pop());               // ? super E — можем писать
        }
    }

    // Обобщённый статический метод: swap любых элементов списка
    public static <T> void swap(List<T> list, int i, int j) {
        T tmp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, tmp);
    }

    // Trick: swap с wildcard через «capture helper»
    public static void swapWildcard(List<?> list, int i, int j) {
        swap(list, i, j); // метод выше выведет T
    }

    public static void main(String[] args) {
        GenericStack<Number> stack = new GenericStack<>();
        List<Integer> ints = List.of(1, 2, 3);
        stack.pushAll(ints);              // OK — ? extends Number

        List<Object> sink = new ArrayList<>();
        stack.popAll(sink);               // OK — ? super Number
        System.out.println(sink);         // [3, 2, 1]

        List<String> words = new ArrayList<>(List.of("foo", "bar"));
        swapWildcard(words, 0, 1);
        System.out.println(words);        // [bar, foo]
    }
}
```

**Пошагово, что происходит**

1.  `GenericStack<E>` хранит элементы типа `E` в `Deque<E>`.

2.  `pushAll` принимает `Iterable<? extends E>` — чужая коллекция **производит** `E`.

3.  `popAll` принимает `Collection<? super E>` — чужая коллекция **потребляет** `E`.

4.  Статический `swap<T>` демонстрирует метод‑generic; `swapWildcard` показывает приём «capture helper» для списка с `?`.

5.  В `main` мы взаимодействуем с `List<Integer>` и `List<Object>` без кастов.


**Типичные ошибки**

| Ошибка | Симптом | Решение |
| --- | --- | --- |
| `List raw = new ArrayList<String>();` | `ClassCastException` в рантайме | Всегда указывайте `<String>` |
| `void pushAll(Collection<E> src)` | нельзя передать `List<Integer>` в `Stack<Number>` | использовать `? extends E` |
| `Stack<Integer> s = new Stack<Number>()` | компилятор ругается | параметры типов инвариантны |

---

### 3. Разбор примера

| Принцип | Где видно |
| --- | --- |
| **Inversion of Control** | Stack не знает о конкретной коллекции‑источнике/приёмнике. |
| **Ковариантность / контравариантность** | `? extends E`, `? super E`. |
| **DRY** | `swap` переиспользуется для wildcard‑списка. |
| **Fail‑fast** | Использование параметризованных типов ловит ошибки при компиляции. |

**Улучшения / mini‑code‑review**

1.  Mark `GenericStack` as **`final`** и сделать поле `data` тоже `final` (уже).

2.  Для многопоточности обернуть `Deque` в `Collections.synchronizedDeque`.

3.  Добавить метод `peek()` c возвратом `Optional<E>` вместо выброса `NoSuchElementException`.

4.  Использовать `record Pair<L,R>` для временных пар в `swap` → чистый код.


**Edge‑cases**

-   Попытка `pop()` на пустом стеке — выбросить `NoSuchElementException` или вернуть `Optional`.

-   Stack с самотипизирующимся параметром (`EnumSet<E extends Enum<E>>`).

-   Расширение: generic фабрика `Stack.of(E... elems)` с varargs.


---

### 4. Задания

| ★ | Задание | Ожидаемый результат | Подсказка |
| --- | --- | --- | --- |
| ★ | Реализуйте метод `max(List<T>)`, возвращающий наибольший элемент, где `T` — `Comparable`. | `Optional<T>` с максимальным значением или `empty()` для пустого списка. | Используйте `<T extends Comparable<? super T>>`. |
| ★★ | Создайте класс `ImmutablePair<L,R>` (или `record`) с методами `mapLeft`, `mapRight`, которые принимают `Function` и возвращают новую пару. | Чисто функциональные преобразования без мутаций. | `mapLeft(Function<? super L, ? extends NL>)`. |
| ★★★ | Спроектируйте `EventBus<E>`: подписчики (`Consumer<? super E>`) и метод `publish(E)`. Должен поддерживать иерархию типов (можно регистрировать `Consumer<Object>`). | Динамическое распределение событий, потокобезопасность. | Храните подписчиков в `CopyOnWriteArrayList`. |

#### Шаблон unit‑теста (JUnit 5)

```java
import org.junit.jupiter.api.Test;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class GenericStackTest {

    @Test
    void pushAndPopWorks() {
        GenericStack<String> stack = new GenericStack<>();
        stack.push("one");
        assertEquals("one", stack.pop());
    }
}
```