# J2-week01 - 자바로 구현하는 할 일 관리 서비스

이번 주차에는 Java로 간단한 할 일 관리 서비스를 구현하면서 CRUD의 기본 구조를 학습했다.

처음에는 하나의 `run()` 메서드 안에서 대부분의 기능을 구현했지만, 기능이 추가될수록 코드가 길어지고 관리하기 어려워졌다.

그래서 긴 메서드를 여러 개의 작은 메서드로 분리하고, 이후에는 `App` 클래스가 담당하던 기능을 `TodoController`, `SystemController`와 같은 별도의 클래스로 이동시키는 과정을 실습했다.

이를 통해 단순히 기능을 구현하는 것뿐만 아니라 코드의 역할을 분리하고 유지보수하기 좋은 구조로 변경하는 과정도 학습했다.

---

# 학습 내용

## 1. CRUD

이번 프로그램에서는 CRUD 기능을 직접 구현했다.

| CRUD | 의미 | 이번 프로젝트 |
| --- | --- | --- |
| Create | 생성 | `add` |
| Read | 조회 | `list` |
| Update | 수정 | `modify` |
| Delete | 삭제 | `del` |

할 일을 추가, 조회, 수정, 삭제하면서 CRUD의 기본 흐름을 익혔다.

---

## 2. Scanner를 이용한 사용자 입력

사용자의 명령어와 할 일 내용을 입력받기 위해 `Scanner`를 사용했다.

```java
Scanner scanner = new Scanner(System.in);

System.out.print("명령) ");
String cmd = scanner.nextLine().trim();
```

`nextLine()`으로 한 줄을 입력받고, `trim()`으로 앞뒤 공백을 제거했다.

---

## 3. 명령어를 계속 입력받도록 반복문 작성

프로그램이 계속 명령을 받을 수 있도록 `while (true)`를 사용했다.

```java
while (true) {
    System.out.print("명령) ");
    String cmd = scanner.nextLine().trim();

    if (cmd.equals("exit")) {
        break;
    }
}
```

`exit`이 입력되면 `break`를 통해 반복문을 종료하도록 했다.

---

## 4. Todo 객체 생성

할 일의 번호와 내용을 함께 관리하기 위해 `Todo` 클래스를 만들었다.

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

하나의 Todo 객체에 `id`와 `content`를 묶어서 관리했다.

---

## 5. ArrayList에 Todo 객체 저장

사용자가 몇 개의 할 일을 추가할지 알 수 없기 때문에 `ArrayList`를 사용했다.

```java
private ArrayList<Todo> todos;
private long todosLastId;
```

새로운 할 일을 추가할 때마다 마지막 ID를 증가시켜 고유 번호를 부여했다.

```java
long id = todosLastId + 1;

Todo todo = new Todo(id, content);

todos.add(todo);
todosLastId++;
```

---

## 6. list 기능 구현

저장된 할 일을 출력하기 위해 `forEach()`와 Lambda를 사용했다.

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

리스트에 저장된 Todo 객체를 하나씩 가져와 번호와 내용을 출력했다.

---

## 7. del 기능 구현

삭제할 번호를 입력받은 후 `removeIf()`를 사용해 해당 Todo를 삭제했다.

```java
long id = Long.parseLong(
    scanner.nextLine().trim()
);

boolean isRemoved =
    todos.removeIf(todo -> todo.getId() == id);
```

존재하지 않는 번호를 입력했을 경우에는 메시지를 출력하고 종료하도록 처리했다.

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

수정할 Todo를 찾기 위해 Stream을 사용했다.

```java
Todo foundTodo = todos.stream()
        .filter(todo -> todo.getId() == id)
        .findFirst()
        .orElse(null);
```

| 코드 | 역할 |
| --- | --- |
| `stream()` | 리스트를 Stream으로 변환 |
| `filter()` | 조건에 맞는 객체 검색 |
| `findFirst()` | 첫 번째 객체 반환 |
| `orElse(null)` | 없으면 `null` 반환 |

Todo를 찾은 뒤 Setter를 이용해 내용을 수정했다.

