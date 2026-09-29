-- J2-week04 / INNER JOIN
-- 부서명을 사원 테이블에 직접 저장하던 구조를 deptId 방식으로 바꾸고 JOIN을 실습한다.

DROP DATABASE IF EXISTS a5;
CREATE DATABASE a5;
USE a5;

-- 1. 부서(dept) 테이블 생성
CREATE TABLE dept (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    PRIMARY KEY(id),
    regDate DATETIME NOT NULL,
    `name` CHAR(100) NOT NULL UNIQUE
);

INSERT INTO dept
SET regDate = NOW(),
    `name` = '홍보';

INSERT INTO dept
SET regDate = NOW(),
    `name` = '기획';

SELECT *
FROM dept;

-- 2. 사원(emp) 테이블 생성
-- 처음에는 부서명을 deptName에 직접 저장한다.
CREATE TABLE emp (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    PRIMARY KEY(id),
    regDate DATETIME NOT NULL,
    `name` CHAR(100) NOT NULL,
    deptName CHAR(100) NOT NULL
);

INSERT INTO emp
SET regDate = NOW(),
    `name` = '홍길동',
    deptName = '홍보';

INSERT INTO emp
SET regDate = NOW(),
    `name` = '홍길순',
    deptName = '홍보';

INSERT INTO emp
SET regDate = NOW(),
    `name` = '임꺽정',
    deptName = '기획';

SELECT *
FROM emp;

-- 3. 부서명을 직접 저장했을 때의 문제 확인
-- dept의 '홍보'를 '마케팅'으로 변경
UPDATE dept
SET `name` = '마케팅'
WHERE `name` = '홍보';

SELECT *
FROM dept;

-- emp에는 아직 '홍보'가 남아 있으므로 별도로 수정해야 한다.
SELECT *
FROM emp;

UPDATE emp
SET deptName = '마케팅'
WHERE deptName = '홍보';

SELECT *
FROM emp;

-- 다시 원래 상태로 복구
UPDATE dept
SET `name` = '홍보'
WHERE `name` = '마케팅';

UPDATE emp
SET deptName = '홍보'
WHERE deptName = '마케팅';

-- 4. 사원 테이블이 부서 이름 대신 부서 번호를 기억하도록 변경
ALTER TABLE emp
ADD COLUMN deptId INT UNSIGNED NOT NULL;

UPDATE emp
SET deptId = 1
WHERE deptName = '홍보';

UPDATE emp
SET deptId = 2
WHERE deptName = '기획';

SELECT *
FROM emp;

-- 기존 deptName 제거
ALTER TABLE emp
DROP COLUMN deptName;

SELECT *
FROM emp;

-- 이제 부서 이름은 dept에서만 바꾸면 된다.
UPDATE dept
SET `name` = '마케팅'
WHERE `name` = '홍보';

SELECT *
FROM dept;

SELECT *
FROM emp;

-- 5. 사장님께 드릴 인명록
-- emp만 조회하면 deptId만 보여 부서 이름을 바로 알기 어렵다.
SELECT *
FROM emp;

-- dept를 따로 조회하면 부서명을 볼 수 있지만 한 화면에 사원 정보와 같이 나오지 않는다.
SELECT *
FROM dept;

-- 6. ON 없이 JOIN
-- MySQL에서는 두 테이블의 가능한 조합이 만들어져 잘못된 소속까지 함께 보일 수 있다.
SELECT emp.*, dept.name AS `부서명`
FROM emp
INNER JOIN dept;

-- 7. ON 조건을 적용한 올바른 INNER JOIN
-- emp.deptId와 dept.id가 같은 행끼리 연결한다.
SELECT emp.*, dept.name AS `부서명`
FROM emp
INNER JOIN dept
ON emp.deptId = dept.id;

-- 8. 출력 컬럼명을 보기 좋게 변경
SELECT emp.id AS `사원번호`,
       emp.name AS `사원명`,
       DATE(emp.regDate) AS `입사일`,
       dept.name AS `부서명`
FROM emp
INNER JOIN dept
ON emp.deptId = dept.id
ORDER BY `부서명`, `사원명`;

-- 9. 테이블에도 별칭(AS) 적용
SELECT E.id AS `사원번호`,
       E.name AS `사원명`,
       DATE(E.regDate) AS `입사일`,
       D.name AS `부서명`
FROM emp AS E
INNER JOIN dept AS D
ON E.deptId = D.id
ORDER BY `부서명`, `사원명`;

-- 10. 기획부서에 김영희 추가
SELECT id, `name`
FROM dept;

INSERT INTO emp
SET regDate = NOW(),
    `name` = '김영희',
    deptId = 2;

-- 11. IT 부서 생성 후 김철수 배속
INSERT INTO dept
SET regDate = NOW(),
    `name` = 'IT';

SELECT id, `name`
FROM dept;

INSERT INTO emp
SET regDate = NOW(),
    `name` = '김철수',
    deptId = 3;

-- 최종 인명록
SELECT E.id AS `사원번호`,
       E.name AS `사원명`,
       DATE(E.regDate) AS `입사일`,
       D.name AS `부서명`
FROM emp AS E
INNER JOIN dept AS D
ON E.deptId = D.id
ORDER BY `부서명`, `사원명`;
