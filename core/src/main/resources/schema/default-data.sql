-- Clear the data base

-- DELIMITER //

USE @db-dba.schema@//

DELETE FROM tradeorderfill WHERE id >='0'//
COMMIT//
DELETE FROM tradeorder WHERE id>='0'//
COMMIT//
UPDATE contract SET id = null where id > 0//
COMMIT//
DELETE FROM tradeposition WHERE id >='0'//
COMMIT//
DELETE FROM candle WHERE id >='0'//
COMMIT//
DELETE FROM tradestrategy WHERE id >='0'//
COMMIT//
DELETE FROM rule WHERE id >='0'//
COMMIT//
DELETE FROM codevalue WHERE id >='0'//
COMMIT//
DELETE FROM portfolioaccount WHERE id >='0'//
COMMIT//
DELETE FROM portfolio WHERE id >='0'//
COMMIT//
DELETE FROM account WHERE id >='0'//
COMMIT//
DELETE FROM indicatorseries WHERE id >='0'//
COMMIT//
DELETE FROM strategy WHERE id >='0'//
COMMIT//
DELETE FROM strategy WHERE id >='0'//
COMMIT//
DELETE FROM contract WHERE id >='0'//
COMMIT//
DELETE FROM tradingday WHERE id >='0'//
COMMIT//
DELETE FROM entrylimit WHERE id >='0'//
COMMIT//
DELETE FROM codeattribute WHERE id >='0'//
COMMIT//
DELETE FROM codetype WHERE id >='0'//
COMMIT//
DELETE FROM employee WHERE id >='0'//
COMMIT//
DELETE FROM user WHERE id >='0'//
COMMIT//
DELETE FROM domain WHERE id >='0'//
COMMIT//

INSERT INTO strategy (id, name, description, market_data, class_name) VALUES (20, 'FHxRBHyR+Heikin', 'Sell front/back half at x/yR or trail BH with Heikin-Ashi bars over xR', 1, 'PosMgrFHXRBHHeikinStrategy')//
INSERT INTO strategy (id, name, description, class_name) VALUES (21, 'AllOrNothing', 'Close open position at 15:58 with stop 1R', 'PosMgrAllOrNothingStrategy')//
INSERT INTO strategy (id, name, description, class_name) VALUES (22, 'All5MinBar', 'Trails whole pos on 5min bars after 9:40', 'PosMgrAll5MinBarStrategy')//
INSERT INTO strategy (id, name, description, class_name) VALUES (23, 'FHxRBHyR', 'Sell front half at xR and back half at yR', 'PosMgrFHXRBHYRStrategy')//
INSERT INTO strategy (id, name, description, market_data, class_name, strategy_manager_id) VALUES (1, '5MinGapBar', 'Enter a tier 1-3 gap in first 5min bar direction, and stop @ 5min high/low',1, 'FiveMinGapBarStrategy',23)//
INSERT INTO strategy (id, name, description, market_data, class_name, strategy_manager_id) VALUES (2, '5MinSideGapBar', 'Enter a tier 1-3 gap via expectd Side after first 5min bar and stop @ 5min high/low',1, 'FiveMinSideGapBarStrategy',23)//
INSERT INTO strategy (id, name, description, market_data, class_name, strategy_manager_id) VALUES (3, '5MinWRBGapBar', 'Enter a tier 1-3 gap in first 5min WRB bar direction, and stop @ 55% of high/low',1, 'FiveMinWRBGapBarStrategy',22)//
INSERT INTO strategy (id, name, description, market_data, class_name, strategy_manager_id) VALUES (4, 'HeikinAshiTrailPosMgr', 'Get and trail an open position on the current time frame using Hiekin-Ashi bars',1, 'PosMgrHeikinAshiTrailStrategy',null)//
COMMIT//

INSERT INTO codetype (id, name, type, category, description) VALUES(1,'MovingAverage','IndicatorParameters','IndicatorParameters','Moving Average')//
INSERT INTO codetype (id, name, type, category, description) VALUES(2,'Pivot','IndicatorParameters','IndicatorParameters','Pivot points')//
INSERT INTO codetype (id, name, type, category, description) VALUES(3,'Candle','IndicatorParameters','IndicatorParameters','Contract to be followed')//
INSERT INTO codetype (id, name, type, category, description) VALUES(4,'AverageTrueRange','IndicatorParameters','IndicatorParameters','Average True Range')//
INSERT INTO codetype (id, name, type, category, description) VALUES(5,'RelativeStrengthIndex','IndicatorParameters','IndicatorParameters','Relative Strength Index')//
INSERT INTO codetype (id, name, type, category, description) VALUES(6,'CommodityChannelIndex','IndicatorParameters','IndicatorParameters','Commodity Channel Index')//
INSERT INTO codetype (id, name, type, category, description) VALUES(7,'BollingerBands','IndicatorParameters','IndicatorParameters','Bollinger Bands')//
INSERT INTO codetype (id, name, type, category, description) VALUES(8,'StochasticOscillator','IndicatorParameters','IndicatorParameters','Stochastic Oscillator')//
INSERT INTO codetype (id, name, type, category, description) VALUES(9,'MoneyFlowIndex','IndicatorParameters','IndicatorParameters','Money Flow Index')//
INSERT INTO codetype (id, name, type, category, description) VALUES(10,'MACD','IndicatorParameters','IndicatorParameters','MACD')//
INSERT INTO codetype (id, name, type, category, description) VALUES(11,'Vostro','IndicatorParameters','IndicatorParameters','Vostro Indicator')//
COMMIT//

INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(1,'Length','The length of the Moving Average','10','java.lang.Integer',null, 1)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(2,'MAType','Type of the Moving Average','LINEAR','java.lang.String', 'org.trade.core.valuetype.CalculationType',1)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(3,'Side','Use candle direct for V','false','java.lang.Boolean','org.trade.core.valuetype.YesNo', 2)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(4,'Quadratic','Use quadratic calc for pivot','true','java.lang.Boolean','org.trade.core.valuetype.YesNo', 2)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(5,'Bars','Number of bars to use for pivot 5 or 7','5','java.lang.Integer', null,2)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(6,'Symbol','The contract symbol','SPY','java.lang.String', null,3)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(7,'Currency','The contract currency','USD','java.lang.String', 'org.trade.core.valuetype.Currency',3)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(8,'Exchange','The contract exchange','SMART','java.lang.String', 'org.trade.core.valuetype.Exchange',3)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(9,'SECType','The contract SECType','STK','java.lang.String', 'org.trade.core.valuetype.SECType',3)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(10,'Length','The length of the Average True Range','14','java.lang.Integer',null, 4)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(11,'RollingCandle','Use rolling candle values','false','java.lang.Boolean','org.trade.core.valuetype.YesNo', 4)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(12,'Length','The length of the Relative Strength Index','14','java.lang.Integer',null, 5)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(13,'RollingCandle','Use rolling candle values','false','java.lang.Boolean','org.trade.core.valuetype.YesNo', 5)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(14,'Length','The length of the Commodity Channel Index','20','java.lang.Integer',null, 6)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(15,'RollingCandle','Use rolling candle values','false','java.lang.Boolean','org.trade.core.valuetype.YesNo', 6)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(16,'Length','The length of the Moving Average','20','java.lang.Integer',null,7)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(17,'NumberOfSTD','Number of STDs','2.0','java.math.BigDecimal', null,7)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(18,'Length','The length of the %K','14','java.lang.Integer',null, 8)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(19,'KSmoothing','The smoothing of the %K','1','java.lang.Integer',null, 8)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(20,'PercentD','The SMA of the %D','3','java.lang.Integer',null, 8)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(21,'Inverse','Stochastic or Percent R','false','java.lang.Boolean','org.trade.core.valuetype.YesNo', 8)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(22,'Length','The length of the MFI','14','java.lang.Integer',null, 9)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(23,'RollingCandle','Use rolling candle values','false','java.lang.Boolean','org.trade.core.valuetype.YesNo', 9)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(24,'Fast Length','The fast length of the EMA','12','java.lang.Integer',null, 10)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(25,'Slow Length','The slow length of the EMA','26','java.lang.Integer',null, 10)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(26,'Signal Smoothing','The EMA length of the MACD','9','java.lang.Integer',null, 10)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(27,'Simple Smoothing MA','Use SMA for signal line smoothing','true','java.lang.Boolean','org.trade.core.valuetype.YesNo', 10)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(28,'Length','The length of the Moving Average','100','java.lang.Integer',null, 11)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(29,'MAType','Type of the Moving Average','WEIGHTED','java.lang.String', 'org.trade.core.valuetype.CalculationType',11)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(30,'Vostro Period','The number of periods for Vostro calc','5','java.lang.Integer',null, 11)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(31,'Vostro Range','The range +/- to indicate a Vostro','8.0','java.math.BigDecimal',null, 11)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(32,'Price Source','Price source used for calculations','5','java.lang.Integer', 'org.trade.core.valuetype.PriceSource',11)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(33,'Price Source','Price source used for calculations','1','java.lang.Integer', 'org.trade.core.valuetype.PriceSource',1)//
COMMIT//

INSERT INTO indicatorseries (id, name, type, description, display_series, series_RGB_color, sub_chart, strategy_id) VALUES(1,'SMA-20','MovingAverageSeries','Simple 20 period Moving Average',1,-52429,0,1)//
INSERT INTO indicatorseries (id, name, type, description, display_series, series_RGB_color, sub_chart, strategy_id) VALUES(2,'SMA-8','MovingAverageSeries','Simple 8 Period Moving Average',1,-16711681,0,1)//
INSERT INTO indicatorseries (id, name, type, description, display_series, series_RGB_color, sub_chart, strategy_id) VALUES(3,'Vwap','VwapSeries','Volume Weighted Moving Average',1,0,0,1)//
INSERT INTO indicatorseries (id, name, type, description, display_series, series_RGB_color, sub_chart, strategy_id) VALUES(4,'Pivot','PivotSeries','5 Bar Pivots',1,0,0,1)//
INSERT INTO indicatorseries (id, name, type, description, display_series, series_RGB_color, sub_chart, strategy_id) VALUES(5,'HeikinAshi','HeikinAshiSeries','HeikinAshi bars used for trail stops',0,0,0,1)//
INSERT INTO indicatorseries (id, name, type, description, display_series, series_RGB_color, sub_chart, strategy_id) VALUES(6,'S&P500','CandleSeries','S&P 500',1,-16738048,0,1)//
INSERT INTO indicatorseries (id, name, type, description, display_series, series_RGB_color, sub_chart, strategy_id) VALUES(7,'Volume','VolumeSeries','Volume',1,1,1,1)//

