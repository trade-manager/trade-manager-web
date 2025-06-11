package org.trade.core.persistent.dao;

import org.springframework.data.jpa.repository.JpaRepository;


public interface TradelogReportRepository extends JpaRepository<TradelogReport, Integer>, TradelogReportRepositoryCustom {

}
