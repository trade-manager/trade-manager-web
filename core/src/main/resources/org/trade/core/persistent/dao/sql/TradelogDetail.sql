select
cast(rand()*1000000000 as unsigned integer) as id_tradelog_detail,
data.sort_col,
if(data.sort_col = 'Total' , 'Total', data.open) as open,
if(data.sort_col = 'Total' , data.symbol, if(data.is_open_position is null,data.symbol ,if(data.is_open_position = 1, data.symbol,  if(data.id_trade_position is null,data.symbol, null))))   as symbol,
data.id_tradestrategy as id_tradestrategy,
if(data.sort_col = 'Total' , null, if(data.is_open_position is null,data.long_sort ,if(data.is_open_position = 1, data.long_sort,  if(data.id_trade_position is null,data.long_sort, null))))  as long_short,
if(data.sort_col = 'Total' , null, if(data.is_open_position is null,data.tier ,if(data.is_open_position = 1, data.tier, if(data.id_trade_position is null, data.tier, null))))  as tier,
if(data.sort_col = 'Total' , null, if(data.is_open_position is null,data.market_bias ,if(data.is_open_position = 1, data.market_bias, if(data.id_trade_position is null, data.market_bias, null)))) as market_bias,
if(data.sort_col = 'Total' , null, if(data.is_open_position is null,data.market_bar ,if(data.is_open_position = 1, data.market_bar, if(data.id_trade_position is null, data.market_bar, null)))) as market_bar,
if(data.sort_col = 'Total' , null, if(data.is_open_position is null,data.name ,if(data.is_open_position = 1, data.name, if(data.id_trade_position is null, data.name, null))))  as name,
if(data.sort_col = 'Total' , null, if(data.is_open_position is null,data.status ,if(data.is_open_position = 1, data.status, if(data.id_trade_position is null, data.status, null))))  as status,
if(data.sort_col = 'Total' , null, if(data.is_open_position is null,data.id_trade_position ,if(data.is_open_position = 1, data.id_trade_position, null)))  as id_trade_position,
if(data.sort_col = 'Total' , null, if(data.is_open_position is null,data.side ,if(data.is_open_position = 1, data.side, if(data.id_trade_position is null, data.side, null))))  as side,
if(data.sort_col = 'Total' , null, data.action) as action,
if(data.sort_col = 'Total' , null, data.stop_price) as stop_price,
if(data.sort_col = 'Total' , null, data.order_status) as order_status,
if(data.sort_col = 'Total' , null, data.filled_date) as filled_date,
cast(data.quantity as signed integer) as quantity,
data.average_filled_price,
data.commission,
if(data.quantity = 0,data.profit_loss,0) as profit_loss
from (select
'A' as sort_col,
date_format(tradingday.open, '%Y/%m/%d') as open,
contract.symbol as symbol,
tradestrategy.id as id_tradestrategy,
tradestrategy.side as long_sort,
tradestrategy.tier as tier,
tradingday.market_bias as market_bias,
tradingday.market_bar as market_bar,
strategy.name as name,
tradestrategy.status as status,
tradeposition.id as id_trade_position,
tradeposition.side as side,
tradeorder.is_open_position  as is_open_position,
tradeorder.action as action,
ifnull(tradeorder.stop_price,0) as stop_price,
tradeorder.status as order_status,
tradeorder.filled_date as filled_date,
((if(tradeorder.action = 'BUY',  1 , -1)) * tradeorder.quantity) as quantity,
ifnull(tradeorder.average_filled_price,0) as average_filled_price,
ifnull(tradeorder.commission,0)  as commission,
ifnull(tradeposition.total_net_value,0) as profit_loss
from
contract
left outer join tradeposition  on contract.id = tradeposition.id_contract
left outer join tradeorder  on tradeposition.id = tradeorder.id_trade_position
inner join tradestrategy on tradestrategy.id = tradeorder.id_tradestrategy
inner join tradingday on tradestrategy.id_trading_day = tradingday.id
inner join strategy on tradestrategy.id_strategy = strategy.id
inner join portfolio on tradestrategy.id_portfolio = portfolio.id
where tradestrategy.trade = 1
and tradeposition.open_quantity = 0
and (0 = :filter and  tradeorder.is_filled = 1)
and (isnull(:symbol) or contract.symbol = :symbol)
and tradeposition.position_close_date between :start and :end
and portfolio.id = :idPortfolio
union all
select
'Total' as sort_col,
date_format(tradingday.open, '%Y/%m/%d') as open,
contract.symbol as symbol,
tradestrategy.id as id_tradestrategy,
tradestrategy.side as long_sort,
tradestrategy.tier as tier,
tradingday.market_bias as market_bias,
tradingday.market_bar as market_bar,
strategy.name as name,
tradestrategy.status as status,
tradeposition.id as id_trade_position,
"" as side,
"" as is_open_position,
"" as action,
"0" as stop_price,
"" as order_status,
null as filled_date,
sum((if( tradeorder.action = 'BUY',  1 , -1)) * (if(tradeorder.is_filled =1, 1, 0))* tradeorder.quantity) as quantity,
(sum((if( tradeorder.action = 'BUY',  -1 , 1))* (if(tradeorder.is_filled =1, 1, 0)) * tradeorder.average_filled_price  * tradeorder.quantity)/sum(((tradeorder.quantity/2)* (if(tradeorder.is_filled =1, 1, 0))))) as average_filled_price,
sum(ifnull(tradeorder.commission,0)) as commission,
(sum((if( tradeorder.action = 'BUY',  -1 , 1))* (if(tradeorder.is_filled =1, 1, 0)) * tradeorder.average_filled_price * tradeorder.quantity) - sum(ifnull(tradeorder.commission,0)))as profit_loss
from
contract
left outer join tradeposition  on contract.id = tradeposition.id_contract
left outer join tradeorder  on tradeposition.id = tradeorder.id_trade_position
inner join tradestrategy on tradestrategy.id = tradeorder.id_tradestrategy
inner join tradingday on tradestrategy.id_trading_day = tradingday.id
inner join strategy on tradestrategy.id_strategy = strategy.id
inner join portfolio on tradestrategy.id_portfolio = portfolio.id
where tradestrategy.trade = 1
and tradeposition.open_quantity = 0
and (0 = :filter and tradeorder.is_filled = 1)
and (isnull(:symbol) or contract.symbol = :symbol)
and tradeposition.position_close_date between :start and :end
and portfolio.id = :idPortfolio
group by
contract.symbol,
tradeposition.id
union all
select
'A' as sort_col,
date_format(tradingday.open, '%Y/%m/%d') as open,
contract.symbol as symbol,
tradestrategy.id as id_tradestrategy,
tradestrategy.side as long_sort,
tradestrategy.tier as tier,
tradingday.market_bias as market_bias,
tradingday.market_bar as market_bar,
strategy.name as name,
tradestrategy.status as status,
tradestrategy.id as id_trade_position,
"" as side,
1 as is_open_position,
"" as action,
"0" as stop_price,
"" as order_status,
null as filled_date,
"0" as quantity,
"0" as average_filled_price,
"0" as commission,
"0" as profit_loss
from
tradestrategy
inner join contract on contract.id = tradestrategy.id_contract
inner join tradingday on tradestrategy.id_trading_day = tradingday.id
inner join strategy on tradestrategy.id_strategy = strategy.id
inner join portfolio on tradestrategy.id_portfolio = portfolio.id
where tradestrategy.trade = 1
and tradestrategy.id not in (select tradeorder.id_tradestrategy
from tradeorder where tradeorder.id_tradestrategy = tradestrategy.id
and tradeorder.is_filled = true)
and (1 = :filter )
and (isnull(:symbol) or contract.symbol = :symbol)
and tradingday.open between :start and :end
and portfolio.id = :idPortfolio
) as data
order by
data.id_trade_position desc,
data.symbol asc,
data.sort_col asc,
data.open asc,
data.is_open_position desc,
data.filled_date asc
