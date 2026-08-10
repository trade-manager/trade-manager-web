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
import org.trade.core.persistent.tradestrategy.Tradestrategy;
import org.trade.core.persistent.tradestrategy.TradestrategyRecord;
import org.trade.core.persistent.tradingday.Tradingday;
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
@RequestMapping("/api/tradestrategy")
public class TradestrategyController {

    private final TradeService tradeService;

    public TradestrategyController(final TradeService tradeService) {

        this.tradeService = tradeService;
    }

    @Operation(security = {@SecurityRequirement(name = BASIC_AUTH_SECURITY_SCHEME)})
    @GetMapping
    public List<TradestrategyRecord> getTradestrategies(@RequestParam(value = "text", required = false) ZonedDateTime open, @RequestParam(value = "text", required = false) ZonedDateTime close) {
        List<Tradestrategy> tradestrategies;

        Tradingday tradingday = tradeService.getTradingdayService().findByOpenCloseDate(open, close);

        if (tradingday != null) {
            tradestrategies = tradeService.getTradestrategyService().findByTradingday(tradingday);
        } else {
            tradestrategies = tradeService.getTradestrategyService().findAll();
        }

        List<TradestrategyRecord> tradestrategyRecord = tradestrategies.stream().map(TradestrategyRecord::from).collect(Collectors.toList());

        return tradestrategyRecord;
    }

    @Operation(security = {@SecurityRequirement(name = BASIC_AUTH_SECURITY_SCHEME)})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public TradestrategyRecord createTradestrategy(@Valid @RequestBody TradestrategyRecord tradestrategyRecord) {

        Tradestrategy tradestrategy = null;

        if (null == tradestrategy) {

            tradestrategy = JSONMapper.convertRecordToEntity(tradestrategyRecord, Tradestrategy.class);
        }

        tradestrategy = tradeService.saveTradestrategy(tradestrategy);
        return TradestrategyRecord.from(tradestrategy);
    }

    @Operation(security = {@SecurityRequirement(name = BASIC_AUTH_SECURITY_SCHEME)})
    @DeleteMapping("/{id}")
    public TradestrategyRecord deleteTradestrategy(@PathVariable Long id) {

        Tradestrategy tradestrategy = tradeService.getTradestrategyService().validateAndGet(id);
        tradeService.getTradestrategyService().delete(tradestrategy);
        return TradestrategyRecord.from(tradestrategy);
    }
}
