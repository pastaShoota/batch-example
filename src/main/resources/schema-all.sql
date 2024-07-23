DROP TABLE POINTS_TRANSIENT IF EXISTS;
DROP TABLE POSTINGS IF EXISTS;
DROP TABLE POINTS IF EXISTS;

-- Temporary table to host records posted to ATN
CREATE TABLE POINTS_TRANSIENT (
    id  BIGINT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    activity_date   DATE NOT NULL,
    fidelity_number VARCHAR(7) NOT NULL,
    first_name      VARCHAR(30) NOT NULL,
    last_name       VARCHAR(30) NOT NULL,
    fidelity_code   VARCHAR(7) NOT NULL,
    points          INTEGER NOT NULL
    -- duplicates allowed
);
-- Main table hosting general data of a POSTING file
CREATE TABLE POSTINGS (
    id  BIGINT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    filename        VARCHAR(50),
    start_processed DATETIME,
    end_processed   DATETIME,
    status          ENUM('STARTED','DONE') DEFAULT 'STARTED'
);
-- Main table for hosting points and their lifecycle
CREATE TABLE POINTS (
    id  BIGINT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    activity_date   DATE NOT NULL,
    fidelity_number VARCHAR(7) NOT NULL,
    first_name      VARCHAR(30) NOT NULL,
    last_name       VARCHAR(30) NOT NULL,
    fidelity_code   VARCHAR(7) NOT NULL,
    points          INTEGER NOT NULL,
    status          ENUM('SENT','ACKNOWLEDGED','REJECTED','RESENT','REGULARIZED_OK','REGULARIZED_KO') NOT NULL DEFAULT 'SENT',
    -- FK
    posting_id      BIGINT NOT NULL,
    FOREIGN KEY (posting_id) REFERENCES POSTINGS(id),
    -- no duplicates allowed
    UNIQUE  (activity_date, fidelity_number, first_name,last_name,fidelity_code)
);