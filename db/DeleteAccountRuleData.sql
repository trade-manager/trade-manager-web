-- Clear Accounts and Rules

USE tradeprod;

DELETE FROM portfolioaccount WHERE id >='0';
DELETE FROM account WHERE id >='0';
DELETE FROM rule  WHERE id >='0';
UPDATE portfolio  SET name = 'Paper' WHERE id = 1;
COMMIT;

