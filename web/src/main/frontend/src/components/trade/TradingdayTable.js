import React from 'react'
import {Button, Form, Input, Table} from 'semantic-ui-react'
import {utils} from '../misc/Utils'

function TradingdayTable({
                             tradingdays,
                             tradingdayOpenSearch,
                             tradingdayCloseSearch,
                             handleAddTradingday,
                             handleDeleteTradingday,
                             handleSearchTradingday,
                             handleInputChange
                         }) {
    let tradingdayList
    if (tradingdays.length === 0) {
        tradingdayList = (
            <Table.Row key='no-tradingday'>
                <Table.Cell collapsing textAlign='center' colSpan='4'>No tradingday</Table.Cell>
            </Table.Row>
        )
    } else {

        tradingdayList = tradingdays.map(tradingday => {
            return (
                <Table.Row key={tradingday.id}>
                    <Table.Cell collapsing>
                        <Button
                            circular
                            color='red'
                            size='small'
                            icon='trash'
                            onClick={() => handleDeleteTradingday(tradingday.id)}
                        />
                    </Table.Cell>
                    <Table.Cell>{tradingday.id}</Table.Cell>
                    <Table.Cell>{tradingday.open}</Table.Cell>
                    <Table.Cell>{tradingday.close}</Table.Cell>
                    <Table.Cell>{tradingday.marketGap}</Table.Cell>
                    <Table.Cell>{tradingday.marketBias}</Table.Cell>
                    <Table.Cell>{tradingday.marketBar}</Table.Cell>
                </Table.Row>
            )
        })
    }

    return (
        <>
            <Form onSubmit={handleSearchTradingday}>
                <Input
                    type="datetime-local"
                    id="open-date"
                    action={{icon: 'search'}}
                    name='tradingdayOpenSearch'
                    placeholder='Search by Open'
                    value={tradingdayOpenSearch}
                    onChange={handleInputChange}
                />
                <Input
                    type="datetime-local"
                    id="close-date"
                    action={{icon: 'search'}}
                    name='tradingdayCloseSearch'
                    placeholder='Search by Close'
                    value={tradingdayCloseSearch}
                    onChange={handleInputChange}
                />
            </Form>
            <Table compact striped selectable>
                <Table.Header>
                    <Table.Row>
                        <Table.HeaderCell width={1}/>
                        <Table.HeaderCell width={1}>ID</Table.HeaderCell>
                        <Table.HeaderCell width={2}>Open</Table.HeaderCell>
                        <Table.HeaderCell width={2}>Close</Table.HeaderCell>
                        <Table.HeaderCell width={2}>Mkt Gap</Table.HeaderCell>
                        <Table.HeaderCell width={2}>Mkt Bias</Table.HeaderCell>
                        <Table.HeaderCell width={2}>Mkt Bar</Table.HeaderCell>
                    </Table.Row>
                </Table.Header>
                <Table.Body>
                    {tradingdayList}
                </Table.Body>
            </Table>
        </>
    )
}

export default TradingdayTable