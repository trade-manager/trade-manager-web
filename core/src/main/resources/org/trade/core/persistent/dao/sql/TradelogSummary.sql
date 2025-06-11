select
cast(rand()*1000000000 as unsigned integer) as id_tradelog_summary,
dataAll.period as period,
(dataAll.win_count/ (dataAll.win_count  + dataAll.loss_count)) as batting_average,
((dataAll.profit_amount/ dataAll.win_count)/((dataAll.loss_amount*-1)/dataAll.loss_count))  as simple_sharpe_ratio,
cast(dataAll.quantity as signed integer) as quantity,
dataAll.commission as commission,
(dataAll.profit_amount + dataAll.loss_amount) as gross_profit_loss,
(dataAll.profit_amount + dataAll.loss_amount - dataAll.commission) as net_profit_loss,
dataAll.profit_amount as profit_amount,
dataAll.loss_amount as loss_amount,
cast(dataAll.win_count as signed integer)  as win_count,
cast(dataAll.loss_count as signed integer)  as loss_count,
cast(dataAll.position_count as signed integer)  as position_count,
cast(dataAll.tradestrategy_count as signed integer)  as tradestrategy_count
from (select
dataC.period as period,
sum(dataC.quantity) as quantity,
sum(dataC.commission) as commission,
sum(dataC.profit_amount) as profit_amount,
sum(dataC.loss_amount) as loss_amount,
sum(dataC.win_count) as win_count,
sum(dataC.loss_count) as loss_count,
sum(dataC.position_count) as position_count,
sum(dataC.tradestrategy_count) as tradestrategy_count
from(select
'Total' as period,
sum(dataA.quantity) as quantity,
sum(dataA.commission) as commission,
sum(dataA.profit_amount) as profit_amount,
sum(dataA.loss_amount) as loss_amount,
sum(dataA.win_count) as win_count,
sum(dataA.loss_count) as loss_count,
sum(dataA.position_count) as position_count,
sum(dataA.tradestrategy_count) as tradestrategy_count
from (select
dataD.period as period,
dataD.quantity_total as quantity,
dataD.commission as commission,
if(dataD.quantity = 0 , dataD.profit_amount, 0) as profit_amount,
if(dataD.quantity = 0 , dataD.loss_amount, 0) as loss_amount,
if(dataD.quantity = 0 , dataD.win_count, 0) as win_count,
if(dataD.quantity = 0 , dataD.loss_count, 0) as loss_count,
dataD.position_count as position_count,
dataD.tradestrategy_count as tradestrategy_count
from(select
date_format(tradeposition.position_close_date , '%Y/%m') as period,
contract.symbol,
tradeposition.id,
sum(ifnull(tradeorder.quantity,0))  as quantity_total,
sum((if(tradeorder.action = 'BUY',  1 , -1)) * ifnull(tradeorder.quantity,0))  as quantity,
sum(ifnull(tradeorder.commission,0)) as commission,
if(sum(((if(tradeorder.action = 'BUY',  -1 , 1))  * tradeorder.quantity * tradeorder.average_filled_price)) > 0, sum(((if( tradeorder.action = 'BUY',  -1 , 1))  * tradeorder.quantity * tradeorder.average_filled_price)), 0)	as profit_amount,
if(sum(((if(tradeorder.action = 'BUY',  -1 , 1))  * tradeorder.quantity * tradeorder.average_filled_price)) < 0, sum(((if( tradeorder.action = 'BUY',  -1 , 1))  * tradeorder.quantity * tradeorder.average_filled_price)), 0)	as loss_amount,
(if((:winLossAmount) < (sum(((if( tradeorder.action = 'BUY',  -1 , 1))  * tradeorder.quantity * tradeorder.average_filled_price))), 1 ,0 )) as win_count,
(if((-1*:winLossAmount) >= (sum(((if( tradeorder.action = 'BUY',  -1 , 1))  * tradeorder.quantity * tradeorder.average_filled_price))), 1 ,0 )) as loss_count,
if(ifnull(tradeposition.id,0),1, 0) as position_count,
0 as tradestrategy_count
from contract
left outer join tradeposition  on contract.id = tradeposition.contract_id
left outer join tradeorder  on tradeposition.id = tradeorder.trade_position_id
inner join tradestrategy on tradestrategy.id = tradeorder.tradestrategy_id
inner join portfolio on tradestrategy.portfolio_id = portfolio.id
where tradeorder.is_filled =1
and tradeposition.open_quantity = 0
and tradestrategy.trade = 1
and (isnull(:symbol) or contract.symbol = :symbol)
and tradeposition.position_close_date between :start and :end
and portfolio.id = :idPortfolio
group by
period,
contract.symbol,
tradeposition.id
union all
select
date_format(tradingday.open , '%Y/%m') as period,
contract.symbol,
0 as trade_position_id,
0 as quantity_total,
0 as quantity,
0 as commission,
0 as profit_amount,
0 as loss_amount,
0 as win_count,
0 as loss_count,
0 as position_count,
if(ifnull(tradestrategy.id,0),1, 0)  as tradestrategy_count
from tradestrategy
inner join contract  on contract.id = tradestrategy.contract_id
inner join tradingday  on tradingday.id = tradestrategy.tradingday_id
inner join portfolio on tradestrategy.portfolio_id = portfolio.id
where 
tradingday.open between :start and :end
and (isnull(:symbol) or contract.symbol = :symbol)
and (portfolio.id = :idPortfolio or portfolio.id is null)
group by
period,
contract.symbol,
tradestrategy.id) dataD) dataA
group by dataA.period) dataC
group by
dataC.period
union all
select
dataM.period as period,
sum(dataM.quantity) as quantity,
sum(dataM.commission) as commission,
sum(dataM.profit_amount) as profit_amount,
sum(dataM.loss_amount) as loss_amount,
sum(dataM.win_count) as win_count,
sum(dataM.loss_count) as loss_count,
sum(dataM.position_count) as position_count,
sum(dataM.tradestrategy_count) as tradestrategy_count
from (select
dataD.period as period,
dataD.quantity_total as quantity,
dataD.commission as commission,
if(dataD.quantity = 0 , dataD.profit_amount, 0) as profit_amount,
if(dataD.quantity = 0 , dataD.loss_amount, 0) as loss_amount,
if(dataD.quantity = 0 , dataD.win_count, 0) as win_count,
if(dataD.quantity = 0 , dataD.loss_count, 0) as loss_count,
dataD.position_count as position_count,
dataD.tradestrategy_count as tradestrategy_count
from(select
date_format(tradeposition.position_close_date , '%Y/%m') as period,
contract.symbol,
tradeposition.id,
sum(ifnull(tradeorder.quantity,0))  as quantity_total,
sum((if( tradeorder.action = 'BUY',  1 , -1)) * ifnull(tradeorder.quantity,0))  as quantity,
sum(ifnull(tradeorder.commission,0)) as commission,
if(sum(((if(tradeorder.action = 'BUY',  -1 , 1))  * tradeorder.quantity * tradeorder.average_filled_price)) > 0, sum(((if( tradeorder.action = 'BUY',  -1 , 1))  * tradeorder.quantity * tradeorder.average_filled_price)), 0)	as profit_amount,
if(sum(((if(tradeorder.action = 'BUY',  -1 , 1))  * tradeorder.quantity * tradeorder.average_filled_price)) < 0, sum(((if( tradeorder.action = 'BUY',  -1 , 1))  * tradeorder.quantity * tradeorder.average_filled_price)), 0)	as loss_amount,
(if((:winLossAmount) < (sum(((if( tradeorder.action = 'BUY',  -1 , 1))  * tradeorder.quantity * tradeorder.average_filled_price))), 1 ,0 )) as win_count,
(if((-1*:winLossAmount) >= (sum(((if(tradeorder.action = 'BUY',  -1 , 1))  * tradeorder.quantity * tradeorder.average_filled_price))), 1 ,0 )) as loss_count,
if(ifnull(tradeposition.id,0),1, 0) as position_count,
0 as tradestrategy_count
from contract
left outer join tradeposition  on contract.id = tradeposition.contract_id
left outer join tradeorder  on tradeposition.id = tradeorder.trade_position_id
inner join tradestrategy on tradestrategy.id = tradeorder.tradestrategy_id
inner join portfolio on tradestrategy.portfolio_id = portfolio.id
where tradeorder.is_filled =1
and tradeposition.open_quantity = 0
and tradestrategy.trade = 1
and (isnull(:symbol) or contract.symbol = :symbol)
and tradeposition.position_close_date between :start and :end
and portfolio.id = :idPortfolio
group by
period,
contract.symbol,
tradeposition.id
union all
select date_format(tradingday.open , '%Y/%m') as period,
contract.symbol,
0 as trade_position_id,
0 as quantity_total,
0 as quantity,
0 as commission,
0 as profit_amount,
0 as loss_amount,
0 as win_count,
0 as loss_count,
0 as position_count,
if(ifnull(tradestrategy.id,0),1, 0)  as tradestrategy_count
from tradestrategy
inner join contract  on contract.id = tradestrategy.contract_id
inner join tradingday  on tradingday.id = tradestrategy.tradingday_id
inner join portfolio on tradestrategy.portfolio_id = portfolio.id
where 
tradingday.open between :start and :end
and (isnull(:symbol) or contract.symbol = :symbol)
and (portfolio.id = :idPortfolio or portfolio.id is null)
group by
period,
contract.symbol,
tradestrategy.id) dataD) dataM
group by dataM.period) dataAll
order by dataAll.period desc
