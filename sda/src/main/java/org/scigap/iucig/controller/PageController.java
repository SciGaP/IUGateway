package org.scigap.iucig.controller;

import org.apache.log4j.Logger;
import org.scigap.iucig.util.ViewNames;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
public class PageController {
	
	private final Logger logger = Logger.getLogger(getClass());
	
	@RequestMapping(value = "/sda", method = RequestMethod.GET)
	public String home(HttpServletRequest request) {
		logger.debug("Loading SDA page");
		return ViewNames.SDA_PAGE;
	}

    @RequestMapping(value = "/error", method = RequestMethod.GET)
    public String errorPage(HttpServletRequest request) {
        logger.debug("Loading Error page");
        return ViewNames.ERROR_PAGE;
    }

}
