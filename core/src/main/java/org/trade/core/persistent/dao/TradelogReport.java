package org.trade.core.persistent.dao;

import org.trade.core.dao.Aspect;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */

public class TradelogReport extends Aspect implements Serializable {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 3388042483785305102L;

    private List<TradelogDetail> tradelogDetail = new ArrayList<>(0);
    private List<TradelogSummary> tradelogSummary = new ArrayList<>(0);

    public TradelogReport() {
    }

    /**
     * Constructor for TradelogReport.
     *
     * @param tradelogDetailId Long
     * @param tradelogDetail   List<TradelogDetail>
     */
    public TradelogReport(Long tradelogDetailId, List<TradelogDetail> tradelogDetail) {
        super.setId(tradelogDetailId);
        this.tradelogDetail = tradelogDetail;

    }

    /**
     * Method getIdTradingdays.
     *
     * @return Long
     */
    public Long getTradingdaysId() {
        return this.getId();
    }

    /**
     * Method setIdTradingdays.
     *
     * @param tradelogDetailId Long
     */
    public void setTradingdaysId(Long tradelogDetailId) {
        super.setId(tradelogDetailId);
    }

    /**
     * Method add.
     *
     * @param tradelogSummary TradelogSummary
     */
    public void add(TradelogSummary tradelogSummary) {
        this.tradelogSummary.add(tradelogSummary);
    }

    /**
     * Method remove.
     *
     * @param tradelogSummary TradelogSummary
     */
    public void remove(TradelogSummary tradelogSummary) {
        this.tradelogSummary.remove(tradelogSummary);
    }

    /**
     * Method getTradelogSummary.
     *
     * @return List<TradelogSummary>
     */
    public List<TradelogSummary> getTradelogSummary() {
        return this.tradelogSummary;
    }

    /**
     * Method setTradelogSummary.
     *
     * @param tradelogSummary List<TradelogSummary>
     */
    public void setTradelogSummary(List<TradelogSummary> tradelogSummary) {
        this.tradelogSummary = tradelogSummary;
    }

    /**
     * Method add.
     *
     * @param tradelogDetail TradelogDetail
     */
    public void add(TradelogDetail tradelogDetail) {
        this.tradelogDetail.add(tradelogDetail);
    }

    /**
     * Method remove.
     *
     * @param tradelogDetail TradelogDetail
     */
    public void remove(TradelogDetail tradelogDetail) {
        this.tradelogDetail.remove(tradelogDetail);
    }

    /**
     * Method getTradelogDetail.
     *
     * @return List<TradelogDetail>
     */
    public List<TradelogDetail> getTradelogDetail() {
        return this.tradelogDetail;
    }

    /**
     * Method setTradelogDetail.
     *
     * @param tradelogDetail List<TradelogDetail>
     */
    public void setTradelogDetail(List<TradelogDetail> tradelogDetail) {
        this.tradelogDetail = tradelogDetail;
    }

    public void clear() {
        tradelogDetail.clear();
        tradelogSummary.clear();
    }
}
