import React, { useState, useEffect } from 'react'
import { NavLink, Navigate } from 'react-router-dom'
import { Button, Form, Grid, Segment, Message } from 'semantic-ui-react'
import { useAuth } from '../context/AuthContext'
import { employeeApi } from '../misc/EmployeeApi'
import { handleLogError } from '../misc/Helpers'


function Signup() {
  const Auth = useAuth()
  const isLoggedIn = Auth.userIsAuthenticated()

  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [name, setName] = useState('')
  const [email, setEmail] = useState('')
  const [role, setRole] = useState('')
  const [domain, setDomain] = useState('')
  const [isError, setIsError] = useState(false)
  const [errorMessage, setErrorMessage] = useState('')

  const [roleOptions, setRoleOptions] = useState([]);
  const [domainOptions, setDomainOptions] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  // Function to fetch data from API
  const fetchRoleOptions = async (query) => {

    setLoading(true);
    setIsError(false);

    try {

      const response = await employeeApi.getRoles();
console.log("roles: " + JSON.stringify(response.data));
      setRoleOptions(response.data); // Assuming API returns data in 'results' array
    } catch (error) {

      handleLogError(error)

      if (error.response && error.response.data) {

        const errorData = error.response.data
        let errorMessage = 'Invalid fields'

        if (errorData.status === 409) {

          errorMessage = errorData.message
        } else if (errorData.status === 400) {

          errorMessage = errorData.errors[0].defaultMessage
        }

        setIsError(true)
        setErrorMessage(errorMessage)
      }
    } finally {

      setLoading(false);
    }
  };

  // Function to fetch data from API
  const fetchDomainOptions = async (query) => {

    setLoading(true);
    setIsError(false);

    try {

      const response = await employeeApi.getDomains();
console.log("domains: " + JSON.stringify(response.data));
      setDomainOptions(response.data); // Assuming API returns data in 'results' array
    } catch (error) {

      handleLogError(error)

      if (error.response && error.response.data) {

        const errorData = error.response.data
        let errorMessage = 'Invalid fields'

        if (errorData.status === 409) {

          errorMessage = errorData.message
        } else if (errorData.status === 400) {

          errorMessage = errorData.errors[0].defaultMessage
        }

        setIsError(true)
        setErrorMessage(errorMessage)
      }
    } finally {

      setLoading(false);
    }
  };

  // Debounce the API call to avoid excessive requests
  useEffect(() => {

    const handler = setTimeout(() => {

      if (domain) {

        fetchDomainOptions(role);
      } else {

        setDomainOptions([]); // Clear options if input is empty
      }

      if (role) {

        fetchRoleOptions(role);
      } else {

        setRoleOptions([]); // Clear options if input is empty
      }
    }, 500); // Adjust debounce time as needed

    return () => {
      clearTimeout(handler);
    };
  }, [role, domain]);

  const handleDomainInputChange = (event) => {
    console.log("event.target.value: " + JSON.stringify(event.target.value));
    setDomain(event.target.value);
  };

  const handleDomainOptionSelect = (option) => {
  console.log("Domain option: " + JSON.stringify(option));
    setDomain(option); // Or whatever property you want to display
    setDomainOptions([]); // Clear options after selection
  };

  const handleRoleInputChange = (event) => {
    console.log("event.target.value: " + JSON.stringify(event.target.value));
    setRole(event.target.value);
  };

  const handleRoleOptionSelect = (option) => {
  console.log("Role option: " + JSON.stringify(option));
    setRole(option); // Or whatever property you want to display
    setRoleOptions([]); // Clear options after selection
  };

  const handleInputChange = (e, { name, value }) => {
    if (name === 'username') {
      setUsername(value)
    } else if (name === 'password') {
      setPassword(value)
    } else if (name === 'name') {
      setName(value)
    } else if (name === 'email') {
      setEmail(value)
    } else if (name === 'role') {
      setRole(value)
    } else if (name === 'domain') {
       setDomain(value)
    }
  }

  const handleSubmit = async (e) => {
    e.preventDefault()

    if (!(username && password && name && email && role && domain)) {
      setIsError(true)
      setErrorMessage('Please, inform all fields!')
      return
    }

    const user = {'username': username,'password': password, 'name': name, 'email': email, 'domain': domain, 'roles': [role]};

    try {

      console.log(JSON.stringify(user));
      const response = await employeeApi.signup(user)
      const { id, name, role } = response.data
      const authdata = window.btoa(username + ':' + password)
      const authenticatedUser = { id, name, role, authdata }

      Auth.userLogin(authenticatedUser)

      setUsername('')
      setPassword('')
      setName('')
      setEmail('')
      setRole('')
      setDomain('')
      setIsError(false)
      setErrorMessage('')
    } catch (error) {

      handleLogError(error)

      if (error.response && error.response.data) {

        const errorData = error.response.data
        let errorMessage = 'Invalid fields'

        if (errorData.status === 409) {

          errorMessage = errorData.message
        } else if (errorData.status === 400) {

          errorMessage = errorData.errors[0].defaultMessage
        }

        setIsError(true)
        setErrorMessage(errorMessage)
      }
    }
  }

  if (isLoggedIn) {
    return <Navigate to='/' />
  }

  return (
    <Grid textAlign='center'>
      <Grid.Column style={{ maxWidth: 450 }}>
        <Form size='large' onSubmit={handleSubmit}>
          <Segment>
            <Form.Input
              fluid
              autoFocus
              name='username'
              icon='user'
              iconPosition='left'
              placeholder='Username'
              value={username}
              onChange={handleInputChange}
            />
            <Form.Input
              fluid
              name='password'
              icon='lock'
              iconPosition='left'
              placeholder='Password'
              type='password'
              value={password}
              onChange={handleInputChange}
            />
            <Form.Input
              fluid
              name='name'
              icon='address card'
              iconPosition='left'
              placeholder='Name'
              value={name}
              onChange={handleInputChange}
            />
            <Form.Input
              fluid
              name='email'
              icon='at'
              iconPosition='left'
              placeholder='Email'
              value={email}
              onChange={handleInputChange}
            />
            <div>
                <Form.Input
                  fluid
                  type="text"
                  name='role'
                  icon='at'
                  iconPosition='left'
                  placeholder='Role'
                  value={role.name}
                  onChange={handleRoleInputChange}
                />
                  {loading && <p>Loading options...</p>}
                  {error && <p style={{ color: 'red' }}>{error}</p>}

                  {roleOptions.length > 0 && (
                    <ul style={{ border: '1px solid #ccc', maxHeight: '200px', overflowY: 'auto' }}>
                      {roleOptions.map((option) => (
                        <li key={option.id} onClick={() => handleRoleOptionSelect(option)} style={{ padding: '8px', cursor: 'pointer' }}>
                          {option.name} {/* Or the property you want to display */}
                        </li>
                      ))}
                    </ul>
                  )}
            </div>
            <div>
                <Form.Input
                  fluid
                  type="text"
                  name='domain'
                  icon='at'
                  iconPosition='left'
                  placeholder='Domain'
                  value={domain.name}
                  onChange={handleDomainInputChange}
                />
                  {loading && <p>Loading options...</p>}
                  {error && <p style={{ color: 'red' }}>{error}</p>}

                  {domainOptions.length > 0 && (
                    <ul style={{ border: '1px solid #ccc', maxHeight: '200px', overflowY: 'auto' }}>
                      {domainOptions.map((option) => (
                        <li key={option.id} onClick={() => handleDomainOptionSelect(option)} style={{ padding: '8px', cursor: 'pointer' }}>
                          {option.name} {/* Or the property you want to display */}
                        </li>
                      ))}
                    </ul>
                  )}
            </div>
            <Button color='blue' fluid size='large'>Signup</Button>
          </Segment>
        </Form>
        <Message>{`Already have an account? `}
          <NavLink to="/login" color='teal'>Login</NavLink>
        </Message>
        {isError && <Message negative>{errorMessage}</Message>}
      </Grid.Column>
    </Grid>
  )
}

export default Signup