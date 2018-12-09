-- DELIMITER //

SET @OLD_UNIQUE_CHECKS = @@UNIQUE_CHECKS, UNIQUE_CHECKS=0//
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0//
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL'//


DROP SCHEMA IF EXISTS @db-dba.schema@//
CREATE SCHEMA IF NOT EXISTS @db-dba.schema@//
SHOW WARNINGS//
USE @db-dba.schema@//

-- -----------------------------------------------------
-- Table EntryLimits
-- -----------------------------------------------------
DROP TABLE IF EXISTS entrylimit //

SHOW WARNINGS//
CREATE  TABLE IF NOT EXISTS entrylimit (
id INT NOT NULL AUTO_INCREMENT ,
start_price DECIMAL(10,2) NOT NULL ,
end_price DECIMAL(10,2) NOT NULL ,
limit_amount DECIMAL(10,2) NULL ,
percent_of_price DECIMAL(10,6) NULL ,
percent_of_margin DECIMAL(10,6) NULL ,
pivot_range DECIMAL(5,2) NULL ,
price_round DECIMAL(10,2) NULL ,
share_round INT NULL ,
version INT NULL,
PRIMARY KEY (id) )
ENGINE = InnoDB//

SHOW WARNINGS//

-- -----------------------------------------------------
-- Table Contract
-- -----------------------------------------------------
DROP TABLE IF EXISTS contract //

SHOW WARNINGS//
CREATE  TABLE IF NOT EXISTS contract (
id INT NOT NULL AUTO_INCREMENT ,
category VARCHAR(80) NULL ,
combo_leg_description VARCHAR(30)  NULL ,
contract_month VARCHAR(6)  NULL ,
currency VARCHAR(3) NOT NULL ,
ev_multiplier DECIMAL(10,2) NULL ,
ev_rule VARCHAR(80) NULL ,
EXCHANGE VARCHAR(30) NOT NULL ,
expiry DATETIME NULL ,
id_contract_IB INT NULL ,
include_expired  SMALLINT(1)  NULL ,
industry VARCHAR(80) NULL ,
local_symbol VARCHAR(20) NULL ,
long_name VARCHAR(80) NULL ,
liquid_hours VARCHAR(50) NULL ,
market_name VARCHAR(80) NULL ,
min_tick DECIMAL(10,2) NULL ,
option_type VARCHAR(1) NULL ,
order_types VARCHAR(50) NULL ,
price_magnifier DECIMAL(10,2) NULL ,
price_multiplier DECIMAL(10,2) NULL ,
primary_exchange VARCHAR(10) NULL ,
symbol VARCHAR(20) NOT NULL ,
sec_id VARCHAR(10) NULL ,
sec_id_type VARCHAR(5) NULL ,
sec_type VARCHAR(4) NOT NULL ,
strike DECIMAL(10,2) NULL ,
sub_category VARCHAR(80) NULL ,
time_zone_Id VARCHAR(7) NULL ,
trading_class VARCHAR(80) NULL ,
trading_hours VARCHAR(100) NULL ,
under_con_id INT NULL ,
valid_exchanges VARCHAR(200) NULL ,
version INT NULL,
id_trade_position INT NULL,
PRIMARY KEY (id) ,
UNIQUE INDEX contract_tradePosition_uq (id_trade_position ASC),
UNIQUE INDEX contract_uq (sec_type ASC, symbol ASC, EXCHANGE ASC, currency ASC, expiry ASC),
CONSTRAINT contract_trade_position_fk
FOREIGN KEY (id_trade_position )
REFERENCES tradeposition (id )
  ON DELETE NO ACTION
  ON UPDATE NO ACTION)
ENGINE = InnoDB//

SHOW WARNINGS//

-- -----------------------------------------------------
-- Table Portfolio
-- -----------------------------------------------------
DROP TABLE IF EXISTS portfolio //