INSERT INTO indicatorseries (id, name, type, description, display_series, series_RGB_color, sub_chart, strategy_id) VALUES(8,'SMA-20','MovingAverageSeries','Simple 20 period Moving Average',1,-52429,0,2)//
INSERT INTO indicatorseries (id, name, type, description, display_series, series_RGB_color, sub_chart, strategy_id) VALUES(9,'SMA-8','MovingAverageSeries','Simple 8 Period Moving Average',1,-16711681,0,2)//
INSERT INTO indicatorseries (id, name, type, description, display_series, series_RGB_color, sub_chart, strategy_id) VALUES(10,'Vwap','VwapSeries','Volume Weighted Moving Average',1,0,0,2)//
INSERT INTO indicatorseries (id, name, type, description, display_series, series_RGB_color, sub_chart, strategy_id) VALUES(11,'Pivot','PivotSeries','5 Bar Pivots',1,0,0,2)//
INSERT INTO indicatorseries (id, name, type, description, display_series, series_RGB_color, sub_chart, strategy_id) VALUES(12,'HeikinAshi','HeikinAshiSeries','HeikinAshi bars used for trail stops',0,0,0,2)//
INSERT INTO indicatorseries (id, name, type, description, display_series, series_RGB_color, sub_chart, strategy_id) VALUES(13,'Volume','VolumeSeries','Volume',1,1,1,2)//

INSERT INTO indicatorseries (id, name, type, description, display_series, series_RGB_color, sub_chart, strategy_id) VALUES(14,'SMA-20','MovingAverageSeries','Simple 20 period Moving Average',1,-52429,0,3)//
INSERT INTO indicatorseries (id, name, type, description, display_series, series_RGB_color, sub_chart, strategy_id) VALUES(15,'SMA-8','MovingAverageSeries','Simple 8 Period Moving Average',1,-16711681,0,3)//
INSERT INTO indicatorseries (id, name, type, description, display_series, series_RGB_color, sub_chart, strategy_id) VALUES(16,'Vwap','VwapSeries','Volume Weighted Moving Average',1,0,0,3)//
INSERT INTO indicatorseries (id, name, type, description, display_series, series_RGB_color, sub_chart, strategy_id) VALUES(17,'Pivot','PivotSeries','5 Bar Pivots',1,0,0,3)//
INSERT INTO indicatorseries (id, name, type, description, display_series, series_RGB_color, sub_chart, strategy_id) VALUES(18,'Volume','VolumeSeries','Volume',1,1,1,3)//

INSERT INTO indicatorseries (id, name, type, description, display_series, series_RGB_color, sub_chart, strategy_id) VALUES(19,'SMA-20','MovingAverageSeries','Simple 20 period Moving Average',1,-52429,0,4)//
INSERT INTO indicatorseries (id, name, type, description, display_series, series_RGB_color, sub_chart, strategy_id) VALUES(20,'SMA-8','MovingAverageSeries','Simple 8 Period Moving Average',1,-16711681,0,4)//
INSERT INTO indicatorseries (id, name, type, description, display_series, series_RGB_color, sub_chart, strategy_id) VALUES(21,'Vwap','VwapSeries','Volume Weighted Moving Average',1,0,0,4)//
INSERT INTO indicatorseries (id, name, type, description, display_series, series_RGB_color, sub_chart, strategy_id) VALUES(22,'Pivot','PivotSeries','5 Bar Pivots',1,0,0,4)//
INSERT INTO indicatorseries (id, name, type, description, display_series, series_RGB_color, sub_chart, strategy_id) VALUES(23,'HeikinAshi','HeikinAshiSeries','HeikinAshi bars used for trail stops',0,0,0,4)//
INSERT INTO indicatorseries (id, name, type, description, display_series, series_RGB_color, sub_chart, strategy_id) VALUES(24,'Volume','VolumeSeries','Volume',1,1,1,4)//

INSERT INTO indicatorseries (id, name, type, description, display_series, series_RGB_color, sub_chart, strategy_id) VALUES(25,'SMA-20','MovingAverageSeries','Simple 20 period Moving Average',1,-52429,0,20)//
INSERT INTO indicatorseries (id, name, type, description, display_series, series_RGB_color, sub_chart, strategy_id) VALUES(26,'SMA-8','MovingAverageSeries','Simple 8 Period Moving Average',1,-16711681,0,20)//
INSERT INTO indicatorseries (id, name, type, description, display_series, series_RGB_color, sub_chart, strategy_id) VALUES(27,'Vwap','VwapSeries','Volume Weighted Moving Average',1,0,0,20)//
INSERT INTO indicatorseries (id, name, type, description, display_series, series_RGB_color, sub_chart, strategy_id) VALUES(28,'Pivot','PivotSeries','5 Bar Pivots',1,0,0,20)//
INSERT INTO indicatorseries (id, name, type, description, display_series, series_RGB_color, sub_chart, strategy_id) VALUES(29,'HeikinAshi','HeikinAshiSeries','HeikinAshi bars used for trail stops',0,0,0,20)//
INSERT INTO indicatorseries (id, name, type, description, display_series, series_RGB_color, sub_chart, strategy_id) VALUES(30,'Volume','VolumeSeries','Volume',1,1,1,20)//
COMMIT//

INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(1,'20',1,1)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(2,'LINEAR',2,1)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(3,'8',1,2)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(4,'LINEAR',2,2)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(5,'20',1,8)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(6,'LINEAR',2,8)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(7,'8',1,9)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(8,'LINEAR',2,9)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(9,'false',3,4)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(10,'true',4,4)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(11,'5',5,4)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(12,'false',3,11)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(13,'true',4,11)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(14,'5',5,11)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(15,'20',1,14)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(16,'LINEAR',2,14)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(17,'8',1,15)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(18,'LINEAR',2,15)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(19,'false',3,17)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(20,'true',4,17)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(21,'5',5,17)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(22,'SPY',6,6)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(23,'USD',7,6)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(24,'SMART',8,6)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(25,'STK',9,6)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(26,'1',33,1)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(27,'1',33,2)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(28,'1',33,8)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(29,'1',33,9)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(30,'1',33,14)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(31,'1',33,15)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(32,'20',1,19)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(33,'LINEAR',2,19)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(34,'1',33,19)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(35,'8',1,20)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(36,'LINEAR',2,20)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(37,'1',33,20)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(38,'false',3,22)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(39,'true',4,22)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(40,'5',5,22)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(41,'20',1,25)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(42,'LINEAR',2,25)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(43,'1',33,25)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(44,'8',1,26)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(45,'LINEAR',2,26)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(46,'1',33,26)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(47,'false',3,28)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(48,'true',4,28)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(49,'5',5,28)//
COMMIT//

INSERT INTO entrylimit (id,start_price,end_price,limit_amount, percent_of_price, percent_of_margin, share_round, pivot_range, price_round) VALUES (1,'0','8','0.02','0.06','0','100','0.05', '0.05')//
INSERT INTO entrylimit (id,start_price,end_price,limit_amount, percent_of_price, percent_of_margin, share_round, pivot_range, price_round) VALUES (2,'8.01','15','0.02', '0.05','0','100', '0.05', '0.05')//
INSERT INTO entrylimit (id,start_price,end_price,limit_amount, percent_of_price, percent_of_margin, share_round, pivot_range, price_round) VALUES (3,'15.01','30','0.03', '0.03','0','100', '0.05', '0.05')//
INSERT INTO entrylimit (id,start_price,end_price,limit_amount, percent_of_price, percent_of_margin, share_round, pivot_range, price_round) VALUES (4,'30.01','50','0.04', '0.02','0', '50', '0.07', '0.07')//
INSERT INTO entrylimit (id,start_price,end_price,limit_amount, percent_of_price, percent_of_margin, share_round, pivot_range, price_round) VALUES (5,'50.01','80','0.6','0.02','0','20', '0.15', '0.15')//
INSERT INTO entrylimit (id,start_price,end_price,limit_amount, percent_of_price, percent_of_margin, share_round, pivot_range, price_round) VALUES (6,'80.01','140','0.08','0.02','0','20', '0.20', '0.20')//
INSERT INTO entrylimit (id,start_price,end_price,limit_amount, percent_of_price, percent_of_margin, share_round, pivot_range, price_round) VALUES (7,'140.01','300','0.15','0.02','0','10', '0.25', '0.25')//
INSERT INTO entrylimit (id,start_price,end_price,limit_amount, percent_of_price, percent_of_margin, share_round, pivot_range, price_round) VALUES (8,'300.01','1000','0.15','0.02','0','10', '0.25', '0.30')//
INSERT INTO entrylimit (id,start_price,end_price,limit_amount, percent_of_price, percent_of_margin, share_round, pivot_range, price_round) VALUES (9,'1000.01','3000','0.30','0.02','0','10', '0.5', '0.50')//
COMMIT//

INSERT INTO portfolio (id, name, alias, description, is_default) VALUES (1, 'Paper', 'Paper Account','Paper trading account', 1)//
COMMIT//

INSERT INTO domain (id, name, description) VALUES (1, 'global', 'global domain accessible to all')//
COMMIT//

INSERT INTO role (id, name, description) VALUES (1, 'ADMIN', 'ADMIN role')//
COMMIT//

INSERT INTO role (id, name, description, contained_role_id) VALUES (2, 'MANAGER', 'MANAGER role', 1)//
COMMIT//

