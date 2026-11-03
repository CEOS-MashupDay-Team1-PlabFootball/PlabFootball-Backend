-- 발표용 전국 시연 데이터. 기존 03_demo.sql 데이터에 추가한다.
-- 수동 실행 전 기준 지역 데이터와 ID 충돌을 확인한다.
-- 2026년 11월 4일부터 11일까지 16개 광역에 512개 매치를 생성한다.
-- 오류 발생 시 실행을 중단하는 MySQL 클라이언트를 사용한다. --force 사용 금지.
SET time_zone = '+09:00';
START TRANSACTION;

INSERT INTO stadium_groups (id, areas_id) VALUES
    (910001, 6),
    (910002, 139),
    (910003, 15),
    (910004, 16),
    (910005, 104),
    (910006, 39),
    (910007, 26),
    (910008, 160),
    (910009, 9),
    (910010, 32),
    (910011, 41),
    (910012, 50),
    (910013, 25),
    (910014, 59),
    (910015, 86),
    (910016, 87),
    (910017, 83),
    (910018, 84),
    (910019, 76),
    (910020, 151),
    (910021, 48),
    (910022, 146),
    (910023, 62),
    (910024, 72),
    (910025, 60),
    (910026, 42),
    (910027, 78),
    (910028, 79),
    (910029, 88),
    (910030, 89),
    (910031, 75),
    (910032, 92);

INSERT INTO stadiums (id, stadium_groups_id, name, address, image_url,
    is_indoor, has_awning, has_parking, has_shoe_rental, latitude, longitude,
    facility_info, created_at, updated_at)
