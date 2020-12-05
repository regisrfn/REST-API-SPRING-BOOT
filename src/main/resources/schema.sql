DROP TABLE IF EXISTS users;
CREATE TABLE users (
    user_id UUID NOT NULL PRIMARY KEY,
    user_name VARCHAR NOT NULL,
    user_nickname VARCHAR,
    user_email VARCHAR NOT NULL UNIQUE,
    user_password VARCHAR NOT NULL,
    created_at VARCHAR NOT NULL   
);

-- INSERT INTO users (user_id, user_name, user_nickname, user_email, user_password, created_at)
-- VALUES ('1a73efb7-c368-4693-92ad-f969ff2a0701', 'Joe Doe', 'Joe', 'joe@gmail.com', '123456','2020-11-29T21:08:37Z');