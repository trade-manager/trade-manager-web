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

INSERT INTO codetype (id, name, type, category, description) VALUES(12,'DAOAccount','DAOAccount','dao_decode','DAOAccount::dao_decode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(34,'code','code',null,'java.lang.String',null, 12)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(35,'display_name','display_name',null,'java.lang.String',null, 12)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(50,'org.trade.core.persistent.account.Account',35,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(51,'getAccountNumber',35,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(13,'StrategyManager','StrategyManager','dao_decode','StrategyManager::dao_decode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(36,'code','code',null,'java.lang.String',null, 13)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(37,'display_name','display_name',null,'java.lang.String',null, 13)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(52,'org.trade.core.persistent.strategy.Strategy',37,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(53,'getName',37,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(14,'DAOEntryLimit','DAOEntryLimit','dao_decode','DAOEntryLimit::dao_decode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(38,'code','code',null,'java.lang.String',null, 14)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(39,'display_name','display_name',null,'java.lang.String',null, 14)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(54,'org.trade.core.persistent.codetype.Entrylimit',39,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(55,'getId',39,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(15,'DAOPortfolio','DAOPortfolio','dao_decode','DAOPortfolio::dao_decode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(40,'code','code',null,'java.lang.String',null, 15)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(41,'display_name','display_name',null,'java.lang.String',null, 15)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(56,'org.trade.core.persistent.portfolio.Portfolio',41,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(57,'getName',41,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(16,'FAGroup','FAGroup','dao_decode','FAGroup::dao_decode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(42,'code','code',null,'java.lang.String',null, 16)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(43,'display_name','display_name',null,'java.lang.String',null, 16)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(58,'org.trade.core.persistent.portfolio.Portfolio',43,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(59,'getName',43,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(17,'Strategy','Strategy','dao_decode','Strategy::dao_decode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(44,'code','code',null,'java.lang.String',null, 17)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(45,'display_name','display_name',null,'java.lang.String',null, 17)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(60,'org.trade.core.persistent.strategy.Strategy',45,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(61,'getName',45,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(18,'ReferenceTable','ReferenceTable','code_decode','ReferenceTable::code_decode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(46,'code','code',null,'java.lang.String',null, 18)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(47,'display_name','display_name',null,'java.lang.String',null, 18)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(48,'value','value',null,'java.lang.String',null, 18)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(62,'org.trade.core.persistent.strategy.Strategy',48,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(63,'Strategy',48,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(64,'Strategy',48,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(65,'org.trade.core.persistent.portfolio.Portfolio',48,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(66,'Portfolio',48,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(67,'Portfolio',48,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(68,'org.trade.core.persistent.codetype.IndicatorParameters',48,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(69,'Indicator Parameters',48,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(70,'IndicatorParameters',48,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(71,'org.trade.core.persistent.strategy.StrategyParameters',48,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(72,'Strategy Parameters',48,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(73,'StrategyParameters',48,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(74,'org.trade.core.persistent.codetype.Entrylimit',48,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(75,'Entrylimit',48,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(76,'Entrylimit',48,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(77,'org.trade.core.persistent.codetype.CodeType',48,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(78,'Decode',48,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(79,'Decode',48,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(19,'Action','Action','code_decode','Action::code_decode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(49,'code','code',null,'java.lang.String',null, 19)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(50,'display_name','display_name',null,'java.lang.String',null, 19)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(51,'value','value',null,'java.lang.String',null, 19)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(80,'BUY',51,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(81,'Buy',51,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(82,'BUY',51,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(83,'SELL',51,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(84,'Sell',51,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(85,'SELL',51,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(20,'ContentType','ContentType','code_decode','ContentType::code_decode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(52,'code','code',null,'java.lang.String',null, 20)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(53,'display_name','display_name',null,'java.lang.String',null, 20)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(54,'value','value',null,'java.lang.String',null, 20)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(86,'java',54,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(87,'Java',54,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(88,'text/java',54,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(89,'js',54,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(90,'Javascript',54,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(91,'text/javascript',54,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(92,'txt',54,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(93,'Text',54,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(94,'text/rtf',54,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(21,'IndicatorSeries','IndicatorSeries','code_decode','IndicatorSeries::code_decode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(55,'code','code',null,'java.lang.String',null, 21)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(56,'display_name','display_name',null,'java.lang.String',null, 21)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(57,'value','value',null,'java.lang.String',null, 21)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(95,'AverageTrueRangeSeries',57,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(96,'ATR',57,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(97,'AverageTrueRangeSeries',57,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(98,'BollingerBandsSeries',57,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(99,'BollingerBands',57,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(100,'BollingerBandsSeries',57,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(101,'CandleSeries',57,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(102,'Candle',57,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(103,'CandleSeries',57,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(104,'CommodityChannelIndexSeries',57,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(105,'CCI',57,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(106,'CommodityChannelIndexSeries',57,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(107,'HeikinAshiSeries',57,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(108,'HeikinAshi',57,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(109,'HeikinAshiSeries',57,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(110,'MACDSeries',57,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(111,'MACD',57,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(112,'MACDSeries',57,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(113,'MoneyFlowIndexSeries',57,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(114,'MFI',57,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(115,'MoneyFlowIndexSeries',57,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(116,'MovingAverageSeries',57,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(117,'MovingAverage',57,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(118,'MovingAverageSeries',57,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(119,'PivotSeries',57,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(120,'Pivot',57,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(121,'PivotSeries',57,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(122,'RelativeStrengthIndexSeries',57,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(123,'RSI',57,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(124,'RelativeStrengthIndexSeries',57,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(125,'VolumeSeries',57,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(126,'Volume',57,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(127,'VolumeSeries',57,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(128,'VostroSeries',57,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(129,'Vostro',57,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(130,'VostroSeries',57,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(131,'VwapSeries',57,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(132,'Vwap',57,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(133,'VwapSeries',57,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(134,'StochasticOscillatorSeries',57,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(135,'% K/R',57,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(136,'StochasticOscillatorSeries',57,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(22,'OverrideConstraints','OverrideConstraints','code_decode','OverrideConstraints::code_decode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(58,'code','code',null,'java.lang.String',null, 22)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(59,'display_name','display_name',null,'java.lang.String',null, 22)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(60,'value','value',null,'java.lang.String',null, 22)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(137,'0',60,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(138,'No',60,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(139,'0',60,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(140,'1',60,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(141,'Yes',60,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(142,'1',60,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(23,'Side','Side','code_decode','Side::code_decode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(61,'code','code',null,'java.lang.String',null, 23)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(62,'display_name','display_name',null,'java.lang.String',null, 23)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(63,'value','value',null,'java.lang.String',null, 23)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(143,'BOT',63,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(144,'Long',63,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(145,'BOT',63,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(146,'SLD',63,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(147,'Short',63,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(148,'SLD',63,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(24,'OrderStatus','OrderStatus','code_decode','OrderStatus::code_decode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(64,'code','code',null,'java.lang.String',null, 24)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(65,'display_name','display_name',null,'java.lang.String',null, 24)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(66,'value','value',null,'java.lang.String',null, 24)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(149,'UNSUBMIT',66,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(150,'UnSubmitted',66,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(151,'UNSUBMIT',66,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(152,'PENDINGSUBMIT',66,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(153,'Pending Submit',66,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(154,'PENDINGSUBMIT',66,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(155,'PENDINGCANCEL',66,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(156,'Pending Cancel',66,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(157,'PENDINGCANCEL',66,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(158,'PRESUBMITTED',66,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(159,'Pre Submit',66,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(160,'PRESUBMITTED',66,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(161,'SUBMITTED',66,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(162,'Submitted',66,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(163,'SUBMITTED',66,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(164,'CANCELLED',66,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(165,'Cancelled',66,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(166,'CANCELLED',66,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(167,'FILLED',66,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(168,'Filled',66,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(169,'FILLED',66,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(170,'INACTIVE',66,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(171,'Inactive',66,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(172,'INACTIVE',66,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(173,'PARTIALFILLED',66,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(174,'Partial Filled',66,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(175,'PARTIALFILLED',66,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(25,'OrderType','OrderType','code_decode','OrderType::code_decode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(67,'code','code',null,'java.lang.String',null, 25)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(68,'display_name','display_name',null,'java.lang.String',null, 25)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(69,'value','value',null,'java.lang.String',null, 25)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(176,'STPLMT',69,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(177,'Stop Lmt',69,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(178,'STPLMT',69,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(179,'LMT',69,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(180,'Limit',69,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(181,'LMT',69,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(182,'STP',69,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(183,'Stop',69,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(184,'STP',69,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(185,'MKT',69,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(186,'Market',69,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(187,'MKT',69,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(188,'MKTCLS',69,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(189,'Market On Cls',69,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(190,'MKTCLS',69,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(191,'LMTCLS',69,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(192,'Limit on Cls',69,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(193,'LMTCLS',69,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(194,'PEGMKT',69,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(195,'Peg Mkt',69,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(196,'PEGMKT',69,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(197,'SCALE',69,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(198,'Scale',69,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(199,'SCALE',69,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(200,'TRAIL',69,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(201,'Trail',69,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(202,'TRAIL',69,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(203,'REL',69,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(204,'Relative',69,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(205,'REL',69,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(206,'VWAP',69,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(207,'Vwap',69,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(208,'VWAP',69,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(209,'TRAILLIMIT',69,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(210,'Trail Lmt',69,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(211,'TRAILLIMIT',69,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(26,'MarketBias','MarketBias','code_decode','MarketBias::code_decode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(70,'code','code',null,'java.lang.String',null, 26)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(71,'display_name','display_name',null,'java.lang.String',null, 26)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(72,'value','value',null,'java.lang.String',null, 26)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(212,'S',72,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(213,'Short',72,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(214,'S',72,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(215,'L',72,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(216,'Long',72,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(217,'L',72,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(218,'N',72,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(219,'Neutral',72,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(220,'N',72,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(27,'Currency','Currency','code_decode','Currency::code_decode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(73,'code','code',null,'java.lang.String',null, 27)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(74,'display_name','display_name',null,'java.lang.String',null, 27)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(75,'value','value',null,'java.lang.String',null, 27)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(221,'USD',75,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(222,'US $',75,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(223,'USD',75,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(224,'EUR',75,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(225,'Euro $',75,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(226,'EUR',75,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(227,'GBP',75,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(228,'GB Pound',75,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(229,'GBP',75,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(230,'CAD',75,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(231,'Canada $',75,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(232,'CAD',75,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(233,'JPY',75,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(234,'Japan Yen',75,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(235,'JPY',75,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(236,'AUD',75,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(237,'Australian $',75,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(238,'AUD',75,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(239,'CHF',75,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(240,'Swiss Franc',75,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(241,'CHF',75,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(242,'INR',75,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(243,'Indian Rupee',75,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(244,'INR',75,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(28,'TimeInForce','TimeInForce','code_decode','TimeInForce::code_decode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(76,'code','code',null,'java.lang.String',null, 28)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(77,'display_name','display_name',null,'java.lang.String',null, 28)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(78,'value','value',null,'java.lang.String',null, 28)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(245,'DAY',78,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(246,'Day',78,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(247,'DAY',78,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(248,'GTC',78,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(249,'Good till Cancel',78,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(250,'GTC',78,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(251,'IOC',78,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(252,'Immediate-or Cancel',78,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(253,'IOC',78,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(254,'GTD',78,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(255,'Good till date',78,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(256,'GTD',78,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(29,'DataType','DataType','code_decode','DataType::code_decode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(79,'code','code',null,'java.lang.String',null, 29)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(80,'display_name','display_name',null,'java.lang.String',null, 29)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(81,'value','value',null,'java.lang.String',null, 29)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(257,'java.lang.String',81,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(258,'String',81,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(259,'java.lang.String',81,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(260,'java.lang.Integer',81,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(261,'Integer',81,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(262,'java.lang.Integer',81,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(263,'java.lang.Long',81,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(264,'Long',81,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(265,'java.lang.Long',81,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(266,'java.math.BigDecimal',81,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(267,'Decimal',81,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(268,'java.math.BigDecimal',81,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(269,'java.lang.Boolean',81,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(270,'Boolean',81,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(271,'java.lang.Boolean',81,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(272,'java.time.LocalDate',81,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(273,'Date',81,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(274,'java.time.LocalDate',81,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(30,'BarSize','BarSize','code_decode','BarSize::code_decode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(82,'code','code',null,'java.lang.String',null, 30)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(83,'display_name','display_name',null,'java.lang.String',null, 30)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(84,'value','value',null,'java.lang.String',null, 30)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(275,'_1_min',84,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(276,'1 min',84,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(277,'60',84,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(278,'_2_mins',84,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(279,'2 mins',84,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(280,'120',84,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(281,'_5_mins',84,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(282,'5 mins',84,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(283,'300',84,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(284,'_10_mins',84,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(285,'10 mins',84,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(286,'600',84,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(287,'_15_mins',84,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(288,'15 mins',84,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(289,'900',84,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(290,'_20_mins',84,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(291,'20 mins',84,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(292,'1200',84,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(293,'_30_mins',84,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(294,'30 mins',84,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(295,'1800',84,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(296,'_1_hour',84,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(297,'1 hour',84,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(298,'3600',84,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(299,'_1_day',84,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(300,'1 day',84,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(301,'86400',84,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(302,'_30_secs',84,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(303,'30 secs',84,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(304,'30',84,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(31,'OCAType','OCAType','code_decode','OCAType::code_decode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(85,'code','code',null,'java.lang.String',null, 31)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(86,'display_name','display_name',null,'java.lang.String',null, 31)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(87,'value','value',null,'java.lang.String',null, 31)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(305,'2',87,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(306,'Reduce remaining orders',87,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(307,'2',87,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(308,'1',87,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(309,'Cancel all remaining',87,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(310,'1',87,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(311,'3',87,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(312,'Prop reduce remaining',87,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(313,'3',87,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(32,'SECIdType','SECIdType','code_decode','SECIdType::code_decode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(88,'code','code',null,'java.lang.String',null, 32)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(89,'display_name','display_name',null,'java.lang.String',null, 32)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(90,'value','value',null,'java.lang.String',null, 32)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(314,'ISIN',90,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(315,'Intl Sec Id #',90,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(316,'ISIN',90,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(317,'SEDOL',90,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(318,'London SE',90,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(319,'SEDOL',90,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(320,'CUSIP',90,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(321,'Cusip',90,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(322,'CUSIP',90,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(323,'RIC',90,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(324,'Reuters Inst Code',90,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(325,'RIC',90,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(326,'SYMBOL',90,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(327,'Symbol',90,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(328,'SYMBOL',90,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(33,'TriggerMethod','TriggerMethod','code_decode','TriggerMethod::code_decode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(91,'code','code',null,'java.lang.String',null, 33)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(92,'display_name','display_name',null,'java.lang.String',null, 33)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(93,'value','value',null,'java.lang.String',null, 33)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(329,'0',93,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(330,'Default',93,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(331,'0',93,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(332,'1',93,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(333,'Double bid/ask',93,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(334,'1',93,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(335,'2',93,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(336,'Last',93,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(337,'2',93,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(338,'3',93,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(339,'Double last',93,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(340,'3',93,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(341,'4',93,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(342,'Bid/Ask',93,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(343,'4',93,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(344,'7',93,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(345,'Last or Bid/Ask',93,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(346,'7',93,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(347,'8',93,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(348,'Mid Point',93,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(349,'8',93,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(34,'MarketBar','MarketBar','code_decode','MarketBar::code_decode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(94,'code','code',null,'java.lang.String',null, 34)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(95,'display_name','display_name',null,'java.lang.String',null, 34)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(96,'value','value',null,'java.lang.String',null, 34)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(350,'+NRB',96,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(351,'+NRB',96,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(352,'+NRB',96,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(353,'-NRB',96,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(354,'-NRB',96,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(355,'-NRB',96,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(356,'+NRBBT',96,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(357,'+NRB BT',96,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(358,'+NRBBT',96,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(359,'+NRBTT',96,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(360,'+NRB TT',96,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(361,'+NRBTT',96,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(362,'-NRBBT',96,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(363,'-NRB BT',96,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(364,'-NRBBT',96,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(365,'-NRBTT',96,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(366,'-NRB TT',96,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(367,'-NRBTT',96,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(368,'+DD',96,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(369,'Darling Doji',96,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(370,'+DD',96,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(371,'-DD',96,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(372,'Deadly Doji',96,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(373,'-DD',96,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(374,'+WRB',96,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(375,'+WRB',96,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(376,'+WRB',96,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(377,'-WRB',96,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(378,'-WRB',96,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(379,'-WRB',96,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(380,'+1010',96,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(381,'+WRB +/-10% Tail',96,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(382,'+1010',96,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(383,'-1010',96,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(384,'-WRB +/-10% Tail',96,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(385,'-1010',96,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(386,'+2020',96,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(387,'+WRB +/-20% Tail',96,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(388,'+2020',96,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(389,'-2020',96,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(390,'-WRB +/-20% Tail',96,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(391,'-2020',96,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(392,'NONE',96,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(393,'None',96,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(394,'NONE',96,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(35,'OptionType','OptionType','code_decode','OptionType::code_decode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(97,'code','code',null,'java.lang.String',null, 35)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(98,'display_name','display_name',null,'java.lang.String',null, 35)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(99,'value','value',null,'java.lang.String',null, 35)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(395,'C',99,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(396,'Call',99,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(397,'C',99,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(398,'P',99,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(399,'Put',99,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(400,'P',99,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(36,'ChartDays','ChartDays','code_decode','ChartDays::code_decode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(100,'code','code',null,'java.lang.String',null, 36)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(101,'display_name','display_name',null,'java.lang.String',null, 36)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(102,'value','value',null,'java.lang.String',null, 36)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(401,'1',102,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(402,'1 D',102,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(403,'1',102,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(404,'2',102,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(405,'2 D',102,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(406,'2',102,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(407,'7',102,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(408,'1 W',102,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(409,'7',102,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(410,'15',102,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(411,'2 W',102,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(412,'15',102,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(413,'30',102,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(414,'1 M',102,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(415,'30',102,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(416,'60',102,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(417,'2 M',102,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(418,'60',102,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(419,'90',102,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(420,'3 M',102,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(421,'90',102,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(422,'180',102,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(423,'6 M',102,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(424,'180',102,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(425,'365',102,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(426,'1 Y',102,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(427,'365',102,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(37,'Tier','Tier','code_decode','Tier::code_decode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(103,'code','code',null,'java.lang.String',null, 37)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(104,'display_name','display_name',null,'java.lang.String',null, 37)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(105,'value','value',null,'java.lang.String',null, 37)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(428,'1',105,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(429,'1',105,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(430,'1',105,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(431,'2',105,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(432,'2',105,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(433,'2',105,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(434,'3',105,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(435,'3',105,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(436,'3',105,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(38,'AllocationMethod','AllocationMethod','code_decode','AllocationMethod::code_decode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(106,'code','code',null,'java.lang.String',null, 38)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(107,'display_name','display_name',null,'java.lang.String',null, 38)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(108,'value','value',null,'java.lang.String',null, 38)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(437,'AvailableEquity',108,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(438,'AvailableEquity',108,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(439,'AvailableEquity',108,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(440,'PctChange',108,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(441,'PctChange',108,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(442,'PctChange',108,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(443,'NetLiq',108,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(444,'NetLiq',108,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(445,'NetLiq',108,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(446,'EqualQuantity',108,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(447,'EqualQuantity',108,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(448,'EqualQuantity',108,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(449,'1',108,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(450,'Percentages',108,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(451,'1',108,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(452,'2',108,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(453,'Ratios',108,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(454,'2',108,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(455,'3',108,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(456,'Shares',108,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(457,'3',108,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(39,'AccountType','AccountType','code_decode','AccountType::code_decode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(109,'code','code',null,'java.lang.String',null, 39)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(110,'display_name','display_name',null,'java.lang.String',null, 39)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(111,'value','value',null,'java.lang.String',null, 39)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(458,'CORPORATION',111,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(459,'Institution',111,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(460,'CORPORATION',111,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(461,'INDIVIDUAL',111,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(462,'Individual',111,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(463,'INDIVIDUAL',111,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(464,'IRA-TRADITIONAL',111,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(465,'IRA Traditional',111,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(466,'IRA-TRADITIONAL',111,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(467,'IRA-ROTH',111,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(468,'IRA Roth',111,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(469,'IRA-ROTH',111,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(470,'TRUST',111,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(471,'Trust',111,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(472,'TRUST',111,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(473,'JOINT',111,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(474,'Joint',111,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(475,'JOINT',111,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(40,'SECType','SECType','code_decode','SECType::code_decode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(112,'code','code',null,'java.lang.String',null, 40)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(113,'display_name','display_name',null,'java.lang.String',null, 40)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(114,'value','value',null,'java.lang.String',null, 40)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(476,'STK',114,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(477,'Stock',114,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(478,'STK',114,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(479,'OPT',114,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(480,'Option',114,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(481,'OPT',114,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(482,'FUT',114,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(483,'Future',114,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(484,'FUT',114,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(485,'CASH',114,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(486,'Cash',114,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(487,'CASH',114,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(488,'IND',114,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(489,'indices',114,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(490,'IND',114,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(491,'FOP',114,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(492,'Fut Opt',114,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(493,'FOP',114,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(494,'BAG',114,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(495,'Bag',114,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(496,'BAG',114,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(41,'Exchange','Exchange','code_decode','Exchange::code_decode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(115,'code','code',null,'java.lang.String',null, 41)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(116,'display_name','display_name',null,'java.lang.String',null, 41)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(117,'value','value',null,'java.lang.String',null, 41)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(497,'SMART',117,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(498,'Smart',117,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(499,'SMART',117,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(500,'ISLAND',117,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(501,'Island',117,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(502,'ISLAND',117,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(503,'NASDAQ',117,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(504,'NASDAQ',117,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(505,'XNAS',117,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(506,'BATS',117,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(507,'Bats Global Mkts',117,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(508,'XCMO',117,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(509,'DRCTEDGE',117,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(510,'Direct Edge',117,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(511,'DRCTEDGE',117,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(512,'ARCA',117,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(513,'NYSE Arca',117,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(514,'ARCX',117,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(515,'NYSE',117,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(516,'New York SE',117,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(517,'XNYS',117,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(518,'GLOBEX',117,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(519,'Globex',117,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(520,'CMEX',117,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(521,'IDEAL',117,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(522,'iDeal',117,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(523,'IDEAL',117,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(524,'IDEALPRO',117,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(525,'iDeal Pro',117,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(526,'IDEALPRO',117,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(527,'DTB',117,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(528,'Eurex(DTB)',117,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(529,'DTB',117,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(530,'IBIS',117,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(531,'XETRA (IBIS)',117,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(532,'IBIS',117,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(533,'NSE',117,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(534,'Nat SE India',117,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(535,'NSE',117,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(536,'ECBOT',117,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(537,'CBOT (ECBOT)',117,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(538,'ECBOT',117,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(42,'CalculationType','CalculationType','code_decode','CalculationType::code_decode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(118,'code','code',null,'java.lang.String',null, 42)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(119,'display_name','display_name',null,'java.lang.String',null, 42)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(120,'value','value',null,'java.lang.String',null, 42)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(539,'LINEAR',120,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(540,'Linear',120,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(541,'LINEAR',120,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(542,'EXPONENTIAL',120,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(543,'Exponential',120,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(544,'EXPONENTIAL',120,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(545,'WEIGHTED',120,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(546,'Weighted',120,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(547,'WEIGHTED',120,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(548,'WEIGHTED_VOLUME',120,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(549,'Weighted Volume',120,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(550,'WEIGHTED_VOLUME',120,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(551,'TRIANGULAR',120,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(552,'Triangular',120,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(553,'TRIANGULAR',120,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(43,'PriceSource','PriceSource','code_decode','PriceSource::code_decode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(121,'code','code',null,'java.lang.String',null, 43)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(122,'display_name','display_name',null,'java.lang.String',null, 43)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(123,'value','value',null,'java.lang.String',null, 43)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(554,'1',123,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(555,'Close',123,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(556,'1',123,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(557,'2',123,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(558,'Open',123,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(559,'2',123,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(560,'3',123,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(561,'High',123,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(562,'3',123,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(563,'4',123,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(564,'Low',123,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(565,'4',123,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(566,'5',123,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(567,'(H+L)/2',123,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(568,'5',123,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(569,'6',123,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(570,'(H+L+C)/3',123,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(571,'6',123,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(572,'7',123,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(573,'(O+H+L+C)/4',123,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(574,'7',123,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(44,'TradestrategyStatus','TradestrategyStatus','code_decode','TradestrategyStatus::code_decode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(124,'code','code',null,'java.lang.String',null, 44)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(125,'display_name','display_name',null,'java.lang.String',null, 44)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(126,'value','value',null,'java.lang.String',null, 44)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(575,'TO',126,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(576,'Time Out',126,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(577,'TO',126,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(578,'GB',126,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(579,'Green Bar',126,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(580,'GB',126,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(581,'RB',126,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(582,'Red Bar',126,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(583,'RB',126,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(584,'PERCENT',126,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(585,'Percent range',126,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(586,'PERCENT',126,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(587,'TTBT',126,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(588,'Bar Tails',126,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(589,'TTBT',126,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(590,'NBB',126,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(591,'Narrow Body Bar',126,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(592,'NBB',126,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(593,'FIVE_MIN_LOW_BROKEN',126,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(594,'5min Low Broken',126,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(595,'FIVE_MIN_LOW_BROKEN',126,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(596,'FIVE_MIN_HIGH_BROKEN',126,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(597,'5min High Broken',126,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(598,'FIVE_MIN_HIGH_BROKEN',126,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(599,'OPEN',126,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(600,'Open position',126,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(601,'OPEN',126,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(602,'CLOSED',126,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(603,'Closed Position',126,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(604,'CLOSED',126,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(605,'CANCELLED',126,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(606,'Cancelled',126,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(607,'CANCELLED',126,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(45,'YesNo','YesNo','code_decode','YesNo::code_decode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(127,'code','code',null,'java.lang.String',null, 45)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(128,'display_name','display_name',null,'java.lang.String',null, 45)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(129,'value','value',null,'java.lang.String',null, 45)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(608,'true',129,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(609,'Yes',129,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(610,'true',129,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(611,'false',129,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(612,'No',129,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(613,'false',129,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(46,'UIComponentProperties','UIComponentProperties','ui_component','UIComponentProperties::ui_component')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(130,'tool_tip','tool_tip',null,'java.lang.String',null, 46)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(131,'image','image',null,'java.lang.String',null, 46)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(132,'code','code',null,'java.lang.String',null, 46)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(133,'method','method',null,'java.lang.String',null, 46)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(134,'mnemonic','mnemonic',null,'java.lang.String',null, 46)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(135,'display_name','display_name',null,'java.lang.String',null, 46)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(136,'enabled','enabled',null,'java.lang.String',null, 46)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(614,'Save',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(615,'save.gif',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(616,'SAVE',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(617,'doSave',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(618,'S',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(619,'Save',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(620,'true',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(621,'Open File',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(622,'openFile.gif',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(623,'OPEN_FILE',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(624,'doOpen',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(625,'F',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(626,'Open File',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(627,'true',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(628,'Print',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(629,'Prnt_up.gif',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(630,'PRINT',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(631,'doPrint',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(632,'P',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(633,'Print',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(634,'true',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(635,'Calc',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(636,'calculation.gif',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(637,'CALCULATE',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(638,'doCalculate',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(639,'C',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(640,'Calculate',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(641,'true',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(642,'Save',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(643,'save.gif',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(644,'SAVE_AS',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(645,'doSaveAs',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(646,'S',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(647,'Save As',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(648,'true',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(649,'New',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(650,'new.gif',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(651,'NEW',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(652,'doNew',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(653,'N',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(654,'New',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(655,'true',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(656,'Close',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(657,'closeFile.gif',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(658,'CLOSE_FILE',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(659,'doCloseFile',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(660,'o',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(661,'Close File',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(662,'true',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(663,'Help',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(664,'help.gif',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(665,'HELP',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(666,'doHelp',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(667,'H',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(668,'Help',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(669,'true',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(670,'Cut',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(671,'cut.gif',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(672,'CUT',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(673,'doCut',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(674,'u',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(675,'Cut',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(676,'true',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(677,'Copy',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(678,'copy.gif',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(679,'COPY',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(680,'doCopy',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(681,'C',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(682,'Copy',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(683,'true',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(684,'Paste',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(685,'paste.gif',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(686,'PASTE',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(687,'doPaste',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(688,'a',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(689,'Paste',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(690,'true',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(691,'Close',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(692,'close.gif',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(693,'CLOSE',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(694,'doClose',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(695,'',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(696,'Close',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(697,'true',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(698,'Results',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(699,'results.gif',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(700,'RESULTS',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(701,'doResults',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(702,'R',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(703,'Results',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(704,'true',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(705,'Connect',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(706,'',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(707,'CONNECT',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(708,'doConnect',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(709,'',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(710,'Connect',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(711,'true',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(712,'Disconnect',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(713,'',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(714,'DISCONNECT',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(715,'doDisconnect',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(716,'',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(717,'Disconnect',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(718,'true',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(719,'Refresh',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(720,'refresh.gif',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(721,'REFRESH',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(722,'doRefresh',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(723,'',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(724,'Refresh',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(725,'true',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(726,'Next',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(727,'',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(728,'NEXT',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(729,'doNext',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(730,'n',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(731,'Next',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(732,'true',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(733,'Prev',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(734,'',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(735,'PREV',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(736,'doPrevious',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(737,'v',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(738,'Prev',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(739,'true',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(740,'Fetch',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(741,'fetch.gif',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(742,'FETCH',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(743,'doFetch',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(744,'',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(745,'Fetch',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(746,'true',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(747,'Insert',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(748,'',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(749,'INSERT',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(750,'doInsert',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(751,'',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(752,'Insert',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(753,'true',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(754,'Commit',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(755,'',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(756,'COMMIT',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(757,'doCommit',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(758,'',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(759,'Commit',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(760,'true',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(761,'Cancel',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(762,'cancel.gif',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(763,'CANCEL',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(764,'doCancel',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(765,'',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(766,'Cancel',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(767,'true',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(768,'Search',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(769,'search.gif',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(770,'SEARCH',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(771,'doSearch',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(772,'',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(773,'Search',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(774,'true',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(775,'Clear',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(776,'clear.gif',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(777,'CLEAR',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(778,'doClear',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(779,'',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(780,'Clear',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(781,'true',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(782,'Print Prev',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(783,'printprev.gif',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(784,'PRINT_PREVIEW',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(785,'doPrintPreview',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(786,'',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(787,'Print Prev',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(788,'true',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(789,'Execute',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(790,'execute.gif',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(791,'EXECUTE',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(792,'doExecute',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(793,'',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(794,'Execute',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(795,'true',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(796,'Update',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(797,'',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(798,'UPDATE',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(799,'doUpdate',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(800,'',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(801,'Update',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(802,'true',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(803,'Delete',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(804,'delete.gif',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(805,'DELETE',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(806,'doDelete',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(807,'',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(808,'Delete',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(809,'true',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(810,'Tile All',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(811,'',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(812,'TILE_ALL',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(813,'doTileAll',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(814,'',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(815,'Tile All',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(816,'true',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(817,'Cascade All',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(818,'',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(819,'CASCADE_ALL',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(820,'doCascadeAll',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(821,'',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(822,'Cascade All',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(823,'true',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(824,'Close All',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(825,'closeall.gif',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(826,'CLOSE_ALL',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(827,'doCloseAll',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(828,'',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(829,'Close All',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(830,'true',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(831,'Cascade',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(832,'',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(833,'CASCADE',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(834,'doCascade',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(835,'',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(836,'Cascade',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(837,'true',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(838,'Test',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(839,'backwardarrow.gif',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(840,'TEST',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(841,'doTest',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(842,'',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(843,'Test',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(844,'true',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(845,'Run',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(846,'forwardarrow.gif',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(847,'RUN',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(848,'doRun',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(849,'',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(850,'Run',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(851,'true',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(852,'Get Data',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(853,'data.gif',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(854,'DATA',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(855,'doData',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(856,'',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(857,'Data',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(858,'true',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(859,'Properties',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(860,'gearsmall.gif',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(861,'PROPERTIES',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(862,'doProperties',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(863,'',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(864,'Properties',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(865,'true',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(866,'Contents',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(867,'',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(868,'CONTENTS',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(869,'doContents',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(870,'',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(871,'Contents',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(872,'true',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(873,'About',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(874,'',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(875,'ABOUT',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(876,'doAbout',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(877,'',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(878,'About',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(879,'true',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(880,'Print Options',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(881,'gearsmall.gif',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(882,'PRINT_OPTIONS',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(883,'doPrintOptions',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(884,'u',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(885,'Print Options',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(886,'true',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(887,'Exit',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(888,'',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(889,'EXIT',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(890,'doExit',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(891,'',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(892,'Exit',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(893,'true',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(894,'Undo',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(895,'',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(896,'UNDO',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(897,'doUndo',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(898,'',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(899,'Undo',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(900,'true',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(901,'Redo',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(902,'',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(903,'REDO',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(904,'doRedo',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(905,'',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(906,'Redo',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(907,'true',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(908,'Find',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(909,'',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(910,'FIND',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(911,'doFind',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(912,'',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(913,'Find',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(914,'true',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(915,'Replace',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(916,'',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(917,'REPLACE',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(918,'doReplace',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(919,'',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(920,'Replace',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(921,'true',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(922,'Transfer',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(923,'',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(924,'TRANSFER',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(925,'doTransfer',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(926,'',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(927,'Transfer',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(928,'true',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(929,'Remove',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(930,'',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(931,'REMOVE',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(932,'doRemove',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(933,'',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(934,'Remove',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(935,'true',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(936,'Disclaimer',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(937,'',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(938,'DISCLAIMER',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(939,'doDisclaimer',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(940,'',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(941,'Disclaimer',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(942,'true',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(943,'Compile',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(944,'gear.gif',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(945,'COMPILE',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(946,'doCompile',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(947,'',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(948,'Compile',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(949,'true',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(950,'Re-Assign strategies for selected tradingdays.',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(951,'gear.gif',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(952,'REASSIGN',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(953,'doReAssign',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(954,'',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(955,'Re-Assign',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(956,'true',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(957,'Strategy Parameters',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(958,'gear.gif',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(959,'STRATEGY_PARMS',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(960,'doStrategyParameters',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(961,'',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(962,'Strategy Parms',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(963,'true',136,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(47,'Profile','Profile','dao_decode','Profile::dao_decode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(137,'code','code',null,'java.lang.String',null, 47)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(138,'display_name','display_name',null,'java.lang.String',null, 47)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(964,'org.trade.core.persistent.portfolio.Portfolio',137,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(965,'getName',138,null)//
COMMIT//