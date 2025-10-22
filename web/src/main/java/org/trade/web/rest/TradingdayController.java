package org.trade.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.trade.core.persistent.TradeService;
import org.trade.core.persistent.tradingday.Tradingday;
import org.trade.core.persistent.tradingday.TradingdayRecord;
import org.trade.core.persistent.tradingday.TradingdayService;
import org.trade.core.util.JSONMapper;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.trade.web.config.SwaggerConfig.BASIC_AUTH_SECURITY_SCHEME;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@RestController
@RequestMapping("/api/tradingday")
public class TradingdayController {

    private final TradeService tradeService;

    public TradingdayController(final TradeService tradeService) {

        this.tradeService = tradeService;
    }

    @Operation(security = {@SecurityRequirement(name = BASIC_AUTH_SECURITY_SCHEME)})
    @GetMapping
    public List<TradingdayRecord> getTradingdays(@RequestParam(value = "text", required = false) ZonedDateTime open, @RequestParam(value = "text", required = false) ZonedDateTime close) {
        List<Tradingday> tradingdays = new ArrayList<>();

        if (open != null && close != null) {
            tradingdays = tradeService.getTradingdayService().findTradingdaysByDateRangeOrderByOpenAsc(open, close).getTradingdays();
        } else {
            tradingdays = tradeService.getTradingdayService().findAll();
        }

        return tradingdays.stream().map(TradingdayRecord::from).collect(Collectors.toList());
    }

    @Operation(security = {@SecurityRequirement(name = BASIC_AUTH_SECURITY_SCHEME)})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public TradingdayRecord createTradingday(@Valid @RequestBody TradingdayRecord tradingdayRecord) {

        Tradingday tradingday = null;

        if (null == tradingday) {

            tradingday = JSONMapper.convertRecordToEntity(tradingdayRecord, Tradingday.class);
        }

        tradingday = tradeService.saveTradingday(tradingday);
        return TradingdayRecord.from(tradingday);
    }

    @Operation(security = {@SecurityRequirement(name = BASIC_AUTH_SECURITY_SCHEME)})
    @DeleteMapping("/{id}")
    public TradingdayRecord deleteEmployee(@PathVariable Long id) {

        Tradingday tradingday = tradeService.getTradingdayService().validateAndGet(id);
        tradeService.getTradingdayService().delete(tradingday);
        return TradingdayRecord.from(tradingday);
    }
}
