import axios from 'axios'
import {config} from '../../Constants'

export const tradestrategyApi = {
    numberOfTradestrategies,
    getTradestrategies,
    deleteTradestrategy,
    addTradestrategy
}

function numberOfTradestrategies() {
    return instance.get('/public/numberOfTradestrategies')
}

function deleteTradestrategy(user, id) {
    return instance.delete(`/api/tradestrategy/${id}`, {
        headers: {'Authorization': basicAuth(user)}
    })
}

function getTradestrategies(user, open, close) {
    const url = open ? `/api/tradestrategyopen=${open}&close=${close}` : '/api/tradestrategy'
    return instance.get(url, {
        headers: {'Authorization': basicAuth(user)}
    })
}

function addTradestrategy(user, tradestrategy) {
    return instance.post('/api/tradestrategy', tradestrategy, {
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