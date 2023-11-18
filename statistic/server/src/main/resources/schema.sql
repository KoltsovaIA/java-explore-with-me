CREATE TABLE IF NOT EXISTS hit
(
    id        BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    app       VARCHAR(512)                            NOT NULL,
    uri       VARCHAR(512)                            NOT NULL,
    ip        VARCHAR(22)                             NOT NULL,
    timestamp TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    CONSTRAINT PK_HIT PRIMARY KEY (id)
);