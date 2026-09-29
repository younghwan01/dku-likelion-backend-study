-- J2-week04 / article CRUD
-- 제목/내용만 있는 article 테이블에서 시작해 id와 작성일을 추가한다.

-- 전체 데이터베이스 확인
SHOW DATABASES;

-- mysql 데이터베이스 선택
USE mysql;

-- 테이블 목록 확인
SHOW TABLES;

-- 특정 테이블 구조 확인
-- 계정 권한에 따라 mysql.user 조회가 제한될 수 있다.
DESC `user`;

-- test 데이터베이스 생성/선택
CREATE DATABASE IF NOT EXISTS test;
USE test;
SHOW TABLES;

-- a1 데이터베이스 초기화
DROP DATABASE IF EXISTS `a1`;
CREATE DATABASE `a1`;
USE `a1`;

SHOW DATABASES;
SHOW TABLES;

-- 게시물 테이블 생성
-- VARCHAR(100): 최대 100자의 문자열
-- TEXT: 긴 문자열 저장
CREATE TABLE article (
    title VARCHAR(100),
    `body` TEXT
);

SHOW TABLES;
DESC article;

-- Create: 데이터 추가
INSERT INTO article
SET title = '제목',
    `body` = '내용';

-- Read: 원하는 컬럼 조회
SELECT title
FROM article;

SELECT title, `body`
FROM article;

SELECT `body`, title
FROM article;

-- 모든 컬럼 조회
SELECT *
FROM article;

-- 같은 내용의 데이터 한 번 더 추가
INSERT INTO article
SET title = '제목',
    `body` = '내용';

-- 두 행을 구분할 id가 아직 없다.
SELECT *
FROM article;

-- id 컬럼을 맨 앞에 추가
ALTER TABLE article ADD COLUMN id INT FIRST;

-- 기존 데이터의 id는 NULL
SELECT *
FROM article;

-- NULL인 모든 id를 1로 변경
UPDATE article
SET id = 1
WHERE id IS NULL;

SELECT *
FROM article;

-- id가 1인 행 중 1개만 id=2로 변경
-- LIMIT 1은 변경할 행 수만 제한한다.
UPDATE article
SET id = 2
WHERE id = 1
LIMIT 1;

SELECT *
FROM article;

-- id=3 데이터 추가
INSERT INTO article
SET id = 3,
    title = '제목3',
    `body` = '내용3';

SELECT *
FROM article;

-- Delete: id=2 게시물 삭제
DELETE FROM article
WHERE id = 2;

SELECT *
FROM article;

-- 작성일 컬럼 추가
ALTER TABLE article
ADD COLUMN regDate DATETIME AFTER id;

DESC article;
SELECT *
FROM article;

-- id=1 게시물의 작성일 직접 입력
UPDATE article
SET regDate = '2018-08-10 15:00:00'
WHERE id = 1;

SELECT *
FROM article;

-- NOW(): 현재 날짜/시간 반환
SELECT NOW();

-- id=3 게시물의 작성일을 현재 시간으로 입력
UPDATE article
SET regDate = NOW()
WHERE id = 3;

SELECT *
FROM article;
