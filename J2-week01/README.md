# J2-week01 - 자바로 구현하는 할 일 관리 서비스

이번 주차에는 Java로 간단한 할 일 관리 서비스를 구현하면서 CRUD의 기본 구조를 학습했다.

처음에는 하나의 `run()` 메서드 안에서 대부분의 기능을 구현했지만, 기능이 추가될수록 코드가 길어지고 관리하기 어려워졌다.

그래서 긴 메서드를 여러 개의 작은 메서드로 분리하고, 이후에는 `App` 클래스가 담당하던 기능을 `TodoController`, `SystemController`와 같은 별도의 클래스로 이동시키는 과정을 실습했다.

이를 통해 단순히 기능을 구현하는 것뿐만 아니라 코드의 역할을 분리하고 유지보수하기 좋은 구조로 변경하는 과정도 학습했다.

---

# 학습 내용

## 1. CRUD

이번 프로그램에서는 데이터를 관리하는 가장 기본적인 패턴인 CRUD를 직접 구현했다.

| CRUD | 의미 | 이번 프로젝트 |
| --- | --- | --- |
| Create | 생성 | `add` |
| Read | 조회 | `list` |
| Update | 수정 | `modify` |
| Delete | 삭제 | `del` |

할 일을 추가하고, 확인하고, 수정하고, 삭제하는 기능을 구현하면서 대부분의 애플리케이션이 CRUD 기능의 조합으로 이루어진다는 것을 확인했다.

---

## 2. Scanner를 이용한 사용자 입력

사용자가 직접 명령어와 할 일 내용을 입력할 수 있도록 `Scanner`를 사용했다.

```java
Scanner scanner = new Scanner(System.in);

System.out.print("명령) ");
String cmd = scanner.nextLine().trim();
```

`nextLine()`은 사용자가 Enter를 입력할 때까지 기다린 뒤 입력한 문자열을 가져온다.

입력값의 앞뒤에 불필요한 공백이 들어갈 수 있기 때문에 `trim()`을 사용했다.

```java
String content = scanner.nextLine().trim();
```

---

## 3. 명령어를 계속 입력받도록 반복문 작성

프로그램이 명령 하나를 실행하고 바로 종료되지 않도록 `while (true)`를 사용했다.

```java
while (true) {
    System.out.print("명령) ");
    String cmd = scanner.nextLine().trim();

    if (cmd.equals("exit")) {
        break;
    }
}
```

`exit`이 입력되기 전까지 계속 명령을 입력받고, `break`를 통해 반복문을 종료한다.

이 과정에서 조건문과 반복문을 이용해 프로그램의 실행 흐름을 제어하는 방법을 실습했다.

---

## 4. Todo 객체 생성

처음에는 할 일의 내용만 있으면 된다고 생각할 수 있지만 여러 개의 할 일을 관리하려면 각 할 일을 구분할 번호가 필요하다.

따라서 `id`와 `content`를 하나로 묶기 위해 `Todo` 클래스를 만들었다.

```java
public class Todo {

    private long id;
    private String content;

    public Todo(long id, String content) {
        this.id = id;
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
```

하나의 Todo 객체에는 다음 두 가지 정보가 들어간다.

```text
Todo
├── id
└── content
```

서로 연관된 데이터를 하나의 클래스로 묶어 객체로 관리할 수 있다는 것을 배웠다.

---

## 5. ArrayList에 Todo 객체 저장

사용자가 앞으로 몇 개의 할 일을 만들지는 미리 알 수 없다.

따라서 크기가 고정되어 있는 배열보다 크기를 자유롭게 늘릴 수 있는 `ArrayList`를 사용했다.

```java
private ArrayList<Todo> todos;
private long todosLastId;
```

새로운 할 일을 만들 때는 마지막 ID를 기준으로 새로운 번호를 생성했다.

```java
long id = todosLastId + 1;

Todo todo = new Todo(id, content);

todos.add(todo);
todosLastId++;
```