VALUES
    (910001, 910001, '서울특별시 노원구 시연 풋살장', '서울특별시 노원구 시연용 주소', '/images/stadiums/001.jpg', 1, 1, 1, 0, NULL, NULL, '시연용 시설 정보', NOW(), NOW()),
    (910002, 910002, '서울특별시 마포구 시연 풋살장', '서울특별시 마포구 시연용 주소', '/images/stadiums/002.jpg', 0, 0, 1, 1, NULL, NULL, '시연용 시설 정보', NOW(), NOW()),
    (910003, 910003, '경기 성남시 시연 풋살장', '경기 성남시 시연용 주소', '/images/stadiums/003.jpg', 1, 0, 1, 1, NULL, NULL, '시연용 시설 정보', NOW(), NOW()),
    (910004, 910004, '경기 수원시 시연 풋살장', '경기 수원시 시연용 주소', '/images/stadiums/004.jpg', 0, 1, 0, 1, NULL, NULL, '시연용 시설 정보', NOW(), NOW()),
    (910005, 910005, '인천광역시 부평구 시연 풋살장', '인천광역시 부평구 시연용 주소', '/images/stadiums/005.jpg', 1, 0, 1, 0, NULL, NULL, '시연용 시설 정보', NOW(), NOW()),
    (910006, 910006, '인천광역시 연수구 시연 풋살장', '인천광역시 연수구 시연용 주소', '/images/stadiums/006.jpg', 0, 0, 1, 1, NULL, NULL, '시연용 시설 정보', NOW(), NOW()),
    (910007, 910007, '대전/세종 유성구 시연 풋살장', '대전/세종 유성구 시연용 주소', '/images/stadiums/007.jpg', 1, 1, 0, 1, NULL, NULL, '시연용 시설 정보', NOW(), NOW()),
    (910008, 910008, '대전/세종 세종시 시연 풋살장', '대전/세종 세종시 시연용 주소', '/images/stadiums/008.jpg', 0, 0, 1, 1, NULL, NULL, '시연용 시설 정보', NOW(), NOW()),
    (910009, 910009, '대구광역시 달서구 시연 풋살장', '대구광역시 달서구 시연용 주소', '/images/stadiums/009.jpg', 1, 0, 1, 0, NULL, NULL, '시연용 시설 정보', NOW(), NOW()),
    (910010, 910010, '대구광역시 수성구 시연 풋살장', '대구광역시 수성구 시연용 주소', '/images/stadiums/010.jpg', 0, 1, 0, 1, NULL, NULL, '시연용 시설 정보', NOW(), NOW()),
    (910011, 910011, '부산광역시 부산진구 시연 풋살장', '부산광역시 부산진구 시연용 주소', '/images/stadiums/011.jpg', 1, 0, 1, 1, NULL, NULL, '시연용 시설 정보', NOW(), NOW()),
    (910012, 910012, '부산광역시 해운대구 시연 풋살장', '부산광역시 해운대구 시연용 주소', '/images/stadiums/012.jpg', 0, 0, 1, 1, NULL, NULL, '시연용 시설 정보', NOW(), NOW()),
    (910013, 910013, '광주광역시 광산구 시연 풋살장', '광주광역시 광산구 시연용 주소', '/images/stadiums/013.jpg', 1, 1, 0, 0, NULL, NULL, '시연용 시설 정보', NOW(), NOW()),
    (910014, 910014, '광주광역시 북구 시연 풋살장', '광주광역시 북구 시연용 주소', '/images/stadiums/014.jpg', 0, 0, 1, 1, NULL, NULL, '시연용 시설 정보', NOW(), NOW()),
    (910015, 910015, '제주특별자치도 제주시 시연 풋살장', '제주특별자치도 제주시 시연용 주소', '/images/stadiums/015.jpg', 1, 0, 1, 1, NULL, NULL, '시연용 시설 정보', NOW(), NOW()),
    (910016, 910016, '제주특별자치도 서귀포시 시연 풋살장', '제주특별자치도 서귀포시 시연용 주소', '/images/stadiums/016.jpg', 0, 1, 0, 1, NULL, NULL, '시연용 시설 정보', NOW(), NOW()),
    (910017, 910017, '강원특별자치도 원주시 시연 풋살장', '강원특별자치도 원주시 시연용 주소', '/images/stadiums/001.jpg', 1, 0, 1, 0, NULL, NULL, '시연용 시설 정보', NOW(), NOW()),
    (910018, 910018, '강원특별자치도 춘천시 시연 풋살장', '강원특별자치도 춘천시 시연용 주소', '/images/stadiums/002.jpg', 0, 0, 1, 1, NULL, NULL, '시연용 시설 정보', NOW(), NOW()),
    (910019, 910019, '충청남도 천안시 시연 풋살장', '충청남도 천안시 시연용 주소', '/images/stadiums/003.jpg', 1, 1, 0, 1, NULL, NULL, '시연용 시설 정보', NOW(), NOW()),
    (910020, 910020, '충청남도 아산시 시연 풋살장', '충청남도 아산시 시연용 주소', '/images/stadiums/004.jpg', 0, 0, 1, 1, NULL, NULL, '시연용 시설 정보', NOW(), NOW()),
    (910021, 910021, '충청북도 청주시 시연 풋살장', '충청북도 청주시 시연용 주소', '/images/stadiums/005.jpg', 1, 0, 1, 0, NULL, NULL, '시연용 시설 정보', NOW(), NOW()),
    (910022, 910022, '충청북도 충주시 시연 풋살장', '충청북도 충주시 시연용 주소', '/images/stadiums/006.jpg', 0, 1, 0, 1, NULL, NULL, '시연용 시설 정보', NOW(), NOW()),
    (910023, 910023, '경상북도 구미시 시연 풋살장', '경상북도 구미시 시연용 주소', '/images/stadiums/007.jpg', 1, 0, 1, 1, NULL, NULL, '시연용 시설 정보', NOW(), NOW()),
    (910024, 910024, '경상북도 포항시 시연 풋살장', '경상북도 포항시 시연용 주소', '/images/stadiums/008.jpg', 0, 0, 1, 1, NULL, NULL, '시연용 시설 정보', NOW(), NOW()),
    (910025, 910025, '울산광역시 남구 시연 풋살장', '울산광역시 남구 시연용 주소', '/images/stadiums/009.jpg', 1, 1, 0, 0, NULL, NULL, '시연용 시설 정보', NOW(), NOW()),
    (910026, 910026, '울산광역시 북구 시연 풋살장', '울산광역시 북구 시연용 주소', '/images/stadiums/010.jpg', 0, 0, 1, 1, NULL, NULL, '시연용 시설 정보', NOW(), NOW()),
    (910027, 910027, '경상남도 김해시 시연 풋살장', '경상남도 김해시 시연용 주소', '/images/stadiums/011.jpg', 1, 0, 1, 1, NULL, NULL, '시연용 시설 정보', NOW(), NOW()),
    (910028, 910028, '경상남도 창원시 시연 풋살장', '경상남도 창원시 시연용 주소', '/images/stadiums/012.jpg', 0, 1, 0, 1, NULL, NULL, '시연용 시설 정보', NOW(), NOW()),
    (910029, 910029, '전라남도 순천시 시연 풋살장', '전라남도 순천시 시연용 주소', '/images/stadiums/013.jpg', 1, 0, 1, 0, NULL, NULL, '시연용 시설 정보', NOW(), NOW()),
    (910030, 910030, '전라남도 여수시 시연 풋살장', '전라남도 여수시 시연용 주소', '/images/stadiums/014.jpg', 0, 0, 1, 1, NULL, NULL, '시연용 시설 정보', NOW(), NOW()),
    (910031, 910031, '전북특별자치도 전주시 시연 풋살장', '전북특별자치도 전주시 시연용 주소', '/images/stadiums/015.jpg', 1, 1, 0, 1, NULL, NULL, '시연용 시설 정보', NOW(), NOW()),
    (910032, 910032, '전북특별자치도 익산시 시연 풋살장', '전북특별자치도 익산시 시연용 주소', '/images/stadiums/016.jpg', 0, 0, 1, 1, NULL, NULL, '시연용 시설 정보', NOW(), NOW());