SHOW WARNINGS//
CREATE  TABLE IF NOT EXISTS portfolio (
id INT NOT NULL AUTO_INCREMENT ,
name VARCHAR(45) NOT NULL ,
alias VARCHAR(45) NULL ,
allocation_method  VARCHAR(20) NULL ,
description VARCHAR(240) NULL ,
is_default SMALLINT(1)  NOT NULL ,
last_update_date DATETIME(3) NOT NULL ,
version INT NULL,
PRIMARY KEY (id) ,
UNIQUE INDEX portfolio_name_uq (name ASC))
ENGINE = InnoDB//

SHOW WARNINGS//

-- -----------------------------------------------------
-- Table Account
-- -----------------------------------------------------
DROP TABLE IF EXISTS account //

SHOW WARNINGS//
CREATE  TABLE IF NOT EXISTS account (
id INT NOT NULL AUTO_INCREMENT ,
account_number VARCHAR(20) NOT NULL ,
account_type VARCHAR(20) NULL ,
name VARCHAR(45) NOT NULL ,
alias VARCHAR(45) NULL ,
available_funds DECIMAL(10,2) NULL ,
buying_power DECIMAL(10,2) NULL ,
cash_balance DECIMAL(10,2) NULL ,
currency VARCHAR(3) NOT NULL ,
gross_position_value DECIMAL(10,2) NULL ,
realized_pn_l DECIMAL(10,2) NULL ,
unrealized_pn_l DECIMAL(10,2) NULL ,
last_update_date DATETIME(3) NOT NULL ,
version INT NULL,
PRIMARY KEY (id) ,
UNIQUE INDEX account_name_uq (name ASC),
UNIQUE INDEX account_number_uq (account_number ASC) )
ENGINE = InnoDB//

SHOW WARNINGS//

-- -----------------------------------------------------
-- Table PortfolioAccount
-- -----------------------------------------------------
DROP TABLE IF EXISTS portfolioaccount //

SHOW WARNINGS//
CREATE  TABLE IF NOT EXISTS portfolioaccount (
id INT NOT NULL AUTO_INCREMENT ,
version INT NULL,
id_portfolio INT NOT NULL ,
id_account INT NOT NULL ,
PRIMARY KEY (id) ,
INDEX portfolioaccount_account_idx  (id_account ASC) ,
INDEX portfolioaccount_portfolio_idx  (id_portfolio ASC) ,
UNIQUE INDEX portfolioaccount_uq (id_portfolio ASC, id_account ASC),
CONSTRAINT portfolioaccount_portfolio_fk
FOREIGN KEY (id_portfolio )
REFERENCES portfolio (id)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION,
CONSTRAINT portfolioaccount_account_fk
FOREIGN KEY (id_account)
REFERENCES account (id)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION )
ENGINE = InnoDB//

SHOW WARNINGS//

-- -----------------------------------------------------
-- Table TradingDay
-- -----------------------------------------------------
DROP TABLE IF EXISTS tradingday //

SHOW WARNINGS//
CREATE  TABLE IF NOT EXISTS tradingday (
id INT NOT NULL AUTO_INCREMENT ,
open DATETIME NOT NULL ,
close DATETIME NOT NULL ,
market_bias VARCHAR(10) NULL ,
market_gap VARCHAR(10) NULL ,
market_bar VARCHAR(10) NULL ,
version INT NULL,
PRIMARY KEY (id) ,
UNIQUE INDEX open_close_uq (open ASC, close ASC))
ENGINE = InnoDB//

SHOW WARNINGS//

-- -----------------------------------------------------
-- Table Strategy
-- -----------------------------------------------------
DROP TABLE IF EXISTS strategy //

