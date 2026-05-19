import React from 'react'
import {Tab} from 'semantic-ui-react'
import TradingdayTable from './TradingdayTable'
import TradestrategyTable from './TradestrategyTable'

function TradeTab(props) {
    const {handleInputChange} = props
    const {
        isTradingdaysLoading,
        tradingdays,
        tradingdayOpen,
        tradingdayClose,
        tradingdayMktGap,
        tradingdayMktBias,
        tradingdayMktBar,
        tradingdayOpenSearch,
        tradingdayCloseSearch,
        handleAddTradingday,
        handleDeleteTradingday,
        handleSearchTradingday
    } = props
    const {
        isTradestrategiesLoading,
        tradestrategies,
        tradestrategyId,
        tradestrategyDate,
        tradestrategyTrade,
        tradestrategySymbol,
        tradestrategySide,
        tradestrategyTier,
        tradestrategyStrategy,
        tradestrategyStrategyMgr,
        tradestrategyPortfolio,
        tradestrategyBarSize,
        tradestrategyChartDays,
        tradestrategySymbolSearch,
        handleAddTradestrategy,
        handleDeleteTradestrategy,
        handleSearchTradestrategy
    } = props

    const panes = [
        {
            menuItem: {key: 'tradingdays', icon: 'tradingday', content: 'Tradingdays'},
            render: () => (
                <Tab.Pane loading={isTradingdaysLoading}>
                    <TradingdayTable
                        tradingdays={tradingdays}
                        tradingdayOpen={tradingdayOpen}
                        tradingdayClose={tradingdayClose}
                        tradingdayMktGap={tradingdayMktGap}
                        tradingdayMktBias={tradingdayMktBias}
                        tradingdayMktBar={tradingdayMktBar}
                        tradingdayOpenSearch={tradingdayOpenSearch}
                        tradingdayCloseSearch={tradingdayCloseSearch}
                        handleInputChange={handleInputChange}
                        handleAddTradingday={handleAddTradingday}
                        handleDeleteTradingday={handleDeleteTradingday}
                        handleSearchTradingday={handleSearchTradingday}
                    />
                </Tab.Pane>
            )
        },
        {
            menuItem: {key: 'tradestrategies', icon: 'tradingday', content: 'Tradestrategies'},
            render: () => (
                <Tab.Pane loading={isTradestrategiesLoading}>
                    <TradestrategyTable
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
                        tradestrategySymbolSearch={tradestrategySymbolSearch}
                        handleInputChange={handleInputChange}
                        handleAddTradestrategy={handleAddTradestrategy}
                        handleDeleteTradestrategy={handleDeleteTradestrategy}
                        handleSearchTradestrategy={handleSearchTradestrategy}
                    />
                </Tab.Pane>
            )
        }
    ]

    return (
        <Tab menu={{attached: 'top'}} panes={panes}/>
    )
}

export default TradeTab