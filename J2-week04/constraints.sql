-- J2-week04 / constraints
-- NULL, 중복 id를 정리한 뒤 제약조건을 적용하고 조건 조회를 실습한다.

DROP DATABASE IF EXISTS `a2`;
CREATE DATABASE `a2`;
USE `a2`;

CREATE TABLE article (
    id INT,
    regDate DATETIME,
    title VARCHAR(100),
    `body` TEXT
);

-- id를 생략하여 NULL인 데이터 2개 생성
INSERT INTO article
SET regDate = NOW(),
    title = '제목',
    `body` = '내용';

INSERT INTO article
SET regDate = NOW(),
    title = '제목',
    `body` = '내용';

SELECT *
FROM article;

-- 실패 예시:
-- 기존 id에 NULL이 있기 때문에 바로 NOT NULL을 적용할 수 없다.
-- ALTER TABLE article MODIFY id INT NOT NULL;

-- 기존 NULL 값을 0으로 정리
UPDATE article
SET id = 0;

-- NOT NULL 적용
ALTER TABLE article
MODIFY id INT NOT NULL;

DESC article;
SELECT *
FROM article;

-- 실패 예시:
-- 현재 두 행의 id가 모두 0이므로 PRIMARY KEY를 적용할 수 없다.
-- ALTER TABLE article ADD PRIMARY KEY(id);

-- 중복 id 제거
UPDATE article
SET id = 1
WHERE id = 0
LIMIT 1;

UPDATE article
SET id = 2
WHERE id = 0;

SELECT *
FROM article;

-- PRIMARY KEY 적용
ALTER TABLE article
ADD PRIMARY KEY(id);

-- AUTO_INCREMENT 적용
-- AUTO_INCREMENT 대상 컬럼은 Key여야 한다.
ALTER TABLE article
MODIFY COLUMN id INT NOT NULL AUTO_INCREMENT;

-- 나머지 컬럼에도 NOT NULL 적용
ALTER TABLE article MODIFY COLUMN regDate DATETIME NOT NULL;
ALTER TABLE article MODIFY COLUMN title VARCHAR(100) NOT NULL;
ALTER TABLE article MODIFY COLUMN `body` TEXT NOT NULL;

-- id는 음수가 필요 없으므로 UNSIGNED 추가
ALTER TABLE article
MODIFY COLUMN id INT UNSIGNED NOT NULL AUTO_INCREMENT;

DESC article;

-- writer 컬럼 추가
ALTER TABLE article
ADD COLUMN writer VARCHAR(100) NOT NULL AFTER title;

-- writer -> nickname 이름 변경
ALTER TABLE article
CHANGE writer nickname VARCHAR(100) NOT NULL;

-- nickname을 body 뒤로 이동
ALTER TABLE article
MODIFY COLUMN nickname VARCHAR(100) NOT NULL AFTER `body`;

-- hit 컬럼 추가 -> 삭제 -> 다시 추가
ALTER TABLE article
ADD COLUMN hit INT UNSIGNED NOT NULL AFTER nickname;

DESC article;

ALTER TABLE article
DROP COLUMN hit;

ALTER TABLE article
ADD COLUMN hit INT UNSIGNED NOT NULL AFTER nickname;

DESC article;

-- 기존 행의 빈 nickname을 '무명'으로 변경
UPDATE article
SET nickname = '무명'
WHERE nickname = '';

-- 데이터 추가
INSERT INTO article
SET regDate = NOW(),
    title = '제목3',
    `body` = '내용3',
    nickname = '홍길순',
    hit = 10;

INSERT INTO article
SET regDate = NOW(),
    title = '제목4',
    `body` = '내용4',
    nickname = '홍길동',
    hit = 55;

INSERT INTO article
SET regDate = NOW(),
    title = '제목5',
    `body` = '내용5',
    nickname = '홍길동',
    hit = 10;

INSERT INTO article
SET regDate = NOW(),
    title = '제목6',
    `body` = '내용6',
    nickname = '임꺽정',
    hit = 100;

SELECT *
FROM article;

-- 조회수가 가장 높은 게시물 3개
SELECT *
FROM article
ORDER BY hit DESC
LIMIT 3;

-- 작성자명이 '홍길'로 시작
SELECT *
FROM article
WHERE nickname LIKE '홍길%';

-- 조회수 10 이상 AND 55 이하
SELECT *
FROM article
WHERE hit >= 10
  AND hit <= 55;

-- 작성자가 '무명'이 아니고 조회수가 50 이하
SELECT *
FROM article
WHERE nickname != '무명'
  AND hit <= 50;

-- 작성자가 '무명'이거나 조회수가 55 이상
SELECT *
FROM article
WHERE nickname = '무명'
   OR hit >= 55;
