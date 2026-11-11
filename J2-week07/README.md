# J2-week07 - Spring Boot 회원가입 API 구현 및 예외 처리

## 학습 개요

이번 주에는 **챕터 08(08-01 ~ 08-16)** 강의를 통해 Spring Boot에서 회원가입 기능을 REST API 형태로 구성하는 방법을 학습했다. 회원가입 요청을 처리하는 것부터 HTTP 메서드, JSON 응답, Validation, 예외 처리와 응답 형식 통일까지 정리했다.

- **학습 목표:** REST API 방식으로 회원가입 API를 구현하는 흐름 이해
- **진행 방식:** 강의 내용을 중심으로 개념과 코드 작성 방법을 정리
- **실습 여부:** 이 저장소에는 이번 주 실제로 작성·실행한 회원가입 프로젝트나 Postman 테스트 결과가 포함되어 있지 않다. 아래 코드는 개념 설명을 위한 간단한 **예시**이며, 실행 및 동작을 검증했다고 의미하지 않는다.

## 1. 회원가입의 기본 흐름 (08-01 ~ 08-02)

회원가입은 사용자가 입력한 정보를 검사한 뒤 `member` 테이블에 새로운 회원 데이터를 저장하는 과정이다.

```text
회원가입 요청
  → username / password / nickname 확인
  → 누락 여부 및 기존 아이디 중복 검사
  → 회원 정보 저장
  → 처리 결과 응답
```

처음에는 `MemberController`에서 회원가입 기능을 작성하며, 이 단계는 아직 REST API 형태가 아니다. 사용자명, 비밀번호, 닉네임은 필요한 값이므로 비어 있거나 누락되었을 때 처리 방법을 정해야 한다.

## 2. 공통 문자열 검사와 예외 처리 (08-03 ~ 08-05)

### Ut 유틸리티 클래스

`str == null || str.isBlank()`처럼 여러 곳에서 반복하는 문자열 검사는 공통 메서드로 분리할 수 있다. `Ut` 클래스 안에 기능별 `static` 중첩 클래스를 두는 구조와 `isBlank`, `hasLength` 메서드의 역할을 배웠다.

### return과 throw의 차이

- `return`: 현재 메서드를 종료하고 선언된 반환 타입에 맞는 값을 돌려준다.
- `throw`: 예외를 발생시켜 일반적인 실행 흐름을 중단한다. 예외를 처리하지 않으면 상위 호출 흐름으로 전달된다.
- 회원가입 중 아이디가 중복되는 것처럼 정상 결과와 구분해야 하는 경우에는 예외를 사용하면 오류 처리 코드를 분리하기 쉽다.

Spring Boot에서는 `@ControllerAdvice`와 `@ExceptionHandler`를 사용해 여러 컨트롤러의 예외를 한곳에서 처리할 수 있다. 예외 종류에 맞는 핸들러가 선택되고, 적절한 핸들러가 없으면 기본 오류 처리 방식이 적용된다.

## 3. REST API와 HTTP 메서드 (08-06 ~ 08-09)

Spring Boot는 HTML을 만들어 사람에게 보여줄 수도 있고, 별도의 프론트엔드 프로그램에 JSON 데이터를 제공할 수도 있다.

- **서버에서 화면까지 처리:** Thymeleaf나 JSP 등을 사용해 HTML을 응답
- **프론트엔드와 분리:** Spring Boot는 데이터와 비즈니스 로직을 처리하고 React, Vue 등에서 화면 구성
- **REST API 개발:** 자원을 나타내는 URL과 HTTP 메서드를 활용해 요청과 응답을 설계

| 메서드 | 기본 용도 | 예시 |
| --- | --- | --- |
| GET | 조회 | `GET /articles` |
| POST | 생성 | `POST /articles` |
| PUT | 전체 수정 | `PUT /articles/1` |
| PATCH | 부분 수정 | `PATCH /articles/1` |
| DELETE | 삭제 | `DELETE /articles/1` |

URL을 `/article/write` 같은 동사형으로 만드는 대신 `/articles`처럼 자원 중심으로 설계하는 방법을 배웠다.

### 회원가입 API 구성

- `@RestController`: 반환값을 주로 HTTP 응답 본문(JSON 등)으로 보낼 때 사용. `@ResponseBody`를 메서드마다 붙이지 않아도 됨
- `@PostMapping`: POST 요청 처리
- `@RequestBody`: 요청 본문에 담긴 JSON을 자바 객체로 변환해 받기
- `ApiV1MemberController`: API 버전(v1)을 경로로 구분하는 컨트롤러 구조

다음은 요청 본문을 받는 방식을 보여주는 **개념 예시**다. 실제 강의 소스를 그대로 옮긴 코드는 아니다.

```java
record MemberJoinReqBody(
    String username,
    String password,
    String nickname
) {}

@PostMapping("/join")
public Object join(@RequestBody MemberJoinReqBody reqBody) {
    // 입력값 확인 → 중복 검사 → 회원 저장 → 결과 반환
    return null; // 실제 동작 구현 전의 구조 예시
}
```

## 4. Postman과 요청 테스트 (08-07)

