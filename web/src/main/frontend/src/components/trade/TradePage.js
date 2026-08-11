import React, {useEffect, useState} from 'react'
import {Navigate} from 'react-router-dom'
import {Container} from 'semantic-ui-react'
import {useAuth} from '../context/AuthContext'
import {tradingdayApi} from '../misc/TradingdayApi'
import {tradestrategyApi} from '../misc/TradestrategyApi'
import TradeTab from './TradeTab'
import {ERROR, logMessage} from '../misc/LoggerApi'

function TradePage() {
    const Auth = useAuth()
    const user = Auth.getUser()
    const isUser = user.role === 'USER'

    const [tradingdays, setTradingdays] = useState([])
    const [tradingdayId, setTradingdayId] = useState('')
    const [tradingdayOpen, setTradingdayOpen] = useState('')
    const [tradingdayClose, setTradingdayClose] = useState('')
    const [tradingdayMktGap, setTradingdayMktGap] = useState('')
    const [tradingdayMktBias, setTradingdayMktBias] = useState('')
    const [tradingdayMktBar, setTradingdayMktBar] = useState('')

    const [tradingdayOpenSearch, setTradingdayOpenSearch] = useState('')
    const [tradingdayCloseSearch, setTradingdayCloseSearch] = useState('')
    const [isTradingdaysLoading, setIsTradingdaysLoading] = useState(false)

    const [tradestrategies, setTradestrategies] = useState([])
    const [tradestrategyId, setTradestrategyId] = useState('')
    const [tradestrategyDate, setTradestrategyDate] = useState('')
    const [tradestrategyTrade, setTradestrategyTrade] = useState('')
    const [tradestrategySymbol, setTradestrategySymbol] = useState('')
    const [tradestrategySide, setTradestrategySide] = useState('')
    const [tradestrategyTier, setTradestrategyTier] = useState('')
    const [tradestrategyStrategy, setTradestrategyStrategy] = useState('')
    const [tradestrategyStrategyMgr, setTradestrategyStrategyMgr] = useState('')
    const [tradestrategyPortfolio, setTradestrategyPortfolio] = useState('')
    const [tradestrategyBarSize, setTradestrategyBarSize] = useState('')
    const [tradestrategyChartDays, setTradestrategyChartDays] = useState('')
    const [tradestrategyStatus, setTradestrategyStatus] = useState('')

    const [tradestrategyOpenSearch, setTradestrategyOpenSearch] = useState('')
    const [tradestrategyCloseSearch, setTradestrategyCloseSearch] = useState('')
    const [isTradestrategiesLoading, setIsTradestrategiesLoading] = useState(false)

    useEffect(() => {
        handleGetTradingdays()
        handleGetTradestrategies()
    }, [])

    const handleInputChange = (e, {name, value}) => {
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
        } else if (name === 'tradestrategyOpenSearch') {
            setTradestrategyOpenSearch(value);
        } else if (name === 'tradestrategyCloseSearch') {
            setTradestrategyCloseSearch(value);
        } else if (name === 'tradestrategyId') {
            setTradestrategyId(value);
        } else if (name === 'tradestrategyDate') {
            setTradestrategyDate(value);
        } else if (name === 'tradestrategyTrade') {
            setTradestrategyTrade(value);
        } else if (name === 'tradestrategySymbol') {
            setTradestrategySymbol(value);
        } else if (name === 'tradestrategySide') {
            setTradestrategySide(value);
        } else if (name === 'tradestrategyTier') {
            setTradestrategyTier(value);
        } else if (name === 'tradestrategyStrategy') {
            setTradestrategyStrategy(value);
        } else if (name === 'tradestrategyStrategyMgr') {
            setTradestrategyStrategyMgr(value);
        } else if (name === 'tradestrategyPortfolio') {
            setTradestrategyPortfolio(value);
        } else if (name === 'tradestrategyBarSize') {
            setTradestrategyBarSize(value);
        } else if (name === 'tradestrategyChartDays') {
            setTradestrategyChartDays(value);
        } else if (name === 'tradestrategyStatus') {
            setTradestrategyStatus(value);
        }
    }

    const handleGetTradingdays = async () => {

        try {
            setIsTradingdaysLoading(true);
            const response = await tradingdayApi.getTradingdays(user, tradingdayOpen, tradingdayClose);
            const data = response.data;
            console.log("handleGetTradingdays data:\n" + JSON.stringify(data));
            const tradingdays = data instanceof Array ? data : [data];

            if (tradingdays.length > 0) {

                const localDateOpen = new Date(tradingdays[0].open);
                const localISOTimeOpen = new Date(localDateOpen.getTime() - (localDateOpen.getTimezoneOffset() * 60000)).toISOString().slice(0, 16);
                const localDateClose = new Date(tradingdays[tradingdays.length - 1].close);
                const localISOTimeClose = new Date(localDateClose.getTime() - (localDateClose.getTimezoneOffset() * 60000)).toISOString().slice(0, 16);
                setTradingdayOpenSearch(localISOTimeOpen);
                setTradingdayCloseSearch(localISOTimeClose);
                setTradestrategyOpenSearch(localISOTimeOpen);
                setTradestrategyCloseSearch(localISOTimeClose);
            }
            setTradingdays(tradingdays);
        } catch (error) {
            console.log("handleGetTradingdays error:\n" + JSON.stringify(error));
            logMessage(ERROR, error, user);
        } finally {

            setIsTradingdaysLoading(false);
        }
    }

    const handleDeleteTradingday = async (id) => {

        try {

            await tradingdayApi.deleteTradingday(user, id);
            await handleGetTradingdays();
        } catch (error) {
            logMessage(ERROR, error, user);
        }
    }

    const handleSearchTradingday = async () => {
        try {

            const response = await tradingdayApi.getTradingdays(user, tradingdayOpenSearch, tradingdayCloseSearch);
            const data = response.data;
            const tradingdays = data instanceof Array ? data : [data];
            setTradingdays(tradingdays);
        } catch (error) {

            logMessage(ERROR, error, user);
            setTradingdays([]);
        }
    }

    const handleGetTradestrategies = async () => {
        try {

            setIsTradestrategiesLoading(true);
            const response = await tradestrategyApi.getTradestrategies(user, tradingdayOpen, tradingdayClose);
            const tradestrategies = response.data;
            setTradestrategies(tradestrategies);
        } catch (error) {

            logMessage(ERROR, error, user);
        } finally {

            setIsTradestrategiesLoading(false);
        }
    }

    const handleDeleteTradestrategy = async (id) => {
        try {
            await tradestrategyApi.deleteTradestrategy(user, id)
            await handleGetTradestrategies()
        } catch (error) {
            logMessage(ERROR, error, user)
        }
    }

    const handleAddTradingday = async () => {
        try {

            const tradingday = {
                open: tradingdayOpen.trim(),
                close: tradingdayClose.trim(),
                mktGap: tradingdayMktGap.trim(),
                mktBias: tradingdayMktBias.trim(),
                mktBar: tradingdayMktBar.trim()
            }
            console.log("tradingday: " + JSON.stringify(tradingday));

            if (!(tradingday.open && tradingday.close)) {

                return;
            }

            await tradingdayApi.addTradingday(user, tradingday);
            await handleGetTradingdays();
            clearTradingdayForm();
        } catch (error) {

            logMessage(ERROR, error, user)
        }
    }

    const handleAddTradestrategy = async () => {
        try {

            const tradestrategy = {
                date: tradestrategyDate.trim(),
                trade: tradestrategyTrade.trim(),
                symbol: tradestrategySymbol.trim(),
                side: tradestrategySide.trim(),
                tier: tradestrategyTier.trim(),
                strategy: tradestrategyStrategy.trim(),
                strategyMgr: tradestrategyStrategyMgr.trim(),
                portfolio: tradestrategyPortfolio.trim(),
                barSize: tradestrategyBarSize.trim(),
                chartDays: tradestrategyChartDays.trim()
            }
            console.log("tradestrategy: " + JSON.stringify(tradestrategy));

            if (!(tradestrategy.date && tradestrategy.symbol)) {

                return;
            }

            await tradestrategyApi.addTradestrategy(user, tradestrategy);
            await handleGetTradestrategies();
            clearTradestrategyForm();
        } catch (error) {

            logMessage(ERROR, error, user)
        }
    }

    const handleSearchTradestrategy = async () => {
        try {

            const response = await tradestrategyApi.getTradestrategies(user, tradestrategyOpenSearch, tradestrategyCloseSearch)
            const tradestrategies = response.data;
            setTradestrategies(tradestrategies)
        } catch (error) {

            logMessage(ERROR, error, user)
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
        setTradestrategySymbol('');
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

    if (!isUser) {
        return <Navigate to='/'/>
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
                tradestrategyOpenSearch={tradestrategyOpenSearch}
                tradestrategyCloseSearch={tradestrategyCloseSearch}
                handleAddTradestrategy={handleAddTradestrategy}
                handleDeleteTradestrategy={handleDeleteTradestrategy}
                handleSearchTradestrategy={handleSearchTradestrategy}
                handleInputChange={handleInputChange}
            />
        </Container>
    )
}

export default TradePage