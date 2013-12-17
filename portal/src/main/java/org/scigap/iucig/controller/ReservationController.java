package org.scigap.iucig.controller;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.log4j.Logger;
import org.scigap.iucig.service.MWSService;
import org.scigap.iucig.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value="/reservation/")
public class ReservationController {
	
	@Value("${quarry.mws.url}")
	private String quarryMwsUrl;

    @Value("${br2.mws.url}")
    private String br2MwsUrl;

    @Value("${mason.mws.url}")
    private String masonMwsUrl;
	
	@Autowired
	MWSService service;
	
	@Autowired
	UserService userService;
	
	private final Logger logger = Logger.getLogger(getClass());
	
	/** Returns all reservations on the machine*/
	@ResponseBody
	@RequestMapping(value="/quarry", method = RequestMethod.GET)
	public String getAllReservations(HttpServletResponse response) {
		HttpRequestBase mwsRequest = new HttpGet(quarryMwsUrl +"reservations");
		logger.debug("Executing REST GET request" + mwsRequest.getRequestLine());
		return service.getResponseForRequest(mwsRequest, response);
	}

    /** Returns all reservations on the machine*/
    @ResponseBody
    @RequestMapping(value="/bigred2", method = RequestMethod.GET)
    public String getAllBR2Reservations(HttpServletResponse response) {
        HttpRequestBase mwsRequest = new HttpGet(br2MwsUrl +"reservations");
        logger.debug("Executing REST GET request" + mwsRequest.getRequestLine());
        return service.getResponseForRequest(mwsRequest, response);
    }

    /** Returns all reservations on the machine*/
    @ResponseBody
    @RequestMapping(value="/mason", method = RequestMethod.GET)
    public String getAllMasonReservations(HttpServletResponse response) {
        HttpRequestBase mwsRequest = new HttpGet(masonMwsUrl +"reservations");
        logger.debug("Executing REST GET request" + mwsRequest.getRequestLine());
        return service.getResponseForRequest(mwsRequest, response);
    }
}