브라우저 주소창으로는 주로 GET 요청을 보내기 때문에 POST, PATCH, DELETE 등의 API를 확인할 때는 **Postman**을 사용할 수 있다. Postman에서는 HTTP 메서드와 헤더를 지정하고 JSON 요청 본문을 직접 넣어 요청할 수 있다.

회원가입 요청 본문의 형태는 다음과 같다.

```http
POST /api/v1/members/join
Content-Type: application/json

{
  "username": "user10",
  "password": "example-password",
  "nickname": "회원10"
}
```

위 경로와 값은 설명을 위한 예시이며 **실제로 Postman에서 호출해 확인한 결과가 아니다.** 테스트할 때는 정상 가입, 필수 입력 누락, 이미 존재하는 아이디 등을 나눠 검증할 수 있다.

## 5. HTTP 상태 코드와 공통 응답 형식 (08-10 ~ 08-11)

HTTP 응답 상태 코드를 통해 요청 처리 결과를 구분한다.

| 상태 코드 | 의미 |
| --- | --- |
| 2xx | 요청 처리 성공 |
| 400 | 잘못된 요청(입력값 오류 등) |
| 404 | 요청한 자원을 찾을 수 없음 |
| 5xx | 서버 내부 오류 |

`@ResponseStatus(HttpStatus.BAD_REQUEST)`는 400 상태를 지정하며, `ResponseEntity`를 사용하면 상황별로 상태 코드를 다르게 지정할 수 있다.

강의에서는 성공과 실패 응답에 같은 필드를 사용하는 방식을 학습했다.

**성공 응답 예시**

```json
{
  "resultCode": "200-1",
  "statusCode": 200,
  "msg": "회원가입이 완료되었습니다.",
  "data": {}
}
```

**실패 응답 예시**

```json
{
  "resultCode": "400-1",
  "statusCode": 400,
  "msg": "이미 존재하는 아이디입니다.",
  "data": {}
}
```

Jackson은 자바 객체를 JSON으로 직렬화하는 데 사용한다. `spring.jackson.serialization.fail-on-empty-beans: false` 설정은 빈 객체 직렬화 오류를 피할 때 강의에서 소개한 옵션이다.

## 6. Validation을 이용한 입력값 검증 (08-12 ~ 08-13)

Controller에서 `if`문으로 입력값을 일일이 검사하는 대신 Validation을 사용해 필드에 검증 규칙을 선언할 수 있다.

```groovy
implementation 'org.springframework.boot:spring-boot-starter-validation'
```

```java
record MemberJoinReqBody(
    @NotBlank String username,
    @NotBlank String password,
    @NotBlank String nickname
) {}
```

요청 객체에 `@Valid`를 사용하면 검증을 실행한다. 조건에 맞지 않으면 `MethodArgumentNotValidException`이 발생할 수 있고, 이에 대한 `@ExceptionHandler`를 만들어 공통 오류 형식으로 변환할 수 있다.

- `@NotBlank`: `null`, 빈 문자열, 공백만 있는 문자열을 허용하지 않음
- `@NotNull`: `null`을 허용하지 않음
- `@Min`: 숫자 최솟값 제한
- `@Valid`: 요청 객체에 선언한 검증 규칙 적용

## 7. 기타 예외 처리와 RsData (08-14 ~ 08-16)

### 예상하지 못한 예외

특정 예외 외에 기타 예외에 대한 핸들러도 두면 기본 오류 응답과 다른 형식이 나오는 것을 방지할 수 있다. 강의에서 소개한 `Rq`는 요청(`HttpServletRequest`)과 응답(`HttpServletResponse`)에 접근하는 공통 기능을 추상화한 객체다.

### 응답 데이터 통일

회원가입 API는 요청 본문 클래스뿐 아니라 응답 본문 클래스(`MemberJoinRespBody`)도 사용할 수 있다. `RsData<T>`는 결과 코드, HTTP 상태 코드에 해당하는 값, 메시지, 데이터를 묶어 전달하는 구조다.

```text
RsData<MemberJoinRespBody>
 ├─ resultCode
 ├─ statusCode
 ├─ msg
 └─ data (회원가입 응답 데이터)
```

이때 객체 안에 담긴 `statusCode` 값만으로 실제 HTTP 응답 코드가 자동 변경되는 것은 아니다. 마지막 08-16 스텝에서는 **Aspect(AOP)**를 통해 일정한 패턴의 메서드 실행 전후에 공통 처리를 추가하고, `RsData`의 상태 코드를 HTTP 응답에 반영하는 방법을 소개했다.

## 정리 및 느낀 점

이번 강의를 통해 회원가입은 단순히 회원 정보를 DB에 저장하는 작업만이 아니라, **입력값 검증 → 예외 처리 → 일관된 응답 반환**까지 함께 고려해야 한다는 것을 배웠다. 특히 `@Valid`로 검증을 분리하고 `@ExceptionHandler`로 오류 응답을 통일하는 방식이 인상적이었다.

이전에는 Controller가 요청을 받아 결과를 반환하는 부분에 집중했다면, 이번에는 API를 사용하는 프론트엔드가 성공과 실패를 구분하기 쉽게 만드는 과정도 중요하다는 것을 이해했다. 이후 실제 회원가입 API를 작성할 때 Postman으로 정상 요청과 오류 상황을 직접 확인해 보고 싶다.
