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

INSERT INTO strategy (id, name, description, market_data, class_name, version) VALUES (50, 'FHxRBHyR+Heikin', 'Sell front/back half at x/yR or trail BH with Heikin-Ashi bars over xR', 1, 'PosMgrFHXRBHHeikinStrategy',0)//
INSERT INTO strategy (id, name, description, class_name, version) VALUES (51, 'AllOrNothing', 'Close open position at 15:58 with stop 1R', 'PosMgrAllOrNothingStrategy',0)//
INSERT INTO strategy (id, name, description, class_name, version) VALUES (52, 'All5MinBar', 'Trails whole pos on 5min bars after 9:40', 'PosMgrAll5MinBarStrategy',0)//
INSERT INTO strategy (id, name, description, class_name, version) VALUES (53, 'FHxRBHyR', 'Sell front half at xR and bacl half at yR', 'PosMgrFHXRBHYRStrategy',0)//
INSERT INTO strategy (id, name, description, market_data, class_name, id_strategy_manager, version) VALUES (1, '5minBarGap', 'Enter a tier 1-3 gap in first 5min bar direction, and stop @ 5min high/low',1, 'FiveMinGapBarStrategy',53,0)//
INSERT INTO strategy (id, name, description, market_data, class_name, id_strategy_manager, version) VALUES (2, '5minSideBarGap', 'Enter a tier 1-3 gap via expectd Side after first 5min bar and stop @ 5min high/low',1, 'FiveMinSideGapBarStrategy',53,0)//
INSERT INTO strategy (id, name, description, market_data, class_name, id_strategy_manager, version) VALUES (3, '5minWRBBarGap', 'Enter a tier 1-3 gap in first 5min WRB bar direction, and stop @ 55% of high/low',1, 'FiveMinWRBGapBarStrategy',52,0)//
INSERT INTO strategy (id, name, description, market_data, class_name, id_strategy_manager, version) VALUES (4, 'PosMgrHeikinAshiTrail', 'Get and trail an open position on the current time frame using Hiekin-Ashi bars',1, 'PosMgrHeikinAshiTrailStrategy',null,0)//

COMMIT//

INSERT INTO codetype (id, name, type, description, version) VALUES(1,'MovingAverage','IndicatorParameters','Moving Average',0)//
INSERT INTO codetype (id, name, type, description, version) VALUES(2,'Pivot','IndicatorParameters','Pivot points',0)//
INSERT INTO codetype (id, name, type, description, version) VALUES(3,'Candle','IndicatorParameters','Contract to be followed',0)//
INSERT INTO codetype (id, name, type, description, version) VALUES(4,'AverageTrueRange','IndicatorParameters','Average True Range',0)//
INSERT INTO codetype (id, name, type, description, version) VALUES(5,'RelativeStrengthIndex','IndicatorParameters','Relative Strength Index',0)//
INSERT INTO codetype (id, name, type, description, version) VALUES(6,'CommodityChannelIndex','IndicatorParameters','Commodity Channel Index',0)//
INSERT INTO codetype (id, name, type, description, version) VALUES(7,'BollingerBands','IndicatorParameters','Bollinger Bands',0)//
INSERT INTO codetype (id, name, type, description, version) VALUES(8,'StochasticOscillator','IndicatorParameters','Stochastic Oscillator',0)//
INSERT INTO codetype (id, name, type, description, version) VALUES(9,'MoneyFlowIndex','IndicatorParameters','Money Flow Index',0)//
INSERT INTO codetype (id, name, type, description, version) VALUES(10,'MACD','IndicatorParameters','MACD',0)//
INSERT INTO codetype (id, name, type, description, version) VALUES(11,'Vostro','IndicatorParameters','Vostro Indicator',0)//

COMMIT//

INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, id_code_type, version) VALUES(1,'Length','The length of the Moving Average','10','java.lang.Integer',null, 1,0) //
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, id_code_type, version) VALUES(2,'MAType','Type of the Moving Average','LINEAR','java.lang.String', 'org.trade.dictionary.valuetype.CalculationType',1,0) //
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, id_code_type, version) VALUES(3,'Side','Use candle direct for V','false','java.lang.Boolean','org.trade.core.valuetype.YesNo', 2,0) //
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, id_code_type, version) VALUES(4,'Quadratic','Use quadratic calc for pivot','true','java.lang.Boolean','org.trade.core.valuetype.YesNo', 2,0) //
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, id_code_type, version) VALUES(5,'Bars','Number of bars to use for pivot 5 or 7','5','java.lang.Integer', null,2,0) //
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, id_code_type, version) VALUES(6,'Symbol','The contract symbol','SPY','java.lang.String', null,3,0)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, id_code_type, version) VALUES(7,'Currency','The contract currency','USD','java.lang.String', 'org.trade.dictionary.valuetype.Currency',3,0)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, id_code_type, version) VALUES(8,'Exchange','The contract exchange','SMART','java.lang.String', 'org.trade.dictionary.valuetype.Exchange',3,0)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, id_code_type, version) VALUES(9,'SECType','The contract SECType','STK','java.lang.String', 'org.trade.dictionary.valuetype.SECType',3,0)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, id_code_type, version) VALUES(10,'Length','The length of the Average True Range','14','java.lang.Integer',null, 4,0) //
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, id_code_type, version) VALUES(11,'RollingCandle','Use rolling candle values','false','java.lang.Boolean','org.trade.core.valuetype.YesNo', 4,0) //
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, id_code_type, version) VALUES(12,'Length','The length of the Relative Strength Index','14','java.lang.Integer',null, 5,0) //
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, id_code_type, version) VALUES(13,'RollingCandle','Use rolling candle values','false','java.lang.Boolean','org.trade.core.valuetype.YesNo', 5,0) //
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, id_code_type, version) VALUES(14,'Length','The length of the Commodity Channel Index','20','java.lang.Integer',null, 6,0) //
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, id_code_type, version) VALUES(15,'RollingCandle','Use rolling candle values','false','java.lang.Boolean','org.trade.core.valuetype.YesNo', 6,0) //
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, id_code_type, version) VALUES(16,'Length','The length of the Moving Average','20','java.lang.Integer',null,7,0) //
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, id_code_type, version) VALUES(17,'NumberOfSTD','Number of STDs','2.0','java.math.BigDecimal', null,7,0) //
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, id_code_type, version) VALUES(18,'Length','The length of the %K','14','java.lang.Integer',null, 8,0) //
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, id_code_type, version) VALUES(19,'KSmoothing','The smoothing of the %K','1','java.lang.Integer',null, 8,0) //
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, id_code_type, version) VALUES(20,'PercentD','The SMA of the %D','3','java.lang.Integer',null, 8,0) //
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, id_code_type, version) VALUES(21,'Inverse','Stochastic or Percent R','false','java.lang.Boolean','org.trade.core.valuetype.YesNo', 8,0) //
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, id_code_type, version) VALUES(22,'Length','The length of the MFI','14','java.lang.Integer',null, 9,0) //
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, id_code_type, version) VALUES(23,'RollingCandle','Use rolling candle values','false','java.lang.Boolean','org.trade.core.valuetype.YesNo', 9,0) //
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, id_code_type, version) VALUES(24,'Fast Length','The fast length of the EMA','12','java.lang.Integer',null, 10,0) //
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, id_code_type, version) VALUES(25,'Slow Length','The slow length of the EMA','26','java.lang.Integer',null, 10,0) //
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, id_code_type, version) VALUES(26,'Signal Smoothing','The EMA length of the MACD','9','java.lang.Integer',null, 10,0) //
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, id_code_type, version) VALUES(27,'Simple Smoothing MA','Use SMA for signal line smoothing','true','java.lang.Boolean','org.trade.core.valuetype.YesNo', 10,0) //
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, id_code_type, version) VALUES(28,'Length','The length of the Moving Average','100','java.lang.Integer',null, 11,0) //
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, id_code_type, version) VALUES(29,'MAType','Type of the Moving Average','WEIGHTED','java.lang.String', 'org.trade.dictionary.valuetype.CalculationType',11,0) //
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, id_code_type, version) VALUES(30,'Vostro Period','The number of periods for Vostro calc','5','java.lang.Integer',null, 11,0) //
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, id_code_type, version) VALUES(31,'Vostro Range','The range +/- to indicate a Vostro','8.0','java.math.BigDecimal',null, 11,0) //
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, id_code_type, version) VALUES(32,'Price Source','Price source used for calculations','5','java.lang.Integer', 'org.trade.dictionary.valuetype.PriceSource',11,0)//
INSERT INTO codeattribute (id, name, description, default_value, class_name, class_editor_name, id_code_type, version) VALUES(33,'Price Source','Price source used for calculations','1','java.lang.Integer', 'org.trade.dictionary.valuetype.PriceSource',1,0)//

