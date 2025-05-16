CREATE TABLE borrower (
    id       BIGSERIAL      PRIMARY KEY,
    name     VARCHAR(255)   NOT NULL,
    email    VARCHAR(255)   NOT NULL UNIQUE
);

CREATE TABLE book (
    id           BIGSERIAL PRIMARY KEY,
    isbn         VARCHAR(255)   NOT NULL,
    title        VARCHAR(255)   NOT NULL,
    author       VARCHAR(255)   NOT NULL,
    borrower_id  BIGINT,

    CONSTRAINT fk_book_borrower FOREIGN KEY (borrower_id) REFERENCES borrower(id)
);

CREATE INDEX idx_book_isbn ON book(isbn);