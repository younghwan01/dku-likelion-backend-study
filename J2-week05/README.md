# 5주차 - Spring Data JPA로 서비스에 영속성 부여

이번 주차에서는 **Spring Data JPA를 활용하여 자바 객체를 MySQL에 저장하고, 애플리케이션을 다시 실행해도 데이터가 유지되는 구조를 학습했습니다.**
게시글 CRUD를 통해 엔티티와 리포지터리의 기본 사용법을 배우고, 회원과 작성자 관계를 연결하여 URL 단축 서비스에 적용하는 흐름을 정리했습니다.

> 강의 챕터 06의 스텝 01~20을 바탕으로 작성한 독립 실행형 실습 프로젝트입니다. 기존 2주차의 메모리 기반 URL 서비스를 JPA 방식으로 재구성했습니다. 강사 원본을 그대로 복사한 프로젝트는 아닙니다.

## 실행 환경

- Java 17 이상, Spring Boot 3.2.4, Gradle 8.7
- Spring Data JPA / Hibernate / MySQL
- Lombok / Bean Validation
- MySQL은 기존 `mysql-1` 컨테이너를 그대로 사용할 수 있습니다.
- 강의의 Java 21 코드와 동일한 핵심 개념을 사용하며, 이 프로젝트의 컴파일 기준은 Java 17입니다.

## 핵심 학습 내용

### 1. 영속성과 ORM

메모리의 `List`에만 URL을 저장하면 서버가 종료될 때 데이터가 사라집니다. DB에 저장하면 애플리케이션을 다시 실행한 후에도 저장된 URL과 조회수를 조회할 수 있습니다.

ORM은 객체와 관계형 테이블을 매핑하여 SQL 생성과 실행을 도와주는 기술입니다. **JPA는 자바 ORM의 표준 명세이며, Hibernate는 그 구현체입니다.** Spring Data JPA는 그 위에서 리포지터리 구현을 지원합니다.

```text
Spring Data JPA → JPA / Hibernate → JDBC → MySQL
```

### 2. JPA 의존성과 DB 연결

```groovy
implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
runtimeOnly 'com.mysql:mysql-connector-j'
```

```yaml
spring:
  datasource:
    url: ${DB_URL:jdbc:mysql://localhost:3306/surl_dev?serverTimezone=Asia/Seoul&characterEncoding=UTF-8}
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD}
    driver-class-name: com.mysql.cj.jdbc.Driver
```

접속 오류가 발생하면 MySQL 실행 여부, 포트, DB 이름, 계정과 비밀번호를 확인해야 한다는 점을 배웠습니다. 비밀번호는 파일에 직접 적지 않고 환경변수로 전달합니다.

### 3. 엔티티와 테이블 생성

```java
@Entity
public class Article extends BaseTime {
    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;
}
```

- `@Entity`: JPA가 관리할 엔티티를 지정합니다.
- `@Id`: 기본키 필드를 지정합니다.
- `@GeneratedValue(strategy = GenerationType.IDENTITY)`: MySQL의 자동 증가 키를 사용합니다.
- `Long`: 키가 아직 생성되지 않은 상태를 `null`로 표현할 수 있습니다. `long`은 원시 타입으로 `null`을 사용할 수 없습니다.
- `@NoArgsConstructor(access = AccessLevel.PROTECTED)`: JPA에 필요한 기본 생성자를 제공합니다.
- `ddl-auto: update`: 개발용으로 엔티티에 맞춰 테이블 구조를 갱신합니다.

`ddl-auto: update`는 엔티티 필드를 지웠다고 기존 컬럼을 자동으로 지워주지는 않습니다. 또한 데이터 값을 수정하는 기능과 테이블 구조를 변경하는 기능은 서로 다릅니다.

### 4. JpaRepository와 CRUD

```java
public interface ArticleRepository extends JpaRepository<Article, Long> {
}
```

| 메서드 | 역할 |
| --- | --- |
| `save()` / `saveAndFlush()` | 엔티티 저장 / 저장 후 즉시 flush |
| `findById()` | ID로 조회, `Optional` 반환 |
| `findAll()` | 전체 조회, `List` 반환 |
| `count()` | 데이터 개수 조회 |
| `delete()` | 엔티티 삭제 |

