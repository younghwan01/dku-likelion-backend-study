# J2-week02 - Spring Boot 기본과 URL 단축 서비스

이번 주차에는 Spring Boot의 기본 동작을 익히고, 간단한 Todo CRUD와 URL 단축 서비스를 구현했다.

## 1. Spring Boot 기본

Spring Boot는 웹 백엔드 개발에 필요한 구조와 설정을 제공하는 프레임워크이다.

- `@Controller` : 요청을 처리하는 컨트롤러 클래스
- `@GetMapping` : GET 요청 URL과 메서드를 연결
- `@ResponseBody` : 메서드 반환값을 HTTP 응답 본문으로 전달
- `@RestController` : `@Controller + @ResponseBody`

```java
@GetMapping("/hello")
@ResponseBody
public String hello() {
    return "Hello";
}
```

## 2. HTTP 요청과 파라미터

브라우저가 서버에 요청을 보내면 Spring Boot가 URL의 파라미터를 자바 타입으로 변환해 메서드에 전달한다.

```text
http://localhost:8090/b?a=20&b=40
```

```java
public String plus(
        @RequestParam("a") int num1,
        @RequestParam("b") int num2
) {
    return "%d".formatted(num1 + num2);
}
```

URL 경로의 값을 받을 때는 `@PathVariable`을 사용한다.

```java
@GetMapping("/{id}")
public Todo getTodo(@PathVariable long id) {
    // id에 해당하는 Todo 조회
}
```

## 3. JSON, Lombok, 의존성 주입

Spring Boot는 Jackson을 통해 자바 객체를 JSON으로 변환해 응답할 수 있다.

Lombok을 사용해 반복되는 코드를 줄였다.

```java
@Getter
@Setter
@Builder
public class Todo {
    private Long id;
    private String body;
}
```

또한 `@Component`, `@Autowired`, `@Configuration`, `@Bean`을 사용해 Spring이 객체를 생성하고 필요한 곳에 주입하는 방식도 실습했다.

## 4. Todo CRUD

`demo02`에서는 메모리에 Todo를 저장하고 기본 CRUD를 구현했다.

| 기능 | 요청 예시 |
| --- | --- |
| 목록 | `GET /todos` |
| 추가 | `GET /todos/add?body=축구` |
| 상세 | `GET /todos/{id}` |
| 수정 | `GET /todos/modify/{id}?body=농구` |
| 삭제 | `GET /todos/remove/{id}` |

`stream()`, `filter()`, `findFirst()`, `removeIf()`를 활용해 데이터를 조회하고 삭제했다.

## 5. URL 단축 서비스

`demo03`에서는 긴 URL을 등록한 뒤 짧은 ID를 이용해 원래 주소로 이동하는 기능을 구현했다.

```text
GET /add?body=구글&url=https://www.google.com
GET /g/1
```

주요 구현 내용은 다음과 같다.

- URL과 설명(body), 생성 날짜 저장
- `HttpServletRequest`로 복잡한 URL과 Query String 처리
- 등록된 ID로 URL 검색
- `redirect:`를 이용해 원래 URL로 이동
- URL 사용 횟수 `count` 증가
- 존재하지 않는 ID 요청 시 예외 처리

```java
surl.increaseCount();
return "redirect:" + surl.getUrl();
```

## 프로젝트 구성

```text
J2-week02
├── README.md
├── demo-01   # Spring Boot 기본, JSON, Lombok, DI
├── demo02    # Todo CRUD
└── demo03    # URL 단축 서비스
```

이번 주차를 통해 Spring Boot에서 요청을 받고 데이터를 처리해 응답하는 기본 흐름과, 간단한 REST API 및 URL 리다이렉트 구현 방법을 익혔다.
