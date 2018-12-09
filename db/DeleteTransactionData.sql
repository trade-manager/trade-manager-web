-- Clear the all transaction data from the DB

USE tradeprod;;

UPDATE contract SET id = NULL WHERE id > 0;
COMMIT;DELETE FROM tradeorderfill WHERE id >= '0';COMMIT;DELETE FROM tradeorder WHERE id >= '0';COMMIT;DELETE FROM
tradeposition WHERE id >= '0';COMMIT;DELETE FROM tradestrategy WHERE id >= '0';COMMIT;DELETE FROM candle WHERE id >=
'0';COMMIT;DELETE FROM tradingday WHERE id >= '0';COMMIT;DELETE FROM contract WHERE id >= '0';COMMIT;