할 일이 삭제되더라도 ID를 다시 사용하는 것이 아니라 마지막 ID를 계속 증가시키도록 구현했다.

예를 들어,

```text
1번 생성
2번 생성
2번 삭제
3번 생성
```

과 같이 동작한다.

---

## 6. list 기능 구현

저장된 할 일들을 출력하기 위해 `forEach()`를 사용했다.

```java
public void list() {
    System.out.println("번호 / 내용");

    todos.forEach(todo ->
        System.out.printf(
            "%d / %s\n",
            todo.getId(),
            todo.getContent()
        )
    );
}
```

`todos` 안에 들어있는 각각의 Todo 객체에 대해 같은 코드를 반복해서 실행한다.

이 과정에서 Lambda 표현식도 함께 사용해 볼 수 있었다.

```java
todo -> 실행할 코드
```

---

## 7. del 기능 구현

삭제할 할 일 번호를 사용자에게 입력받았다.

```java
System.out.print("삭제할 할일의 번호 : ");

long id = Long.parseLong(
    scanner.nextLine().trim()
);
```

사용자 입력은 문자열이기 때문에 `Long.parseLong()`을 이용해 `long` 타입으로 변경했다.

이후 `removeIf()`를 사용해 같은 ID를 가진 Todo를 삭제했다.

```java
boolean isRemoved =
    todos.removeIf(todo -> todo.getId() == id);
```

`removeIf()`는 리스트의 요소를 하나씩 확인하면서 조건이 `true`가 되는 객체를 삭제한다.

삭제할 Todo가 존재하지 않는 경우도 처리했다.

```java
if (!isRemoved) {
    System.out.printf(
        "%d번 할일은 존재하지 않습니다.\n",
        id
    );

    return;
}
```

---

## 8. modify 기능 구현

수정 기능에서는 먼저 사용자가 입력한 ID와 일치하는 Todo 객체를 찾아야 했다.

이를 위해 Stream을 사용했다.

```java
Todo foundTodo = todos.stream()
        .filter(todo -> todo.getId() == id)
        .findFirst()
        .orElse(null);
```

각 부분의 역할은 다음과 같다.

| 코드 | 역할 |
| --- | --- |
| `stream()` | 리스트를 Stream 형태로 변환 |
| `filter()` | 조건에 맞는 객체만 찾음 |
| `findFirst()` | 조건에 맞는 첫 번째 객체 반환 |
| `orElse(null)` | 찾지 못하면 `null` 반환 |

찾은 Todo가 없다면 메서드를 종료했다.

```java
if (foundTodo == null) {
    System.out.printf(
        "%d번 할일은 존재하지 않습니다.\n",
        id
    );

    return;
}
```

Todo가 존재한다면 기존 내용을 출력하고 새 내용을 입력받았다.

```java
System.out.printf(
    "기존 할일 : %s\n",
    foundTodo.getContent()
);

System.out.print("새 할일 : ");

String newContent =
    scanner.nextLine().trim();

foundTodo.setContent(newContent);
```

Setter를 사용해 기존 객체의 `content` 값만 변경했다.

---

# 코드 구조 개선

이번 강의에서 가장 중요하게 느낀 부분은 CRUD 구현 이후에 진행한 코드 구조 개선이었다.

## 9. 너무 길어진 run() 메서드

처음에는 `run()` 메서드 하나 안에 모든 기능이 들어 있었다.

구조를 단순하게 보면 다음과 같다.

```text
run()
│
├── Scanner 입력
├── exit 처리
├── add 처리
├── list 처리
├── del 처리
└── modify 처리
```

기능이 적을 때는 문제가 없어 보였지만 기능이 계속 추가되자 `run()`이 너무 길어졌다.

하나의 메서드가 여러 가지 일을 동시에 하고 있어서 어떤 기능을 수정하려면 긴 코드 안에서 해당 부분을 찾아야 했다.

---

## 10. run() 안의 코드를 별도 메서드로 분리

