/* ===========================================================
 * TradeManager : a application to trade strategies for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2011-2011, by Simon Allen and Contributors.
 *
 * Project Info:  org.trade
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * [Java is a trademark or registered trademark of Oracle, Inc.
 * in the United States and other countries.]
 *
 * (C) Copyright 2011-2011, by Simon Allen and Contributors.
 *
 * Original Author:  Simon Allen;
 * Contributor(s):   -;
 *
 * Changes
 * -------
 *
 */
package org.trade.core.broker.client;

import org.trade.core.broker.BrokerModelException;
import org.trade.core.persistent.TradeService;
import org.trade.core.persistent.dao.Contract;
import org.trade.core.persistent.dao.Tradestrategy;

import java.time.ZonedDateTime;
import java.util.concurrent.ConcurrentHashMap;

public class ClientSocket {

    private final TradeService tradeService;
    private final IClientWrapper client;
    private static final ConcurrentHashMap<Integer, Broker> backTestBroker = new ConcurrentHashMap<>();

    public ClientSocket(IClientWrapper client, TradeService tradeService) {

        this.tradeService = tradeService;
        this.client = client;
    }

    /**
     * Method reqHistoricalData.
     *
     * @param reqId             int
     * @param tradestrategy     Tradestrategy
     * @param startDateTime     ZonedDateTime
     * @param endDateTime       ZonedDateTime
     * @param chartDays         String
     * @param barSizeSetting    String
     * @param whatToShow        String
     * @param useRTH            int
     * @param formatDateInteger int
     */
    public void reqHistoricalData(int reqId, Tradestrategy tradestrategy, ZonedDateTime startDateTime, ZonedDateTime endDateTime, String chartDays,
                                  String barSizeSetting, String whatToShow, int useRTH, int formatDateInteger) throws BrokerModelException {

        try {

            if (null != endDateTime) {

                PolygonBroker broker = new PolygonBroker(reqId, tradestrategy.getContract(), startDateTime, endDateTime, chartDays,
                        barSizeSetting, client);
                backTestBroker.put(reqId, broker);
                broker.execute();
            } else {

                if (tradestrategy.getTrade()) {

                    DBBroker backTestBroker = new DBBroker(this.tradeService, tradestrategy.getStrategyData(),
                            tradestrategy.getId(), client);
                    ClientSocket.backTestBroker.put(reqId, backTestBroker);
                    backTestBroker.execute();
                }
            }
        } catch (Exception ex) {

            throw new BrokerModelException(0, 6000, "Error initializing BackTestBroker Msg: " + ex.getMessage());
        }
    }

    /**
     * Method removeBackTestBroker.
     *
     * @param reqId Integer
     */

    public void removeBackTestBroker(Integer reqId) {

        synchronized (backTestBroker) {

            Broker worker = backTestBroker.get(reqId);

            if (null != worker) {

                if (worker.isDone() || worker.isCancelled()) {

                    backTestBroker.remove(reqId);
                }
            }
        }
    }

    /**
     * Method getBackTestBroker.
     *
     * @param reqId Integer
     * @return BackTestBroker
     */
    public Broker getBackTestBroker(Integer reqId) {

        return backTestBroker.get(reqId);
    }

    /**
     * Method getBackTestBroker.
     *
     * @param reqId      int
     * @param contract   Contract
     * @param barSize    int
     * @param whatToShow String
     * @param useRTH     boolean
     */
    public void reqRealTimeBars(int reqId, Contract contract, int barSize, String whatToShow, boolean useRTH) {

    }
}
