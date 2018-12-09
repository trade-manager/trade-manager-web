# Query TO GET trade orders FOR a contract
SELECT tradeorder. *
FROM tradestrategy INNER
  JOIN tradingday ON tradestrategy.id_tradingday = tradingday.id
  INNER JOIN contract ON tradestrategy.id_contract = contract.id
  INNER JOIN strategy ON tradestrategy.idStrategy = strategy.id
  INNER JOIN tradeorder ON tradestrategy.id = tradeorder.id_tradeStrategy
  LEFT OUTER JOIN tradeposition ON tradeorder.id_tradePosition = tradeposition.id
  LEFT OUTER JOIN tradeorderfill ON tradeorder.id = tradeorderfill.id_tradeorder
WHERE contract.symbol = 'IBM'

# Query TO check IF candle DATA IS correct i.e 1min bars & trading time frame bars.
SELECT
contract.symbol,
tradingday.open,
candle.barSize,
COUNT(candle.id)
FROM
candle INNER JOIN contract ON candle.id_contract = contract.id
INNER JOIN tradingday ON candle.id_tradingday = tradingday.id
INNER JOIN tradestrategy ON tradestrategy.id_contract = Contract.id
WHERE
tradestrategy.trade = 1 
AND tradestrategy.id_tradingday = tradingday.id
-- and contract.symbol = 'SPY'
GROUP BY
candle.id_tradingday,
candle.id_contract,
candle.barsize
ORDER BY
tradingday.open DESC,
contract.symbol ASC,
candle.barSize DESC,
candle.startPeriod ASC;

# Query TO check 1min bar AND 5min bar open prices FROM 1 / 1 / 2015
SELECT a.idContract, a.startPeriod, a.open 5minOpen, b.open 1minOpen, b.close 1minClose, b.volume 1minVolume, (
  SELECT close
  FROM candle c
  WHERE c.id = (b.id - 1)) AS preclose
FROM tradeprod.candle a
  INNER JOIN tradeprod.candle b ON a.id_contract = b.id_Contract
WHERE a.id_contract <> 1099 AND a.startPeriod = b.startPeriod AND a.barSize = 300 AND b.barSize = 60 AND b.volume <> 0
AND (a.open > (b.open + 0.01) OR a.open < (b.open - 0.01)) AND a.id_tradingday > 950;