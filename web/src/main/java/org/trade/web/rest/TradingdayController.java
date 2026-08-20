package org.trade.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
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
import org.trade.core.util.JSONMapper;
import org.trade.core.util.time.TradingCalendar;

import java.time.ZonedDateTime;
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
    public List<TradingdayRecord> getTradingdays(@RequestParam(name = "open", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) ZonedDateTime open, @RequestParam(value = "close", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) ZonedDateTime close) {
        List<Tradingday> tradingdays;

        if (open != null && close != null) {

            tradingdays = tradeService.getTradingdayService().findTradingdaysByDateRangeOrderByOpenAsc(TradingCalendar.getTradingDayStart(open), TradingCalendar.getTradingDayEnd(close)).getTradingdays();
        } else {

            tradingdays = tradeService.getTradingdayService().findAll();
        }

        return tradingdays.stream().map(TradingdayRecord::from).collect(Collectors.toList());
    }

    @Operation(security = {@SecurityRequirement(name = BASIC_AUTH_SECURITY_SCHEME)})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public TradingdayRecord createTradingday(@Valid @RequestBody TradingdayRecord tradingdayRecord) {

        if (null != tradingdayRecord) {

            Tradingday tradingday = JSONMapper.convertRecordToEntity(tradingdayRecord, Tradingday.class);
            tradingday = tradeService.saveTradingday(tradingday);
            return TradingdayRecord.from(tradingday);
        }

        return null;
    }

    @Operation(security = {@SecurityRequirement(name = BASIC_AUTH_SECURITY_SCHEME)})
    @DeleteMapping("/{id}")
    public TradingdayRecord deleteTradingday(@PathVariable Long id) {

        Tradingday tradingday = tradeService.getTradingdayService().validateAndGet(id);
        tradeService.getTradingdayService().delete(tradingday);
        return TradingdayRecord.from(tradingday);
    }
}
