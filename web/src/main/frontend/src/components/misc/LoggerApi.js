import axios from 'axios'
import {config} from '../../Constants'

let user;

export const ERROR = "ERROR"
export const WARNING = "WARNING"
export const INFO = "INFO"
export const DEBUG = "DEBUG"

export const logMessage = (level, messsage, user) => {

    if (messsage.response) {

        log(level, messsage.response.data, user)
    } else if (messsage.request) {

        log(level, messsage.request, user)
    } else if (messsage.message) {

        log(level, messsage.message, user)
    } else {

        log(level, messsage, user)
    }
}

function log(level, message, user) {

    const logRecord = {
        "timestamp": new Date().toISOString(),
        "level": level,
        "thread": "main",
        "logger": "components.misc.LoggerAPI",
        "message": message,
        "context": {
            "user": user
        }
    };

    return instance.post('/public/log', logRecord, {
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