먼저 `run()` 안에 있던 기능들을 각각의 메서드로 꺼냈다.

### 변경 전

```java
public void run() {

    while (true) {

        String cmd = scanner.nextLine().trim();

        if (cmd.equals("add")) {
            // 할 일 추가 코드
        }

        else if (cmd.equals("list")) {
            // 리스트 출력 코드
        }

        else if (cmd.equals("del")) {
            // 삭제 코드
        }

        else if (cmd.equals("modify")) {
            // 수정 코드
        }
    }
}
```

### 변경 후

```java
private void add() {
    // 할 일 추가
}

private void list() {
    // 할 일 조회
}

private void del() {
    // 할 일 삭제
}

private void modify() {
    // 할 일 수정
}
```

`run()`에서는 직접 기능을 구현하지 않고 해당 메서드를 호출하도록 변경했다.

```java
if (cmd.equals("add")) {
    add();
}
else if (cmd.equals("list")) {
    list();
}
else if (cmd.equals("del")) {
    del();
}
else if (cmd.equals("modify")) {
    modify();
}
```

구조가 다음과 같이 바뀌었다.

```text
run()
 │
 ├── add()
 ├── list()
 ├── del()
 └── modify()
```

한 메서드 안에서 모든 일을 처리하던 코드를 기능별로 나누면서 각 메서드가 담당하는 역할이 명확해졌다.

---

## 11. 메서드를 나눴지만 App 클래스가 너무 많은 일을 함

메서드를 분리하면서 `run()`은 짧아졌지만 이번에는 `App` 클래스 안에 너무 많은 기능이 있다는 문제가 생겼다.

```text
App

├── run()
├── add()
├── list()
├── del()
└── modify()
```

`App`은 명령어를 입력받는 역할도 하고 있었고 Todo를 추가하고 조회하고 삭제하고 수정하는 기능까지 전부 가지고 있었다.

즉 하나의 클래스가 너무 많은 책임을 가지고 있었다.

---

## 12. TodoController 클래스 생성

Todo와 관련된 기능을 따로 관리하기 위해 `TodoController` 클래스를 생성했다.

```java
public class TodoController {

    private Scanner scanner;
    private ArrayList<Todo> todos;
    private long todosLastId;

    public TodoController() {
        scanner = new Scanner(System.in);
        todos = new ArrayList<>();
        todosLastId = 0;
    }
}
```

그리고 기존 `App` 안에 있던 다음 메서드들을 이동했다.

```text
add()
list()
del()
modify()
```

### 변경 전

```text
App
├── run()
├── add()
├── list()
├── del()
└── modify()
```

### 변경 후

```text
App
└── run()

TodoController
├── add()
├── list()
├── del()
└── modify()
```

실제로 `TodoController`는 다음과 같은 형태가 되었다.

```java
public class TodoController {

    public void add() {
        // 추가 기능
    }

    public void list() {
        // 조회 기능
    }

    public void del() {
        // 삭제 기능
    }

    public void modify() {
        // 수정 기능
    }
}
```

메서드를 단순히 나누는 것에서 한 단계 더 나아가, 비슷한 역할을 담당하는 메서드들을 별도의 클래스로 옮기는 과정을 실습했다.

---

## 13. SystemController 생성

Todo 기능과 프로그램 종료 기능은 서로 다른 역할이다.

그래서 `exit` 기능은 `SystemController`라는 별도의 클래스로 분리했다.

```java
public class SystemController {

    public void exit() {
        System.out.println(
            "앱 종료 명령이 입력되었습니다."
        );

        System.out.println(
            "프로그램이 곧 종료합니다."
        );
    }
}
```

최종적으로 Controller의 역할은 다음처럼 나누어졌다.

```text
TodoController
├── add()
├── list()
├── del()
└── modify()


SystemController
└── exit()
```

---

## 14. App은 명령을 Controller에게 전달

Controller들을 분리한 이후 `App`에서는 Controller 객체를 가지고 있도록 변경했다.

