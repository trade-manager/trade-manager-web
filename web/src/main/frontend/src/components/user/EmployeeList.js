import React from 'react'
import { Grid, Header, Form, Icon, Image, Input, Item, Segment } from 'semantic-ui-react'

function EmployeeList({ isEmployeesLoading, employeeTextSearch, employees, handleInputChange, handleSearchEmployee }) {
  let employeeList
  if (employees.length === 0) {
    employeeList = <Item key='no-employee'>No employee</Item>
  } else {
    employeeList = employees.map(employee => {
      return (
        <Item key={employee.id}>
          <Image src={`http://covers.openlibrary.org/b/id/${employee.id}-M.jpg`} size='tiny' bordered rounded />
          <Item.Content>
            <Item.Header>{employee.name}</Item.Header>
            <Item.Meta>{employee.id}</Item.Meta>
            <Item.Description>
              <Image src='https://react.semantic-ui.com/images/wireframe/short-paragraph.png' />
            </Item.Description>
          </Item.Content>
        </Item>
      )
    })
  }

  return (
    <Segment loading={isEmployeesLoading} color='blue'>
      <Grid stackable divided>
        <Grid.Row columns='2'>
          <Grid.Column width='3'>
            <Header as='h2'>
              <Icon name='employee' />
              <Header.Content>Employees</Header.Content>
            </Header>
          </Grid.Column>
          <Grid.Column>
            <Form onSubmit={handleSearchEmployee}>
              <Input
                action={{ icon: 'search' }}
                name='employeeTextSearch'
                placeholder='Search by Name'
                value={employeeTextSearch}
                onChange={handleInputChange}
              />
            </Form>
          </Grid.Column>
        </Grid.Row>
      </Grid>
      <Item.Group divided unstackable relaxed link>
        {employeeList}
      </Item.Group>
    </Segment>
  )
}

export default EmployeeList