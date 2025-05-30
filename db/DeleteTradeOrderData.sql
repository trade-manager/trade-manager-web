-- Clear the tradeOrders from the DB
-- used for back testing

USE tradeprod;

UPDATE contract SET id_trade_position = NULL WHERE id > 0;
COMMIT;
DELETE FROM tradeorderfill WHERE id >= '0';
COMMIT;
DELETE FROM tradeorder WHERE id >= '0';
COMMIT;
DELETE FROM tradeposition WHERE id >= '0';
COMMIT;
UPDATE tradestrategy SET STATUS = NULL WHERE id >= '0';
COMMIT;

