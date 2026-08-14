import axios from 'axios'
import {config} from '../../Constants'

export const tradingdayApi = {
    numberOfTradingdays,
    getTradingdays,
    deleteTradingday,
    addTradingday
}

function numberOfTradingdays() {
    return instance.get('/public/numberOfTradingdays')
}

function deleteTradingday(user, id) {
    return instance.delete(`/api/tradingday/${id}`, {
        headers: {'Authorization': basicAuth(user)}
    })
}

function getTradingdays(user, open, close) {

    const url = open ? `/api/tradingday?open=${(new Date(open)).toISOString()}&close=${(new Date(close)).toISOString()}` : '/api/tradingday'
    console.log("tradingdayApi::getTradingdays url: " + url);
    return instance.get(url, {
        headers: {'Authorization': basicAuth(user)}
    })
}

function addTradingday(user, tradingday) {
    return instance.post('/api/tradingday', tradingday, {
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