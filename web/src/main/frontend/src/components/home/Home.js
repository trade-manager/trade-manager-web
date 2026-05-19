import React, {useEffect, useState} from 'react'
import {Container, Dimmer, Grid, Icon, Image, Loader, Segment, Statistic} from 'semantic-ui-react'
import {employeeApi} from '../misc/EmployeeApi'
import {tradingdayApi} from '../misc/TradingdayApi'
import {tradestrategyApi} from '../misc/TradestrategyApi'
import {ERROR, logMessage} from '../misc/LoggerApi'
import {useAuth} from "../context/AuthContext";

function Home() {
    const Auth = useAuth()
    const user = Auth.getUser()

    const [numberOfUsers, setNumberOfUsers] = useState(0)
    const [numberOfEmployees, setNumberOfEmployees] = useState(0)
    const [numberOfTradingdays, setNumberOfTradingdays] = useState(0)
    const [numberOfTradestrategies, setNumberOfTradestrategies] = useState(0)
    const [isLoading, setIsLoading] = useState(false)

    useEffect(() => {

        const fetchData = async () => {

            setIsLoading(true)

            try {

                const responseUsers = await employeeApi.numberOfUsers()
                setNumberOfUsers(responseUsers.data)
                // logMessage(ERROR, "Info: Home::fetchData ResponseUsers: " + JSON.stringify(responseUsers.data), user)

                const responseEmployees = await employeeApi.numberOfEmployees()
                setNumberOfEmployees(responseEmployees.data)

                const responseTradingdays = await tradingdayApi.numberOfTradingdays()
                setNumberOfTradingdays(responseTradingdays.data)

                const responseTradestrategies = await tradestrategyApi.numberOfTradestrategies()
                setNumberOfTradestrategies(responseTradestrategies.data)

            } catch (error) {
                logMessage(ERROR, error, user)
            } finally {
                setIsLoading(false)
            }
        }

        fetchData()
    }, [])

    if (isLoading) {
        return (
            <Segment basic style={{marginTop: window.innerHeight / 2}}>
                <Dimmer active inverted>
                    <Loader inverted size='huge'>Loading</Loader>
                </Dimmer>
            </Segment>
        )
    }

    return (
        <Container text>
            <Grid stackable columns={2}>
                <Grid.Row>
                    <Grid.Column textAlign='center'>
                        <Segment color='blue'>
                            <Statistic>
                                <Statistic.Value><Icon name='user' color='grey'/>{numberOfUsers}</Statistic.Value>
                                <Statistic.Label>Users</Statistic.Label>
                            </Statistic>
                        </Segment>
                    </Grid.Column>
                    <Grid.Column textAlign='center'>
                        <Segment color='blue'>
                            <Statistic>
                                <Statistic.Value><Icon name='employee' color='grey'/>{numberOfEmployees}
                                </Statistic.Value>
                                <Statistic.Label>Employees</Statistic.Label>
                            </Statistic>
                        </Segment>
                    </Grid.Column>
                    <Grid.Column textAlign='center'>
                        <Segment color='blue'>
                            <Statistic>
                                <Statistic.Value><Icon name='employee' color='grey'/>{numberOfTradingdays}
                                </Statistic.Value>
                                <Statistic.Label>Tradingdays</Statistic.Label>
                            </Statistic>
                        </Segment>
                    </Grid.Column>
                    <Grid.Column textAlign='center'>
                        <Segment color='blue'>
                            <Statistic>
                                <Statistic.Value><Icon name='employee' color='grey'/>{numberOfTradestrategies}
                                </Statistic.Value>
                                <Statistic.Label>Tradestrategies</Statistic.Label>
                            </Statistic>
                        </Segment>
                    </Grid.Column>
                </Grid.Row>
            </Grid>

            <Image src='https://react.semantic-ui.com/images/wireframe/media-paragraph.png' style={{marginTop: '2em'}}/>
            <Image src='https://react.semantic-ui.com/images/wireframe/paragraph.png' style={{marginTop: '2em'}}/>
        </Container>
    )
}

export default Home