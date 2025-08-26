import React, { Component } from 'react';
import './App.css';
import Home from './Home';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import EmployeeList from './EmployeeList';
import EmployeeEdit from "./EmployeeEdit";

class App extends Component {
  render() {
    return (
        <Router>
          <Routes>
            <Route path='/' exact={true} component={Home}/>
            <Route path='/employees' exact={true} component={EmployeeList}/>
            <Route path='/employees/:id' component={EmployeeEdit}/>
          </Routes>
        </Router>
    )
  }
}

export default App;