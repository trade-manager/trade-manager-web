import React from 'react'
import { Button, Form, Grid, Image, Input, Table } from 'semantic-ui-react'
import TradingdayForm from './TradingdayForm'

function TradingdayTable({ tradingdays, tradingdayId, tradingdayOpen, tradingdayClose, tradingdayMktGap, tradingdayMktBias, tradingdayMktBar, tradingdayOpenSearch, tradingdayCloseSearch, handleAddTradingday, handleDeleteTradingday, handleSearchTradingday, handleInputChange}) {
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
          <Table.Cell>{tradingday.mktGap}</Table.Cell>
          <Table.Cell>{tradingday.mktBias}</Table.Cell>
          <Table.Cell>{tradingday.mktBar}</Table.Cell>
        </Table.Row>
      )
    })
  }

  return (
    <>
      <Grid stackable divided>
        <Grid.Row columns='2'>
          <Grid.Column width={4}>
            <Form onSubmit={handleSearchTradingday}>
              <Input
                action={{ icon: 'search' }}
                name='tradingdayOpenSearch'
                placeholder='Search by Open'
                value={tradingdayOpenSearch}
                onChange={handleInputChange}
              />
              <Input
                action={{ icon: 'search' }}
                name='tradingdayCloseSearch'
                placeholder='Search by Close'
                value={tradingdayCloseSearch}
                onChange={handleInputChange}
              />
            </Form>
          </Grid.Column>
          <Grid.Column width={3}>
            <TradingdayForm
              tradingdayId={tradingdayId}
              tradingdayOpen={tradingdayOpen}
              tradingdayClose={tradingdayClose}
              tradingdayMktGap={tradingdayMktGap}
              tradingdayMktBias={tradingdayMktBias}
              tradingdayMktBar={tradingdayMktBar}
              handleInputChange={handleInputChange}
              handleAddTradingday={handleAddTradingday}
            />
          </Grid.Column>
        </Grid.Row>
      </Grid>
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