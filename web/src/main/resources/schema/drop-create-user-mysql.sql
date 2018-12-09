-- DELIMITER //

SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ANSI' //

DROP PROCEDURE IF EXISTS @db-dba.SCHEMA@.drop_user_if_exists  //

CREATE PROCEDURE @db-dba.SCHEMA@.drop_user_if_exists(IN userName VARCHAR(255))
BEGIN
  DECLARE userCount BIGINT DEFAULT 0;
  SELECT COUNT(*)
  INTO userCount
    FROM mysql.USER
      WHERE USER = userName;

  IF userCount > 0 THEN
         DROP USER @db-USER.username@@'localhost';
  END IF;END / /


CALL @db - dba.SCHEMA @.drop_user_if_exists('@db-user.username@')/ /

DROP PROCEDURE IF EXISTS @db - dba.SCHEMA @.drop_users_if_exists / /

CREATE USER @db - USER.username@@'localhost' IDENTIFIED BY '@db-user.password@' / /

GRANT SELECT, UPDATE, INSERT, DELETE ON @db - dba.SCHEMA @. * TO @db - USER.username@@'localhost' IDENTIFIED BY
'@db-user.password@' / /

SET SQL_MODE = @OLD_SQL_MODE / /