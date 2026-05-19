import React from 'react'
import {Button, Form, Icon} from 'semantic-ui-react'

function TradestrategyForm({
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
                               tradestrategyStatus,
                               handleAddTradestrategy
                           }) {
    let createBtnDisabled = tradestrategyDate.trim() === '' || tradestrategySymbol.trim() === ''
    return (
        <Form onSubmit={handleAddTradestrategy}>
            <Form.Group>
                <Form.Input
                    name='tradestrategyDate'
                    placeholder='Date *'
                    value={tradestrategyDate}
                    onChange={handleInputChange}
                />
                <Form.Input
                    name='tradestrategyTrade'
                    placeholder='Trade *'
                    value={tradestrategyTrade}
                    onChange={handleInputChange}
                />
                <Form.Input
                    name='tradestrategySymbol'
                    placeholder='Last Symbol *'
                    value={tradestrategySymbol}
                    onChange={handleInputChange}
                />
                <Form.Input
                    name='tradestrategySide'
                    placeholder='Side *'
                    value={tradestrategySide}
                    onChange={handleInputChange}
                />
                <Form.Input
                    name='tradestrategyTier'
                    placeholder='Tier *'
                    value={tradestrategyTier}
                    onChange={handleInputChange}
                />
                <Form.Input
                    name='tradestrategyStrategy'
                    placeholder='Strategy *'
                    value={tradestrategyStrategy}
                    onChange={handleInputChange}
                />
                <Form.Input
                    name='tradestrategyStrategyMgr'
                    placeholder='Strategy Mgr'
                    value={tradestrategyStrategyMgr}
                    onChange={handleInputChange}
                />
                <Form.Input
                    name='tradestrategyPortfolio'
                    placeholder='Portfolio'
                    value={tradestrategyPortfolio}
                    onChange={handleInputChange}
                />
                <Form.Input
                    name='tradestrategyBarSize'
                    placeholder='Bar Size *'
                    value={tradestrategyBarSize}
                    onChange={handleInputChange}
                />
                <Form.Input
                    name='tradestrategyChartDays'
                    placeholder='Chart Days *'
                    value={tradestrategyChartDays}
                    onChange={handleInputChange}
                />
                <Form.Input
                    name='tradestrategyStatus'
                    placeholder='Status'
                    value={tradestrategyStatus}
                    onChange={handleInputChange}
                />
                <Button icon labelPosition='right' disabled={createBtnDisabled}>Create<Icon name='add'/></Button>
            </Form.Group>
        </Form>
    )
}

export default TradestrategyForm