-- 각 구장에 매일 20시, 22시 두 매치를 배치한다.
-- 20시 매치는 발표 시점에 마감임박, 일부 22시 매치는 확정임박 조건이 되도록 구성한다.
INSERT INTO matches (id, stadium_id, start_time, end_time, price,
    status_type, match_type, gender_type, level_type, min_players, max_players,
    current_players, parking_capacity, reserved_parking_count, is_earlybird,
    has_ai_report, has_screen, created_at, updated_at)
WITH RECURSIVE demo_days AS (
    SELECT 0 AS day_offset
    UNION ALL
    SELECT day_offset + 1 FROM demo_days WHERE day_offset < 7
), demo_slots AS (
    SELECT 0 AS slot_index, 20 AS start_hour
    UNION ALL SELECT 1, 22
)
SELECT
    920001 + (s.id - 910001) * 16 + d.day_offset * 2 + t.slot_index,
    s.id,
    TIMESTAMP(DATE_ADD('2026-11-04', INTERVAL d.day_offset DAY), MAKETIME(t.start_hour, 0, 0)),
    DATE_ADD(TIMESTAMP(DATE_ADD('2026-11-04', INTERVAL d.day_offset DAY), MAKETIME(t.start_hour, 0, 0)), INTERVAL 2 HOUR),
    CASE WHEN t.slot_index = 0 THEN 10000 ELSE 12000 END,
    CASE WHEN t.slot_index = 1 AND MOD(s.id - 910001, 8) = 7 THEN 'CLOSED' ELSE 'OPEN' END,
    CASE WHEN MOD(s.id - 910001, 2) = 0 THEN 'FIVE_VS_FIVE' ELSE 'SIX_VS_SIX' END,
    CASE WHEN t.slot_index = 0 THEN 'MIXED' WHEN MOD(s.id - 910001, 2) = 0 THEN 'FEMALE' ELSE 'MALE' END,
    CASE WHEN t.slot_index = 0 THEN 'STARTER' WHEN MOD(s.id - 910001, 2) = 0 THEN 'BASIC' ELSE 'INTERMEDIATE' END,
    10, 18,
    CASE WHEN t.slot_index = 0 THEN 12 WHEN MOD(s.id - 910001, 8) = 7 THEN 18
         WHEN MOD(s.id - 910001, 2) = 0 THEN 9 ELSE 12 END,
    CASE WHEN s.has_parking = 1 THEN 10 ELSE 0 END, 0,
    CASE WHEN MOD(s.id - 910001, 2) = 0 THEN 1 ELSE 0 END,
    CASE WHEN MOD(s.id - 910001, 3) = 0 THEN 1 ELSE 0 END,
    CASE WHEN MOD(s.id - 910001, 3) = 1 THEN 1 ELSE 0 END,
    NOW(), NOW()
FROM stadiums s
CROSS JOIN demo_days d
CROSS JOIN demo_slots t
WHERE s.id BETWEEN 910001 AND 910032;

COMMIT;
