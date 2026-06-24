# Week06 파이널 프로젝트 - Books CRUD API

## 프로젝트 소개

Spring Boot를 사용하여 책 정보를 관리하는 CRUD API를 구현하였다.  
기존 Product 예제를 참고하되, 과제 요구사항에 맞게 Books 모델로 수정하였다.

책 정보는 책 이름, 가격, 저자로 구성하였고, Postman을 사용하여 등록, 조회, 수정, 삭제 기능을 테스트하였다.  
또한 H2 Console을 통해 API 요청 결과가 실제 데이터베이스에 저장되는지 확인하였다.

## 사용 기술

- Java
- Spring Boot
- Spring Data JPA
- H2 Database
- Gradle
- Postman

## 수정한 내용

- Product 모델을 Book 모델로 변경
- 책 이름, 가격, 저자 필드 추가
- BookController 구현
- BookService, BookServiceImpl 구현
- BookRepository 구현
- /api/books 경로로 CRUD API 구현
- Postman으로 API 요청 테스트
- H2 Console에서 BOOKS 테이블 확인

## 프로젝트 구조

`	ext
week06
├─ SpringBootApi
│  ├─ src
│  │  └─ main
│  │     ├─ java
│  │     │  └─ com.test.SpringBootApi
│  │     │     ├─ controller
│  │     │     │  └─ BookController.java
│  │     │     ├─ domain
│  │     │     │  └─ Book.java
│  │     │     ├─ repository
│  │     │     │  └─ BookRepository.java
│  │     │     ├─ service
│  │     │     │  ├─ BookService.java
│  │     │     │  └─ BookServiceImpl.java
│  │     │     └─ SpringBootApiApplication.java
│  │     └─ resources
│  │        └─ application.properties
│  ├─ build.gradle
│  ├─ gradlew
│  ├─ gradlew.bat
│  └─ settings.gradle
├─ images
└─ README.md
