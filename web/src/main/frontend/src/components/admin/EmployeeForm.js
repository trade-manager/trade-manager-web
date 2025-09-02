import React from 'react'
import { Button, Form, Icon } from 'semantic-ui-react'

function EmployeeForm({ employeeId, employeeName, handleInputChange, handleAddEmployee }) {
  const createBtnDisabled = employeeId.trim() === '' || employeeName.trim() === ''
  return (
    <Form onSubmit={handleAddEmployee}>
      <Form.Group>
        <Form.Input
          name='employeeId'
          placeholder='ID *'
          value={employeeId}
          onChange={handleInputChange}
        />
        <Form.Input
          name='employeeName'
          placeholder='Name *'
          value={employeeName}
          onChange={handleInputChange}
        />
        <Button icon labelPosition='right' disabled={createBtnDisabled}>
          Create<Icon name='add' />
        </Button>
      </Form.Group>
    </Form>
  )
}

export default EmployeeForm