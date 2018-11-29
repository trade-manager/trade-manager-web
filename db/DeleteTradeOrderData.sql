-- Clear the tradeOrders from the DB
-- used for back testing

USE tradeprod;;

UPDATE contract SET idTradePosition = null where id > 0;
COMMIT;
DELETE FROM tradeorderfill WHERE id >='0';
COMMIT;
DELETE FROM tradeorder WHERE id >='0';
COMMIT;
DELETE FROM tradeposition WHERE id >='0';
COMMIT;
UPDATE tradestrategy SET status = null WHERE id >='0';
COMMIT;

