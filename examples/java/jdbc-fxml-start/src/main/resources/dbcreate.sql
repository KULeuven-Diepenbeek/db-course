DROP TABLE IF EXISTS student;
CREATE TABLE student(
    studnr INT NOT NULL PRIMARY KEY,
    naam VARCHAR(200) NOT NULL,
    voornaam VARCHAR(200),
    goedbezig BOOL
);
DROP TABLE IF EXISTS log;
CREATE TABLE log(
    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    date DATETIME DEFAULT CURRENT_TIMESTAMP,
    foreign_id INT NOT NULL,
    msg TEXT
);
INSERT INTO student(studnr, naam, voornaam, goedbezig) VALUES (123, 'Trekhaak', 'Jaak', 0);
INSERT INTO student(studnr, naam, voornaam, goedbezig) VALUES (456, 'Peeters', 'Jos', 0);
INSERT INTO student(studnr, naam, voornaam, goedbezig) VALUES (890, 'Dongmans', 'Ding', 1);