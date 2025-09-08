import React, { useEffect, useState } from 'react'
import { Navigate } from 'react-router-dom'
import { Container } from 'semantic-ui-react'
import { useAuth } from '../context/AuthContext'
import { employeeApi } from '../misc/EmployeeApi'
import AdminTab from './AdminTab'
import { handleLogError } from '../misc/Helpers'

function AdminPage() {
  const Auth = useAuth()
  const user = Auth.getUser()
  const isAdmin = user.role === 'ADMIN'

  const [users, setUsers] = useState([])
  const [userUsernameSearch, setUserUsernameSearch] = useState('')
  const [isUsersLoading, setIsUsersLoading] = useState(false)

  const [employees, setEmployees] = useState([])
  const [employeeId, setEmployeeId] = useState('')
  const [employeeName, setEmployeeName] = useState('')
  const [employeeFirstName, setEmployeeFirstName] = useState('')
  const [employeeLastName, setEmployeeLastName] = useState('')
  const [employeeEmail, setEmployeeEmail] = useState('')
  const [employeeTextSearch, setEmployeeTextSearch] = useState('')
  const [isEmployeesLoading, setIsEmployeesLoading] = useState(false)

  useEffect(() => {
    handleGetUsers()
    handleGetEmployees()
  }, [])

  const handleInputChange = (e, { name, value }) => {
    if (name === 'userUsernameSearch') {
      setUserUsernameSearch(value)
    } else if (name === 'employeeId') {
      setEmployeeId(value)
    } else if (name === 'employeeName') {
      setEmployeeName(value)
    } else if (name === 'employeeFirstName') {
      setEmployeeFirstName(value)
    } else if (name === 'employeeLastName') {
      setEmployeeLastName(value)
    } else if (name === 'employeeEmail') {
      setEmployeeEmail(value)
    } else if (name === 'employeeTextSearch') {
      setEmployeeTextSearch(value)
    }
  }

  const handleGetUsers = async () => {
    try {
      setIsUsersLoading(true)
      const response = await employeeApi.getUsers(user)
      const users = response.data
      setUsers(users)
    } catch (error) {
      handleLogError(error)
    } finally {
      setIsUsersLoading(false)
    }
  }

  const handleDeleteUser = async (username) => {
    try {
      await employeeApi.deleteUser(user, username)
      await handleGetUsers()
    } catch (error) {
      handleLogError(error)
    }
  }

  const handleSearchUser = async () => {
    try {
      const response = await employeeApi.getUsers(user, userUsernameSearch)
      const data = response.data
      const users = data instanceof Array ? data : [data]
      setUsers(users)
    } catch (error) {
      handleLogError(error)
      setUsers([])
    }
  }

  const handleGetEmployees = async () => {
    try {
      setIsEmployeesLoading(true)
      const response = await employeeApi.getEmployees(user)
      setEmployees(response.data)
    } catch (error) {
      handleLogError(error)
    } finally {
      setIsEmployeesLoading(false)
    }
  }

  const handleDeleteEmployee = async (id) => {
    try {
      await employeeApi.deleteEmployee(user, id)
      await handleGetEmployees()
    } catch (error) {
      handleLogError(error)
    }
  }

  const handleAddEmployee = async () => {
    try {

      const employee = { name: employeeName.trim(), firstName: employeeFirstName.trim() , lastName: employeeLastName.trim(), email: employeeEmail.trim(), user: user}
      console.log(JSON.stringify(employee));

      if (!(employee.email && employee.name)) {

        return
      }
      await employeeApi.addEmployee(user, employee)
      clearEmployeeForm()
      await handleGetEmployees()
    } catch (error) {

      handleLogError(error)
    }
  }

  const handleSearchEmployee = async () => {
    try {
      const response = await employeeApi.getEmployees(user, employeeTextSearch)
      const employees = response.data
      setEmployees(employees)
    } catch (error) {
      handleLogError(error)
      setEmployees([])
    }
  }

  const clearEmployeeForm = () => {
    setEmployeeName('')
    setEmployeeFirstName('')
    setEmployeeLastName('')
    setEmployeeEmail('')
  }

  if (!isAdmin) {
    return <Navigate to='/' />
  }

  return (
    <Container>
      <AdminTab
        isUsersLoading={isUsersLoading}
        users={users}
        userUsernameSearch={userUsernameSearch}
        handleDeleteUser={handleDeleteUser}
        handleSearchUser={handleSearchUser}
        isEmployeesLoading={isEmployeesLoading}
        employees={employees}
        employeeId={employeeId}
        employeeName={employeeName}
        employeeTextSearch={employeeTextSearch}
        handleAddEmployee={handleAddEmployee}
        handleDeleteEmployee={handleDeleteEmployee}
        handleSearchEmployee={handleSearchEmployee}
        handleInputChange={handleInputChange}
      />
    </Container>
  )
}

export default AdminPage