`Optional`은 값이 없거나 하나인 결과를 표현하고, `List`는 여러 결과를 담습니다. `Optional.get()`을 바로 호출하기보다 `orElseThrow()`로 조회 실패를 처리하는 방법을 배웠습니다.

### 5. 트랜잭션과 더티 체킹

```java
@Transactional
public ArticleDto modify(Long id, String title, String body, Long memberId) {
    Article article = findEntity(id);
    checkAuthor(article, memberId);
    article.modify(title, body);
    repository.flush();
    return ArticleDto.from(article);
}
```

트랜잭션 안에서 조회한 영속 엔티티의 값을 변경하면, Hibernate가 변경 사항을 감지하여 UPDATE를 실행합니다. 수정할 때 반드시 `save()`를 다시 호출해야 하는 것은 아니라는 점을 배웠습니다.

- 서비스 클래스에는 `@Transactional(readOnly = true)`를 적용했습니다.
- 생성·수정·삭제 메서드에는 쓰기용 `@Transactional`을 적용했습니다.
- 기본 설정에서 `RuntimeException`이 트랜잭션 경계를 넘어 전달되면 롤백됩니다.
- 같은 객체 내부의 `this.method()` 호출은 기본 프록시 방식에서 새로운 트랜잭션 설정을 적용하지 않습니다.
- 이 프로젝트는 컨트롤러나 다른 빈에서 서비스를 호출하는 구조를 사용합니다.

`try-catch`가 롤백의 필수 조건은 아닙니다. 예외를 어디에서 잡았는지와 트랜잭션 경계를 통과했는지에 따라 결과가 달라집니다. 기본 REQUIRED 전파로 내부 서비스가 같은 트랜잭션에 참여하다 롤백 전용으로 표시하면, 바깥쪽에서 예외를 잡아도 정상 커밋할 수 없습니다.

### 6. 조건 검색과 정렬

```java
List<Article> findByTitleContainingOrderByIdAsc(String keyword);
List<Article> findByIdInOrderByTitleDescIdAsc(List<Long> ids);
List<Article> findByTitleAndBody(String title, String body);
```

메서드 이름의 규칙으로 LIKE 검색, IN 조건, AND 조건, 정렬을 표현하는 방법을 학습했습니다. 여러 게시물의 작성자를 조회하는 메서드에는 `@EntityGraph`를 적용했습니다.

### 7. 서비스 계층과 공통 응답

컨트롤러는 HTTP 요청을 받고, 서비스는 업무 로직과 트랜잭션을 처리하며, 리포지터리는 데이터 접근을 담당합니다.

```java
public record RsData<T>(String resultCode, String message, T data) {}
```

응답은 결과 코드·메시지·데이터를 함께 반환합니다. 예를 들어 회원가입 성공은 HTTP 201과 `201-1` 결과 코드를 반환하고, 존재하지 않는 게시물 조회는 HTTP 404를 반환합니다. 응답 안의 코드와 실제 HTTP 상태 코드를 함께 구분하여 처리했습니다.

`GlobalException`과 `@RestControllerAdvice`로 오류 처리를 통일했습니다. 회원 아이디 중복은 사전 조회와 DB의 UNIQUE 제약으로 검사합니다.

### 8. 날짜 자동 기록과 공통 엔티티

```java
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTime extends BaseEntity {
    @CreatedDate
    private LocalDateTime createDate;

    @LastModifiedDate
    private LocalDateTime modifyDate;
}
```

`BaseEntity`의 ID와 `BaseTime`의 생성일·수정일을 각 엔티티가 상속받도록 구성했습니다. `@EnableJpaAuditing`과 엔티티 리스너를 함께 사용하여 날짜가 자동으로 기록되도록 했습니다. `@MappedSuperclass` 자체에 대응하는 별도 테이블은 생성되지 않습니다.

### 9. 작성자 관계와 지연 로딩

```java
@ManyToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "author_id", nullable = false)
private Member author;
```

한 회원이 여러 게시글과 URL을 등록할 수 있으므로 `ManyToOne` 관계를 사용했습니다. 자바에서는 `Member` 객체로 다루고, DB에서는 `author_id` 외래키로 저장합니다.

`getReferenceById()`는 일반적으로 엔티티의 참조를 반환하여 실제 회원 정보 조회를 늦출 수 있습니다. **프록시는 쓰기 서비스의 트랜잭션 안에서 얻도록** 구성했습니다. ID 이외의 속성을 읽으면 초기화가 필요할 수 있으며, 존재하지 않는 ID의 확인 시점도 `findById()`와 다릅니다.

