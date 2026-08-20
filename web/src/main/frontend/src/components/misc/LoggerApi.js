import axios from 'axios'
import {config} from '../../Constants'

let user;

let constants;

// -- Axios
const instance = axios.create({
    baseURL: config.url.API_BASE_URL
})

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

// -- Helper functions
function basicAuth(user) {
    return `Basic ${user.authdata}`
}

export const getUIConfig = async () => {

    if (null == constants) {

        const response = await instance.get('/public/init-values')
        const data = response.data;
        constants = data instanceof Array ? data : [data];
        console.log("getUIConfig data:\n" + JSON.stringify(constants));
    }
}

