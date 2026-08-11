import React from 'react'
import {Button, Form, Input, Table} from 'semantic-ui-react'

function TradestrategyTable({
                                tradestrategies,
                                tradestrategyOpenSearch,
                                tradestrategyCloseSearch,
                                handleAddTradestrategy,
                                handleDeleteTradestrategy,
                                handleSearchTradestrategy,
                                handleInputChange
                            }) {
    let tradestrategyList
    if (tradestrategies.length === 0) {
        tradestrategyList = (
            <Table.Row key='no-tradestrategy'>
                <Table.Cell collapsing textAlign='center' colSpan='6'>No Tradestrategy</Table.Cell>
            </Table.Row>
        )
    } else {
        tradestrategyList = tradestrategies.map(tradestrategy => {
            return (
                <Table.Row key={tradestrategy.id}>
                    <Table.Cell collapsing>
                        <Button
                            circular
                            color='red'
                            size='small'
                            icon='trash'
                            disabled={tradestrategy.contract.symbol === ''}
                            onClick={() => handleDeleteTradestrategy(tradestrategy.id)}
                        />
                    </Table.Cell>
                    <Table.Cell>{tradestrategy.id}</Table.Cell>
                    <Table.Cell>{tradestrategy.tradingday.open}</Table.Cell>
                    <Table.Cell>{tradestrategy.trade}</Table.Cell>
                    <Table.Cell>{tradestrategy.contract.symbol}</Table.Cell>
                    <Table.Cell>{tradestrategy.side}</Table.Cell>
                    <Table.Cell>{tradestrategy.teir}</Table.Cell>
                    <Table.Cell>{tradestrategy.strategy.name}</Table.Cell>
                    <Table.Cell>{tradestrategy.strategy.strategyMgr.name}</Table.Cell>
                    <Table.Cell>{tradestrategy.portfolio.name}</Table.Cell>
                    <Table.Cell>{tradestrategy.barSize}</Table.Cell>
                    <Table.Cell>{tradestrategy.chartDays}</Table.Cell>
                    <Table.Cell>{tradestrategy.status}</Table.Cell>
                </Table.Row>
            )
        })
    }

    return (
        <>
            <Form onSubmit={handleSearchTradestrategy}>
                <Input
                    type="datetime-local"
                    id="start-date"
                    action={{icon: 'search'}}
                    name='tradestrategyOpenSearch'
                    placeholder='Search by start date'
                    value={tradestrategyOpenSearch}
                    onChange={handleInputChange}
                />
                <Input
                    type="datetime-local"
                    id="end-date"
                    action={{icon: 'search'}}
                    name='tradestrategyCloseSearch'
                    placeholder='Search by end date'
                    value={tradestrategyCloseSearch}
                    onChange={handleInputChange}
                />
            </Form>
            <Table compact striped selectable>
                <Table.Header>
                    <Table.Row>
                        <Table.HeaderCell width={1}/>
                        <Table.HeaderCell width={1}>ID</Table.HeaderCell>
                        <Table.HeaderCell width={3}>Open</Table.HeaderCell>
                        <Table.HeaderCell width={4}>Trade</Table.HeaderCell>
                        <Table.HeaderCell width={5}>Symbol</Table.HeaderCell>
                        <Table.HeaderCell width={2}>Side</Table.HeaderCell>
                        <Table.HeaderCell width={2}>Tier</Table.HeaderCell>
                        <Table.HeaderCell width={2}>Strategy</Table.HeaderCell>
                        <Table.HeaderCell width={2}>Strategy Mgr</Table.HeaderCell>
                        <Table.HeaderCell width={2}>Portfolio</Table.HeaderCell>
                        <Table.HeaderCell width={2}>Bar Size</Table.HeaderCell>
                        <Table.HeaderCell width={2}>Chart Days</Table.HeaderCell>
                        <Table.HeaderCell width={2}>Status</Table.HeaderCell>
                    </Table.Row>
                </Table.Header>
                <Table.Body>
                    {tradestrategyList}
                </Table.Body>
            </Table>
        </>
    )
}

export default TradestrategyTable