SHOW WARNINGS//
CREATE  TABLE IF NOT EXISTS strategy (
id INT NOT NULL AUTO_INCREMENT ,
name VARCHAR(45) NOT NULL ,
description VARCHAR(240) NULL ,
market_data SMALLINT(1)  NULL ,
class_Name VARCHAR(100) NOT NULL ,
id_strategy_manager INT NULL ,
version INT NULL,
PRIMARY KEY (id) ,
UNIQUE INDEX strategy_name_uq (name ASC) ,
INDEX strategy_strategy_idx (id_strategy_manager ASC) ,
CONSTRAINT strategy_strategy_fk
FOREIGN KEY (id_strategy_manager )
REFERENCES strategy (id )
  ON DELETE NO ACTION
  ON UPDATE NO ACTION)
ENGINE = InnoDB//

SHOW WARNINGS//

-- -----------------------------------------------------
-- Table TradeStrategy
-- -----------------------------------------------------
DROP TABLE IF EXISTS tradestrategy //

SHOW WARNINGS//
CREATE  TABLE IF NOT EXISTS tradestrategy (
id INT NOT NULL AUTO_INCREMENT ,
bar_Size  INT NULL ,
chart_days INT NULL ,
STATUS VARCHAR(20) NULL ,
risk_amount DECIMAL(10,2) NULL ,
side VARCHAR(3) NULL ,
tier VARCHAR(1) NULL ,
trade SMALLINT(1)  NULL ,
last_update_date DATETIME(3) NOT NULL ,
version INT NULL,
id_trading_day INT NOT NULL ,
id_contract INT NOT NULL ,
id_strategy INT NOT NULL ,
id_portfolio INT NOT NULL ,
PRIMARY KEY (id) ,
INDEX tradeStrategy_tradingDay_idx (id_trading_day ASC) ,
INDEX tradeStrategy_contract_idx  (id_contract ASC) ,
INDEX tradeStrategy_stategy_idx  (id_strategy ASC) ,
INDEX tradeStrategy_portfolio_idx  (id_portfolio ASC) ,
UNIQUE INDEX tradestrategy_uq (id_trading_day ASC, id_contract ASC, id_strategy ASC, id_portfolio ASC, bar_size ASC),
CONSTRAINT tradestrategy_tradingday_fk
FOREIGN KEY (id_trading_day )
REFERENCES tradingday (id )
  ON DELETE NO ACTION
  ON UPDATE NO ACTION,
CONSTRAINT tradestrategy_contract_fk
FOREIGN KEY (id_contract )
REFERENCES contract (id )
  ON DELETE NO ACTION
  ON UPDATE NO ACTION,
CONSTRAINT tradestrategy_stategy_fk
FOREIGN KEY (id_strategy )
REFERENCES strategy (id )
  ON DELETE NO ACTION
  ON UPDATE NO ACTION,
CONSTRAINT tradestrategy_portfolio_fk
FOREIGN KEY (id_portfolio)
REFERENCES portfolio (id)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION)
ENGINE = InnoDB//

SHOW WARNINGS//

-- -----------------------------------------------------
-- Table TradePosition
-- -----------------------------------------------------
DROP TABLE IF EXISTS tradeposition //

SHOW WARNINGS//
CREATE  TABLE IF NOT EXISTS tradeposition (
id INT NOT NULL AUTO_INCREMENT ,
open_quantity INT NULL ,
position_open_date DATETIME(3) NOT NULL ,
position_close_date DATETIME(3) NULL ,
side VARCHAR(3) NOT NULL ,
total_commission DECIMAL(10,2) NULL ,
total_buy_quantity INT NULL ,
total_buy_value DECIMAL(10,2) NULL ,
total_sell_quantity INT NULL ,
total_sell_value DECIMAL(10,2) NULL ,
total_net_value DECIMAL(10,2) NULL ,
last_update_date DATETIME(3) NOT NULL ,
version INT NULL,
id_contract INT NOT NULL ,
PRIMARY KEY (id) ,
INDEX tradeposition_contract_idx (id_contract ASC) ,
CONSTRAINT tradeposition_contract_fk
FOREIGN KEY (id_contract )
REFERENCES contract (id )
  ON DELETE CASCADE
  ON UPDATE NO ACTION)
