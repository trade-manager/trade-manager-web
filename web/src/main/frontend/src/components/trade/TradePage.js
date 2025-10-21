import React, { useEffect, useState } from 'react'
import { Navigate } from 'react-router-dom'
import { Container } from 'semantic-ui-react'
import { useAuth } from '../context/AuthContext'
import { tradingdayApi } from '../misc/TradingdayApi'
import { tradestrategyApi } from '../misc/TradestrategyApi'
import TradeTab from './TradeTab'
import { handleLogError } from '../misc/Helpers'

function TradePage() {
  const Auth = useAuth()
  const user = Auth.getUser()
  const isAdmin = user.role === 'ADMIN'

  const tradingday = tradingdayApi.getTradingdays()
  const [tradingdays, setTradingdays] = useState([])
  const [tradingdayId, setTradingdayId] = useState('')
  const [tradingdayOpen, setTradingdayOpen] = useState('')
  const [tradingdayClose, setTradingdayClose] = useState('')
  const [tradingdayMktGap, setTradingdayMktGap] = useState('')
  const [tradingdayMktBias, setTradingdayMktBias] = useState('')
  const [tradingdayMktBar, setTradingdayMktBar] = useState('')

  const [isTradingdaysLoading, setIsTradingdaysLoading] = useState(false)
  const [tradingdayOpenSearch, setTradingdayOpenSearch] = useState('')
  const [tradingdayCloseSearch, setTradingdayCloseSearch] = useState('')

  const [tradestrategies, setTradestrategies] = useState([])
  const [tradestrategyId, setTradestrategyId] = useState('')
  const [tradestrategyDate, setTradestrategyDate] = useState('')
  const [tradestrategyTrade, setTradestrategyTrade] = useState('')
  const [tradestrategySymbol, seTradestrategySymbol] = useState('')
  const [tradestrategySide, setTradestrategySide] = useState('')
  const [tradestrategyTier, setTradestrategyTier] = useState('')
  const [tradestrategyStrategy, setTradestrategyStrategy] = useState('')
  const [tradestrategyStrategyMgr, setTradestrategyStrategyMgr] = useState('')
  const [tradestrategyPortfolio, setTradestrategyPortfolio] = useState('')
  const [tradestrategyBarSize, setTradestrategyBarSize] = useState('')
  const [tradestrategyChartDays, setTradestrategyChartDays] = useState('')
  const [tradestrategyStatus, setTradestrategyStatus] = useState('')

  const [tradestrategySymbolSearch, setTradestrategySymbolSearch] = useState('')
  const [isTradestrategiesLoading, setIsTradestrategiesLoading] = useState(false)

  useEffect(() => {
    handleGetTradingdays()
    handleGetTradestrategies()
  }, [])

  const handleInputChange = (e, { name, value }) => {
    if (name === 'tradingdayOpenSearch') {
      setTradingdayOpenSearch(value);
    } else if (name === 'tradingdayCloseSearch') {
       setTradingdayCloseSearch(value);
    } else if (name === 'tradingdayId') {
      setTradingdayId(value);
    } else if (name === 'tradingdayOpen') {
      setTradingdayOpen(value);
    } else if (name === 'tradingdayClose') {
      setTradingdayClose(value);
    } else if (name === 'tradingdayMktGap') {
      setTradingdayMktGap(value);
    } else if (name === 'tradingdayMktBias') {
      setTradingdayMktBias(value);
    } else if (name === 'tradingdayMktBar') {
      setTradingdayMktBar(value);
    }
  }

  const handleGetTradingdays = async () => {

    try {

      setIsTradingdaysLoading(true);
      const response = await tradingdayApi.getTradingdays(user, tradingday);
      const tradingdays = response.data;
      console.log("handleGetTradingdays tradingdays:\n" + JSON.stringify(tradingdays));
      setTradingdays(tradingdays);
    } catch (error) {

      handleLogError(error);
    } finally {

      setIsTradingdaysLoading(false);
    }
  }

  const handleDeleteTradingday = async (id) => {

    try {

      await tradingdayApi.deleteTradingday(user, id);
      await handleGetTradingdays();
    } catch (error) {
      handleLogError(error);
    }
  }

  const handleSearchTradingday = async () => {
    try {
      const response = await tradingdayApi.getTradingdays(user, tradingdayOpenSearch, tradingdayCloseSearch);
      const data = response.data;
      const tradingdays = data instanceof Array ? data : [data];
      setTradingdays(tradingdays);
    } catch (error) {

      handleLogError(error);
      setTradingdays([]);
    }
  }

  const handleGetTradestrategies = async () => {
    try {

      setIsTradestrategiesLoading(true);
      const response = await tradestrategyApi.getTradestrategyApi(user, tradingdayId);
      const tradestrategies = response.data;
      console.log("handleGetTradestrategies Tradestrategies:\n" + JSON.stringify(tradestrategies));
      setTradestrategies(tradestrategies);
    } catch (error) {

      handleLogError(error);
    } finally {

      setIsTradestrategiesLoading(false);
    }
  }

  const handleDeleteTradestrategy = async (id) => {
    try {
      await tradestrategyApi.deleteTradestrategy(user, id)
      await handleGetTradestrategies()
    } catch (error) {
      handleLogError(error)
    }
  }

  const handleAddTradingday = async () => {
    try {

      const tradingday = { open: tradingdayOpen.trim(), close: tradingdayClose.trim() , mktGap: tradingdayMktGap.trim(), mktBias: tradingdayMktBias.trim(), mktBar: tradingdayMktBar.trim()}
      console.log("tradingday: " + JSON.stringify(tradingday));

      if (!(tradingday.open && tradingday.close)) {

        return;
      }
      await tradingdayApi.addTradingday(user, tradingday);
      await handleGetTradingdays();
      clearTradingdayForm();
    } catch (error) {

      handleLogError(error)
    }
  }

  const handleAddTradestrategy = async () => {
    try {

      const tradestrategy = { date: tradestrategyDate.trim(), trade: tradestrategyTrade.trim() , symbol: tradestrategySymbol.trim(), side: tradestrategySide.trim(), tier: tradestrategyTier.trim(), strategy: tradestrategyStrategy.trim(), strategyMgr: tradestrategyStrategyMgr.trim(), portfolio: tradestrategyPortfolio.trim(), barSize: tradestrategyBarSize.trim(), chartDays: tradestrategyChartDays.trim()}
      console.log("tradestrategy: " + JSON.stringify(tradestrategy));

      if (!(tradestrategy.date && tradestrategy.symbol)) {

        return;
      }

      await tradestrategyApi.addTradestrategy(user, tradestrategy);
      await handleGetTradestrategies();
      clearTradestrategyForm();
    } catch (error) {

      handleLogError(error)
    }
  }

  const handleSearchTradestrategy = async () => {
    try {
      const response = await tradestrategyApi.getTradestrategies(user, tradestrategySymbolSearch)
      const tradestrategies = response.data;
      console.log("handleSearchTradestrategy tradestrategies:\n" + JSON.stringify(tradestrategies));
      setTradestrategies(tradestrategies)
    } catch (error) {

      handleLogError(error)
      setTradestrategies([])
    }
  }

  const clearTradingdayForm = () => {
    setTradingdayOpen('');
    setTradingdayClose('');
    setTradingdayMktGap('');
    setTradingdayMktBias('');
    setTradingdayMktBar('');
    console.log("Info: clearTradingdayForm");
  }

  const clearTradestrategyForm = () => {
    setTradestrategyDate('');
    setTradestrategyTrade('');
    seTradestrategySymbol('');
    setTradestrategySide('');
    setTradestrategyTier('');
    setTradestrategyStrategy('');
    setTradestrategyStrategyMgr('');
    setTradestrategyPortfolio('');
    setTradestrategyBarSize('');
    setTradestrategyChartDays('');
    setTradestrategyStatus('');
    console.log("Info: clearTradestrategyForm");
  }

  if (!isAdmin) {
    return <Navigate to='/' />
  }

  return (
    <Container>
      <TradeTab
        isTradingdaysLoading={isTradingdaysLoading}
        tradingdays={tradingdays}
        tradingdayOpenSearch={tradingdayOpenSearch}
        tradingdayCloseSearch={tradingdayCloseSearch}
        handleAddTradingday={handleAddTradingday}
        handleDeleteTradingday={handleDeleteTradingday}
        handleSearchTradingday={handleSearchTradingday}
        isTradestrategiesLoading={isTradestrategiesLoading}
        tradestrategies={tradestrategies}
        tradestrategyId={tradestrategyId}
        tradestrategyDate={tradestrategyDate}
        tradestrategyTrade={tradestrategyTrade}
        tradestrategySymbol={tradestrategySymbol}
        tradestrategySide={tradestrategySide}
        tradestrategyTier={tradestrategyTier}
        tradestrategyStrategy={tradestrategyStrategy}
        tradestrategyStrategyMgr={tradestrategyStrategyMgr}
        tradestrategyPortfolio={tradestrategyPortfolio}
        tradestrategyBarSize={tradestrategyBarSize}
        tradestrategyChartDays={tradestrategyChartDays}
        tradestrategyStatus={tradestrategyStatus}
        tradestrategySymbolSearch={tradestrategySymbolSearch}
        handleAddTradestrategy={handleAddTradestrategy}
        handleDeleteTradestrategy={handleDeleteTradestrategy}
        handleSearchTradestrategy={handleSearchTradestrategy}
        handleInputChange={handleInputChange}
      />
    </Container>
  )
}

export default TradePage