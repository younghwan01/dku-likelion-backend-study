# 5주차 - Spring Data JPA로 영속성 부여

이번 주차에서는 **Spring Data JPA를 사용해 기존 URL 단축 서비스의 데이터를 MySQL에 저장하는 방법을 학습했습니다.**
메모리에서 관리하던 데이터를 DB에 저장하고, 애플리케이션을 재시작해도 데이터가 유지되는 구조를 배웠습니다.

멋쟁이사자처럼 Spring Boot 강의 **챕터 06, 스텝 06-01~06-20**을 기준으로 정리했습니다. 실습 코드는 강의 자료에 연결된 [06-20 최종 소스](https://github.com/jhs512/demo03-2024/tree/4211d41)의 JPA 관련 코드를 참고했습니다.

## 핵심 학습 내용

### 1. 영속성과 JPA

기존에는 Controller의 `ArrayList`에 URL을 저장했기 때문에 프로그램이 종료되면 데이터가 사라졌습니다.
이번에는 JPA를 통해 MySQL에 데이터를 저장하는 방법을 배웠습니다.

- **영속성**: 프로그램이 종료된 후에도 데이터가 유지되는 성질
- **ORM**: 자바 객체와 DB 테이블을 매핑하는 기술
- **JPA**: 자바 ORM의 표준 명세
- **Hibernate**: JPA를 구현하는 라이브러리
- **Spring Data JPA**: Repository를 통해 JPA를 쉽게 사용하는 기능

### 2. 의존성과 MySQL 연결

`build.gradle`에 JPA와 MySQL 드라이버를 추가하는 방법을 학습했습니다.

```groovy
implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
runtimeOnly 'com.mysql:mysql-connector-j'
```

`application.yml`에서는 DB 주소, 사용자명, 비밀번호와 드라이버를 설정합니다.
현재 실습 DB는 `surl_dev`이며, MySQL 비밀번호는 `DB_PASSWORD` 환경변수로 전달합니다.

### 3. Entity와 테이블 생성

`Article`을 엔티티로 등록하고, 필드를 DB 컬럼과 연결하는 방법을 배웠습니다.

```java
@Entity
public class Article extends BaseTime {
    private String title;

    @Column(columnDefinition = "TEXT")
    private String body;

    @ManyToOne
    private Member author;
}
```

| 설정 | 역할 |
| --- | --- |
| `@Entity` | JPA가 관리할 엔티티 지정 |
| `@Id` | 기본키 지정 |
| `@GeneratedValue(strategy = IDENTITY)` | MySQL에서 자동 증가 ID 사용 |
| `@Column(columnDefinition = "TEXT")` | 긴 내용을 TEXT 컬럼에 저장 |
| `ddl-auto: update` | 엔티티 매핑에 맞춰 테이블 생성·갱신 |

ID는 공통 부모인 `BaseEntity`에서 관리합니다. `Long`을 사용하면 아직 ID를 부여받지 않은 상태를 `null`로 표현할 수 있습니다.
엔티티 필드를 삭제해도 `ddl-auto: update`가 기존 컬럼을 자동으로 삭제해주는 것은 아니라는 점도 배웠습니다.

### 4. Repository를 이용한 CRUD

```java
public interface ArticleRepository extends JpaRepository<Article, Long> {
}
```

Repository에 엔티티와 ID 타입을 지정하면 기본 저장·조회·삭제 메서드를 사용할 수 있습니다.

| 메서드 | 학습 내용 |
| --- | --- |
| `save()` | 엔티티 저장 |
| `findById()` | ID로 한 건 조회, Optional 반환 |
| `findAll()` | 전체 조회, List 반환 |
| `count()` | 데이터 개수 조회 |
| `delete()` | 데이터 삭제 |

`Optional`은 값이 없거나 하나인 결과를 표현하며, `List`는 여러 결과를 담는다는 차이를 배웠습니다.
샘플 데이터가 재실행 때마다 추가되지 않도록 게시물 개수를 확인하는 방법도 학습했습니다.

06-10에서는 메서드 이름으로 조건 조회를 만드는 규칙을 배웠습니다. 최종 `ArticleRepository`는 강의 최종 소스처럼 기본 메서드를 사용하며, `MemberRepository`에는 다음 조건 조회가 있습니다.

```java
Optional<Member> findByUsername(String username);
```

### 5. 트랜잭션과 더티 체킹

`@Transactional`을 사용해 여러 DB 작업을 하나의 작업 단위로 묶는 방법을 배웠습니다.

- 서비스 클래스에는 `@Transactional(readOnly = true)`를 적용합니다.
- 저장·삭제·변경 메서드에는 일반 `@Transactional`을 적용합니다.
- 관리 중인 엔티티를 쓰기 트랜잭션에서 변경하면 더티 체킹으로 UPDATE가 실행됩니다.
- 기본 설정에서 런타임 예외가 트랜잭션 경계 밖으로 전달되면 롤백됩니다.

현재 코드에서는 URL 조회수 증가에 변경 감지를 사용합니다.

```java
@Transactional
public void increaseCount(Surl surl) {
    surl.increaseCount();
}
```

객체의 값을 변경했다고 모든 객체가 자동으로 저장되는 것은 아닙니다. JPA가 관리하는 엔티티를 트랜잭션 안에서 변경해야 합니다.
현재 URL 요청 흐름은 강의처럼 기본 Open EntityManager in View 설정을 사용합니다.

같은 객체의 `this`로 메서드를 호출하면 기본 프록시 방식의 트랜잭션 설정을 적용하지 못한다는 점도 배웠습니다.
`NotProd`에서는 `@Lazy`, `@Autowired`로 얻은 `self`를 통해 초기 데이터 생성 메서드를 호출합니다.

### 6. Service 계층과 결과 반환

Controller에서 직접 DB를 다루는 대신 Service와 Repository의 역할을 나누는 방법을 학습했습니다.

- **Controller**: 요청 처리
- **Service**: 객체 생성, 중복 검사, 작성자 연결, 조회수 변경
- **Repository**: 데이터 저장과 조회

`RsData<T>`로 결과 코드, 상태 코드, 메시지와 데이터를 함께 반환하는 방법을 배웠습니다.
회원가입 시 이미 존재하는 아이디는 `GlobalException`을 발생시켜 처리 흐름을 중단합니다.

현재 코드는 강의 최종 소스의 예외 클래스를 사용합니다. `RsData.statusCode`나 예외에 담긴 코드는 실제 HTTP 응답 상태를 자동으로 바꾸는 설정이 아닙니다.

### 7. 날짜 자동 기록과 공통 필드

`@CreatedDate`, `@LastModifiedDate`로 생성일과 수정일을 자동으로 기록하는 방법을 학습했습니다.

| 클래스 | 공통 필드 |
| --- | --- |
| `BaseEntity` | ID |
| `BaseTime` | 생성일·수정일, BaseEntity 상속 |
| `Article`, `Member`, `Surl` | BaseTime 상속 |

`@EnableJpaAuditing`과 `AuditingEntityListener`를 함께 사용합니다.
`@MappedSuperclass`는 자식 엔티티에 매핑을 상속하며, 부모 자체의 테이블을 만드는 설정은 아닙니다.

### 8. 작성자 관계와 프록시

한 회원이 여러 게시물과 URL을 작성할 수 있어 `@ManyToOne`으로 작성자를 연결했습니다.
자바에서는 `Member author` 객체로 표현하고, DB에는 `author_id`가 저장됩니다.

`Rq`는 요청마다 사용할 회원 참조를 제공합니다.

```java
public Member getMember() {
    return memberService.getReferenceById(1L);
}
```

강의 실습처럼 **1번 회원을 현재 회원이라고 가정합니다.** 실제 로그인 기능을 구현한 코드는 아닙니다.
`getReferenceById()`로 프록시를 얻고, ID 접근과 username 접근 사이의 SQL 실행 시점을 비교하는 실습 코드가 `/add`에 있습니다.

`Surl.author`에는 `@JsonIgnore`를 적용하여 JSON 응답에서 작성자를 제외했습니다. DB의 작성자 관계는 그대로 유지됩니다.

## 실습 코드 구성

| 경로 | 내용 |
| --- | --- |
| `domain/article/article` | Article 엔티티·Repository·Service |
| `domain/member/member` | Member 엔티티·Repository·Service |
| `domain/surl/surl` | Surl 엔티티·Repository·Service·Controller |
| `global/initData/NotProd.java` | 샘플 회원 2명·게시물 4개 생성 |
| `global/jpa/entity` | 공통 ID·날짜 매핑 |
| `global/rsData` | 처리 결과 반환 |
| `global/exceptions` | 예외 클래스 |
| `global/rq` | 요청에서 사용할 회원 참조 |

`NotProd.work1()`은 게시물이 이미 있으면 초기 데이터 생성을 건너뜁니다. `work2()`는 강의 최종 코드처럼 비워두었습니다.

## 실행 방법

Java 21과 MySQL을 사용합니다. Docker Desktop을 실행한 뒤 기존 MySQL 컨테이너를 시작합니다.

```bash
docker start mysql-1
```

DBeaver에서 실습용 DB를 생성합니다.

```sql
CREATE DATABASE IF NOT EXISTS surl_dev;
```

MySQL 비밀번호를 입력하고 실행합니다.

```bash
cd J2-week05
read -s DB_PASSWORD
export DB_PASSWORD
./gradlew bootRun
```

IntelliJ로 실행할 경우 Run Configuration의 환경변수에 `DB_PASSWORD`를 설정합니다.
서버 포트는 강의 최종 설정과 같은 `8070`입니다.

| 요청 | 기능 |
| --- | --- |
| `/add?body=구글&url=https://www.google.com` | URL 저장 |
| `/s/{body}/**` | 경로에 포함된 원본 URL 저장 |
| `/all` | 저장된 URL 목록 조회 |
| `/g/{id}` | 조회수 증가 후 원래 URL로 이동 |

`http://localhost:8070/add?body=구글&url=https://www.google.com`으로 URL을 추가한 뒤 `/all`에서 ID를 확인합니다.
해당 ID의 `/g/{id}`에 접근하고, 서버를 다시 실행한 후 `/all`에서 URL과 조회수가 유지되는지 확인할 수 있습니다.

현재 저장소에는 강의의 기본 `contextLoads` 테스트만 포함되어 있습니다. 이 테스트를 일반적으로 실행하려면 MySQL 연결 설정이 필요합니다.

## 느낀점

SQL로 직접 다뤘던 데이터를 엔티티와 Repository를 통해 저장하고 조회하는 방법을 배웠습니다.
특히 트랜잭션과 더티 체킹, 작성자 관계를 학습하면서 자바 객체와 DB가 연결되는 흐름을 이해할 수 있었습니다.
메모리로 관리하던 URL 정보를 DB에 저장하는 과정을 통해 영속성이 필요한 이유를 정리했습니다.
