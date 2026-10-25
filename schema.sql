CREATE DATABASE IF NOT EXISTS digital_diary;
USE digital_diary;

CREATE TABLE IF NOT EXISTS users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS entries (
    entry_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    entry_type VARCHAR(20) NOT NULL,
    entry_date DATE NOT NULL,
    title VARCHAR(100) NOT NULL,
    content TEXT NOT NULL,
    mood VARCHAR(30) NOT NULL,
    CONSTRAINT fk_entries_user
        FOREIGN KEY (user_id) REFERENCES users(user_id)
        ON DELETE CASCADE
);
