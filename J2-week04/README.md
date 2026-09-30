# 4주차 - MySQL과 데이터베이스 기초

이번 주차에서는 **MySQL과 DBeaver를 활용하여 데이터베이스의 기본 개념과 SQL 사용법을 학습했습니다.**  
데이터를 단순히 저장하는 것뿐 아니라, 테이블 구조를 설계하고 제약조건을 적용하는 방법과 여러 테이블의 데이터를 연결하는 방법까지 배웠습니다.

## 핵심 학습 내용

### 1. 데이터베이스와 테이블 구조

데이터베이스와 DBMS의 기본 개념을 학습했습니다.

- **Database**: 데이터를 저장하고 관리하는 공간
- **DBMS(Database Management System)**: 여러 데이터베이스를 관리하는 시스템

### 2. 기본 SQL 명령어

데이터베이스와 테이블을 생성하고, 데이터를 추가·조회·수정·삭제하는 기본 SQL 명령어를 학습했습니다.

- `CREATE`: 데이터베이스나 테이블 생성
- `INSERT`: 데이터 추가
- `SELECT`: 데이터 조회
- `UPDATE`: 데이터 수정
- `DELETE`: 데이터 삭제
- `DROP`: 데이터베이스나 테이블 삭제

데이터베이스 생성:

```sql
CREATE DATABASE a1;
```

테이블 생성:

```sql
CREATE TABLE article (
    title VARCHAR(100),
    body TEXT
);
```

데이터 추가:

```sql
INSERT INTO article
SET title = '제목',
    body = '내용';
```

데이터 조회:

```sql
SELECT *
FROM article;
```

데이터 수정:

```sql
UPDATE article
SET title = '수정된 제목'
WHERE id = 1;
```

데이터 삭제:

```sql
DELETE FROM article
WHERE id = 2;
```

이러한 명령어를 직접 사용하면서 데이터베이스에서 데이터를 생성하고 관리하는 기본적인 흐름을 배웠습니다.

### 3. 테이블 구조 변경

`ALTER TABLE`을 사용하여 기존 테이블의 구조를 변경하는 방법을 학습했습니다.

처음에는 게시글의 `title`, `body`만 저장했지만, 각 게시글을 구분하고 작성 시간을 기록하기 위해 다음 컬럼을 추가했습니다.

- `id`: 게시글을 구분하기 위한 번호
- `regDate`: 게시글 작성 시간

예시:

```sql
ALTER TABLE article
ADD COLUMN id INT FIRST;
```

```sql
ALTER TABLE article
ADD COLUMN regDate DATETIME AFTER id;
```

또한 `DESC article;`을 사용하여 컬럼명, 자료형, NULL 허용 여부, Key 등 **테이블의 구조를 확인하는 방법**을 배웠습니다.

### 4. 제약조건

데이터의 무결성을 유지하기 위한 여러 제약조건을 학습했습니다.

- `NOT NULL`: NULL 값 허용하지 않음
- `PRIMARY KEY`: 각 행을 고유하게 식별
- `AUTO_INCREMENT`: id 값을 자동으로 증가
- `UNSIGNED`: 음수 값을 사용하지 않음
- `UNIQUE`: 중복 값 허용하지 않음

기존 데이터에 NULL이나 중복 값이 존재하면 제약조건을 바로 적용할 수 없다는 점도 배웠습니다.  
따라서 **기존 데이터를 먼저 정리한 뒤 제약조건을 적용해야 한다는 점**을 학습했습니다.

### 5. 조건을 활용한 데이터 조회

원하는 데이터만 조회하기 위해 다양한 조건문과 정렬 방법을 학습했습니다.

- `WHERE`: 조건에 맞는 데이터 조회
- `LIKE`: 문자열 패턴 검색
- `AND`: 여러 조건을 모두 만족
- `OR`: 여러 조건 중 하나 이상 만족
- `ORDER BY`: 조회 결과 정렬
- `LIMIT`: 조회할 데이터 개수 제한

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

이를 통해 전체 데이터를 조회하는 것뿐 아니라, 필요한 데이터만 필터링하고 정렬하는 방법을 배웠습니다.

### 6. 테이블 설계와 관계

같은 정보를 여러 테이블에 중복해서 저장했을 때 발생하는 문제를 학습했습니다.

처음에는 사원 테이블에 부서명을 직접 저장했습니다.

```text
홍길동 | 홍보
홍길순 | 홍보
임꺽정 | 기획
```

이 구조에서는 부서명 `홍보`를 `마케팅`으로 변경할 경우 부서 테이블과 사원 테이블을 모두 수정해야 했습니다.

이를 해결하기 위해 사원 테이블에는 부서명 대신 **부서 번호(deptId)** 를 저장하도록 구조를 변경하는 방법을 배웠습니다.

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

이를 통해 같은 정보를 반복해서 저장하는 것보다, 각 테이블의 역할을 나누고 id를 통해 관계를 연결하는 방식이 더 효율적이라는 점을 배웠습니다.

### 7. INNER JOIN

`INNER JOIN`을 사용하여 서로 다른 두 테이블에서 **관계가 있는 데이터만 연결해서 조회하는 방법**을 학습했습니다.

이번 실습에서는 사원 정보와 부서 정보를 하나의 결과로 조회하는 방법을 배웠습니다.

```sql
SELECT E.id AS `사원번호`,
       E.name AS `사원명`,
       DATE(E.regDate) AS `입사일`,
       D.name AS `부서명`
FROM emp AS E
INNER JOIN dept AS D
ON E.deptId = D.id;
```

또한 `AS`를 사용하여 테이블명과 컬럼명에 별칭을 지정해 조회 결과를 더 보기 쉽게 표현하는 방법도 학습했습니다.

## 실습 파일

| 파일 | 학습 내용 |
| --- | --- |
| [article-crud.sql](./article-crud.sql) | 데이터베이스 / 테이블 생성, 게시글 CRUD, id와 작성일 추가 |
| [constraints.sql](./constraints.sql) | NOT NULL, PRIMARY KEY, AUTO_INCREMENT, 컬럼 변경, 조건 조회 |
| [inner-join.sql](./inner-join.sql) | dept / emp 테이블 관계 설계, deptId 적용, INNER JOIN |

## 느낀점

데이터베이스를 처음 배워 생소했지만, CRUD와 JOIN을 직접 실습하면서 데이터가 저장되고 관리되는 기본적인 흐름을 이해할 수 있었습니다.  
특히 테이블 구조를 어떻게 설계하는지가 중요하다는 점을 배웠습니다.
