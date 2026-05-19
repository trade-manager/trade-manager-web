package org.trade.web.rest;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.trade.core.persistent.TradeService;
import org.trade.core.persistent.domain.DomainRecord;
import org.trade.core.persistent.domain.DomainService;
import org.trade.core.persistent.employee.EmployeeService;
import org.trade.core.persistent.log.LogRecord;
import org.trade.core.persistent.role.RoleRecord;
import org.trade.core.persistent.role.RoleService;
import org.trade.core.persistent.user.UserService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@RestController
@RequestMapping("/public")
public class PublicController {

    private static final Logger _log = LoggerFactory.getLogger(PublicController.class);

    private final DomainService domainService;
    private final RoleService roleService;
    private final UserService userService;
    private final EmployeeService employeeService;
    private final TradeService tradeService;

    public PublicController(final DomainService domainService, final RoleService roleService, final UserService userService, final EmployeeService employeeService, final TradeService tradeService) {

        this.domainService = domainService;
        this.roleService = roleService;
        this.userService = userService;
        this.employeeService = employeeService;
        this.tradeService = tradeService;
    }

    @GetMapping("/numberOfUsers")
    public Integer getNumberOfUsers() {

        return userService.findAll().size();
    }

    @GetMapping("/numberOfEmployees")
    public Integer getNumberOfEmployees() {

        return employeeService.findAll().size();
    }

    @GetMapping("/numberOfTradestrategies")
    public Integer getNumberOfTradestrategies() {

        return this.tradeService.getTradestrategyService().findAll().size();
    }

    @GetMapping("/numberOfTradingdays")
    public Integer getNumberOfTradingdays() {

        return this.tradeService.getTradingdayService().findAll().size();
    }

    @GetMapping("/domains")
    public List<DomainRecord> getDomains() {

        return domainService.finaAll().stream().map(DomainRecord::from).collect(Collectors.toList());
    }

    @GetMapping("/roles")
    public List<RoleRecord> getRoles() {

        return roleService.findAll().stream().map(RoleRecord::from).collect(Collectors.toList());
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/log")
    public LogRecord log(@Valid @RequestBody LogRecord logRecord) {

        if (logRecord.getLevel().equals("ERROR")) {

            _log.error(logRecord.getMessage());
        } else if (logRecord.getLevel().equals("WARNING")) {

            _log.warn(logRecord.getMessage());
        } else if (logRecord.getLevel().equals("INFO")) {

            _log.info(logRecord.getMessage());
        } else {

            _log.debug(logRecord.getMessage());
        }
        return logRecord;
    }
}

