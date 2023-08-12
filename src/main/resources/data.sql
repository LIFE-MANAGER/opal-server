insert into member_details (created_at, state, updated_at, has_children, hobby, is_married, personality, relation_type) VALUES
    (now(), "ACTIVE", now(), true, "basketball", true, "activity", "sexualfriend") ,
    (now(), "ACTIVE", now(), true, "baseball", true, "activity", "friend") ,
    (now(), "ACTIVE", now(), true, "basketball", true, "activity", "sexualfriend") ,
    (now(), "ACTIVE", now(), true, "baseball", true, "activity", "friend") ,
    (now(), "ACTIVE", now(), true, "basketball", true, "activity", "sexualfriend") ,
    (now(), "ACTIVE", now(), true, "baseball", true, "activity", "friend") ,
    (now(), "ACTIVE", now(), true, "basketball", true, "activity", "sexualfriend") ,
    (now(), "ACTIVE", now(), true, "baseball", true, "activity", "friend") ,
    (now(), "ACTIVE", now(), true, "basketball", true, "activity", "sexualfriend") ,
    (now(), "ACTIVE", now(), true, "baseball", true, "activity", "friend")
;

insert into member_birth (created_at, state, updated_at, birth) VALUES
    (now(), "ACTIVE", now(), "1999-11-11") ,
    (now(), "ACTIVE", now(), "1999-02-01") ,
    (now(), "ACTIVE", now(), "1999-01-11") ,
    (now(), "ACTIVE", now(), "1999-03-11") ,
    (now(), "ACTIVE", now(), "1999-04-11") ,
    (now(), "ACTIVE", now(), "1999-05-11") ,
    (now(), "ACTIVE", now(), "1999-06-11") ,
    (now(), "ACTIVE", now(), "1999-07-11") ,
    (now(), "ACTIVE", now(), "1999-08-11") ,
    (now(), "ACTIVE", now(), "1999-09-11")

;

insert into member_location (created_at, state, updated_at, location) VALUES
    (now(), "ACTIVE", now(), Point(22,22)),
    (now(), "ACTIVE", now(), Point(22,22)),
    (now(), "ACTIVE", now(), Point(22,22)),
    (now(), "ACTIVE", now(), Point(22,22)),
    (now(), "ACTIVE", now(), Point(22,22)),
    (now(), "ACTIVE", now(), Point(22,22)),
    (now(), "ACTIVE", now(), Point(22,22)),
    (now(), "ACTIVE", now(), Point(22,22)),
    (now(), "ACTIVE", now(), Point(22,22)),
    (now(), "ACTIVE", now(), Point(22,22)),
    (now(), "ACTIVE", now(), Point(22,22))
;


insert into member (id_with_provider, nickname, member_name, email, password, phone_num, job, introduction, location_yn, subscribe, nickname_update_at, created_at, updated_at, state, member_details_idx, member_birth_idx, member_location_idx) values
    ("LOCAL", "chann", "LeeDongChan", "ldc0000@naver.com", "chan123", "010-1234-1234", "job", "introduction", true, true, now(), now(), now(), "ACTIVE", 1, 1, 1),
    ("LOCAL", "minjung", "BokMinJung", "min1111@naver.com", "chan123", "010-1234-1234", "job", "introduction", true, true, now(), now(), now(), "ACTIVE", 2, 2, 2),
    ("KAKAO_12341423", "kakaoUser", "CHAN", "ldc1222@naver.com", "chan123", "010-1234-1234", "job", "introduction", true, true, now(), now(), now(), "ACTIVE", 3, 3, 3) ,
    ("LOCAL", "user1", "USER1", "ldc1111@naver.com", "chan123", "010-1234-1234", "job", "introduction", true, true, now(), now(), now(), "ACTIVE", 4, 4, 4) ,
    ("LOCAL", "user2", "USER2", "ldc2222@naver.com", "chan123", "010-1234-1234", "job", "introduction", true, true, now(), now(), now(), "ACTIVE", 5, 5, 5) ,
    ("LOCAL", "user3", "USER3", "ldc3333@naver.com", "chan123", "010-1234-1234", "job", "introduction", true, true, now(), now(), now(), "ACTIVE", 6, 6, 6) ,
    ("LOCAL", "user4", "USER4", "ldc4444@naver.com", "chan123", "010-1234-1234", "job", "introduction", true, true, now(), now(), now(), "ACTIVE", 7, 7, 7) ,
    ("LOCAL", "user5", "USER5", "ldc5555@naver.com", "chan123", "010-1234-1234", "job", "introduction", true, true, now(), now(), now(), "ACTIVE", 8, 8, 8) ,
    ("LOCAL", "user6", "USER6", "ldc6666@naver.com", "chan123", "010-1234-1234", "job", "introduction", true, true, now(), now(), now(), "ACTIVE", 9, 9, 9) ,
    ("LOCAL", "user7", "USER7", "ldc7777@naver.com", "chan123", "010-1234-1234", "job", "introduction", true, true, now(), now(), now(), "ACTIVE", 10, 10, 10)
;


insert into todays_friends (member_idx, member_by_personality, member_by_relation_type, member_by_hobby) values
    (1, 2, 3, 4) ,
    (2, 1, 3, 4) ,
    (3, 1, 2, 4) ,
    (4, 1, 2, 3) ,
    (5, 1, 2, 3) ,
    (6, 2, 3, 4) ,
    (7, 2, 3, 4) ,
    (8, 2, 3, 4) ,
    (9, 1, 2, 3) ,
    (10, 1, 2, 3)
;

insert into member_image (created_at, state, updated_at, image_url, member_idx) VALUES
    (now(), "ACTIVE", now(), "url1", 1),
    (now(), "ACTIVE", now(), "url2", 2),
    (now(), "ACTIVE", now(), "url3", 3),
    (now(), "ACTIVE", now(), "url4", 4),
    (now(), "ACTIVE", now(), "url5", 5),
    (now(), "ACTIVE", now(), "url6", 6),
    (now(), "ACTIVE", now(), "url7", 7),
    (now(), "ACTIVE", now(), "url8", 8),
    (now(), "ACTIVE", now(), "url9", 9),
    (now(), "ACTIVE", now(), "url10", 10)
;