INSERT INTO role (id, name, description, contained_role_id) VALUES (3, 'USER', 'USER role', 2)//
COMMIT//

INSERT INTO user (id, name, first_name, last_name, user_name, email, password, domain) VALUES (1, 'Admin', 'admin', 'admin', 'admin','admin@global.com', '{bcrypt}$2a$10$iJGTqOGkIacvFfaTQBfcjuFj3iIH7VlmMWzLk.Ohbon3tf3p8aGB.', 1)//
COMMIT//

INSERT INTO userrole (id, user_id, role_id) VALUES (1, 1, 1)//
COMMIT//

INSERT INTO codetype (id, name, type, category, description) VALUES(12,'UIComponentProperties','CodeType','UIComponent','UIComponentProperties::UIComponent')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(34,'tool_tip','tool_tip',null,'java.lang.String',null, 12)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(35,'image','image',null,'java.lang.String',null, 12)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(36,'code','code',null,'java.lang.String',null, 12)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(37,'method','method',null,'java.lang.String',null, 12)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(38,'mnemonic','mnemonic',null,'java.lang.String',null, 12)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(39,'display_name','display_name',null,'java.lang.String',null, 12)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(40,'enabled','enabled',null,'java.lang.String',null, 12)//
INSERT INTO decodetype (id, type, description) VALUES(1,'UIComponentProperties','Decode of type UIComponentProperties.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(50,'Save','1',34,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(51,'save.gif','1',35,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(52,'SAVE','1',36,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(53,'doSave','1',37,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(54,'S','1',38,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(55,'Save','1',39,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(56,'true','1',40,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(2,'UIComponentProperties','Decode of type UIComponentProperties.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(57,'Open File','2',34,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(58,'openFile.gif','2',35,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(59,'OPEN_FILE','2',36,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(60,'doOpen','2',37,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(61,'F','2',38,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(62,'Open File','2',39,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(63,'true','2',40,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(3,'UIComponentProperties','Decode of type UIComponentProperties.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(64,'Print','3',34,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(65,'Prnt_up.gif','3',35,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(66,'PRINT','3',36,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(67,'doPrint','3',37,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(68,'P','3',38,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(69,'Print','3',39,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(70,'true','3',40,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(4,'UIComponentProperties','Decode of type UIComponentProperties.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(71,'Calc','4',34,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(72,'calculation.gif','4',35,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(73,'CALCULATE','4',36,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(74,'doCalculate','4',37,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(75,'C','4',38,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(76,'Calculate','4',39,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(77,'true','4',40,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(5,'UIComponentProperties','Decode of type UIComponentProperties.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(78,'Save','5',34,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(79,'save.gif','5',35,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(80,'SAVE_AS','5',36,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(81,'doSaveAs','5',37,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(82,'S','5',38,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(83,'Save As','5',39,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(84,'true','5',40,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(6,'UIComponentProperties','Decode of type UIComponentProperties.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(85,'New','6',34,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(86,'new.gif','6',35,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(87,'NEW','6',36,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(88,'doNew','6',37,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(89,'N','6',38,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(90,'New','6',39,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(91,'true','6',40,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(7,'UIComponentProperties','Decode of type UIComponentProperties.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(92,'Close','7',34,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(93,'closeFile.gif','7',35,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(94,'CLOSE_FILE','7',36,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(95,'doCloseFile','7',37,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(96,'o','7',38,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(97,'Close File','7',39,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(98,'true','7',40,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(8,'UIComponentProperties','Decode of type UIComponentProperties.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(99,'Help','8',34,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(100,'help.gif','8',35,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(101,'HELP','8',36,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(102,'doHelp','8',37,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(103,'H','8',38,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(104,'Help','8',39,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(105,'true','8',40,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(9,'UIComponentProperties','Decode of type UIComponentProperties.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(106,'Cut','9',34,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(107,'cut.gif','9',35,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(108,'CUT','9',36,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(109,'doCut','9',37,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(110,'u','9',38,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(111,'Cut','9',39,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(112,'true','9',40,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(10,'UIComponentProperties','Decode of type UIComponentProperties.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(113,'Copy','10',34,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(114,'copy.gif','10',35,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(115,'COPY','10',36,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(116,'doCopy','10',37,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(117,'C','10',38,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(118,'Copy','10',39,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(119,'true','10',40,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(11,'UIComponentProperties','Decode of type UIComponentProperties.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(120,'Paste','11',34,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(121,'paste.gif','11',35,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(122,'PASTE','11',36,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(123,'doPaste','11',37,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(124,'a','11',38,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(125,'Paste','11',39,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(126,'true','11',40,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(12,'UIComponentProperties','Decode of type UIComponentProperties.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(127,'Close','12',34,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(128,'close.gif','12',35,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(129,'CLOSE','12',36,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(130,'doClose','12',37,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(131,'','12',38,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(132,'Close','12',39,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(133,'true','12',40,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(13,'UIComponentProperties','Decode of type UIComponentProperties.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(134,'Results','13',34,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(135,'results.gif','13',35,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(136,'RESULTS','13',36,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(137,'doResults','13',37,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(138,'R','13',38,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(139,'Results','13',39,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(140,'true','13',40,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(14,'UIComponentProperties','Decode of type UIComponentProperties.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(141,'Connect','14',34,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(142,'','14',35,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(143,'CONNECT','14',36,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(144,'doConnect','14',37,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(145,'','14',38,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(146,'Connect','14',39,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(147,'true','14',40,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(15,'UIComponentProperties','Decode of type UIComponentProperties.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(148,'Disconnect','15',34,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(149,'','15',35,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(150,'DISCONNECT','15',36,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(151,'doDisconnect','15',37,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(152,'','15',38,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(153,'Disconnect','15',39,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(154,'true','15',40,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(16,'UIComponentProperties','Decode of type UIComponentProperties.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(155,'Refresh','16',34,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(156,'refresh.gif','16',35,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(157,'REFRESH','16',36,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(158,'doRefresh','16',37,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(159,'','16',38,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(160,'Refresh','16',39,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(161,'true','16',40,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(17,'UIComponentProperties','Decode of type UIComponentProperties.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(162,'Next','17',34,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(163,'','17',35,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(164,'NEXT','17',36,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(165,'doNext','17',37,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(166,'n','17',38,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(167,'Next','17',39,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(168,'true','17',40,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(18,'UIComponentProperties','Decode of type UIComponentProperties.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(169,'Prev','18',34,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(170,'','18',35,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(171,'PREV','18',36,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(172,'doPrevious','18',37,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(173,'v','18',38,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(174,'Prev','18',39,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(175,'true','18',40,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(19,'UIComponentProperties','Decode of type UIComponentProperties.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(176,'Fetch','19',34,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(177,'fetch.gif','19',35,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(178,'FETCH','19',36,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(179,'doFetch','19',37,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(180,'','19',38,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(181,'Fetch','19',39,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(182,'true','19',40,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(20,'UIComponentProperties','Decode of type UIComponentProperties.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(183,'Insert','20',34,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(184,'','20',35,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(185,'INSERT','20',36,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(186,'doInsert','20',37,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(187,'','20',38,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(188,'Insert','20',39,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(189,'true','20',40,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(21,'UIComponentProperties','Decode of type UIComponentProperties.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(190,'Commit','21',34,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(191,'','21',35,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(192,'COMMIT','21',36,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(193,'doCommit','21',37,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(194,'','21',38,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(195,'Commit','21',39,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(196,'true','21',40,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(22,'UIComponentProperties','Decode of type UIComponentProperties.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(197,'Cancel','22',34,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(198,'cancel.gif','22',35,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(199,'CANCEL','22',36,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(200,'doCancel','22',37,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(201,'','22',38,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(202,'Cancel','22',39,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(203,'true','22',40,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(23,'UIComponentProperties','Decode of type UIComponentProperties.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(204,'Search','23',34,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(205,'search.gif','23',35,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(206,'SEARCH','23',36,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(207,'doSearch','23',37,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(208,'','23',38,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(209,'Search','23',39,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(210,'true','23',40,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(24,'UIComponentProperties','Decode of type UIComponentProperties.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(211,'Clear','24',34,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(212,'clear.gif','24',35,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(213,'CLEAR','24',36,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(214,'doClear','24',37,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(215,'','24',38,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(216,'Clear','24',39,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(217,'true','24',40,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(25,'UIComponentProperties','Decode of type UIComponentProperties.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(218,'Print Prev','25',34,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(219,'printprev.gif','25',35,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(220,'PRINT_PREVIEW','25',36,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(221,'doPrintPreview','25',37,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(222,'','25',38,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(223,'Print Prev','25',39,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(224,'true','25',40,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(26,'UIComponentProperties','Decode of type UIComponentProperties.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(225,'Execute','26',34,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(226,'execute.gif','26',35,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(227,'EXECUTE','26',36,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(228,'doExecute','26',37,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(229,'','26',38,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(230,'Execute','26',39,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(231,'true','26',40,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(27,'UIComponentProperties','Decode of type UIComponentProperties.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(232,'Update','27',34,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(233,'','27',35,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(234,'UPDATE','27',36,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(235,'doUpdate','27',37,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(236,'','27',38,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(237,'Update','27',39,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(238,'true','27',40,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(28,'UIComponentProperties','Decode of type UIComponentProperties.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(239,'Delete','28',34,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(240,'delete.gif','28',35,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(241,'DELETE','28',36,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(242,'doDelete','28',37,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(243,'','28',38,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(244,'Delete','28',39,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(245,'true','28',40,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(29,'UIComponentProperties','Decode of type UIComponentProperties.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(246,'Tile All','29',34,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(247,'','29',35,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(248,'TILE_ALL','29',36,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(249,'doTileAll','29',37,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(250,'','29',38,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(251,'Tile All','29',39,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(252,'true','29',40,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(30,'UIComponentProperties','Decode of type UIComponentProperties.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(253,'Cascade All','30',34,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(254,'','30',35,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(255,'CASCADE_ALL','30',36,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(256,'doCascadeAll','30',37,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(257,'','30',38,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(258,'Cascade All','30',39,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(259,'true','30',40,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(31,'UIComponentProperties','Decode of type UIComponentProperties.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(260,'Close All','31',34,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(261,'closeall.gif','31',35,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(262,'CLOSE_ALL','31',36,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(263,'doCloseAll','31',37,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(264,'','31',38,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(265,'Close All','31',39,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(266,'true','31',40,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(32,'UIComponentProperties','Decode of type UIComponentProperties.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(267,'Cascade','32',34,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(268,'','32',35,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(269,'CASCADE','32',36,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(270,'doCascade','32',37,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(271,'','32',38,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(272,'Cascade','32',39,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(273,'true','32',40,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(33,'UIComponentProperties','Decode of type UIComponentProperties.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(274,'Test','33',34,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(275,'backwardarrow.gif','33',35,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(276,'TEST','33',36,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(277,'doTest','33',37,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(278,'','33',38,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(279,'Test','33',39,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(280,'true','33',40,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(34,'UIComponentProperties','Decode of type UIComponentProperties.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(281,'Run','34',34,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(282,'forwardarrow.gif','34',35,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(283,'RUN','34',36,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(284,'doRun','34',37,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(285,'','34',38,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(286,'Run','34',39,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(287,'true','34',40,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(35,'UIComponentProperties','Decode of type UIComponentProperties.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(288,'Get Data','35',34,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(289,'data.gif','35',35,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(290,'DATA','35',36,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(291,'doData','35',37,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(292,'','35',38,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(293,'Data','35',39,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(294,'true','35',40,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(36,'UIComponentProperties','Decode of type UIComponentProperties.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(295,'Properties','36',34,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(296,'gearsmall.gif','36',35,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(297,'PROPERTIES','36',36,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(298,'doProperties','36',37,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(299,'','36',38,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(300,'Properties','36',39,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(301,'true','36',40,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(37,'UIComponentProperties','Decode of type UIComponentProperties.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(302,'Contents','37',34,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(303,'','37',35,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(304,'CONTENTS','37',36,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(305,'doContents','37',37,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(306,'','37',38,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(307,'Contents','37',39,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(308,'true','37',40,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(38,'UIComponentProperties','Decode of type UIComponentProperties.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(309,'About','38',34,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(310,'','38',35,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(311,'ABOUT','38',36,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(312,'doAbout','38',37,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(313,'','38',38,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(314,'About','38',39,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(315,'true','38',40,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(39,'UIComponentProperties','Decode of type UIComponentProperties.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(316,'Print Options','39',34,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(317,'gearsmall.gif','39',35,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(318,'PRINT_OPTIONS','39',36,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(319,'doPrintOptions','39',37,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(320,'u','39',38,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(321,'Print Options','39',39,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(322,'true','39',40,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(40,'UIComponentProperties','Decode of type UIComponentProperties.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(323,'Exit','40',34,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(324,'','40',35,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(325,'EXIT','40',36,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(326,'doExit','40',37,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(327,'','40',38,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(328,'Exit','40',39,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(329,'true','40',40,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(41,'UIComponentProperties','Decode of type UIComponentProperties.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(330,'Undo','41',34,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(331,'','41',35,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(332,'UNDO','41',36,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(333,'doUndo','41',37,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(334,'','41',38,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(335,'Undo','41',39,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(336,'true','41',40,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(42,'UIComponentProperties','Decode of type UIComponentProperties.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(337,'Redo','42',34,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(338,'','42',35,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(339,'REDO','42',36,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(340,'doRedo','42',37,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(341,'','42',38,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(342,'Redo','42',39,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(343,'true','42',40,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(43,'UIComponentProperties','Decode of type UIComponentProperties.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(344,'Find','43',34,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(345,'','43',35,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(346,'FIND','43',36,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(347,'doFind','43',37,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(348,'','43',38,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(349,'Find','43',39,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(350,'true','43',40,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(44,'UIComponentProperties','Decode of type UIComponentProperties.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(351,'Replace','44',34,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(352,'','44',35,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(353,'REPLACE','44',36,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(354,'doReplace','44',37,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(355,'','44',38,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(356,'Replace','44',39,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(357,'true','44',40,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(45,'UIComponentProperties','Decode of type UIComponentProperties.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(358,'Transfer','45',34,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(359,'','45',35,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(360,'TRANSFER','45',36,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(361,'doTransfer','45',37,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(362,'','45',38,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(363,'Transfer','45',39,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(364,'true','45',40,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(46,'UIComponentProperties','Decode of type UIComponentProperties.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(365,'Remove','46',34,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(366,'','46',35,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(367,'REMOVE','46',36,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(368,'doRemove','46',37,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(369,'','46',38,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(370,'Remove','46',39,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(371,'true','46',40,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(47,'UIComponentProperties','Decode of type UIComponentProperties.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(372,'Disclaimer','47',34,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(373,'','47',35,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(374,'DISCLAIMER','47',36,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(375,'doDisclaimer','47',37,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(376,'','47',38,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(377,'Disclaimer','47',39,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(378,'true','47',40,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(48,'UIComponentProperties','Decode of type UIComponentProperties.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(379,'Compile','48',34,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(380,'gear.gif','48',35,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(381,'COMPILE','48',36,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(382,'doCompile','48',37,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(383,'','48',38,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(384,'Compile','48',39,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(385,'true','48',40,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(49,'UIComponentProperties','Decode of type UIComponentProperties.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(386,'Re-Assign strategies for selected tradingdays.','49',34,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(387,'gear.gif','49',35,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(388,'REASSIGN','49',36,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(389,'doReAssign','49',37,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(390,'','49',38,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(391,'Re-Assign','49',39,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(392,'true','49',40,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(50,'UIComponentProperties','Decode of type UIComponentProperties.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(393,'Strategy Parameters','50',34,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(394,'gear.gif','50',35,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(395,'STRATEGY_PARMS','50',36,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(396,'doStrategyParameters','50',37,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(397,'','50',38,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(398,'Strategy Parms','50',39,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(399,'true','50',40,null, null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(13,'ReferenceTable','CodeType','CodeDecode','ReferenceTable::CodeDecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(41,'code','code',null,'java.lang.String',null, 13)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(42,'display_name','display_name',null,'java.lang.String',null, 13)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(43,'value','value',null,'java.lang.String',null, 13)//
INSERT INTO decodetype (id, type, description) VALUES(51,'ReferenceTable','Decode of type ReferenceTable.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(400,'org.trade.core.persistent.strategy.Strategy','51',41,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(401,'Strategy','51',42,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(402,'Strategy','51',43,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(52,'ReferenceTable','Decode of type ReferenceTable.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(403,'org.trade.core.persistent.portfolio.Portfolio','52',41,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(404,'Portfolio','52',42,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(405,'Portfolio','52',43,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(53,'ReferenceTable','Decode of type ReferenceTable.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(406,'org.trade.core.persistent.codetype.IndicatorParameters','53',41,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(407,'Indicator Parameters','53',42,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(408,'IndicatorParameters','53',43,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(54,'ReferenceTable','Decode of type ReferenceTable.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(409,'org.trade.core.persistent.strategy.StrategyParameters','54',41,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(410,'Strategy Parameters','54',42,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(411,'StrategyParameters','54',43,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(55,'ReferenceTable','Decode of type ReferenceTable.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(412,'org.trade.core.persistent.codetype.Entrylimit','55',41,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(413,'Entrylimit','55',42,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(414,'Entrylimit','55',43,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(56,'ReferenceTable','Decode of type ReferenceTable.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(415,'org.trade.core.persistent.codetype.CodeType','56',41,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(416,'Decode','56',42,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(417,'Decode','56',43,null, null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(14,'Action','CodeType','CodeDecode','Action::CodeDecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(44,'code','code',null,'java.lang.String',null, 14)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(45,'display_name','display_name',null,'java.lang.String',null, 14)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(46,'value','value',null,'java.lang.String',null, 14)//
INSERT INTO decodetype (id, type, description) VALUES(57,'Action','Decode of type Action.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(418,'BUY','57',44,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(419,'Buy','57',45,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(420,'BUY','57',46,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(58,'Action','Decode of type Action.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(421,'SELL','58',44,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(422,'Sell','58',45,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(423,'SELL','58',46,null, null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(15,'ContentType','CodeType','CodeDecode','ContentType::CodeDecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(47,'code','code',null,'java.lang.String',null, 15)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(48,'display_name','display_name',null,'java.lang.String',null, 15)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(49,'value','value',null,'java.lang.String',null, 15)//
INSERT INTO decodetype (id, type, description) VALUES(59,'ContentType','Decode of type ContentType.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(424,'java','59',47,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(425,'Java','59',48,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(426,'text/java','59',49,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(60,'ContentType','Decode of type ContentType.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(427,'js','60',47,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(428,'Javascript','60',48,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(429,'text/javascript','60',49,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(61,'ContentType','Decode of type ContentType.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(430,'txt','61',47,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(431,'Text','61',48,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(432,'text/rtf','61',49,null, null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(16,'OverrideConstraints','CodeType','CodeDecode','OverrideConstraints::CodeDecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(50,'code','code',null,'java.lang.String',null, 16)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(51,'display_name','display_name',null,'java.lang.String',null, 16)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(52,'value','value',null,'java.lang.String',null, 16)//
INSERT INTO decodetype (id, type, description) VALUES(62,'OverrideConstraints','Decode of type OverrideConstraints.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(433,'0','62',50,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(434,'No','62',51,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(435,'0','62',52,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(63,'OverrideConstraints','Decode of type OverrideConstraints.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(436,'1','63',50,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(437,'Yes','63',51,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(438,'1','63',52,null, null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(17,'Side','CodeType','CodeDecode','Side::CodeDecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(53,'code','code',null,'java.lang.String',null, 17)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(54,'display_name','display_name',null,'java.lang.String',null, 17)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(55,'value','value',null,'java.lang.String',null, 17)//
INSERT INTO decodetype (id, type, description) VALUES(64,'Side','Decode of type Side.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(439,'BOT','64',53,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(440,'Long','64',54,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(441,'BOT','64',55,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(65,'Side','Decode of type Side.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(442,'SLD','65',53,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(443,'Short','65',54,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(444,'SLD','65',55,null, null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(18,'OrderStatus','CodeType','CodeDecode','OrderStatus::CodeDecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(56,'code','code',null,'java.lang.String',null, 18)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(57,'display_name','display_name',null,'java.lang.String',null, 18)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(58,'value','value',null,'java.lang.String',null, 18)//
INSERT INTO decodetype (id, type, description) VALUES(66,'OrderStatus','Decode of type OrderStatus.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(445,'UNSUBMIT','66',56,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(446,'UnSubmitted','66',57,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(447,'UNSUBMIT','66',58,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(67,'OrderStatus','Decode of type OrderStatus.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(448,'PENDINGSUBMIT','67',56,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(449,'Pending Submit','67',57,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(450,'PENDINGSUBMIT','67',58,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(68,'OrderStatus','Decode of type OrderStatus.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(451,'PENDINGCANCEL','68',56,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(452,'Pending Cancel','68',57,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(453,'PENDINGCANCEL','68',58,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(69,'OrderStatus','Decode of type OrderStatus.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(454,'PRESUBMITTED','69',56,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(455,'Pre Submit','69',57,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(456,'PRESUBMITTED','69',58,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(70,'OrderStatus','Decode of type OrderStatus.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(457,'SUBMITTED','70',56,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(458,'Submitted','70',57,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(459,'SUBMITTED','70',58,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(71,'OrderStatus','Decode of type OrderStatus.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(460,'CANCELLED','71',56,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(461,'Cancelled','71',57,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(462,'CANCELLED','71',58,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(72,'OrderStatus','Decode of type OrderStatus.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(463,'FILLED','72',56,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(464,'Filled','72',57,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(465,'FILLED','72',58,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(73,'OrderStatus','Decode of type OrderStatus.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(466,'INACTIVE','73',56,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(467,'Inactive','73',57,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(468,'INACTIVE','73',58,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(74,'OrderStatus','Decode of type OrderStatus.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(469,'PARTIALFILLED','74',56,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(470,'Partial Filled','74',57,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(471,'PARTIALFILLED','74',58,null, null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(19,'OrderType','CodeType','CodeDecode','OrderType::CodeDecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(59,'code','code',null,'java.lang.String',null, 19)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(60,'display_name','display_name',null,'java.lang.String',null, 19)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(61,'value','value',null,'java.lang.String',null, 19)//
INSERT INTO decodetype (id, type, description) VALUES(75,'OrderType','Decode of type OrderType.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(472,'STPLMT','75',59,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(473,'Stop Lmt','75',60,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(474,'STPLMT','75',61,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(76,'OrderType','Decode of type OrderType.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(475,'LMT','76',59,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(476,'Limit','76',60,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(477,'LMT','76',61,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(77,'OrderType','Decode of type OrderType.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(478,'STP','77',59,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(479,'Stop','77',60,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(480,'STP','77',61,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(78,'OrderType','Decode of type OrderType.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(481,'MKT','78',59,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(482,'Market','78',60,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(483,'MKT','78',61,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(79,'OrderType','Decode of type OrderType.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(484,'MKTCLS','79',59,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(485,'Market On Cls','79',60,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(486,'MKTCLS','79',61,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(80,'OrderType','Decode of type OrderType.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(487,'LMTCLS','80',59,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(488,'Limit on Cls','80',60,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(489,'LMTCLS','80',61,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(81,'OrderType','Decode of type OrderType.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(490,'PEGMKT','81',59,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(491,'Peg Mkt','81',60,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(492,'PEGMKT','81',61,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(82,'OrderType','Decode of type OrderType.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(493,'SCALE','82',59,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(494,'Scale','82',60,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(495,'SCALE','82',61,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(83,'OrderType','Decode of type OrderType.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(496,'TRAIL','83',59,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(497,'Trail','83',60,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(498,'TRAIL','83',61,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(84,'OrderType','Decode of type OrderType.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(499,'REL','84',59,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(500,'Relative','84',60,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(501,'REL','84',61,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(85,'OrderType','Decode of type OrderType.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(502,'VWAP','85',59,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(503,'Vwap','85',60,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(504,'VWAP','85',61,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(86,'OrderType','Decode of type OrderType.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(505,'TRAILLIMIT','86',59,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(506,'Trail Lmt','86',60,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(507,'TRAILLIMIT','86',61,null, null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(20,'MarketBias','CodeType','CodeDecode','MarketBias::CodeDecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(62,'code','code',null,'java.lang.String',null, 20)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(63,'display_name','display_name',null,'java.lang.String',null, 20)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(64,'value','value',null,'java.lang.String',null, 20)//
INSERT INTO decodetype (id, type, description) VALUES(87,'MarketBias','Decode of type MarketBias.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(508,'S','87',62,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(509,'Short','87',63,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(510,'S','87',64,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(88,'MarketBias','Decode of type MarketBias.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(511,'L','88',62,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(512,'Long','88',63,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(513,'L','88',64,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(89,'MarketBias','Decode of type MarketBias.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(514,'N','89',62,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(515,'Neutral','89',63,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(516,'N','89',64,null, null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(21,'IndicatorSeriesUI','CodeType','CodeDecode','IndicatorSeriesUI::CodeDecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(65,'code','code',null,'java.lang.String',null, 21)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(66,'display_name','display_name',null,'java.lang.String',null, 21)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(67,'value','value',null,'java.lang.String',null, 21)//
INSERT INTO decodetype (id, type, description) VALUES(90,'IndicatorSeriesUI','Decode of type IndicatorSeriesUI.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(517,'AverageTrueRangeSeries','90',65,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(518,'ATR','90',66,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(519,'AverageTrueRangeSeries','90',67,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(91,'IndicatorSeriesUI','Decode of type IndicatorSeriesUI.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(520,'BollingerBandsSeries','91',65,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(521,'BollingerBands','91',66,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(522,'BollingerBandsSeries','91',67,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(92,'IndicatorSeriesUI','Decode of type IndicatorSeriesUI.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(523,'CandleSeries','92',65,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(524,'Candle','92',66,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(525,'CandleSeries','92',67,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(93,'IndicatorSeriesUI','Decode of type IndicatorSeriesUI.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(526,'CommodityChannelIndexSeries','93',65,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(527,'CCI','93',66,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(528,'CommodityChannelIndexSeries','93',67,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(94,'IndicatorSeriesUI','Decode of type IndicatorSeriesUI.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(529,'HeikinAshiSeries','94',65,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(530,'HeikinAshi','94',66,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(531,'HeikinAshiSeries','94',67,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(95,'IndicatorSeriesUI','Decode of type IndicatorSeriesUI.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(532,'MACDSeries','95',65,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(533,'MACD','95',66,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(534,'MACDSeries','95',67,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(96,'IndicatorSeriesUI','Decode of type IndicatorSeriesUI.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(535,'MoneyFlowIndexSeries','96',65,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(536,'MFI','96',66,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(537,'MoneyFlowIndexSeries','96',67,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(97,'IndicatorSeriesUI','Decode of type IndicatorSeriesUI.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(538,'MovingAverageSeries','97',65,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(539,'MovingAverage','97',66,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(540,'MovingAverageSeries','97',67,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(98,'IndicatorSeriesUI','Decode of type IndicatorSeriesUI.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(541,'PivotSeries','98',65,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(542,'Pivot','98',66,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(543,'PivotSeries','98',67,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(99,'IndicatorSeriesUI','Decode of type IndicatorSeriesUI.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(544,'RelativeStrengthIndexSeries','99',65,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(545,'RSI','99',66,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(546,'RelativeStrengthIndexSeries','99',67,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(100,'IndicatorSeriesUI','Decode of type IndicatorSeriesUI.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(547,'VolumeSeries','100',65,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(548,'Volume','100',66,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(549,'VolumeSeries','100',67,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(101,'IndicatorSeriesUI','Decode of type IndicatorSeriesUI.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(550,'VostroSeries','101',65,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(551,'Vostro','101',66,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(552,'VostroSeries','101',67,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(102,'IndicatorSeriesUI','Decode of type IndicatorSeriesUI.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(553,'VwapSeries','102',65,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(554,'Vwap','102',66,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(555,'VwapSeries','102',67,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(103,'IndicatorSeriesUI','Decode of type IndicatorSeriesUI.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(556,'StochasticOscillatorSeries','103',65,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(557,'% K/R','103',66,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(558,'StochasticOscillatorSeries','103',67,null, null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(22,'Currency','CodeType','CodeDecode','Currency::CodeDecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(68,'code','code',null,'java.lang.String',null, 22)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(69,'display_name','display_name',null,'java.lang.String',null, 22)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(70,'value','value',null,'java.lang.String',null, 22)//
INSERT INTO decodetype (id, type, description) VALUES(104,'Currency','Decode of type Currency.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(559,'USD','104',68,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(560,'US $','104',69,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(561,'USD','104',70,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(105,'Currency','Decode of type Currency.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(562,'EUR','105',68,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(563,'Euro $','105',69,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(564,'EUR','105',70,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(106,'Currency','Decode of type Currency.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(565,'GBP','106',68,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(566,'GB Pound','106',69,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(567,'GBP','106',70,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(107,'Currency','Decode of type Currency.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(568,'CAD','107',68,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(569,'Canada $','107',69,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(570,'CAD','107',70,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(108,'Currency','Decode of type Currency.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(571,'JPY','108',68,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(572,'Japan Yen','108',69,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(573,'JPY','108',70,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(109,'Currency','Decode of type Currency.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(574,'AUD','109',68,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(575,'Australian $','109',69,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(576,'AUD','109',70,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(110,'Currency','Decode of type Currency.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(577,'CHF','110',68,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(578,'Swiss Franc','110',69,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(579,'CHF','110',70,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(111,'Currency','Decode of type Currency.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(580,'INR','111',68,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(581,'Indian Rupee','111',69,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(582,'INR','111',70,null, null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(23,'TimeInForce','CodeType','CodeDecode','TimeInForce::CodeDecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(71,'code','code',null,'java.lang.String',null, 23)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(72,'display_name','display_name',null,'java.lang.String',null, 23)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(73,'value','value',null,'java.lang.String',null, 23)//
INSERT INTO decodetype (id, type, description) VALUES(112,'TimeInForce','Decode of type TimeInForce.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(583,'DAY','112',71,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(584,'Day','112',72,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(585,'DAY','112',73,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(113,'TimeInForce','Decode of type TimeInForce.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(586,'GTC','113',71,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(587,'Good till Cancel','113',72,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(588,'GTC','113',73,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(114,'TimeInForce','Decode of type TimeInForce.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(589,'IOC','114',71,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(590,'Immediate-or Cancel','114',72,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(591,'IOC','114',73,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(115,'TimeInForce','Decode of type TimeInForce.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(592,'GTD','115',71,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(593,'Good till date','115',72,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(594,'GTD','115',73,null, null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(24,'DataType','CodeType','CodeDecode','DataType::CodeDecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(74,'code','code',null,'java.lang.String',null, 24)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(75,'display_name','display_name',null,'java.lang.String',null, 24)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(76,'value','value',null,'java.lang.String',null, 24)//
INSERT INTO decodetype (id, type, description) VALUES(116,'DataType','Decode of type DataType.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(595,'java.lang.String','116',74,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(596,'String','116',75,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(597,'java.lang.String','116',76,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(117,'DataType','Decode of type DataType.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(598,'java.lang.Integer','117',74,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(599,'Integer','117',75,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(600,'java.lang.Integer','117',76,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(118,'DataType','Decode of type DataType.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(601,'java.lang.Long','118',74,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(602,'Long','118',75,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(603,'java.lang.Long','118',76,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(119,'DataType','Decode of type DataType.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(604,'java.math.BigDecimal','119',74,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(605,'Decimal','119',75,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(606,'java.math.BigDecimal','119',76,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(120,'DataType','Decode of type DataType.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(607,'java.lang.Boolean','120',74,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(608,'Boolean','120',75,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(609,'java.lang.Boolean','120',76,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(121,'DataType','Decode of type DataType.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(610,'java.time.LocalDate','121',74,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(611,'Date','121',75,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(612,'java.time.LocalDate','121',76,null, null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(25,'BarSize','CodeType','CodeDecode','BarSize::CodeDecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(77,'code','code',null,'java.lang.String',null, 25)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(78,'display_name','display_name',null,'java.lang.String',null, 25)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(79,'value','value',null,'java.lang.String',null, 25)//
INSERT INTO decodetype (id, type, description) VALUES(122,'BarSize','Decode of type BarSize.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(613,'_1_min','122',77,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(614,'1 min','122',78,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(615,'60','122',79,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(123,'BarSize','Decode of type BarSize.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(616,'_2_mins','123',77,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(617,'2 mins','123',78,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(618,'120','123',79,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(124,'BarSize','Decode of type BarSize.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(619,'_5_mins','124',77,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(620,'5 mins','124',78,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(621,'300','124',79,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(125,'BarSize','Decode of type BarSize.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(622,'_10_mins','125',77,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(623,'10 mins','125',78,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(624,'600','125',79,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(126,'BarSize','Decode of type BarSize.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(625,'_15_mins','126',77,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(626,'15 mins','126',78,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(627,'900','126',79,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(127,'BarSize','Decode of type BarSize.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(628,'_20_mins','127',77,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(629,'20 mins','127',78,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(630,'1200','127',79,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(128,'BarSize','Decode of type BarSize.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(631,'_30_mins','128',77,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(632,'30 mins','128',78,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(633,'1800','128',79,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(129,'BarSize','Decode of type BarSize.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(634,'_1_hour','129',77,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(635,'1 hour','129',78,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(636,'3600','129',79,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(130,'BarSize','Decode of type BarSize.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(637,'_1_day','130',77,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(638,'1 day','130',78,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(639,'86400','130',79,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(131,'BarSize','Decode of type BarSize.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(640,'_30_secs','131',77,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(641,'30 secs','131',78,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(642,'30','131',79,null, null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(26,'OCAType','CodeType','CodeDecode','OCAType::CodeDecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(80,'code','code',null,'java.lang.String',null, 26)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(81,'display_name','display_name',null,'java.lang.String',null, 26)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(82,'value','value',null,'java.lang.String',null, 26)//
INSERT INTO decodetype (id, type, description) VALUES(132,'OCAType','Decode of type OCAType.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(643,'2','132',80,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(644,'Reduce remaining orders','132',81,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(645,'2','132',82,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(133,'OCAType','Decode of type OCAType.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(646,'1','133',80,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(647,'Cancel all remaining','133',81,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(648,'1','133',82,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(134,'OCAType','Decode of type OCAType.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(649,'3','134',80,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(650,'Prop reduce remaining','134',81,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(651,'3','134',82,null, null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(27,'SECIdType','CodeType','CodeDecode','SECIdType::CodeDecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(83,'code','code',null,'java.lang.String',null, 27)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(84,'display_name','display_name',null,'java.lang.String',null, 27)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(85,'value','value',null,'java.lang.String',null, 27)//
INSERT INTO decodetype (id, type, description) VALUES(135,'SECIdType','Decode of type SECIdType.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(652,'ISIN','135',83,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(653,'Intl Sec Id #','135',84,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(654,'ISIN','135',85,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(136,'SECIdType','Decode of type SECIdType.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(655,'SEDOL','136',83,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(656,'London SE','136',84,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(657,'SEDOL','136',85,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(137,'SECIdType','Decode of type SECIdType.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(658,'CUSIP','137',83,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(659,'Cusip','137',84,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(660,'CUSIP','137',85,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(138,'SECIdType','Decode of type SECIdType.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(661,'RIC','138',83,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(662,'Reuters Inst Code','138',84,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(663,'RIC','138',85,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(139,'SECIdType','Decode of type SECIdType.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(664,'SYMBOL','139',83,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(665,'Symbol','139',84,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(666,'SYMBOL','139',85,null, null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(28,'TriggerMethod','CodeType','CodeDecode','TriggerMethod::CodeDecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(86,'code','code',null,'java.lang.String',null, 28)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(87,'display_name','display_name',null,'java.lang.String',null, 28)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(88,'value','value',null,'java.lang.String',null, 28)//
INSERT INTO decodetype (id, type, description) VALUES(140,'TriggerMethod','Decode of type TriggerMethod.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(667,'0','140',86,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(668,'Default','140',87,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(669,'0','140',88,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(141,'TriggerMethod','Decode of type TriggerMethod.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(670,'1','141',86,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(671,'Double bid/ask','141',87,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(672,'1','141',88,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(142,'TriggerMethod','Decode of type TriggerMethod.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(673,'2','142',86,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(674,'Last','142',87,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(675,'2','142',88,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(143,'TriggerMethod','Decode of type TriggerMethod.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(676,'3','143',86,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(677,'Double last','143',87,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(678,'3','143',88,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(144,'TriggerMethod','Decode of type TriggerMethod.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(679,'4','144',86,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(680,'Bid/Ask','144',87,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(681,'4','144',88,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(145,'TriggerMethod','Decode of type TriggerMethod.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(682,'7','145',86,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(683,'Last or Bid/Ask','145',87,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(684,'7','145',88,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(146,'TriggerMethod','Decode of type TriggerMethod.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(685,'8','146',86,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(686,'Mid Point','146',87,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(687,'8','146',88,null, null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(29,'MarketBar','CodeType','CodeDecode','MarketBar::CodeDecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(89,'code','code',null,'java.lang.String',null, 29)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(90,'display_name','display_name',null,'java.lang.String',null, 29)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(91,'value','value',null,'java.lang.String',null, 29)//
INSERT INTO decodetype (id, type, description) VALUES(147,'MarketBar','Decode of type MarketBar.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(688,'+NRB','147',89,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(689,'+NRB','147',90,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(690,'+NRB','147',91,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(148,'MarketBar','Decode of type MarketBar.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(691,'-NRB','148',89,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(692,'-NRB','148',90,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(693,'-NRB','148',91,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(149,'MarketBar','Decode of type MarketBar.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(694,'+NRBBT','149',89,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(695,'+NRB BT','149',90,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(696,'+NRBBT','149',91,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(150,'MarketBar','Decode of type MarketBar.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(697,'+NRBTT','150',89,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(698,'+NRB TT','150',90,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(699,'+NRBTT','150',91,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(151,'MarketBar','Decode of type MarketBar.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(700,'-NRBBT','151',89,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(701,'-NRB BT','151',90,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(702,'-NRBBT','151',91,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(152,'MarketBar','Decode of type MarketBar.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(703,'-NRBTT','152',89,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(704,'-NRB TT','152',90,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(705,'-NRBTT','152',91,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(153,'MarketBar','Decode of type MarketBar.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(706,'+DD','153',89,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(707,'Darling Doji','153',90,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(708,'+DD','153',91,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(154,'MarketBar','Decode of type MarketBar.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(709,'-DD','154',89,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(710,'Deadly Doji','154',90,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(711,'-DD','154',91,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(155,'MarketBar','Decode of type MarketBar.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(712,'+WRB','155',89,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(713,'+WRB','155',90,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(714,'+WRB','155',91,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(156,'MarketBar','Decode of type MarketBar.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(715,'-WRB','156',89,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(716,'-WRB','156',90,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(717,'-WRB','156',91,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(157,'MarketBar','Decode of type MarketBar.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(718,'+1010','157',89,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(719,'+WRB +/-10% Tail','157',90,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(720,'+1010','157',91,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(158,'MarketBar','Decode of type MarketBar.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(721,'-1010','158',89,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(722,'-WRB +/-10% Tail','158',90,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(723,'-1010','158',91,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(159,'MarketBar','Decode of type MarketBar.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(724,'+2020','159',89,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(725,'+WRB +/-20% Tail','159',90,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(726,'+2020','159',91,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(160,'MarketBar','Decode of type MarketBar.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(727,'-2020','160',89,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(728,'-WRB +/-20% Tail','160',90,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(729,'-2020','160',91,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(161,'MarketBar','Decode of type MarketBar.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(730,'NONE','161',89,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(731,'None','161',90,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(732,'NONE','161',91,null, null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(30,'OptionType','CodeType','CodeDecode','OptionType::CodeDecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(92,'code','code',null,'java.lang.String',null, 30)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(93,'display_name','display_name',null,'java.lang.String',null, 30)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(94,'value','value',null,'java.lang.String',null, 30)//
INSERT INTO decodetype (id, type, description) VALUES(162,'OptionType','Decode of type OptionType.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(733,'C','162',92,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(734,'Call','162',93,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(735,'C','162',94,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(163,'OptionType','Decode of type OptionType.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(736,'P','163',92,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(737,'Put','163',93,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(738,'P','163',94,null, null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(31,'ChartDays','CodeType','CodeDecode','ChartDays::CodeDecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(95,'code','code',null,'java.lang.String',null, 31)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(96,'display_name','display_name',null,'java.lang.String',null, 31)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(97,'value','value',null,'java.lang.String',null, 31)//
INSERT INTO decodetype (id, type, description) VALUES(164,'ChartDays','Decode of type ChartDays.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(739,'1','164',95,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(740,'1 D','164',96,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(741,'1','164',97,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(165,'ChartDays','Decode of type ChartDays.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(742,'2','165',95,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(743,'2 D','165',96,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(744,'2','165',97,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(166,'ChartDays','Decode of type ChartDays.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(745,'7','166',95,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(746,'1 W','166',96,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(747,'7','166',97,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(167,'ChartDays','Decode of type ChartDays.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(748,'15','167',95,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(749,'2 W','167',96,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(750,'15','167',97,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(168,'ChartDays','Decode of type ChartDays.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(751,'30','168',95,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(752,'1 M','168',96,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(753,'30','168',97,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(169,'ChartDays','Decode of type ChartDays.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(754,'60','169',95,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(755,'2 M','169',96,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(756,'60','169',97,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(170,'ChartDays','Decode of type ChartDays.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(757,'90','170',95,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(758,'3 M','170',96,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(759,'90','170',97,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(171,'ChartDays','Decode of type ChartDays.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(760,'180','171',95,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(761,'6 M','171',96,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(762,'180','171',97,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(172,'ChartDays','Decode of type ChartDays.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(763,'365','172',95,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(764,'1 Y','172',96,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(765,'365','172',97,null, null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(32,'Tier','CodeType','CodeDecode','Tier::CodeDecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(98,'code','code',null,'java.lang.String',null, 32)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(99,'display_name','display_name',null,'java.lang.String',null, 32)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(100,'value','value',null,'java.lang.String',null, 32)//
INSERT INTO decodetype (id, type, description) VALUES(173,'Tier','Decode of type Tier.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(766,'1','173',98,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(767,'1','173',99,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(768,'1','173',100,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(174,'Tier','Decode of type Tier.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(769,'2','174',98,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(770,'2','174',99,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(771,'2','174',100,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(175,'Tier','Decode of type Tier.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(772,'3','175',98,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(773,'3','175',99,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(774,'3','175',100,null, null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(33,'AllocationMethod','CodeType','CodeDecode','AllocationMethod::CodeDecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(101,'code','code',null,'java.lang.String',null, 33)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(102,'display_name','display_name',null,'java.lang.String',null, 33)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(103,'value','value',null,'java.lang.String',null, 33)//
INSERT INTO decodetype (id, type, description) VALUES(176,'AllocationMethod','Decode of type AllocationMethod.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(775,'AvailableEquity','176',101,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(776,'AvailableEquity','176',102,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(777,'AvailableEquity','176',103,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(177,'AllocationMethod','Decode of type AllocationMethod.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(778,'PctChange','177',101,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(779,'PctChange','177',102,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(780,'PctChange','177',103,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(178,'AllocationMethod','Decode of type AllocationMethod.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(781,'NetLiq','178',101,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(782,'NetLiq','178',102,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(783,'NetLiq','178',103,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(179,'AllocationMethod','Decode of type AllocationMethod.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(784,'EqualQuantity','179',101,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(785,'EqualQuantity','179',102,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(786,'EqualQuantity','179',103,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(180,'AllocationMethod','Decode of type AllocationMethod.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(787,'1','180',101,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(788,'Percentages','180',102,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(789,'1','180',103,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(181,'AllocationMethod','Decode of type AllocationMethod.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(790,'2','181',101,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(791,'Ratios','181',102,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(792,'2','181',103,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(182,'AllocationMethod','Decode of type AllocationMethod.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(793,'3','182',101,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(794,'Shares','182',102,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(795,'3','182',103,null, null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(34,'AccountType','CodeType','CodeDecode','AccountType::CodeDecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(104,'code','code',null,'java.lang.String',null, 34)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(105,'display_name','display_name',null,'java.lang.String',null, 34)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(106,'value','value',null,'java.lang.String',null, 34)//
INSERT INTO decodetype (id, type, description) VALUES(183,'AccountType','Decode of type AccountType.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(796,'CORPORATION','183',104,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(797,'Institution','183',105,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(798,'CORPORATION','183',106,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(184,'AccountType','Decode of type AccountType.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(799,'INDIVIDUAL','184',104,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(800,'Individual','184',105,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(801,'INDIVIDUAL','184',106,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(185,'AccountType','Decode of type AccountType.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(802,'IRA-TRADITIONAL','185',104,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(803,'IRA Traditional','185',105,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(804,'IRA-TRADITIONAL','185',106,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(186,'AccountType','Decode of type AccountType.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(805,'IRA-ROTH','186',104,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(806,'IRA Roth','186',105,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(807,'IRA-ROTH','186',106,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(187,'AccountType','Decode of type AccountType.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(808,'TRUST','187',104,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(809,'Trust','187',105,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(810,'TRUST','187',106,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(188,'AccountType','Decode of type AccountType.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(811,'JOINT','188',104,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(812,'Joint','188',105,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(813,'JOINT','188',106,null, null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(35,'SECType','CodeType','CodeDecode','SECType::CodeDecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(107,'code','code',null,'java.lang.String',null, 35)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(108,'display_name','display_name',null,'java.lang.String',null, 35)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(109,'value','value',null,'java.lang.String',null, 35)//
INSERT INTO decodetype (id, type, description) VALUES(189,'SECType','Decode of type SECType.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(814,'STK','189',107,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(815,'Stock','189',108,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(816,'STK','189',109,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(190,'SECType','Decode of type SECType.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(817,'OPT','190',107,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(818,'Option','190',108,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(819,'OPT','190',109,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(191,'SECType','Decode of type SECType.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(820,'FUT','191',107,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(821,'Future','191',108,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(822,'FUT','191',109,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(192,'SECType','Decode of type SECType.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(823,'CASH','192',107,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(824,'Cash','192',108,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(825,'CASH','192',109,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(193,'SECType','Decode of type SECType.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(826,'IND','193',107,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(827,'indices','193',108,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(828,'IND','193',109,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(194,'SECType','Decode of type SECType.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(829,'FOP','194',107,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(830,'Fut Opt','194',108,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(831,'FOP','194',109,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(195,'SECType','Decode of type SECType.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(832,'BAG','195',107,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(833,'Bag','195',108,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(834,'BAG','195',109,null, null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(36,'Exchange','CodeType','CodeDecode','Exchange::CodeDecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(110,'code','code',null,'java.lang.String',null, 36)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(111,'display_name','display_name',null,'java.lang.String',null, 36)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(112,'value','value',null,'java.lang.String',null, 36)//
INSERT INTO decodetype (id, type, description) VALUES(196,'Exchange','Decode of type Exchange.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(835,'SMART','196',110,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(836,'Smart','196',111,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(837,'SMART','196',112,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(197,'Exchange','Decode of type Exchange.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(838,'ISLAND','197',110,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(839,'Island','197',111,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(840,'ISLAND','197',112,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(198,'Exchange','Decode of type Exchange.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(841,'NASDAQ','198',110,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(842,'NASDAQ','198',111,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(843,'XNAS','198',112,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(199,'Exchange','Decode of type Exchange.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(844,'BATS','199',110,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(845,'Bats Global Mkts','199',111,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(846,'XCMO','199',112,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(200,'Exchange','Decode of type Exchange.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(847,'DRCTEDGE','200',110,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(848,'Direct Edge','200',111,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(849,'DRCTEDGE','200',112,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(201,'Exchange','Decode of type Exchange.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(850,'ARCA','201',110,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(851,'NYSE Arca','201',111,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(852,'ARCX','201',112,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(202,'Exchange','Decode of type Exchange.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(853,'NYSE','202',110,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(854,'New York SE','202',111,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(855,'XNYS','202',112,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(203,'Exchange','Decode of type Exchange.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(856,'GLOBEX','203',110,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(857,'Globex','203',111,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(858,'CMEX','203',112,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(204,'Exchange','Decode of type Exchange.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(859,'IDEAL','204',110,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(860,'iDeal','204',111,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(861,'IDEAL','204',112,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(205,'Exchange','Decode of type Exchange.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(862,'IDEALPRO','205',110,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(863,'iDeal Pro','205',111,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(864,'IDEALPRO','205',112,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(206,'Exchange','Decode of type Exchange.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(865,'DTB','206',110,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(866,'Eurex(DTB)','206',111,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(867,'DTB','206',112,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(207,'Exchange','Decode of type Exchange.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(868,'IBIS','207',110,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(869,'XETRA (IBIS)','207',111,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(870,'IBIS','207',112,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(208,'Exchange','Decode of type Exchange.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(871,'NSE','208',110,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(872,'Nat SE India','208',111,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(873,'NSE','208',112,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(209,'Exchange','Decode of type Exchange.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(874,'ECBOT','209',110,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(875,'CBOT (ECBOT)','209',111,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(876,'ECBOT','209',112,null, null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(37,'CalculationType','CodeType','CodeDecode','CalculationType::CodeDecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(113,'code','code',null,'java.lang.String',null, 37)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(114,'display_name','display_name',null,'java.lang.String',null, 37)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(115,'value','value',null,'java.lang.String',null, 37)//
INSERT INTO decodetype (id, type, description) VALUES(210,'CalculationType','Decode of type CalculationType.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(877,'LINEAR','210',113,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(878,'Linear','210',114,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(879,'LINEAR','210',115,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(211,'CalculationType','Decode of type CalculationType.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(880,'EXPONENTIAL','211',113,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(881,'Exponential','211',114,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(882,'EXPONENTIAL','211',115,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(212,'CalculationType','Decode of type CalculationType.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(883,'WEIGHTED','212',113,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(884,'Weighted','212',114,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(885,'WEIGHTED','212',115,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(213,'CalculationType','Decode of type CalculationType.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(886,'WEIGHTED_VOLUME','213',113,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(887,'Weighted Volume','213',114,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(888,'WEIGHTED_VOLUME','213',115,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(214,'CalculationType','Decode of type CalculationType.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(889,'TRIANGULAR','214',113,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(890,'Triangular','214',114,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(891,'TRIANGULAR','214',115,null, null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(38,'PriceSource','CodeType','CodeDecode','PriceSource::CodeDecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(116,'code','code',null,'java.lang.String',null, 38)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(117,'display_name','display_name',null,'java.lang.String',null, 38)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(118,'value','value',null,'java.lang.String',null, 38)//
INSERT INTO decodetype (id, type, description) VALUES(215,'PriceSource','Decode of type PriceSource.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(892,'1','215',116,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(893,'Close','215',117,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(894,'1','215',118,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(216,'PriceSource','Decode of type PriceSource.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(895,'2','216',116,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(896,'Open','216',117,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(897,'2','216',118,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(217,'PriceSource','Decode of type PriceSource.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(898,'3','217',116,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(899,'High','217',117,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(900,'3','217',118,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(218,'PriceSource','Decode of type PriceSource.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(901,'4','218',116,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(902,'Low','218',117,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(903,'4','218',118,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(219,'PriceSource','Decode of type PriceSource.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(904,'5','219',116,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(905,'(H+L)/2','219',117,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(906,'5','219',118,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(220,'PriceSource','Decode of type PriceSource.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(907,'6','220',116,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(908,'(H+L+C)/3','220',117,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(909,'6','220',118,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(221,'PriceSource','Decode of type PriceSource.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(910,'7','221',116,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(911,'(O+H+L+C)/4','221',117,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(912,'7','221',118,null, null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(39,'TradestrategyStatus','CodeType','CodeDecode','TradestrategyStatus::CodeDecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(119,'code','code',null,'java.lang.String',null, 39)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(120,'display_name','display_name',null,'java.lang.String',null, 39)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(121,'value','value',null,'java.lang.String',null, 39)//
INSERT INTO decodetype (id, type, description) VALUES(222,'TradestrategyStatus','Decode of type TradestrategyStatus.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(913,'TO','222',119,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(914,'Time Out','222',120,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(915,'TO','222',121,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(223,'TradestrategyStatus','Decode of type TradestrategyStatus.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(916,'GB','223',119,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(917,'Green Bar','223',120,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(918,'GB','223',121,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(224,'TradestrategyStatus','Decode of type TradestrategyStatus.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(919,'RB','224',119,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(920,'Red Bar','224',120,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(921,'RB','224',121,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(225,'TradestrategyStatus','Decode of type TradestrategyStatus.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(922,'PERCENT','225',119,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(923,'Percent range','225',120,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(924,'PERCENT','225',121,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(226,'TradestrategyStatus','Decode of type TradestrategyStatus.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(925,'TTBT','226',119,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(926,'Bar Tails','226',120,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(927,'TTBT','226',121,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(227,'TradestrategyStatus','Decode of type TradestrategyStatus.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(928,'NBB','227',119,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(929,'Narrow Body Bar','227',120,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(930,'NBB','227',121,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(228,'TradestrategyStatus','Decode of type TradestrategyStatus.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(931,'FIVE_MIN_LOW_BROKEN','228',119,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(932,'5min Low Broken','228',120,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(933,'FIVE_MIN_LOW_BROKEN','228',121,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(229,'TradestrategyStatus','Decode of type TradestrategyStatus.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(934,'FIVE_MIN_HIGH_BROKEN','229',119,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(935,'5min High Broken','229',120,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(936,'FIVE_MIN_HIGH_BROKEN','229',121,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(230,'TradestrategyStatus','Decode of type TradestrategyStatus.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(937,'OPEN','230',119,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(938,'Open position','230',120,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(939,'OPEN','230',121,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(231,'TradestrategyStatus','Decode of type TradestrategyStatus.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(940,'CLOSED','231',119,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(941,'Closed Position','231',120,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(942,'CLOSED','231',121,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(232,'TradestrategyStatus','Decode of type TradestrategyStatus.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(943,'CANCELLED','232',119,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(944,'Cancelled','232',120,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(945,'CANCELLED','232',121,null, null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(40,'YesNo','CodeType','CodeDecode','YesNo::CodeDecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(122,'code','code',null,'java.lang.String',null, 40)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(123,'display_name','display_name',null,'java.lang.String',null, 40)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(124,'value','value',null,'java.lang.String',null, 40)//
INSERT INTO decodetype (id, type, description) VALUES(233,'YesNo','Decode of type YesNo.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(946,'true','233',122,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(947,'Yes','233',123,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(948,'true','233',124,null, null)//
INSERT INTO decodetype (id, type, description) VALUES(234,'YesNo','Decode of type YesNo.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(949,'false','234',122,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(950,'No','234',123,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(951,'false','234',124,null, null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(41,'DAOAccount','CodeType','DAODecode','DAOAccount::DAODecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(125,'code','code',null,'java.lang.String',null, 41)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(126,'display_name','display_name',null,'java.lang.String',null, 41)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(127,'value','value',null,'java.lang.String',null, 41)//
INSERT INTO decodetype (id, type, description) VALUES(235,'DAOAccount','Decode of type DAOAccount.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(952,'org.trade.core.persistent.account.Account','235',125,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(953,'getAccountNumber','235',126,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(954,'getAccountNumber','235',127,null, null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(42,'DAOProfile','CodeType','DAODecode','DAOProfile::DAODecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(128,'code','code',null,'java.lang.String',null, 42)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(129,'display_name','display_name',null,'java.lang.String',null, 42)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(130,'value','value',null,'java.lang.String',null, 42)//
INSERT INTO decodetype (id, type, description) VALUES(236,'DAOProfile','Decode of type DAOProfile.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(955,'org.trade.core.persistent.portfolio.Portfolio','236',128,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(956,'getName','236',129,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(957,'getName','236',130,null, null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(43,'DAOEntryLimit','CodeType','DAODecode','DAOEntryLimit::DAODecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(131,'code','code',null,'java.lang.String',null, 43)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(132,'display_name','display_name',null,'java.lang.String',null, 43)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(133,'value','value',null,'java.lang.String',null, 43)//
INSERT INTO decodetype (id, type, description) VALUES(237,'DAOEntryLimit','Decode of type DAOEntryLimit.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(958,'org.trade.core.persistent.codetype.Entrylimit','237',131,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(959,'getId','237',132,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(960,'getId','237',133,null, null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(44,'DAOStrategy','CodeType','DAODecode','DAOStrategy::DAODecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(134,'code','code',null,'java.lang.String',null, 44)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(135,'display_name','display_name',null,'java.lang.String',null, 44)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(136,'value','value',null,'java.lang.String',null, 44)//
INSERT INTO decodetype (id, type, description) VALUES(238,'DAOStrategy','Decode of type DAOStrategy.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(961,'org.trade.core.persistent.strategy.Strategy','238',134,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(962,'getName','238',135,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(963,'getName','238',136,null, null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(45,'DAOStrategyManager','CodeType','DAODecode','DAOStrategyManager::DAODecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(137,'code','code',null,'java.lang.String',null, 45)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(138,'display_name','display_name',null,'java.lang.String',null, 45)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(139,'value','value',null,'java.lang.String',null, 45)//
INSERT INTO decodetype (id, type, description) VALUES(239,'DAOStrategyManager','Decode of type DAOStrategyManager.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(964,'org.trade.core.persistent.strategy.Strategy','239',137,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(965,'getName','239',138,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(966,'getName','239',139,null, null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(46,'DAOPortfolio','CodeType','DAODecode','DAOPortfolio::DAODecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(140,'code','code',null,'java.lang.String',null, 46)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(141,'display_name','display_name',null,'java.lang.String',null, 46)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(142,'value','value',null,'java.lang.String',null, 46)//
INSERT INTO decodetype (id, type, description) VALUES(240,'DAOPortfolio','Decode of type DAOPortfolio.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(967,'org.trade.core.persistent.portfolio.Portfolio','240',140,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(968,'getName','240',141,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(969,'getName','240',142,null, null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(47,'DAOFAGroup','CodeType','DAODecode','DAOFAGroup::DAODecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(143,'code','code',null,'java.lang.String',null, 47)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(144,'display_name','display_name',null,'java.lang.String',null, 47)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(145,'value','value',null,'java.lang.String',null, 47)//
INSERT INTO decodetype (id, type, description) VALUES(241,'DAOFAGroup','Decode of type DAOFAGroup.')//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(970,'org.trade.core.persistent.portfolio.Portfolio','241',143,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(971,'getName','241',144,null, null)//
INSERT INTO codevalue (id , code_value, decodetype_id, code_attribute_id,indicator_series_id, tradestrategy_id) VALUES(972,'getName','241',145,null, null)//
COMMIT//

