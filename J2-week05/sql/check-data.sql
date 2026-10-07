-- DBeaver에서 surl_dev 연결을 선택한 후 실행합니다. 데이터를 삭제하지 않습니다.
USE surl_dev;
SHOW TABLES;
DESC member;
DESC article;
DESC surl;
SELECT id, username, nickname, create_date, modify_date FROM member;
SELECT a.id, a.title, m.username AS author, a.create_date, a.modify_date
FROM article a INNER JOIN member m ON a.author_id = m.id ORDER BY a.id;
SELECT s.id, s.body, s.url, s.count, m.username AS author
FROM surl s INNER JOIN member m ON s.author_id = m.id ORDER BY s.id;
SELECT COUNT(*) AS article_count FROM article;
