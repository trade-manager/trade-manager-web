import React, { useEffect, useState } from 'react'
import { Navigate } from 'react-router-dom'
import { Container } from 'semantic-ui-react'
import EmployeeList from './EmployeeList'
import { useAuth } from '../context/AuthContext'
import { employeeApi } from '../misc/EmployeeApi'
import { handleLogError } from '../misc/Helpers'

function UserPage() {
  const Auth = useAuth()
  const user = Auth.getUser()
  const isUser = user.role === 'USER'

  const [employees, setEmployees] = useState([])
  const [employeeTextSearch, setEmployeeTextSearch] = useState('')
  const [isEmployeesLoading, setIsEmployeesLoading] = useState(false)

  useEffect(() => {
    handleGetEmployees()
  }, [])

  const handleInputChange = (e, { name, value }) => {
    if (name === 'employeeTextSearch') {
      setEmployeeTextSearch(value)
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

  if (!isUser) {
    return <Navigate to='/' />
  }

  return (
    <Container>
      <EmployeeList
        isEmployeesLoading={isEmployeesLoading}
        employeeTextSearch={employeeTextSearch}
        employees={employees}
        handleInputChange={handleInputChange}
        handleSearchEmployee={handleSearchEmployee}
      />
    </Container>
  )
}

export default UserPage