ENGINE = InnoDB//

SHOW WARNINGS//

-- -----------------------------------------------------
-- Table TradeOrder
-- -----------------------------------------------------
DROP TABLE IF EXISTS tradeorder //

SHOW WARNINGS//

CREATE  TABLE IF NOT EXISTS tradeorder (
id INT NOT NULL AUTO_INCREMENT ,
action VARCHAR(6) NOT NULL ,
account_number VARCHAR(20) NULL ,
all_or_nothing SMALLINT(1)  NULL ,
aux_price DECIMAL(10,2) NULL ,
average_filled_price DECIMAL(11,3) NULL ,
client_id INT NULL ,
commission DECIMAL(10,2) NULL ,
create_date DATETIME(3) NOT NULL ,
display_quantity INT NULL ,
FA_group  VARCHAR(45) NULL ,
FA_method  VARCHAR(45) NULL ,
FA_percent  DECIMAL(10,6) NULL ,
FA_profile  VARCHAR(45) NULL ,
filled_date DATETIME(3) NULL ,
filled_quantity INT NULL ,
good_after_time DATETIME NULL ,
good_till_time DATETIME NULL ,
hidden SMALLINT(1)  NULL ,
is_filled SMALLINT(1)  NULL ,
is_open_position SMALLINT(1)  NULL ,
limit_price DECIMAL(10,2) NULL ,
oca_group_name VARCHAR(45) NULL ,
oca_type INT NULL ,
order_key INT NOT NULL ,
order_reference VARCHAR(45) NULL ,
order_type VARCHAR(10) NOT NULL ,
override_constraints INT NOT NULL ,
perm_id INT NULL ,
parent_id INT NULL ,
quantity INT NOT NULL ,
time_in_force VARCHAR(3) NOT NULL ,
STATUS VARCHAR(45) NULL ,
stop_price DECIMAL(10,2) NULL ,
transmit SMALLINT(1)  NULL ,
trail_stop_price DECIMAL(10,2) NULL ,
trailing_percent DECIMAL(10,2) NULL ,
trigger_method INT NOT NULL ,
warning_message VARCHAR(200) NULL ,
why_held VARCHAR(45) NULL ,
last_update_date DATETIME(3) NOT NULL ,
version INT NULL,
id_tradestrategy INT NOT NULL ,
id_trade_position INT NULL ,
PRIMARY KEY (id) ,
INDEX tradeorder_tradestrategy_idx (id_tradestrategy ASC) ,
INDEX tradeorder_tradeposition_idx (id_trade_position ASC) ,
UNIQUE INDEX tradeorderkey_uq (order_Key ASC) ,
CONSTRAINT tradeorder_tradestrategy_fk
FOREIGN KEY (id_tradestrategy )
REFERENCES tradestrategy (id )
  ON DELETE CASCADE
  ON UPDATE NO ACTION,
CONSTRAINT tradeorder_tradeposition_fk
FOREIGN KEY (id_trade_position )
REFERENCES tradeposition (id )
  ON DELETE CASCADE
  ON UPDATE NO ACTION)
ENGINE = InnoDB//

SHOW WARNINGS//

-- -----------------------------------------------------
-- Table TradeOrderFill
-- -----------------------------------------------------
DROP TABLE IF EXISTS tradeorderfill //

SHOW WARNINGS//

CREATE  TABLE IF NOT EXISTS tradeorderfill (
id INT NOT NULL AUTO_INCREMENT ,
account_number VARCHAR(20) NULL ,
average_price DECIMAL(11,3) NULL ,
commission DECIMAL(11,3) NULL ,
cumulative_quantity INT NULL ,
EXCHANGE VARCHAR(10) NULL ,
exec_id VARCHAR(45) NULL ,
order_reference VARCHAR(45) NULL ,
perm_id INT NULL ,
price DECIMAL(10,2) NOT NULL ,
quantity INT NOT NULL ,
side VARCHAR(3) NOT NULL ,
time DATETIME(3) NOT NULL ,
version INT NULL,
id_trade_order INT NOT NULL ,
PRIMARY KEY (id) ,
INDEX tradeorderfill_order_idx (id_trade_order ASC) ,
UNIQUE INDEX execid_uq (exec_Id ASC, id_trade_order ASC) ,
CONSTRAINT tradeorderfill_order_fk
FOREIGN KEY (id_trade_order )
REFERENCES tradeorder (id )
  ON DELETE CASCADE
  ON UPDATE NO ACTION)
