import React from 'react'
import { Button, Form, Grid, Image, Input, Table } from 'semantic-ui-react'
import EmployeeForm from './EmployeeForm'

function EmployeeTable({ employees, employeeId, employeeName, employeeTextSearch, handleInputChange, handleAddEmployee, handleDeleteEmployee, handleSearchEmployee }) {
  let employeeList
  if (employees.length === 0) {
    employeeList = (
      <Table.Row key='no-employee'>
        <Table.Cell collapsing textAlign='center' colSpan='4'>No employee</Table.Cell>
      </Table.Row>
    )
  } else {
    employeeList = employees.map(employee => {
      return (
        <Table.Row key={employee.id}>
          <Table.Cell collapsing>
            <Button
              circular
              color='red'
              size='small'
              icon='trash'
              onClick={() => handleDeleteEmployee(employee.id)}
            />
          </Table.Cell>
          <Table.Cell>{employee.id}</Table.Cell>
          <Table.Cell>{employee.name}</Table.Cell>
          <Table.Cell>{employee.firstName}</Table.Cell>
          <Table.Cell>{employee.lastName}</Table.Cell>
          <Table.Cell>{employee.email}</Table.Cell>
        </Table.Row>
      )
    })
  }

  return (
    <>
      <Grid stackable divided>
        <Grid.Row columns='2'>
          <Grid.Column width='5'>
            <Form onSubmit={handleSearchEmployee}>
              <Input
                action={{ icon: 'search' }}
                name='employeeTextSearch'
                placeholder='Search by Title'
                value={employeeTextSearch}
                onChange={handleInputChange}
              />
            </Form>
          </Grid.Column>
          <Grid.Column>
            <EmployeeForm
              employeeId={employeeId}
              employeeName={employeeName}
              handleInputChange={handleInputChange}
              handleAddEmployee={handleAddEmployee}
            />
          </Grid.Column>
        </Grid.Row>
      </Grid>
      <Table compact striped selectable>
        <Table.Header>
          <Table.Row>
            <Table.HeaderCell width={1}/>
            <Table.HeaderCell width={1}>ID</Table.HeaderCell>
            <Table.HeaderCell width={2}>Name</Table.HeaderCell>
            <Table.HeaderCell width={3}>First Name</Table.HeaderCell>
            <Table.HeaderCell width={4}>Last Name</Table.HeaderCell>
            <Table.HeaderCell width={5}>Email</Table.HeaderCell>
          </Table.Row>
        </Table.Header>
        <Table.Body>
          {employeeList}
        </Table.Body>
      </Table>
    </>
  )
}

export default EmployeeTable