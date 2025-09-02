import React from 'react'
import { Tab } from 'semantic-ui-react'
import UserTable from './UserTable'
import EmployeeTable from './EmployeeTable'

function AdminTab(props) {
  const { handleInputChange } = props
  const { isUsersLoading, users, userUsernameSearch, handleDeleteUser, handleSearchUser } = props
  const { isEmployeesLoading, employees, employeeId, employeeName, employeeTextSearch, handleAddEmployee, handleDeleteEmployee, handleSearchEmployee } = props

  const panes = [
    {
      menuItem: { key: 'users', icon: 'users', content: 'Users' },
      render: () => (
        <Tab.Pane loading={isUsersLoading}>
          <UserTable
            users={users}
            userUsernameSearch={userUsernameSearch}
            handleInputChange={handleInputChange}
            handleDeleteUser={handleDeleteUser}
            handleSearchUser={handleSearchUser}
          />
        </Tab.Pane>
      )
    },
    {
      menuItem: { key: 'employees', icon: 'employee', content: 'Employees' },
      render: () => (
        <Tab.Pane loading={isEmployeesLoading}>
          <EmployeeTable
            employees={employees}
            employeeId={employeeId}
            employeeName={employeeName}
            employeeTextSearch={employeeTextSearch}
            handleInputChange={handleInputChange}
            handleAddEmployee={handleAddEmployee}
            handleDeleteEmployee={handleDeleteEmployee}
            handleSearchEmployee={handleSearchEmployee}
          />
        </Tab.Pane>
      )
    }
  ]

  return (
    <Tab menu={{ attached: 'top' }} panes={panes} />
  )
}

export default AdminTab