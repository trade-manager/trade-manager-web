package org.trade.security;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Greg Turnquist
 */
// tag::code[]
@Controller
public class HomeController {

    @RequestMapping(value = "/")
    public String index() {
        return "index";
    }

}
// end::code[]