# Week 05 - 웹의 동작 원리와 Spring Boot CRUD API

이번 주차에는 웹이 요청과 응답으로 동작하는 흐름을 배우고, Spring Boot로 Product CRUD API를 실습했습니다.

---

## 학습 내용

| 구분 | 정리 |
|---|---|
| 웹 동작 원리 | 클라이언트가 요청하고 서버가 응답 |
| HTTP / URL | 웹에서 데이터를 주고받는 규칙과 주소 체계 |
| 쿠키 / 세션 | HTTP의 무상태성을 보완하는 상태 관리 방식 |
| 네트워크 | IP, 포트, DNS의 역할 |
| Spring Boot | Java 웹 애플리케이션 개발 프레임워크 |
| CRUD | 데이터 생성, 조회, 수정, 삭제 |
| H2 Database | 실습용 인메모리 데이터베이스 |

---

## 웹 기본 개념

| 개념 | 설명 |
|---|---|
| 클라이언트 | 브라우저처럼 서버에 요청을 보내는 쪽 |
| 서버 | 요청을 처리하고 응답을 보내는 쪽 |
| HTTP | 클라이언트와 서버가 통신하기 위한 규칙 |
| URL | 인터넷 자원의 위치를 나타내는 주소 |

```text
Client -> Request -> Server
Client <- Response <- Server
```

URL은 프로토콜, 호스트, 경로, 쿼리 문자열로 구성됩니다.

```text
https://www.google.com/search?q=hackit
```

| 구성 요소 | 예시 |
|---|---|
| 프로토콜 | `https://` |
| 호스트 | `www.google.com` |
| 경로 | `/search` |
| 쿼리 문자열 | `?q=hackit` |

---

## 쿠키와 세션

HTTP는 이전 요청을 기억하지 않는 무상태성을 가집니다. 로그인 상태처럼 사용자의 상태를 유지하기 위해 쿠키와 세션을 사용합니다.

| 구분 | 설명 |
|---|---|
| 쿠키 | 클라이언트 브라우저에 저장되는 작은 데이터 |
| 세션 | 서버에 상태 정보를 저장하고 클라이언트에는 세션 ID만 전달 |

---

## 네트워크 기초

| 개념 | 설명 |
|---|---|
| IP | 네트워크에서 컴퓨터를 찾기 위한 주소 |
| 포트 | 한 컴퓨터 안에서 실행 중인 서비스를 구분하는 번호 |
| DNS | 도메인 이름을 IP 주소로 변환하는 시스템 |
| localhost | 내 컴퓨터를 가리키는 주소 |

```text
localhost:8080
```

---

## Spring Boot 프로젝트 생성

Spring Initializr에서 Spring Boot 프로젝트를 만들고 필요한 의존성을 추가했습니다.

<img src="./images/spring-initializr.png" alt="Spring Initializr 설정 화면" width="760" />

| 설정 | 내용 |
|---|---|
| Project | Gradle - Groovy |
| Language | Java |
| Packaging | Jar |
| Java | 17 |
| Dependencies | Spring Web, Spring Data JPA, H2 Database |

---

## Spring Boot와 MVC

Spring Boot는 Java 웹 애플리케이션을 쉽게 만들 수 있게 도와주는 프레임워크입니다. 내장 Tomcat을 사용해 별도 서버 설정 없이 실행할 수 있습니다.

```text
Client
  -> Controller
  -> Service
  -> Repository
  -> H2 Database
```

| 계층 | 역할 |
|---|---|
| Controller | HTTP 요청을 받음 |
| Service | 핵심 로직 처리 |
| Repository | 데이터베이스와 연결 |
| Database | 데이터 저장 |

---

## Product CRUD API 실습

Product 데이터를 생성, 조회, 수정, 삭제하는 API를 구현했습니다.

| 기능 | HTTP Method | 경로 |
|---|---|---|
| 생성 | `POST` | `/api/products` |
| 조회 | `GET` | `/api/products/{id}` |
| 수정 | `PUT` | `/api/products/{id}` |
| 삭제 | `DELETE` | `/api/products/{id}` |

요청 데이터 예시는 다음과 같습니다.

```json
{
  "name": "책상",
  "characteristic": "나무",
  "price": 140000
}
```

---

## H2 Database 확인

Spring Boot 애플리케이션 실행 후 브라우저에서 H2 콘솔에 접속해 데이터베이스를 확인했습니다.

```text
http://localhost:8080/h2-console
```

<img src="./images/h2-console.png" alt="H2 Console 실행 화면" width="900" />

데이터 조회는 SQL로 확인할 수 있습니다.

```sql
SELECT * FROM PRODUCTS;
```

---

## 실습 파일 구조

```text
week05/
├── README.md
└── SpringBootApi/
    ├── build.gradle
    ├── settings.gradle
    ├── gradlew
    ├── gradlew.bat
    ├── gradle/wrapper/
    └── src/
        ├── main/java/com/test/SpringBootApi/
        │   ├── SpringBootApiApplication.java
        │   ├── controller/ProductController.java
        │   ├── domain/Product.java
        │   ├── respository/ProductRepository.java
        │   └── service/
        └── main/resources/application.properties
```

---

## 이번 주차 정리

이번 주차에는 웹의 기본 흐름인 요청과 응답 구조를 이해하고, HTTP, URL, 쿠키, 세션, IP, 포트, DNS의 역할을 정리했습니다.

또한 Spring Boot 프로젝트를 생성하고 MVC 구조를 바탕으로 Product CRUD API를 구현했습니다. H2 Database와 Postman을 사용해 데이터 생성, 조회, 수정, 삭제 흐름을 직접 확인했습니다.