COMMIT//

INSERT INTO indicatorseries (id, name, type, description, display_series, series_RGB_color, sub_chart, id_strategy, version) VALUES(1,'SMA-20','MovingAverageSeries','Simple 20 period Moving Average',1,-52429,0,1,0) //
INSERT INTO indicatorseries (id, name, type, description, display_series, series_RGB_color, sub_chart, id_strategy, version) VALUES(2,'SMA-8','MovingAverageSeries','Simple 8 Period Moving Average',1,-16711681,0,1,0) //
INSERT INTO indicatorseries (id, name, type, description, display_series, series_RGB_color, sub_chart, id_strategy, version) VALUES(3,'Vwap','VwapSeries','Volume Weighted Moving Average',1,0,0,1,0) //
INSERT INTO indicatorseries (id, name, type, description, display_series, series_RGB_color, sub_chart, id_strategy, version) VALUES(4,'Pivot','PivotSeries','5 Bar Pivots',1,0,0,1,0) //
INSERT INTO indicatorseries (id, name, type, description, display_series, series_RGB_color, sub_chart, id_strategy, version) VALUES(5,'HeikinAshi','HeikinAshiSeries','HeikinAshi bars used for trail stops',0,0,0,1,0) //
INSERT INTO indicatorseries (id, name, type, description, display_series, series_RGB_color, sub_chart, id_strategy, version) VALUES(6,'S&P500','CandleSeries','S&P 500',1,-16738048,0,1,0) //
INSERT INTO indicatorseries (id, name, type, description, display_series, series_RGB_color, sub_chart, id_strategy, version) VALUES(7,'Volume','VolumeSeries','Volume',1,1,1,1,0) //

INSERT INTO indicatorseries (id, name, type, description, display_series, series_RGB_color, sub_chart, id_strategy, version) VALUES(8,'SMA-20','MovingAverageSeries','Simple 20 period Moving Average',1,-52429,0,2,0) //
INSERT INTO indicatorseries (id, name, type, description, display_series, series_RGB_color, sub_chart, id_strategy, version) VALUES(9,'SMA-8','MovingAverageSeries','Simple 8 Period Moving Average',1,-16711681,0,2,0) //
INSERT INTO indicatorseries (id, name, type, description, display_series, series_RGB_color, sub_chart, id_strategy, version) VALUES(10,'Vwap','VwapSeries','Volume Weighted Moving Average',1,0,0,2,0) //
INSERT INTO indicatorseries (id, name, type, description, display_series, series_RGB_color, sub_chart, id_strategy, version) VALUES(11,'Pivot','PivotSeries','5 Bar Pivots',1,0,0,2,0) //
INSERT INTO indicatorseries (id, name, type, description, display_series, series_RGB_color, sub_chart, id_strategy, version) VALUES(12,'HeikinAshi','HeikinAshiSeries','HeikinAshi bars used for trail stops',0,0,0,2,0) //
INSERT INTO indicatorseries (id, name, type, description, display_series, series_RGB_color, sub_chart, id_strategy, version) VALUES(13,'Volume','VolumeSeries','Volume',1,1,1,2,0) //

INSERT INTO indicatorseries (id, name, type, description, display_series, series_RGB_color, sub_chart, id_strategy, version) VALUES(14,'SMA-20','MovingAverageSeries','Simple 20 period Moving Average',1,-52429,0,3,0) //
INSERT INTO indicatorseries (id, name, type, description, display_series, series_RGB_color, sub_chart, id_strategy, version) VALUES(15,'SMA-8','MovingAverageSeries','Simple 8 Period Moving Average',1,-16711681,0,3,0) //
INSERT INTO indicatorseries (id, name, type, description, display_series, series_RGB_color, sub_chart, id_strategy, version) VALUES(16,'Vwap','VwapSeries','Volume Weighted Moving Average',1,0,0,3,0) //
INSERT INTO indicatorseries (id, name, type, description, display_series, series_RGB_color, sub_chart, id_strategy, version) VALUES(17,'Pivot','PivotSeries','5 Bar Pivots',1,0,0,3,0) //
INSERT INTO indicatorseries (id, name, type, description, display_series, series_RGB_color, sub_chart, id_strategy, version) VALUES(18,'Volume','VolumeSeries','Volume',1,1,1,3,0) //

