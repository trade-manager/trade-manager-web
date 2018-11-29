-- DELIMITER //

SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ANSI'//

DROP PROCEDURE IF EXISTS tradeprod.drop_user_if_exists//

CREATE PROCEDURE tradeprod.drop_user_if_exists(IN userName VARCHAR(255))
BEGIN
  DECLARE userCount BIGINT DEFAULT 0;
  SELECT COUNT(*)
  INTO userCount
    FROM mysql.user
      WHERE User = userName;

  IF userCount > 0 THEN
         DROP USER trader@'localhost';
  END IF;
END
//


CALL tradeprod.drop_user_if_exists('trader')//

DROP PROCEDURE IF EXISTS tradeprod.drop_users_if_exists//

CREATE USER trader@'localhost' IDENTIFIED BY '${sql.user_password}'//

GRANT SELECT, UPDATE, INSERT, DELETE ON tradeprod.* TO trader@'localhost' IDENTIFIED BY 'ledzepplin'//

SET SQL_MODE=@OLD_SQL_MODE//