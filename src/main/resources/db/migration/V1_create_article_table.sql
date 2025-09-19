CREATE TABLE article
(
    id      UUID PRIMARY KEY,
    title   VARCHAR(255) NOT NULL,
    content TEXT         NOT NULL,
    created TIMESTAMP    NOT NULL
);