INSERT INTO indicatorseries (id, name, type, description, display_series, series_RGB_color, sub_chart, id_strategy, version) VALUES(19,'SMA-20','MovingAverageSeries','Simple 20 period Moving Average',1,-52429,0,4,0) //
INSERT INTO indicatorseries (id, name, type, description, display_series, series_RGB_color, sub_chart, id_strategy, version) VALUES(20,'SMA-8','MovingAverageSeries','Simple 8 Period Moving Average',1,-16711681,0,4,0) //
INSERT INTO indicatorseries (id, name, type, description, display_series, series_RGB_color, sub_chart, id_strategy, version) VALUES(21,'Vwap','VwapSeries','Volume Weighted Moving Average',1,0,0,4,0) //
INSERT INTO indicatorseries (id, name, type, description, display_series, series_RGB_color, sub_chart, id_strategy, version) VALUES(22,'Pivot','PivotSeries','5 Bar Pivots',1,0,0,4,0) //
INSERT INTO indicatorseries (id, name, type, description, display_series, series_RGB_color, sub_chart, id_strategy, version) VALUES(23,'HeikinAshi','HeikinAshiSeries','HeikinAshi bars used for trail stops',0,0,0,4,0) //
INSERT INTO indicatorseries (id, name, type, description, display_series, series_RGB_color, sub_chart, id_strategy, version) VALUES(24,'Volume','VolumeSeries','Volume',1,1,1,4,0) //

INSERT INTO indicatorseries (id, name, type, description, display_series, series_RGB_color, sub_chart, id_strategy, version) VALUES(25,'SMA-20','MovingAverageSeries','Simple 20 period Moving Average',1,-52429,0,50,0) //
INSERT INTO indicatorseries (id, name, type, description, display_series, series_RGB_color, sub_chart, id_strategy, version) VALUES(26,'SMA-8','MovingAverageSeries','Simple 8 Period Moving Average',1,-16711681,0,50,0) //
INSERT INTO indicatorseries (id, name, type, description, display_series, series_RGB_color, sub_chart, id_strategy, version) VALUES(27,'Vwap','VwapSeries','Volume Weighted Moving Average',1,0,0,50,0) //
INSERT INTO indicatorseries (id, name, type, description, display_series, series_RGB_color, sub_chart, id_strategy, version) VALUES(28,'Pivot','PivotSeries','5 Bar Pivots',1,0,0,50,0) //
INSERT INTO indicatorseries (id, name, type, description, display_series, series_RGB_color, sub_chart, id_strategy, version) VALUES(29,'HeikinAshi','HeikinAshiSeries','HeikinAshi bars used for trail stops',0,0,0,50,0) //
INSERT INTO indicatorseries (id, name, type, description, display_series, series_RGB_color, sub_chart, id_strategy, version) VALUES(30,'Volume','VolumeSeries','Volume',1,1,1,50,0) //

COMMIT//

