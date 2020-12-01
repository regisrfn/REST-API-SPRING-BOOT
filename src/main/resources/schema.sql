CREATE TABLE IF NOT EXISTS users (
    user_id UUID NOT NULL PRIMARY KEY,
    user_name VARCHAR NOT NULL,
    user_nickname VARCHAR,
    user_email VARCHAR NOT NULL UNIQUE,
    user_password VARCHAR NOT NULL,
    created_at DATE
);