ENGINE = InnoDB//

SHOW WARNINGS//

-- -----------------------------------------------------
-- Table Candle
-- -----------------------------------------------------
DROP TABLE IF EXISTS candle //

SHOW WARNINGS//

CREATE  TABLE IF NOT EXISTS candle (
id INT NOT NULL AUTO_INCREMENT ,
open DECIMAL(10,2) NULL ,
high DECIMAL(10,2) NULL ,
low DECIMAL(10,2) NULL ,
close DECIMAL(10,2) NULL ,
period VARCHAR(45) NULL ,
start_period DATETIME(3) NULL ,
end_period DATETIME(3) NULL ,
bar_size INT  NULL ,
trade_count INT NULL ,
volume INT NULL ,
vwap DECIMAL(10,2) NULL ,
last_update_date DATETIME(3) NOT NULL ,
version INT NULL,
id_contract INT NOT NULL ,
id_trading_day INT NOT NULL ,
PRIMARY KEY (id) ,
INDEX candle_contract_idx (id_contract ASC) ,
INDEX candle_tradingday_idx (id_trading_day ASC) ,
INDEX candle_condaybar_idx (id_contract ASC, id_trading_day ASC, bar_size ASC) ,
UNIQUE INDEX candle_uq (id_contract ASC, id_trading_day ASC,  start_period ASC, end_period ASC) ,
CONSTRAINT candle_contract_fk
FOREIGN KEY (id_contract )
REFERENCES contract (id )
  ON DELETE CASCADE
  ON UPDATE NO ACTION,
CONSTRAINT candle_tradingday_fk
FOREIGN KEY (id_trading_day )
REFERENCES tradingday (id )
  ON DELETE CASCADE
  ON UPDATE NO ACTION)
ENGINE = InnoDB//

SHOW WARNINGS//

-- -----------------------------------------------------
-- Table Rule
-- -----------------------------------------------------
DROP TABLE IF EXISTS rule //

SHOW WARNINGS//
CREATE  TABLE IF NOT EXISTS rule (
id INT NOT NULL AUTO_INCREMENT,
COMMENT TEXT NULL,
create_date DATETIME(3) NOT NULL,
rule BLOB NULL,
last_update_date DATETIME(3) NOT NULL,
version INT NOT NULL,
id_strategy INT NOT NULL,
PRIMARY KEY (id),
INDEX rule_stategy_idx (id_strategy ASC),
UNIQUE INDEX idstrategy_version_uq (id_strategy ASC, version ASC),
CONSTRAINT rule_stategy_fk
FOREIGN KEY (id_strategy )
REFERENCES strategy (id )
  ON DELETE NO ACTION
  ON UPDATE NO ACTION)
ENGINE = InnoDB//

SHOW WARNINGS//

-- -----------------------------------------------------
-- Table IndicatorSeries
-- -----------------------------------------------------
DROP TABLE IF EXISTS indicatorseries //

SHOW WARNINGS//
CREATE  TABLE IF NOT EXISTS indicatorseries (
id INT NOT NULL AUTO_INCREMENT,
name VARCHAR(45) NOT NULL ,
description VARCHAR(100) NULL ,
TYPE VARCHAR(45) NOT NULL ,
display_series SMALLINT(1) NULL ,
series_RGB_color INT NULL ,
sub_chart SMALLINT(1) NULL ,
version INT NULL,
id_strategy INT NULL ,
PRIMARY KEY (id) ,
INDEX indicator_strategy_idx (id_strategy ASC) ,
UNIQUE INDEX indicatorseries_uq (id_strategy ASC, TYPE ASC, name ASC),
CONSTRAINT indicator_strategy_fk
FOREIGN KEY (id_strategy )
REFERENCES strategy (id )
  ON DELETE NO ACTION
  ON UPDATE NO ACTION)