INSERT INTO codevalue (id , code_value, id_code_attribute,id_indicator_series, version) VALUES(1,'20',1,1,0) //
INSERT INTO codevalue (id , code_value, id_code_attribute,id_indicator_series, version) VALUES(2,'LINEAR',2,1,0) //
INSERT INTO codevalue (id , code_value, id_code_attribute,id_indicator_series, version) VALUES(3,'8',1,2,0) //
INSERT INTO codevalue (id , code_value, id_code_attribute,id_indicator_series, version) VALUES(4,'LINEAR',2,2,0) //
INSERT INTO codevalue (id , code_value, id_code_attribute,id_indicator_series, version) VALUES(5,'20',1,8,0) //
INSERT INTO codevalue (id , code_value, id_code_attribute,id_indicator_series, version) VALUES(6,'LINEAR',2,8,0) //
INSERT INTO codevalue (id , code_value, id_code_attribute,id_indicator_series, version) VALUES(7,'8',1,9,0) //
INSERT INTO codevalue (id , code_value, id_code_attribute,id_indicator_series, version) VALUES(8,'LINEAR',2,9,0) //
INSERT INTO codevalue (id , code_value, id_code_attribute,id_indicator_series, version) VALUES(9,'false',3,4,0) //
INSERT INTO codevalue (id , code_value, id_code_attribute,id_indicator_series, version) VALUES(10,'true',4,4,0) //
INSERT INTO codevalue (id , code_value, id_code_attribute,id_indicator_series, version) VALUES(11,'5',5,4,0)//
INSERT INTO codevalue (id , code_value, id_code_attribute,id_indicator_series, version) VALUES(12,'false',3,11,0) //
INSERT INTO codevalue (id , code_value, id_code_attribute,id_indicator_series, version) VALUES(13,'true',4,11,0) //
INSERT INTO codevalue (id , code_value, id_code_attribute,id_indicator_series, version) VALUES(14,'5',5,11,0)//
INSERT INTO codevalue (id , code_value, id_code_attribute,id_indicator_series, version) VALUES(15,'20',1,14,0) //
INSERT INTO codevalue (id , code_value, id_code_attribute,id_indicator_series, version) VALUES(16,'LINEAR',2,14,0) //
INSERT INTO codevalue (id , code_value, id_code_attribute,id_indicator_series, version) VALUES(17,'8',1,15,0) //
INSERT INTO codevalue (id , code_value, id_code_attribute,id_indicator_series, version) VALUES(18,'LINEAR',2,15,0) //
INSERT INTO codevalue (id , code_value, id_code_attribute,id_indicator_series, version) VALUES(19,'false',3,17,0) //
INSERT INTO codevalue (id , code_value, id_code_attribute,id_indicator_series, version) VALUES(20,'true',4,17,0) //
INSERT INTO codevalue (id , code_value, id_code_attribute,id_indicator_series, version) VALUES(21,'5',5,17,0)//
INSERT INTO codevalue (id , code_value, id_code_attribute,id_indicator_series, version) VALUES(22,'SPY',6,6,0)//
INSERT INTO codevalue (id , code_value, id_code_attribute,id_indicator_series, version) VALUES(23,'USD',7,6,0)//
INSERT INTO codevalue (id , code_value, id_code_attribute,id_indicator_series, version) VALUES(24,'SMART',8,6,0)//
INSERT INTO codevalue (id , code_value, id_code_attribute,id_indicator_series, version) VALUES(25,'STK',9,6,0)//
INSERT INTO codevalue (id , code_value, id_code_attribute,id_indicator_series, version) VALUES(26,'1',33,1,0) //
INSERT INTO codevalue (id , code_value, id_code_attribute,id_indicator_series, version) VALUES(27,'1',33,2,0) //
INSERT INTO codevalue (id , code_value, id_code_attribute,id_indicator_series, version) VALUES(28,'1',33,8,0) //
INSERT INTO codevalue (id , code_value, id_code_attribute,id_indicator_series, version) VALUES(29,'1',33,9,0) //
INSERT INTO codevalue (id , code_value, id_code_attribute,id_indicator_series, version) VALUES(30,'1',33,14,0) //
INSERT INTO codevalue (id , code_value, id_code_attribute,id_indicator_series, version) VALUES(31,'1',33,15,0) //
INSERT INTO codevalue (id , code_value, id_code_attribute,id_indicator_series, version) VALUES(32,'20',1,19,0) //
INSERT INTO codevalue (id , code_value, id_code_attribute,id_indicator_series, version) VALUES(33,'LINEAR',2,19,0) //
INSERT INTO codevalue (id , code_value, id_code_attribute,id_indicator_series, version) VALUES(34,'1',33,19,0) //
INSERT INTO codevalue (id , code_value, id_code_attribute,id_indicator_series, version) VALUES(35,'8',1,20,0) //
INSERT INTO codevalue (id , code_value, id_code_attribute,id_indicator_series, version) VALUES(36,'LINEAR',2,20,0) //
INSERT INTO codevalue (id , code_value, id_code_attribute,id_indicator_series, version) VALUES(37,'1',33,20,0) //
INSERT INTO codevalue (id , code_value, id_code_attribute,id_indicator_series, version) VALUES(38,'false',3,22,0) //
INSERT INTO codevalue (id , code_value, id_code_attribute,id_indicator_series, version) VALUES(39,'true',4,22,0) //
INSERT INTO codevalue (id , code_value, id_code_attribute,id_indicator_series, version) VALUES(40,'5',5,22,0)//
INSERT INTO codevalue (id , code_value, id_code_attribute,id_indicator_series, version) VALUES(41,'20',1,25,0) //
INSERT INTO codevalue (id , code_value, id_code_attribute,id_indicator_series, version) VALUES(42,'LINEAR',2,25,0) //
INSERT INTO codevalue (id , code_value, id_code_attribute,id_indicator_series, version) VALUES(43,'1',33,25,0) //
INSERT INTO codevalue (id , code_value, id_code_attribute,id_indicator_series, version) VALUES(44,'8',1,26,0) //
INSERT INTO codevalue (id , code_value, id_code_attribute,id_indicator_series, version) VALUES(45,'LINEAR',2,26,0) //
INSERT INTO codevalue (id , code_value, id_code_attribute,id_indicator_series, version) VALUES(46,'1',33,26,0) //
INSERT INTO codevalue (id , code_value, id_code_attribute,id_indicator_series, version) VALUES(47,'false',3,28,0) //
INSERT INTO codevalue (id , code_value, id_code_attribute,id_indicator_series, version) VALUES(48,'true',4,28,0) //
INSERT INTO codevalue (id , code_value, id_code_attribute,id_indicator_series, version) VALUES(49,'5',5,28,0)//

