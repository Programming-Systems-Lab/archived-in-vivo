DROP DATABASE invite;
CREATE DATABASE invite;

USE invite;

DROP TABLE IF EXISTS diffvals;
DROP TABLE IF EXISTS diffinfo;
DROP TABLE IF EXISTS trials;

CREATE TABLE trials (
	tid int NOT NULL AUTO_INCREMENT,
	time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	failed int(2) NOT NULL,
	CONSTRAINT errors_pk PRIMARY KEY( tid )
) ENGINE = InnoDB;

CREATE TABLE diffinfo (
	did int NOT NULL AUTO_INCREMENT,
	tid int NOT NULL,
	object_type varchar(60) NOT NULL,
	method_name varchar(120) NOT NULL,
	CONSTRAINT diffinfo_pk PRIMARY KEY( did ),
	CONSTRAINT diffinfo_fk1 FOREIGN KEY( tid ) REFERENCES trials( tid ) ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE TABLE diffvals (
	did int NOT NULL,
	field_name varchar(45) NOT NULL,
	field_value DECIMAL(15,8) NOT NULL,
	field_change DECIMAL(15,8) NOT NULL,
	CONSTRAINT diffvals_pk PRIMARY KEY( did, field_name ),
	CONSTRAINT diffvals_fk1 FOREIGN KEY( did ) REFERENCES diffinfo( did ) ON DELETE CASCADE
) ENGINE = InnoDB;

