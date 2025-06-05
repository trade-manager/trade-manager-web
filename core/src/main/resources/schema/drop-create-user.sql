-- DELIMITER //

SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ANSI' //

DROP PROCEDURE IF EXISTS @db-dba.schema@.drop_user_if_exists  //

CREATE PROCEDURE @db-dba.schema@.drop_user_if_exists(IN userName VARCHAR(255))
BEGIN
  DECLARE userCount BIGINT DEFAULT 0;
  SELECT COUNT(*)
  INTO userCount
    FROM mysql.user
      WHERE User = userName;

  IF userCount > 0 THEN
         DROP USER @db-user.username@@'localhost';
  END IF;
END//


CALL @db-dba.schema@.drop_user_if_exists('@db-user.username@') //

DROP PROCEDURE IF EXISTS @db-dba.schema@.drop_users_if_exists //

CREATE USER @db-user.username@@'localhost' IDENTIFIED BY
'@db-user.password@' //

GRANT SELECT, UPDATE, INSERT, DELETE ON @db-dba.schema@.* TO @db-user.username@@'localhost' IDENTIFIED BY
'@db-user.password@' //

SET SQL_MODE=@OLD_SQL_MODE //
