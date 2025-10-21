import React from 'react'
import { Form, Button, Input, Table } from 'semantic-ui-react'

function jsonToCsv(jsonData) {
  // Extract headers from the first JSON object
  const headers = ['name'];
  // Create the header row
  const csvHeader = headers.join(',');

  // Create data rows
  const csvRows = jsonData.map(obj => {
    return headers.map(header => {
      // Handle cases where a key might be missing in an object
      return obj[header] !== undefined ? obj[header] : '';
    }).join(',');
  });

  // Combine header and data rows
  return [...csvRows].join(',');
}

function TradestrategyTable({ tradestrategies, tradestrategySymbolSearch, handleDeleteTradestrategy, handleSearchTradestrategy , handleInputChange}) {
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
              disabled={tradestrategy.symbol === ''}
              onClick={() => handleDeleteTradestrategy(tradestrategy.id)}
            />
          </Table.Cell>
          <Table.Cell>{tradestrategy.id}</Table.Cell>
          <Table.Cell>{tradestrategy.date}</Table.Cell>
          <Table.Cell>{tradestrategy.trade}</Table.Cell>
          <Table.Cell>{tradestrategy.symbol}</Table.Cell>
          <Table.Cell>{tradestrategy.side}</Table.Cell>
          <Table.Cell>{tradestrategy.teir}</Table.Cell>
          <Table.Cell>{jsonToCsv(tradestrategy.strategy)}</Table.Cell>
          <Table.Cell>{jsonToCsv(tradestrategy.strategyMgr)}</Table.Cell>
          <Table.Cell>{jsonToCsv(tradestrategy.portfolio)}</Table.Cell>
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
          action={{ icon: 'search' }}
          name='tradestrategySymbolSearch'
          placeholder='Search by Symbol'
          value={tradestrategySymbolSearch}
          onChange={handleInputChange}
        />
      </Form>
      <Table compact striped selectable>
        <Table.Header>
          <Table.Row>
            <Table.HeaderCell width={1}/>
            <Table.HeaderCell width={1}>ID</Table.HeaderCell>
            <Table.HeaderCell width={3}>Date</Table.HeaderCell>
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