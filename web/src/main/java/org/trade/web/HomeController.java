package org.trade.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Controller
public class HomeController {

    @RequestMapping(value = "/")
    public String index() {

        return "index";
    }

}