ENGINE = InnoDB//

SHOW WARNINGS//

-- -----------------------------------------------------
-- Table CodeType
-- -----------------------------------------------------
DROP TABLE IF EXISTS codetype //

SHOW WARNINGS//

CREATE  TABLE IF NOT EXISTS codetype (
id INT NOT NULL AUTO_INCREMENT ,
name VARCHAR(45) NOT NULL ,
TYPE VARCHAR(45) NOT NULL ,
description VARCHAR(100) NULL ,
version INT NULL,
PRIMARY KEY (id) ,
UNIQUE INDEX codetype_name_type_uq (name ASC, TYPE ASC) )
ENGINE = InnoDB//

SHOW WARNINGS//

-- -----------------------------------------------------
-- Table CodeAttribute
-- -----------------------------------------------------
DROP TABLE IF EXISTS codeattribute //

SHOW WARNINGS//
CREATE  TABLE IF NOT EXISTS codeattribute (
id INT NOT NULL AUTO_INCREMENT ,
name VARCHAR(45) NOT NULL ,
description VARCHAR(100) NULL ,
default_value VARCHAR(45) NULL ,
class_name VARCHAR(100) NOT NULL ,
class_editor_Name VARCHAR(100) NULL ,
version INT NULL,
id_code_type INT NOT NULL ,
PRIMARY KEY (id) ,
INDEX codeAttribute_codetype_idx (id_code_type ASC) ,
CONSTRAINT codeattribute_codetype_fk
FOREIGN KEY (id_code_type )
REFERENCES codetype (id )
  ON DELETE NO ACTION
  ON UPDATE NO ACTION)
ENGINE = InnoDB//

SHOW WARNINGS//

-- -----------------------------------------------------
-- Table CodeValue
-- -----------------------------------------------------
DROP TABLE IF EXISTS codevalue //

SHOW WARNINGS//
CREATE  TABLE IF NOT EXISTS codevalue (
id INT NOT NULL AUTO_INCREMENT ,
code_value VARCHAR(45) NOT NULL ,
version INT NULL,
id_code_attribute INT NOT NULL ,
id_indicator_series INT NULL ,
id_tradestrategy INT NULL ,
PRIMARY KEY (id) ,
INDEX codeValue_codeattribute_idx (id_code_attribute ASC) ,
INDEX codeValue_indicatorseries_idx (id_indicator_series ASC) ,
INDEX codeValue_tradestrategy_idx (id_tradestrategy ASC) ,
UNIQUE INDEX codeValue_tradestrategy_codeattribute_uq (id_code_attribute ASC, id_tradestrategy ASC),
UNIQUE INDEX codeValue_indicatorseries_codeattribute_uq (id_indicator_series ASC, id_code_attribute ASC),
CONSTRAINT codeValue_codeattribute_fk
FOREIGN KEY (id_code_attribute )
REFERENCES codeattribute (id )
  ON DELETE NO ACTION
  ON UPDATE NO ACTION,
CONSTRAINT codeValue_indicatorseries_fk
FOREIGN KEY (id_indicator_series )
REFERENCES indicatorseries (id )
  ON DELETE NO ACTION
  ON UPDATE NO ACTION,
CONSTRAINT codevalue_tradestrategy_fk
FOREIGN KEY (id_tradestrategy )
REFERENCES tradestrategy (id )
  ON DELETE NO ACTION
  ON UPDATE NO ACTION)
ENGINE = InnoDB//

SHOW WARNINGS//

