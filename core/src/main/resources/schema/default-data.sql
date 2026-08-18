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

INSERT INTO codetype (id, name, type, category, description) VALUES(12,'DAOAccount','DAOAccount','DAODecode','DAOAccount::DAODecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(34,'code','code',null,'java.lang.String',null, 12)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(35,'display_name','display_name',null,'java.lang.String',null, 12)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(36,'value','value',null,'java.lang.String',null, 12)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(50,'org.trade.core.persistent.account.Account',34,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(51,'getAccountNumber',35,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(52,'getAccountNumber',36,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(13,'DAOProfile','DAOProfile','DAODecode','DAOProfile::DAODecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(37,'code','code',null,'java.lang.String',null, 13)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(38,'display_name','display_name',null,'java.lang.String',null, 13)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(39,'value','value',null,'java.lang.String',null, 13)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(53,'org.trade.core.persistent.portfolio.Portfolio',37,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(54,'getName',38,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(55,'getName',39,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(14,'DAOEntryLimit','DAOEntryLimit','DAODecode','DAOEntryLimit::DAODecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(40,'code','code',null,'java.lang.String',null, 14)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(41,'display_name','display_name',null,'java.lang.String',null, 14)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(42,'value','value',null,'java.lang.String',null, 14)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(56,'org.trade.core.persistent.codetype.Entrylimit',40,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(57,'getId',41,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(58,'getId',42,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(15,'DAOStrategy','DAOStrategy','DAODecode','DAOStrategy::DAODecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(43,'code','code',null,'java.lang.String',null, 15)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(44,'display_name','display_name',null,'java.lang.String',null, 15)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(45,'value','value',null,'java.lang.String',null, 15)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(59,'org.trade.core.persistent.strategy.Strategy',43,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(60,'getName',44,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(61,'getName',45,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(16,'DAOStrategyManager','DAOStrategyManager','DAODecode','DAOStrategyManager::DAODecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(46,'code','code',null,'java.lang.String',null, 16)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(47,'display_name','display_name',null,'java.lang.String',null, 16)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(48,'value','value',null,'java.lang.String',null, 16)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(62,'org.trade.core.persistent.strategy.Strategy',46,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(63,'getName',47,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(64,'getName',48,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(17,'DAOPortfolio','DAOPortfolio','DAODecode','DAOPortfolio::DAODecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(49,'code','code',null,'java.lang.String',null, 17)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(50,'display_name','display_name',null,'java.lang.String',null, 17)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(51,'value','value',null,'java.lang.String',null, 17)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(65,'org.trade.core.persistent.portfolio.Portfolio',49,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(66,'getName',50,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(67,'getName',51,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(18,'DAOFAGroup','DAOFAGroup','DAODecode','DAOFAGroup::DAODecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(52,'code','code',null,'java.lang.String',null, 18)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(53,'display_name','display_name',null,'java.lang.String',null, 18)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(54,'value','value',null,'java.lang.String',null, 18)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(68,'org.trade.core.persistent.portfolio.Portfolio',52,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(69,'getName',53,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(70,'getName',54,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(19,'ReferenceTable','ReferenceTable','CodeDecode','ReferenceTable::CodeDecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(55,'code','code',null,'java.lang.String',null, 19)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(56,'display_name','display_name',null,'java.lang.String',null, 19)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(57,'value','value',null,'java.lang.String',null, 19)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(71,'org.trade.core.persistent.strategy.Strategy',55,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(72,'Strategy',56,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(73,'Strategy',57,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(74,'org.trade.core.persistent.portfolio.Portfolio',55,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(75,'Portfolio',56,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(76,'Portfolio',57,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(77,'org.trade.core.persistent.codetype.IndicatorParameters',55,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(78,'Indicator Parameters',56,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(79,'IndicatorParameters',57,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(80,'org.trade.core.persistent.strategy.StrategyParameters',55,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(81,'Strategy Parameters',56,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(82,'StrategyParameters',57,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(83,'org.trade.core.persistent.codetype.Entrylimit',55,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(84,'Entrylimit',56,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(85,'Entrylimit',57,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(86,'org.trade.core.persistent.codetype.CodeType',55,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(87,'Decode',56,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(88,'Decode',57,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(20,'Action','Action','CodeDecode','Action::CodeDecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(58,'code','code',null,'java.lang.String',null, 20)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(59,'display_name','display_name',null,'java.lang.String',null, 20)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(60,'value','value',null,'java.lang.String',null, 20)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(89,'BUY',58,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(90,'Buy',59,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(91,'BUY',60,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(92,'SELL',58,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(93,'Sell',59,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(94,'SELL',60,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(21,'ContentType','ContentType','CodeDecode','ContentType::CodeDecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(61,'code','code',null,'java.lang.String',null, 21)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(62,'display_name','display_name',null,'java.lang.String',null, 21)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(63,'value','value',null,'java.lang.String',null, 21)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(95,'java',61,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(96,'Java',62,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(97,'text/java',63,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(98,'js',61,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(99,'Javascript',62,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(100,'text/javascript',63,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(101,'txt',61,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(102,'Text',62,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(103,'text/rtf',63,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(22,'OverrideConstraints','OverrideConstraints','CodeDecode','OverrideConstraints::CodeDecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(64,'code','code',null,'java.lang.String',null, 22)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(65,'display_name','display_name',null,'java.lang.String',null, 22)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(66,'value','value',null,'java.lang.String',null, 22)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(104,'0',64,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(105,'No',65,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(106,'0',66,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(107,'1',64,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(108,'Yes',65,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(109,'1',66,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(23,'Side','Side','CodeDecode','Side::CodeDecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(67,'code','code',null,'java.lang.String',null, 23)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(68,'display_name','display_name',null,'java.lang.String',null, 23)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(69,'value','value',null,'java.lang.String',null, 23)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(110,'BOT',67,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(111,'Long',68,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(112,'BOT',69,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(113,'SLD',67,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(114,'Short',68,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(115,'SLD',69,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(24,'OrderStatus','OrderStatus','CodeDecode','OrderStatus::CodeDecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(70,'code','code',null,'java.lang.String',null, 24)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(71,'display_name','display_name',null,'java.lang.String',null, 24)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(72,'value','value',null,'java.lang.String',null, 24)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(116,'UNSUBMIT',70,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(117,'UnSubmitted',71,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(118,'UNSUBMIT',72,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(119,'PENDINGSUBMIT',70,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(120,'Pending Submit',71,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(121,'PENDINGSUBMIT',72,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(122,'PENDINGCANCEL',70,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(123,'Pending Cancel',71,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(124,'PENDINGCANCEL',72,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(125,'PRESUBMITTED',70,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(126,'Pre Submit',71,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(127,'PRESUBMITTED',72,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(128,'SUBMITTED',70,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(129,'Submitted',71,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(130,'SUBMITTED',72,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(131,'CANCELLED',70,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(132,'Cancelled',71,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(133,'CANCELLED',72,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(134,'FILLED',70,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(135,'Filled',71,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(136,'FILLED',72,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(137,'INACTIVE',70,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(138,'Inactive',71,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(139,'INACTIVE',72,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(140,'PARTIALFILLED',70,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(141,'Partial Filled',71,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(142,'PARTIALFILLED',72,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(25,'OrderType','OrderType','CodeDecode','OrderType::CodeDecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(73,'code','code',null,'java.lang.String',null, 25)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(74,'display_name','display_name',null,'java.lang.String',null, 25)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(75,'value','value',null,'java.lang.String',null, 25)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(143,'STPLMT',73,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(144,'Stop Lmt',74,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(145,'STPLMT',75,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(146,'LMT',73,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(147,'Limit',74,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(148,'LMT',75,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(149,'STP',73,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(150,'Stop',74,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(151,'STP',75,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(152,'MKT',73,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(153,'Market',74,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(154,'MKT',75,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(155,'MKTCLS',73,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(156,'Market On Cls',74,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(157,'MKTCLS',75,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(158,'LMTCLS',73,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(159,'Limit on Cls',74,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(160,'LMTCLS',75,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(161,'PEGMKT',73,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(162,'Peg Mkt',74,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(163,'PEGMKT',75,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(164,'SCALE',73,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(165,'Scale',74,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(166,'SCALE',75,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(167,'TRAIL',73,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(168,'Trail',74,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(169,'TRAIL',75,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(170,'REL',73,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(171,'Relative',74,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(172,'REL',75,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(173,'VWAP',73,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(174,'Vwap',74,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(175,'VWAP',75,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(176,'TRAILLIMIT',73,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(177,'Trail Lmt',74,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(178,'TRAILLIMIT',75,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(26,'MarketBias','MarketBias','CodeDecode','MarketBias::CodeDecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(76,'code','code',null,'java.lang.String',null, 26)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(77,'display_name','display_name',null,'java.lang.String',null, 26)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(78,'value','value',null,'java.lang.String',null, 26)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(179,'S',76,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(180,'Short',77,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(181,'S',78,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(182,'L',76,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(183,'Long',77,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(184,'L',78,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(185,'N',76,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(186,'Neutral',77,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(187,'N',78,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(27,'UIIndicatorSeries','UIIndicatorSeries','CodeDecode','UIIndicatorSeries::CodeDecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(79,'code','code',null,'java.lang.String',null, 27)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(80,'display_name','display_name',null,'java.lang.String',null, 27)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(81,'value','value',null,'java.lang.String',null, 27)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(188,'AverageTrueRangeSeries',79,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(189,'ATR',80,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(190,'AverageTrueRangeSeries',81,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(191,'BollingerBandsSeries',79,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(192,'BollingerBands',80,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(193,'BollingerBandsSeries',81,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(194,'CandleSeries',79,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(195,'Candle',80,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(196,'CandleSeries',81,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(197,'CommodityChannelIndexSeries',79,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(198,'CCI',80,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(199,'CommodityChannelIndexSeries',81,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(200,'HeikinAshiSeries',79,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(201,'HeikinAshi',80,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(202,'HeikinAshiSeries',81,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(203,'MACDSeries',79,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(204,'MACD',80,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(205,'MACDSeries',81,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(206,'MoneyFlowIndexSeries',79,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(207,'MFI',80,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(208,'MoneyFlowIndexSeries',81,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(209,'MovingAverageSeries',79,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(210,'MovingAverage',80,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(211,'MovingAverageSeries',81,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(212,'PivotSeries',79,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(213,'Pivot',80,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(214,'PivotSeries',81,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(215,'RelativeStrengthIndexSeries',79,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(216,'RSI',80,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(217,'RelativeStrengthIndexSeries',81,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(218,'VolumeSeries',79,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(219,'Volume',80,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(220,'VolumeSeries',81,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(221,'VostroSeries',79,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(222,'Vostro',80,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(223,'VostroSeries',81,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(224,'VwapSeries',79,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(225,'Vwap',80,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(226,'VwapSeries',81,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(227,'StochasticOscillatorSeries',79,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(228,'% K/R',80,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(229,'StochasticOscillatorSeries',81,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(28,'Currency','Currency','CodeDecode','Currency::CodeDecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(82,'code','code',null,'java.lang.String',null, 28)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(83,'display_name','display_name',null,'java.lang.String',null, 28)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(84,'value','value',null,'java.lang.String',null, 28)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(230,'USD',82,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(231,'US $',83,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(232,'USD',84,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(233,'EUR',82,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(234,'Euro $',83,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(235,'EUR',84,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(236,'GBP',82,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(237,'GB Pound',83,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(238,'GBP',84,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(239,'CAD',82,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(240,'Canada $',83,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(241,'CAD',84,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(242,'JPY',82,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(243,'Japan Yen',83,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(244,'JPY',84,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(245,'AUD',82,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(246,'Australian $',83,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(247,'AUD',84,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(248,'CHF',82,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(249,'Swiss Franc',83,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(250,'CHF',84,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(251,'INR',82,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(252,'Indian Rupee',83,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(253,'INR',84,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(29,'TimeInForce','TimeInForce','CodeDecode','TimeInForce::CodeDecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(85,'code','code',null,'java.lang.String',null, 29)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(86,'display_name','display_name',null,'java.lang.String',null, 29)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(87,'value','value',null,'java.lang.String',null, 29)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(254,'DAY',85,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(255,'Day',86,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(256,'DAY',87,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(257,'GTC',85,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(258,'Good till Cancel',86,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(259,'GTC',87,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(260,'IOC',85,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(261,'Immediate-or Cancel',86,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(262,'IOC',87,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(263,'GTD',85,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(264,'Good till date',86,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(265,'GTD',87,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(30,'DataType','DataType','CodeDecode','DataType::CodeDecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(88,'code','code',null,'java.lang.String',null, 30)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(89,'display_name','display_name',null,'java.lang.String',null, 30)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(90,'value','value',null,'java.lang.String',null, 30)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(266,'java.lang.String',88,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(267,'String',89,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(268,'java.lang.String',90,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(269,'java.lang.Integer',88,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(270,'Integer',89,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(271,'java.lang.Integer',90,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(272,'java.lang.Long',88,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(273,'Long',89,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(274,'java.lang.Long',90,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(275,'java.math.BigDecimal',88,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(276,'Decimal',89,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(277,'java.math.BigDecimal',90,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(278,'java.lang.Boolean',88,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(279,'Boolean',89,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(280,'java.lang.Boolean',90,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(281,'java.time.LocalDate',88,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(282,'Date',89,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(283,'java.time.LocalDate',90,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(31,'BarSize','BarSize','CodeDecode','BarSize::CodeDecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(91,'code','code',null,'java.lang.String',null, 31)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(92,'display_name','display_name',null,'java.lang.String',null, 31)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(93,'value','value',null,'java.lang.String',null, 31)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(284,'_1_min',91,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(285,'1 min',92,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(286,'60',93,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(287,'_2_mins',91,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(288,'2 mins',92,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(289,'120',93,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(290,'_5_mins',91,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(291,'5 mins',92,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(292,'300',93,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(293,'_10_mins',91,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(294,'10 mins',92,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(295,'600',93,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(296,'_15_mins',91,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(297,'15 mins',92,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(298,'900',93,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(299,'_20_mins',91,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(300,'20 mins',92,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(301,'1200',93,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(302,'_30_mins',91,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(303,'30 mins',92,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(304,'1800',93,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(305,'_1_hour',91,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(306,'1 hour',92,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(307,'3600',93,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(308,'_1_day',91,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(309,'1 day',92,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(310,'86400',93,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(311,'_30_secs',91,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(312,'30 secs',92,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(313,'30',93,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(32,'OCAType','OCAType','CodeDecode','OCAType::CodeDecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(94,'code','code',null,'java.lang.String',null, 32)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(95,'display_name','display_name',null,'java.lang.String',null, 32)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(96,'value','value',null,'java.lang.String',null, 32)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(314,'2',94,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(315,'Reduce remaining orders',95,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(316,'2',96,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(317,'1',94,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(318,'Cancel all remaining',95,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(319,'1',96,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(320,'3',94,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(321,'Prop reduce remaining',95,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(322,'3',96,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(33,'SECIdType','SECIdType','CodeDecode','SECIdType::CodeDecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(97,'code','code',null,'java.lang.String',null, 33)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(98,'display_name','display_name',null,'java.lang.String',null, 33)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(99,'value','value',null,'java.lang.String',null, 33)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(323,'ISIN',97,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(324,'Intl Sec Id #',98,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(325,'ISIN',99,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(326,'SEDOL',97,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(327,'London SE',98,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(328,'SEDOL',99,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(329,'CUSIP',97,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(330,'Cusip',98,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(331,'CUSIP',99,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(332,'RIC',97,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(333,'Reuters Inst Code',98,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(334,'RIC',99,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(335,'SYMBOL',97,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(336,'Symbol',98,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(337,'SYMBOL',99,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(34,'TriggerMethod','TriggerMethod','CodeDecode','TriggerMethod::CodeDecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(100,'code','code',null,'java.lang.String',null, 34)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(101,'display_name','display_name',null,'java.lang.String',null, 34)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(102,'value','value',null,'java.lang.String',null, 34)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(338,'0',100,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(339,'Default',101,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(340,'0',102,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(341,'1',100,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(342,'Double bid/ask',101,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(343,'1',102,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(344,'2',100,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(345,'Last',101,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(346,'2',102,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(347,'3',100,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(348,'Double last',101,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(349,'3',102,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(350,'4',100,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(351,'Bid/Ask',101,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(352,'4',102,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(353,'7',100,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(354,'Last or Bid/Ask',101,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(355,'7',102,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(356,'8',100,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(357,'Mid Point',101,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(358,'8',102,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(35,'MarketBar','MarketBar','CodeDecode','MarketBar::CodeDecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(103,'code','code',null,'java.lang.String',null, 35)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(104,'display_name','display_name',null,'java.lang.String',null, 35)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(105,'value','value',null,'java.lang.String',null, 35)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(359,'+NRB',103,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(360,'+NRB',104,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(361,'+NRB',105,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(362,'-NRB',103,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(363,'-NRB',104,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(364,'-NRB',105,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(365,'+NRBBT',103,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(366,'+NRB BT',104,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(367,'+NRBBT',105,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(368,'+NRBTT',103,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(369,'+NRB TT',104,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(370,'+NRBTT',105,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(371,'-NRBBT',103,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(372,'-NRB BT',104,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(373,'-NRBBT',105,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(374,'-NRBTT',103,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(375,'-NRB TT',104,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(376,'-NRBTT',105,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(377,'+DD',103,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(378,'Darling Doji',104,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(379,'+DD',105,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(380,'-DD',103,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(381,'Deadly Doji',104,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(382,'-DD',105,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(383,'+WRB',103,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(384,'+WRB',104,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(385,'+WRB',105,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(386,'-WRB',103,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(387,'-WRB',104,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(388,'-WRB',105,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(389,'+1010',103,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(390,'+WRB +/-10% Tail',104,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(391,'+1010',105,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(392,'-1010',103,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(393,'-WRB +/-10% Tail',104,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(394,'-1010',105,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(395,'+2020',103,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(396,'+WRB +/-20% Tail',104,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(397,'+2020',105,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(398,'-2020',103,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(399,'-WRB +/-20% Tail',104,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(400,'-2020',105,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(401,'NONE',103,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(402,'None',104,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(403,'NONE',105,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(36,'OptionType','OptionType','CodeDecode','OptionType::CodeDecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(106,'code','code',null,'java.lang.String',null, 36)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(107,'display_name','display_name',null,'java.lang.String',null, 36)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(108,'value','value',null,'java.lang.String',null, 36)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(404,'C',106,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(405,'Call',107,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(406,'C',108,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(407,'P',106,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(408,'Put',107,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(409,'P',108,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(37,'ChartDays','ChartDays','CodeDecode','ChartDays::CodeDecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(109,'code','code',null,'java.lang.String',null, 37)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(110,'display_name','display_name',null,'java.lang.String',null, 37)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(111,'value','value',null,'java.lang.String',null, 37)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(410,'1',109,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(411,'1 D',110,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(412,'1',111,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(413,'2',109,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(414,'2 D',110,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(415,'2',111,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(416,'7',109,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(417,'1 W',110,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(418,'7',111,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(419,'15',109,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(420,'2 W',110,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(421,'15',111,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(422,'30',109,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(423,'1 M',110,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(424,'30',111,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(425,'60',109,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(426,'2 M',110,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(427,'60',111,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(428,'90',109,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(429,'3 M',110,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(430,'90',111,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(431,'180',109,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(432,'6 M',110,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(433,'180',111,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(434,'365',109,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(435,'1 Y',110,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(436,'365',111,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(38,'Tier','Tier','CodeDecode','Tier::CodeDecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(112,'code','code',null,'java.lang.String',null, 38)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(113,'display_name','display_name',null,'java.lang.String',null, 38)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(114,'value','value',null,'java.lang.String',null, 38)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(437,'1',112,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(438,'1',113,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(439,'1',114,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(440,'2',112,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(441,'2',113,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(442,'2',114,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(443,'3',112,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(444,'3',113,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(445,'3',114,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(39,'AllocationMethod','AllocationMethod','CodeDecode','AllocationMethod::CodeDecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(115,'code','code',null,'java.lang.String',null, 39)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(116,'display_name','display_name',null,'java.lang.String',null, 39)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(117,'value','value',null,'java.lang.String',null, 39)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(446,'AvailableEquity',115,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(447,'AvailableEquity',116,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(448,'AvailableEquity',117,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(449,'PctChange',115,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(450,'PctChange',116,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(451,'PctChange',117,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(452,'NetLiq',115,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(453,'NetLiq',116,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(454,'NetLiq',117,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(455,'EqualQuantity',115,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(456,'EqualQuantity',116,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(457,'EqualQuantity',117,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(458,'1',115,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(459,'Percentages',116,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(460,'1',117,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(461,'2',115,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(462,'Ratios',116,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(463,'2',117,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(464,'3',115,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(465,'Shares',116,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(466,'3',117,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(40,'AccountType','AccountType','CodeDecode','AccountType::CodeDecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(118,'code','code',null,'java.lang.String',null, 40)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(119,'display_name','display_name',null,'java.lang.String',null, 40)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(120,'value','value',null,'java.lang.String',null, 40)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(467,'CORPORATION',118,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(468,'Institution',119,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(469,'CORPORATION',120,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(470,'INDIVIDUAL',118,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(471,'Individual',119,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(472,'INDIVIDUAL',120,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(473,'IRA-TRADITIONAL',118,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(474,'IRA Traditional',119,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(475,'IRA-TRADITIONAL',120,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(476,'IRA-ROTH',118,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(477,'IRA Roth',119,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(478,'IRA-ROTH',120,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(479,'TRUST',118,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(480,'Trust',119,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(481,'TRUST',120,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(482,'JOINT',118,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(483,'Joint',119,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(484,'JOINT',120,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(41,'SECType','SECType','CodeDecode','SECType::CodeDecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(121,'code','code',null,'java.lang.String',null, 41)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(122,'display_name','display_name',null,'java.lang.String',null, 41)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(123,'value','value',null,'java.lang.String',null, 41)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(485,'STK',121,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(486,'Stock',122,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(487,'STK',123,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(488,'OPT',121,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(489,'Option',122,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(490,'OPT',123,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(491,'FUT',121,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(492,'Future',122,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(493,'FUT',123,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(494,'CASH',121,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(495,'Cash',122,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(496,'CASH',123,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(497,'IND',121,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(498,'indices',122,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(499,'IND',123,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(500,'FOP',121,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(501,'Fut Opt',122,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(502,'FOP',123,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(503,'BAG',121,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(504,'Bag',122,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(505,'BAG',123,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(42,'Exchange','Exchange','CodeDecode','Exchange::CodeDecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(124,'code','code',null,'java.lang.String',null, 42)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(125,'display_name','display_name',null,'java.lang.String',null, 42)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(126,'value','value',null,'java.lang.String',null, 42)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(506,'SMART',124,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(507,'Smart',125,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(508,'SMART',126,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(509,'ISLAND',124,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(510,'Island',125,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(511,'ISLAND',126,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(512,'NASDAQ',124,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(513,'NASDAQ',125,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(514,'XNAS',126,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(515,'BATS',124,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(516,'Bats Global Mkts',125,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(517,'XCMO',126,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(518,'DRCTEDGE',124,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(519,'Direct Edge',125,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(520,'DRCTEDGE',126,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(521,'ARCA',124,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(522,'NYSE Arca',125,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(523,'ARCX',126,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(524,'NYSE',124,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(525,'New York SE',125,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(526,'XNYS',126,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(527,'GLOBEX',124,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(528,'Globex',125,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(529,'CMEX',126,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(530,'IDEAL',124,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(531,'iDeal',125,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(532,'IDEAL',126,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(533,'IDEALPRO',124,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(534,'iDeal Pro',125,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(535,'IDEALPRO',126,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(536,'DTB',124,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(537,'Eurex(DTB)',125,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(538,'DTB',126,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(539,'IBIS',124,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(540,'XETRA (IBIS)',125,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(541,'IBIS',126,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(542,'NSE',124,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(543,'Nat SE India',125,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(544,'NSE',126,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(545,'ECBOT',124,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(546,'CBOT (ECBOT)',125,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(547,'ECBOT',126,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(43,'CalculationType','CalculationType','CodeDecode','CalculationType::CodeDecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(127,'code','code',null,'java.lang.String',null, 43)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(128,'display_name','display_name',null,'java.lang.String',null, 43)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(129,'value','value',null,'java.lang.String',null, 43)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(548,'LINEAR',127,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(549,'Linear',128,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(550,'LINEAR',129,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(551,'EXPONENTIAL',127,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(552,'Exponential',128,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(553,'EXPONENTIAL',129,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(554,'WEIGHTED',127,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(555,'Weighted',128,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(556,'WEIGHTED',129,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(557,'WEIGHTED_VOLUME',127,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(558,'Weighted Volume',128,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(559,'WEIGHTED_VOLUME',129,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(560,'TRIANGULAR',127,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(561,'Triangular',128,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(562,'TRIANGULAR',129,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(44,'PriceSource','PriceSource','CodeDecode','PriceSource::CodeDecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(130,'code','code',null,'java.lang.String',null, 44)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(131,'display_name','display_name',null,'java.lang.String',null, 44)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(132,'value','value',null,'java.lang.String',null, 44)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(563,'1',130,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(564,'Close',131,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(565,'1',132,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(566,'2',130,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(567,'Open',131,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(568,'2',132,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(569,'3',130,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(570,'High',131,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(571,'3',132,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(572,'4',130,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(573,'Low',131,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(574,'4',132,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(575,'5',130,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(576,'(H+L)/2',131,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(577,'5',132,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(578,'6',130,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(579,'(H+L+C)/3',131,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(580,'6',132,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(581,'7',130,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(582,'(O+H+L+C)/4',131,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(583,'7',132,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(45,'TradestrategyStatus','TradestrategyStatus','CodeDecode','TradestrategyStatus::CodeDecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(133,'code','code',null,'java.lang.String',null, 45)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(134,'display_name','display_name',null,'java.lang.String',null, 45)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(135,'value','value',null,'java.lang.String',null, 45)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(584,'TO',133,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(585,'Time Out',134,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(586,'TO',135,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(587,'GB',133,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(588,'Green Bar',134,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(589,'GB',135,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(590,'RB',133,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(591,'Red Bar',134,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(592,'RB',135,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(593,'PERCENT',133,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(594,'Percent range',134,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(595,'PERCENT',135,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(596,'TTBT',133,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(597,'Bar Tails',134,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(598,'TTBT',135,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(599,'NBB',133,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(600,'Narrow Body Bar',134,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(601,'NBB',135,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(602,'FIVE_MIN_LOW_BROKEN',133,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(603,'5min Low Broken',134,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(604,'FIVE_MIN_LOW_BROKEN',135,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(605,'FIVE_MIN_HIGH_BROKEN',133,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(606,'5min High Broken',134,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(607,'FIVE_MIN_HIGH_BROKEN',135,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(608,'OPEN',133,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(609,'Open position',134,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(610,'OPEN',135,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(611,'CLOSED',133,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(612,'Closed Position',134,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(613,'CLOSED',135,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(614,'CANCELLED',133,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(615,'Cancelled',134,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(616,'CANCELLED',135,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(46,'YesNo','YesNo','CodeDecode','YesNo::CodeDecode')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(136,'code','code',null,'java.lang.String',null, 46)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(137,'display_name','display_name',null,'java.lang.String',null, 46)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(138,'value','value',null,'java.lang.String',null, 46)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(617,'true',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(618,'Yes',137,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(619,'true',138,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(620,'false',136,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(621,'No',137,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(622,'false',138,null)//
INSERT INTO codetype (id, name, type, category, description) VALUES(47,'UIComponentProperties','UIComponentProperties','UIComponent','UIComponentProperties::UIComponent')//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(139,'tool_tip','tool_tip',null,'java.lang.String',null, 47)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(140,'image','image',null,'java.lang.String',null, 47)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(141,'code','code',null,'java.lang.String',null, 47)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(142,'method','method',null,'java.lang.String',null, 47)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(143,'mnemonic','mnemonic',null,'java.lang.String',null, 47)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(144,'display_name','display_name',null,'java.lang.String',null, 47)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, code_type_id) VALUES(145,'enabled','enabled',null,'java.lang.String',null, 47)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(623,'Save',139,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(624,'save.gif',140,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(625,'SAVE',141,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(626,'doSave',142,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(627,'S',143,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(628,'Save',144,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(629,'true',145,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(630,'Open File',139,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(631,'openFile.gif',140,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(632,'OPEN_FILE',141,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(633,'doOpen',142,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(634,'F',143,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(635,'Open File',144,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(636,'true',145,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(637,'Print',139,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(638,'Prnt_up.gif',140,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(639,'PRINT',141,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(640,'doPrint',142,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(641,'P',143,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(642,'Print',144,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(643,'true',145,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(644,'Calc',139,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(645,'calculation.gif',140,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(646,'CALCULATE',141,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(647,'doCalculate',142,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(648,'C',143,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(649,'Calculate',144,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(650,'true',145,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(651,'Save',139,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(652,'save.gif',140,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(653,'SAVE_AS',141,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(654,'doSaveAs',142,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(655,'S',143,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(656,'Save As',144,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(657,'true',145,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(658,'New',139,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(659,'new.gif',140,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(660,'NEW',141,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(661,'doNew',142,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(662,'N',143,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(663,'New',144,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(664,'true',145,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(665,'Close',139,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(666,'closeFile.gif',140,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(667,'CLOSE_FILE',141,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(668,'doCloseFile',142,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(669,'o',143,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(670,'Close File',144,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(671,'true',145,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(672,'Help',139,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(673,'help.gif',140,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(674,'HELP',141,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(675,'doHelp',142,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(676,'H',143,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(677,'Help',144,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(678,'true',145,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(679,'Cut',139,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(680,'cut.gif',140,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(681,'CUT',141,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(682,'doCut',142,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(683,'u',143,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(684,'Cut',144,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(685,'true',145,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(686,'Copy',139,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(687,'copy.gif',140,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(688,'COPY',141,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(689,'doCopy',142,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(690,'C',143,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(691,'Copy',144,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(692,'true',145,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(693,'Paste',139,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(694,'paste.gif',140,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(695,'PASTE',141,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(696,'doPaste',142,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(697,'a',143,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(698,'Paste',144,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(699,'true',145,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(700,'Close',139,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(701,'close.gif',140,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(702,'CLOSE',141,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(703,'doClose',142,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(704,'',143,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(705,'Close',144,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(706,'true',145,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(707,'Results',139,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(708,'results.gif',140,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(709,'RESULTS',141,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(710,'doResults',142,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(711,'R',143,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(712,'Results',144,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(713,'true',145,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(714,'Connect',139,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(715,'',140,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(716,'CONNECT',141,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(717,'doConnect',142,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(718,'',143,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(719,'Connect',144,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(720,'true',145,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(721,'Disconnect',139,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(722,'',140,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(723,'DISCONNECT',141,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(724,'doDisconnect',142,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(725,'',143,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(726,'Disconnect',144,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(727,'true',145,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(728,'Refresh',139,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(729,'refresh.gif',140,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(730,'REFRESH',141,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(731,'doRefresh',142,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(732,'',143,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(733,'Refresh',144,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(734,'true',145,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(735,'Next',139,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(736,'',140,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(737,'NEXT',141,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(738,'doNext',142,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(739,'n',143,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(740,'Next',144,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(741,'true',145,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(742,'Prev',139,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(743,'',140,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(744,'PREV',141,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(745,'doPrevious',142,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(746,'v',143,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(747,'Prev',144,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(748,'true',145,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(749,'Fetch',139,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(750,'fetch.gif',140,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(751,'FETCH',141,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(752,'doFetch',142,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(753,'',143,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(754,'Fetch',144,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(755,'true',145,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(756,'Insert',139,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(757,'',140,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(758,'INSERT',141,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(759,'doInsert',142,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(760,'',143,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(761,'Insert',144,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(762,'true',145,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(763,'Commit',139,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(764,'',140,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(765,'COMMIT',141,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(766,'doCommit',142,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(767,'',143,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(768,'Commit',144,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(769,'true',145,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(770,'Cancel',139,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(771,'cancel.gif',140,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(772,'CANCEL',141,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(773,'doCancel',142,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(774,'',143,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(775,'Cancel',144,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(776,'true',145,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(777,'Search',139,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(778,'search.gif',140,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(779,'SEARCH',141,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(780,'doSearch',142,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(781,'',143,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(782,'Search',144,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(783,'true',145,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(784,'Clear',139,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(785,'clear.gif',140,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(786,'CLEAR',141,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(787,'doClear',142,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(788,'',143,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(789,'Clear',144,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(790,'true',145,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(791,'Print Prev',139,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(792,'printprev.gif',140,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(793,'PRINT_PREVIEW',141,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(794,'doPrintPreview',142,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(795,'',143,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(796,'Print Prev',144,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(797,'true',145,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(798,'Execute',139,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(799,'execute.gif',140,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(800,'EXECUTE',141,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(801,'doExecute',142,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(802,'',143,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(803,'Execute',144,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(804,'true',145,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(805,'Update',139,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(806,'',140,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(807,'UPDATE',141,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(808,'doUpdate',142,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(809,'',143,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(810,'Update',144,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(811,'true',145,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(812,'Delete',139,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(813,'delete.gif',140,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(814,'DELETE',141,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(815,'doDelete',142,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(816,'',143,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(817,'Delete',144,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(818,'true',145,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(819,'Tile All',139,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(820,'',140,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(821,'TILE_ALL',141,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(822,'doTileAll',142,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(823,'',143,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(824,'Tile All',144,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(825,'true',145,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(826,'Cascade All',139,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(827,'',140,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(828,'CASCADE_ALL',141,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(829,'doCascadeAll',142,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(830,'',143,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(831,'Cascade All',144,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(832,'true',145,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(833,'Close All',139,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(834,'closeall.gif',140,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(835,'CLOSE_ALL',141,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(836,'doCloseAll',142,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(837,'',143,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(838,'Close All',144,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(839,'true',145,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(840,'Cascade',139,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(841,'',140,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(842,'CASCADE',141,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(843,'doCascade',142,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(844,'',143,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(845,'Cascade',144,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(846,'true',145,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(847,'Test',139,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(848,'backwardarrow.gif',140,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(849,'TEST',141,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(850,'doTest',142,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(851,'',143,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(852,'Test',144,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(853,'true',145,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(854,'Run',139,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(855,'forwardarrow.gif',140,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(856,'RUN',141,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(857,'doRun',142,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(858,'',143,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(859,'Run',144,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(860,'true',145,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(861,'Get Data',139,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(862,'data.gif',140,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(863,'DATA',141,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(864,'doData',142,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(865,'',143,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(866,'Data',144,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(867,'true',145,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(868,'Properties',139,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(869,'gearsmall.gif',140,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(870,'PROPERTIES',141,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(871,'doProperties',142,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(872,'',143,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(873,'Properties',144,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(874,'true',145,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(875,'Contents',139,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(876,'',140,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(877,'CONTENTS',141,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(878,'doContents',142,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(879,'',143,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(880,'Contents',144,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(881,'true',145,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(882,'About',139,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(883,'',140,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(884,'ABOUT',141,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(885,'doAbout',142,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(886,'',143,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(887,'About',144,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(888,'true',145,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(889,'Print Options',139,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(890,'gearsmall.gif',140,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(891,'PRINT_OPTIONS',141,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(892,'doPrintOptions',142,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(893,'u',143,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(894,'Print Options',144,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(895,'true',145,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(896,'Exit',139,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(897,'',140,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(898,'EXIT',141,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(899,'doExit',142,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(900,'',143,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(901,'Exit',144,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(902,'true',145,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(903,'Undo',139,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(904,'',140,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(905,'UNDO',141,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(906,'doUndo',142,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(907,'',143,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(908,'Undo',144,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(909,'true',145,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(910,'Redo',139,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(911,'',140,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(912,'REDO',141,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(913,'doRedo',142,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(914,'',143,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(915,'Redo',144,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(916,'true',145,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(917,'Find',139,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(918,'',140,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(919,'FIND',141,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(920,'doFind',142,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(921,'',143,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(922,'Find',144,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(923,'true',145,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(924,'Replace',139,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(925,'',140,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(926,'REPLACE',141,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(927,'doReplace',142,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(928,'',143,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(929,'Replace',144,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(930,'true',145,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(931,'Transfer',139,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(932,'',140,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(933,'TRANSFER',141,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(934,'doTransfer',142,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(935,'',143,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(936,'Transfer',144,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(937,'true',145,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(938,'Remove',139,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(939,'',140,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(940,'REMOVE',141,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(941,'doRemove',142,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(942,'',143,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(943,'Remove',144,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(944,'true',145,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(945,'Disclaimer',139,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(946,'',140,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(947,'DISCLAIMER',141,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(948,'doDisclaimer',142,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(949,'',143,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(950,'Disclaimer',144,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(951,'true',145,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(952,'Compile',139,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(953,'gear.gif',140,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(954,'COMPILE',141,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(955,'doCompile',142,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(956,'',143,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(957,'Compile',144,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(958,'true',145,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(959,'Re-Assign strategies for selected tradingdays.',139,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(960,'gear.gif',140,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(961,'REASSIGN',141,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(962,'doReAssign',142,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(963,'',143,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(964,'Re-Assign',144,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(965,'true',145,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(966,'Strategy Parameters',139,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(967,'gear.gif',140,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(968,'STRATEGY_PARMS',141,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(969,'doStrategyParameters',142,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(970,'',143,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(971,'Strategy Parms',144,null)//
INSERT INTO codevalue (id , code_value, code_attribute_id,indicator_series_id) VALUES(972,'true',145,null)//
COMMIT//
