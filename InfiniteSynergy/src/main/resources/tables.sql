CREATE TABLE IF NOT EXISTS users (
                       id SERIAL PRIMARY KEY,
                       username VARCHAR(255) NOT NULL,
                       password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS bank_accounts (
                               id SERIAL PRIMARY KEY,
                               user_id BIGINT REFERENCES users(id),
                               account_number VARCHAR(50) UNIQUE NOT NULL,
                               amount DOUBLE PRECISION NOT NULL,
                               FOREIGN KEY (user_id) REFERENCES users(id)
);