엔티티를 직접 JSON으로 반환하지 않고 DTO로 변환하여 비밀번호 노출과 프록시 직렬화 문제를 피했습니다. `default_batch_fetch_size`는 일부 N+1 문제를 완화하는 설정이며, 모든 N+1 문제를 자동으로 해결하는 것은 아닙니다.

### 10. URL 서비스에 적용

- 로그인한 회원의 ID를 세션에 저장합니다.
- 요청마다 생성되는 `Rq`가 현재 회원 ID를 읽습니다.
- `SurlService`가 URL·설명·작성자 정보를 DB에 저장합니다.
- `/g/{id}` 접근 시 조회수를 증가시키고 원래 주소로 HTTP 302 이동합니다.
- 조회수 변경에는 행 잠금을 사용하여 동시에 들어온 요청의 증가분 유실을 방지했습니다.
- URL 정보와 조회수는 DB에 남고, 기본 메모리 세션은 서버 재시작 시 사라집니다. 다시 로그인하면 계속 등록할 수 있습니다.

## 프로젝트 구성

| 경로 | 내용 |
| --- | --- |
| `src/main/java/com/ll/jpa/domain/article` | 게시글 엔티티·리포지터리·서비스·컨트롤러·DTO |
| `src/main/java/com/ll/jpa/domain/member` | 회원가입·로그인·회원 DTO, 비밀번호 해시 저장 |
| `src/main/java/com/ll/jpa/domain/surl` | URL 저장·조회·리다이렉트·조회수 증가 |
| `src/main/java/com/ll/jpa/global` | 공통 엔티티·응답·예외 처리·Rq·개발 샘플 데이터 |
| `src/main/resources` | MySQL 연결과 JPA 설정 |
| `src/test` | H2 기반 API·트랜잭션·재시작 영속성 테스트 |
| `sql/check-data.sql` | DBeaver에서 테이블·작성자·조회수 확인 |
| `requests.http` | IntelliJ HTTP Client 실습 요청 |

## 실행 방법

### 1. MySQL 준비

Docker Desktop을 켜고 기존 MySQL을 시작합니다.

```bash
docker start mysql-1
```

DBeaver의 SQL 편집기에서 다음을 한 번 실행합니다. 기존 데이터베이스나 컨테이너를 삭제하지 않습니다.

```sql
CREATE DATABASE IF NOT EXISTS surl_dev CHARACTER SET utf8mb4;
```

### 2. 프로젝트 실행

저장소를 내려받은 후 `J2-week05`를 IntelliJ에서 Gradle 프로젝트로 엽니다. Lombok 관련 오류가 표시되면 Gradle 동기화와 Annotation Processing 설정을 확인합니다.

터미널에서는 다음과 같이 실행합니다. `DB_PASSWORD`에는 본인의 MySQL 비밀번호를 입력합니다.

```bash
cd J2-week05
read -s DB_PASSWORD
export DB_PASSWORD
./gradlew bootRun
```

Mac에서 `permission denied`가 나오면 `chmod +x gradlew`를 먼저 실행합니다. Windows에서는 `gradlew.bat`를 사용하고 실행 환경에 `DB_PASSWORD`를 설정합니다.

샘플 회원 2명과 게시글 4개가 필요한 경우에만 개발 프로파일로 실행합니다.

```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```

새로 생성되는 샘플 계정은 `user1`, `user2`이며 비밀번호는 `password123`입니다. 게시물이 이미 있으면 샘플 게시물을 다시 추가하지 않습니다. 기본 프로파일은 샘플 계정을 생성하지 않습니다.

### 3. 회원가입과 로그인

```bash
curl -X POST http://localhost:8080/api/members \
  -H 'Content-Type: application/json' \
  -d '{"username":"younghwan","password":"password123","nickname":"영환"}'

curl -c cookies.txt -X POST http://localhost:8080/api/members/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"younghwan","password":"password123"}'
```

### 4. 게시물과 URL 등록

```bash
curl -b cookies.txt -X POST http://localhost:8080/api/articles \
  -H 'Content-Type: application/json' \
  -d '{"title":"JPA 학습","body":"영속성과 CRUD를 배웠습니다."}'

curl -b cookies.txt -X POST http://localhost:8080/api/surls \
  -H 'Content-Type: application/json' \
  -d '{"body":"구글","url":"https://www.google.com"}'
```