COMMIT//

INSERT INTO entrylimit (id,start_price,end_price,limit_amount, percent_of_price, percent_of_margin, share_round, pivot_range, price_round, version) VALUES (1,'0','8','0.02','0.06','0','100','0.05', '0.05',0)//
INSERT INTO entrylimit (id,start_price,end_price,limit_amount, percent_of_price, percent_of_margin, share_round, pivot_range, price_round, version) VALUES (2,'8.01','15','0.02', '0.05','0','100', '0.05', '0.05',0)//
INSERT INTO entrylimit (id,start_price,end_price,limit_amount, percent_of_price, percent_of_margin, share_round, pivot_range, price_round, version) VALUES (3,'15.01','30','0.03', '0.03','0','100', '0.05', '0.05',0)//
INSERT INTO entrylimit (id,start_price,end_price,limit_amount, percent_of_price, percent_of_margin, share_round, pivot_range, price_round, version) VALUES (4,'30.01','50','0.04', '0.02','0', '50', '0.07', '0.07',0)//
INSERT INTO entrylimit (id,start_price,end_price,limit_amount, percent_of_price, percent_of_margin, share_round, pivot_range, price_round, version) VALUES (5,'50.01','80','0.6','0.02','0','20', '0.15', '0.15',0)//
INSERT INTO entrylimit (id,start_price,end_price,limit_amount, percent_of_price, percent_of_margin, share_round, pivot_range, price_round, version) VALUES (6,'80.01','140','0.08','0.02','0','20', '0.20', '0.20',0)//
INSERT INTO entrylimit (id,start_price,end_price,limit_amount, percent_of_price, percent_of_margin, share_round, pivot_range, price_round, version) VALUES (7,'140.01','300','0.15','0.02','0','10', '0.25', '0.25',0)//
INSERT INTO entrylimit (id,start_price,end_price,limit_amount, percent_of_price, percent_of_margin, share_round, pivot_range, price_round, version) VALUES (8,'300.01','1000','0.15','0.02','0','10', '0.25', '0.30',0)//
INSERT INTO entrylimit (id,start_price,end_price,limit_amount, percent_of_price, percent_of_margin, share_round, pivot_range, price_round, version) VALUES (9,'1000.01','3000','0.30','0.02','0','10', '0.5', '0.50',0)//

COMMIT//


INSERT INTO portfolio (id, name, alias, description, is_default, last_update_date, version) VALUES (1, 'Paper','Paper Account','Paper trading account', 1, NOW(), 0)//

COMMIT//