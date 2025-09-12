const prod = {
  url: {
    API_BASE_URL: 'https://trade-manager.com'
  }
}

const dev = {
  url: {
    API_BASE_URL: 'http://localhost:8080'
  }
}

export const config = process.env.REACT_APP_PROD === 'development' ? dev : prod