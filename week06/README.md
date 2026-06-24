# TECHIT 온보딩 트랙 Week06 Final Project

## 프로젝트 소개

이번 프로젝트는 Spring Boot로 책 정보를 관리하는 Books CRUD API를 구현한 과제이다.

기존 Product 예제를 참고해서 `Book` 모델로 바꾸고, 과제 요구사항에 맞게 등록, 조회, 수정, 삭제 기능을 구현했다. API 기본 경로는 `/api/books`이고, H2 Database와 Postman을 사용해 동작을 확인했다.

코드 캡처는 따로 없어서 실행 결과와 H2 Console 확인 화면 중심으로 정리했다.

## 사용 기술

- Java
- Spring Boot
- Spring Data JPA
- H2 Database
- Postman

## 수정한 내용

- `Product` 관련 모델을 `Book` 기준으로 변경
- 책 이름, 가격, 저자를 저장할 수 있도록 필드 구성
- API 경로를 `/api/books`로 변경
- 책 등록, 전체 조회, 단일 조회, 수정, 삭제 기능 구현
- H2 Database와 JPA 설정 추가

## 프로젝트 구조

```text
SpringBootApi
├── src
│   └── main
│       ├── java
│       │   └── com/test/SpringBootApi
│       │       ├── controller
│       │       │   └── BookController.java
│       │       ├── domain
│       │       │   └── Book.java
│       │       ├── repository
│       │       │   └── BookRepository.java
│       │       ├── service
│       │       │   ├── BookService.java
│       │       │   └── BookServiceImpl.java
│       │       └── SpringBootApiApplication.java
│       └── resources
│           └── application.properties
├── build.gradle
├── gradlew
├── gradlew.bat
└── settings.gradle
```

### 구현 파일 설명

| 파일 | 설명 |
| --- | --- |
| `Book.java` | 책 정보를 저장하는 Entity |
| `BookRepository.java` | JPA를 이용해 DB와 연결하는 Repository |
| `BookService.java` | 책 CRUD 기능의 인터페이스 |
| `BookServiceImpl.java` | 실제 책 등록, 조회, 수정, 삭제 로직 구현 |
| `BookController.java` | 클라이언트 요청을 받아 API를 처리하는 Controller |
| `application.properties` | H2 Database와 JPA 설정 |

## CRUD API 정리

| 기능 | Method | URL | 설명 |
| --- | --- | --- | --- |
| 책 등록 | POST | `/api/books` | 새로운 책 정보 등록 |
| 책 전체 조회 | GET | `/api/books` | 저장된 책 목록 조회 |
| 책 단일 조회 | GET | `/api/books/{id}` | 특정 id의 책 조회 |
| 책 수정 | PUT | `/api/books/{id}` | 특정 id의 책 정보 수정 |
| 책 삭제 | DELETE | `/api/books/{id}` | 특정 id의 책 삭제 |

### 요청 예시

```json
{
  "bookName": "자바의 정석",
  "price": 30000,
  "author": "남궁성"
}
```

```json
{
  "bookName": "스프링 부트 입문",
  "price": 25000,
  "author": "김영한"
}
```

## 실행 결과

### 1. 책 등록

Postman에서 `POST /api/books` 요청을 보내 책 정보를 등록했다. 응답 상태는 `201 Created`였고, `id`, `bookName`, `price`, `author` 값이 함께 반환되었다.

![Postman POST 책 등록 성공](./images/화면%20캡처%202026-06-24%20143300.png)

### 2. 책 등록 후 DB 조회

H2 Console에서 `SELECT * FROM BOOKS;`를 실행해 등록한 데이터가 저장된 것을 확인했다.

![H2 Console 책 등록 후 조회](./images/화면%20캡처%202026-06-24%20143514.png)

### 3. 책 전체 조회

Postman에서 `GET /api/books` 요청으로 저장된 책 목록을 조회했다.

### 4. 책 단일 조회

Postman에서 `GET /api/books/{id}` 요청으로 특정 책 정보를 조회했다.

### 5. 책 수정

Postman에서 `PUT /api/books/{id}` 요청을 보내 책 정보를 수정했다. 응답 상태는 `200 OK`였고, 수정된 값으로 다시 반환되었다.

![Postman PUT 책 수정 성공](./images/화면%20캡처%202026-06-24%20143705.png)

### 6. 책 수정 후 DB 조회

H2 Console에서 다시 조회했을 때 수정한 값이 DB에도 반영된 것을 확인했다.

![H2 Console 책 수정 후 조회](./images/화면%20캡처%202026-06-24%20143723.png)

### 7. 책 삭제

Postman에서 `DELETE /api/books/{id}` 요청을 보내 책 정보를 삭제했다. 응답 상태는 `204 No Content`였다.

![Postman DELETE 책 삭제 성공](./images/화면%20캡처%202026-06-24%20143804.png)

### 8. 책 삭제 후 DB 조회

H2 Console에서 다시 조회했을 때 데이터가 삭제되어 `(no rows)`로 표시되는 것을 확인했다.

![H2 Console 책 삭제 후 조회](./images/화면%20캡처%202026-06-24%20143821.png)

## H2 Console 확인

H2 Console에 접속해서 API 결과가 실제 DB에 반영되는지 확인했다.

H2 Console 접속 주소:

```text
http://localhost:8080/h2-console
```

JDBC URL:

```text
jdbc:h2:~/test;AUTO_SERVER=true
```

데이터 확인 쿼리:

```sql
SELECT * FROM BOOKS;
```

## 배운 점

Spring Boot 구조가 아직 익숙하지 않아 처음엔 조금 어려웠지만, Controller, Service, Repository, Entity가 각각 어떤 역할을 하는지 이해할 수 있었다.

Postman으로 요청을 보내고 H2 Console에서 데이터가 실제로 저장, 수정, 삭제되는 것을 보면서 CRUD API 흐름을 익힐 수 있었다.