-- -----------------------------------------------------
-- Table Domain
-- -----------------------------------------------------
DROP TABLE IF EXISTS domain //

CREATE  TABLE IF NOT EXISTS domain (
id BIGINT NOT NULL AUTO_INCREMENT ,
name VARCHAR(255) NOT NULL ,
description VARCHAR(100) NULL ,
version BIGINT NULL,
PRIMARY KEY (id) ,
UNIQUE INDEX domain_name_uq (name ASC) )
ENGINE = InnoDB//

SHOW WARNINGS//

-- -----------------------------------------------------
-- Table User
-- -----------------------------------------------------
DROP TABLE IF EXISTS USER //

CREATE  TABLE IF NOT EXISTS USER (
id BIGINT NOT NULL AUTO_INCREMENT ,
name VARCHAR(100) NOT NULL ,
first_name VARCHAR(100)  NULL ,
last_name VARCHAR(100)  NULL ,
password VARCHAR(255) NOT NULL ,
ROLES TINYBLOB NULL ,
domain_id BIGINT NOT NULL ,
version BIGINT NULL,
PRIMARY KEY (id) ,
UNIQUE INDEX user_name_uq (name ASC) ,
INDEX user_domain_idx (domain_id ASC) ,
CONSTRAINT user_domain_fk
FOREIGN KEY (domain_id)
REFERENCES domain (id )
  ON DELETE NO ACTION
  ON UPDATE NO ACTION)
ENGINE = InnoDB//

SHOW WARNINGS//

-- -----------------------------------------------------
-- Table Role
-- -----------------------------------------------------
DROP TABLE IF EXISTS ROLE //

CREATE  TABLE IF NOT EXISTS ROLE (
id BIGINT NOT NULL AUTO_INCREMENT ,
name VARCHAR(100) NOT NULL ,
description VARCHAR(100) NULL ,
version BIGINT NULL,
PRIMARY KEY (id) ,
UNIQUE INDEX user_name_uq (name ASC) )
ENGINE = InnoDB//

SHOW WARNINGS//

-- -----------------------------------------------------
-- Table UserRole
-- -----------------------------------------------------
DROP TABLE IF EXISTS userrole //

CREATE  TABLE IF NOT EXISTS userrole (
id BIGINT NOT NULL AUTO_INCREMENT ,
user_id BIGINT NOT NULL ,
role_id BIGINT NOT NULL ,
version BIGINT NULL,
PRIMARY KEY (id) ,
INDEX userrole_Role_idx (role_id ASC) ,
CONSTRAINT userrole_Role_fk
FOREIGN KEY (role_id )
REFERENCES ROLE (id )
  ON DELETE NO ACTION
  ON UPDATE NO ACTION,
INDEX userrole_user_idx (user_id ASC) ,
CONSTRAINT userrole_user_fk
FOREIGN KEY (user_id )
REFERENCES USER (id )
  ON DELETE NO ACTION
  ON UPDATE NO ACTION)

ENGINE = InnoDB//

SHOW WARNINGS//

-- -----------------------------------------------------
-- Table employee dummy table
-- -----------------------------------------------------

DROP TABLE IF EXISTS employee//

CREATE  TABLE IF NOT EXISTS employee (
id BIGINT NOT NULL AUTO_INCREMENT ,
first_name VARCHAR(100)  NULL ,
last_name VARCHAR(100)  NULL ,
description VARCHAR(100)  NULL ,
user_id BIGINT NOT NULL ,
version BIGINT NULL,
PRIMARY KEY (id) ,
INDEX employee_name_uq (first_name ASC, last_name ASC) ,
INDEX user_domain_idx (user_id ASC) ,
CONSTRAINT employee_user_fk
FOREIGN KEY (user_id )
REFERENCES USER (id )
  ON DELETE NO ACTION
  ON UPDATE NO ACTION)
ENGINE = InnoDB//

SHOW WARNINGS//

SET SQL_MODE=@OLD_SQL_MODE//
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS//
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS//
