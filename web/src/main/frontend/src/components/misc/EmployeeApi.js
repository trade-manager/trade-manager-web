import axios from 'axios'
import { config } from '../../Constants'

export const employeeApi = {
  authenticate,
  signup,
  numberOfUsers,
  numberOfEmployees,
  getUsers,
  deleteUser,
  getEmployees,
  deleteEmployee,
  addEmployee
}

function authenticate(username, password) {
  return instance.post('/auth/authenticate', { username, password }, {
    headers: { 'Content-type': 'application/json' }
  })
}

function signup(user) {
  return instance.post('/auth/signup', user, {
    headers: { 'Content-type': 'application/json' }
  })
}

function numberOfUsers() {
  return instance.get('/public/numberOfUsers')
}

function numberOfEmployees() {
  return instance.get('/public/numberOfEmployees')
}

function getUsers(user, username) {
  const url = username ? `/api/users/${username}` : '/api/users'
  return instance.get(url, {
    headers: { 'Authorization': basicAuth(user) }
  })
}

function deleteUser(user, username) {
  return instance.delete(`/api/users/${username}`, {
    headers: { 'Authorization': basicAuth(user) }
  })
}

function getEmployees(user, text) {
  const url = text ? `/api/employees?text=${text}` : '/api/employees'
  return instance.get(url, {
    headers: { 'Authorization': basicAuth(user) }
  })
}

function deleteEmployee(user, id) {
  return instance.delete(`/api/employees/${id}`, {
    headers: { 'Authorization': basicAuth(user) }
  })
}

function addEmployee(user, employee) {
  return instance.post('/api/employees', employee, {
    headers: {
      'Content-type': 'application/json',
      'Authorization': basicAuth(user)
    }
  })
}

// -- Axios

const instance = axios.create({
  baseURL: config.url.API_BASE_URL
})

// -- Helper functions

function basicAuth(user) {
  return `Basic ${user.authdata}`
}