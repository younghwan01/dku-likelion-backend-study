# J2-week04 - MySQL 기초 / CRUD / 제약조건 / INNER JOIN

이번 주차에서는 **MySQL + DBeaver**를 사용해 데이터베이스의 기본 구조를 익히고,
게시물 데이터를 직접 생성·조회·수정·삭제한 뒤 제약조건과 JOIN까지 실습했다.

## 학습 내용

- Database / Table 생성 및 조회
- CRUD: `INSERT`, `SELECT`, `UPDATE`, `DELETE`
- 테이블 구조 확인: `DESC`
- 컬럼 추가·수정·삭제: `ALTER TABLE`
- 제약조건: `NOT NULL`, `PRIMARY KEY`, `AUTO_INCREMENT`, `UNSIGNED`
- 조건 조회: `WHERE`, `LIKE`, `AND`, `OR`
- 정렬 및 개수 제한: `ORDER BY`, `LIMIT`
- 여러 테이블 연결: `INNER JOIN ... ON`
- 별칭: `AS`

## 실습 파일

| 파일 | 내용 |
| --- | --- |
| [article-crud.sql](./article-crud.sql) | 게시글 테이블 생성, CRUD, id / 작성일 컬럼 추가 |
| [constraints.sql](./constraints.sql) | NOT NULL, PRIMARY KEY, AUTO_INCREMENT, 컬럼 변경, 조건 조회 |
| [inner-join.sql](./inner-join.sql) | 부서/사원 테이블 구조 개선, deptId 도입, INNER JOIN |

---

## 1. article CRUD

처음에는 게시글에 `title`, `body`만 저장했다.

```sql
CREATE TABLE article (
    title VARCHAR(100),
    `body` TEXT
);
```

하지만 같은 제목과 내용을 가진 데이터가 여러 개 생기면 각각을 구분하기 어렵다.
그래서 `id` 컬럼을 추가하고, 이후 작성 시간을 저장하기 위해 `regDate`도 추가했다.

### CRUD

| 구분 | SQL | 의미 |
| --- | --- | --- |
| Create | `INSERT` | 데이터 추가 |
| Read | `SELECT` | 데이터 조회 |
| Update | `UPDATE` | 데이터 수정 |
| Delete | `DELETE` | 데이터 삭제 |

`SELECT * FROM article;`은 article 테이블의 **모든 컬럼과 실제 데이터**를 조회한다.

반면,

```sql
DESC article;
```

은 실제 데이터가 아니라 컬럼명, 자료형, NULL 허용 여부, Key 등 **테이블 구조**를 확인한다.

---

## 2. 제약조건

처음 만든 `article` 테이블에서는 id를 입력하지 않아도 데이터가 저장되어 id가 `NULL`이 될 수 있었다.

### NOT NULL

```sql
ALTER TABLE article MODIFY id INT NOT NULL;
```

기존 데이터에 이미 NULL이 존재하면 바로 적용할 수 없다.
따라서 기존 값을 먼저 정리한 뒤 제약조건을 적용해야 한다.

### PRIMARY KEY

PRIMARY KEY는 각 행을 구분하는 대표 값이다.

- NULL 불가
- 중복 불가

기존 id 값이 모두 0처럼 중복되어 있으면 PRIMARY KEY 적용이 실패한다.
중복을 먼저 제거한 뒤 적용했다.

### AUTO_INCREMENT

```sql
ALTER TABLE article
MODIFY COLUMN id INT UNSIGNED NOT NULL AUTO_INCREMENT;
```

새 데이터를 추가할 때 id를 직접 입력하지 않아도 자동으로 증가하는 번호가 부여된다.

### 컬럼 변경

이번 실습에서는 `ALTER TABLE`을 이용해 다음 작업도 수행했다.

- `ADD COLUMN`: 컬럼 추가
- `CHANGE`: 컬럼 이름 변경
- `MODIFY COLUMN`: 자료형/조건/위치 변경
- `DROP COLUMN`: 컬럼 삭제

---

## 3. 조건 조회

```sql
SELECT *
FROM article
ORDER BY hit DESC
LIMIT 3;
```

조회수가 높은 순으로 정렬한 뒤 상위 3개만 조회한다.

```sql
SELECT *
FROM article
WHERE nickname LIKE '홍길%';
```

`%`는 뒤에 어떤 문자열이 와도 된다는 의미이므로 작성자명이 **홍길로 시작하는 데이터**를 찾는다.

또한,

- `AND`: 두 조건을 모두 만족
- `OR`: 둘 중 하나 이상 만족
- `!=`: 같지 않음

을 사용해 원하는 데이터만 필터링했다.

---

## 4. dept / emp와 INNER JOIN

처음에는 사원 테이블에 부서명을 직접 저장했다.

예를 들어 홍보 부서의 이름을 마케팅으로 변경하면,

- `dept.name`
- `emp.deptName`

두 곳을 모두 수정해야 한다.

이 중복을 줄이기 위해 사원 테이블에는 부서 이름 대신 **부서 번호(deptId)** 를 저장하도록 구조를 변경했다.

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

이제 부서 이름은 `dept`에서 한 번만 관리하고,
사원은 `deptId`를 통해 자신의 부서를 기억한다.

### INNER JOIN

```sql
SELECT E.id AS `사원번호`,
       E.name AS `사원명`,
       DATE(E.regDate) AS `입사일`,
       D.name AS `부서명`
FROM emp AS E
INNER JOIN dept AS D
ON E.deptId = D.id;
```

핵심은

```sql
ON E.deptId = D.id
```

이다.

사원의 `deptId`와 부서의 `id`가 같은 행끼리 연결해서
사원 정보와 부서명을 한 번에 조회한다.

## 이번 주 핵심 정리

1. 테이블의 **데이터 조회**는 `SELECT`, **구조 확인**은 `DESC`
2. 제약조건을 추가하려면 기존 데이터도 해당 조건을 만족해야 함
3. `PRIMARY KEY`는 행을 고유하게 구분
4. `AUTO_INCREMENT`는 id를 자동으로 증가시킴
5. 같은 정보를 여러 테이블에 반복 저장하면 수정할 곳이 늘어남
6. 관계가 있는 데이터는 번호(id)로 연결하고 JOIN으로 함께 조회할 수 있음
7. `INNER JOIN`에서는 어떤 행끼리 연결할지 `ON` 조건이 중요함

## 실행 환경

- MySQL 8.4.1
- DBeaver
- Docker
