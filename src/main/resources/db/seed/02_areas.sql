-- 서울 25개 구: 지역 id 종합(2026-10-05 조사)의 실제 area_id를 유지한다.
-- 광역 ID 및 필터 묶음 ID와 구분한다. 기존 데이터는 변경하지 않는다.
INSERT INTO areas (id, cities_id, name)
SELECT source.id, city.id, source.name
FROM cities city
CROSS JOIN (
    SELECT 1 AS id, '은평구' AS name
    UNION ALL SELECT 2 AS id, '용산구' AS name
    UNION ALL SELECT 3 AS id, '양천구' AS name
    UNION ALL SELECT 5 AS id, '동대문구' AS name
    UNION ALL SELECT 6 AS id, '노원구' AS name
    UNION ALL SELECT 7 AS id, '송파구' AS name
    UNION ALL SELECT 8 AS id, '도봉구' AS name
    UNION ALL SELECT 51 AS id, '광진구' AS name
    UNION ALL SELECT 55 AS id, '서초구' AS name
    UNION ALL SELECT 56 AS id, '강서구' AS name
    UNION ALL SELECT 99 AS id, '강남구' AS name
    UNION ALL SELECT 100 AS id, '금천구' AS name
    UNION ALL SELECT 101 AS id, '영등포구' AS name
    UNION ALL SELECT 133 AS id, '종로구' AS name
    UNION ALL SELECT 134 AS id, '중구' AS name
    UNION ALL SELECT 135 AS id, '중랑구' AS name
    UNION ALL SELECT 136 AS id, '성북구' AS name
    UNION ALL SELECT 137 AS id, '강북구' AS name
    UNION ALL SELECT 138 AS id, '서대문구' AS name
    UNION ALL SELECT 139 AS id, '마포구' AS name
    UNION ALL SELECT 140 AS id, '구로구' AS name
    UNION ALL SELECT 141 AS id, '동작구' AS name
    UNION ALL SELECT 142 AS id, '관악구' AS name
    UNION ALL SELECT 143 AS id, '강동구' AS name
    UNION ALL SELECT 144 AS id, '성동구' AS name
) source
WHERE city.name = '서울특별시'
ON DUPLICATE KEY UPDATE name = areas.name;
