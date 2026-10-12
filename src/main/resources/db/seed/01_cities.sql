-- 01_cities.sql
-- 시/도 초기 시드 데이터

INSERT INTO cities (name)
VALUES ('서울특별시')
ON DUPLICATE KEY UPDATE
    name = VALUES(name);