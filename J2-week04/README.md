# J2-week04 - MySQL / Database 기초

이번 주차에서는 **데이터베이스를 처음 접하면서 MySQL과 DBeaver를 이용해 기본적인 SQL 사용법을 학습**했다.  
단순히 데이터를 저장하는 것뿐 아니라, 테이블 구조를 어떻게 설계하고 여러 테이블의 데이터를 어떻게 연결하는지도 함께 실습했다.

## 학습 환경

- MySQL
- DBeaver
- Docker

## 핵심 학습 내용

### 1. Database / DBMS / Table 구조

데이터베이스는 데이터를 저장하는 공간이고, MySQL은 여러 데이터베이스를 관리하는 **DBMS(Database Management System)** 이다.

테이블은 다음과 같이 구성된다.

- **Column**: 데이터의 속성
- **Row**: 실제 데이터 한 건

예를 들어 사원 테이블이라면 이름, 부서, 입사일 등이 컬럼이고 사원 한 명의 정보가 하나의 로우가 된다.

### 2. DDL과 DML

SQL은 크게 구조를 다루는 명령과 데이터를 다루는 명령으로 나눌 수 있다.

**DDL**

- `CREATE`: 데이터베이스 / 테이블 생성
- `ALTER`: 테이블 구조 변경
- `DROP`: 데이터베이스 / 테이블 / 컬럼 삭제

**DML**

- `INSERT`: 데이터 추가
- `SELECT`: 데이터 조회
- `UPDATE`: 데이터 수정
- `DELETE`: 데이터 삭제

이번 실습에서는 게시글 테이블을 직접 만들고 CRUD를 반복하면서 SQL의 기본 흐름을 익혔다.

### 3. 테이블 구조 변경

처음에는 게시글을 `title`, `body`만 저장했지만 같은 내용의 게시글을 구분하기 어려웠다.

그래서 `ALTER TABLE`을 이용해 다음 컬럼을 추가했다.

- `id`: 각 게시글을 구분하기 위한 번호
- `regDate`: 작성 시간

`DESC article;`을 통해 컬럼명, 자료형, NULL 허용 여부, Key 등 **테이블 구조**를 확인할 수 있다는 것도 배웠다.

### 4. 제약조건

잘못된 데이터가 들어가는 것을 막기 위해 여러 제약조건을 적용했다.

- `NOT NULL`: NULL 값 금지
- `PRIMARY KEY`: 각 행을 고유하게 식별
- `AUTO_INCREMENT`: id 자동 증가
- `UNSIGNED`: 음수 값 사용하지 않음
- `UNIQUE`: 중복 값 방지

특히 기존 데이터에 NULL이나 중복 값이 있으면 제약조건을 바로 적용할 수 없어서, **기존 데이터를 먼저 정리한 뒤 제약조건을 적용해야 한다는 점**을 확인했다.

### 5. 조건 조회

`WHERE`, `LIKE`, `AND`, `OR`, `ORDER BY`, `LIMIT`을 사용해 원하는 데이터만 조회했다.

예시:

```sql
SELECT *
FROM article
WHERE nickname LIKE '홍길%';
```

```sql
SELECT *
FROM article
ORDER BY hit DESC
LIMIT 3;
```

이를 통해 단순 전체 조회뿐 아니라 조건에 맞는 데이터만 필터링하고 정렬하는 방법을 익혔다.

### 6. 테이블 설계와 관계

처음에는 사원 테이블에 부서명을 직접 저장했다.

```text
홍길동 | 홍보
홍길순 | 홍보
임꺽정 | 기획
```

하지만 부서명 `홍보`를 `마케팅`으로 바꾸려면 부서 테이블과 사원 테이블을 모두 수정해야 했다.

그래서 사원 테이블에는 부서 이름 대신 **부서 번호(deptId)** 를 저장하도록 구조를 변경했다.

```text
dept
id | name
1  | 마케팅
2  | 기획

emp
id | name   | deptId
1  | 홍길동 | 1
2  | 홍길순 | 1
3  | 임꺽정 | 2
```

이렇게 하면 부서 이름이 변경되어도 사원 테이블의 `deptId`는 그대로 유지할 수 있다.

### 7. INNER JOIN

사원 정보와 부서명을 한 번에 보기 위해 `INNER JOIN`을 사용했다.

```sql
SELECT E.id AS `사원번호`,
       E.name AS `사원명`,
       DATE(E.regDate) AS `입사일`,
       D.name AS `부서명`
FROM emp AS E
INNER JOIN dept AS D
```

여기서 핵심은


이다.

`ON` 조건을 통해 서로 관계가 있는 행끼리 연결해야 올바른 결과를 얻을 수 있다는 점을 배웠다.

## 실습 파일

| 파일 | 내용 |
| --- | --- |
| [article-crud.sql](./article-crud.sql) | 데이터베이스 / 테이블 생성, 게시글 CRUD, id와 작성일 추가 |
| [constraints.sql](./constraints.sql) | NOT NULL, PRIMARY KEY, AUTO_INCREMENT, 컬럼 변경, 조건 조회 |
| [inner-join.sql](./inner-join.sql) | dept / emp 테이블 설계 변경, deptId 적용, INNER JOIN |

## 느낀점

데이터베이스를 처음 배워서 처음에는 테이블, 로우, 컬럼 같은 용어부터 익숙하지 않았다.  
하지만 직접 게시글 데이터를 추가하고 수정해 보고, 부서와 사원 테이블을 나눠 JOIN까지 해보면서 **백엔드에서 데이터를 어떻게 저장하고 관리하는지 흐름을 조금씩 이해할 수 있었다.**
