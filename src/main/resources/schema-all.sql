DROP TABLE POINTS IF EXISTS;

CREATE TABLE POINTS (
    id  BIGINT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    activity_date   DATE NOT NULL,
    fidelity_number VARCHAR(7) NOT NULL,
    first_name      VARCHAR(30) NOT NULL,
    last_name       VARCHAR(30) NOT NULL,
    fidelity_code   VARCHAR(7) NOT NULL,
    points          INTEGER NOT NULL,

    UNIQUE  (activity_date, fidelity_number, first_name,last_name,fidelity_code)
)