```java
public class App {

    private Scanner scanner;
    private TodoController todoController;
    private SystemController systemController;

    public App() {
        scanner = new Scanner(System.in);
        systemController = new SystemController();
        todoController = new TodoController();
    }
}
```

이제 `App`은 직접 Todo를 추가하거나 수정하지 않는다.

사용자가 입력한 명령을 확인하고 해당 기능을 담당하는 Controller에 전달한다.

```java
if (cmd.equals("exit")) {
    systemController.exit();
    break;
}
else if (cmd.equals("add")) {
    todoController.add();
}
else if (cmd.equals("list")) {
    todoController.list();
}
else if (cmd.equals("modify")) {
    todoController.modify();
}
else if (cmd.equals("del")) {
    todoController.del();
}
```

구조를 그림으로 표현하면 다음과 같다.

```text
사용자
  │
  │ 명령 입력
  ▼
 App
  │
  ├──────────────────┐
  │                  │
  ▼                  ▼
TodoController    SystemController
  │                  │
  ├── add()           └── exit()
  ├── list()
  ├── del()
  └── modify()
```

`App`은 요청을 직접 처리하는 것이 아니라, 사용자의 명령을 보고 어떤 Controller가 일을 처리할지 연결해주는 역할을 한다.

---

# 최종 프로젝트 구조

```text
J2-week01
├── README.md
└── src
    └── main
        └── java
            └── com
                └── ll
                    ├── Main.java
                    ├── App.java
                    ├── Todo.java
                    ├── TodoController.java
                    └── SystemController.java
```

각 파일의 역할은 다음과 같다.

| 파일 | 역할 |
| --- | --- |
| `Main.java` | 프로그램 실행 시작 |
| `App.java` | 명령어 입력 및 Controller 연결 |
| `Todo.java` | 할 일 데이터를 표현하는 객체 |
| `TodoController.java` | Todo 추가, 조회, 수정, 삭제 |
| `SystemController.java` | 프로그램 종료 처리 |

---

# 사용한 Java 문법

이번 주차에서 사용한 주요 Java 문법은 다음과 같다.

- `Scanner`
- `while`
- `if / else if`
- `break`
- `return`
- 클래스 / 객체
- 생성자
- Getter / Setter
- `ArrayList`
- `forEach()`
- Lambda
- `removeIf()`
- Stream
- `filter()`
- `findFirst()`
- `orElse()`
- `Long.parseLong()`

---

# 배운 점

이번 실습에서는 CRUD 기능 자체를 구현하는 것도 중요했지만, **기능을 구현한 이후 코드를 다시 정리하는 과정**이 더 인상적이었다.

처음에는 `run()` 메서드 하나 안에 명령어 처리와 CRUD 코드가 모두 들어 있었지만 기능이 늘어나면서 코드가 점점 길어졌다.

그래서 `add()`, `list()`, `del()`, `modify()`와 같이 기능별로 메서드를 분리했고, 이후에는 이 메서드들까지 `TodoController`라는 새로운 클래스로 옮겼다.

또한 Todo와 관계없는 종료 기능은 `SystemController`로 따로 분리하면서 하나의 클래스가 모든 기능을 담당하는 것보다 **비슷한 역할끼리 묶어서 관리하는 것이 훨씬 효율적이라는 것을 알게 되었다.**

처음에는 프로그램이 정상적으로 실행되면 충분하다고 생각했지만, 코드를 계속 수정하면서 단순히 동작하는 코드와 수정하기 편한 코드는 다를 수 있다는 생각이 들었다.

특히 기능을 메서드와 클래스로 나누면 나중에 특정 기능을 수정하거나 문제가 발생했을 때 확인해야 할 위치가 명확해지고, 다른 부분에 영향을 줄 가능성도 줄어든다.

이번 실습을 통해 앞으로는 기능 구현뿐만 아니라 **코드의 가독성, 역할 분리, 확장성, 유지보수성까지 고려하면서 코드를 작성하는 것이 중요하다는 점**을 배울 수 있었다.