```java
String newContent =
    scanner.nextLine().trim();

foundTodo.setContent(newContent);
```

---

# 코드 구조 개선

이번 주차에서 중요하게 학습한 부분은 CRUD 구현 이후 코드 구조를 개선하는 과정이었다.

## 9. 너무 길어진 run() 메서드

처음에는 `run()` 메서드 안에서 모든 기능을 처리했다.

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

기능이 추가될수록 `run()`이 길어지고 관리하기 어려워졌다.

---

## 10. run() 안의 코드를 별도 메서드로 분리

`run()` 안의 기능들을 `add()`, `list()`, `del()`, `modify()` 메서드로 분리했다.

### 변경 전

```java
if (cmd.equals("add")) {
    // 추가 코드
}
else if (cmd.equals("list")) {
    // 조회 코드
}
```

### 변경 후

```java
private void add() {
    // 추가 기능
}

private void list() {
    // 조회 기능
}

private void del() {
    // 삭제 기능
}

private void modify() {
    // 수정 기능
}
```

이후 `run()`에서는 기능을 직접 처리하지 않고 메서드를 호출하도록 변경했다.

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

---

## 11. 메서드를 나눴지만 App 클래스가 너무 많은 일을 함

메서드는 분리했지만 여전히 `App` 클래스 안에 모든 기능이 모여 있었다.

```text
App
├── run()
├── add()
├── list()
├── del()
└── modify()
```

그래서 클래스의 역할도 나눌 필요가 있었다.

---

## 12. TodoController 클래스 생성

Todo와 관련된 기능을 따로 관리하기 위해 `TodoController` 클래스를 만들었다.

기존 `App` 안에 있던 다음 메서드들을 이동했다.

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

비슷한 역할의 메서드들을 하나의 클래스로 묶어 관리하도록 구조를 변경했다.

---

## 13. SystemController 생성

Todo 기능과 프로그램 종료 기능을 분리하기 위해 `SystemController`를 만들었다.

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

최종적으로 각 Controller가 담당하는 역할을 나눌 수 있었다.

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

Controller를 분리한 이후 `App`은 직접 CRUD 기능을 처리하지 않고, 입력된 명령에 따라 적절한 Controller를 호출한다.

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

최종 구조는 다음과 같다.

```text
사용자
  │
  ▼
 App
  │
  ├───────────────┐
  ▼               ▼
TodoController   SystemController
  │               │
  ├── add()        └── exit()
  ├── list()
  ├── del()
  └── modify()
```

`App`은 명령을 받고 각 기능을 담당하는 Controller로 전달하는 역할을 하게 되었다.

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

| 파일 | 역할 |
| --- | --- |
| `Main.java` | 프로그램 실행 시작 |
| `App.java` | 명령어 입력 및 Controller 연결 |
| `Todo.java` | 할 일 데이터 저장 |
| `TodoController.java` | Todo CRUD 기능 처리 |
| `SystemController.java` | 프로그램 종료 처리 |

---

# 사용한 Java 문법

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

이번 실습에서는 CRUD 기능을 구현하는 것뿐만 아니라, 기능이 많아졌을 때 코드를 어떻게 정리하고 분리해야 하는지도 학습했다.

처음에는 `run()` 메서드 하나에 대부분의 기능이 들어 있었지만, 기능별로 메서드를 분리하고 이후에는 `TodoController`, `SystemController`로 클래스를 나누었다.

이 과정을 통해 하나의 메서드나 클래스에 모든 기능을 넣는 것보다 역할별로 코드를 나누는 것이 더 효율적이라는 것을 알게 되었다.

특히 기능을 수정하거나 문제가 발생했을 때 확인해야 할 위치가 명확해져 유지보수가 쉬워지고, 새로운 기능을 추가할 때도 기존 코드에 미치는 영향을 줄일 수 있다는 점을 이해했다.

앞으로는 단순히 동작하는 코드를 만드는 것뿐만 아니라 **가독성, 역할 분리, 확장성, 유지보수성을 고려한 구조를 작성하는 것이 중요하다는 점**을 생각하게 되었다.
