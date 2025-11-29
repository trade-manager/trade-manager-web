import React from 'react'
import { Button, Form, Icon } from 'semantic-ui-react'

function TradingdayForm({ tradingdayId, tradingdayOpen, tradingdayClose, tradingdayMktGap, tradingdayMktBias, tradingdayMktBar, handleInputChange, handleAddTradingday }) {
  let createBtnDisabled = tradingdayOpen.trim() === '' || tradingdayClose.trim() === ''
  return (
    <Form onSubmit={handleAddTradingday}>
      <Form.Group>
        <Form.Input
          name='tradingdayOpen'
          placeholder='Open *'
          value={tradingdayOpen}
          onChange={handleInputChange}
        />
        <Form.Input
          name='tradingdayClose'
          placeholder='Close *'
          value={tradingdayClose}
          onChange={handleInputChange}
        />
        <Form.Input
          name='tradingdayMktGap'
          placeholder='Mkt Bar'
          value={tradingdayMktGap}
          onChange={handleInputChange}
        />
        <Form.Input
          name='tradingdayMktBias'
          placeholder='Mkt Bias'
          value={tradingdayMktBias}
          onChange={handleInputChange}
        />
        <Form.Input
          name='tradingdayMktBar'
          placeholder='Mkt Bar'
          value={tradingdayMktBar}
          onChange={handleInputChange}
        />
        <Button icon labelPosition='right' disabled={createBtnDisabled}>Create<Icon name='add' /></Button>
      </Form.Group>
    </Form>
  )
}

export default TradingdayForm