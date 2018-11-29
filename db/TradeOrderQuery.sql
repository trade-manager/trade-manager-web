# Query to get trade orders for a contract
SELECT tradeorder.*
FROM 
tradestrategy inner join tradingday on tradestrategy.id_tradingday = tradingday.id
inner join contract on tradestrategy.id_contract = contract.id
inner join strategy on tradestrategy.idStrategy = strategy.id
inner join tradeorder  on tradestrategy.id = tradeorder.id_tradeStrategy
left outer join tradeposition  on tradeorder.id_tradePosition = tradeposition.id
left outer join tradeorderfill on tradeorder.id = tradeorderfill.id_tradeorder
where contract.symbol = 'IBM'

# Query to check if candle data is correct i.e 1min bars & trading time frame bars.
select 
contract.symbol,
tradingday.open,
candle.barSize,
count(candle.id)
from 
candle inner join contract on candle.id_contract = contract.id
inner join tradingday on candle.id_tradingday = tradingday.id
inner join tradestrategy on tradestrategy.id_contract = Contract.id
where 
tradestrategy.trade = 1 
and tradestrategy.id_tradingday = tradingday.id
-- and contract.symbol = 'SPY'
group by
candle.id_tradingday,
candle.id_contract,
candle.barsize
order by
tradingday.open desc,
contract.symbol asc,
candle.barSize desc,
candle.startPeriod asc;

# Query to check 1min bar and 5min bar open prices from 1/1/2015
SELECT 
a.idContract,
a.startPeriod,
a.open 5minOpen,
b.open 1minOpen,
b.close 1minClose,
b.volume 1minVolume,
(select close from candle c where c.id = (b.id -1)) as preclose
FROM 
tradeprod.candle a inner join tradeprod.candle b on a.id_contract = b.id_Contract
where
a.id_contract <>1099
and a.startPeriod = b.startPeriod
and a.barSize = 300 
and b.barSize = 60
and b.volume <> 0 
and ( a.open > (b.open + 0.01) or a.open < (b.open -0.01))
and a.id_tradingday > 950;