URL 생성 응답의 `id`를 사용하여 `http://localhost:8080/g/{id}`에 접근하면 원래 주소로 이동합니다.

### 5. 영속성 확인

1. `/api/surls/{id}`에서 URL과 조회수를 확인합니다.
2. Spring Boot만 종료하고 다시 실행합니다.
3. 같은 ID로 조회하여 URL과 조회수가 유지되는지 확인합니다.
4. `sql/check-data.sql`로 MySQL 테이블의 값도 확인합니다.

테스트 DB인 H2 인메모리는 일반적인 실행용 MySQL과 다릅니다. 재시작 테스트에서는 별도의 파일 기반 H2를 사용합니다. MySQL 저장소를 삭제하거나 `ddl-auto: create`로 변경하면 데이터 유지 조건이 달라집니다.

## API 목록

| 메서드 | 주소 | 기능 | 로그인 |
| --- | --- | --- | --- |
| POST | `/api/members` | 회원가입 | 불필요 |
| POST | `/api/members/login` | 로그인 | 불필요 |
| POST | `/api/members/logout` | 로그아웃 | 불필요 |
| GET | `/api/members/me` | 내 정보 | 필요 |
| POST | `/api/articles` | 게시글 등록 | 필요 |
| GET | `/api/articles?keyword=JPA` | 제목 검색·목록 | 불필요 |
| GET | `/api/articles/count` | 게시글 수 | 불필요 |
| GET | `/api/articles/by-ids?ids=1,2` | ID 목록 검색·정렬 | 불필요 |
| GET | `/api/articles/exact?title=제목&body=내용` | 제목·내용 일치 검색 | 불필요 |
| GET | `/api/articles/{id}` | 게시글 조회 | 불필요 |
| PUT | `/api/articles/{id}` | 게시글 수정 | 작성자 |
| DELETE | `/api/articles/{id}` | 게시글 삭제 | 작성자 |
| POST | `/api/surls` | URL 등록 | 필요 |
| GET | `/api/surls` | URL 목록 | 불필요 |
| GET | `/api/surls/{id}` | URL·조회수 확인 | 불필요 |
| GET | `/g/{id}` | 조회수 증가·원래 URL 이동 | 불필요 |

## 테스트

```bash
./gradlew test
```

테스트는 MySQL이나 실제 비밀번호 없이 H2로 실행합니다. Java 17 환경에서 테스트 7개가 모두 통과했으며, `bootJar` 빌드도 확인했습니다. MySQL 실제 접속은 위 실행 절차로 별도 확인할 수 있습니다.

- 회원가입 중복·비밀번호 해시·로그인·로그아웃
- 게시글 CRUD·조건 조회·작성자 권한
- URL 생성·리다이렉트·조회수·잘못된 URL 거부
- 런타임 예외 발생 시 회원과 게시글 전체 롤백
- 더티 체킹·날짜 자동 기록·프록시 초기화 시점
- 애플리케이션 종료 후 재시작하여 URL·조회수 유지

이 프로젝트는 로컬 학습용입니다. 운영 배포에는 CSRF 보호, 세션·쿠키 보안, 페이지네이션, 스키마 마이그레이션과 의존성 업데이트를 별도로 적용해야 합니다. SQL 바인딩 로그는 개발 프로파일에서만 켭니다.

## 느낀점

SQL로 직접 다뤘던 CRUD를 JPA의 엔티티와 리포지터리로 표현하는 방법을 학습했습니다.
특히 트랜잭션 안에서 객체를 변경하면 DB에 반영되는 더티 체킹과, 회원·게시글을 외래키 관계로 연결하는 구조를 이해했습니다.
URL 정보를 DB에 저장하는 흐름을 통해 메모리 저장과 영속성의 차이를 정리할 수 있었습니다.

## 참고 자료

- 멋쟁이사자처럼 「Spring Boot」 챕터 06: 서비스에 Spring Data JPA로 영속성 부여, 스텝 01~20
- [강의 참고 저장소](https://github.com/jhs512/demo03-2024)
- [Spring의 트랜잭션 어노테이션과 프록시](https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/annotations.html)
- [Spring Data JPA Auditing](https://docs.spring.io/spring-data/jpa/reference/auditing.html)
